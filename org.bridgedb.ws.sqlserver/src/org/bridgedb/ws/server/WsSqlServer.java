/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.server;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.sql.URLSqlFactory;
import org.bridgedb.ws.WSService;

/**
 *
 * @author Christian
 */
public class WsSqlServer extends WSService{
    
    public WsSqlServer() throws BridgeDbSqlException  {
        SQLAccess sqlAccess = URLSqlFactory.createSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        idMapper = urlMapperSQL;
        urlMapper = urlMapperSQL;
        byXrefPosition = urlMapperSQL;
        byURLPosition = urlMapperSQL;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInnerPure;
        StringBuilder sbInnerEncoded;

        Xref first = byXrefPosition.getXrefByPosition(0);
        Xref second = byXrefPosition.getXrefByPosition(1);
        Set<Xref> firstMaps = idMapper.mapID(first);
        Set<String> keys = idMapper.getCapabilities().getKeys();

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>Open PHACTS Identity Mapping Service</h1>");
        sb.append("<p>Welcome to the prototype Identity Mapping Service. </p>");
        
        sb.append("<p>The Main OPS method is <a href=\"/OPS-IMS/api/#mapByURL\">mapByURL</a></dt>");
        sb.append("<dd>List the URLs that map to this URL</dd>");
        sb.append("<p><a href=\"/OPS-IMS/api\">API Page</a></p>");
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/api")
    public Response apiPage() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInnerPure;
        StringBuilder sbInnerEncoded;

        Xref first = byXrefPosition.getXrefByPosition(0);
        Xref second = byXrefPosition.getXrefByPosition(1);
        Set<Xref> firstMaps = idMapper.mapID(first);
        Set<String> keys = idMapper.getCapabilities().getKeys();

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>Open PHACTS Identity Mapping Service</h1>");
        sb.append("<p><a href=\"/OPS-IMS\">Home Page</a></p>");
        sb.append("<p>Welcome to the prototype Identity Mapping Service. ");
                
        sb.append("<p>");
        sb.append("Support services include:");
        sb.append("<dl>");
        sb.append("<dt><a href=\"#mapByURLs\">mapByURLs</a></dt>");
        sb.append("<dd>List the URLs that map to these URLs</dd>");
        sb.append("<dt><a href=\"#mapByURL\">mapByURL</a></dt>");
        sb.append("<dd>List the URLs that map to this URL</dd>");
        sb.append("<dt><a href=\"#URLExists\">URLExists</a></dt>");
        sb.append("<dd>State if the URL is know to the Mapping Service or not</dd>");
        sb.append("<dt><a href=\"#URLSearch\">URLSearch</a></dt>");
        sb.append("<dd>Searches for URLs that have this ending.</dd>");
        if (byURLPosition != null){ 
            sb.append("<dt><a href=\"#getURLByposition\">getURLByposition</a></dt>");
            sb.append("<dd>Returns the URL(s) currently at this position.</dd>");
        }
        
        
        sb.append("<dt><a href=\"#mapByXRef\">mapByXRef</a></dt>");
        sb.append("<dd>List the Xrefs that map to this Xref</dd>");
        sb.append("<dt><a href=\"#mapByXRefs\">mapByXRefs</a></dt>");
        sb.append("<dd>List the Xrefs that map to these Xrefs</dd>");
        sb.append("<dt><a href=\"#xrefExists\">xrefExists</a></dt>");
        sb.append("<dd>State if the Xref is know to the Mapping Service or not</dd>");       
        sb.append("<dt><a href=\"#freeSearch\">freeSearch</a></dt>");
        sb.append("<dd>Searches for Xrefs that have this id.</dd>");
        sb.append("<dt><a href=\"#getCapabilities\">getCapabilities</a></dt>");
        sb.append("<dd>Gives the Capabilitles as defined by BridgeDB.</dd>");
        sb.append("<dt>Close()</a></dt>");
        sb.append("<dd>Not supported as clients should not be able to close the server.</dd>");
        sb.append("<dt>isConnected</dt>");
        sb.append("<dd>Not supported as Close() is not allowed</dd>");
        
        if (byXrefPosition != null){ 
            sb.append("<dt><a href=\"#getXrefByposition\">getXrefByposition</a></dt>");
            sb.append("<dd>Returns the Xref(s) currently at this position.</dd>");
        }
        
        sb.append("<dt><a href=\"#isFreeSearchSupported\">isFreeSearchSupported</a></dt>");
        if (idMapper.getCapabilities().isFreeSearchSupported()){
            sb.append("<dd>Returns True as freeSearch and URLSearch are supported.</dd>");
        } else {
            sb.append("<dd>Returns False because underlying IDMappper does not support freeSearch or URLSearch.</dd>");                
        }        
        sb.append("<dt><a href=\"#getSupportedSrcDataSources\">getSupportedSrcDataSources</a></dt>");
        sb.append("<dd>Returns Supported Source (BridgeDB)DataSource(s).</dd>");
        sb.append("<dt><a href=\"#getSupportedTgtDataSources\">getSupportedTgtDataSources</a></dt>");
        sb.append("<dd>Returns Supported Target (BridgeDB)DataSource(s).</dd>");
        sb.append("<dt><a href=\"#isMappingSupported\">isMappingSupported</a></dt>");
        sb.append("<dd>States if two DataSources are mapped at least once.</dd>");
        sb.append("<dt><a href=\"#getProperty\">getProperty</a></dt>");
        if (keys.isEmpty()){
            sb.append("<dd>There are currently no properties supported.</dd>");
        } else {
            sb.append("<dd>Returns The property value for this key.</dd>");
        }
        sb.append("<dt><a href=\"#getKeys\">getKeys</a></dt>");
        if (keys.isEmpty()){
            sb.append("<dd>There are currently no properties supported.</dd>");
        } else {
            sb.append("<dd>Returns The keys and their property value.</dd>");
        }
        sb.append("</dt>");
        sb.append("</p>");
                
        sb.append("<h2>URL based methods");
        sb.append("<h3><a name=\"mapByURL\">mapByURL</h3>");
            sb.append("<ul>");
            sb.append("<li>List the URLs that map to this URL</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>srcURL as string</li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li>tgtNameSpace as string</li> ");
                    sb.append("<ul>");        
                    sb.append("<li>There can be more than one</li>");        
                    sb.append("<li>This is the NameSpace part of the URLs to mapped to</li>");
                    sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sbInnerPure = new StringBuilder("mapByURL?srcURL=");
                    sbInnerEncoded = new StringBuilder("mapByURL?srcURL=");
                    sbInnerPure.append(first.getUrl());
                    sbInnerEncoded.append(URLEncoder.encode(first.getUrl(), "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    for (Xref map:firstMaps){
                        sbInnerPure.append("&tgtNameSpace=");
                        sbInnerPure.append(map.getDataSource().getNameSpace());
                        sbInnerEncoded.append("&tgtNameSpace=");
                        sbInnerEncoded.append(URLEncoder.encode(map.getDataSource().getNameSpace(), "UTF-8"));
                    }
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");                    
            sb.append("</ul>");

        sb.append("<h3><a name=\"mapByURLs\">mapByURLs</h3>");
            sb.append("<ul>");
            sb.append("<li>List the URLs that map to these URLs</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>srcURL as string</li>");
                sb.append("<li>There can be more than one</li>");        
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li>tgtNameSpace as string</li> ");
                    sb.append("<ul>");        
                    sb.append("<li>There can be more than one</li>");        
                    sb.append("<li>This is the NameSpace part of the URLs to mapped to</li>");
                    sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sbInnerPure = new StringBuilder("mapByURLs?srcURL=");
                    sbInnerEncoded = new StringBuilder("mapByURLs?srcURL=");
                    sbInnerPure.append(first.getUrl());
                    sbInnerEncoded.append(URLEncoder.encode(first.getUrl(), "UTF-8"));
                    sbInnerPure.append("&srcURL=");
                    sbInnerEncoded.append("&srcURL=");
                    sbInnerPure.append(second.getUrl());
                    sbInnerEncoded.append(URLEncoder.encode(second.getUrl(), "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    for (Xref map:firstMaps){
                        sbInnerPure.append("&tgtNameSpace=");
                        sbInnerPure.append(map.getDataSource().getNameSpace());
                        sbInnerEncoded.append("&tgtNameSpace=");
                        sbInnerEncoded.append(URLEncoder.encode(map.getDataSource().getNameSpace(), "UTF-8"));
                    }
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");                    
            sb.append("</ul>");

        sb.append("<h3><a name=\"URLExists\">URLExists</h3>");
            sb.append("<ul>");
            sb.append("<li>State if the URL is know to the Mapping Service or not</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>URL as string</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sbInnerPure = new StringBuilder("URLExists?URL=");
                    sbInnerEncoded = new StringBuilder("URLExists?URL=");
                    sbInnerPure.append(first.getUrl());
                    sbInnerEncoded.append(URLEncoder.encode(first.getUrl(), "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("</ul>");

        sb.append("<h3><a name=\"URLSearch\">URLSearch</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for URLs that have this ending.</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>text as string</li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li>limit as an Integer </li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sbInnerPure = new StringBuilder("URLSearch?text=");
                    sbInnerEncoded = new StringBuilder("URLSearch?text=");
                    sbInnerPure.append(first.getId());
                    sbInnerEncoded.append(URLEncoder.encode(first.getId(), "UTF-8"));
                    sbInnerPure.append("&limit=5");
                    sbInnerEncoded.append("&limit=5");
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("</ul>");
            
         if (byURLPosition != null){ 
            sb.append("<h3><a name=\"getURLByposition\">getURLByposition</h3>");
                sb.append("<ul>");
                sb.append("<li>Returns the URL(s) currently at this position.</li>");
                sb.append("<li><b>WARNING:</b> There is no guarantee that the same position will return the same URL over time.</li>");
                    sb.append("<ul>");
                    sb.append("<li>This method can <b>NOT</b> be used as an Identifier.</li>");
                    sb.append("</ul>");
                sb.append("<li>Used to Implement a URL version of:  org.bridgedb.XrefIterator</li>");
                sb.append("<li>Required arguements:</li>");
                    sb.append("<ul>");
                    sb.append("<li>position as Integer</li>");
                    sb.append("</ul>");
                sb.append("<li>Optional arguments</li>");
                    sb.append("<ul>");
                    sb.append("<li>nameSpace as String </li>");
                    sb.append("<li>limit as an Integer </li>");
                        sb.append("<ul>");
                        sb.append("<li>Will return this number of URLs starting at position. (assuming that many remain) </li>");
                        sb.append("</ul>");
                    sb.append("</ul>");
                sb.append("<li>Example: <a href=\"");
                        sb.append(uriInfo.getBaseUri());
                        sb.append("getURLByposition?position=2");
                        sb.append("\">");
                        sb.append("getURLByposition?position=2");
                        sb.append("</a></li>");    
                sb.append("<li>Example: <a href=\"");
                        sb.append(uriInfo.getBaseUri());
                        sb.append("getURLByposition?position=0&limit=20&nameSpace=");
                        sb.append(URLEncoder.encode(first.getDataSource().getNameSpace(), "UTF-8"));
                        sb.append("\">");
                        sb.append("getURLByposition?position=0&limit=20&nameSpace=");
                        sb.append(first.getDataSource().getNameSpace());
                        sb.append("</a></li>");    
                sb.append("</ul>");
         }

        sb.append("<h2>Implementations of BridgeDB's IDMapper methods</h2>");
        sb.append("<h3><a name=\"mapByXRef\">mapByXRef</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set&ltXref&gt mapID (Xref ref, DataSource... tgtDataSources)</li>");
            sb.append("<li>List the Xrefs that map to this Xref</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>id as string</li>");
                sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li>tgtCode as string</li> ");
                    sb.append("<ul>");        
                    sb.append("<li>There can be more than one</li>");        
                    sb.append("<li>Where code is the SystemCode of the DataSource</li>");
                    sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sbInnerPure = new StringBuilder("mapByXRef?id=");
                    sbInnerEncoded = new StringBuilder("mapByXRef?id=");
                    sbInnerPure.append(first.getId());
                    sbInnerEncoded.append(URLEncoder.encode(first.getId(), "UTF-8"));
                    sbInnerPure.append("&code=");
                    sbInnerEncoded.append("&code=");
                    sbInnerPure.append(first.getDataSource().getSystemCode());
                    sbInnerEncoded.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    for (Xref map:firstMaps){
                        sbInnerPure.append("&tgtCode=");
                        sbInnerEncoded.append("&tgtCode=");
                        sbInnerPure.append(map.getDataSource().getSystemCode());
                        sbInnerEncoded.append(URLEncoder.encode(map.getDataSource().getSystemCode(), "UTF-8"));
                    }
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");                    
            sb.append("</ul>");

        sb.append("<h3><a name=\"mapByXRefs\">mapByXRefs</h3>");
            sb.append("<ul>");
            sb.append("<li>List the Xrefs that map to these Xrefs</li>");
            sb.append("<li>Implements:  Map&ltXref, Set&ltXref&gt&lt mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources)</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>id as string</li>");
                sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
                sb.append("<li>(There can be multiple \"id\" and \"code\" values</li>");
                    sb.append("<ul>");
                    sb.append("<li>There must be at least one of each.</li>");                
                    sb.append("<li>There must be the same number of each.</li>");                
                    sb.append("<li>They will be paired by order.</li>");                
                    sb.append("</ul>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                    sb.append("<li>tgtCode as string</li> ");
                    sb.append("<li>Where code is the SystemCode of the DataSource</li>");
                sb.append("</ul>");        
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sbInnerPure = new StringBuilder("mapByXRefs?id=");
                    sbInnerEncoded = new StringBuilder("mapByXRefs?id=");
                    sbInnerPure.append(first.getId());
                    sbInnerEncoded.append(first.getId());
                    sbInnerPure.append("&code=");
                    sbInnerEncoded.append("&code=");
                    sbInnerPure.append(first.getDataSource().getSystemCode());
                    sbInnerEncoded.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                    sbInnerPure.append("&id=");
                    sbInnerEncoded.append("&id=");
                    sbInnerPure.append(second.getId());
                    sbInnerEncoded.append(URLEncoder.encode(second.getId(), "UTF-8"));
                    sbInnerPure.append("&code=");
                    sbInnerEncoded.append("&code=");
                    sbInnerPure.append(second.getDataSource().getSystemCode());
                    sbInnerEncoded.append(URLEncoder.encode(second.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    for (Xref map:firstMaps){
                        sbInnerPure.append("&tgtCode=");
                        sbInnerEncoded.append("&tgtCode=");
                        sbInnerPure.append(map.getDataSource().getSystemCode());
                        sbInnerEncoded.append(URLEncoder.encode(map.getDataSource().getSystemCode(), "UTF-8"));
                    }
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");                    
            sb.append("</ul>");
            
        sb.append("<h3><a name=\"xrefExists\">xrefExists</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  boolean xrefExists(Xref xref)</li>");
            sb.append("<li>State if the Xref is know to the Mapping Service or not</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>id as string</li>");
                sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sbInnerPure = new StringBuilder("xrefExists?id=");
                    sbInnerEncoded = new StringBuilder("xrefExists?id=");
                    sbInnerPure.append(first.getId());
                    sbInnerEncoded.append(URLEncoder.encode(first.getId(), "UTF-8"));
                    sbInnerPure.append("&code=");
                    sbInnerEncoded.append("&code=");
                    sbInnerPure.append(first.getDataSource().getSystemCode());
                    sbInnerEncoded.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("</ul>");
            
         sb.append("<h3><a name=\"freeSearch\">freeSearch</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for Xrefs that have this id.</li>");
            sb.append("<li>Implements:  Set@ltXref@gt freeSearch (String text, int limit)</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>text as string</li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li>limit as an Integer </li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sbInnerPure = new StringBuilder("freeSearch?text=");
                    sbInnerEncoded = new StringBuilder("freeSearch?text=");
                    sbInnerPure.append(first.getId());
                    sbInnerEncoded.append(URLEncoder.encode(first.getId(), "UTF-8"));
                    sbInnerPure.append("&limit=5");
                    sbInnerEncoded.append("&limit=5");
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("</ul>");

         sb.append("<h3><a name=\"getCapabilities\">getCapabilities</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  IDMapperCapabilities getCapabilities()</li>");
            sb.append("<li>Gives the Capabilitles as defined by BridgeDB.</li>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sb.append("getCapabilities\">getCapabilities</a></li>");    
            sb.append("</ul>");

         if (byXrefPosition != null){ 
            sb.append("<h3><a name=\"getXrefByposition\">getXrefByposition</h3>");
                sb.append("<ul>");
                sb.append("<li>Returns the Xref(s) currently at this position.</li>");
                sb.append("<li><b>WARNING:</b> There is no guarantee that the same position will return the same Xref over time.</li>");
                    sb.append("<ul>");
                    sb.append("<li>This method can <b>NOT</b> be used as an Identifier.</li>");
                    sb.append("</ul>");
                sb.append("<li>Used to Implement:  org.bridgedb.XrefIterator</li>");
                sb.append("<li>Required arguements:</li>");
                    sb.append("<ul>");
                    sb.append("<li>position as Integer</li>");
                    sb.append("</ul>");
                sb.append("<li>Optional arguments</li>");
                    sb.append("<ul>");
                    sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
                    sb.append("<li>limit as an Integer </li>");
                        sb.append("<ul>");
                        sb.append("<li>Will return this number of Xrefs starting at position. (assuming that many remain) </li>");
                        sb.append("</ul>");
                    sb.append("</ul>");
                sb.append("<li>Example: <a href=\"");
                        sb.append(uriInfo.getBaseUri());
                        sb.append("getXrefByposition?position=2");
                        sb.append("\">");
                        sb.append("getXrefByposition?position=2");
                        sb.append("</a></li>");    
                sb.append("<li>Example: <a href=\"");
                        sb.append(uriInfo.getBaseUri());
                        sb.append("getXrefByposition?position=0&limit=5&code=");
                        sb.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                        sb.append("\">");
                        sb.append("getXrefByposition?position=0&limit=5&code=");
                        sb.append(first.getDataSource().getSystemCode());
                        sb.append("</a></li>");    
                sb.append("</ul>");
         }

         sb.append("<h2>Implementations of BridgeDB's IDMapperCapabilities methods</h2>");
         sb.append("<h3><a name=\"isFreeSearchSupported\">isFreeSearchSupported</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  boolean isFreeSearchSupported()</li>");
            if (idMapper.getCapabilities().isFreeSearchSupported()){
                sb.append("<li>Returns True as freeSearch and URLSearch are supported.</li>");
            } else {
                sb.append("<li>Returns False because underlying IDMappper does not support freeSearch or URLSearch.</li>");                
            }
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sb.append("isFreeSearchSupported\">isFreeSearchSupported</a></li>");    
            sb.append("</ul>");

         sb.append("<h3><a name=\"getSupportedSrcDataSources\">getSupportedSrcDataSources</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set@ltDataSource@gt  getSupportedSrcDataSources()</li>");
            sb.append("<li>Returns Supported Source (BridgeDB)DataSource(s).</li>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sb.append("getSupportedSrcDataSources\">getSupportedSrcDataSources</a></li>");    
            sb.append("</ul>");
          
         sb.append("<h3><a name=\"getSupportedTgtDataSources\">getSupportedTgtDataSources</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set@ltDataSource@gt  getSupportedTgtDataSources()</li>");
            sb.append("<li>Returns Supported Target (BridgeDB)DataSource(s).</li>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sb.append("getSupportedTgtDataSources\">getSupportedTgtDataSources</a></li>");    
            sb.append("</ul>");
            
         sb.append("<h3><a name=\"isMappingSupported\">isMappingSupported</h3>");
            sb.append("<ul>");
            sb.append("<li>States if two DataSources are mapped at least once.</li>");
            sb.append("<li>Implements:  boolean isMappingSupported(DataSource src, DataSource tgt)</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>source as string (Where source is the SystemCode of the Source DataSource)</li>");
                sb.append("<li>target as string (Where target is the SystemCode of the Target DataSource)</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(uriInfo.getBaseUri());
                    sb.append("isMappingSupported?source=");
                    sb.append(first.getDataSource().getSystemCode());
                    sb.append("&target=");
                    sb.append(firstMaps.iterator().next().getDataSource().getSystemCode());
                    sb.append("\">");
                    sb.append("isMappingSupported?source=");
                    sb.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("&target=");
                    sb.append(URLEncoder.encode(firstMaps.iterator().next().getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("</a></li>");    
            sb.append("</ul>");

         sb.append("<h3><a name=\"getProperty\">getProperty</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  String getProperty(String key)</li>");
            sb.append("<li>Returns The property value for this key.</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>key as String </li>");
                sb.append("</ul>");
            if (keys.isEmpty()){
                sb.append("<li>There are currently no properties supported</li>");
            } else {
                sb.append("<li>Example: <a href=\"");
                        sb.append(uriInfo.getBaseUri());
                        sb.append("getProperty?key=");
                        sb.append(keys.iterator().next());
                        sb.append("\">");
                        sb.append("getProperty?key=");
                        sb.append(URLEncoder.encode(keys.iterator().next(), "UTF-8"));
                        sb.append("</a></li>");    
            }
            sb.append("</ul>");
            
         sb.append("<h3><a name=\"getKeys\">getKeys</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set<String> getKeys()</li>");
            sb.append("<li>Returns The keys and their property value.</li>");
            if (keys.isEmpty()){
                sb.append("<li>There are currently no properties supported</li>");
            } else {
                sb.append("<li>Example: <a href=\"");
                        sb.append(uriInfo.getBaseUri());
                        sb.append("getKeys");
                        sb.append("\">");
                        sb.append("getKeys");
                        sb.append("</a></li>");    
            }
            sb.append("</ul>");

         if (byXrefPosition != null){ 
            sb.append("<h3><a name=\"getXrefByposition\">getXrefByposition</h3>");
                sb.append("<ul>");
                sb.append("<li>Returns the Xref(s) currently at this position.</li>");
                sb.append("<li><b>WARNING:</b> There is no guarantee that the same position will return the same Xref over time.</li>");
                    sb.append("<ul>");
                    sb.append("<li>This method can <b>NOT</b> be used as an Identifier.</li>");
                    sb.append("</ul>");
                sb.append("<li>Used to Implement:  org.bridgedb.XrefIterator</li>");
                sb.append("<li>Required arguements:</li>");
                    sb.append("<ul>");
                    sb.append("<li>position as Integer</li>");
                    sb.append("</ul>");
                sb.append("<li>Optional arguments</li>");
                    sb.append("<ul>");
                    sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
                    sb.append("<li>limit as an Integer </li>");
                        sb.append("<ul>");
                        sb.append("<li>Will return this number of Xrefs starting at position. (assuming that many remain) </li>");
                        sb.append("</ul>");
                    sb.append("</ul>");
                sb.append("<li>Example: <a href=\"");
                        sb.append(uriInfo.getBaseUri());
                        sb.append("getXrefByposition?position=2");
                        sb.append("\">");
                        sb.append("getXrefByposition?position=2");
                        sb.append("</a></li>");    
                sb.append("<li>Example: <a href=\"");
                        sb.append(uriInfo.getBaseUri());
                        sb.append("getXrefByposition?position=0&limit=2&code=");
                        sb.append(first.getDataSource().getSystemCode());
                        sb.append("\">");
                        sb.append("getXrefByposition?position=0&limit=2&code=");
                        sb.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                        sb.append("</a></li>");    
                sb.append("</ul>");
         }
         
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    

}


