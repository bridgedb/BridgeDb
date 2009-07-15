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
 * AttributeMapper knows about attributes for Xrefs.
 */
public interface AttributeMapper
{
	/**
	 * Get attributes for an entity, such as gene Symbol.
	 * @param ref the entity to get the attribute for
	 * @param attrType the attribute to look for, e.g. 'Symbol' or 'Description'.
	 * @return the attribute, or null if nothing was found
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Set<String> getAttributes(Xref ref, String attrType) throws IDMapperException;
	
	/**
	 * free text search for matching symbols.
	 * @return references that match the query
	 * @param query The text to search for
	 * @param attrType the attribute to look for, e.g. 'Symbol' or 'Description'.
	 * @param limit The number of results to limit the search to
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Set<Xref> freeAttributeSearch (String query, String attrType, int limit) throws IDMapperException;	
}
