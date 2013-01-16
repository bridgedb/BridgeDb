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
package org.bridgedb.mapping;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

/**
 * This interface is used to load Data into the System using Xrefs and DataSources.
 * @author Christian
 */
public interface MappingListener {
    
    /**
     * Loads all the meta data for a Mapping Set.
     * @param source The SysCode of DataSource part of the source xref/url
     * @param predicate The predicate to be associated. Can be null
     * @param target The SysCode of DataSource part of the target xref/url
     * @param symetric Flag to say if mapping should be loaded one way of both ways. 
     *     Creates two mapping sets this one and the inverse with one number higher.
     * @param transative Flag to indicate if the mapping was created using transativity
     * @return Id of the forward mappingSet.
     * @throws IDMapperException 
     */
    public int registerMappingSet(DataSource source, String predicate, DataSource target, 
            boolean symetric, boolean transative) throws IDMapperException;
    
    /**
     * Inserts a mapping into the system.
     * <p>
     * Implementatins may buffer the inserts, so closeInput must be called after the last insert or inserts may be lost. 
     * <p>
     * No checking can be done that the DataSource part of the Xrefs match the declaration.
     * <p>
     * For speed, the implemented methods is also not required to check that the MappingsSet exists 
     * nor that the mapping Set's semantic setting matches that in the insert call. 
     * 
     * @param sourceId ID of the source xref
     * @param targetId ID of the target xref
     * @param mappingSet The ID of the mapping set to be inserted into.
     * @param symetric If true the inverse mapping will be inserted into the mapping set one number higher.
     * @throws IDMapperException 
     */
    public void insertLink(String sourceId, String targetId, int mappingSet, boolean symetric) throws IDMapperException;

    /**
     * Closes the input, flushing any links into storage.
     * <p>
     * May also update any cashed counts ext.
     * <p>
     * This method and URLListener method are intended to be duplicates of each other.
     * @throws IDMapperException 
     */
    public void closeInput()throws IDMapperException;
}
