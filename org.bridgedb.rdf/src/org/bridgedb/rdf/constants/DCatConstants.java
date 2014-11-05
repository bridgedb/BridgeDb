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
public class DCatConstants {
    public static final String voidns = "http://www.w3.org/ns/dcat#";
    public static final String PREFIX_NAME = "dcat";
    
    public static final String ACCESS_URL = "accessURL";
    public static final URI ACCESS_URL_URI = new URIImpl(voidns + ACCESS_URL);
    public static final String BYTE_SIZE = "byteSize";
    public static final URI BYTE_SIZE_URI = new URIImpl(voidns + BYTE_SIZE);
    public static final String DESCRIPTION = "description";
    public static final URI DESCRIPTION_URI = new URIImpl(voidns + DESCRIPTION);
    public static final String DISTRIBUTION = "distribution";
    public static final URI DISTRIBUTION_URI = new URIImpl(voidns + DISTRIBUTION);
    public static final String DOWNLOAD = "downloadURL";
    public static final URI DOWNLOAD_URI = new URIImpl(voidns + DOWNLOAD);
    public static final String LANDING_PAGE = "landingPage";
    public static final URI LANDING_PAGE_URI = new URIImpl(voidns + LANDING_PAGE);
    public static final String MEDIA_TYPE = "mediaType ";
    public static final URI MEDIA_TYPE_URI = new URIImpl(voidns + MEDIA_TYPE);
    public static final String THEME = "theme";
    public static final URI THEME_URI = new URIImpl(voidns + THEME);
    public static final String TITLE = "title";
    public static final URI TITLE_URI = new URIImpl(voidns + TITLE);
    
    

}
