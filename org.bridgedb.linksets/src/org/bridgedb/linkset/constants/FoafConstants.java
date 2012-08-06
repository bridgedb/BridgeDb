package org.bridgedb.linkset.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class FoafConstants {

	private static final String foafns = "http://xmlns.com/foaf/0.1/";
	
	public static final URI PRIMARY_TOPIC = new URIImpl(foafns + "primaryTopic");
}
