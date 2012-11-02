package org.bridgedb.metadata.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class BridgeDBConstants {

	private static final String bdb = "http://www.bridgedb.org/test#";
	
	public static final URI TEST_PREDICATE = new URIImpl(bdb + "testPredicate");
	
}
