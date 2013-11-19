// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
// Copyright 2012-2103 Christian Brenninkmeijer
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

import java.util.HashSet;
import java.util.Iterator;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
@Ignore
public abstract class XrefIteratorTest extends IDMapperTestBase{
    //Must be set by supclasses.
    protected static XrefIterator XrefIterator;
    
    @Test
    public void TestAllDataSourcesIterator() throws IDMapperException{
        report("TestAllDataSourcesIterator");
        Iterable<Xref> iterable = XrefIterator.getIterator();
        Iterator<Xref> iterator = iterable.iterator();
        HashSet<Xref> asSet = new HashSet<Xref>();
        while (iterator.hasNext()){
            asSet.add(iterator.next());
        }
        assertTrue(asSet.contains(map1xref1));
        assertTrue(asSet.contains(map1xref3));
        assertTrue(asSet.contains(map2xref2));
        assertTrue(asSet.contains(map3xref3));
        assertFalse(asSet.contains(mapBadxref1));
        assertFalse(asSet.contains(mapBadxref2));
        assertFalse(asSet.contains(mapBadxref3));
    }

    @Test
    public void TestOneDataSourcesIterator() throws IDMapperException{
        report("TestOneDataSourcesIterator");
        Iterable<Xref> iterable = XrefIterator.getIterator(DataSource2);
        Iterator<Xref> iterator = iterable.iterator();
        HashSet<Xref> asSet = new HashSet<Xref>();
        while (iterator.hasNext()){
            asSet.add(iterator.next());
        }
        assertFalse(asSet.contains(map1xref1));
        assertFalse(asSet.contains(map1xref3));
        assertTrue(asSet.contains(map1xref2));
        assertTrue(asSet.contains(map2xref2));
        assertTrue(asSet.contains(map3xref2));
        assertFalse(asSet.contains(map3xref1));
        assertFalse(asSet.contains(map3xref3));
        assertFalse(asSet.contains(mapBadxref1));
        assertFalse(asSet.contains(mapBadxref2));
        assertFalse(asSet.contains(mapBadxref3));
    }

}
