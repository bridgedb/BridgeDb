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
public class DCatConstants {
	private static final SimpleValueFactory factory = SimpleValueFactory.getInstance();

    public static final String voidns = "http://www.w3.org/ns/dcat#";
    public static final String PREFIX_NAME = "dcat";
    
    public static final String ACCESS_URL = "accessURL";
    public static final IRI ACCESS_URL_URI = factory.createIRI(voidns + ACCESS_URL);
    public static final String BYTE_SIZE = "byteSize";
    public static final IRI BYTE_SIZE_URI = factory.createIRI(voidns + BYTE_SIZE);
    public static final String DESCRIPTION = "description";
    public static final IRI DESCRIPTION_URI = factory.createIRI(voidns + DESCRIPTION);
    public static final String DISTRIBUTION = "distribution";
    public static final IRI DISTRIBUTION_URI = factory.createIRI(voidns + DISTRIBUTION);
    public static final String DOWNLOAD = "downloadURL";
    public static final IRI DOWNLOAD_URI = factory.createIRI(voidns + DOWNLOAD);
    public static final String LANDING_PAGE = "landingPage";
    public static final IRI LANDING_PAGE_URI = factory.createIRI(voidns + LANDING_PAGE);
    public static final String MEDIA_TYPE = "mediaType ";
    public static final IRI MEDIA_TYPE_URI = factory.createIRI(voidns + MEDIA_TYPE);
    public static final String THEME = "theme";
    public static final IRI THEME_URI = factory.createIRI(voidns + THEME);
    public static final String TITLE = "title";
    public static final IRI TITLE_URI = factory.createIRI(voidns + TITLE);
    
    

}
