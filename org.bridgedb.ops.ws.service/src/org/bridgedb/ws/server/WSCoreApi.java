// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.ws.server;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.rdf.RdfFactory;

/**
 *
 * @author Christian
 */
public class WSCoreApi {
        
    public WSCoreApi() {      
    }
            
    protected void describeParameter(StringBuilder sb){
        sb.append("<h2>Parameters </h2>");
        sb.append("The following parametes may be applicable to the methods. ");
        sb.append("See the indiviual method description for which are required and which are optional.");
        sb.append("Their behaviour is consitant across all methods.");
        
        sb.append("<h3>BridgeDB Parameters</h3>");
        sb.append("<ul>");
        sb.append("<dt><a name=\"id_code\">id and code</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with this/these Xrefs.</li>");
            sb.append("<li>code is the SystemCode of the Xref's DataSource)</li>");
            sb.append("<li>id of the Xref</li>");
            sb.append("<li>Typically There can be multiple \"id\" and \"code\" values</li>");
                sb.append("<ul>");
                sb.append("<li>There must be at least one of each.</li>");                
                sb.append("<li>There must be the same number of each.</li>");                
                sb.append("<li>They will be paired by order.</li>");                
                sb.append("<li>If multiple Xref's have the same DataSource their code must be repeated.</li>");                
                sb.append("</ul>");
            sb.append("</ul>");           
            sb.append("<li>Note: Other methods may obtain a different id by following the method name with a slash // ");           
                sb.append("and the their id. These do not require a \"code\"</li>");
        sb.append("<dt><a name=\"key\">key</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Selects which property to return.</li>");
            sb.append("<li>Only one key parameter is supported.</li>");
            sb.append("</ul>");
        sb.append("<dt><a name=\"limit\">limit</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the number of results.</li>");
            sb.append("<li>Must be a positive Integer in String Format</li>");
            sb.append("<li>If less than limit results are availabe limit will have no effect.</li>");
            sb.append("<li>Only one limit parameter is supported.</li>");
            sb.append("<li>If no limit is set a default limit will be used.</li>");
            sb.append("<li>If too high a limit is set the default limit will be used.</li>");
            sb.append("<li>To obtain a full data dump please contact the admins.</li>");
            sb.append("</ul>");
        sb.append("<dt><a name=\"sourceCode\">sourceCode</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with those Source Xref's DataSource has this sysCode.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Typically there must be exactly one sourceSysCode when used..</li>");
            sb.append("</ul>");
        sb.append("<dt><a name=\"targetCode\">targetCode</a></dt>");
            sb.append("<ul>");
            sb.append("<li>Limits the results to ones with those Target Xref's DataSource has this sysCode.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Typically there can but need not be more than one.</li>");
            sb.append("</ul>");
         sb.append("<dt><a name=\"text\">text</a></dt>");
            sb.append("<ul>");
            sb.append("<li>A bit of text that will be searched for.</li>");
            sb.append("<li>String Format</li>");
            sb.append("<li>Only one text parameter is supported.</li>");
            sb.append("<li>Note this is for searching for text in Identifiers not for mapping between text and Identifiers.</li>");
            sb.append("</ul>");      
        sb.append("</ul>");
    }

   protected final void introduce_IDMapper(StringBuilder sb, boolean freeSearchSupported) {
        sb.append("<dt><a href=\"#mapID\">mapID</a></dt>");
        sb.append("<dd>List the Xrefs that map to these Xrefs</dd>");
        sb.append("<dt><a href=\"#xrefExists\">xrefExists</a></dt>");
        sb.append("<dd>State if the Xref is know to the Mapping Service or not</dd>");   
        if (freeSearchSupported){
            sb.append("<dt><a href=\"#freeSearch\">freeSearch</a></dt>");
            sb.append("<dd>Searches for Xrefs that have this id.</dd>");
        } else {
            sb.append("<dt>freeSearch</dt>");
            sb.append("<dd>This is currently not supported.</dd>");      
        }
        sb.append("<dt><a href=\"#getCapabilities\">getCapabilities</a></dt>");
        sb.append("<dd>Gives the Capabilitles as defined by BridgeDB.</dd>");
        sb.append("<dt>Close()</a></dt>");
        sb.append("<dd>Not supported as clients should not be able to close the server.</dd>");
        sb.append("<dt>isConnected</dt>");
        sb.append("<dd>Not supported as Close() is not allowed</dd>");
    }

    protected void describe_IDMapper(StringBuilder sb, Xref first, Set<Xref> firstMaps, Xref second,
            boolean freeSearchSupported) throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h2>Implementations of BridgeDB's IDMapper methods</h2>");

        describe_mapID(sb, first, firstMaps, second);    
        describe_xrefExists(sb, first);
        if (freeSearchSupported){
            describe_freeSearch(sb, first);
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
    
    private void describe_mapID(StringBuilder sb, Xref first, Set<Xref> firstMaps, Xref second) 
            throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h3><a name=\"mapID\">mapID</h3>");
            sb.append("<ul>");
            sb.append("<li>List the Xrefs that map to these Xrefs</li>");
            sb.append("<li>Implements:  Map&ltXref, Set&ltXref&gt&gt mapID(Collection&ltXref&gt srcXrefs, DataSource... tgtDataSources)</li>");
            sb.append("<li>Implements:  Set&ltXref&gt mapID(Xref srcXrefs, DataSource... tgtDataSources)</li>");
            sb.append("<li>Required arguements: (Only Source Xref considered)</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#id_code\">id</a></li>");
                sb.append("<li><a href=\"#id_code\">code</a></li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#targetCode\">targetCode</a></li> ");
                sb.append("</ul>");        
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    StringBuilder sbInnerPure = new StringBuilder("mapID?id=");
                    StringBuilder sbInnerEncoded = new StringBuilder("mapID?id=");
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
                sb.append(RdfConfig.getTheBaseURI());
                    for (Xref map:firstMaps){
                        sbInnerPure.append("&targetCode=");
                        sbInnerEncoded.append("&targetCode=");
                        sbInnerPure.append(map.getDataSource().getSystemCode());
                        sbInnerEncoded.append(URLEncoder.encode(map.getDataSource().getSystemCode(), "UTF-8"));
                    }
                    sb.append(sbInnerEncoded.toString());
                    sb.append("\">");
                    sb.append(sbInnerPure.toString());
                    sb.append("</a></li>");                    
            sb.append("</ul>");
    }
    
    private void describe_xrefExists(StringBuilder sb, Xref first) throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h3><a name=\"xrefExists\">xrefExists</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  boolean xrefExists(Xref xref)</li>");
            sb.append("<li>State if the Xref is know to the Mapping Service or not</li>");
            sb.append("<li>Required arguements: (Considers both Source and target Xrefs</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#id_code\">id</a></li>");
                sb.append("<li><a href=\"#id_code\">code</a></li>");
                sb.append("<li>Currently on a single id and single code supported.</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("xrefExists?id=");
                    sb.append(URLEncoder.encode(first.getId(), "UTF-8"));
                    sb.append("&code=");
                    sb.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("\">");
                    sb.append("xrefExists?id=");
                    sb.append(first.getId());
                    sb.append("&code=");
                    sb.append(first.getDataSource().getSystemCode());
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_freeSearch(StringBuilder sb, Xref first) throws UnsupportedEncodingException, IDMapperException{
         sb.append("<h3><a name=\"freeSearch\">freeSearch</h3>");
            sb.append("<ul>");
            sb.append("<li>Searches for Xrefs that have this id.</li>");
            sb.append("<li>Implements:  Set@ltXref@gt freeSearch (String text, int limit)</li>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#text\">text</a></li>");
                sb.append("<li><a href=\"#limit\">limit</a> (default available)</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("freeSearch?text=");
                    sb.append(URLEncoder.encode(first.getId(), "UTF-8"));
                    sb.append("&limit=5");
                    sb.append("\">");
                    sb.append("freeSearch?text=");
                    sb.append(first.getId());
                    sb.append("&limit=5");
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }
    
  protected final void introduce_IDMapperCapabilities(StringBuilder sb, Set<String> keys, boolean freeSearchSupported) {
        sb.append("<dt><a href=\"#isFreeSearchSupported\">isFreeSearchSupported</a></dt>");
        if (freeSearchSupported){
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
        sb.append("<dt><a href=\"#property\">property/key</a></dt>");
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
    }
  
    private void describe_getCapabilities(StringBuilder sb) throws IDMapperException {
         sb.append("<h3><a name=\"getCapabilities\">getCapabilities</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  IDMapperCapabilities getCapabilities()</li>");
            sb.append("<li>Gives the Capabilitles as defined by BridgeDB.</li>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("getCapabilities\">getCapabilities</a></li>");    
            sb.append("</ul>");
    }
    
    protected void describe_IDMapperCapabilities(StringBuilder sb, Xref first, Set<Xref> firstMaps, Set<String> keys, 
            boolean freeSearchSupported) throws UnsupportedEncodingException, IDMapperException{
        sb.append("<h2>Implementations of BridgeDB's IDMapperCapabilities methods</h2>");
        describe_isFreeSearchSupported(sb, freeSearchSupported);
        describe_getSupportedDataSources(sb);
        describe_isMappingSupported(sb, first, firstMaps); 
        describe_getProperty(sb, keys);            
        describe_getKeys(sb, keys);
    }
    
    private void describe_isFreeSearchSupported(StringBuilder sb, boolean freeSearchSupported) throws IDMapperException {
         sb.append("<h3><a name=\"isFreeSearchSupported\">isFreeSearchSupported</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  boolean isFreeSearchSupported()</li>");
            if (freeSearchSupported){
                sb.append("<li>Returns True as freeSearch and URLSearch are supported.</li>");
            } else {
                sb.append("<li>Returns False because underlying IDMappper does not support freeSearch or URLSearch.</li>");                
            }
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("isFreeSearchSupported\">isFreeSearchSupported</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_getSupportedDataSources(StringBuilder sb) throws IDMapperException {
         sb.append("<h3><a name=\"getSupportedSrcDataSources\">getSupportedSrcDataSources</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set&ltDataSource&gt  getSupportedSrcDataSources()</li>");
            sb.append("<li>Returns Supported Source (BridgeDB)DataSource(s).</li>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("getSupportedSrcDataSources\">getSupportedSrcDataSources</a></li>");    
            sb.append("</ul>");
          
         sb.append("<h3><a name=\"getSupportedTgtDataSources\">getSupportedTgtDataSources</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set&ltDataSource&gt  getSupportedTgtDataSources()</li>");
            sb.append("<li>Returns Supported Target (BridgeDB)DataSource(s).</li>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("getSupportedTgtDataSources\">getSupportedTgtDataSources</a></li>");    
            sb.append("</ul>");
    }
    
    private void describe_isMappingSupported(StringBuilder sb, Xref first, Set<Xref> firstMaps) 
            throws UnsupportedEncodingException, IDMapperException{
         sb.append("<h3><a name=\"isMappingSupported\">isMappingSupported</h3>");
            sb.append("<ul>");
            sb.append("<li>States if two DataSources are mapped at least once.</li>");
            sb.append("<li>Implements:  boolean isMappingSupported(DataSource src, DataSource tgt)</li>");
            sb.append("<li>Required arguements: (One of each)</li>");
                sb.append("<ul>");
                sb.append("<li><a href=\"#sourceCode\">sourceCode</a></li> ");
                sb.append("<li><a href=\"#targetCode\">targetCode</a></li> ");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(RdfConfig.getTheBaseURI());
                    sb.append("isMappingSupported?sourceCode=");
                    sb.append(first.getDataSource().getSystemCode());
                    sb.append("&targetCode=");
                    sb.append(firstMaps.iterator().next().getDataSource().getSystemCode());
                    sb.append("\">");
                    sb.append("isMappingSupported?sourceCode=");
                    sb.append(URLEncoder.encode(first.getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("&targetCode=");
                    sb.append(URLEncoder.encode(firstMaps.iterator().next().getDataSource().getSystemCode(), "UTF-8"));
                    sb.append("</a></li>");    
            sb.append("</ul>");
    }

    private void describe_getProperty(StringBuilder sb, Set<String> keys) 
            throws UnsupportedEncodingException, IDMapperException{
         sb.append("<h3><a name=\"property\">property/key</h3>");
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
                        sb.append("property/");
                        sb.append(keys.iterator().next());
                        sb.append("\">");
                        sb.append("property/");
                        sb.append(URLEncoder.encode(keys.iterator().next(), "UTF-8"));
                        sb.append("</a></li>");    
            }
            sb.append("</ul>");
    }
    
    private void describe_getKeys(StringBuilder sb, Set<String> keys) throws IDMapperException{
         sb.append("<h3><a name=\"getKeys\">getKeys</h3>");
            sb.append("<ul>");
            sb.append("<li>Implements:  Set<String> getKeys()</li>");
            sb.append("<li>Returns The keys and their property value.</li>");
            if (keys.isEmpty()){
                sb.append("<li>There are currently no properties supported</li>");
            } else {
                sb.append("<li>Example: <a href=\"");
                    sb.append(RdfConfig.getTheBaseURI());
                        sb.append("getKeys");
                        sb.append("\">");
                        sb.append("getKeys");
                        sb.append("</a></li>");    
            }
            sb.append("</ul>");
    }
    
}


