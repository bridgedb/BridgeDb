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
public class CitoConstants {
    public static final String voidns = "http://purl.org/spar/cito/";
    public static final String PREFIX_NAME = "cito";
      
    public static final String CITE_AS_AUTHORITY = "citeAsAuthority";
    public static final URI CITE_AS_AUTHORITY_URI = new URIImpl(voidns + CITE_AS_AUTHORITY);
  
}
