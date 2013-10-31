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
import javax.xml.bind.annotation.XmlType;
import org.bridgedb.DataSource;

@XmlRootElement(name="DataSources")
public class DataSourcesBean {

    private Set<DataSourceBean> DataSource;
    
    //Webservice constructor
    public DataSourcesBean(){
        DataSource = new HashSet<DataSourceBean>();
    }

    public DataSourcesBean(Set<DataSource> dataSources) {
        DataSource = new HashSet<DataSourceBean>();
        for (DataSource ds:dataSources){
            DataSource.add(new DataSourceBean(ds));
        }
    }

    public Set<DataSource> getDataSources(){
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean bean:DataSource){
            results.add(DataSourceBean.asDataSource(bean));
        }
        return results;
    }
    
    /**
     * @return the DataSource
     */
    public Set<DataSourceBean> getDataSource() {
        return DataSource;
    }

    /**
     * @param DataSource the DataSource to set
     */
    public void setDataSource(Set<DataSourceBean> DataSource) {
        this.DataSource = DataSource;
    }

    public boolean isEmpty() {
        return DataSource.isEmpty();
    }

    

}
