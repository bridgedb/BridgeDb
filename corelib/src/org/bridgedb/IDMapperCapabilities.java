// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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

package org.bridgedb;

import java.util.Set;

/**
 * Describes capabilities of an IDMapper.
 */
public interface IDMapperCapabilities {
    // get all supported organisms
    //TODO: to modify--better to keep the bio concept Organism in a high level
    //public Set<Organism> getSupportedOrganisms();

    /**
     *
     * @return true if free text search is supported, false otherwise.
     */
    public boolean isFreeSearchSupported();

    /**
     *
     * @return supported source ID types
     * @throws IDMapperException if supported DataSources 
     * 	could not be determined because of service unavailability.
     */
    public Set<DataSource>  getSupportedSrcDataSources() throws IDMapperException;

    /**
     *
     * @return supported target ID types
     * @throws IDMapperException if supported DataSources 
     * 	could not be determined because of service unavailability.
     */
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException;
}
