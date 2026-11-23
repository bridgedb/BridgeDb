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
package org.bridgedb.uri.ws.bean;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.statistics.DataSetInfo;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="DataSetInfo")
public class DataSetInfoBean {
    private String sysCode;
    private String fullName;

    /**
     * WS Constructor
     */
    public DataSetInfoBean(){
    }
    
    public static DataSetInfo asDataSetInfo(DataSetInfoBean bean){
       return new DataSetInfo(bean.getSysCode(), bean.getFullName());
    }

    public static DataSetInfoBean asBean(DataSetInfo info) {
        DataSetInfoBean bean = new DataSetInfoBean();
        bean.sysCode = info.getSysCode();
        bean.fullName = info.getFullName();
        return bean;
    }

    public static Set<DataSetInfoBean> asBeans(Set<DataSetInfo> infos){
        HashSet<DataSetInfoBean> results = new HashSet<DataSetInfoBean>();
        if (results != null){
            for (DataSetInfo info:infos){
                results.add(asBean(info)); 
            }
        }
        return results;
    }
    
    public static Set<DataSetInfo> asDataSetInfos(Set<DataSetInfoBean> beans){
        HashSet<DataSetInfo> results = new HashSet<DataSetInfo>();
        if (beans!= null){
            for (DataSetInfoBean bean:beans){
                results.add(asDataSetInfo(bean)); 
            }
        }
        return results;
    }
    
    /**
     * @return the sysCode
     */
    public String getSysCode() {
        return sysCode;
    }

    /**
     * @param sysCode the sysCode to set
     */
    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
}
