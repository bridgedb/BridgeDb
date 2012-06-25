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
public class DctermsConstants {
    
    private static final String dctermns = "http://purl.org/dc/terms/";
    
    public static final URI CREATED = new URIImpl(dctermns + "created");
    public static final URI CREATOR = new URIImpl(dctermns + "creator");
    public static final URI LICENSE = new URIImpl(dctermns + "license");
    public static final URI SUBJECT = new URIImpl(dctermns + "subject");
    
}
