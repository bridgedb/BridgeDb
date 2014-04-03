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
package org.bridgedb.statistics;

/**
 * Holder class for the main Meta Data of MappingSet.
 *
 * Does not include everything in the void header but only what is captured in the SQL.
 * @author Christian
 */
public class SourceTargetInfo {
    private final DataSetInfo source;
    private final DataSetInfo target;
    private final Integer numberOfLinksets;
    private final Integer numberOfLinks;
 
    public SourceTargetInfo(DataSetInfo source, DataSetInfo target, Integer numberOfLinksets, Integer numberOfLinks){
        this.source = source;
        this.target = target;
        this.numberOfLinksets = numberOfLinksets;
        this.numberOfLinks = numberOfLinks;
    }
    
    /**
     * @return the source
     */
    public DataSetInfo getSource() {
        return source;
    }

    /**
     * @return the target
     */
    public DataSetInfo getTarget() {
        return target;
    }

    /**
     * @return the numberOfLinks
     */
    public Integer getNumberOfLinks() {
        return numberOfLinks;
    }
    
    /**
     * @return the numberOfLinksets
     */
    public Integer getNumberOfLinksets() {
        return numberOfLinksets;
    }

    @Override
    public String toString(){
        return "source:" + this.getSource() 
                + " -> Target: " + this.getTarget()
                + "\n\tnumberOfLinksets: " + this.getNumberOfLinksets()
                + "\n\tnumberOfLinks: " + this.getNumberOfLinks()
                 + "\n";
    }

 }
