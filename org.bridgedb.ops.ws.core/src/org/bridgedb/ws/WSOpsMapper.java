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
import org.bridgedb.statistics.MappingSetStatistics;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.URLMapping;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.MappingSetInfoBeanFactory;
import org.bridgedb.ws.bean.MappingSetStatisticsBean;
import org.bridgedb.ws.bean.MappingSetStatisticsBeanFactory;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.UriSpacesBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefBeanFactory;

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
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, String... targetURISpaces) throws IDMapperException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>> ();
        if (sourceURLs.isEmpty()) return results; //No valid srcrefs so return empty set
        for (String sourceURL:sourceURLs){
            Set<String> urls = mapURL(sourceURL, targetURISpaces);
            results.put(sourceURL, urls);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String sourceURL, String... targetURISpaces) throws IDMapperException {
        List<URLMappingBean> beans = opsService.mapURL(sourceURL, Arrays.asList(targetURISpaces));
        HashSet<String> targetURLS = new HashSet<String>(); 
        for (URLMappingBean bean:beans){
            targetURLS.add(bean.getTargetURL());
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
    public Set<URLMapping> mapURLFull(String sourceURL, String... targetURISpaces) throws IDMapperException {
        List<URLMappingBean> beans = opsService.mapURL(sourceURL, Arrays.asList(targetURISpaces));
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
    public MappingSetStatistics getMappingSetStatistics() throws IDMapperException {
        MappingSetStatisticsBean bean = opsService.getMappingSetStatistics();
        return MappingSetStatisticsBeanFactory.asMappingSetStatistics(bean);
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
    public Set<String> getUriSpaces(String sysCode) throws IDMapperException {
        UriSpacesBean bean = opsService.getUriSpaces(sysCode);
        return bean.getUriSpaceSet();
    }

}
