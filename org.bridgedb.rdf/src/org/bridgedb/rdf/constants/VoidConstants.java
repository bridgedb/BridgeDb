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

import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.URIImpl;

/**
 *
 */
public class VoidConstants {

    public static final String voidns = "http://rdfs.org/ns/void#";
    public static final String PREFIX_NAME = "void:";
    
    public static final URI DATA_DUMP = new URIImpl(voidns + "dataDump");
    public static final URI DATASET = new URIImpl(voidns + "Dataset");
    public static final URI DATASET_DESCRIPTION = new URIImpl(voidns + "DatasetDescription");
    public static final URI EXAMPLE_RESOURCE = new URIImpl(voidns + "exampleResource");
    public static final URI LINK_PREDICATE = new URIImpl(voidns + "linkPredicate");
    public static final URI IN_DATASET = new URIImpl(voidns + "inDataset");
    public static final URI LINKSET = new URIImpl(voidns + "Linkset");
    public static final URI TARGET = new URIImpl(voidns + "target");
    public static final URI TRIPLES = new URIImpl(voidns + "triples");
    public static final URI SUBJECTSTARGET = new URIImpl(voidns + "subjectsTarget");
    public static final URI OBJECTSTARGET = new URIImpl(voidns + "objectsTarget");
    public static final URI SUBSET = new URIImpl(voidns + "subset");
    public static final URI SPARQL_ENDPOINT = new URIImpl(voidns + "sparqlEndpoint");
    private static final String URI_SPACE = "uriSpace";
    public static final String URI_SPACE_SHORT = PREFIX_NAME + URI_SPACE;
    public static final URI URI_SPACE_URI = new URIImpl(voidns + URI_SPACE);
    public static final URI VOCABULARY = new URIImpl(voidns + "vocabulary");    

}
