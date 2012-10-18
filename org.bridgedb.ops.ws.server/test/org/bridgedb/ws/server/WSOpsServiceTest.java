package org.bridgedb.ws.server;

import org.bridgedb.IDMapperException;
import org.bridgedb.ws.WSOpsService;
import org.junit.Test;

public class WSOpsServiceTest {
	
	@Test(expected=IDMapperException.class)
	public void testMapUrlNoArguments() throws IDMapperException {
		WSOpsService service = new WSOpsService();
		service.mapURL(null, null, null);
	}
	
	@Test(expected=IDMapperException.class)
	public void testMapUrlEmptyArguments() throws IDMapperException {
		WSOpsService service = new WSOpsService();
		service.mapURL("", "", null);
	}
	
	@Test
	public void testMapUrlEmptyProfile() throws IDMapperException {
		WSOpsService service = new WSOpsService();
		service.mapURL("http://www.foo.com/123", null, null);
	}
	
}