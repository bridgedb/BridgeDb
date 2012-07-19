/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public interface WSOpsInterface extends WSCoreInterface{

    public List<URLMappingBean> mapURL(String URL, List<String> targetUriSpace) throws IDMapperException;
    
    public URLExistsBean URLExists(String URL) throws IDMapperException;

    public URLSearchBean URLSearch(String text, String limitString) throws IDMapperException;

    public XrefBean toXref(String URL) throws IDMapperException;

    public URLMappingBean getMapping(String id) throws IDMapperException;

    public List<URLBean> getSampleSourceURLs() throws IDMapperException;

    public OverallStatisticsBean getOverallStatistics() throws IDMapperException;

    public List<MappingSetInfoBean> getMappingSetInfos() throws IDMapperException;

    public DataSourceUriSpacesBean getDataSource(String dataSource) throws IDMapperException;
}
