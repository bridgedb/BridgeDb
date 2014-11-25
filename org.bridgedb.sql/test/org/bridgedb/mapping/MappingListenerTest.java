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

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.IDMapperTest;

/**
 * Extends the IDMapper Tests with a method to load the test data before running the tests.
 *
 * @author Christian
 */
public abstract class MappingListenerTest extends IDMapperTest{
    
    protected static final boolean SYMETRIC = true;
    protected static final Set<Integer> NO_CHAIN = null;
    
    protected static MappingListener listener;

    /**
     * Method to load the test data.
     *
     * Should be called by a beforeClass method but only after it has set listener.
     * 
     * @throws BridgeDBException
     */
    public static void loadData() throws BridgeDBException{
        System.out.println("Old loadData called!");
        int mappingSet = listener.registerMappingSet(DataSource1, DataSource2, SYMETRIC);
        listener.insertLink(map1xref1.getId(), map1xref2.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map2xref1.getId(), map2xref2.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map3xref1.getId(), map3xref2.getId(), mappingSet, SYMETRIC);
        HashSet<String> via = new HashSet<String>();
        HashSet<Integer> chain = new HashSet<Integer>();
        mappingSet = listener.registerMappingSet(DataSource2, DataSource3, SYMETRIC);
        listener.insertLink(map1xref2.getId(), map1xref3.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map2xref2.getId(), map2xref3.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map3xref2.getId(), map3xref3.getId(), mappingSet, SYMETRIC);
        via.add("test via");
        chain.add(1);
        mappingSet = listener.registerMappingSet(DataSource1, DataSource3, SYMETRIC);
        listener.insertLink(map1xref1.getId(), map1xref3.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map2xref1.getId(), map2xref3.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map3xref1.getId(), map3xref3.getId(), mappingSet, SYMETRIC);
        listener.closeInput();
    }

}
