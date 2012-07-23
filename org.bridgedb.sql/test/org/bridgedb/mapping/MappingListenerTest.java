package org.bridgedb.mapping;

import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;

/**
 *
 * @author Christian
 */
public abstract class MappingListenerTest extends IDMapperTest{
    
    protected static final String TEST_PREDICATE = "http://www.bridgedb.org/test#testPredicate";
    private static final boolean SYMETRIC = true;
    private static final boolean ORIGINAL = false;
    private static final boolean TRANSATIVE = true;
    
    protected static MappingListener listener;

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
