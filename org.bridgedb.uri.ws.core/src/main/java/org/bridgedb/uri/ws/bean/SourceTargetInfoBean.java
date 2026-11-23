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

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.statistics.SourceTargetInfo;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="SourceInfo")
public class SourceTargetInfoBean {
    private DataSetInfoBean source;
    private DataSetInfoBean target;
    private Integer numberOfLinksets;
    private Integer numberOfLinks;

    /**
     * WS Constructor
     */
    public SourceTargetInfoBean(){
    }
    

    public SourceTargetInfoBean (SourceTargetInfo info) {
        source = DataSetInfoBean.asBean(info.getSource());
        target = DataSetInfoBean.asBean(info.getTarget());
        numberOfLinksets = info.getNumberOfLinksets();
        numberOfLinks = info.getNumberOfLinks();
    }

    public SourceTargetInfo asSourceTargetInfo(){
       return new SourceTargetInfo(DataSetInfoBean.asDataSetInfo(getSource()), 
               DataSetInfoBean.asDataSetInfo(getTarget()), 
               getNumberOfLinksets(),
               getNumberOfLinks());
    }

    /**
     * @return the source
     */
    public DataSetInfoBean getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(DataSetInfoBean source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    public DataSetInfoBean getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(DataSetInfoBean target) {
        this.target = target;
    }
   
    /**
     * @return the numberOfLinksets
     */
    public Integer getNumberOfLinksets() {
        return numberOfLinksets;
    }

    /**
     * @param numberOfLinksets the numberOfLinksets to set
     */
    public void setNumberOfLinksets(Integer numberOfLinksets) {
        this.numberOfLinksets = numberOfLinksets;
    }

    /**
     * @return the numberOfLinks
     */
    public Integer getNumberOfLinks() {
        return numberOfLinks;
    }

    /**
     * @param numberOfLinks the numberOfLinks to set
     */
    public void setNumberOfLinks(Integer numberOfLinks) {
        this.numberOfLinks = numberOfLinks;
    }

}
