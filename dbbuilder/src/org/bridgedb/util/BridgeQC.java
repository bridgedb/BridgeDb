package org.bridgedb.util;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;

/**
 * Utility to do simple quality control on a BridgeDerby database.
 * Run with two parameters: [old database] and [new database]
 * Some basic comparisons will be done, which servers as a sanity check
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
				int oldGenes = oldSet.get(ds);
				double delta = (double)(newGenes - oldGenes) / (double)newGenes; 
				System.out.printf ("INFO: Number of ids in %s: %d (changed %+3.1f%%)\n", ds.getSystemCode(), newGenes, (delta * 100)); 
				if (delta < -0.1) 
					System.out.println ("WARNING: Number of ids in " + ds.getSystemCode() + " has shrunk by more than 10%");
			}
		}
	}
	
	public void compareLinks() throws SQLException
	{
		Connection con = oldGdb.getConnection();
		//TODO
	}

	public void compareFileSizes() throws SQLException
	{
		long oldSize = oldDb.length();
		long newSize = newDb.length();
		System.out.printf ("INFO: new size is %d Mb (changed %+3.1f%%)", newSize / 1000000, 
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

	public void run() throws IDMapperException, SQLException
	{
		initDatabases();
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
		main.run();
	}

}
