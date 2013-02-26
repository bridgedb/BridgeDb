/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;


/**
 *
 * @author Christian
 */
public class UrlReader {
    
    private String address;
    private String scrubbedAddress;
    private String OPENPHACTS_GITHUB = "https://raw.github.com/openphacts/";
    private String RAW_GITHUB = "https://raw.github.com";
    private String HTML_GITHUB = "https://github.com";
    private String BLOB = "blob/";
    
    public UrlReader(String address) throws BridgeDBException{
        this.address = address;
        scrubbedAddress = getPath();
        if (scrubbedAddress.startsWith(HTML_GITHUB)){
            scrubbedAddress = scrubbedAddress.replaceFirst(HTML_GITHUB, RAW_GITHUB);
            if (scrubbedAddress.contains(BLOB)){
                scrubbedAddress = scrubbedAddress.replaceFirst(BLOB, "");
            }
        }
    }
    
    public final String getPath() throws BridgeDBException{
        java.net.URI uri;
        try {
            uri = new java.net.URI(address);
        } catch (URISyntaxException ex) {
            throw new BridgeDBException ("Illegal uri " + address, ex);
        }
        return uri.getScheme() + ":" + uri.getSchemeSpecificPart();
    }

    public InputStream getInputStream() throws IOException{
        if (scrubbedAddress.startsWith(OPENPHACTS_GITHUB)){
            if (!scrubbedAddress.contains("?login=")){
                scrubbedAddress = scrubbedAddress + "?login=OpsReadOnly&token=27cca7f3c4ef1005467366c2ae6df305";
            }
        }
        System.out.println(scrubbedAddress);
        URL uri = new URL(scrubbedAddress);
        return uri.openStream();
    }
    
}
