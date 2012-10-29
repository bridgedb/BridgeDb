/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class XsdConstants {
    
    public final static String PREFIX = "http://www.w3.org/2001/XMLSchema#";
    public static final String STRING = PREFIX + "string";
    public static final URI STRING_URI = new URIImpl(STRING);
    public static final URI INTEGER_URI = new URIImpl(PREFIX + "integer");
              
}
