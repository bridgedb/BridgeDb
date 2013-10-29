package org.bridgedb;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IDMapperStackTest {

	public static Map<String, IDMapper> mappers;
	public static final String FILENAMES [] = { "AB", "BC", "CD", "DE", "XY", "XZ", "YW", "YZ" };
	private static IDMapperStack stack;
	
	private static DataSource dsW, dsX, dsY, dsZ, dsA, dsE, dsB, dsC, dsD;
	
	@Before
	public void setUp() throws ClassNotFoundException, IDMapperException, MalformedURLException
	{
		Class.forName("org.bridgedb.file.IDMapperText");
			
		mappers = new HashMap<String,IDMapper>();
		stack = new IDMapperStack();
		stack.setTransitive(true);
		
		for (String fileName : FILENAMES) 
		{   // Load all IDMappers for test data files
            File file = new File("test-data/" + fileName + ".csv");
            URL url = file.toURI().toURL();
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
		Assert.assertEquals (4, results.size());
		Assert.assertTrue (results.contains (new Xref("a1", dsA)));
		Assert.assertTrue (results.contains (new Xref("b1", dsB)));
		Assert.assertTrue (results.contains (new Xref("c1", dsC)));
		Assert.assertTrue (results.contains (new Xref("d1", dsD)));
		Assert.assertTrue(true);
	}
	
	@Test
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
	
	@Test
	public void testMapID_A_to_E () throws IDMapperException 
	{		
		Xref src = new Xref ("a1", dsA );
		Set<Xref> results = stack.mapID( src, dsE );		
		System.out.println("results.size(): " + results.size());
		for( Xref x : results ) {
			System.out.println(x);
		}
		Assert.assertEquals (1, results.size());
		Assert.assertTrue (results.contains (new Xref("e1", dsE )));
	}
	
	@Test
	public void testMapID_X_W_via_Y () throws IDMapperException 
	{
		Xref src = new Xref ("x2", dsX );
		Set<Xref> results = stack.mapID( src, dsW );		
		Assert.assertEquals (1, results.size());
		Assert.assertTrue (results.contains (new Xref("w2", dsW )));
	}
	
	/** do an untargetted mapping */
	@Test
	public void testMapID_all () throws IDMapperException
	{
		Xref src = new Xref ("x2", dsX );
		Set<Xref> results = stack.mapID( src );
		System.out.println ("RESULTS");
		for (Xref ref : results)
			System.out.println (ref);
		Assert.assertEquals (3, results.size());
		Assert.assertTrue (results.contains (new Xref("y2", dsY )));
		Assert.assertTrue (results.contains (new Xref("z2", dsZ )));
		Assert.assertTrue (results.contains (new Xref("w2", dsW )));
	}

}
