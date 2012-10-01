package org.bridgedb;

import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;
import junit.framework.TestCase;

public class IDMapperStackTest extends TestCase 
{
	public static HashMap<String, IDMapper> Mappers;
	public static String FILENAMES [] = { "AB", "BC", "CD", "DE", "XY", "XZ", "YW", "YZ" };
	private static IDMapperStack stack;
	
	private static DataSource dsW, dsX, dsY, dsZ, dsA, dsE;
	
	protected void setUp() throws Exception 
	{
		super.setUp();
			
		try {
			Class.forName("org.bridgedb.file.IDMapperText");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		Mappers = new HashMap<String,IDMapper>();
		stack = new IDMapperStack();
		stack.setTransitive(true);
		
		for (String fileName : FILENAMES) {                  // Load all IDMappers for test data files
			URL url = IDMapperStackTest.class.getResource("/org/bridgedb/" + fileName + ".csv");
			try {
				IDMapper m = BridgeDb.connect("idmapper-text:" + url);
				Mappers.put(fileName, m);
				stack.addIDMapper(m);
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		dsW = DataSource.getByFullName("W");
		dsX = DataSource.getByFullName("X");
		dsY = DataSource.getByFullName("Y");
		dsZ = DataSource.getByFullName("Z");
		dsA = DataSource.getByFullName("A");
		dsE = DataSource.getByFullName("E");
	}
	
	public void testRebuildDataStack() throws IDMapperException 
	{
		stack.rebuildDataSourcesMap();
		assertTrue( true );
	}
	
	
	public void testMapIDXrefDataSourceArray() throws IDMapperException {
				
		Xref src = new Xref ("e1", dsE );
		Set<Xref> results = stack.mapID(src);
		System.out.println("src Xref: " + src);
		System.out.println("results.size(): " + results.size());
		for( Xref x : results ) {
			System.out.println(x);
		}
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
		Xref src = new Xref ("a1", dsX );
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
	
	public void testMapID_all () throws IDMapperException
	{
		Xref src = new Xref ("x2", dsX );
		Set<Xref> results = stack.mapID( src );		
		assertEquals (2, results.size());
		assertTrue (results.contains (new Xref("y2", dsY )));
		assertTrue (results.contains (new Xref("w2", dsW )));
	}

}
