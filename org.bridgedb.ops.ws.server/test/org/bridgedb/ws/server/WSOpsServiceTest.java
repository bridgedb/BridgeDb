package org.bridgedb.ws.server;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.bridgedb.IDMapperException;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.url.URLMapper;
import org.bridgedb.ws.WSOpsService;
import org.bridgedb.ws.bean.URLMappingBean;
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
		List<URLMappingBean> mapURL = service.mapURL("http://www.foo.com/123", null, null);
		assertEquals(3, mapURL.size());
	}
	
	@Test(expected=IDMapperException.class)
	public void testMapUrlInvalidProfile() throws IDMapperException {
		service.mapURL("http://www.foo.com/123", "1", null);
	}
	
}