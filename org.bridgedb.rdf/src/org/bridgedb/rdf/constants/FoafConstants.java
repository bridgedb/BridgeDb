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
public class FoafConstants {
    public static final String voidns = "http://xmlns.com/foaf/0.1/";
    public static final String PREFIX_NAME = "foaf";
      
    public static final String PAGE = "citeAsAuthority";
    public static final URI PAGE_URI = new URIImpl(voidns + PAGE);
  
}
