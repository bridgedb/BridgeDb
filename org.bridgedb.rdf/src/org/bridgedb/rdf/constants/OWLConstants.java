// OpenPHACTS RDF Validator,
// A tool for validating and storing RDF.
//
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  University of Manchester
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
public class OWLConstants {

    private static final String xmlschemans = "http://www.w3.org/2002/07/owl#";
    
    public static final String EQUIVALENT_CLASS = xmlschemans + "equivalentClass";
    public static final String SAME_AS = xmlschemans + "sameAs";
    public static final IRI SAMEAS_URI = SimpleValueFactory.getInstance().createIRI(SAME_AS);     
    public static final String THING = "owl:Thing";

}
