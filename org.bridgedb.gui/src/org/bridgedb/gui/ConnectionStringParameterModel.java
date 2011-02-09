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

}
