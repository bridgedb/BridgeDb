package org.bridgedb.tools.qc;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;

/**
 * Utility to do simple quality control on a BridgeDerby database.
 * Run with two parameters: [old database] and [new database]
 * Some basic comparisons will be done, which serves as a sanity check
 * that not suddenly a whole identifier system has gone missing.
 * <p>
 * The script produces a report on stdout, lines starting with "INFO"
 * are strictly informative, whereas lines starting with "WARNING" are
 * problems worth investigating further. Ideally there are no "WARNING" lines
 * in the report. 
 */
public class BridgeQC
{
	private final File oldDb;
	private final File newDb;
	private SimpleGdb oldGdb;
	private SimpleGdb newGdb;

	public BridgeQC(File f1, File f2) throws IDMapperException
	{
		oldDb = f1;
		newDb = f2;
	}

	Map<DataSource, Integer> oldSet = new HashMap<DataSource, Integer>();
	Map<DataSource, Integer> newSet = new HashMap<DataSource, Integer>();

	public void initDatabases() throws IDMapperException
	{
		String url1 = "jdbc:derby:jar:(" + oldDb + ")database";
		oldGdb = SimpleGdbFactory.createInstance("old", url1);
		String url2 = "jdbc:derby:jar:(" + newDb + ")database";
		newGdb = SimpleGdbFactory.createInstance("new", url2);
	}
	
	public void compareDataSources() throws IDMapperException
	{
		for (DataSource ds : oldGdb.getCapabilities().getSupportedSrcDataSources())
		{
			int oldGenes = oldGdb.getGeneCount(ds);
			oldSet.put (ds, oldGenes);
		}
		
		for (DataSource ds : newGdb.getCapabilities().getSupportedSrcDataSources())
		{
			int newGenes = newGdb.getGeneCount(ds);
			newSet.put (ds, newGenes);
		}

		// not in new
		for (DataSource ds : oldSet.keySet())
		{
			if (!newSet.containsKey(ds))
			{
				System.out.println ("WARNING: " + ds.getSystemCode() + " is only in old database");
			}
		}

		// not in old
		for (DataSource ds : newSet.keySet())
		{
			int newGenes = newSet.get(ds);
			if (newGenes == 0)
			{
				System.out.println ("WARNING: " + ds.getSystemCode() + " has 0 ids");
			}
			
			if (!oldSet.containsKey(ds))
			{
				System.out.println ("INFO: " + ds.getSystemCode() + " is only in new database"); 
				System.out.printf ("INFO: Number of ids in %s: %d\n", ds.getSystemCode(), newGenes); 
			}
			else
			{
				Set<String> oldIDs = new HashSet<String>();
				for (Xref oldXref : oldGdb.getIterator(ds)) oldIDs.add(oldXref.getId());
				Set<String> newIDs = new HashSet<String>();
				for (Xref newXref : newGdb.getIterator(ds)) newIDs.add(newXref.getId());

				// determine all new IDs
				Set<String> newGenesAdded = new HashSet<String>();
				newGenesAdded.addAll(newIDs);
				newGenesAdded.removeAll(oldIDs);
				
				// determine all no longer existing (removed) IDs
				Set<String> genesRemoved = new HashSet<String>();
				genesRemoved.addAll(oldIDs);
				genesRemoved.removeAll(newIDs);
				
				int oldGenes = oldSet.get(ds);
				double delta = (double)(newGenes - oldGenes) / (double)oldGenes;
				System.out.printf(
					"INFO: Number of ids in %s%s: %d (%d added, %d removed -> overall changed %+3.1f%%)\n",
					ds.getSystemCode(),
					(ds.getFullName() != null && ds.getFullName().length() > 0) ?
						" (" + ds.getFullName() + ")" : "",
					newGenes,
					newGenesAdded.size(),
					genesRemoved.size(),
					(delta * 100)
				);
				if (genesRemoved.size() > 0 && "true".equals(System.getProperty("showRemovedIDs", "false")))
					System.out.printf(
						"INFO: The ids removed from %s%s: %s\n",
							ds.getSystemCode(),
							(ds.getFullName() != null && ds.getFullName().length() > 0) ?
								" (" + ds.getFullName() + ")" : "",
							"" + genesRemoved
						);

				if (delta < -0.1) 
					System.out.println ("WARNING: Number of ids in " + ds.getSystemCode() + " has shrunk by more than 10%");
			}
		}
	}
	
	public void compareLinks() throws SQLException
	{
		Connection con = oldGdb.getConnection();
		//TODO ... do something to compare cross-link consistency ...
	}
	
	public void checkDatabaseSanity() throws SQLException
	{
		Connection con = newGdb.getConnection();
		Statement st = con.createStatement();
		/** check for ids that occur in the link table but not in datanode table. We expect zero results */
		String sql = "select coderight, idright from link left outer join datanode on link.idright = datanode.id and link.coderight = datanode.code where datanode.code IS NULL";
		ResultSet rs = st.executeQuery(sql);
		
		if (rs.next())
		{
			System.out.println ("ERROR: 'link' table contains ids that do not occur in 'datanode' table.");
			System.out.print ("ERROR: A few examples: ");
			String sep = "";
			int i = 0;
			do 
			{
				System.out.print (sep + rs.getString(1) + ":" + rs.getString(2));
				sep = ", ";
			}
			while (rs.next() && ++i < 8);
			System.out.println();
			System.out.println ("ERROR: These ids will not map properly.");
		}
		
	}

	public void compareFileSizes() throws SQLException
	{
		long oldSize = oldDb.length();
		long newSize = newDb.length();
		System.out.printf ("INFO: new size is %d Mb (changed %+3.1f%%)\n", newSize / 1000000, 
				(double)(newSize - oldSize) / (double)oldSize * 100);
	}

	public void compareAttributes() throws IDMapperException
	{
		Set<String> oldAttrSet = oldGdb.getAttributeSet();
		Set<String> newAttrSet = newGdb.getAttributeSet();
		
		for (String oldAttr : oldAttrSet)
		{
			if (!newAttrSet.contains(oldAttr))
			{
				System.out.println ("WARNING: Attribute " + oldAttr + " only in old database");
			}
		}

		for (String newAttr : newAttrSet)
		{
			System.out.println ("INFO: Attribute provided: " + newAttr);
			if (!oldAttrSet.contains(newAttr))
			{
				System.out.println ("INFO: Attribute " + newAttr + " only in new database");
			}
		}
	}

	public static boolean safeEquals (Object a, Object b)
	{
		return a == null ? b == null : a.equals(b);
	}

	public interface PropertyChecker
	{
		abstract void check(String oldVal, String newVal);
	}
	
	enum Props implements PropertyChecker
	{
		ORGANISM (true, false) {
			public void check(String oldVal, String newVal) 
			{
				if (newVal != null)
				{
					Organism o = Organism.fromLatinName(newVal);
					if (o == null) System.out.println ("WARNING: species '" + newVal + "' is not a recognized latin name");
				}
			}
		},
		DATASOURCENAME (true, true) {
			public void check(String oldVal, String newVal) {}
		},
		SERIES (true, true) {
			public void check(String oldVal, String newVal) {}
		},
		DATATYPE (true, true) {
			public void check(String oldVal, String newVal) {}
		},
		DATASOURCEVERSION (false, true) {
			public void check(String oldVal, String newVal) {}
		},
		BUILDDATE (false, true) {
			public void check(String oldVal, String newVal) {
				SimpleDateFormat sft = new SimpleDateFormat("yyyyMMdd");
				Date oldDate = null;
				Date newDate = null;
				try
				{
					if (oldVal != null)
						oldDate = sft.parse(oldVal);
				}
				catch (ParseException e)
				{
					System.out.println ("ERROR: " + oldVal + " does not match pattern yyyymmdd"); 
				}
				try
				{
					if (newVal != null)
						newDate = sft.parse(newVal);
				}
				catch (ParseException e)
				{
					System.out.println ("ERROR: " + oldVal + " does not match pattern yyyymmdd"); 
				}
				if (oldDate != null && newDate != null && oldDate.after(newDate))
				{
					System.out.println ("ERROR: new date " + newVal + " is older than old date " + oldVal); 
				}
			}
		},
		SCHEMAVERSION (false, true) {
			public void check(String oldVal, String newVal) {}
		},
		;
		
		private boolean mustBeSame;
		private boolean mustBeDefined;
		
		Props(boolean mustBeSame, boolean mustBeDefined)
		{
			this.mustBeSame = mustBeSame;
			this.mustBeDefined = mustBeDefined;
		}
		
		public void checkWrap(String oldVal, String newVal)
		{
			if (mustBeSame && !safeEquals (oldVal, newVal))
				System.out.println ("WARNING: old " + name() + " '" + oldVal + "' doesn\'t match new " + name() + " '" + newVal + "'");
			if (mustBeDefined && (newVal == null || newVal.equals("")))
				System.out.println ("WARNING: property " + name() + " is undefined");
			check(oldVal, newVal);
		}
	}
	
	public void compareInfo()
	{
		for (Props p : Props.values())
		{
			p.checkWrap(oldGdb.getCapabilities().getProperty(p.name()),
					newGdb.getCapabilities().getProperty(p.name()));
		}
	}
	
	public void run() throws IDMapperException, SQLException
	{
		initDatabases();
		checkDatabaseSanity();
		compareInfo();
		compareDataSources();

		compareLinks();
		
		compareAttributes();
		compareFileSizes();		
	}
	
	public static void printUsage()
	{
		System.out.println ("Expected 2 arguments: <old database> <new database>");
	}
	
	/**
	 * @param args
	 * @throws IDMapperException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IDMapperException, SQLException
	{
		if (args.length != 2) { printUsage(); return; }
		BridgeQC main = new BridgeQC (new File(args[0]), new File(args[1]));
		DataSourceTxt.init();
		main.run();
		
		PatternChecker checker = new PatternChecker();
		checker.run(new File(args[0]));
	}

}
