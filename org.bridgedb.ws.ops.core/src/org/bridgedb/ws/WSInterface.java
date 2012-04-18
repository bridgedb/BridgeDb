/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.ws.bean.DataSourceStatisticsBean;
import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLsBean;
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
    List<XrefBean> getXrefByPosition (String code, Integer position, Integer limit) throws IDMapperException;

    URLsBean getURLByPosition (String nameSpace, Integer position, Integer limit) throws IDMapperException;

    //provenanceIdStrings, positionString and limitString are Strings so I can catch and report illegal values
    public List<URLMappingBean> getMappings(List<String> idStrings, 
            List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces,
            List<String> provenanceIdStrings,String positionString, String limitString, Boolean full);

    //public ProvenanceStatisticsBean getProvenance(Integer id) throws IDMapperException;

    //public ProvenanceStatisticsBean getProvenanceByPosition(Integer position) throws IDMapperException;

    //public List<ProvenanceStatisticsBean> getProvenanceByPosition(Integer position, Integer limit) throws IDMapperException;

    //public List<ProvenanceStatisticsBean> getSourceProvenanceByNameSpace(String nameSpace) throws IDMapperException;

    //public List<ProvenanceStatisticsBean> getTargetProvenanceByNameSpace(String nameSpace) throws IDMapperException;

    public DataSourceStatisticsBean getDataSourceStatistics(String code) throws IDMapperException;

    public DataSourceStatisticsBean getDataSourceStatisticsByPosition(Integer position) throws IDMapperException;

    public List<DataSourceStatisticsBean> getDataSourceStatisticsByPosition(Integer position, Integer limit) throws IDMapperException;


}
