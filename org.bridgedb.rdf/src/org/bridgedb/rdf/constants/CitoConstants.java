/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.constants;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 *
 * @author Christian
 */
public class CitoConstants {
    public static final String voidns = "http://purl.org/spar/cito/";
    public static final String PREFIX_NAME = "cito";
      
    public static final String CITE_AS_AUTHORITY = "citeAsAuthority";
    public static final IRI CITE_AS_AUTHORITY_URI = SimpleValueFactory.getInstance().createIRI(voidns + CITE_AS_AUTHORITY);
  
}
