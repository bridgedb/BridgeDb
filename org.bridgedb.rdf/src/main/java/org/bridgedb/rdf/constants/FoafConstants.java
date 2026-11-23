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
public class FoafConstants {
	private static final SimpleValueFactory factory = SimpleValueFactory.getInstance();

    public static final String voidns = "http://xmlns.com/foaf/0.1/";
    public static final String PREFIX_NAME = "foaf";
      
    public static final String PAGE = "citeAsAuthority";
    public static final IRI PAGE_URI = factory.createIRI(voidns + PAGE);
    
    public static final String PRIMARY_TOPIC = "primaryTopic";
    public static final IRI PRIMARY_TOPIC_URI = factory.createIRI(voidns + PRIMARY_TOPIC);
    
}
