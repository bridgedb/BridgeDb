package org.bridgedb.gui;


public class JdbcParameterModel extends SimpleParameterModel implements BridgeDbParameterModel
{
	/**
	 * Fields:
	 * 
	 * 0 jdbc driver
	 * 1 database
	 * 2 host
	 * 3 port
	 * 4 username
	 * 5 password
	 * 
	 */

	public JdbcParameterModel()
	{
		super (new Object[][] {
				new Object[] { "Driver class", "mysql" },
				new Object[] { "Database name", "" },
				new Object[] { "Host", "localhost"},
				new Object[] { "Username", "" },
				new Object[] { "Password", "" }
		});
	}

	@Override
	public String getConnectionString()
	{
		String driver = getString(0);
		String database = getString(1);
		String host = getString(2);
		String port = getString(3);
		String username = getString(4);
		String password = getString(5);
		
		return ("idmapper-jdbc:" + driver + ":" + username + ":" + password + "@" + 
				host + ":" + port + "/" + database); 
	}

	@Override
	public String getName()
	{
		return "Relational database";
	}

	public String toString() { return getName(); }

	@Override
	public String getHelpHtml()
	{
		return 
			"<html><h1>Relational database" +
			"<p>Connect to a relational database, such as mysql. The relational database must " +
			"have been set up using a BridgeDb-compatible database schema." +
			"<p>Relational databases are much faster than webservices. Use this method" +
			"if you have to map thousands of identifiers, and you're willing to invest " +
			"the time to set up a database." +
			"<p>You can leave the password field empty if the database is not password protected.";
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
		// optional...
		Class.forName("com.mysql.jdbc.Driver");		
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

}
