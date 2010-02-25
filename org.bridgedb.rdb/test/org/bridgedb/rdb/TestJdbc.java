package org.bridgedb.rdb;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;

import junit.framework.TestCase;

public class TestJdbc extends TestCase
{

	public void testMysql() throws ClassNotFoundException, IDMapperException
	{
		Class.forName ("com.mysql.jdbc.Driver");
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		String connectString = "idmapper-jdbc:mysql://localhost/worm?user=bridgedb";
		
		IDMapper mapper = BridgeDb.connect(connectString);
		
		for (String key : mapper.getCapabilities().getKeys())
		{
			System.out.println (key + "=" + mapper.getCapabilities().getProperty(key)); 
		}
	}
	
}
