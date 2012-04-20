/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.CapabilitiesBean;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.MappingSupportedBean;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.XrefMapBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefExistsBean;

/**
 *
 * @author Christian
 */
public interface WSCoreInterface {

    List<XrefBean> freeSearch(String text, Integer limit) throws IDMapperException;

    DataSourceBean getDataSoucre(String code) throws IDMapperException;

    List<PropertyBean> getKeys();

    PropertyBean getProperty(String key);

    List<DataSourceBean> getSupportedSrcDataSources() throws IDMapperException;

    List<DataSourceBean> getSupportedTgtDataSources() throws IDMapperException;

    FreeSearchSupportedBean isFreeSearchSupported();

    MappingSupportedBean isMappingSupported( String sourceSysCode, String targetCode) throws IDMapperException;

    List<XrefMapBean> mapID(List<String> id, List<String> scrCode, List<String> provenanceId, 
            List<String> targetCodes) throws IDMapperException;

    XrefExistsBean xrefExists(String id, String scrCode) throws IDMapperException;
 
    CapabilitiesBean getCapabilities();

    public List<URLMappingBean> mapByURLs(List<String> sourceURL, List<String> provenanceId, List<String> targetNameSpace) 
            throws IDMapperException;

    public URLExistsBean urlExists(String URL) throws IDMapperException;

    public URLSearchBean URLSearch(String text, Integer limit) throws IDMapperException;

    //public ProvenanceBean getProvenance(String provenanceId);
}
