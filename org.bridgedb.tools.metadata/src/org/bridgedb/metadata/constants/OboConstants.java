package org.bridgedb.metadata.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class OboConstants {

	private static final String obons = "http://purl.obolibrary.org/obo#";
	
	public static final URI HAS_PART = new URIImpl(obons + "has_part");
	
}
