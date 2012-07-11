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
public class OwlConstants {

    private static final String owlns = "http://www.w3.org/2002/07/owl#";
    
    public static final URI EQUIVALENT_CLASS = new URIImpl(owlns + "equivalentClass");
    public static final URI SAME_AS = new URIImpl(owlns + "sameAs");

}
