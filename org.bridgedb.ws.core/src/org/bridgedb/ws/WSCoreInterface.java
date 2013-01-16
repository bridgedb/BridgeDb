// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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

    MappingSupportedBean isMappingSupported( String sourceCode, String targetCode) throws IDMapperException;

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
