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
public class IdenitifiersOrgConstants {
	private static final SimpleValueFactory factory = SimpleValueFactory.getInstance();

    public static final String voidns = "http://identifiers.org/terms#";
    public static final String PREFIX_NAME = "idot:";
    
    public static final String NAMESPACE = "namespace";
    public static final IRI NAMESPACE_URI = factory.createIRI(voidns + NAMESPACE);
    public static final String REGEX = "idRegexPattern";
    public static final IRI REGEX_URI = factory.createIRI(voidns + REGEX);

}
