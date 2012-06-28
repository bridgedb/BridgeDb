/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.ops.LinkSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.ws.bean.XrefBean;
import java.util.List;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.ops.LinkSetStore;
import org.bridgedb.ops.OpsMapper;
import org.bridgedb.result.URLMapping;
import org.bridgedb.url.URISpace;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.OverallStatisticsBeanFactory;
import org.bridgedb.ws.bean.LinkSetBean;
import org.bridgedb.ws.bean.LinkSetFactory;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.XrefBeanFactory;

/**
 *
 * @author Christian
 */
public class WSMapper extends WSCoreMapper implements OpsMapper, LinkSetStore {
    //removed due to scale issues XrefIterator, URISpace, 

    WSInterface webService;
    final static List<String> NO_IDS = new ArrayList<String>();
    final static List<String> EMPTY = new ArrayList<String>();
    
    public WSMapper(WSInterface webService){
        super(webService);
        this.webService = webService;
    }
    
    /** OpsMapper Methods **/
    @Override
    public List<URLMapping> getMappings(List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces, 
            List<String> linkSetIds, Integer position, Integer limit){
        String positionString;
        if (position == null) {
            positionString = null;
        } else {
            positionString = position.toString();
        }
        String limitString;
        if (limit == null) {
            limitString = null;
        } else {
            limitString = limit.toString();
        }
        List<URLMappingBean> beans = webService.getMappings(URLs, sourceURLs, targetURLs, 
                nameSpaces, sourceNameSpaces, targetNameSpaces, linkSetIds, positionString, limitString, true);
        ArrayList<URLMapping> mappings = new ArrayList<URLMapping>();
        for (URLMappingBean bean:beans){
            URLMapping map = URLMappingBeanFactory.asURLMapping(bean);
            mappings.add(map);
        }
        return mappings;
    }

    @Override
    public URLMapping getMapping(int id)  {
        URLMappingBean bean = webService.getMapping(""+id);
        return URLMappingBeanFactory.asURLMapping(bean);
    }
    
    @Override
    public OverallStatistics getOverallStatistics() throws IDMapperException {
        OverallStatisticsBean bean = webService.getOverallStatistics();
        return OverallStatisticsBeanFactory.asOverallStatistics(bean);
    }

    @Override
    public List<LinkSetInfo> getLinkSetInfos() throws IDMapperException {
        List<LinkSetBean> beans = webService.getLinkSetInfos();
        ArrayList<LinkSetInfo> results = new ArrayList<LinkSetInfo>();
        for (LinkSetBean bean: beans){
            results.add(LinkSetFactory.asLinkSetInfo(bean));
        }
        return results;
    }

    @Override
    public LinkSetInfo getLinkSetInfo(String id) throws IDMapperException {
        LinkSetBean bean = webService.getLinkSetInfo(id);
        return LinkSetFactory.asLinkSetInfo(bean);
    }

    @Override
    public List<String> getSampleSourceURLs() throws IDMapperException {
        List<URLBean> beans = webService.getSampleSourceURLs();
        ArrayList<String> results = new ArrayList<String>();
        for (URLBean bean:beans){
            results.add(bean.getURL());
        }
        return results;
    }





    @Override
    public List<String> getLinksetNames() throws IDMapperException {
        List<URLBean> beans = webService.getLinksetNames();
        ArrayList<String> results = new ArrayList<String>();
        for (URLBean bean:beans){
            results.add(bean.getURL());
        }
        return results;
    }

    @Override
    public String getRDF(int id) throws IDMapperException {
        return webService.linkset(""+id);
    }

}
