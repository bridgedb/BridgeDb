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
package org.bridgedb.uri.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.SourceInfo;
import org.bridgedb.statistics.SourceTargetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.MappingsBySysCodeId;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.ws.bean.*;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.ws.WSCoreMapper;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public class WSUriMapper extends WSCoreMapper implements UriMapper{
    
    WSUriInterface uriService;
    private static final String NO_ID = null;
    private static final String NO_SYSCODE = null;
    private static final String NO_URI = null;
    private static final String NULL_GRAPH = null;
    private static final ArrayList<String> NO_SYSCODES = null;
    private static final ArrayList<String> NO_URI_PATTERNS = null;
    
    private static final Boolean INCLUDE_XREF_RESULTS = true;
    private static final Boolean EXCLUDE_XREF_RESULTS = false;
    private static final Boolean INCLUDE_URI_RESULTS = true;
    private static final Boolean EXCLUDE_URI_RESULTS = false;
   
    public WSUriMapper(WSUriInterface uriService){
        super(uriService);
        this.uriService = uriService;
    }

    @Override
    public Set<Xref> mapID(Xref sourceXref, String lensUri, Collection<DataSource> tgtDataSources) throws BridgeDBException {
        Set<Mapping> beans = mapFull(sourceXref, lensUri, INCLUDE_XREF_RESULTS, tgtDataSources);
        return extractXref(beans);
    }
    
    private Set<Xref> extractXref(Collection<Mapping> beans){
        HashSet<Xref> results = new HashSet<Xref>();
        for (Mapping bean:beans){
           Xref targetXref = bean.getTarget();
           results.add(targetXref);
        }
        return results;        
    }
    
    @Override
    public Set<String> mapUri(String sourceUri, String lensUri, String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        List<String> uris = new ArrayList<String>();
        uris.add(sourceUri);
        List<String> targetUriPatterns = toList(tgtUriPatterns);
        Response response = uriService.mapUri(uris, lensUri, graph, targetUriPatterns);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            return new HashSet<String> ();
        } else {
            UriMappings beans = (UriMappings)response.getEntity();
            return beans.getTargetUri();
        }
    }

    private Set<String> extractUris(Collection<Mapping> beans){
        HashSet<String> results = new HashSet<String>();
        for (Mapping bean:beans){
            results.addAll(bean.getTargetUri());
        }
        return results;          
    }
    
    @Override
    public Set<String> mapUri(Xref sourceXref, String lensUri, String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        Set<Mapping> beans = mapFull(sourceXref, lensUri, graph, tgtUriPatterns);
        return extractUris(beans);
    }

    @Override
    public MappingsBySet mapBySet(Collection<String> sourceUris, String lensUri, String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        Set<Mapping> mappings = new HashSet<Mapping>();
        for (String uri:sourceUris){
            mappings.addAll(this.mapFull(uri, lensUri, EXCLUDE_XREF_RESULTS, graph, tgtUriPatterns));
        }
        return new MappingsBySet(lensUri, mappings);
    }

    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String lensUri, Boolean includeUriResults, Collection<DataSource> tgtDataSources) throws BridgeDBException {
        if (sourceXref == null){
            return new HashSet<Mapping>();
        }
        List<String> tgtSysCodes = this.toSysCodeList(tgtDataSources);
        return mapFull(sourceXref.getId(), sourceXref.getDataSource().getSystemCode(), 
                NO_URI, lensUri, 
                INCLUDE_XREF_RESULTS, includeUriResults,
                tgtSysCodes, NULL_GRAPH, NO_URI_PATTERNS);
    }
 
    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String lensUri, String graph, 
            Collection<String> tgtUriPatterns) throws BridgeDBException {
        if (sourceXref == null){
            return new HashSet<Mapping>();
        }
        List<String> tgtUriPatternStrings = toList(tgtUriPatterns);
        return mapFull(sourceXref.getId(), sourceXref.getDataSource().getSystemCode(), 
                NO_URI, lensUri, 
                INCLUDE_XREF_RESULTS, INCLUDE_URI_RESULTS,
                NO_SYSCODES, graph, tgtUriPatternStrings);
    }
     
    private Set<Mapping> mapFull(String id, String scrCode, String uri, String lensUri, 
            Boolean includeXrefResults, Boolean includeUriResults,
            List<String> targetCodes, String graph, List<String> targetUriPattern) throws BridgeDBException{ 
        System.out.println("includeUriResults=" + includeUriResults);
        Response response = uriService.map(id, scrCode, uri, lensUri, 
                includeXrefResults, includeUriResults, 
                targetCodes, graph, targetUriPattern);
        //In the server it is a MappingsBean
        //if (response.getEntity() instanceof MappingsBean){
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            return new HashSet<Mapping> ();
        } else {
            MappingsBean bean = (MappingsBean)response.getEntity();
            return bean.asMappings();
        }
        //} else {
        //    //But the client builds an ArrayList of mappings
        //    List<MappingBean> beans = (List<MappingBean>)response.getEntity();
        //    HashSet<Mapping> mappings = new HashSet<Mapping> ();
        //    for (MappingBean bean:beans){
        //        mappings.add(MappingBean.asMapping(bean));
        //    }
        //    return mappings;
        //}
    }
    
    @Override
    public Set<Mapping> mapFull(String sourceUri, String lensUri, Collection<DataSource> tgtDataSources) throws BridgeDBException {
        if (sourceUri == null){
            return new HashSet<Mapping>();
        }
        List<String> tgtSysCodes = toSysCodeList(tgtDataSources);
        return mapFull(NO_ID, NO_SYSCODE, sourceUri, lensUri, 
                INCLUDE_XREF_RESULTS, INCLUDE_URI_RESULTS,
                tgtSysCodes, NULL_GRAPH, NO_URI_PATTERNS);
    }

    @Override
    public Set<Mapping> mapFull(String sourceUri, String lensUri, Boolean includeXrefResults, 
            String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        if (sourceUri == null){
            return new HashSet<Mapping>();
        }
        List<String> tgtUriPatternStrings = toList(tgtUriPatterns);
        return mapFull(NO_ID, NO_SYSCODE, sourceUri, lensUri, 
                includeXrefResults, INCLUDE_URI_RESULTS,
                NO_SYSCODES, graph, tgtUriPatternStrings);
    }
    
    @Override
    public boolean uriExists(String Uri) throws BridgeDBException {
        Response response = uriService.UriExists(Uri);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            //Should never happen but just in case
            return false;
        }
        UriExistsBean bean = (UriExistsBean)response.getEntity();
        return bean.exists();
    }

    @Override
    public Set<String> uriSearch(String text, int limit) throws BridgeDBException {
        Response response = uriService.UriSearch(text, "" + limit);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            return new HashSet<String>();
        }
        UriSearchBean bean = (UriSearchBean)response.getEntity();
        return bean.getUriSet();
    }

    @Override
    public Xref toXref(String Uri) throws BridgeDBException {
        Response response = uriService.toXref(Uri);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            //Should never happen but just in case
            throw new BridgeDBException("Unable to convert " + Uri + " to an xref. Server returned no context");
        }
        XrefBean bean = (XrefBean)response.getEntity(); 
        if (bean == null){
            return null;
        }
        return bean.asXref();
    }
    
    public Set<String> toUris(Xref xref) throws BridgeDBException{
        Response response = uriService.toUris(xref.getId(), xref.getDataSource().getSystemCode());
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            return new HashSet<String> ();
        } else {
            UriMappings beans = (UriMappings)response.getEntity();
            return beans.getTargetUri();
        }       
    }
    
    //@Override Too slow
    //public List<Mapping> getSampleMapping() throws BridgeDBException {
    //    return uriService.getSampleMappings();
    //}
    
    @Override
    public OverallStatistics getOverallStatistics(String lensUri) throws BridgeDBException {
        Response response = uriService.getOverallStatistics(lensUri);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            //Should never happen but just in case
            throw new BridgeDBException("Unable to get OverallStatistics. Server returned no context");
        }
        OverallStatisticsBean bean = (OverallStatisticsBean)response.getEntity(); 
        return OverallStatisticsBean.asOverallStatistics(bean);
    }

    @Override
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws BridgeDBException {
        Response response = uriService.getMappingSetInfo("" + mappingSetId);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            throw new BridgeDBException("Unable to get MappingSetInfo for " + mappingSetId + " Server returned no context");
        }
        MappingSetInfoBean bean = (MappingSetInfoBean)response.getEntity(); 
        return bean.asMappingSetInfo();
    }

    @Override
    public List<MappingSetInfo> getMappingSetInfos(String sourceSysCode, String targetSysCode, String lensUri) throws BridgeDBException {
        Response response = uriService.getMappingSetInfos(sourceSysCode, targetSysCode, lensUri);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            throw new BridgeDBException("Unable to get MappingSetInfo for " + sourceSysCode + " -> " + targetSysCode + 
                    " lens: " + lensUri + " Server returned no context");
        }
        MappingSetInfosBean bean = (MappingSetInfosBean)response.getEntity(); 
        return bean.getMappingSetInfos();
    }
   
    @Override
    public List<SourceInfo> getSourceInfos(String lensUri) throws BridgeDBException {
        Response response = uriService.getSourceInfos(lensUri);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            throw new BridgeDBException(uriService.getClass() + " Unable to get Source for lens: " + lensUri + " Server returned " + response.getStatus());
        }
        SourceInfosBean bean = (SourceInfosBean)response.getEntity(); 
        return bean.getSourceInfos();
    }

    @Override
    public List<SourceTargetInfo> getSourceTargetInfos(String sourceSysCode, String lensUri) throws BridgeDBException {
        Response response = uriService.getSourceTargetInfos(sourceSysCode, lensUri);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            throw new BridgeDBException(uriService.getClass() + " Unable to get Source for lens: " + lensUri + " Server returned " + response.getStatus());
        }
        SourceTargetInfosBean bean = (SourceTargetInfosBean)response.getEntity(); 
        return bean.getSourceTargetInfos();
    }

    @Override
    public Set<String> getUriPatterns(String dataSource) throws BridgeDBException {
        Response response = uriService.getDataSource(dataSource);
        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            return new HashSet<String>();
        }
        DataSourceUriPatternBean bean = (DataSourceUriPatternBean)response.getEntity(); 
        if (bean == null) {
            return new HashSet<String>();
        } else {
            return bean.getUriPattern();
        }
    }
  
    @Override
    public int getSqlCompatVersion() throws BridgeDBException {
        Response response = uriService.getSqlCompatVersion();
       if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            throw new BridgeDBException("Unable to get SqlCompatVersion. Server returned no context");
        }
        String version = response.getEntity().toString();
        return Integer.parseInt(version);
    }

    @Override
    public List<Mapping> getSampleMapping() throws BridgeDBException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IdSysCodePair toIdSysCodePair(String uri) throws BridgeDBException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getJustifications() throws BridgeDBException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private List<String> toList(Collection<String> collectionOrNull){
        if (collectionOrNull == null){
            return new ArrayList<String>();
        } else {
            return new ArrayList<String>(collectionOrNull);            
        }
    }
 
    private List<String> toSysCodeList(Collection<DataSource> collectionOrNull){
        ArrayList<String> tgtSysCodes = new ArrayList<String>();
        if (collectionOrNull != null){
            for (DataSource tgtDataSource:collectionOrNull){
                if (tgtDataSource != null){
                    tgtSysCodes.add(tgtDataSource.getSystemCode());
                } else {
                    tgtSysCodes.add(null);
                }
            }
        }
        return tgtSysCodes;
    }

    @Override
    public MappingsBySysCodeId mapUriBySysCodeId(String sourceUri, String lensUri, String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MappingsBySysCodeId mapUriBySysCodeId(Collection<String> sourceUri, String lensUri, String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
