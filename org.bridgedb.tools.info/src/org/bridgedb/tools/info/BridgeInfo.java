package org.bridgedb.tools.info;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;

public class BridgeInfo
{
	private final File database;
	private SimpleGdb databaseGdb;
	private PrintStream out;

	public BridgeInfo(File f1) throws IDMapperException
	{
		this(f1, System.out);
	}

	public BridgeInfo(File f1, OutputStream out) throws IDMapperException
	{
		this.database = f1;
		if (out == null)
			throw new NullPointerException(
				"OutputStream is null"
			);
		this.out = new PrintStream(out);
	}

	Map<DataSource, Integer> oldSet = new HashMap<DataSource, Integer>();
	Map<DataSource, Integer> newSet = new HashMap<DataSource, Integer>();

	public void initDatabases() throws IDMapperException
	{
		String url1 = "jdbc:derby:jar:(" + database + ")database";
		databaseGdb = SimpleGdbFactory.createInstance("old", url1);
	}
	
	public void listInfo()
	{
		IDMapperCapabilities capabilities = databaseGdb.getCapabilities();
		for (String prop : capabilities.getKeys()) {
			this.out.println(prop + ": " + capabilities.getProperty(prop));
		}
	}
	
	public void run() throws IDMapperException, SQLException
	{
		initDatabases();
		listInfo();
	}
	
	public static void printUsage()
	{
		System.out.println ("Expected a argument: <database>");
	}

	public static void main(String[] args) throws IDMapperException, SQLException
	{
		if (args.length != 1) { printUsage(); return; }
		BridgeInfo main = new BridgeInfo(new File(args[0]));
		DataSourceTxt.init();
		main.run();
	}

}
