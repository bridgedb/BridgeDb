package org.bridgedb.mapping;

import org.bridgedb.mapping.MappingListener;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTestBase;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public abstract class MappingListenerTest extends IDMapperTestBase{
    
    protected static final String TEST_PREDICATE = "http://www.bridgedb.org/test#testPredicate";
    private static final boolean SYMETRIC = true;
    private static final boolean ORIGINAL = false;
    private static final boolean TRANSATIVE = true;
    
    protected static MappingListener listener;

    @Test
    public void testLinkListener() throws IDMapperException{
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
