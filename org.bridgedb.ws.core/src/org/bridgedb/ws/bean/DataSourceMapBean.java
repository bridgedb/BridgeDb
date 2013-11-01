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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;
import org.bridgedb.utils.BridgeDBException;

@XmlRootElement(name="DataSourceMapping")
public class DataSourceMapBean {
    DataSourceBean source;
    //Names of list are singular as they appear in the xml individually
    List<DataSourceBean> target;
    
    public DataSourceMapBean(){}

    public static DataSourceMapBean asBean(DataSource source, Set<DataSource> tgtDataSource){
        DataSourceMapBean bean = new DataSourceMapBean();
        bean.source = new DataSourceBean(source);
        bean.target = new ArrayList<DataSourceBean>();
        for (DataSource tgt:tgtDataSource){
           bean.target.add(new DataSourceBean(tgt));
        }
        return bean;
    }

    public static DataSource AsDataSource(DataSourceMapBean bean) throws BridgeDBException {
        if (bean.source == null){
            return null;
        }
        return DataSourceBean.asDataSource(bean.source);
    }

    public static Set<DataSource> getMappedSet(DataSourceMapBean bean) throws BridgeDBException {
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean trg:bean.target){
            results.add(DataSourceBean.asDataSource(trg));
        }
        return results;
    }
    
    /**
     * @return the source
     */
    public DataSourceBean getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(DataSourceBean source) {
        this.source = source;
    }

    /**
     * @return the target(s)
     */
    public List<DataSourceBean> getTarget() {
        return target;
    }

    /**
     * @param targets the targets to set
     */
    public void setTarget(List<DataSourceBean> target) {
        this.target = target;
    }
    
}
