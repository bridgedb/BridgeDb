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
import javax.xml.bind.annotation.XmlType;
import org.bridgedb.DataSource;

@XmlRootElement(name="DataSource")
//@XmlType(propOrder={"sysCode","urlPattern", "mainUrl", "fullName", "idExample", "isPrimary", "organism", "urnBase", "type"})
@XmlType(propOrder={"sysCode","urlPattern", "mainUrl", "fullName", "idExample", "isPrimary", "urnBase", "type"})
public class DataSourceBean {
  	String sysCode;
	String fullName;
    String urlPattern;
	String idExample;
	boolean isPrimary;
	String type;
    //I wonder how to do this?
	//Object organism;
	String urnBase;    
	String mainUrl;

    //Webservice constructor
    public DataSourceBean(){
    }

    public DataSourceBean (DataSource dataSource){
        sysCode = dataSource.getSystemCode();
        fullName = dataSource.getFullName();
        urlPattern = dataSource.getKnownUrl("$id");
        idExample = dataSource.getExample().getId();
        isPrimary = dataSource.isPrimary();
        type = dataSource.getType();
        //Object organism = dataSource.getOrganism();
        //if (organism instanceof Organism)
        String emptyUrn = dataSource.getMiriamURN("");
        if (emptyUrn != null && emptyUrn.length() > 1){
            urnBase = emptyUrn.substring(0, emptyUrn.length()-1);    
        } else {
            urnBase = null;
        }
        mainUrl = dataSource.getMainUrl(); 
    }
    
    public static DataSourceBean asBean(String sysCode){
        DataSourceBean bean = new DataSourceBean();
        bean.sysCode = sysCode;
        bean.fullName = null;
        bean.urlPattern = null;
        bean.idExample = null;
        bean.isPrimary = false;
        bean.type = null;
        bean.urnBase = null;
        bean.mainUrl = null;; 
        return bean;
    }
    
    public static DataSource asDataSource(DataSourceBean bean) {
        if (bean == null){
            return null;
        }
        return bean.asDataSource();
    }

    public DataSource asDataSource() {
        DataSource.Builder builder = DataSource.register(sysCode, fullName);
        if (urlPattern != null){
            builder = builder.urlPattern(urlPattern);
        }
        if (idExample != null){
            builder = builder.idExample(idExample);
        }
        builder = builder.primary(isPrimary);
        builder = builder.type(type);
//        if (organism != null){
//            builder = builder.organism(organism);
//        }
        if (urnBase != null){
            builder = builder.urnBase(urnBase);
        }
        if (mainUrl != null){
            builder = builder.mainUrl(mainUrl);
        }
        return builder.asDataSource();
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

    /**
     * @return the urlPattern
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * @param urlPattern the urlPattern to set
     */
    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    /**
     * @return the idExample
     */
    public String getIdExample() {
        return idExample;
    }

    /**
     * @param idExample the idExample to set
     */
    public void setIdExample(String idExample) {
        this.idExample = idExample;
    }

    /**
     * @return the isPrimary
     */
    public boolean isIsPrimary() {
        return isPrimary;
    }

    /**
     * @param isPrimary the isPrimary to set
     */
    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the organism
     * /
    public Object getOrganism() {
        return organism;
    }

    /**
     * 
     * /
    public void setOrganism(Object organism) {
        this.organism = organism;
    }

    /**
     * @return the urnBase
     */
    public String getUrnBase() {
        return urnBase;
    }

    /**
     * @param urnBase the urnBase to set
     */
    public void setUrnBase(String urnBase) {
        this.urnBase = urnBase;
    }

    /**
     * @return the mainUrl
     */
    public String getMainUrl() {
        return mainUrl;
    }

    /**
     * @param mainUrl the mainUrl to set
     */
    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }
     
    public String toString(){
        if (sysCode == null){
            return "FullName = " + fullName;
        }
        if (fullName == null){
            return "sysCode = " + sysCode;
        }
        return sysCode + ":" + fullName;
    }
}
