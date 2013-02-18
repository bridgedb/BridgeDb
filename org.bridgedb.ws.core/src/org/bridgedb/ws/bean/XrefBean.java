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

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;

@XmlRootElement(name="Xref")
public class XrefBean {
    String id;
    DataSourceBean dataSource;
    
    public XrefBean(){
        id = null;
        dataSource = null;
    }
        
    public String getId() {
        return id;
    }
  
    public static XrefBean asBean(Xref xref){
        if (xref == null){
            return null;
        }
        XrefBean bean = new XrefBean();
        bean.id = xref.getId();
        bean.dataSource = DataSourceBean.asBean(xref.getDataSource());
        return bean;
    }

    public static Xref asXref(XrefBean bean){
        if (bean == null){
            return null;
        }
        DataSource ds = DataSourceBean.asDataSource(bean.dataSource);
        if (ds == null && bean.id == null){
            return null;
        }
        return new Xref(bean.id, ds);
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public DataSourceBean getDataSource(){
        return dataSource;
    }
    
    public void setDataSource(DataSourceBean dataSource){
        this.dataSource = dataSource;
    }

    public String toString(){
        return id + ":" + dataSource;
    }
}
