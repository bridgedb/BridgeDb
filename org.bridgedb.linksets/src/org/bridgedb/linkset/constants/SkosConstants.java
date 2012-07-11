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
public class SkosConstants {

    private static final String skosns = "http://www.w3.org/2004/02/skos/core#";
    
    public static final URI BROAD_MATCH = new URIImpl(skosns + "broadMatch");
    public static final URI CLOSE_MATCH = new URIImpl(skosns + "closeMatch");
    public static final URI EXACT_MATCH = new URIImpl(skosns + "exactMatch");
    public static final URI MAPPING_RELATION = new URIImpl(skosns + "mappingRelation");
    public static final URI NARROW_MATCH = new URIImpl(skosns + "narrowMatch");
    public static final URI RELATED_MATCH = new URIImpl(skosns + "relatedMatch");

}
