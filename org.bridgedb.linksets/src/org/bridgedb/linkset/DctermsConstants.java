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
class DctermsConstants {
    
    private static final String dctermns = "http://purl.org/dc/terms/";
    
    static final URI CREATED = new URIImpl(dctermns + "created");
    static final URI  CREATOR = new URIImpl(dctermns + "creator");
    
}
