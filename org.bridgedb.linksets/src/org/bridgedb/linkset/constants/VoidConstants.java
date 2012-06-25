/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 */
public class VoidConstants {

    private static final String voidns = "http://rdfs.org/ns/void#";
    
    public static final String DATASET = voidns + "Dataset";
    public static final URI DATASET_URI = new URIImpl(DATASET);
    public static final String LINK_PREDICATE = voidns + "linkPredicate";
    public static final URI LINK_PREDICATE_URI = new URIImpl(LINK_PREDICATE);
    public static final String LINKSET = voidns + "Linkset";
    public static final URI LINKSET_URI = new URIImpl(LINKSET);
    public static final String TARGET = voidns + "target";
    public static final URI TARGETURI = new URIImpl(TARGET);
    public static final String SUBJECTSTARGET = voidns + "subjectsTarget";
    public static final URI SUBJECTSTARGETURI = new URIImpl(SUBJECTSTARGET);
    public static final String OBJECTSTARGET = voidns + "objectsTarget";
    public static final URI OBJECTSTARGETURI = new URIImpl(OBJECTSTARGET);
    public static final String SUBSET = voidns + "subset";
    public static final String URI_SPACE = voidns + "uriSpace";
    public static final URI URI_SPACEURI = new URIImpl(URI_SPACE);
    

}
