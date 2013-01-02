/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class BridgeDBConstants {

    public static final String PREFIX = "http://openphacts.cs.man.ac.uk:9090//ontology/DataSource.owl#";
    public static final String PREFIX_NAME1 = "bridgeDB";
    public static final String PREFIX_NAME = "bridgeDB:";

    //Types
    public static final String DATA_SOURCE_LABEL = "DataSource";
    public static final String DATA_SOURCE_SHORT = PREFIX_NAME + DATA_SOURCE_LABEL;
    public static final String DATA_SOURCE1 = PREFIX + DATA_SOURCE_LABEL;
    public static final URI DATA_SOURCE_URI = new URIImpl(PREFIX + DATA_SOURCE_LABEL);
    public static final String URI_MAPPING = "uriMapping";
    public static final String URI_MAPPING_SHORT = PREFIX_NAME + URI_MAPPING;
    public static final URI URI_MAPPING_URI = new URIImpl(PREFIX + URI_MAPPING);
    public static final String URI_PATTERN_LABEL = "uriPattern";
    public static final String URI_PATTERN_SHORT = PREFIX_NAME + URI_PATTERN_LABEL;
    public static final String URI_PATTERN1 = PREFIX + URI_PATTERN_LABEL;
    public static final URI URI_PATTERN_URI = new URIImpl(URI_PATTERN1);

    //DataSource fields
    private static final String ALTERNATIVE_FULL_NAME = "alternativeFullName";
    public static final String ALTERNATIVE_FULL_NAME_SHORT = PREFIX_NAME + ALTERNATIVE_FULL_NAME;
    public static final URI ALTERNATIVE_FULL_NAME_URI = new URIImpl(PREFIX + ALTERNATIVE_FULL_NAME);

    private static final String FULL_NAME = "fullName";
    public static final String FULL_NAME_SHORT = PREFIX_NAME + FULL_NAME;
    public static final URI FULL_NAME_URI = new URIImpl(PREFIX + FULL_NAME);
    private static final String ID_EXAMPLE = "idExample";
    public static final String ID_EXAMPLE_SHORT = PREFIX_NAME + ID_EXAMPLE;
    public static final URI ID_EXAMPLE_URI = new URIImpl(PREFIX + ID_EXAMPLE);
    private static final String MAIN_URL = "mainUrl";
    public static final String MAIN_URL_SHORT = PREFIX_NAME + MAIN_URL;
    public static final URI MAIN_URL_URI = new URIImpl(PREFIX + MAIN_URL);
    public static final String ORGANISM_LABEL = "Organism";
    public static final String ORGANISM_SHORT = PREFIX_NAME + ORGANISM_LABEL;
    public static final String ORGANISM1 = PREFIX + ORGANISM_LABEL;
    public static final URI ORGANISM_URI = new URIImpl(ORGANISM1);
    private static final String PRIMAY = "primary";
    public static final String PRIMAY_SHORT = PREFIX_NAME + PRIMAY;
    public static final URI PRIMAY_URI = new URIImpl(PREFIX + PRIMAY);
    private static final String SYSTEM_CODE = "systemCode";
    public static final String SYSTEM_CODE_SHORT = PREFIX_NAME + SYSTEM_CODE;
    public static final URI SYSTEM_CODE_URI = new URIImpl(PREFIX + SYSTEM_CODE);
    private static final String TYPE = "type";
    public static final String TYPE_SHORT = PREFIX_NAME + TYPE;
    public static final URI TYPE_URI = new URIImpl(PREFIX + TYPE);
    private static final String URL_PATTERN = "urlPattern";
    public static final String URL_PATTERN_SHORT = PREFIX_NAME + URL_PATTERN;
    public static final URI URL_PATTERN_URI = new URIImpl(PREFIX + URL_PATTERN);
    private static final String URN_BASE = "urnBase";
    public static final String URN_BASE_SHORT = PREFIX_NAME + URN_BASE;
    public static final URI URN_BASE_URI = new URIImpl(PREFIX + URN_BASE);

    //Organism Fields
    private static final String CODE = "code";
    public static final String CODE_SHORT = PREFIX_NAME + CODE;
    public static final URI CODE_URI = new URIImpl(PREFIX + CODE);
    private static final String SHORT_NAME = "shortName";
    public static final String SHORT_NAME_SHORT = PREFIX_NAME + SHORT_NAME;
    public static final URI SHORT_NAME_URI = new URIImpl(PREFIX + SHORT_NAME);
    private static final String LATIN_NAME = "latinName";
    public static final String LATIN_NAME_SHORT = PREFIX_NAME + LATIN_NAME;
    public static final URI LATIN_NAME_URI = new URIImpl(PREFIX + LATIN_NAME);

    //UriPatternFields
    private static final String POSTFIX = "postfix";
    public static final String POSTFIX_SHORT = PREFIX_NAME + POSTFIX;
    public static final URI POSTFIX_URI = new URIImpl(PREFIX + POSTFIX);

    private static final String HAS_DATA_SOURCE = "hasDataSource";
    public static final String HAS_DATA_SOURCE_SHORT = PREFIX_NAME + HAS_DATA_SOURCE;
    public static final URI HAS_DATA_SOURCE_URI = new URIImpl(PREFIX + HAS_DATA_SOURCE);

    private static final String HAS_URI_PATTERN = "hasUriPattern";
    public static final String HAS_URI_PATTERN_SHORT = PREFIX_NAME + HAS_URI_PATTERN;
    public static final URI HAS_URI_PATTERN_URI = new URIImpl(PREFIX + HAS_URI_PATTERN);
    
    //Relationships
    private static final String HAS_RELATIONSHIP = "hasRelationship";
    public static final String HAS_RELATIONSHIP_SHORT = PREFIX_NAME + HAS_RELATIONSHIP;
    public static final URI HAS_RELATIONSHIP_URI = new URIImpl(PREFIX + HAS_RELATIONSHIP);
    
    private static final String DATA_SOURCE_URL_PATTERN = "dataSourceUrlPattern";
    public static final String DATA_SOURCE_URL_PATTERN_SHORT = PREFIX_NAME + DATA_SOURCE_URL_PATTERN;
    public static final URI DATA_SOURCE_URL_PATTERN_URI = new URIImpl(PREFIX + DATA_SOURCE_URL_PATTERN);
    
    private static final String IDENTIFERS_ORG_PATTERN = "identifiersOrgPattern";
    public static final String IDENTIFERS_ORG_PATTERN_SHORT = PREFIX_NAME + IDENTIFERS_ORG_PATTERN;
    public static final URI IDENTIFERS_ORG_PATTERN_URI = new URIImpl(PREFIX + IDENTIFERS_ORG_PATTERN);
    
    private static final String WIKIPATHWAYS_PATTERN = "WikiPathwaysPattern";
    public static final String WIKIPATHWAYS_PATTERN_SHORT = PREFIX_NAME + WIKIPATHWAYS_PATTERN;
    public static final URI WIKIPATHWAYS_PATTERN_URI = new URIImpl(PREFIX + WIKIPATHWAYS_PATTERN);
    
    private static final String SOURCE_RDF_PATTERN = "sourceRdfPattern";
    public static final String SOURCE_RDF_PATTERN_SHORT = PREFIX_NAME + SOURCE_RDF_PATTERN;
    public static final URI SOURCE_RDF_PATTERN_URI = new URIImpl(PREFIX + SOURCE_RDF_PATTERN);
    
    private static final String BIO2RDF_PATTERN = "bio2RdfPattern";
    public static final String BIO2RDF_PATTERN_SHORT = PREFIX_NAME + BIO2RDF_PATTERN;
    public static final URI BIO2RDF_PATTERN_URI = new URIImpl(PREFIX + BIO2RDF_PATTERN);

    //URI types (Used in Format received from wikiPathways
    private static final String BIO2RDF = "bio2RDF";
    public static final String BIO2RDF_SHORT = PREFIX_NAME + BIO2RDF;
    public static final URI BIO2RDF_URI = new URIImpl(PREFIX + BIO2RDF);
    private static final String IDENTIFIERS_ORG_BASE = "identifiers_org_base";
    public static final String IDENTIFIERS_ORG_BASE_SHORT = PREFIX_NAME + IDENTIFIERS_ORG_BASE;
    public static final URI IDENTIFIERS_ORG_BASE_URI = new URIImpl(PREFIX + IDENTIFIERS_ORG_BASE);
    private static final String WIKIPATHWAYS_BASE = "wikipathways_id_base";
    public static final String WIKIPATHWAYS_BASE_SHORT = PREFIX_NAME + WIKIPATHWAYS_BASE;
    public static final URI WIKIPATHWAYS_BASE_URI = new URIImpl(PREFIX + WIKIPATHWAYS_BASE);
    private static final String SOURCE_RDF = "sourceRDFURI";
    public static final String SOURCE_RDF_SHORT = PREFIX_NAME + SOURCE_RDF;
    public static final URI SOURCE_RDF_URI = new URIImpl(PREFIX + SOURCE_RDF);
}
