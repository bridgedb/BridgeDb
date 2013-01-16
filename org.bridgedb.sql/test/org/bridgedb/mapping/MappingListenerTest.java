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

import org.bridgedb.IDMapperException;
import org.bridgedb.utils.IDMapperTest;

/**
 * Extends the IDMapper Tests with a method to load the test data before running the tests.
 *
 * @author Christian
 */
public abstract class MappingListenerTest extends IDMapperTest{
    
    protected static final String TEST_PREDICATE = "http://www.bridgedb.org/test#testPredicate";
    private static final boolean SYMETRIC = true;
    private static final boolean ORIGINAL = false;
    private static final boolean TRANSATIVE = true;
    
    protected static MappingListener listener;

    /**
     * Method to load the test data.
     *
     * Should be called by a beforeClass method but only after it has set listener.
     * 
     * @throws IDMapperException
     */
    public static void loadData() throws IDMapperException{
        int mappingSet = listener.registerMappingSet(DataSource1, TEST_PREDICATE, DataSource2, SYMETRIC, ORIGINAL);
        listener.insertLink(map1xref1.getId(), map1xref2.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map2xref1.getId(), map2xref2.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map3xref1.getId(), map3xref2.getId(), mappingSet, SYMETRIC);
        mappingSet = listener.registerMappingSet(DataSource2, TEST_PREDICATE, DataSource3, SYMETRIC, ORIGINAL);
        listener.insertLink(map1xref2.getId(), map1xref3.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map2xref2.getId(), map2xref3.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map3xref2.getId(), map3xref3.getId(), mappingSet, SYMETRIC);
        mappingSet = listener.registerMappingSet(DataSource1, TEST_PREDICATE, DataSource3, SYMETRIC, TRANSATIVE);
        listener.insertLink(map1xref1.getId(), map1xref3.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map2xref1.getId(), map2xref3.getId(), mappingSet, SYMETRIC);
        listener.insertLink(map3xref1.getId(), map3xref3.getId(), mappingSet, SYMETRIC);
        listener.closeInput();
    }

}
