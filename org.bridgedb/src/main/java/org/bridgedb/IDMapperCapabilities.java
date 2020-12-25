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
package org.bridgedb;

import java.util.Set;

/**
 * Describes capabilities of an IDMapper.
 * Free form key-value properties are used for version info etc.
 */
public interface IDMapperCapabilities {

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

    /**
     *
     * @param src source data source
     * @param tgt target data source
     * @return true if mapping is supported from src to des
     * @throws IDMapperException if service is unavailable
     */
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException;
    
    /**
     * Return a value for a property, or null if this property is not defined.
     * Implementations are free to choose a set of properties. Suggested properties:
     * <ul>
     * <li>Version
     * <li>DateCreated
     * <li>SourceDatabase
     * <li>SourceDatabaseVersion
     * <li>Species
     * </ul>
     * @param key key
     * @return property string
     */
    public String getProperty(String key);
    
    /**
     * @return the keys of all properties. 
     * Implementations may return an empty set but never null.
     */
    public Set<String> getKeys();
}
