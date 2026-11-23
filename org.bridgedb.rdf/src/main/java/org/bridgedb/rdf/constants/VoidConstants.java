// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009 BridgeDb developers
// Copyright 2012  Christian Y. A. Brenninkmeijer
// Copyright 2012  OpenPhacts
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
 */
public class VoidConstants {
	private static final SimpleValueFactory factory = SimpleValueFactory.getInstance();

    public static final String voidns = "http://rdfs.org/ns/void#";
    public static final String PREFIX_NAME = "void:";
    
    public static final IRI DATA_DUMP = factory.createIRI(voidns + "dataDump");
    public static final IRI DATASET = factory.createIRI(voidns + "Dataset");
    public static final IRI DATASET_DESCRIPTION = factory.createIRI(voidns + "DatasetDescription");
    public static final IRI EXAMPLE_RESOURCE = factory.createIRI(voidns + "exampleResource");
    public static final IRI LINK_PREDICATE = factory.createIRI(voidns + "linkPredicate");
    public static final IRI IN_DATASET = factory.createIRI(voidns + "inDataset");
    public static final IRI LINKSET = factory.createIRI(voidns + "Linkset");
    public static final IRI TARGET = factory.createIRI(voidns + "target");
    public static final IRI TRIPLES = factory.createIRI(voidns + "triples");
    public static final IRI SUBJECTSTARGET = factory.createIRI(voidns + "subjectsTarget");
    public static final IRI OBJECTSTARGET = factory.createIRI(voidns + "objectsTarget");
    public static final IRI SUBSET = factory.createIRI(voidns + "subset");
    public static final IRI SPARQL_ENDPOINT = factory.createIRI(voidns + "sparqlEndpoint");
    private static final String URI_SPACE = "uriSpace";
    public static final String URI_SPACE_SHORT = PREFIX_NAME + URI_SPACE;
    public static final IRI URI_SPACE_URI = factory.createIRI(voidns + URI_SPACE);
    public static final IRI VOCABULARY = factory.createIRI(voidns + "vocabulary");    

}
