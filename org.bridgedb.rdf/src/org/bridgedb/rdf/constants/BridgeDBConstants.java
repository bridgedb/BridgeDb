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

    public static final String PREFIX = "http://openphacts.cs.man.ac.uk:9090/ontology/DataSource.owl#";
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
    public static final URI HAS_DATA_TYPE_URI = new URIImpl(PREFIX + "hasDataType");
    public static final URI HAS_URL_PATTERN_URI = new URIImpl(PREFIX + "hasUrlPattern");
    public static final URI HAS_REGEX_PATTERN_URI = new URIImpl(PREFIX + "hasRegexPattern");
    public static final URI HAS_REGEX_URL_PATTERN_URI = new URIImpl(PREFIX + "hasRegexUrlPattern");
    public static final URI HAS_URI_PATTERN_URI = new URIImpl(PREFIX + "hasUriPattern");
    public static final URI HAS_REGEX_URI_PATTERN_URI = new URIImpl(PREFIX + "hasRegexUriPattern");
    public static final URI IS_DEPRICATED_BY_URI = new URIImpl(PREFIX + "isDepricatedBy");
    public static final String VARIOUS = PREFIX + "various";
    
    //old verions
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
    private static final String HAS_PREFIX = "hasPrefix";
    public static final URI HAS_PREFIX_URI = new URIImpl(PREFIX + HAS_PREFIX);
    private static final String HAS_POSTFIX = "hasPostfix";
    public static final URI HAS_POSTFIX_URI = new URIImpl(PREFIX + HAS_POSTFIX);

    private static final String IDENTIFERS_ORG_PATTERN = "IdentifiersOrgPattern";
    public static final URI HAS_IDENTIFERS_ORG_PATTERN_URI = new URIImpl(PREFIX + "has" + IDENTIFERS_ORG_PATTERN);
    private static final String IDENTIFERS_ORG_INFO_PATTERN = "IdentifiersOrgInfoPattern";
    public static final URI HAS_IDENTIFERS_ORG_INFO_PATTERN_URI = new URIImpl(PREFIX + "has" + IDENTIFERS_ORG_INFO_PATTERN);
 
    //CodeMapper feilds
    private static final String CODE_MAPPER = "CodeMapper";
    public static final String CODE_MAPPER1 = PREFIX + CODE_MAPPER;
    public static final URI CODE_MAPPER_URI = new URIImpl(CODE_MAPPER1);
    private static final String XREF_PREFIX = "xrefPrefix";
    public static final URI XREF_PREFIX_URI = new URIImpl(PREFIX + XREF_PREFIX);
 
    
    public static final URI HAS_DATA_SOURCE = new URIImpl(PREFIX + "hasDataSource");
    public static final URI IS_URI_PATTERN_OF = new URIImpl(PREFIX + "isUriPatternOf");
    
    private static final String bdb = "http://www.bridgedb.org/test#";	
    public static final URI TEST_PREDICATE = new URIImpl(bdb + "testPredicate");

    public static final URI VIA_URI = new URIImpl(PREFIX + "isTransativeVia");
    public static final URI IS_SYMETRIC = new URIImpl(PREFIX + "isSymetric");
    public static final URI LINKSET_JUSTIFICATION = new URIImpl(PREFIX + "linksetJustification");
    
    public static final URI LENS_URI = new URIImpl(PREFIX + "lens");
    
}
