package org.bridgedb.metadata.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class ChemInf {

	private static final String cheminfns = "http://semanticscience.org/resource/";
	
	public static final String INCHI_KEY = cheminfns + "CHEMINF_000059";
	public static final URI INCHI_KEY_URI = new URIImpl(INCHI_KEY);
	
}
