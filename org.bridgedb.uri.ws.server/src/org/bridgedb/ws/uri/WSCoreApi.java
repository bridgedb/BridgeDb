// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.ws.uri;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import org.bridgedb.Xref;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WsConstants;

/**
 *
 * @author Christian
 */
public class WSCoreApi {
        
    public WSCoreApi() {      
    }
            
    static final String ID_CODE = "id_code";
    static final String FIRST_ID_PARAMETER = "?" + WsConstants.ID + "=";
    static final String ID_PARAMETER = "&" + WsConstants.ID + "=";
    static final String DATASOURCE_SYSTEM_CODE_PARAMETER = "&" + WsConstants.DATASOURCE_SYSTEM_CODE + "=";
    final static String FIRST_SOURCE_PARAMETER = "?" + WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE + "=";
    final static String TARGET_PARAMETER = "&" + WsConstants.TARGET_DATASOURCE_SYSTEM_CODE + "=";
    final static String FIRST_TEXT_PARAMETER = "?" + WsConstants.TEXT + "=";
    final static String LIMIT5_PARAMETER = "&" + WsConstants.LIMIT + "=5";

    protected void describeParameter(StringBuilder sb){
        sb.append("<h2>Parameters </h2>");
        sb.append("The following parametes may be applicable to the methods. ");
        sb.append("See the indiviual method description for which are required and which are optional.");
        sb.append("Their behaviour is consitant across all methods.");
        
        sb.append("<h3>BridgeDB Parameters</h3>");
        sb.append("<ul>");
        sb.append("<dt><a name=\"id_code\">");
                sb.append(WsConstants.ID);
                sb.append(" and ");
                sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with this/these Xrefs.</li>");
            sb.append("<li>");
                    sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                    sb.append(" is the SystemCode of the Xref's DataSource)</li>");
            sb.append("<li>");
                    sb.append(WsConstants.ID);
                    sb.append(" is the identifier part of the Xref</li>");
            sb.append("<li>Typically There can be multiple \"");
                    sb.append(WsConstants.ID);
                    sb.append("\" and \"");
                    sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                    sb.append("\" values</li>");
                sb.append("<ul>");
                sb.append("<li>There must be at least one of each.</li>");                
                sb.append("<li>There must be the same number of each.</li>");                
                sb.append("<li>They will be paired by order.</li>");                
                sb.append("<li>If multiple Xref's have the same DataSource their code must be repeated.</li>");                
                sb.append("</ul>");
            sb.append("</ul>");           
            sb.append("<li>Note: Other methods may obtain a different ");           
                    sb.append(WsConstants.ID);           
                    sb.append(" by following the method name with a slash // ");
                    sb.append(WsConstants.ID);
                    sb.append(". These do not require a \"");
                    sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                    sb.append("\"</li>");
        sb.append("<dt><a name=\"key\">key</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Selects which property to return.</li>");
            sb.append("<li>Only one key parameter is supported.</li>");
            sb.append("</ul>");
        sb.append("<dt><a name=\"");
                sb.append(WsConstants.LIMIT);
                sb.append("\">");
                sb.append(WsConstants.LIMIT);
                sb.append("</a></dt>");
                sb.append("<ul>");
            sb.append("<li>Limits the number of results.</li>");
            sb.append("<li>Must be a positive Integer in String Format</li>");
            sb.append("<li>If less than ");
                    sb.append(WsConstants.LIMIT);
                    sb.append("results are availabe ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" will have no effect.</li>");
            sb.append("<li>Only one ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" parameter is supported.</li>");
            sb.append("<li>If no ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" is set a default ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" will be used.</li>");
            sb.append("<li>If too high a ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" is set the default ");
                    sb.append(WsConstants.LIMIT);
                    sb.append(" will be used.</li>");
            sb.append("<li>To obtain a full data dump please contact the admins.</li>");
            sb.append("</ul>");
        sb.append("<dt><a name=\"");
                sb.append(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                sb.append("\">");
                sb.append(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with those Source Xref's DataSource has this sysCode.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Typically there must be exactly one ");
                    sb.append(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                    sb.append(" when used..</li>");
            sb.append("</ul>");
        sb.append("<dt><a name=\"");
                sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("\">");
                sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with those Target Xref's DataSource has this sysCode.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Typically there can but need not be more than one.</li>");
            sb.append("</ul>");
         sb.append("<dt><a name=\"");
                    sb.append(WsConstants.TEXT);
                    sb.append("\">");
                    sb.append(WsConstants.TEXT);
                    sb.append("</a></dt>");
            sb.append("<ul>");
            sb.append("<li>A bit of text that will be searched for.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Only one text parameter is supported.</li>");
            sb.append("<li>Note this is for searching for text in Identifiers not for mapping between text and Identifiers.</li>");
            sb.append("</ul>");      
        sb.append("</ul>");
    }

   protected final void introduce_IDMapper(StringBuilder sb, boolean freeSearchSupported) {
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.MAP_ID);
                sb.append("\">");
                sb.append(WsConstants.MAP_ID);
                sb.append("</a></dt>");
        sb.append("<dd>List the Xrefs that map to these Xrefs</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.XREF_EXISTS);
                sb.append("\">");
                sb.append(WsConstants.XREF_EXISTS);
                sb.append("</a></dt>");
        sb.append("<dd>State if the Xref is know to the Mapping Service or not</dd>");   
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.FREE_SEARCH);
                sb.append("\">");
                sb.append(WsConstants.FREE_SEARCH);
                sb.append("</a></dt>");
        if (freeSearchSupported){
            sb.append("<dd>Searches for Xrefs that have this id.</dd>");
        } else {
            sb.append("<dd>This is currently not supported.</dd>");      
        }
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.GET_CAPABILITIES);
                sb.append("\">");
                sb.append(WsConstants.GET_CAPABILITIES);
                sb.append("</a></dt>");
        sb.append("<dd>Gives the Capabilitles as defined by BridgeDB.</dd>");
        sb.append("<dt>Close()</a></dt>");
        sb.append("<dd>Not supported as clients should not be able to close the server.</dd>");
        sb.append("<dt>isConnected</dt>");
        sb.append("<dd>Not supported as Close() is not allowed</dd>");
    }

    protected void describe_IDMapper(StringBuilder sb, Xref sourceXref1, String tragetSysCode1, Xref sourceXref2,
            boolean freeSearchSupported) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h2>Implementations of BridgeDB's IDMapper methods</h2>");

        describe_mapID(sb, sourceXref1, tragetSysCode1, sourceXref2);    
        describe_xrefExists(sb, sourceXref1);
        if (freeSearchSupported){
            describe_freeSearch(sb, sourceXref1);
        }
        describe_getCapabilities(sb); 
        sb.append("<h3>Other IDMapper Functions</h3>");
        sb.append("<dl>");
        sb.append("<dt>Close()</a></dt>");
        sb.append("<dd>Not supported as clients should not be able to close the server.</dd>");
        sb.append("<dt>isConnected</dt>");
        sb.append("<dd>Not supported as Close() is not allowed</dd>");
        sb.append("</dl>");
    }
    
    private void describe_mapID(StringBuilder sb, Xref sourceXref1, String tragetSysCode1, Xref sourceXref2) 
            throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                    sb.append(WsConstants.MAP_ID);
                    sb.append("\">");
                    sb.append(WsConstants.MAP_ID);
                    sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>List the Xrefs that map to these Xrefs</li>");
            sb.append("<li>Implements:  Map&ltXref, Set&ltXref&gt&gt mapID(Collection&ltXref&gt srcXrefs, DataSource... tgtDataSources)</li>");
            sb.append("<li>Implements:  Set&ltXref&gt mapID(Xref srcXrefs, DataSource... tgtDataSources)</li>");
            sb.append("<li>Required arguements: (Only Source Xref considered)</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(ID_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.ID);
                        sb.append("</a></li>");
                sb.append("<li><a href=\"#");
                        sb.append(ID_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li> ");
                sb.append("</ul>");        
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    StringBuilder front = new StringBuilder(WsConstants.MAP_ID);
                    StringBuilder sbInnerPure = new StringBuilder(WsConstants.MAP_ID);
                    StringBuilder sbInnerEncoded = new StringBuilder(WsConstants.MAP_ID);
                    sbInnerPure.append(FIRST_ID_PARAMETER);
                    sbInnerEncoded.append(FIRST_ID_PARAMETER);
                    sbInnerPure.append(sourceXref1.getId());
                    sbInnerEncoded.append(sourceXref1.getId());
                    sbInnerPure.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sbInnerEncoded.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sbInnerPure.append(sourceXref1.getDataSource().getSystemCode());
                    sbInnerEncoded.append(URLEncoder.encode(sourceXref1.getDataSource().getSystemCode(), "UTF-8"));
                    sbInnerPure.append(ID_PARAMETER);
                    sbInnerEncoded.append(ID_PARAMETER);
                    sbInnerPure.append(sourceXref2.getId());
                    sbInnerEncoded.append(URLEncoder.encode(sourceXref2.getId(), "UTF-8"));
                    sbInnerPure.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sbInnerEncoded.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sbInnerPure.append(sourceXref2.getDataSource().getSystemCode());
                    sbInnerEncoded.append(URLEncoder.encode(sourceXref2.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");    
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    String targetPart = "&" + WsConstants.TARGET_DATASOURCE_SYSTEM_CODE + "=";
                    sbInnerPure.append(targetPart);
                    sbInnerEncoded.append(targetPart);
                    sbInnerPure.append(tragetSysCode1);
                    sbInnerEncoded.append(URLEncoder.encode(tragetSysCode1, "UTF-8"));
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");                    
            sb.append("</ul>");
    }
    
    private void describe_xrefExists(StringBuilder sb, Xref xref1) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h3><a name=\"");
                sb.append(WsConstants.XREF_EXISTS);
                sb.append("\">");
                sb.append(WsConstants.XREF_EXISTS);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  boolean xrefExists(Xref xref)</li>");
            sb.append("<li>State if the Xref is know to the Mapping Service or not</li>");
            sb.append("<li>Required arguements: (Considers both Source and target Xrefs</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(ID_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.ID);
                        sb.append("</a></li>");
                sb.append("<li><a href=\"#");
                        sb.append(ID_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li>");
                sb.append("<li>Currently only a single ");
                        sb.append(WsConstants.ID);
                        sb.append(" and single ");
                        sb.append(WsConstants.DATASOURCE_SYSTEM_CODE);
                        sb.append(" supported.</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsConstants.XREF_EXISTS);
                    sb.append(FIRST_ID_PARAMETER);
                    sb.append(URLEncoder.encode(xref1.getId(), "UTF-8"));
                    sb.append(DATASOURCE_SYSTEM_CODE_PARAMETER);
                    sb.append(URLEncoder.encode(xref1.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("\">");
                    sb.append(WsConstants.XREF_EXISTS);
                    sb.append(FIRST_ID_PARAMETER);
                    sb.append(xref1.getId());
                    sb.append(FIRST_ID_PARAMETER);
                    sb.append(xref1.getDataSource().getSystemCode());
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_freeSearch(StringBuilder sb, Xref xref1) throws UnsupportedEncodingException, BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.FREE_SEARCH);
                sb.append("\">");
                sb.append(WsConstants.FREE_SEARCH);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for Xrefs that have this id.</li>");
            sb.append("<li>Implements:  Set@ltXref@gt freeSearch (String text, int limit)</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsConstants.TEXT);
                        sb.append("\">");
                        sb.append(WsConstants.TEXT);
                        sb.append("</a></li>");
                sb.append("<li><a href=\"#");
                        sb.append(WsConstants.LIMIT);
                        sb.append("\">");
                        sb.append(WsConstants.LIMIT);
                        sb.append("</a> (default available)</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsConstants.FREE_SEARCH);
                    sb.append(FIRST_TEXT_PARAMETER);
                    sb.append(URLEncoder.encode(xref1.getId(), "UTF-8"));
                    sb.append(LIMIT5_PARAMETER);
                    sb.append("\">");
                    sb.append(WsConstants.FREE_SEARCH);
                    sb.append(FIRST_TEXT_PARAMETER);
                    sb.append(xref1.getId());
                    sb.append(LIMIT5_PARAMETER);
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
  protected final void introduce_IDMapperCapabilities(StringBuilder sb, Set<String> keys, boolean freeSearchSupported) {
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                sb.append("\">");
                sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                sb.append("</a></dt>");
        if (freeSearchSupported){
            sb.append("<dd>Returns True as freeSearch and URLSearch are supported.</dd>");
        } else {
            sb.append("<dd>Returns False because underlying IDMappper does not support freeSearch or URLSearch.</dd>");                
        }        
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                sb.append("\">");
                sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                sb.append("</a></dt>");
        sb.append("<dd>Returns Supported Source (BridgeDB)DataSource(s).</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                sb.append("\">");
                sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                sb.append("</a></dt>");
        sb.append("<dd>Returns Supported Target (BridgeDB)DataSource(s).</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                sb.append("\">");
                sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                sb.append("</a></dt>");
        sb.append("<dd>States if two DataSources are mapped at least once.</dd>");
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.PROPERTY);
                sb.append("\">");
                sb.append(WsConstants.PROPERTY);
                sb.append("/key</a></dt>");
        if (keys.isEmpty()){
            sb.append("<dd>There are currently no properties supported.</dd>");
        } else {
            sb.append("<dd>Returns The property value for this key.</dd>");
        }
        sb.append("<dt><a href=\"#");
                sb.append(WsConstants.GET_KEYS);
                sb.append("\">");
                sb.append(WsConstants.GET_KEYS);
                sb.append("</a></dt>");
        if (keys.isEmpty()){
            sb.append("<dd>There are currently no properties supported.</dd>");
        } else {
            sb.append("<dd>Returns The keys and their property value.</dd>");
        }
    }
  
    private void describe_getCapabilities(StringBuilder sb) throws BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.GET_CAPABILITIES);
                sb.append("\">");
                sb.append(WsConstants.GET_CAPABILITIES);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  IDMapperCapabilities getCapabilities()</li>");
            sb.append("<li>Gives the Capabilitles as defined by BridgeDB.</li>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsConstants.GET_CAPABILITIES);
                    sb.append("\">");
                    sb.append(WsConstants.GET_CAPABILITIES);
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    protected void describe_IDMapperCapabilities(StringBuilder sb, Xref xref1, String tragetSysCode1, Set<String> keys, 
            boolean freeSearchSupported) throws UnsupportedEncodingException, BridgeDBException{
        sb.append("<h2>Implementations of BridgeDB's IDMapperCapabilities methods</h2>");
        describe_isFreeSearchSupported(sb, freeSearchSupported);
        describe_getSupportedDataSources(sb);
        describe_isMappingSupported(sb, xref1, tragetSysCode1); 
        describe_getProperty(sb, keys);            
        describe_getKeys(sb, keys);
    }
    
    private void describe_isFreeSearchSupported(StringBuilder sb, boolean freeSearchSupported) throws BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                sb.append("\">");
                sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  boolean isFreeSearchSupported()</li>");
            if (freeSearchSupported){
                sb.append("<li>Returns True as freeSearch and URISearch are supported.</li>");
            } else {
                sb.append("<li>Returns False because underlying IDMappper does not support freeSearch or URISearch.</li>");                
            }
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                    sb.append("\">");
                    sb.append(WsConstants.IS_FREE_SEARCH_SUPPORTED);
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_getSupportedDataSources(StringBuilder sb) throws BridgeDBException {
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                sb.append("\">");
                sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set&ltDataSource&gt  getSupportedSrcDataSources()</li>");
            sb.append("<li>Returns Supported Source (BridgeDB)DataSource(s).</li>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                    sb.append("\">");
                    sb.append(WsConstants.GET_SUPPORTED_SOURCE_DATA_SOURCES);
                    sb.append("</a></li>");    
            sb.append("</ul>");
          
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                sb.append("\">");
                sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set&ltDataSource&gt  getSupportedTgtDataSources()</li>");
            sb.append("<li>Returns Supported Target (BridgeDB)DataSource(s).</li>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                    sb.append("\">");
                    sb.append(WsConstants.GET_SUPPORTED_TARGET_DATA_SOURCES);
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_isMappingSupported(StringBuilder sb, Xref sourceXref1, String targetSysCode) 
            throws UnsupportedEncodingException, BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                sb.append("\">");
                sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>States if two DataSources are mapped at least once.</li>");
            sb.append("<li>Implements:  boolean isMappingSupported(DataSource src, DataSource tgt)</li>");
            sb.append("<li>Required arguements: (One of each)</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#");
                        sb.append(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.SOURCE_DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li> ");
                sb.append("<li><a href=\"#");
                        sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("\">");
                        sb.append(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE);
                        sb.append("</a></li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                    sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                    sb.append(FIRST_SOURCE_PARAMETER);
                    sb.append(sourceXref1.getDataSource().getSystemCode());
                    sb.append(TARGET_PARAMETER);
                    sb.append(targetSysCode);
                    sb.append("\">");
                    sb.append(WsConstants.IS_MAPPING_SUPPORTED);
                    sb.append(FIRST_SOURCE_PARAMETER);
                    sb.append(URLEncoder.encode(sourceXref1.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append(TARGET_PARAMETER);
                    sb.append(URLEncoder.encode(targetSysCode, "UTF-8"));
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }

    private void describe_getProperty(StringBuilder sb, Set<String> keys) 
            throws UnsupportedEncodingException, BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.PROPERTY);
                sb.append("\">");
                sb.append(WsConstants.PROPERTY);
                sb.append("/key</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  String getProperty(String key)</li>");
            sb.append("<li>Returns The property value for this key.</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>Place the actual key after the /</li> ");
                sb.append("</ul>");
            if (keys.isEmpty()){
                sb.append("<li>There are currently no properties supported</li>");
            } else {
                sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                        sb.append(WsConstants.PROPERTY);
                        sb.append("/");
                        sb.append(keys.iterator().next());
                        sb.append("\">");
                        sb.append(WsConstants.PROPERTY);
                        sb.append("/");
                        sb.append(URLEncoder.encode(keys.iterator().next(), "UTF-8"));
                        sb.append("</a></li>");    
            }
            sb.append("</ul>");
    }
    
    private void describe_getKeys(StringBuilder sb, Set<String> keys) throws BridgeDBException{
         sb.append("<h3><a name=\"");
                sb.append(WsConstants.GET_KEYS);
                sb.append("\">");
                sb.append(WsConstants.GET_KEYS);
                sb.append("</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set<String> getKeys()</li>");
            sb.append("<li>Returns The keys and their property value.</li>");
            if (keys.isEmpty()){
                sb.append("<li>There are currently no properties supported</li>");
            } else {
                sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                        sb.append(WsConstants.GET_KEYS);
                        sb.append("\">");
                        sb.append(WsConstants.GET_KEYS);
                        sb.append("</a></li>");    
            }
            sb.append("</ul>");
    }
    
}


