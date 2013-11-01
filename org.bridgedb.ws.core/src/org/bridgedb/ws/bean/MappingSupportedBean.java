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

/**
 *
 * @author Christian
 */
@XmlRootElement(name="FreeSearchSupported")
public class MappingSupportedBean {
    
    Boolean isMappingSupported;
    DataSourceBean source;
    DataSourceBean target;

    public MappingSupportedBean(){
    }
    
    public MappingSupportedBean(DataSource sourceDataSource, DataSource targetDataSource, boolean supported){
        source = new DataSourceBean(sourceDataSource);
        target = new DataSourceBean(targetDataSource);
        isMappingSupported = supported;
    }
    
    /**
     * @return the isMappingSupported
     */
    public Boolean getisMappingSupported() {
        return isMappingSupported;
    }

    /**
     * @return the isMappingSupported
     */
    public boolean isMappingSupported() {
        return isMappingSupported;
    }
    /**
     * @param isMappingSupported the isMappingSupported to set
     */
    public void setisMappingSupported(Boolean isMappingSupported) {
        this.isMappingSupported = isMappingSupported;
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
     * @return the target
     */
    public DataSourceBean getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(DataSourceBean target) {
        this.target = target;
    }
    
    @Override
    public String toString(){
        String toString = "";
        if (isMappingSupported) {
           toString = "mapping supported:\n\t";
        } else {
           toString = "mapping unknown:\n\t";            
        }
        toString+= source.toString();
        toString+= "\n\t";
        toString+= target.toString();
        return toString;
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
