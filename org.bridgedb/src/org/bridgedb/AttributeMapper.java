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
	 * Get all attributes for an entity. 
	 * Usually this method is more efficient if you want to query several attributes in a sequence.
	 * @param ref the entity to get the attributes for
	 * @return a Map where attribute names are the keys and attribute values are the values. 
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Map<String, Set<String>> getAttributes(Xref ref) throws IDMapperException;

        /**
         *
         * @return true if free attribute search is supported, false otherwise.
         */
        public boolean isFreeAttributeSearchSupported();

	/**
	 * free text search for matching symbols.
	 * @return map references and attribute values that match the query
	 * @param query The text to search for
	 * @param attrType the attribute to look for, e.g. 'Symbol' or 'Description'. 
	 * 	If you use the special MATCH_ID constant, it will query the identifier instead.
	 * @param limit The number of results to limit the search to
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Map<Xref, String> freeAttributeSearch (String query, String attrType, int limit) throws IDMapperException;

	/** use this magic constant as the attrType parameter to also search for identifiers. */
	public static final String MATCH_ID = "org.bridgedb.MATCH_ID";

	/**
	 * Set of attributes provided by this AttributeMapper.
	 * There is no guarantee that a specific Xref has these attributes.
	 * @return set of available attributes in this AttributeMapper. 
	 *    If there are none available, returns an empty set.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Set<String> getAttributeSet() throws IDMapperException;
}
