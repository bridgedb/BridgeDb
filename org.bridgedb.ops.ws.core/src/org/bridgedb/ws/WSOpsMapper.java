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
package org.bridgedb.ws;

import java.io.InputStream;
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
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.linkset.LinksetInterfaceMinimal;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.MappingSetInfoBeanFactory;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.OverallStatisticsBeanFactory;
import org.bridgedb.ws.bean.ProfileBean;
import org.bridgedb.ws.bean.ProfileBeanFactory;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.UriSpaceBean;
import org.bridgedb.ws.bean.ValidationBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefBeanFactory;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public class WSOpsMapper extends WSCoreMapper implements URLMapper{
    
    WSOpsInterface opsService;
    
    public WSOpsMapper(WSOpsInterface opsService){
        super(opsService);
        this.opsService = opsService;
    }

    @Override
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, 
    		String profileURL, String... targetURISpaces) throws IDMapperException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>> ();
        if (sourceURLs.isEmpty()) return results; //No valid srcrefs so return empty set
        for (String sourceURL:sourceURLs){
            Set<String> urls = mapURL(sourceURL, profileURL, targetURISpaces);
            results.put(sourceURL, urls);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String sourceURL, String profileURL,
    		String... targetURISpaces) throws IDMapperException {
        List<URLMappingBean> beans = opsService.mapURL(sourceURL, profileURL, Arrays.asList(targetURISpaces));
        HashSet<String> targetURLS = new HashSet<String>(); 
        for (URLMappingBean bean:beans){
            targetURLS.addAll(bean.getTargetURL());
        }
        return targetURLS;
    }


    @Override
    public boolean uriExists(String URL) throws IDMapperException {
        return opsService.URLExists(URL).exists();
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws IDMapperException {
        URLSearchBean  bean = opsService.URLSearch(text, "" + limit);
        return bean.getURLSet();
    }

    @Override
    public Set<URLMapping> mapURLFull(String sourceURL, String profileURL, String... targetURISpaces) throws IDMapperException {
        List<URLMappingBean> beans = opsService.mapURL(sourceURL, profileURL, Arrays.asList(targetURISpaces));
        HashSet<URLMapping> results = new HashSet<URLMapping>();
        for (URLMappingBean bean:beans){
            results.add(URLMappingBeanFactory.asURLMapping(bean));
        }
        return results;
    }

    @Override
    public Xref toXref(String URL) throws IDMapperException {
        XrefBean bean = opsService.toXref(URL);
        return XrefBeanFactory.asXref(bean);
    }

    @Override
    public URLMapping getMapping(int id) throws IDMapperException {
        URLMappingBean bean = opsService.getMapping("" + id);
        return URLMappingBeanFactory.asURLMapping(bean);
    }

    @Override
    public Set<String> getSampleSourceURLs() throws IDMapperException {
        List<URLBean> beans = opsService.getSampleSourceURLs();
        HashSet<String> results = new HashSet<String>();
        for (URLBean bean:beans){
            results.add(bean.getURL());
        }
        return results;
    }

    @Override
    public OverallStatistics getOverallStatistics() throws IDMapperException {
        OverallStatisticsBean bean = opsService.getOverallStatistics();
        return OverallStatisticsBeanFactory.asOverallStatistics(bean);
    }

    @Override
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws IDMapperException {
        MappingSetInfoBean bean = opsService.getMappingSetInfo("" + mappingSetId);
        return MappingSetInfoBeanFactory.asMappingSetInfo(bean);
    }

    @Override
    public List<MappingSetInfo> getMappingSetInfos() throws IDMapperException {
        List<MappingSetInfoBean> beans = opsService.getMappingSetInfos();
        ArrayList<MappingSetInfo> results = new ArrayList<MappingSetInfo>(); 
        for (MappingSetInfoBean bean:beans){
            results.add(MappingSetInfoBeanFactory.asMappingSetInfo(bean));
        }
        return results;  
    }

    @Override
    public Set<String> getUriSpaces(String dataSource) throws IDMapperException {
        DataSourceUriSpacesBean bigBean = opsService.getDataSource(dataSource);
        List<UriSpaceBean> beans = bigBean.getUriSpace(); 
        HashSet<String> results = new HashSet<String>();
        for (UriSpaceBean bean:beans){
            results.add(bean.getUriSpace());
        }
        return results;
    }

	@Override
	public List<ProfileInfo> getProfiles() throws IDMapperException {
		List<ProfileBean> beans = opsService.getProfiles();
		List<ProfileInfo> results = new ArrayList<ProfileInfo>();
		for (ProfileBean bean:beans) {
			results.add(ProfileBeanFactory.asProfileInfo(bean));
		}
		return results;
	}

	@Override
	public ProfileInfo getProfile(String profileURI)
			throws IDMapperException {
		ProfileBean profile = opsService.getProfile(profileURI);
		ProfileInfo result = ProfileBeanFactory.asProfileInfo(profile);
		return result;
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

    // *****   LinksetInterfaceMinimal Methods
    
    /*@Override
    public String validateString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType, boolean includeWarnings) throws IDMapperException {
        ValidationBean bean = opsService.validateString(info, format.getDefaultMIMEType(), storeType.toString(), 
                validationType.toString(), Boolean.toString(includeWarnings));
        return bean.getOkReport();
    }

    @Override
     public String validateInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws IDMapperException{
        ValidationBean bean = opsService.validateInputStream(inputStream, format.getDefaultMIMEType(), storeType.toString(), 
                validationType.toString(), Boolean.toString(includeWarnings));
        return bean.getOkReport();
    }

    @Override
    public String loadString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException {
        return opsService.loadString(info, format.getDefaultMIMEType(), storeType.toString(), validationType.toString());
    }

    @Override
    public String saveString(String source, String info, RDFFormat format, StoreType storeType, ValidationType validationType) 
            throws IDMapperException {
        return opsService.saveString(info, format.getDefaultMIMEType(), storeType.toString(), validationType.toString());
    }

    @Override
    public void checkStringValid(String source, String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws IDMapperException {
        opsService.checkStringValid(info, format.getDefaultMIMEType(), storeType.toString(), validationType.toString());
    }

    @Override
    public String loadInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws IDMapperException {
         return opsService.loadInputStream(source, inputStream, format.getDefaultMIMEType(), storeType.toString(), 
                 validationType.toString());
    }

    @Override
    public String saveInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws IDMapperException {
         return opsService.saveInputStream(source, inputStream, format.getDefaultMIMEType(), storeType.toString(), 
                 validationType.toString());
    }

    @Override
    public void checkInputStreamValid(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws IDMapperException {
        opsService.checkInputStreamValid(source, inputStream, format.getDefaultMIMEType(), storeType.toString(), 
                 validationType.toString());
    }
     */
}
