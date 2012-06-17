package org.bridgedb.gui;

import java.io.File;

import javax.swing.JFileChooser;


public class FileParameterModel extends SimpleParameterModel implements BridgeDbParameterModel
{
	public FileParameterModel()
	{
		super (new Object[][] {
			new Object[]
			{
				"Tab delimited text file",
				new File(System.getProperty("user.home")),
				new FileParameter ("Tab-delimited text files", "*.txt|*.tsv", false, JFileChooser.FILES_ONLY)
			} 
		});
	}

	@Override
	public String getConnectionString()
	{
		return "idmapper-text:file://" + getFile(0).getAbsolutePath();
	}

	@Override
	public String getName()
	{
		return "Local text file";
	}

	public String toString() { return getName(); }

	@Override
	public String getHelpHtml()
	{
		return 
		"<html><h1>Local text file" +
		"<p>Read mappings from a text file." +
		"<p>The file should be formatted as a tab-delimited text file, with " +
		"two or more columns, and a header row. Each column should contain " +
		"identifiers from exactly one source. The system will work best if the " +
		"header row uses the correct data source name." +
		"<p>All identifiers on the same row are assumed to be related " +
		"to each other. So a row with three columns will establish a relation " +
		"between the identifiers in column 1 and 2, column 1 and 3, and column 2 and 3.";
	}

	@Override
	public Category getCategory()
	{
		return Category.CUSTOM;
	}

	private boolean enabled = false;
	
	@Override
	public void loadClass() throws ClassNotFoundException
	{
		Class.forName("org.bridgedb.file.IDMapperText");
		enabled = true;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

}
