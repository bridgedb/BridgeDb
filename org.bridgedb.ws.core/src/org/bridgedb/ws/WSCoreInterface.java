package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.CapabilitiesBean;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.MappingSupportedBean;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefExistsBean;
import org.bridgedb.ws.bean.XrefMapBean;

/**
 *
 * @author Christian
 */
public interface WSCoreInterface {

    List<XrefMapBean> mapID(List<String> id, List<String> scrCode, List<String> targetCodes) throws IDMapperException;

    XrefExistsBean xrefExists(String id, String scrCode) throws IDMapperException;

    List<XrefBean> freeSearch(String text, String limit) throws IDMapperException;

    CapabilitiesBean getCapabilities();

    FreeSearchSupportedBean isFreeSearchSupported();

    List<DataSourceBean> getSupportedSrcDataSources() throws IDMapperException;

    List<DataSourceBean> getSupportedTgtDataSources() throws IDMapperException;

    MappingSupportedBean isMappingSupported( String sourceSysCode, String targetCode) throws IDMapperException;

    PropertyBean getProperty(String key);

    List<PropertyBean> getKeys();
   
    /*DataSourceBean getDataSoucre(String code) throws IDMapperException;



    CapabilitiesBean getCapabilities();

    public List<URLMappingBean> mapByURLs(List<String> sourceURL, List<String> linkSetId, List<String> targetNameSpace) 
            throws IDMapperException;

    public URLExistsBean urlExists(String URL) throws IDMapperException;

    public URLSearchBean URLSearch(String text, Integer limit) throws IDMapperException;
*/

}
