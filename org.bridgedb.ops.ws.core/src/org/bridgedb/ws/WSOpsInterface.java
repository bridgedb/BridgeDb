// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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

import java.io.InputStream;
import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLMappingBean;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.ValidationBean;
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

    public List<MappingSetInfoBean> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws IDMapperException;

    public MappingSetInfoBean getMappingSetInfo(String mappingSetId) throws IDMapperException;

    public DataSourceUriSpacesBean getDataSource(String dataSource) throws IDMapperException;

    //public ValidationBean validateString(String info, String mimeTypee, String storeType, String validationType, 
    //        String includeWarnings) throws IDMapperException;

    //public ValidationBean validateStringAsLinksetVoid(String info, String mimeType) throws IDMapperException;

    /*public String loadString(String info, String mineType, String storeType, String validationType) 
            throws IDMapperException;

    public String saveString(String info, String mineType, String storeType, String validationType) 
            throws IDMapperException;

    public String checkStringValid(String info, String defaultMIMEType, String storeType, String validationType) 
            throws IDMapperException;

    public ValidationBean validateInputStream(InputStream inputStream, String mimeType, String storeType, 
            String validationType, String includeWarnings)throws IDMapperException;

    public String loadInputStream(String source, InputStream inputStream, String mimeType, String storeType, 
            String validationType);

    public String saveInputStream(String source, InputStream inputStream, String mimeType, String storeType, 
            String validationType);

    public void checkInputStreamValid(String source, InputStream inputStream, String mimeType, String storeType, 
            String validationType);
    */
     
 }
