package org.bridgedb.metadata.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class BridgeDBConstants {

	private static final String bdb = "http://www.bridgedb.org/test#";
	
	public static final URI TEST_PREDICATE = new URIImpl(bdb + "testPredicate");
	
    public static final String PREFIX = "http://openphacts.cs.man.ac.uk:9090//ontology/DataSource.owl#";

    public static final String DATA_SOURCE = "DataSource";
    public static final URI DATA_SOURCE_URI = new URIImpl(PREFIX + DATA_SOURCE);
    public static final String SYSTEM_CODE = "systemCode";
    public static final URI SYSTEM_CODE_URI = new URIImpl(PREFIX + SYSTEM_CODE);
    public static final String MAIN_URL = "mainUrl";
    public static final URI MAIN_URL_URI = new URIImpl(PREFIX + MAIN_URL);
    public static final String ID_EXAMPLE = "idExample";
    public static final URI ID_EXAMPLE_URI = new URIImpl(PREFIX + ID_EXAMPLE);
    public static final String IDENTIFIERS_ORG_BASE = "identifiers_org_base";
    public static final URI IDENTIFIERS_ORG_BASE_URI = new URIImpl(PREFIX + IDENTIFIERS_ORG_BASE);
    public static final String WIKIPATHWAYS_BASE = "wikipathways_id_base";
    public static final URI WIKIPATHWAYS_BASE_URI = new URIImpl(PREFIX + WIKIPATHWAYS_BASE);
    public static final String PRIMAY = "primary";
    public static final URI PRIMAY_URI = new URIImpl(PREFIX + PRIMAY);
    public static final String TYPE = "type";
    public static final URI TYPE_URI = new URIImpl(PREFIX + TYPE);
    public static final String URL_PATTERN = "urlPattern";
    public static final URI URL_PATTERN_URI = new URIImpl(PREFIX + URL_PATTERN);
    public static final String URN_BASE = "urnBase";
    public static final URI URN_BASE_URI = new URIImpl(PREFIX + URN_BASE);
    public static final String ORGANISM = "Organism";
    public static final URI ORGANISM_URI = new URIImpl(PREFIX + ORGANISM);
    public static final String FULL_NAME = "fullName";
    public static final URI FULL_NAME_URI = new URIImpl(PREFIX + FULL_NAME);
    public static final String CODE = "code";
    public static final URI CODE_URI = new URIImpl(PREFIX + CODE);
    public static final String SHORT_NAME = "shortName";
    public static final URI SHORT_NAME_URI = new URIImpl(PREFIX + SHORT_NAME);
    public static final String LATIN_NAME = "latinName";
    public static final URI LATIN_NAME_URI = new URIImpl(PREFIX + LATIN_NAME);

}
