//BridgeDb,
//An abstraction layer for identifer mapping services, both local and online.
//Copyright 2006-2009 BridgeDb developers
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//
package org.bridgedb.rest;

import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

import junit.framework.TestCase;

public class Test extends TestCase
{
	public void testLocalService() throws ClassNotFoundException, IDMapperException
	{
		// Start local service
		Server server = new Server();
		server.run(8183, null);
	
		// Create a client
		Class.forName("org.bridgedb.webservice.bridgerest.BridgeRest");
		IDMapper mapper = BridgeDb.connect("idmapper-bridgerest:http://localhost:8183/Human");
		
		Xref insr = new Xref ("3643", DataSource.getBySystemCode("L"));
		Xref affy = new Xref ("33162_at", DataSource.getBySystemCode("X"));
		Set<Xref> result = mapper.mapID(insr, null);
		assertTrue (result.contains(affy));
		
		result = mapper.freeSearch("1234", 100);
		System.out.println (result);
	}
	
}
