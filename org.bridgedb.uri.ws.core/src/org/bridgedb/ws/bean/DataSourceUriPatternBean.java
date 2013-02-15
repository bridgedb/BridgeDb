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
package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="DataSourceUriPattern")
public class DataSourceUriPatternBean {
    private List<UriPatternBean> uriPatterns;

    private DataSourceBean DataSource;
    
    /**
     * Empty Constructor for WebServcices
     */
    public DataSourceUriPatternBean(){
        uriPatterns = new ArrayList<UriPatternBean>();
    }
    
    public DataSourceUriPatternBean(DataSource dataSource, List<String> patterns){
        DataSource = new DataSourceBean(dataSource);
        uriPatterns = new ArrayList<UriPatternBean>();
        for (String pattern:patterns){
            uriPatterns.add(new UriPatternBean(pattern));
        }
    }
    
    /**
     * @return the UriPattern(s)
     */
    public List<UriPatternBean> getUriPatterns() {
        return uriPatterns;
    }

    /**
     * @param uriPattern the uriPattern(s) to set
     */
    public void setUriPatterns(List<UriPatternBean> uriPatterns) {
        this.uriPatterns = uriPatterns;
    }

    /**
     * @return the DataSource
     */
    public DataSourceBean getDataSource() {
        return DataSource;
    }

    /**
     * @param DataSource the DataSource to set
     */
    public void setDataSource(DataSourceBean DataSource) {
        this.DataSource = DataSource;
    }

    @Override
    public String toString(){
        return uriPatterns + "->" + DataSource;
    }
}