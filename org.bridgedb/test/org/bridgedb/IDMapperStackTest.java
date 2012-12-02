package org.bridgedb;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

public class IDMapperStackTest extends TestCase 
{
	public static Map<String, IDMapper> mappers;
	public static final String FILENAMES [] = { "AB", "BC", "CD", "DE", "XY", "XZ", "YW", "YZ" };
	private static IDMapperStack stack;
	
	private static DataSource dsW, dsX, dsY, dsZ, dsA, dsE, dsB, dsC, dsD;
	
	protected void setUp() throws ClassNotFoundException, IDMapperException
	{
		Class.forName("org.bridgedb.file.IDMapperText");
			
		mappers = new HashMap<String,IDMapper>();
		stack = new IDMapperStack();
		stack.setTransitive(true);
		
		for (String fileName : FILENAMES) 
		{   // Load all IDMappers for test data files
			String fullName = "/org/bridgedb/" + fileName + ".csv";
			URL url = IDMapperStackTest.class.getResource(fullName);
			assertNotNull("Could not find resource in classpath: " + fullName, url);
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
	
	public void testSimpleMapID() throws IDMapperException{
		
		Xref src = new Xref ("x1", dsX );
		Set<Xref> results = stack.getIDMapperAt(0).mapID( src, dsY );
		System.out.println("single IDMapper");
		System.out.println("src Xref: " + src);
		System.out.println("src.dataSource: " + src.getDataSource());
		System.out.println("results.size(): " + results.size());
		for( Xref x : results ) {
			System.out.println(x);
		}
	}
	
	public void testMapID_A_to_E () throws IDMapperException 
	{		
		Xref src = new Xref ("a1", dsA );
		Set<Xref> results = stack.mapID( src, dsE );		
		System.out.println("results.size(): " + results.size());
		for( Xref x : results ) {
			System.out.println(x);
		}
		assertEquals (1, results.size());
		assertTrue (results.contains (new Xref("e1", dsE )));
	}
	
	public void testMapID_X_W_via_Y () throws IDMapperException 
	{
		Xref src = new Xref ("x2", dsX );
		Set<Xref> results = stack.mapID( src, dsW );		
		assertEquals (1, results.size());
		assertTrue (results.contains (new Xref("w2", dsW )));
	}
	
	/** do an untargetted mapping */
	public void testMapID_all () throws IDMapperException
	{
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
