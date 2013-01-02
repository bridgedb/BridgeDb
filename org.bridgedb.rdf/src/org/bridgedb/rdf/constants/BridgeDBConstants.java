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

    //Types
    public static final String DATA_SOURCE_LABEL = "DataSource";
    public static final String DATA_SOURCE1 = PREFIX + DATA_SOURCE_LABEL;
    public static final URI DATA_SOURCE_URI = new URIImpl(PREFIX + DATA_SOURCE_LABEL);
    public static final String URI_MAPPING = "uriMapping";
    public static final URI URI_MAPPING_URI = new URIImpl(PREFIX + URI_MAPPING);
    public static final String URI_PATTERN_LABEL = "uriPattern";
    public static final String URI_PATTERN1 = PREFIX + URI_PATTERN_LABEL;
    public static final URI URI_PATTERN_URI = new URIImpl(URI_PATTERN1);

    //DataSource fields
    private static final String ALTERNATIVE_FULL_NAME = "alternativeFullName";
    public static final URI ALTERNATIVE_FULL_NAME_URI = new URIImpl(PREFIX + ALTERNATIVE_FULL_NAME);

    private static final String FULL_NAME = "fullName";
    public static final URI FULL_NAME_URI = new URIImpl(PREFIX + FULL_NAME);
    private static final String ID_EXAMPLE = "idExample";
    public static final URI ID_EXAMPLE_URI = new URIImpl(PREFIX + ID_EXAMPLE);
    private static final String MAIN_URL = "mainUrl";
    public static final URI MAIN_URL_URI = new URIImpl(PREFIX + MAIN_URL);
    public static final String ORGANISM_LABEL = "Organism";
    public static final String ORGANISM1 = PREFIX + ORGANISM_LABEL;
    public static final URI ORGANISM_URI = new URIImpl(ORGANISM1);
    private static final String PRIMAY = "primary";
    public static final URI PRIMAY_URI = new URIImpl(PREFIX + PRIMAY);
    private static final String SYSTEM_CODE = "systemCode";
    public static final URI SYSTEM_CODE_URI = new URIImpl(PREFIX + SYSTEM_CODE);
    private static final String TYPE = "type";
    public static final URI TYPE_URI = new URIImpl(PREFIX + TYPE);
    private static final String URL_PATTERN = "urlPattern";
    public static final URI URL_PATTERN_URI = new URIImpl(PREFIX + URL_PATTERN);
    private static final String URN_BASE = "urnBase";
    public static final URI URN_BASE_URI = new URIImpl(PREFIX + URN_BASE);

    //Organism Fields
    private static final String CODE = "code";
    public static final URI CODE_URI = new URIImpl(PREFIX + CODE);
    private static final String SHORT_NAME = "shortName";
    public static final URI SHORT_NAME_URI = new URIImpl(PREFIX + SHORT_NAME);
    private static final String LATIN_NAME = "latinName";
    public static final URI LATIN_NAME_URI = new URIImpl(PREFIX + LATIN_NAME);

    //UriPatternFields
    private static final String POSTFIX = "postfix";
    public static final URI POSTFIX_URI = new URIImpl(PREFIX + POSTFIX);

    private static final String HAS_DATA_SOURCE = "hasDataSource";
    public static final URI HAS_DATA_SOURCE_URI = new URIImpl(PREFIX + HAS_DATA_SOURCE);

    private static final String HAS_URI_PATTERN = "hasUriPattern";
    public static final URI HAS_URI_PATTERN_URI = new URIImpl(PREFIX + HAS_URI_PATTERN);
    
    //Relationships
    private static final String HAS_RELATIONSHIP = "hasRelationship";
    public static final URI HAS_RELATIONSHIP_URI = new URIImpl(PREFIX + HAS_RELATIONSHIP);
    
    private static final String DATA_SOURCE_URL_PATTERN = "dataSourceUrlPattern";
    public static final URI DATA_SOURCE_URL_PATTERN_URI = new URIImpl(PREFIX + DATA_SOURCE_URL_PATTERN);
    
    private static final String IDENTIFERS_ORG_PATTERN = "identifiersOrgPattern";
    public static final URI IDENTIFERS_ORG_PATTERN_URI = new URIImpl(PREFIX + IDENTIFERS_ORG_PATTERN);
    
    private static final String WIKIPATHWAYS_PATTERN = "WikiPathwaysPattern";
    public static final URI WIKIPATHWAYS_PATTERN_URI = new URIImpl(PREFIX + WIKIPATHWAYS_PATTERN);
    
    private static final String SOURCE_RDF_PATTERN = "sourceRdfPattern";
    public static final URI SOURCE_RDF_PATTERN_URI = new URIImpl(PREFIX + SOURCE_RDF_PATTERN);
    
    private static final String BIO2RDF_PATTERN = "bio2RdfPattern";
    public static final URI BIO2RDF_PATTERN_URI = new URIImpl(PREFIX + BIO2RDF_PATTERN);

    //URI types (Used in Format received from wikiPathways
    private static final String IDENTIFIERS_ORG_BASE = "identifiers_org_base";
    public static final URI IDENTIFIERS_ORG_BASE_URI = new URIImpl(PREFIX + IDENTIFIERS_ORG_BASE);
    private static final String WIKIPATHWAYS_BASE = "wikipathways_id_base";
    public static final URI WIKIPATHWAYS_BASE_URI = new URIImpl(PREFIX + WIKIPATHWAYS_BASE); //used only to ignore in one file used
}
