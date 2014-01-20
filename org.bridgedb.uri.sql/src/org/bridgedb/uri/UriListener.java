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
package org.bridgedb.uri;

import java.util.Set;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;

/**
 * Import Interface that supports the adding of Uris.
 * <p>
 * This is similar to MappingListener allowing UriSpaces and Uris to be used directly.
 * But also with added support for multiple UriSpaces version like Identifiers.org does.
 * 
 * @author Christian
 */
public interface UriListener {
        
    /**
     * Registers a mapping set using UriSpaces rather than DataSoucres.
     * <p>
     * Similar to MappingListener.registerMappingSet(DataSource source, String predicate, DataSource target, 
     *    boolean symetric, boolean isTransitive)
     * In fact implementations are encouraged to obtain the DataSources and call the MappingListener method.
     * @param sourceUriPattern A registered UriPattern used by the source UriS
     * @param predicate The predicate to be associated. Can be null
     * @param justification The URI that states why the link holds. Can be null
     * @param targetUriPattern A registered UriPattern used by the source UriS
     * @param symetric Flag to say if mapping should be loaded one way of both ways. 
     *     Creates two mapping sets this one and the inverse with one number higher.
     * @param transative Flag to indicate if the mapping was created using transativity
     * @return Id of the forward mappingSet.
     * @throws BridgeDBException Thrown if either UriSpace has not previously been registered using registerUriSpace
     */
   public int registerMappingSet(RegexUriPattern sourceUriPattern, String predicate, String justification, 
           RegexUriPattern targetUriPattern, Resource mappingResource, Resource mappingSource, boolean symetric, 
           Set<String> viaLabels, Set<Integer> chainedLinkSets) throws  BridgeDBException;

   public RegexUriPattern toUriPattern(String uri) throws BridgeDBException;
            
    /**
     * Inserts a mapping into the system.
     * <p>
     * Similar to MappingListener.insertLink(String sourceId, String targetId, int mappingSet, boolean symetric)
     * In fact implementations are encouraged to obtain the ids and call the MappingListener method.
     * <p>
     * For speed the implementations are not required to verify that the UriSpace part of the Uris matches the de
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
   public void insertUriMapping(String sourceUri, String targetUri, int mappingSet, boolean symetric) 
            throws BridgeDBException;
    
    /**
     * Closes the input, flushing any links into storage.
     * <p>
     * May also update any cashed counts ext.
     * <p>
     * This method and MappingListener method are intended to be duplicates of each other.
     * A single actual method can implement closeInput() for both interfaces. 
     * @throws BridgeDBException 
     */
   public void closeInput() throws BridgeDBException;
   
   public void recover() throws BridgeDBException;

}
