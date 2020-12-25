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

import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.utils.BridgeDBException;


/**
 * This interface is used to load Data into the System using Xrefs and DataSources.
 * @author Christian
 */
public interface MappingListener {
    
    /**
     * Loads all the meta data for a Mapping Set.
     * 
     * @param source The SysCode of DataSource part of the source xref/url
     * @param target The SysCode of DataSource part of the target xref/url
     * @param symetric Flag to say if mapping should be loaded one way of both ways. 
     *     Creates two mapping sets this one and the inverse with one number higher.
     * @return Id of the forward mappingSet.
     * @throws BridgeDBException If something goes wrong. 
     * Always thrown by the URI based version as it requires extra parameters to register a mapping Set. 
     */
    public int registerMappingSet(DataSource source, DataSource target, boolean symetric) throws BridgeDBException;
    
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
     * @throws BridgeDBException 
     */
    public void insertLink(String sourceId, String targetId, int mappingSet, boolean symetric) throws BridgeDBException;

    /**
     * Closes the input, flushing any links into storage.
     * <p>
     * May also update any cashed counts ext.
     * <p>
     * This method and URLListener method are intended to be duplicates of each other.
     * @throws BridgeDBException 
     */
    public void closeInput()throws BridgeDBException;
}
