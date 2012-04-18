/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.Collection;
import java.util.Map;
import org.bridgedb.provenance.XrefProvenance;
import java.util.ArrayList;
import org.bridgedb.ws.bean.XrefBean;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.iterator.ByPositionURLIterator;
import org.bridgedb.iterator.URLByPosition;
import org.bridgedb.iterator.XrefByPosition;
import org.bridgedb.provenance.ProvenanceMapper;
import org.bridgedb.result.URLMapping;
import org.bridgedb.sql.FullMapper;
import org.bridgedb.url.URLIterator;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.URLsBean;
import org.bridgedb.ws.bean.XrefBeanFactory;

/**
 *
 * @author Christian
 */
public class WSMapper extends WSCoreMapper implements XrefIterator, XrefByPosition, 
        URLIterator, URLByPosition, FullMapper {

    WSInterface webService;
    final static List<String> NO_IDS = new ArrayList<String>();
    final static List<String> EMPTY = new ArrayList<String>();
    
    public WSMapper(WSInterface webService){
        super(webService);
        this.webService = webService;
    }
    
    //OPTIONAL
    @Override
    public Iterable<Xref> getIterator(DataSource ds) throws IDMapperException {
        return new WSIterator(webService, ds);
    }

    //OPTIONAL
    @Override
    public Iterable<Xref> getIterator() throws IDMapperException {
        return new WSIterator(webService);
    }

    @Override
    public Set<Xref> getXrefByPosition(int position, int limit) throws IDMapperException {
        List<XrefBean> beans = webService.getXrefByPosition(null, position, limit);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean bean:beans){
            results.add(XrefBeanFactory.asXref(bean));
        }
        return results;
    }

    @Override
    public Xref getXrefByPosition(int position) throws IDMapperException {
        List<XrefBean> beans = webService.getXrefByPosition(null, position, null);
        HashSet<Xref> results = new HashSet<Xref>();
        if (beans.isEmpty()) {
            return null;
        }
        return XrefBeanFactory.asXref(beans.get(0));
    }

    @Override
    public Set<Xref> getXrefByPosition(DataSource ds, int position, int limit) throws IDMapperException {
        List<XrefBean> beans = webService.getXrefByPosition(ds.getSystemCode(), position, limit);
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean bean:beans){
            results.add(XrefBeanFactory.asXref(bean));
        }
        return results;
    }

    @Override
    public Xref getXrefByPosition(DataSource ds, int position) throws IDMapperException {
        List<XrefBean> beans = webService.getXrefByPosition(ds.getSystemCode(), position, null);
        HashSet<Xref> results = new HashSet<Xref>();
        if (beans.isEmpty()) {
            return null;
        }
        return XrefBeanFactory.asXref(beans.get(0));
    }

    @Override
    public Iterable<String> getURLIterator(String nameSpace) throws IDMapperException {
        return new ByPositionURLIterator (this, nameSpace);
    }

    @Override
    public Iterable<String> getURLIterator() throws IDMapperException {
        return new ByPositionURLIterator (this);
    }

    @Override
    public Set<String> getURLByPosition(int position, int limit) throws IDMapperException {
        URLsBean beans = webService.getURLByPosition(null, position, limit);
        return beans.getUrlSet();
    }

    @Override
    public String getURLByPosition(int position) throws IDMapperException {
        return getURLByPosition(null, position);
    }

    @Override
    public Set<String> getURLByPosition(String nameSpace, int position, int limit) throws IDMapperException {
        URLsBean beans = webService.getURLByPosition(nameSpace, position, limit);
        return beans.getUrlSet();
    }

    @Override
    public String getURLByPosition(String nameSpace, int position) throws IDMapperException {
        URLsBean beans = webService.getURLByPosition(nameSpace, position, 1);
        List<String> urls = beans.getUrl();
        if (urls.size() == 1){
            return urls.get(0);
        }
        if (urls.isEmpty()){
            throw new IDMapperException("Empty list received from getURLByPosition at position " + position);
        }
        throw new IDMapperException("More than one url received from getURLByPosition at position " + position);
    }
    
    @Override
    public List<URLMapping> getMappings(List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces, 
            List<String> provenanceIds, Integer position, Integer limit){
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
        List<URLMappingBean> beans = webService.getMappings(NO_IDS, URLs, sourceURLs, targetURLs, 
                nameSpaces, sourceNameSpaces, targetNameSpaces, provenanceIds, positionString, limitString, true);
        ArrayList<URLMapping> mappings = new ArrayList<URLMapping>();
        for (URLMappingBean bean:beans){
            URLMapping map = URLMappingBeanFactory.asURLMapping(bean);
            mappings.add(map);
        }
        return mappings;
    }

    @Override
    public URLMapping getMapping(int id)  {
        List<String> ids = new ArrayList<String>();
        ids.add("" + id);
        List<URLMappingBean> beans = webService.getMappings(ids, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, null, null, true);
        return URLMappingBeanFactory.asURLMapping(beans.get(0));
    }
    
    /*@Override
    public ProvenanceStatistics getProvenance(int id) throws IDMapperException {
        ProvenanceStatisticsBean bean = webService.getProvenance(id);
        return bean.asProvenanceStatistics();
    }

    @Override
    public ProvenanceStatistics getProvenanceByPosition(int position) throws IDMapperException {
        ProvenanceStatisticsBean bean = webService.getProvenanceByPosition(position);
        return bean.asProvenanceStatistics();        
    }

    @Override
    public List<ProvenanceStatistics> getProvenanceByPosition(int position, int limit) throws IDMapperException {
        List<ProvenanceStatisticsBean> beans = webService.getProvenanceByPosition(position, limit);
        ArrayList<ProvenanceStatistics> stats = new ArrayList<ProvenanceStatistics>();
        for (ProvenanceStatisticsBean bean: beans){
            stats.add(bean.asProvenanceStatistics());
        }
        return stats;        
    }

    @Override
    public Set<ProvenanceStatistics> getSourceProvenanceByNameSpace(String nameSpace) throws IDMapperException {
        List<ProvenanceStatisticsBean> beans = webService.getSourceProvenanceByNameSpace(nameSpace);
        HashSet<ProvenanceStatistics> stats = new HashSet<ProvenanceStatistics>();
        for (ProvenanceStatisticsBean bean: beans){
            stats.add(bean.asProvenanceStatistics());
        }
        return stats;        
    }

    @Override
    public Set<ProvenanceStatistics> getTargetProvenanceByNameSpace(String nameSpace) throws IDMapperException {
        List<ProvenanceStatisticsBean> beans = webService.getTargetProvenanceByNameSpace(nameSpace);
        HashSet<ProvenanceStatistics> stats = new HashSet<ProvenanceStatistics>();
        for (ProvenanceStatisticsBean bean: beans){
            stats.add(bean.asProvenanceStatistics());
        }
        return stats;        
    }

    @Override
    public DataSourceStatistics getDataSourceStatistics(DataSource dataSource) throws IDMapperException {
        DataSourceStatisticsBean bean = webService.getDataSourceStatistics(dataSource.getSystemCode());
        return bean.asDataSourceStatistics();
    }

    @Override
    public DataSourceStatistics getDataSourceStatisticsByPosition(int position) throws IDMapperException {
        DataSourceStatisticsBean bean = webService.getDataSourceStatisticsByPosition(position);
        return bean.asDataSourceStatistics();
    }

    @Override
    public List<DataSourceStatistics> getDataSourceStatisticsByPosition(int position, int limit) throws IDMapperException {
        List<DataSourceStatisticsBean> beans = webService.getDataSourceStatisticsByPosition(position, limit);
        ArrayList<DataSourceStatistics> stats = new ArrayList<DataSourceStatistics>();
        for (DataSourceStatisticsBean bean: beans){
            stats.add(bean.asDataSourceStatistics());
        }
        return stats;        
    }

    @Override
    public DataVersion getDataVersion(int id) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DataVersion getDataVersionByPosition(int position) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DataVersion> getDataVersionByPosition(int position, int limit) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DataVersion> getDataVersionByNameSpace(String nameSpace) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MapperStatistics getMapperStatistics() throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
*/

}
