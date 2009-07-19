// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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

import java.util.Map;
import java.util.Set;

/**
 * Base interface for all id mapping methods.
 * Has methods for basic functionality such as looking up cross-references and backpage text.
 */
public interface IDMapper {

	/**
	 * Get all cross-references for a set of entities, restricting the
	 * result to contain only references from the given set of data sources.
     * Supports one-to-one mapping and one-to-many mapping.
     * @param srcXrefs source Xref, containing ID and ID type/data source
     * @param tgtDataSources target ID types/data sources. Set this to null
     *   if you want to retrieve all results.
     * @return a map from source Xref to target Xref's. The map is not guaranteed
     *    to contain a result for each srcXrefs you pass in. This method will never
     *    return null however.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
     */
    public Map<Xref, Set<Xref>> mapID(Set<Xref> srcXrefs, Set<DataSource> tgtDataSources) throws IDMapperException;

	/**
	 * Get all cross-references for the given entity, restricting the
	 * result to contain only references from the given set of data sources.
	 * @param ref the entity to get cross-references for. 
     * @param tgtDataSources target ID types/data sources. Set this to null if you 
     *   want to retrieve all results.
	 * @return A Set containing the cross references, or an empty
	 * Set when no cross references could be found. This method does not return null.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Set<Xref> mapID (Xref ref, Set<DataSource> tgtDataSources) throws IDMapperException;
	
    /**
     * Check whether an Xref exists.
     * @param xref reference to check
     * @return if the reference exists, false if not
     * @throws IDMapperException if failed
     */
    public boolean xrefExists(Xref xref) throws IDMapperException;

    /**
     * free text search for matching symbols or identifiers.
     * @param text text to search
     * @param limit up limit of number of hits
     * @return a set of hit references
     * @throws IDMapperException if failed
     */
    public Set<Xref> freeSearch (String text, int limit) throws IDMapperException;

    /**
     *
     * @return capacities of the ID mapper
     */
    public IDMapperCapabilities getCapabilities();
    
    /**
     * dispose any resources (such as open database connections) associated
     * with this IDMapper.
     * @throws IDMapperException if the associated resources could not be freed.
     */
    public void close() throws IDMapperException;
    
    /**
     * Use this method to check if the IDMapper is still valid.
     * @return false after the close() method is called on this object, true otherwise 
     */
    public boolean isConnected();
}
