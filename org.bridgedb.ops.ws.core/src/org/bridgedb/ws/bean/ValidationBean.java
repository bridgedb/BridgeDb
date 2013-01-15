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

import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.IDMapperException;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="Validation")
public class ValidationBean {
    private String report;
    private String info;
    private String mimeType;
    private String storeType;
    private String validationType;
    private Boolean includeWarnings;
    private String exception;
    
    //Webservice constructor
    public ValidationBean(){
    }

    public ValidationBean(String report, String info, String mimeType, String storeType, String validationType, 
            Boolean includeWarnings, String exception) {
        this.report = report;
        this.info = info;
        this.mimeType = mimeType;
        this.storeType = storeType;
        this.validationType = validationType;
        this.includeWarnings = includeWarnings;
        this.exception = exception;
    }
    
    public ValidationBean(String report, String info, String mimeType, String storeType, String validationType, 
            String includeWarnings, String exception) {
        this.report = report;
        this.info = info;
        this.mimeType = mimeType;
        this.storeType = storeType;
        this.validationType = validationType;
        this.includeWarnings = Boolean.parseBoolean(includeWarnings);
        this.exception = exception;
    }

    public ValidationBean(String report, String info, String mimeType, StoreType storeType, 
            ValidationType validationType, Boolean includeWarnings, String exception) {
        this.report = report;
        this.info = info;
        this.mimeType = mimeType;
        this.storeType = storeType.toString();
        this.validationType = validationType.toString();
        this.includeWarnings = includeWarnings;
        this.exception = exception;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Validation Report: ");
        builder.append("\n");
        builder.append(report);
        builder.append("\nMimeType: ");
        builder.append(getMimeType());
        builder.append("\nStoreType: ");
        builder.append(getStoreType());
        builder.append("\nValidationType: ");
        builder.append(getMimeType());      
        builder.append("\nIncludeWarnings: ");
        builder.append(getIncludeWarnings());
        builder.append("\nException: ");
        builder.append(getException());
        return builder.toString();
     }

    /**
     * @return the report
     */
    public String getReport() {
        return report;
    }

    /**
     * @return the report
     */
    public String getOkReport() throws IDMapperException {
        if (exception == null || exception.isEmpty()){
            return report;
        }
        throw new BridgeDBException(exception);
    }

    /**
     * @param report the report to set
     */
    public void setReport(String report) {
        this.report = report;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the storeType
     */
    public String getStoreType() {
        return storeType;
    }

    /**
     * @param storeType the storeType to set
     */
    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    /**
     * @return the validationType
     */
    public String getValidationType() {
        return validationType;
    }

    /**
     * @param validationType the validationType to set
     */
    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    /**
     * @return the includeWarnings
     */
    public Boolean getIncludeWarnings() {
        return includeWarnings;
    }

    /**
     * @param includeWarnings the includeWarnings to set
     */
    public void setIncludeWarnings(Boolean includeWarnings) {
        this.includeWarnings = includeWarnings;
    }

    /**
     * @return the exception
     */
    public String getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(String exception) {
        this.exception = exception;
    }

}
