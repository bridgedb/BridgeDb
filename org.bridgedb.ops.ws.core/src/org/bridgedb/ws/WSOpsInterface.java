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

    public List<URLMappingBean> mapURL(String URL, String profileURL, List<String> targetUriSpace) throws IDMapperException;
    
    public URLExistsBean URLExists(String URL) throws IDMapperException;

    public URLSearchBean URLSearch(String text, String limitString) throws IDMapperException;

    public XrefBean toXref(String URL) throws IDMapperException;

    public URLMappingBean getMapping(String id) throws IDMapperException;

    public List<URLBean> getSampleSourceURLs() throws IDMapperException;

    public OverallStatisticsBean getOverallStatistics() throws IDMapperException;

    public List<MappingSetInfoBean> getMappingSetInfos() throws IDMapperException;

    public DataSourceUriSpacesBean getDataSource(String dataSource) throws IDMapperException;
}
