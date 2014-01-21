/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class DCTermsConstants {
    public static final String voidns = "http://purl.org/dc/terms/";
    public static final String PREFIX_NAME = "dcterms";
    
    public static final String ALTERNATIVE = "alternative";
    public static final URI ALTERNATIVE_URI = new URIImpl(voidns + ALTERNATIVE);
    
    public static final String DESCRIPTION = "description";
    public static final URI DESCRIPTION_URI = new URIImpl(voidns + DESCRIPTION);
  
    public static final String TITLE = "title";
    public static final URI TITLE_URI = new URIImpl(voidns + TITLE);
}
