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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;

import static org.junit.jupiter.api.Assertions.*;


public class IDMapperStackTest implements org.bridgedb.Driver
{
	public static Map<String, IDMapper> mappers;
	public static final String FILENAMES [] = { "AB", "BC", "CD", "DE", "XY", "XZ", "YW", "YZ" };
	private static IDMapperStack stack;
	private static IDMapperStack stack2;
	private static IDMapperStack stack3;
	private static IDMapperStack stack4;
	private static IDMapperStack stack5;
	private static DataSource dsW, dsX, dsY, dsZ, dsA, dsE, dsB, dsC, dsD, dsOrphanet;

	@org.junit.jupiter.api.Test
	protected void setUp() throws ClassNotFoundException, IDMapperException, MalformedURLException
	{
		Class.forName("org.bridgedb.file.IDMapperText");
			
		mappers = new HashMap<String,IDMapper>();
		stack = new IDMapperStack();
		stack2 = new IDMapperStack();
		stack3 = new IDMapperStack();
		stack4 = new IDMapperStack();
		stack5 = new IDMapperStack();
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
			stack3.addIDMapper(m);
			stack5.addIDMapper(m);
			stack.addIDMapper("idmapper-text:" + url);
			stack3.addIDMapper("idmapper-text:" + url);
			stack5.addIDMapper("idmapper-text:" + url);
		}
		
		String fileName = "yeast_id_mapping";
		String fullNameYeast = fileName + ".txt";
		ClassLoader classLoader = this.getClass().getClassLoader();
		URL urlYeast = classLoader.getResource(fullNameYeast);
		IDMapper yeastMap = BridgeDb.connect("idmapper-text:" + urlYeast);
		stack.addIDMapper(yeastMap);
		
		// Test exception when adding an invalid IDMapper to the stack
		Assertions.assertThrows(IDMapperException.class, () -> {
					stack.addIDMapper("test");});
		
		// Test getTransitive method
		assertTrue(stack.getTransitive());
		
		// Test removeIDMapper method, set up datasource AB.csv as idmapper-text
		String fullName = "AB.csv";
		classLoader = this.getClass().getClassLoader();
		URL url = classLoader.getResource(fullName);
		IDMapper m = BridgeDb.connect("idmapper-text:" + url);
		stack4.addIDMapper(m);
		stack4.removeIDMapper(m);
		assertEquals(0, stack4.getSize());
		// Test adding a null IDMapper to the stack
		m = null;
		stack4.addIDMapper(m);
		assertEquals(0, stack4.getSize());
		
		dsW = DataSource.register("dsW", "W").asDataSource();
		dsX = DataSource.register("dsX", "X").asDataSource();
		dsY = DataSource.register("dsY", "Y").asDataSource();
		dsZ = DataSource.register("dsZ", "Z").asDataSource();
		dsA = DataSource.register("dsA", "A").asDataSource();
		dsB = DataSource.register("dsB", "B").asDataSource();
		dsC = DataSource.register("dsC", "C").asDataSource();
		dsD = DataSource.register("dsD", "D").asDataSource();
		dsE = DataSource.register("dsE", "E").asDataSource();
		dsOrphanet = DataSource.register("On", "Orphanet")
				.mainUrl("http://www.orpha.net/consor/")
				.identifiersOrgBase("http://identifiers.org/orphanet:$id")
				.asDataSource();
	}

	@Test
	public void testMapIDXrefDataSourceArray() throws IDMapperException {
				
		Xref src = new Xref ("e1", dsE );
		Xref src2 = new Xref ("e2", dsE );
		Set<Xref> results = stack.mapID(src);
		stack5.mapID(src);
		System.out.println("src Xref: " + src);
		assertEquals (5, results.size());
		assertTrue (results.contains (new Xref("a1", dsA)));
		assertTrue (results.contains (new Xref("b1", dsB)));
		assertTrue (results.contains (new Xref("c1", dsC)));
		assertTrue (results.contains (new Xref("d1", dsD)));
		assertTrue(stack.isConnected());
		assertTrue(stack5.isConnected());
		assertFalse(stack2.isConnected());
		assertTrue(stack.xrefExists(src));
		assertFalse(stack2.xrefExists(src2));
		// Test .close() method, ensure connection is closed.
		stack2.close();
		stack5.close();
		assertFalse(stack5.isConnected() && stack2.isConnected());
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
			System.out.println(results);
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
		Xref srcOrphanet = new Xref("85163",dsOrphanet);
		Set<Xref> results = stack.mapID( src );
		stack.mapID(srcOrphanet);
		stack2.setTransitive(false);
		stack3.setTransitive(false);
		Set<Xref> results2 = stack2.mapID( src );
		Set<Xref> results3 = stack3.mapID( srcOrphanet );
		assertNotNull(results2);
		assertNotNull(results3);

		System.out.println ("RESULTS");
		for (Xref ref : results)
			System.out.println (ref);
		assertEquals (4, results.size());
		assertTrue (results.contains (new Xref("y2", dsY )));
		assertTrue (results.contains (new Xref("z2", dsZ )));
		assertTrue (results.contains (new Xref("w2", dsW )));
		
		// Test .getCapabilities()
		assertNotNull(stack.getCapabilities());

		Map<Xref, Set<Xref>> resultsTransitive  = stack.mapID(results);
		Map<Xref, Set<Xref>> resultsNonTransitive  = stack2.mapID(results2);
		Map<Xref, Set<Xref>> resultsNonTransitive2  = stack3.mapID(results3);
		
		for (Map.Entry<Xref, Set<Xref>> entry : resultsTransitive.entrySet()) {
			System.out.println("mapping");
		    System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		
		assertNotNull(resultsTransitive);
		assertNotNull(resultsNonTransitive);
		assertNotNull(resultsNonTransitive2);
		
		// Attribute results return empty because for child in stack, (child instanceof AttributeSet) is false
		Set<String> getAttributesResults = new HashSet<String>();
		getAttributesResults = stack.getAttributes(srcOrphanet, "Description");
		assertTrue(getAttributesResults.isEmpty() && stack.getAttributeSet().isEmpty());
		assertTrue(stack3.getAttributes(srcOrphanet).isEmpty() && stack3.getAttributesForAllMappings(srcOrphanet, dsOrphanet).isEmpty());
		assertFalse(stack.isFreeAttributeSearchSupported());
	}

	@Override
	public IDMapper connect(String locationString) throws IDMapperException {
			Reader reader;
			try
			{
				reader = getReader (new URL(locationString));
			}
			catch (MalformedURLException e)
			{
				throw new IDMapperException(e);
			}
			return (IDMapper) new IDMapperStackTest();
		}
    private static Reader getReader(URL url) throws IDMapperException {
        try {
            InputStream inputStream = InternalUtils.getInputStream(url);
            return new InputStreamReader(inputStream);
        } catch(IOException e) {
            throw new IDMapperException(e);
        }
    }
	private static final class Driver implements org.bridgedb.Driver
	{
		@Override
		public IDMapper connect(String locationString) throws IDMapperException
		{
			Reader reader;
			try
			{
				reader = getReader (new URL(locationString));
			}
			catch (MalformedURLException e)
			{
				throw new IDMapperException(e);
			}
			return (IDMapper) new IDMapperStackTest();
		}
	}
	
}
