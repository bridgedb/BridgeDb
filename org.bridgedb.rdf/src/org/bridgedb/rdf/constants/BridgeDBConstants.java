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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 *
 * @author Christian
 */
public class BridgeDBConstants {
	private static final SimpleValueFactory factory = SimpleValueFactory.getInstance();

    public static final String OLD_PREFIX = "http://openphacts.cs.man.ac.uk:9090/ontology/DataSource.owl#";
    public static final String OLD_PREFIX_NAME = "bridgeDB";
    public static final String PREFIX = "http://vocabularies.bridgedb.org/ops#";
    public static final String PREFIX_NAME = "bdb";
    
    //Types
    public static final String DATA_SOURCE_LABEL = "DataSource";
    public static final String DATA_SOURCE1 = PREFIX + DATA_SOURCE_LABEL;
    public static final IRI DATA_SOURCE_URI = factory.createIRI(PREFIX + DATA_SOURCE_LABEL);
    //public static final String URI_MAPPING = "UriMapping";
    //public static final IRI URI_MAPPING_URI = factory.createIRI(PREFIX + URI_MAPPING);
    public static final String URI_PATTERN_LABEL = "UriPattern";
    public static final String URI_PATTERN1 = PREFIX + URI_PATTERN_LABEL;
    public static final IRI URI_PATTERN_URI = factory.createIRI(URI_PATTERN1);

    //DataSource fields
    private static final String FULL_NAME = "fullName";
    public static final IRI FULL_NAME_URI = factory.createIRI(PREFIX + FULL_NAME);
    private static final String ID_EXAMPLE = "idExample";
    public static final IRI ID_EXAMPLE_URI = factory.createIRI(PREFIX + ID_EXAMPLE);
    private static final String MAIN_URL = "mainUrl";
    public static final IRI MAIN_URL_URI = factory.createIRI(PREFIX + MAIN_URL);
    public static final String ORGANISM_LABEL = "Organism";
    public static final String ORGANISM1 = PREFIX + ORGANISM_LABEL;
    public static final IRI ORGANISM_URI = factory.createIRI(ORGANISM1);
    public static final String ABOUT_ORGANISM_LABEL = "aboutOrganism";
    public static final String ABOUT_ORGANISM1 = PREFIX + ORGANISM_LABEL;
    public static final IRI ABOUT_ORGANISM_URI = factory.createIRI(ORGANISM1);
    
    private static final String PRIMARY = "primary";
    public static final IRI PRIMARY_URI = factory.createIRI(PREFIX + PRIMARY);
    private static final String SYSTEM_CODE = "systemCode";
    public static final IRI SYSTEM_CODE_URI = factory.createIRI(PREFIX + SYSTEM_CODE);
    private static final String TYPE = "type";
    public static final IRI TYPE_URI = factory.createIRI(PREFIX + TYPE);
    public static final IRI HAS_DATA_TYPE_URI = factory.createIRI(PREFIX + "hasDataType");
    
    public static final IRI HAS_REGEX_PATTERN_URI = factory.createIRI(PREFIX + "hasRegexPattern");
    public static final IRI HAS_REGEX_URL_PATTERN_URI = factory.createIRI(PREFIX + "hasRegexUrlPattern");
    public static final IRI HAS_PRIMARY_URI_PATTERN_URI = factory.createIRI(PREFIX + "hasPrimaryUriPattern");
    public static final IRI HAS_URI_PATTERN_URI = factory.createIRI(PREFIX + "hasUriPattern");
    public static final IRI HAS_REGEX_URI_PATTERN_URI = factory.createIRI(PREFIX + "hasRegexUriPattern");
    public static final IRI IS_DEPRICATED_BY_URI = factory.createIRI(PREFIX + "isDepricatedBy");
    public static final String VARIOUS = PREFIX + "various";
    
    //old verions
    private static final String URN_BASE = "urnBase";
    public static final IRI URN_BASE_URI = factory.createIRI(PREFIX + URN_BASE);

    //Organism Fields
    private static final String CODE = "code";
    public static final IRI CODE_URI = factory.createIRI(PREFIX + CODE);
    private static final String SHORT_NAME = "shortName";
    public static final IRI SHORT_NAME_URI = factory.createIRI(PREFIX + SHORT_NAME);
    private static final String LATIN_NAME = "latinName";
    public static final IRI LATIN_NAME_URI = factory.createIRI(PREFIX + LATIN_NAME);

    //UriPatternFields    
    private static final String HAS_PREFIX = "hasPrefix";
    public static final IRI HAS_PREFIX_URI = factory.createIRI(PREFIX + HAS_PREFIX);
    private static final String HAS_POSTFIX = "hasPostfix";
    public static final IRI HAS_POSTFIX_URI = factory.createIRI(PREFIX + HAS_POSTFIX);

    private static final String IDENTIFERS_ORG_PATTERN = "IdentifiersOrgPattern";
    public static final IRI HAS_IDENTIFERS_ORG_PATTERN_URI = factory.createIRI(PREFIX + "has" + IDENTIFERS_ORG_PATTERN);
    private static final String IDENTIFERS_ORG_INFO_PATTERN = "IdentifiersOrgInfoPattern";
    public static final IRI HAS_IDENTIFERS_ORG_INFO_PATTERN_URI = factory.createIRI(PREFIX + "has" + IDENTIFERS_ORG_INFO_PATTERN);
 
    //CodeMapper feilds
    private static final String CODE_MAPPER = "CodeMapper";
    public static final String CODE_MAPPER1 = PREFIX + CODE_MAPPER;
    public static final IRI CODE_MAPPER_URI = factory.createIRI(CODE_MAPPER1);
    private static final String XREF_PREFIX = "xrefPrefix";
    public static final IRI XREF_PREFIX_URI = factory.createIRI(PREFIX + XREF_PREFIX);
 
    
    public static final IRI HAS_DATA_SOURCE = factory.createIRI(PREFIX + "hasDataSource");
    public static final IRI IS_URI_PATTERN_OF = factory.createIRI(PREFIX + "isUriPatternOf");
    
    private static final String bdb = "http://www.bridgedb.org/test#";	
    public static final IRI TEST_PREDICATE = factory.createIRI(bdb + "testPredicate");

    public static final IRI ASSERTION_METHOD = factory.createIRI(PREFIX + "assertionMethod ");
    public static final IRI VIA_URI = factory.createIRI(PREFIX + "isTransativeVia");
    public static final IRI IS_SYMETRIC = factory.createIRI(PREFIX + "isSymetric");
    public static final IRI LINKSET_JUSTIFICATION = factory.createIRI(PREFIX + "linksetJustification");
    public static final IRI SUBJECTS_DATATYPE = factory.createIRI(PREFIX + "subjectsDatatype");
    public static final IRI OBJECTS_DATATYPE = factory.createIRI(PREFIX + "objectsDatatype");
    public static final IRI SUBJECTS_SPECIES = factory.createIRI(PREFIX + "subjectsSpecies");
    public static final IRI OBJECTS_SPECIES  = factory.createIRI(PREFIX + "objectsSpecies");
    public static final IRI FULFILLS_LENS = factory.createIRI(PREFIX + "fulfillsLens");
    
    
    public static final IRI LENS_URI = factory.createIRI(PREFIX + "lens");
    public static final String TRANSITIVE = PREFIX + "Transitive";
    public static final IRI TRANSITIVE_URI = factory.createIRI(TRANSITIVE);
}
