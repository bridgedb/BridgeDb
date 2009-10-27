package org.bridgedb.bio;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;

import junit.framework.TestCase;

public class TestStack extends TestCase
{
	private static final String GDB_HUMAN = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Hs_Derby_20081119.pgdb";
	private static final File YEAST_ID_MAPPING = new File ("test-data/yeast_id_mapping.txt");
	private static final File NUGO_CUSTOM_MAPPINGS = new File ("test-data/Nugo-hs-custom.txt");

	private Set<Xref> src = new HashSet<Xref>();
	private static final Xref RAD51 = new Xref ("YER095W", BioDataSource.ENSEMBL_SCEREVISIAE);
	private static final Xref INSR = new Xref ("Hs.705877", BioDataSource.UNIGENE);

	private static final Xref NUGO = new Xref ("NuGO_eht0320285_at", BioDataSource.AFFY);
	private static final Xref ENSEMBL = new Xref ("ENSG00000026652", BioDataSource.ENSEMBL);
	private static final Xref ENTREZ = new Xref ("56895", BioDataSource.ENTREZ_GENE);

	public void setUp() throws ClassNotFoundException
	{
		BioDataSource.init();
		Class.forName ("org.bridgedb.file.IDMapperText");
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
	}
	
	public void testNeededFiles()
	{
		assertTrue (YEAST_ID_MAPPING.exists());
		assertTrue (new File(GDB_HUMAN).exists());
	}
	
	public void testFile() throws IDMapperException, MalformedURLException
	{
		IDMapper mapper = BridgeDb.connect ("idmapper-text:" + YEAST_ID_MAPPING.toURL());
		src.add (RAD51);
		Map<Xref, Set<Xref>> refmap = mapper.mapID(src, BioDataSource.ENTREZ_GENE);
		Set<Xref> expected = new HashSet<Xref>();
		expected.add (new Xref ("856831", BioDataSource.ENTREZ_GENE));
		assertEquals (expected, refmap.get(RAD51));
		
		System.out.println (mapper.getCapabilities().getSupportedTgtDataSources());
	}
	
	public void testPgdb() throws IDMapperException
	{
		IDMapper mapper = BridgeDb.connect("idmapper-pgdb:" + GDB_HUMAN);
		src.add (INSR);
		Map<Xref, Set<Xref>> refmap = mapper.mapID(src, BioDataSource.ENTREZ_GENE);
		Set<Xref> expected = new HashSet<Xref>();
		expected.add (new Xref ("3643", BioDataSource.ENTREZ_GENE));
		assertEquals (expected, refmap.get(INSR));
	}

	public void testStack() throws IDMapperException, MalformedURLException
	{
		IDMapperStack stack = new IDMapperStack();
		stack.addIDMapper("idmapper-pgdb:" + GDB_HUMAN);
		stack.addIDMapper("idmapper-text:" + YEAST_ID_MAPPING.toURL());
		src.add (INSR);
		src.add (RAD51);
		Map<Xref, Set<Xref>> refmap = stack.mapID(src, BioDataSource.ENTREZ_GENE);
		Set<Xref> expected = new HashSet<Xref>();
		expected.add (new Xref ("3643", BioDataSource.ENTREZ_GENE));
		assertEquals (expected, refmap.get(INSR));
		expected.clear();
		expected.add (new Xref ("856831", BioDataSource.ENTREZ_GENE));
		assertEquals (expected, refmap.get(RAD51));
	}

	public void testTransitive() throws IDMapperException, ClassNotFoundException, MalformedURLException
	{		 
		IDMapper textMapper = BridgeDb.connect ("idmapper-text:" + NUGO_CUSTOM_MAPPINGS.toURL());
		IDMapper derbyMapper = BridgeDb.connect ("idmapper-pgdb:" + GDB_HUMAN);
		IDMapperStack stack = new IDMapperStack();
		stack.addIDMapper(derbyMapper);
		stack.addIDMapper(textMapper);
	
		stack.setTransitive(false);
		
		// test the link between NUGO and ENSEMBL that only occurs in text		
		Set<Xref> result = stack.mapID(NUGO);
		assertTrue(result.contains(ENSEMBL));
		assertFalse(result.contains(ENTREZ));
				
		// test the link between ENTREZ and ENSEMBL that only occurs in pgdb
		result = stack.mapID(ENTREZ);		
		assertFalse(result.contains(NUGO));
		assertTrue(result.contains(ENSEMBL));
		
		stack.setTransitive(true);

		// test transitive
		result = stack.mapID(NUGO);
		assertTrue(result.contains(ENTREZ));
		assertTrue(result.contains(ENSEMBL));
		
		// and the other way around
		//TODO: disabled, getting OutOfMemory
		//result = stack.mapID(ENTREZ);
		//assertTrue(result.contains(NUGO));
		//assertTrue(result.contains(ENSEMBL));

		// map multiple IDs
		//TODO: disabled, getting OutOfMemory
		//Set<Xref> set1 = new HashSet<Xref>();
		//set1.add (ENTREZ);
		//Map<Xref, Set<Xref>> result2 = stack.mapID(set1);
		//assertTrue (result2.get(ENTREZ).contains(NUGO));
	}
}
