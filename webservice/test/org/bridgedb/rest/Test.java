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

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;

public class Test extends TestCase
{
	private IDMapper mapper;
	
	@Override
	protected void setUp() throws Exception {
		if(mapper == null) {
			// Start local service
			Server server = new Server();
			server.run(8183, null);

			// Create a client
			Class.forName("org.bridgedb.webservice.bridgerest.BridgeRest");
			mapper = BridgeDb.connect("idmapper-bridgerest:http://localhost:8183/Human");
		}
	}

	public IDMapper getLocalService() {
		return mapper;
	}
	
	public void testLocalMapID() throws IDMapperException, ClassNotFoundException {
		IDMapper mapper = getLocalService();
		
		Xref insr = new Xref ("3643", BioDataSource.ENTREZ_GENE);
		Xref affy = new Xref ("33162_at", BioDataSource.AFFY);
		Set<Xref> result = mapper.mapID(insr);
		assertTrue (result.contains(affy));
		
		assertTrue(mapper.xrefExists(insr));
	}
	
	public void testLocalCapabilities() throws IDMapperException, ClassNotFoundException {
		IDMapper mapper = getLocalService();
		
		IDMapperCapabilities cap = mapper.getCapabilities();
		
		Set<DataSource> supported = cap.getSupportedSrcDataSources();
		assertTrue (supported.contains(DataSource.getBySystemCode("L")));

		String val = cap.getProperty("SCHEMAVERSION");
		assertNotNull(val);
		
		Set<DataSource> srcDs = cap.getSupportedSrcDataSources();
		assertTrue(srcDs.size() > 0);
		
		assertTrue(cap.isFreeSearchSupported());
		
		assertTrue(cap.isMappingSupported(BioDataSource.UNIPROT, BioDataSource.ENTREZ_GENE));
		
		assertFalse(cap.isMappingSupported(
				DataSource.getBySystemCode("??"), DataSource.getBySystemCode("!!")));
	}
	
	public void testLocalSearch() throws IDMapperException, ClassNotFoundException {
		IDMapper mapper = getLocalService();
		
		Set<Xref> result = mapper.freeSearch("1234", 100);
		System.out.println(result);
		assertTrue(result.size() > 0);
	}
	
	public void testLocalAttributes() throws ClassNotFoundException, IDMapperException {
		AttributeMapper mapper = (AttributeMapper)getLocalService();
		
		Xref insr = new Xref("3643", BioDataSource.ENTREZ_GENE);
		Map<String, Set<String>> attrMap = mapper.getAttributes(insr);
		assertNotNull(attrMap.get("Symbol"));
		assertTrue(attrMap.get("Symbol").size() == 2);
		
		Set<String> attrValues = mapper.getAttributes(insr, "Symbol");
		assertTrue(attrValues.size() == 2);
		
		Map<Xref, String> xrefMap = mapper.freeAttributeSearch("INSR", "Symbol", 1);
		assertTrue(xrefMap.size() == 1);

		xrefMap = mapper.freeAttributeSearch("INSR", "Symbol", 100);
		assertTrue(xrefMap.containsKey(insr));
		assertTrue(xrefMap.size() > 1);
		
		Set<String> attrs = mapper.getAttributeSet();
		assertTrue(attrs.size() > 0);
	}
}
