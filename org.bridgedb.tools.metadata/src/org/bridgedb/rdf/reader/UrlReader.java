/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.bridgedb.utils.BridgeDBException;


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
    private String encodedAuthorization;
    private String login = "OpsReadOnly";
    private String password = "Gerhard1";
    private String method = "GET";
    
    public UrlReader(String address) throws BridgeDBException{
        this.address = address;
        scrubbedAddress = getPath();
        if (scrubbedAddress.startsWith(HTML_GITHUB)){
            scrubbedAddress = scrubbedAddress.replaceFirst(HTML_GITHUB, RAW_GITHUB);
            if (scrubbedAddress.contains(BLOB)){
                scrubbedAddress = scrubbedAddress.replaceFirst(BLOB, "");
            }
        }
        String authorization = login + ':'  + password;
        encodedAuthorization = new String(Base64.encodeBase64(authorization.getBytes()));
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

    //taken from https://github.com/kohsuke/github-api
    //http://opensource.org/licenses/mit-license.php
    private HttpURLConnection setupConnection(URL url) throws IOException {
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();

        // if the authentication is needed but no credential is given, try it anyway (so that some calls
        // that do work with anonymous access in the reduced form should still work.)
        // if OAuth token is present, it'll be set in the URL, so need to set the Authorization header
        uc.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

        try {
            uc.setRequestMethod(method);
        } catch (ProtocolException e) {
            // JDK only allows one of the fixed set of verbs. Try to override that
            try {
                Field $method = HttpURLConnection.class.getDeclaredField("method");
                $method.setAccessible(true);
                $method.set(uc,method);
            } catch (Exception x) {
                throw (IOException)new IOException("Failed to set the custom verb").initCause(x);
            }
        }
        //uc.setRequestProperty("Accept-Encoding", "gzip");
        return uc;
    }
    
    public InputStream getInputStream() throws IOException{
        System.out.println(scrubbedAddress);
        URL url = new URL(scrubbedAddress);
        HttpURLConnection uc = setupConnection(url);
        return uc.getInputStream();
   }
    
}
