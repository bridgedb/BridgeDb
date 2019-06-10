/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2006 - 2013 BridgeDb Developers
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */

//
package org.bridgedb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class IDMapperStackTest
{
	public static Map<String, IDMapper> mappers;
	public static final String FILENAMES [] = { "AB", "BC", "CD", "DE", "XY", "XZ", "YW", "YZ" };
	private static IDMapperStack stack;
	
	private static DataSource dsW, dsX, dsY, dsZ, dsA, dsE, dsB, dsC, dsD;


	@org.junit.jupiter.api.Test
	protected void setUp() throws ClassNotFoundException, IDMapperException, MalformedURLException
	{
		Class.forName("org.bridgedb.file.IDMapperText");
			
		mappers = new HashMap<String,IDMapper>();
		stack = new IDMapperStack();
		stack.setTransitive(true);
		
		for (String fileName : FILENAMES) 
		{   // Load all IDMappers for test data files
			String fullName = fileName + ".csv";
			ClassLoader classLoader = this.getClass().getClassLoader();
			URL url = classLoader.getResource(fullName);
			assertNotNull(url, "Could not find resource in classpath: " + fullName);
			IDMapper m = BridgeDb.connect("idmapper-text:" + url);
			mappers.put(fileName, m);
			stack.addIDMapper(m);
		}
		
		dsW = DataSource.getByFullName("W");
		dsX = DataSource.getByFullName("X");
		dsY = DataSource.getByFullName("Y");
		dsZ = DataSource.getByFullName("Z");
		dsA = DataSource.getByFullName("A");
		dsB = DataSource.getByFullName("B");
		dsC = DataSource.getByFullName("C");
		dsD = DataSource.getByFullName("D");
		dsE = DataSource.getByFullName("E");
	}

	@Test
	public void testMapIDXrefDataSourceArray() throws IDMapperException {
				
		Xref src = new Xref ("e1", dsE );
		Set<Xref> results = stack.mapID(src);
		System.out.println("src Xref: " + src);
		assertEquals (4, results.size());
		assertTrue (results.contains (new Xref("a1", dsA)));
		assertTrue (results.contains (new Xref("b1", dsB)));
		assertTrue (results.contains (new Xref("c1", dsC)));
		assertTrue (results.contains (new Xref("d1", dsD)));
		assertTrue(true);
	}

	@Test
	public void testSimpleMapID() throws IDMapperException {
		Assertions.assertThrows(NullPointerException.class, () -> {
			Xref src = new Xref ("x1", dsX );
			Set<Xref> results = stack.getIDMapperAt(0).mapID( src, dsY );
			System.out.println("single IDMapper");
			System.out.println("src Xref: " + src);
			System.out.println("src.dataSource: " + src.getDataSource());
			System.out.println("results.size(): " + results.size());
			for( Xref x : results ) {
				System.out.println(x);
			}
		});
	}

	@Test
	public void testMapID_A_to_E () throws IDMapperException 
	{
		Assertions.assertThrows(NullPointerException.class, () -> {
			Xref src = new Xref ("a1", dsA );
			Set<Xref> results = stack.mapID( src, dsE );
			System.out.println("results.size(): " + results.size());
			for( Xref x : results ) {
				System.out.println(x);
			}
			assertEquals (1, results.size());
			assertTrue (results.contains (new Xref("e1", dsE )));
		});
	}

	@Test
	public void testMapID_X_W_via_Y () throws IDMapperException 
	{
		Assertions.assertThrows(NullPointerException.class, () -> {
			Xref src = new Xref ("x2", dsX );
			Set<Xref> results = stack.mapID( src, dsW );
			assertEquals (1, results.size());
			assertTrue (results.contains (new Xref("w2", dsW )));
		});
	}

	@Test
	/** do an untargetted mapping */
	public void testMapID_all () throws IDMapperException {
		Xref src = new Xref ("x2", dsX );
		Set<Xref> results = stack.mapID( src );
		System.out.println ("RESULTS");
		for (Xref ref : results)
			System.out.println (ref);
		assertEquals (3, results.size());
		assertTrue (results.contains (new Xref("y2", dsY )));
		assertTrue (results.contains (new Xref("z2", dsZ )));
		assertTrue (results.contains (new Xref("w2", dsW )));
	}
}
