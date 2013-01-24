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
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.Mapping;
import org.bridgedb.url.URLMapper;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.MappingSetInfoBeanFactory;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.OverallStatisticsBeanFactory;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.UriSpaceBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefBeanFactory;

/**
 *
 * @author Christian
 */
public class WSUriMapper extends WSCoreMapper implements URLMapper{
    
    WSUriInterface uriService;
    
    public WSUriMapper(WSUriInterface uriService){
        super(uriService);
        this.uriService = uriService;
    }

    @Override
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, String... targetURISpaces) throws IDMapperException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>> ();
        for (String sourceURL:sourceURLs){
            Set<String> urls = mapURL(sourceURL, targetURISpaces);
            results.put(sourceURL, urls);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String sourceURL, String... targetURISpaces) throws IDMapperException {
        List<Mapping> beans = uriService.mapURL(sourceURL, Arrays.asList(targetURISpaces));
        HashSet<String> targetURLS = new HashSet<String>(); 
        for (Mapping bean:beans){
            targetURLS.addAll(bean.getTargetURL());
        }
        return targetURLS;
    }

    @Override
    public Set<String> mapToURLs(Xref xref, String... targetURISpaces) throws IDMapperException {
        List<Mapping> beans = uriService.mapToURLs(xref.getId(), xref.getDataSource().getSystemCode(), Arrays.asList(targetURISpaces));
        HashSet<String> targetURLS = new HashSet<String>(); 
        for (Mapping bean:beans){
            targetURLS.addAll(bean.getTargetURL());
        }
        return targetURLS;
    }

    @Override
    public Set<Mapping> mapToURLsFull(Xref xref, String... targetURISpaces) throws IDMapperException {
        List<Mapping> beans = uriService.mapToURLs(xref.getId(), xref.getDataSource().getSystemCode(), Arrays.asList(targetURISpaces));
        return new HashSet<Mapping>(beans); 
    }

    @Override
    public Map<Xref, Set<String>> mapToURLs(Collection<Xref> srcXrefs, String... targetURISpaces) throws IDMapperException {
        HashMap<Xref, Set<String>> results = new HashMap<Xref, Set<String>> ();
        for (Xref ref:srcXrefs){
            Set<String> urls = mapToURLs(ref, targetURISpaces);
            results.put(ref, urls);
        }
        return results;
     }

    @Override
    public boolean uriExists(String URL) throws IDMapperException {
        return uriService.URLExists(URL).exists();
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws IDMapperException {
        URLSearchBean  bean = uriService.URLSearch(text, "" + limit);
        return bean.getURLSet();
    }

    @Override
    public Set<Mapping> mapURLFull(String sourceURL, String... targetURISpaces) throws IDMapperException {
        List<Mapping> beans = uriService.mapURL(sourceURL, Arrays.asList(targetURISpaces));
        return new HashSet<Mapping>(beans);
    }

    @Override
    public Xref toXref(String URL) throws IDMapperException {
        XrefBean bean = uriService.toXref(URL);
        return XrefBeanFactory.asXref(bean);
    }

    @Override
    public Mapping getMapping(int id) throws IDMapperException {
        return uriService.getMapping("" + id);
    }

    @Override
    public List<Mapping> getSampleMapping() throws IDMapperException {
        return uriService.getSampleMappings();
    }
    
    @Override
    public OverallStatistics getOverallStatistics() throws IDMapperException {
        OverallStatisticsBean bean = uriService.getOverallStatistics();
        return OverallStatisticsBeanFactory.asOverallStatistics(bean);
    }

    @Override
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws IDMapperException {
        MappingSetInfoBean bean = uriService.getMappingSetInfo("" + mappingSetId);
        return MappingSetInfoBeanFactory.asMappingSetInfo(bean);
    }

    @Override
    public List<MappingSetInfo> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws IDMapperException {
        List<MappingSetInfoBean> beans = uriService.getMappingSetInfos(sourceSysCode, targetSysCode);
        ArrayList<MappingSetInfo> results = new ArrayList<MappingSetInfo>(); 
        for (MappingSetInfoBean bean:beans){
            results.add(MappingSetInfoBeanFactory.asMappingSetInfo(bean));
        }
        return results;  
    }
   
    @Override
    public Set<String> getUriSpaces(String dataSource) throws IDMapperException {
        DataSourceUriSpacesBean bigBean = uriService.getDataSource(dataSource);
        List<UriSpaceBean> beans = bigBean.getUriSpace(); 
        HashSet<String> results = new HashSet<String>();
        for (UriSpaceBean bean:beans){
            results.add(bean.getUriSpace());
        }
        return results;
    }

    @Override
    public Set<String> getSourceUriSpace(int mappingSetId) throws IDMapperException {
        MappingSetInfo info = getMappingSetInfo(mappingSetId);
        return getUriSpaces(info.getSourceSysCode());
    }

    @Override
    public Set<String> getTargetUriSpace(int mappingSetId) throws IDMapperException {
        MappingSetInfo info = getMappingSetInfo(mappingSetId);
        return getUriSpaces(info.getTargetSysCode());
    }

    @Override
    public int getSqlCompatVersion() throws IDMapperException {
        return Integer.parseInt(uriService.getSqlCompatVersion());
    }

  }
