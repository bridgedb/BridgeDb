package org.bridgedb.benchmarking;

import java.io.File;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.construct.DBConnectorDerbyServer;

import buildsystem.Measure;

public class TestAll extends Base
{
	private Measure measure;
	
	@Override public void setUp()
	{
		measure = new Measure("bridgedb_timing.txt");
	}

	public void testDerbyClient () throws IDMapperException, ClassNotFoundException
	{
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		Xref insr1 = new Xref ("ENSG00000171105", DataSource.getBySystemCode("EnHs"));
		Xref insr2 = new Xref ("3643", DataSource.getBySystemCode("L"));
		basicMapperTest (measure, "derbyclient", "idmapper-derbyclient:Homo sapiens", insr1, insr2);
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
		basicMapperTest (measure, "pgdb", "idmapper-pgdb:" + GDB_HUMAN, insr1, insr2);
	}	

	public void testPicr() throws IDMapperException, ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.picr.IDMapperPicr");
		Xref insr1 = new Xref ("YER095W", DataSource.getByFullName("SGD"));
		Xref insr2 = new Xref ("1SZP", DataSource.getByFullName("PDB"));
		basicMapperTest (measure, "picr", "idmapper-picr:", insr1, insr2);
	}

	public void testPicrRest() throws IDMapperException, ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.picr.IDMapperPicrRest");
		Xref insr1 = new Xref ("YER095W", DataSource.getByFullName("SGD"));
		Xref insr2 = new Xref ("1SZP", DataSource.getByFullName("PDB"));
		basicMapperTest (measure, "picr-rest", "idmapper-picr-rest:", insr1, insr2);
	}

	public void testSynergizer() throws IDMapperException, ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.synergizer.IDMapperSynergizer");
		Xref insr1 = new Xref ("snph", DataSource.getByFullName("hgnc_symbol"));
		Xref insr2 = new Xref ("9751", DataSource.getByFullName("entrezgene"));
		basicMapperTest (measure, "synergizer", "idmapper-synergizer:?authority=ensembl&species=Homo sapiens", insr1, insr2);
	}

	public void testBridgeWebservice() throws IDMapperException, ClassNotFoundException
	{
		BioDataSource.init();
		Class.forName("org.bridgedb.webservice.bridgerest.BridgeRest");
		Xref insr1 = new Xref ("ENSG00000171105", DataSource.getBySystemCode("EnHs"));
		Xref insr2 = new Xref ("3643", DataSource.getBySystemCode("L"));
		basicMapperTest (measure, "bridgerest", "idmapper-bridgerest:http://webservice.bridgedb.org/Human", insr1, insr2);
	}
	
	public void testFile() throws IDMapperException, ClassNotFoundException
	{
		File YEAST_IDS = new File ("../test-data/yeast_id_mapping.txt");
		assertTrue (YEAST_IDS.exists());
		Class.forName("org.bridgedb.file.IDMapperText");		
		Xref ref1 = new Xref("YHR055C", DataSource.getByFullName("Ensembl Yeast"));
		Xref ref2 = new Xref("U00061", DataSource.getByFullName("EMBL"));
		basicMapperTest (measure, "text", "idmapper-text:file://" + YEAST_IDS.getAbsolutePath(), ref1, ref2);		
	}
	
	public void testBioMart() throws IDMapperException, ClassNotFoundException
	{
		Class.forName("org.bridgedb.webservice.biomart.IDMapperBiomart");
		Xref insr1 = new Xref ("ENSG00000171105", DataSource.getByFullName("ensembl_gene_id"));
		Xref insr2 = new Xref ("3643", DataSource.getByFullName("entrezgene"));
		basicMapperTest (measure, "biomart", "idmapper-biomart:http://www.biomart.org/biomart/martservice?mart=ensembl&dataset=hsapiens_gene_ensembl", insr1, insr2);		
	}

	public void testCronos() throws IDMapperException, ClassNotFoundException
	{
		Class.forName ("org.bridgedb.webservice.cronos.IDMapperCronos");
		BioDataSource.init();
		Xref insr1 = new Xref ("ENSG00000171105", BioDataSource.ENSEMBL);
		Xref insr2 = new Xref ("3643", DataSource.getExistingBySystemCode("L"));
		basicMapperTest (measure, "cronos", "idmapper-cronos:hsa", insr1, insr2);		
	}
}

