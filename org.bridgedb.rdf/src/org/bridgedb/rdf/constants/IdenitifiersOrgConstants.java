/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.constants;

import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class IdenitifiersOrgConstants {
    public static final String voidns = "http://identifiers.org/terms#";
    public static final String PREFIX_NAME = "idot:";
    
    public static final String NAMESPACE = "namespace";
    public static final URI NAMESPACE_URI = new URIImpl(voidns + NAMESPACE);
    public static final String REGEX = "idRegexPattern";
    public static final URI REGEX_URI = new URIImpl(voidns + REGEX);
    
    

}
