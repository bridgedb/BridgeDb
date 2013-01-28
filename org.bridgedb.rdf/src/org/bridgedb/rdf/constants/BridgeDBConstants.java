// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
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
    public static final String PRIMARY = "Primary";

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
    public static final URI HAS_URL_PATTERN_URI = new URIImpl(PREFIX + "hasUrlPattern");
    public static final URI HAS_PRIMARY_URL_PATTERN_URI = new URIImpl(PREFIX + "has" + PRIMARY + "UrlPattern");
    public static final URI HAS_URI_PATTERN_URI = new URIImpl(PREFIX + "hasUriPattern");
    public static final URI HAS_PRIMARY_URI_PATTERN_URI = new URIImpl(PREFIX + "has" + PRIMARY + "UriPattern");
    //old verions
    public static final URI URL_PATTERN_URI = new URIImpl(PREFIX + "urlPattern");
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

    private static final String IDENTIFERS_ORG_PATTERN = "IdentifiersOrgPattern";
    public static final URI IDENTIFERS_ORG_BASE = new URIImpl(PREFIX + "identifiers_org_base");
    public static final URI HAS_PRIMARY_IDENTIFERS_ORG_PATTERN_URI = new URIImpl(PREFIX + "has" + PRIMARY + IDENTIFERS_ORG_PATTERN);
    public static final URI HAS_IDENTIFERS_ORG_PATTERN_URI = new URIImpl(PREFIX + "has" + IDENTIFERS_ORG_PATTERN);

    private static final String WIKIPATHWAYS_PATTERN = "WikiPathwaysPattern";
    public static final URI HAS_PRIMARY_WIKIPATHWAYS_PATTERN_URI = new URIImpl(PREFIX + "has" + PRIMARY + WIKIPATHWAYS_PATTERN);
    public static final URI HAS_WIKIPATHWAYS_PATTERN_URI = new URIImpl(PREFIX + "has" + WIKIPATHWAYS_PATTERN);
    
    private static final String SOURCE_RDF_PATTERN = "SourceRdfPattern";
    public static final URI HAS_PRIMARY_SOURCE_RDF_PATTERN_URI = new URIImpl(PREFIX + "has" + PRIMARY + SOURCE_RDF_PATTERN);
    public static final URI HAS_SOURCE_RDF_PATTERN_URI = new URIImpl(PREFIX + "has" + SOURCE_RDF_PATTERN);
    public static final URI SOURCE_RDF_PATTERN_URI = new URIImpl(PREFIX + "sourceRDFURI");
    
    private static final String BIO2RDF_PATTERN = "Bio2RdfPattern";
    public static final URI HAS_PRIMARY_BIO2RDF_PATTERN_URI = new URIImpl(PREFIX + "has" + PRIMARY + BIO2RDF_PATTERN);
    public static final URI HAS_BIO2RDF_PATTERN_URI = new URIImpl(PREFIX + "has" + BIO2RDF_PATTERN);
    public static final URI BIO2RDF_PATTERN_URI = new URIImpl(PREFIX + "bio2RDF");

    public static final URI HAS_URI_PARENT_URI = new URIImpl(PREFIX + "hasUriParent");

    public static final URI HAS_DATA_SOURCE = new URIImpl(PREFIX + "hasDataSource");
    public static final URI IS_URI_PATTERN_OF = new URIImpl(PREFIX + "isUriPatternOf");
}
