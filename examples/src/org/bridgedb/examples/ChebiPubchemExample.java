package org.bridgedb.examples;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;

public class ChebiPubchemExample
{
	
	public static void main (String[] args) throws ClassNotFoundException, IDMapperException
	{
		// We'll use the BridgeRest webservice in this case, as it does compound mapping fairly well.
		// We'll use the human database, but it doesn't really matter which species we pick.
		Class.forName ("org.bridgedb.webservice.bridgerest.BridgeRest");
		IDMapper mapper = BridgeDb.connect("idmapper-bridgerest:http://webservice.bridgedb.org/Human");

		// Start with defining the Chebi identifier for
		// Methionine, id 16811
		Xref src = new Xref("16811", BioDataSource.CHEBI);		
		
		// the method returns a set, but in actual fact there is only one result
		for (Xref dest : mapper.mapID(src, BioDataSource.PUBCHEM_COMPOUND))
		{
			// this should print 6137, the pubchem identifier for Methionine.
			System.out.println ("" + dest.getId());
		}
	}

}
