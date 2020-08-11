/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2006 - 2009  BridgeDb Developers
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */
package org.bridgedb.rdb;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;


import buildsystem.Measure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Test access to the derby client running on the webservice.
 */
public class Test {
	
	private Measure measure;
	
	@BeforeEach
	public void setUp() {
		measure = new Measure("bridgedb_timing.txt");
	}


	@Disabled
	@org.junit.jupiter.api.Test
	public void testDerbyClient() throws IDMapperException, ClassNotFoundException {
		long start, end, delta;
		start = System.currentTimeMillis();
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		Class.forName ("org.apache.derby.jdbc.ClientDriver");
		
		IDMapper mapper = BridgeDb.connect ("idmapper-derbyclient:Homo sapiens?host=www.wikipathways.org");
		end = System.currentTimeMillis();
		delta = end - start;
		measure.add ("timing::idmapper-derbyclient connect to two databases", "" + delta, "msec");
		
		for (String key : mapper.getCapabilities().getKeys()) {
			System.out.println (key + " -> " + mapper.getCapabilities().getProperty(key));
		}

		System.out.println (mapper.getCapabilities().getSupportedTgtDataSources());
		
		Set <String> symbols = new HashSet<String>();
		AttributeMapper attr = (AttributeMapper)mapper;

		for (String key : attr.getAttributeSet()) {
			System.out.println (key);
		}

		// time the common case of doing a free search and then querying all for symbol
		start = System.currentTimeMillis();
		Map<Xref, String> symbolMap = attr.freeAttributeSearch("p53", "symbol", 100);
		end = System.currentTimeMillis();
		delta = end - start;
		System.out.println (delta);
		measure.add ("timing::idmapper-derbyclient free query for p53", "" + delta, "msec");
		System.out.println (symbols);
		
		// time the case of getting all attributes for backpage info
		start = System.currentTimeMillis();
		Xref insr = new Xref ("ENSG00000171105", DataSource.getExistingBySystemCode("En"));
		for (String x : new String[] {"Description", "Symbol", "Chromosome"})
		//TODO: Synonyms is also available, but not on ENSG.... ids
		{
			Set<String> result = attr.getAttributes(insr , x);
			assertTrue ( result.size() > 0, "No result for " + x);

			System.out.println (result);
		}
		
		end = System.currentTimeMillis();
		delta = end - start;
		measure.add ("timing::idmapper-derbyclient query for backpage attributes", "" + delta, "msec");
		System.out.println (delta);
	}
}
