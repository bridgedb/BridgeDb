package org.bridgedb.examples;

import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.bio.DataSourceTxt;

public class ExHello 
{

	public static void main(String args[]) throws ClassNotFoundException, IDMapperException
	{
		// This example shows how to map an identifier
		// using BridgeWebservice
		
		// first we have to load the driver
		// and initialize information about DataSources
		Class.forName("org.bridgedb.webservice.bridgerest.BridgeRest");
		DataSourceTxt.init();
		
		// now we connect to the driver and create a IDMapper instance.
		IDMapper mapper = BridgeDb.connect ("idmapper-bridgerest:http://webservice.bridgedb.org/Human");
		
		// We create an Xref instance for the identifier that we want to look up.
		// In this case we want to look up Entrez gene 3643.
		Xref src = new Xref ("3643", BioDataSource.ENTREZ_GENE);
		
		// let's see if there are cross-references to Ensembl Human
		Set<Xref> dests = mapper.mapID(src, DataSource.getBySystemCode("EnHs"));
		
		// and print the results.
		// with getURN we obtain valid MIRIAM urn's if possible.
		System.out.println (src.getURN() + " maps to:");
		for (Xref dest : dests)
			System.out.println("  " + dest.getURN());
		
	}
	
}
