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
import org.bridgedb.ws.bean.ProvenanceBean;
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

    //provenanceIdStrings, positionString and limitString are Strings so I can catch and report illegal values
    public List<URLMappingBean> getMappings(
            List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces,
            List<String> provenanceIdStrings,String positionString, String limitString, Boolean full);

    //public ProvenanceStatisticsBean getProvenance(Integer id) throws IDMapperException;

    //public ProvenanceStatisticsBean getProvenanceByPosition(Integer position) throws IDMapperException;

    //public List<ProvenanceStatisticsBean> getProvenanceByPosition(Integer position, Integer limit) throws IDMapperException;

    //public List<ProvenanceStatisticsBean> getSourceProvenanceByNameSpace(String nameSpace) throws IDMapperException;

    //public List<ProvenanceStatisticsBean> getTargetProvenanceByNameSpace(String nameSpace) throws IDMapperException;

    //public DataSourceStatisticsBean getDataSourceStatistics(String code) throws IDMapperException;

    // DataSourceStatisticsBean getDataSourceStatisticsByPosition(Integer position) throws IDMapperException;

    //public List<DataSourceStatisticsBean> getDataSourceStatisticsByPosition(Integer position, Integer limit) throws IDMapperException;

    public OverallStatisticsBean getOverallStatistics() throws IDMapperException;

    public URLMappingBean getMapping(String idString);

    //Removed due to scale issues
    //Position and Limit are Strings rather than Integers as this allow for better error handling
    //public List<XrefBean> getXrefs(ArrayList<String> dataSourceSysCodes, List<String> provenanceIds, 
    //        String position, String limit) throws IDMapperException;

    //Removed due to scale issues
    //Position and Limit are Strings rather than Integers as this allow for better error handling
    //public List<URLBean> getURLs(List<String> nameSpaces, List<String> provenanceIds,
    //        String position, String limit) throws IDMapperException;

    public List<ProvenanceBean> getProvenanceInfos() throws IDMapperException;

    public List<URLBean> getSampleSourceURLs() throws IDMapperException;

    public ProvenanceBean getProvenanceInfo(String id) throws IDMapperException;

}
