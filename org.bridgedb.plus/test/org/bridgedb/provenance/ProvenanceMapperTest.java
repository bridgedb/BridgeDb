package org.bridgedb.provenance;

import java.util.Map;
import org.bridgedb.Xref;
import org.bridgedb.DataSource;
import java.util.Collection;
import org.bridgedb.result.URLMapping;
import org.bridgedb.url.*;

import org.bridgedb.IDMapperException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.*;

/**
 *
 * @author Christian
 */
public abstract class ProvenanceMapperTest extends URLMapperTestBase{
    
    //Must be instantiated by implementation of these tests.
    protected static ProvenanceMapper provenaceMapper;
    
    protected final Collection<String> ALL_PROVENACE = new HashSet<String>();
    protected final Collection<DataSource> ALL_TARGET_DATA_SOURCE = new HashSet<DataSource>();
    protected final Collection<String> ALL_TARGET_NAME_SPACES = new HashSet<String>();
    
    @Test
    public void testMapURlManyToManyNoDataSources() throws IDMapperException{
        report("MapURlManyToManyNoDataSources");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(map1URL1);
        sourceURLs.add(map2URL2);
        sourceURLs.add(mapBadURL1);
        Set<URLMapping> results = provenaceMapper.mapURL(sourceURLs, ALL_PROVENACE, ALL_TARGET_NAME_SPACES);
        assertThat(results.size(), greaterThanOrEqualTo(4));
        for (URLMapping result:results){
            assertTrue(result.isValid());
            assertThat(result.getSourceURL(), anyOf(equalTo(map1URL1), equalTo(map2URL2)));
            if (result.getSourceURL().equals(map1URL1)){
                assertThat(result.getTargetURL(), anyOf(equalTo(map1URL1), equalTo(map1URL2), equalTo(map1URL3)));
            }
            if (result.getSourceURL().equals(map2URL2)){
                assertThat(result.getTargetURL(), anyOf(equalTo(map2URL1), equalTo(map2URL2), equalTo(map2URL3)));
            }            
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
      }
    
    @Test
    public void testMapURlOneToManyNoDataSources() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(map1URL1);
        Set<URLMapping> results = provenaceMapper.mapURL(sourceURLs, ALL_PROVENACE, ALL_TARGET_NAME_SPACES);
        assertThat(results.size(), greaterThanOrEqualTo(2));
        for (URLMapping result:results){
            assertTrue(result.isValid());
            assertEquals(result.getSourceURL(), map1URL1);
            assertThat(result.getTargetURL(), anyOf(equalTo(map1URL1), equalTo(map1URL2), equalTo(map1URL3)));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
    }
    
    @Test
    public void testMapURLOneBad() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(mapBadURL1);
        Set<URLMapping> results = provenaceMapper.mapURL(sourceURLs, ALL_PROVENACE, ALL_TARGET_NAME_SPACES);
        assertEquals(0, results.size());
    }

    @Test
    public void testMapURLOneToManyWithOneDataSource() throws IDMapperException{
        report("MapIDOneToManyWithOneDataSource");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(map1URL1);
        HashSet<String> trgNameSpaces = new HashSet<String>();
        trgNameSpaces.add(nameSpace2);
        Set<URLMapping> results = provenaceMapper.mapURL(sourceURLs, ALL_PROVENACE, trgNameSpaces);
        assertEquals(results.size(), 1);
        for (URLMapping result:results){
            assertTrue(result.isValid());
            assertEquals(result.getSourceURL(), map1URL1);
            assertEquals(result.getTargetURL(), map1URL2);
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
    }
 
    @Test
    public void testMapURLOneToManyWithTwoDataSources() throws IDMapperException{
        report("MapIDOneToManyWithTwoDataSources");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(map1URL1);
        HashSet<String> trgNameSpaces = new HashSet<String>();
        trgNameSpaces.add(nameSpace2);
        trgNameSpaces.add(nameSpace3);
        Set<URLMapping> results = provenaceMapper.mapURL(sourceURLs, ALL_PROVENACE, trgNameSpaces);
        assertEquals(results.size(), 2);
        for (URLMapping result:results){
            assertTrue(result.isValid());
            assertEquals(result.getSourceURL(), map1URL1);
            assertThat(result.getTargetURL(), anyOf(equalTo(map1URL2), equalTo(map1URL3)));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
    }
 
    @Test
    public void testMapURLOneProvenance() throws IDMapperException{
        report("MapMapURLOneProvenance");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(map1URL1);
        HashSet<String> provenanceIds = new HashSet<String>();
        provenanceIds.add(link1to2);
        Set<URLMapping> results = provenaceMapper.mapURL(sourceURLs, provenanceIds, ALL_TARGET_NAME_SPACES);
        assertEquals(results.size(), 1);
        for (URLMapping result:results){
            assertTrue(result.isValid());
            assertEquals(map1URL1, result.getSourceURL());
            assertEquals(map1URL2, result.getTargetURL());
            assertEquals(TEST_PREDICATE, result.getPredicate());
        }
    }
    
    @Test
    public void testMapURLTwoProvenance() throws IDMapperException{
        report("MapMapURLTwoProvenance");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(map1URL1);
        HashSet<String> provenanceIds = new HashSet<String>();
        provenanceIds.add(link1to2);
        provenanceIds.add(link1to3);
        Set<URLMapping> results = provenaceMapper.mapURL(sourceURLs, provenanceIds, ALL_TARGET_NAME_SPACES);
        assertEquals(results.size(), 2);
        for (URLMapping result:results){
            assertTrue(result.isValid());
            assertEquals(result.getSourceURL(), map1URL1);
            assertThat(result.getTargetURL(), anyOf(equalTo(map1URL2), equalTo(map1URL3)));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
    }

    @Test
    public void testMapIDManyToManyNoDataSources() throws IDMapperException{
        report("MapIDManyToManyNoDataSources");
        HashSet<Xref> refs = new HashSet<Xref>();
        refs.add(map1xref1);
        refs.add(map2xref2);
        refs.add(mapBadxref1);
        Map<Xref, Set<XrefProvenance>> results = provenaceMapper.mapIDProvenance(refs, ALL_PROVENACE, ALL_TARGET_DATA_SOURCE);
        assertEquals(results.size(), 2);
        Set<XrefProvenance> results1 = results.get(map1xref1);
        assertThat(results1.size(), greaterThanOrEqualTo(2));
        Set<XrefProvenance> results2 = results.get(map2xref2);
        assertThat(results2.size(), greaterThanOrEqualTo(2));
        for (XrefProvenance result:results1){
            assertThat(result.getId(), anyOf(equalTo(map1xref1.getId()), equalTo(map1xref2.getId()), equalTo(map1xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map1xref1.getDataSource()), 
                    equalTo(map1xref2.getDataSource()), equalTo(map1xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
        for (XrefProvenance result:results2){
            assertThat(result.getId(), anyOf(equalTo(map2xref1.getId()), equalTo(map2xref2.getId()), equalTo(map2xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map2xref1.getDataSource()), 
                    equalTo(map2xref2.getDataSource()), equalTo(map2xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
      }
    
    @Test
    public void testMapIDOneToManyNoDataSources() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<XrefProvenance> results = provenaceMapper.mapIDProvenance(map1xref1, ALL_PROVENACE, ALL_TARGET_DATA_SOURCE);
        assertThat(results.size(), greaterThanOrEqualTo(2));
        for (XrefProvenance result:results){
            assertThat(result.getId(), anyOf(equalTo(map1xref1.getId()), equalTo(map1xref2.getId()), equalTo(map1xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map1xref1.getDataSource()), 
                    equalTo(map1xref2.getDataSource()), equalTo(map1xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
    }
    
    @Test
    public void testMapIDOneBad() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        HashSet<String> sourceURLs = new HashSet<String>();
        Set<XrefProvenance> results = provenaceMapper.mapIDProvenance(mapBadxref1, ALL_PROVENACE, ALL_TARGET_DATA_SOURCE);
        assertEquals(0, results.size());
    }

    @Test
    public void testMapIDOneToManyWithOneDataSource() throws IDMapperException{
        report("MapIDOneToManyWithOneDataSource");
        HashSet<DataSource> trgDataSources = new HashSet<DataSource>();
        trgDataSources.add(DataSource2);
        Set<XrefProvenance> results = provenaceMapper.mapIDProvenance(map1xref1, ALL_PROVENACE, trgDataSources);
        assertEquals(results.size(), 1);
        for (XrefProvenance result:results){
            assertEquals(result.getId(), map1xref2.getId());
            assertEquals(result.getDataSource(), map1xref2.getDataSource());
            assertEquals(result.getPredicate(), TEST_PREDICATE);
            assertEquals(link1to2, result.getProvenanceId());
        }
    }
 
    @Test
    public void testMapIDOneToManyWithTwoDataSources() throws IDMapperException{
        report("MapIDOneToManyWithTwoDataSources");
        HashSet<DataSource> trgDataSources = new HashSet<DataSource>();
        trgDataSources.add(DataSource2);
        trgDataSources.add(DataSource3);
        Set<XrefProvenance> results = provenaceMapper.mapIDProvenance(map1xref1, ALL_PROVENACE, trgDataSources);
        assertEquals(results.size(), 2);
        for (XrefProvenance result:results){
            assertThat(result.getId(), anyOf(equalTo(map1xref2.getId()), equalTo(map1xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map1xref2.getDataSource()), equalTo(map1xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
            assertThat(result.getProvenanceId(), anyOf(equalTo(link1to2), equalTo(link1to3)));
        }
    }
 
    @Test
    public void testMapOneProvenanceOneDataSource() throws IDMapperException{
        report("MapMapOneProvenanceOneDataSource");
        HashSet<String> provenanceIds = new HashSet<String>();
        provenanceIds.add(link1to2);
        Set<XrefProvenance> results = provenaceMapper.mapIDProvenance(map1xref1, provenanceIds, ALL_TARGET_DATA_SOURCE);
        assertEquals(1, results.size());
        for (XrefProvenance result:results){
            assertEquals(map1xref2.getId(), result.getId());
            assertEquals(map1xref2.getDataSource(), result.getDataSource());
            assertEquals(TEST_PREDICATE, result.getPredicate());
            assertEquals(link1to2, result.getProvenanceId());
        }
    }
    
    @Test
    public void testMapTwoProvenanceOneDataSource() throws IDMapperException{
        report("MapMapOneProvenanceOneDataSource");
        HashSet<String> sourceURLs = new HashSet<String>();
        sourceURLs.add(map1URL1);
        HashSet<String> provenanceIds = new HashSet<String>();
        provenanceIds.add(link1to2);
        provenanceIds.add(link1to3);
        Set<XrefProvenance> results = provenaceMapper.mapIDProvenance(map1xref1, provenanceIds, ALL_TARGET_DATA_SOURCE);
        assertEquals(results.size(), 2);
        for (XrefProvenance result:results){
            assertThat(result.getId(), anyOf(equalTo(map1xref2.getId()), equalTo(map1xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map1xref2.getDataSource()), equalTo(map1xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
            assertThat(result.getProvenanceId(), anyOf(equalTo(link1to2), equalTo(link1to3)));
        }
    }
    
    //@Test
    //public void testGetProvenance() throws IDMapperException{
    //    report("GetProvenance");
    //    ProvenanceLink result = provenaceMapper.getProvenance(link1to2.getId());
    //    assertEquals(result, link1to2);
    // }
    
}
