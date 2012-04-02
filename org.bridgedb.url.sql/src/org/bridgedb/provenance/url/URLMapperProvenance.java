// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
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
package org.bridgedb.provenance.url;

import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.provenance.DataVersion;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.url.URLMapper;

/**
 * Base interface for all URL mapping methods with Provenance.
 * Has methods for basic functionality such as looking up cross-references and backpage text.
 * 
 * Similar to the IDMapper interface except treats URLs as first class citizens.
 * To keep code size small URLs are represented as Strings.
 */
public interface URLMapperProvenance extends URLMapper {

	/**
	 * Get all cross-references for the given entity, restricting the
	 * result to contain only references from the given set of name spaces.
     * 
     * Similar to the mapID method in IDMapper.
     * 
	 * @param ref the entity to get cross-references for. 
     * @param tgtNameSpaces target name spaces (prefix) that can be included in the resulst. Set this to null
     *   if you want to retrieve all results.
	 * @return A Set containing the URLMapping(s), or an empty
	 * Set when no cross references could be found. This method does not return null.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Set<URLMapping> getURLMappings (String ref, String... tgtNameSpaces) throws IDMapperException;
	
    public URLMapping getMapping(int id) throws IDMapperException;
        
    public ProvenanceStatistics getProvenance(int id) throws IDMapperException;
    
    public ProvenanceStatistics getProvenanceByPosition(int position) throws IDMapperException;

    public List<ProvenanceStatistics> getProvenanceByPosition(int position, int limit) throws IDMapperException;

    public Set<ProvenanceStatistics> getSourceProvenanceByNameSpace(String nameSpace) throws IDMapperException;

    public Set<ProvenanceStatistics> getTargetProvenanceByNameSpace(String nameSpace) throws IDMapperException;

    public DataSourceStatistics getDataSourceStatistics(DataSource dataSource) throws IDMapperException;
    
    public DataSourceStatistics getDataSourceStatisticsByPosition(int position) throws IDMapperException;

    public List<DataSourceStatistics> getDataSourceStatisticsByPosition(int position, int limit) throws IDMapperException;
        
    //totest
    public DataVersion getDataVersion(int id) throws IDMapperException;
    
    //totest
    public DataVersion getDataVersionByPosition(int position) throws IDMapperException;

    //totest
    public List<DataVersion> getDataVersionByPosition(int position, int limit) throws IDMapperException;
    
    //totest
    public List<DataVersion> getDataVersionByNameSpace(String nameSpace) throws IDMapperException;
    
    //totest
    public MapperStatistics getMapperStatistics() throws IDMapperException;
    
}
