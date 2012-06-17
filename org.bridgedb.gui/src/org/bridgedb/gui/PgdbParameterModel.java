package org.bridgedb.gui;

import java.io.File;

import javax.swing.JFileChooser;


public class PgdbParameterModel extends SimpleParameterModel implements BridgeDbParameterModel
{
	/**
	 * 
	 * Fields:
	 * 
	 * 0: File
	 */
	
	public PgdbParameterModel()
	{
		super (new Object[][] {
				new Object[] {
						"BridgeDerby database file", 
						new File(System.getProperty("user.home")),
						new FileParameter("BridgeDerby database", "*.bridge|*.pgdb", false, JFileChooser.FILES_ONLY)
				},
		});
	}

	public String toString() { return getName(); }

	@Override
	public String getConnectionString()
	{
		return "idmapper-pgdb:" + getFile(0).getAbsolutePath();
	}

	@Override
	public String getName()
	{
		return "BridgeDerby database";
	}

	@Override
	public String getHelpHtml()
	{
		return 
			"<html><h1>BridgeDerby database" +
			"<p>BridgeDerby are databases that consist of a single file which you can download " +
			"to your computer for fast access. Once downloaded, BridgeDerby databases are much" +
			"faster than a webservice." +
			"<p>BridgeDb databases can be downloaded from " +
			"<a href=\"http://bridgedb.org/data/gene_database/\">http://bridgedb.org/data/gene_database/</a>. " +
			"Download them anywhere on your machine, and then select that file below.";
	}

	@Override
	public Category getCategory()
	{
		return Category.DATABASE;
	}

	private boolean enabled = false;
	
	@Override
	public void loadClass() throws ClassNotFoundException
	{
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		enabled = true;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

}
