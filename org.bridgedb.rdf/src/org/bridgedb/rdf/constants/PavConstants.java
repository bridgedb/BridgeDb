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
 */
public class PavConstants {
	private static final SimpleValueFactory factory = SimpleValueFactory.getInstance();

    private static final String pavTermns = "http://purl.org/pav/";
    
    public static final IRI SOURCE_ACCESSED_BY = factory.createIRI(pavTermns + "sourceAccessedBy");
    public static final IRI SOURCE_ACCESSED_FROM = factory.createIRI(pavTermns + "sourceAccessedAt");
    public static final IRI SOURCE_ACCESSED_ON = factory.createIRI(pavTermns + "sourceAccessedOn");
    public static final IRI AUTHORED_BY = factory.createIRI(pavTermns + "authoredBy");
    public static final IRI AUTHORED_ON = factory.createIRI(pavTermns + "authoredOn");
    public static final IRI CREATED_BY = factory.createIRI(pavTermns + "createdBy");
    public static final IRI CREATED_ON = factory.createIRI(pavTermns + "createdOn");
    public static final IRI CREATED_WITH = factory.createIRI(pavTermns + "createdWith");
    public static final IRI DERIVED_BY = factory.createIRI(pavTermns + "derivedBy");
    public static final IRI DERIVED_FROM = factory.createIRI(pavTermns + "derivedFrom");
    public static final IRI DERIVED_ON = factory.createIRI(pavTermns + "derivedOn");
    public static final IRI IMPORTED_BY = factory.createIRI(pavTermns + "importedBy");
    public static final IRI IMPORTED_FROM = factory.createIRI(pavTermns + "importedFrom");
    public static final IRI IMPORTED_ON = factory.createIRI(pavTermns + "importedOn");
    public static final IRI LAST_REFERSHED_ON = factory.createIRI(pavTermns + "lastRefreshedOn");
    public static final IRI MODIFIED_ON = factory.createIRI(pavTermns + "modified");
    public static final IRI PREVIOUS_VERSION = factory.createIRI(pavTermns + "previousVersion");
    public static final IRI RETRIEVED_BY = factory.createIRI(pavTermns + "retrievedBy");
    public static final IRI RETRIEVED_ON = factory.createIRI(pavTermns + "retrievedOn");
    public static final IRI RETRIEVED_FROM = factory.createIRI(pavTermns + "retrievedFrom");
    public static final IRI VERSION = factory.createIRI(pavTermns + "version");
    
    
}
