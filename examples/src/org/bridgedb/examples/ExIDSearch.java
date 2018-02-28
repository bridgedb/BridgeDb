package org.bridgedb.examples;

import java.util.Set;
import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTsv;

public class ExIDSearch 
{

	public static void main(String args[]) throws ClassNotFoundException, IDMapperException
	{
		// This example shows how to do a free text search for an identifier
		
		// first we have to load the driver
		// and initialize information about DataSources
		Class.forName("org.bridgedb.webservice.bridgerest.BridgeRest");
		DataSourceTsv.init();
		
		// now we connect to the driver and create a IDMapper instance.
		IDMapper mapper = BridgeDb.connect ("idmapper-bridgerest:http://webservice.bridgedb.org/Human");
		
		String query = "3643";
			
		// let's do a free search without specifying the input type:
		Set<Xref> hits = mapper.freeSearch(query, 100);
		
		// Now print the results.
		// with getURN we obtain valid MIRIAM urn's if possible.
		System.out.println (query + " search results:");
		for (Xref hit : hits)
			System.out.println("  " + hit.getURN());
	}
	
}
