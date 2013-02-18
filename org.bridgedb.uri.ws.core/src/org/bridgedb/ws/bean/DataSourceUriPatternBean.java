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

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="DataSourceUriPattern")
public class DataSourceUriPatternBean {
    private Set<String> UriPattern;

    private DataSourceBean DataSource;
    
    /**
     * Empty Constructor for WebServcices
     */
    public DataSourceUriPatternBean(){
        UriPattern = new HashSet<String>();
    }
    
    public DataSourceUriPatternBean(DataSource dataSource, Set<String> patterns){
        DataSource = new DataSourceBean(dataSource);
        UriPattern = patterns;
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
        return UriPattern + "->" + DataSource;
    }

    /**
     * @return the UriPattern
     */
    public Set<String> getUriPattern() {
        return UriPattern;
    }

    /**
     * @param UriPattern the UriPattern to set
     */
    public void setUriPattern(Set<String> UriPattern) {
        this.UriPattern = UriPattern;
    }
}