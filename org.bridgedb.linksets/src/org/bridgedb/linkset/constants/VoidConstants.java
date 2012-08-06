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
    
    public static final URI DATASET_DESCRIPTION = new URIImpl(voidns + "DatasetDescription");
    public static final URI DATASET = new URIImpl(voidns + "Dataset");
    public static final URI LINK_PREDICATE = new URIImpl(voidns + "linkPredicate");
    public static final URI LINKSET = new URIImpl(voidns + "Linkset");
    public static final URI TARGET = new URIImpl(voidns + "target");
    public static final URI SUBJECTSTARGET = new URIImpl(voidns + "subjectsTarget");
    public static final URI OBJECTSTARGET = new URIImpl(voidns + "objectsTarget");
    //public static final String SUBSET = voidns + "subset";
    public static final URI URI_SPACE = new URIImpl(voidns + "uriSpace");

    

}
