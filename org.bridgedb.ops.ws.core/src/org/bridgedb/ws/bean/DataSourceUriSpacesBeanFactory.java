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
package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;

public class DataSourceUriSpacesBeanFactory{

    //Webservice constructor
    public DataSourceUriSpacesBeanFactory(){
    }
    
    public static DataSourceUriSpacesBean asBean(DataSource dataSource, Set<String> uriSpaces){
        DataSourceUriSpacesBean bean = new DataSourceUriSpacesBean();
        bean.setSysCode(dataSource.getSystemCode());
        bean.setFullName(dataSource.getFullName());
        String urlPattern = dataSource.getUrl("$id");
        if (urlPattern.length() > 3 ){
            bean.setUrlPattern(urlPattern);
        } else {
            bean.setUrlPattern(null);
        }
        bean.setIdExample(dataSource.getExample().getId());
        bean.setIsPrimary(dataSource.isPrimary());
        bean.setType(dataSource.getType());
    	bean.setOrganism(dataSource.getOrganism());
        String emptyUrn = dataSource.getURN("");
        if (emptyUrn.length() > 1){
            bean.setUrnBase(emptyUrn.substring(0, emptyUrn.length()-1));    
        } else {
            bean.setUrnBase(null);
        }
        bean.setMainUrl(dataSource.getMainUrl()); 
        List<UriSpaceBean> beans = new ArrayList<UriSpaceBean>();
        for (String uriSpace: uriSpaces){
            beans.add(new UriSpaceBean(uriSpace));
        }
        bean.setUriSpace(beans);
        return bean;
    }
    
}
