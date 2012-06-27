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
public class PavConstants {
    
    private static final String pavTermns = "http://purl.org/pav/2.0/";
    
    public static final URI AUTHORED_BY = new URIImpl(pavTermns + "authoredBy");
    public static final URI AUTHORED_ON = new URIImpl(pavTermns + "authorOn");
    public static final URI CREATED_BY = new URIImpl(pavTermns + "createdBy");
    public static final URI CREATED_ON = new URIImpl(pavTermns + "created");
    public static final URI DERIVED_BY = new URIImpl(pavTermns + "derivedBy");
    public static final URI DERIVED_FROM = new URIImpl(pavTermns + "derivedFrom");
    public static final URI DERIVED_ON = new URIImpl(pavTermns + "derivedOn");
    public static final URI IMPORTED_ON = new URIImpl(pavTermns + "importedOn");
    public static final URI MODIFIED_ON = new URIImpl(pavTermns + "modified");
    public static final URI RETRIEVED_ON = new URIImpl(pavTermns + "retrievedOn");
    public static final URI VERSION = new URIImpl(pavTermns + "version");
    
    
}
