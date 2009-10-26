package org.bridgedb.benchmarking;

import java.io.File;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.DBConnectorDerbyServer;

//import buildsystem.Measure;

public class TestAll extends Base
{
	@Override public void setUp()
	{
//		measure = new Measure("bridgedb_timing.txt");
	}

	public void testDerbyClient () throws IDMapperException, ClassNotFoundException
	{
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		DBConnectorDerbyServer.init ("wikipathways.org", 1527);
		Xref insr1 = new Xref ("ENSG00000171105", DataSource.getBySystemCode("EnHs"));
		Xref insr2 = new Xref ("3643", DataSource.getBySystemCode("L"));
		basicMapperTest ("idmapper-derbyclient:Homo sapiens", insr1, insr2);
	}

	static final String GDB_HUMAN = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Hs_Derby_20081119.pgdb";

	public void testDerby() throws IDMapperException, ClassNotFoundException
	{
		Xref insr1 = new Xref ("ENSG00000171105", DataSource.getBySystemCode("En"));
		Xref insr2 = new Xref ("3643", DataSource.getBySystemCode("L"));
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		
		assertTrue (new File(GDB_HUMAN).exists());
		basicMapperTest ("idmapper-pgdb:" + GDB_HUMAN, insr1, insr2);
	}	

	public void testPicr() throws IDMapperException, ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.picr.IDMapperPicr");
		Xref insr1 = new Xref ("YER095W", DataSource.getByFullName("SGD"));
		Xref insr2 = new Xref ("1SZP", DataSource.getByFullName("PDB"));
		basicMapperTest ("idmapper-picr:", insr1, insr2);
	}
	
	public void testSynergizer() throws IDMapperException, ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.synergizer.IDMapperSynergizer");
		Xref insr1 = new Xref ("snph", DataSource.getByFullName("hgnc_symbol"));
		Xref insr2 = new Xref ("9751", DataSource.getByFullName("entrezgene"));
		basicMapperTest ("idmapper-synergizer:authority=ensembl&species=Homo sapiens", insr1, insr2);
	}

	public void testBridgeWebservice() throws IDMapperException, ClassNotFoundException
	{
		BioDataSource.init();
		Class.forName("org.bridgedb.webservice.bridgerest.BridgeRest");
		Xref insr1 = new Xref ("ENSG00000171105", DataSource.getBySystemCode("EnHs"));
		Xref insr2 = new Xref ("3643", DataSource.getBySystemCode("L"));
		basicMapperTest ("idmapper-bridgerest:http://webservice.bridgedb.org/Human", insr1, insr2);
	}
	
	public void testFile() throws IDMapperException, ClassNotFoundException
	{
		File YEAST_IDS = new File ("../test-data/yeast_id_mapping.txt");
		assertTrue (YEAST_IDS.exists());
		Class.forName("org.bridgedb.file.IDMapperFile");		
		Xref ref1 = new Xref("YHR055C", DataSource.getByFullName("Ensembl Yeast"));
		Xref ref2 = new Xref("U00061", DataSource.getByFullName("EMBL"));
		basicMapperTest ("idmapper-file:" + YEAST_IDS, ref1, ref2);		
	}
}
