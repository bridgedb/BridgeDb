package org.bridgedb.examples;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.bio.DataSourceTxt;

/*
 * This is a simple example script to demonstrate 
 * how BridgeDb makes identifier mapping easy.
 */
public class ExampleWithBridgeDb 
{
	/*
	 * Connect to a mapping service.
	 * Assume id is from the given source DataSource,
	 * and map that id to the given destination.  
	 */
	private void domapping(String connectString, String id, DataSource src, DataSource dest) throws IDMapperException
	{
		// connect to the mapper.
		IDMapper mapper = BridgeDb.connect(connectString);
		Xref srcRef = new Xref (id, src);
		
		// map the source Xref and print the results, one per line
		for (Xref destRef : mapper.mapID(srcRef, dest))
		{
			System.out.println ("  " + destRef);
		}
	}
	
	private void run() throws ClassNotFoundException, IDMapperException
	{
		DataSourceTxt.init();		

		// Entrez gene id for INSR.
		String id = "3643";
		
		Class.forName ("org.bridgedb.webservice.cronos.IDMapperCronos");
		domapping ("idmapper-cronos:hsa", "3643", BioDataSource.ENTREZ_GENE,
				BioDataSource.ENSEMBL_HUMAN);

		Class.forName ("org.bridgedb.webservice.synergizer.IDMapperSynergizer");
		domapping ("idmapper-synergizer:?authority=ensembl&species=Homo sapiens",
				id, DataSource.getByFullName("entrezgene"),
				DataSource.getByFullName("ensembl_gene_id"));
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IDMapperException
	{
		ExampleWithBridgeDb main = new ExampleWithBridgeDb();
		main.run();
	}
}