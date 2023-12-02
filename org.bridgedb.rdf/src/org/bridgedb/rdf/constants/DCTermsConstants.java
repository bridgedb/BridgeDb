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
public class DCTermsConstants {
	private static final SimpleValueFactory factory = SimpleValueFactory.getInstance();

    public static final String voidns = "http://purl.org/dc/terms/";
    public static final String PREFIX_NAME = "dcterms";
     
    public static final String ACCRUAL_PERIODICITY = "accrualPeriodicity";
    public static final IRI ACCRUAL_PERIODICITY_URI = factory.createIRI(voidns + ACCRUAL_PERIODICITY);

    public static final String ALTERNATIVE = "alternative";
    public static final IRI ALTERNATIVE_URI = factory.createIRI(voidns + ALTERNATIVE);
    
    public static final String DESCRIPTION = "description";
    public static final IRI DESCRIPTION_URI = factory.createIRI(voidns + DESCRIPTION);
 
    public static final String ISSUED = "issued";
    public static final IRI ISSUED_URI = factory.createIRI(voidns + ISSUED);
  
    public static final String LICENSE = "license";
    public static final IRI LICENSE_URI = factory.createIRI(voidns + LICENSE);
  
    public static final String PUBLISHER = "publisher";
    public static final IRI PUBLISHER_URI = factory.createIRI(voidns + PUBLISHER);
  
    public static final String TITLE = "title";
    public static final IRI TITLE_URI = factory.createIRI(voidns + TITLE);
}
