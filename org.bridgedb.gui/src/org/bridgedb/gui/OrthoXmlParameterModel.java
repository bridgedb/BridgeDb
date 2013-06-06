package org.bridgedb.gui;

import java.io.File;

import javax.swing.JFileChooser;


public class OrthoXmlParameterModel extends SimpleParameterModel implements BridgeDbParameterModel
{
	public OrthoXmlParameterModel()
	{
		super (new Object[][] {
			new Object[]
			{
				"OrthoXML file",
				new File(System.getProperty("user.home")),
				new FileParameter ("OrthoXml files", "*.orthoxml", false, JFileChooser.FILES_ONLY)
			} 
		});
	}

	@Override
	public String getConnectionString()
	{
		return "idmapper-orthoxml:" + getFile(0).toURI();
	}

	@Override
	public String getName()
	{
		return "Local OrthoXml file";
	}

	public String toString() { return getName(); }

	@Override
	public String getHelpHtml()
	{
		return 
		"<html><h1>Local OrthoXml file" +
		"<p>Read mappings from an OrthoXml file." +
		"<p>OrthoXml is a standard format for cross-species mappings. " +
		"See http://www.orthoxml.org for a complete description and a " +
		"list of providers. Mapping files can be generated, or a " +
		"number of common mappings can be downloaded from various websites ";
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
		Class.forName("org.bridgedb.file.orthoxml.IDMapperOrthoXml");
		enabled = true;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

}
