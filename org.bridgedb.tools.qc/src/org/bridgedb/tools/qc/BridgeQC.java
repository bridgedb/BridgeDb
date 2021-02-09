package org.bridgedb.tools.qc;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
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
 * The script produces a report on STDOUT (configurable), lines starting with "INFO"
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
	private PrintStream out;

	/**
	 * Compares two Derby databases and reports the output
	 * to STDOUT.
	 *
	 * @param f1 the original Derby@prefix gpml:  <http://vocabularies.wikipathways.org/gpml#> .
 database
	 * @param f2 the new Derby database
	 */
	public BridgeQC(File f1, File f2) throws IDMapperException
	{
		this(f1, f2, System.out);
	}

	/**
	 * Compares two Derby databases and reports the output
	 * to the given {@link java.io.OutputStream}.
	 *
	 * @param f1 the original Derby database
	 * @param f2 the new Derby database
	 */
	public BridgeQC(File f1, File f2, OutputStream out) throws IDMapperException
	{
		if (out == null)
			throw new NullPointerException(
				"OutputStream is null"
			);
		oldDb = f1;
		newDb = f2;
		this.out = new PrintStream(out);
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
				this.out.println ("WARNING: " + ds.getSystemCode() + " is only in old database");
			}
		}

		// not in old
		for (DataSource ds : newSet.keySet())
		{
			int newGenes = newSet.get(ds);
			if (newGenes == 0)
			{
				this.out.println ("WARNING: " + ds.getSystemCode() + " has 0 ids");
			}
			
			if (!oldSet.containsKey(ds))
			{
				this.out.println ("INFO: " + ds.getSystemCode() + " is only in new database");
				this.out.printf ("INFO: Number of ids in %s: %d\n", ds.getSystemCode(), newGenes);
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
				if (newGenesAdded.size() + genesRemoved.size() == 0)
					this.out.printf(
						"INFO: Number of ids in %s%s: %d (unchanged)\n",
						ds.getSystemCode(),
						(ds.getFullName() != null && ds.getFullName().length() > 0) ?
							" (" + ds.getFullName() + ")" : "",
						newGenes
					);
				else
					this.out.printf(
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
					this.out.printf(
						"INFO: The ids removed from %s%s: %s\n",
							ds.getSystemCode(),
							(ds.getFullName() != null && ds.getFullName().length() > 0) ?
								" (" + ds.getFullName() + ")" : "",
							"" + genesRemoved
						);

				if (delta < -0.1) 
					this.out.println ("WARNING: Number of ids in " + ds.getSystemCode() + " has shrunk by more than 10%");
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
			this.out.println ("ERROR: 'link' table contains ids that do not occur in 'datanode' table.");
			this.out.print ("ERROR: A few examples: ");
			String sep = "";
			int i = 0;
			do 
			{
				this.out.print (sep + rs.getString(1) + ":" + rs.getString(2));
				sep = ", ";
			}
			while (rs.next() && ++i < 8);
			this.out.println();
			this.out.println ("ERROR: These ids will not map properly.");
		}
		
	}

	public void compareFileSizes() throws SQLException
	{
		long oldSize = oldDb.length();
		long newSize = newDb.length();
		this.out.printf ("INFO: new size is %d Mb (changed %+3.1f%%)\n", newSize / 1000000,
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
				this.out.println ("WARNING: Attribute " + oldAttr + " only in old database");
			}
		}

		for (String newAttr : newAttrSet)
		{
			this.out.println ("INFO: Attribute provided: " + newAttr);
			if (!oldAttrSet.contains(newAttr))
			{
				this.out.println ("INFO: Attribute " + newAttr + " only in new database");
			}
		}
	}

	public static boolean safeEquals (Object a, Object b)
	{
		return a == null ? b == null : a.equals(b);
	}

	public interface PropertyChecker
	{
		abstract void check(String oldVal, String newVal, PrintStream out);
	}
	
	enum Props implements PropertyChecker
	{
		ORGANISM (true, false) {
			public void check(String oldVal, String newVal, PrintStream out)
			{
				if (newVal != null)
				{
					Organism o = Organism.fromLatinName(newVal);
					if (o == null) this.out.println ("WARNING: species '" + newVal + "' is not a recognized latin name");
				}
			}
		},
		DATASOURCENAME (true, true) {
			public void check(String oldVal, String newVal, PrintStream out) {}
		},
		SERIES (true, true) {
			public void check(String oldVal, String newVal, PrintStream out) {}
		},
		DATATYPE (true, true) {
			public void check(String oldVal, String newVal, PrintStream out) {}
		},
		DATASOURCEVERSION (false, true) {
			public void check(String oldVal, String newVal, PrintStream out) {}
		},
		BUILDDATE (false, true) {
			public void check(String oldVal, String newVal, PrintStream out) {
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
					this.out.println ("ERROR: " + oldVal + " does not match pattern yyyymmdd");
				}
				try
				{
					if (newVal != null)
						newDate = sft.parse(newVal);
				}
				catch (ParseException e)
				{
					this.out.println ("ERROR: " + oldVal + " does not match pattern yyyymmdd");
				}
				if (oldDate != null && newDate != null && oldDate.after(newDate))
				{
					this.out.println ("ERROR: new date " + newVal + " is older than old date " + oldVal);
				}
			}
		},
		SCHEMAVERSION (false, true) {
			public void check(String oldVal, String newVal, PrintStream out) {}
		},
		;
		
		private boolean mustBeSame;
		private boolean mustBeDefined;
		PrintStream out;
		
		Props(boolean mustBeSame, boolean mustBeDefined)
		{
			this.mustBeSame = mustBeSame;
			this.mustBeDefined = mustBeDefined;
		}
		
		public void checkWrap(String oldVal, String newVal)
		{
			if (mustBeSame && !safeEquals (oldVal, newVal))
				this.out.println ("WARNING: old " + name() + " '" + oldVal + "' doesn\'t match new " + name() + " '" + newVal + "'");
			if (mustBeDefined && (newVal == null || newVal.equals("")))
				this.out.println ("WARNING: property " + name() + " is undefined");
			check(oldVal, newVal, this.out);
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

		summarizeOverallStats();
	}
	
	private void summarizeOverallStats() throws IDMapperException, SQLException
	{
		this.out.println("INFO: total number of identifiers is " + newGdb.getGeneCount());
		this.out.println("INFO: total number of mappings is " + newGdb.getLinkCount());
		Boolean isSchemaUpdated = false;
		int countOfPrimary;
		int countofSecondary;
		for (DataSource ds : newGdb.getCapabilities().getSupportedSrcDataSources()) {
			countOfPrimary = 0;
			countofSecondary = 0;
			Connection con = newGdb.getConnection();
			Statement st = con.createStatement();
			Connection con2 = oldGdb.getConnection();
			Statement st1 = con2.createStatement();
			con.setAutoCommit(false);
			for (Xref xref : newGdb.getIterator(ds)) {
				String sqlSchema = "SELECT schemaversion FROM info ";
				ResultSet schema = st.executeQuery(sqlSchema);
				while (schema.next()) {
					if (schema.getInt("schemaversion") == 4) {
						isSchemaUpdated = true;
					}
					if (isSchemaUpdated) {
						String sql = "SELECT isPrimary FROM datanode WHERE datanode.id = '" + xref.getId() + "'AND datanode.code = '" + ds.getSystemCode() + "'";
						ResultSet rs = st1.executeQuery(sql);
						while (rs.next()) {
							if (rs.getBoolean("isPrimary")) {
								countOfPrimary++;
							} else
								countofSecondary++;
							}
						}
					}
				}
			if (isSchemaUpdated) {
				this.out.println("NEW DB INFO: total number of primary ids in " + ds.getFullName() + " are " + countOfPrimary);
				this.out.println("NEW DB INFO: total number of secondary ids in " + ds.getFullName() + " are " + countofSecondary);
			}
			else
				this.out.println("NEW DB INFO: "+ds.getFullName()+" Schema Version is less than 4 cannot calculate Primary and Secondary ids\'");
		}
		isSchemaUpdated = false;
		for (DataSource ds : oldGdb.getCapabilities().getSupportedSrcDataSources()){
			Connection con = oldGdb.getConnection();
			Statement st = con.createStatement();
			Connection con2 = oldGdb.getConnection();
			Statement st1 = con2.createStatement();
			con.setAutoCommit(false);
			countOfPrimary = 0;
			countofSecondary=0;
			for (Xref xref : oldGdb.getIterator(ds)) {
				String sqlSchema = "SELECT schemaversion FROM info ";
				ResultSet schema = st.executeQuery(sqlSchema);
				while (schema.next()) {
					if (schema.getInt("schemaversion") == 4) {
						isSchemaUpdated = true;
					}
					if (isSchemaUpdated) {
						String sql = "SELECT isPrimary FROM datanode WHERE datanode.id ='" + xref.getId() + "'" + " AND datanode.code = '" + ds.getSystemCode() + "'";
						ResultSet rs = st1.executeQuery(sql);
						while (rs.next()) {
							if (rs.getBoolean("isPrimary")) {
								countOfPrimary++;
							} else
								countofSecondary++;
						}
					}
				}
			}
			if (isSchemaUpdated){
				this.out.println("OLD DB INFO: total number of primary ids in "+ds.getFullName()+" are "+countOfPrimary);
				this.out.println("OLD DB INFO: total number of secondary ids in "+ds.getFullName()+" are "+countofSecondary);
			}
			else
				this.out.println("OLD DB INFO: "+ds.getFullName()+" Schema Version is less than 4 cannot calculate Primary and Secondary ids\'");
		}
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
