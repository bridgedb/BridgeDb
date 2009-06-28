package org.bridgedb.bio;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;

import junit.framework.TestCase;

public class TestStack extends TestCase
{

	static final String GDB_HUMAN = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Hs_Derby_20081119.pgdb";
	static final File YEAST_ID_MAPPING = new File ("../test-data/yeast_id_mapping.txt");

	Set<Xref> src = new HashSet<Xref>();
	Set<DataSource> dsset = new HashSet<DataSource>();
	static final Xref RAD51 = new Xref ("YER095W", BioDataSource.ENSEMBL_SCEREVISIAE);
	static final Xref INSR = new Xref ("Hs.705877", BioDataSource.UNIGENE);
	
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
		dsset.add (BioDataSource.ENTREZ_GENE);
		Map<Xref, Set<Xref>> refmap = mapper.mapID(src, dsset);
		Set<Xref> expected = new HashSet<Xref>();
		expected.add (new Xref ("856831", BioDataSource.ENTREZ_GENE));
		assertEquals (expected, refmap.get(RAD51));
		
		System.out.println (mapper.getCapabilities().getSupportedTgtDataSources());
	}
	
	public void testPgdb() throws IDMapperException
	{
		IDMapper mapper = BridgeDb.connect("idmapper-pgdb:" + GDB_HUMAN);
		src.add (INSR);
		dsset.add (BioDataSource.ENTREZ_GENE);
		Map<Xref, Set<Xref>> refmap = mapper.mapID(src, dsset);
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
		dsset.add (BioDataSource.ENTREZ_GENE);
		Map<Xref, Set<Xref>> refmap = stack.mapID(src, dsset);
		Set<Xref> expected = new HashSet<Xref>();
		expected.add (new Xref ("3643", BioDataSource.ENTREZ_GENE));
		assertEquals (expected, refmap.get(INSR));
		expected.clear();
		expected.add (new Xref ("856831", BioDataSource.ENTREZ_GENE));
		assertEquals (expected, refmap.get(RAD51));
	}
	
}
