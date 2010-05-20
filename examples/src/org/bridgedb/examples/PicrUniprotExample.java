package org.bridgedb.examples;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

public class PicrUniprotExample
{
	public static void main (String[] args) throws ClassNotFoundException, IDMapperException
	{
		// We want to map primary or secondary Uniprot ID's to the primary ID.
		// The best resource to do this is Picr, so we set up a connection
		Class.forName ("org.bridgedb.webservice.picr.IDMapperPicr");
		IDMapper mapper = BridgeDb.connect("idmapper-picr:");

		
		// what's there?
		System.out.println("IDMapperBiomart\n keys" + mapper.getCapabilities().getKeys().toString());
		System.out.println("supported src: " + mapper.getCapabilities().getSupportedSrcDataSources().toString());
		System.out.println("supported dst: " + mapper.getCapabilities().getSupportedTgtDataSources().toString());
		
		
		// we look for ID "Q91Y97"
		Xref src = new Xref("Q91Y97", DataSource.getByFullName("SWISSPROT"));
		
		// we request swissprot id's back. By default, we only get primary identifiers from picr. 
		// the method returns a set, but in actual fact there is only one result
		for (Xref dest : mapper.mapID(src, DataSource.getByFullName("SWISSPROT")))
		{
			//This should print Q91Y97 again, as it already is the primary identifier.
			System.out.println ("" + dest.getId());
		}
		
	}
}
