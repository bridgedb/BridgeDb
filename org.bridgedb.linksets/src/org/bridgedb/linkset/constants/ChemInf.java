package org.bridgedb.linkset.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class ChemInf {

	private static final String cheminfns = "http://semanticscience.org/resource/";
	
	public static final URI INCHI_KEY = new URIImpl(cheminfns + "CHEMINF_000059");
	
}
