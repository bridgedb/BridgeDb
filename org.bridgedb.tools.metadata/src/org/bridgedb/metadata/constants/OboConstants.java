package org.bridgedb.metadata.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class OboConstants {

	public static final String PREFIX = "http://purl.obolibrary.org/obo#";
	
	public static final URI HAS_PART = new URIImpl(PREFIX + "has_part");
	public static final URI HAS_FUNCTIONAL_PARENT = new URIImpl(PREFIX + "has_functional_parent");
}
