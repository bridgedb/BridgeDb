/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.Set;
import org.bridgedb.ws.bean.DataSourceStatisticsBean;
import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.LinkSetBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.XrefBean;

/**
 *
 * @author Christian
 */
public interface WSInterface extends WSCoreInterface {

    /**
     * Obtains the Xref(s) by their current position in the underlying data source.
     * 
     * Used for Iteration NOT assigning IDs.
     * <p>
     * Position numbers must be sequencial starting at zero.
     * There is NO requirement that the same xref is returned for the same position over time.
     * OPTIONAL
     * @param code DataSource Optional default is all DataSources
     * @param position Zero based start position
     * @param limit Maximum number of xrefs to return OPTIONAL
     * @return 
     */
    //List<XrefBean> getXrefByPosition (String code, Integer position, Integer limit) throws IDMapperException;

    //URLsBean getURLByPosition (String nameSpace, Integer position, Integer limit) throws IDMapperException;

    public List<URLMappingBean> getMappings(
            List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces,
            List<String> linkSetIdStrings,String positionString, String limitString, Boolean full);

    public OverallStatisticsBean getOverallStatistics() throws IDMapperException;

    public URLMappingBean getMapping(String idString);

    public List<LinkSetBean> getLinkSetInfos() throws IDMapperException;

    public List<URLBean> getSampleSourceURLs() throws IDMapperException;

    public LinkSetBean getLinkSetInfo(String id) throws IDMapperException;

    public List<URLBean> getLinksetNames() throws IDMapperException;

    public String linkset(String idString) throws IDMapperException;

}
