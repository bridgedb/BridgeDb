package org.bridgedb.gui;

public class ConnectionStringParameterModel extends SimpleParameterModel implements BridgeDbParameterModel
{
	public ConnectionStringParameterModel()
	{
		super (new Object[][] 
		     {new Object[] { "Connection String", "" }}
		);
	}
	
	@Override
	public String getConnectionString()
	{
		return getString(0);
	}

	@Override
	public String getName()
	{
		return "Connection string";
	}

	public String toString() { return getName(); }

	@Override
	public String getHelpHtml()
	{
		return 
		"<html><h1>Connection string" +
		"<p>Enter a raw connection string. For advanced users. Use this method only " +
		"if you're familiar with the internals of the BridgeDb system.";
	}

	@Override
	public Category getCategory()
	{
		return Category.CUSTOM;
	}

	@Override
	public void loadClass() throws ClassNotFoundException
	{
		ClassNotFoundException saved = null;

		// all of these are optional.
		for (String className : new String[] {
				"org.bridgedb.webservice.picr.IDMapperPicr",
				"org.bridgedb.webservice.picr.IDMapperPicrRest",
				"org.bridgedb.webservice.cronos.IDMapperCronos",
				"org.bridgedb.webservice.synergizer.IDMapperSynergizer",
				"org.bridgedb.webservice.biomart.IDMapperBiomart"})
		{
			try
			{
				Class.forName(className);
			}
			catch (ClassNotFoundException ex)
			{
				saved = ex;
			}
		}
		// rethrow the last exception we got.
		if (saved != null)
			throw saved;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

}
