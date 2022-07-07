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
public class DCTermsConstants {
    public static final String voidns = "http://purl.org/dc/terms/";
    public static final String PREFIX_NAME = "dcterms";
     
    public static final String ACCRUAL_PERIODICITY = "accrualPeriodicity";
    public static final URI ACCRUAL_PERIODICITY_URI = new URIImpl(voidns + ACCRUAL_PERIODICITY);

    public static final String ALTERNATIVE = "alternative";
    public static final URI ALTERNATIVE_URI = new URIImpl(voidns + ALTERNATIVE);
    
    public static final String DESCRIPTION = "description";
    public static final URI DESCRIPTION_URI = new URIImpl(voidns + DESCRIPTION);
 
    public static final String ISSUED = "issued";
    public static final URI ISSUED_URI = new URIImpl(voidns + ISSUED);
  
    public static final String LICENSE = "license";
    public static final URI LICENSE_URI = new URIImpl(voidns + LICENSE);
  
    public static final String PUBLISHER = "publisher";
    public static final URI PUBLISHER_URI = new URIImpl(voidns + PUBLISHER);
  
    public static final String TITLE = "title";
    public static final URI TITLE_URI = new URIImpl(voidns + TITLE);
}
