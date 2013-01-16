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
import org.bridgedb.IDMapperException;

@XmlRootElement(name="DataSourceMapping")
public class DataSourceMapBeanFactory {

    public static DataSourceMapBean asBean(DataSourceBean source, List<DataSourceBean> targets){
        DataSourceMapBean bean = new DataSourceMapBean();
        bean.source = source;
        bean.target = targets;
        return bean;
    }

    public static DataSourceMapBean asBean(DataSource source, Set<DataSource> tgtDataSource){
        DataSourceMapBean bean = new DataSourceMapBean();
        bean.source = DataSourceBeanFactory.asBean(source);
        bean.target = new ArrayList<DataSourceBean>();
        for (DataSource tgt:tgtDataSource){
           bean.target.add(DataSourceBeanFactory.asBean(tgt));
        }
        return bean;
    }

    public static DataSource getKey(DataSourceMapBean bean) throws IDMapperException {
        return DataSourceBeanFactory.asDataSource(bean.source);
    }

    public static Set<DataSource> getMappedSet(DataSourceMapBean bean) throws IDMapperException {
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean trg:bean.target){
            results.add(DataSourceBeanFactory.asDataSource(trg));
        }
        return results;
    }
    
}
