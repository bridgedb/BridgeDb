package org.bridgedb.ws.server;

import org.bridgedb.IDMapperException;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.url.URLMapper;
import org.bridgedb.ws.WSOpsService;
import org.junit.Before;
import org.junit.Test;

public class WSOpsServiceTest {
	
	private WSOpsService service;

	@Before
	public void serviceSetup() throws IDMapperException {
		SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
		URLMapper urlMapper = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
		service = new WSOpsService(urlMapper);
	}
	
	@Test
	public void testEmptySetup() throws IDMapperException {
		WSOpsService serviceDefault = new WSOpsService();
		serviceDefault.mapURL("http://www.foo.com/123", "", null);
	}
	
	@Test(expected=IDMapperException.class)
	public void testMapUrlNoArguments() throws IDMapperException {
		service.mapURL(null, null, null);
	}
	
	@Test(expected=IDMapperException.class)
	public void testMapUrlEmptyArguments() throws IDMapperException {
		service.mapURL("", "", null);
	}
	
	@Test
	public void testMapUrlEmptyProfile() throws IDMapperException {
		service.mapURL("http://www.foo.com/123", null, null);
	}
	
}