/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 */
class VoidConstants {

    private static final String voidns = "http://rdfs.org/ns/void#";
    
    static final String DATASET = voidns + "Dataset";
    static final String LINK_PREDICATE = voidns + "linkPredicate";
    static final String TARGET = voidns + "target";
    static final URI TARGETURI = new URIImpl(TARGET);
    static final String SUBJECTSTARGET = voidns + "subjectsTarget";
    static final URI SUBJECTSTARGETURI = new URIImpl(SUBJECTSTARGET);
    static final String OBJECTSTARGET = voidns + "objectsTarget";
    static final URI OBJECTSTARGETURI = new URIImpl(OBJECTSTARGET);
    static final String SUBSET = voidns + "subset";
    static final String URI_SPACE = voidns + "uriSpace";
    static final URI URI_SPACEURI = new URIImpl(URI_SPACE);

}
