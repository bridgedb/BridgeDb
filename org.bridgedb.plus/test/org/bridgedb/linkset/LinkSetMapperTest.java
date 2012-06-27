package org.bridgedb.linkset;

import org.bridgedb.linkset.XrefLinkSet;
import org.bridgedb.linkset.LinkSetMapper;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.bridgedb.Xref;
import org.bridgedb.DataSource;
import org.bridgedb.result.URLMapping;
import org.bridgedb.url.*;

import org.bridgedb.IDMapperException;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.*;

/**
 *
 * @author Christian
 */
public abstract class LinkSetMapperTest extends URLMapperTestBase{
    
    //Must be instantiated by implementation of these tests.
    protected static LinkSetMapper linkSetMapper;
    
    protected final List<String> ALL_PROVENACE = new ArrayList<String>();
    protected final List<DataSource> ALL_TARGET_DATA_SOURCE = new ArrayList<DataSource>();
    protected final List<String> ALL_TARGET_NAME_SPACES = new ArrayList<String>();
    
    @Test
    public void testMapURlManyToManyNoDataSources() throws IDMapperException{
        report("MapURlManyToManyNoDataSources");
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(map1URL1);
        sourceURLs.add(map2URL2);
        sourceURLs.add(mapBadURL1);
        Set<URLMapping> results = linkSetMapper.mapURL(sourceURLs, ALL_PROVENACE, ALL_TARGET_NAME_SPACES);
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
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(map1URL1);
        Set<URLMapping> results = linkSetMapper.mapURL(sourceURLs, ALL_PROVENACE, ALL_TARGET_NAME_SPACES);
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
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(mapBadURL1);
        Set<URLMapping> results = linkSetMapper.mapURL(sourceURLs, ALL_PROVENACE, ALL_TARGET_NAME_SPACES);
        assertEquals(0, results.size());
    }

    @Test
    public void testMapURLOneToManyWithOneDataSource() throws IDMapperException{
        report("MapIDOneToManyWithOneDataSource");
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(map1URL1);
        ArrayList<String> trgNameSpaces = new ArrayList<String>();
        trgNameSpaces.add(nameSpace2);
        Set<URLMapping> results = linkSetMapper.mapURL(sourceURLs, ALL_PROVENACE, trgNameSpaces);
        assertEquals(1, results.size());
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
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(map1URL1);
        ArrayList<String> trgNameSpaces = new ArrayList<String>();
        trgNameSpaces.add(nameSpace2);
        trgNameSpaces.add(nameSpace3);
        Set<URLMapping> results = linkSetMapper.mapURL(sourceURLs, ALL_PROVENACE, trgNameSpaces);
        assertEquals(results.size(), 2);
        for (URLMapping result:results){
            assertTrue(result.isValid());
            assertEquals(result.getSourceURL(), map1URL1);
            assertThat(result.getTargetURL(), anyOf(equalTo(map1URL2), equalTo(map1URL3)));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
    }
 
    @Test
    public void testMapURLOneLinkSet() throws IDMapperException{
        report("MapMapURLOneLinkSet");
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(map1URL1);
        ArrayList<String> linkSetIds = new ArrayList<String>();
        linkSetIds.add(link1to2);
        Set<URLMapping> results = linkSetMapper.mapURL(sourceURLs, linkSetIds, ALL_TARGET_NAME_SPACES);
        assertEquals(results.size(), 1);
        for (URLMapping result:results){
            assertTrue(result.isValid());
            assertEquals(map1URL1, result.getSourceURL());
            assertEquals(map1URL2, result.getTargetURL());
            assertEquals(TEST_PREDICATE, result.getPredicate());
        }
    }
    
    @Test
    public void testMapURLTwoLinkSet() throws IDMapperException{
        report("MapMapURLTwoLinkSet");
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(map1URL1);
        ArrayList<String> linkSetIds = new ArrayList<String>();
        linkSetIds.add(link1to2);
        linkSetIds.add(link1to3);
        Set<URLMapping> results = linkSetMapper.mapURL(sourceURLs, linkSetIds, ALL_TARGET_NAME_SPACES);
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
        ArrayList<Xref> refs = new ArrayList<Xref>();
        refs.add(map1xref1);
        refs.add(map2xref2);
        refs.add(mapBadxref1);
        Map<Xref, Set<XrefLinkSet>> results = linkSetMapper.mapIDwithLinkSet(refs, ALL_PROVENACE, ALL_TARGET_DATA_SOURCE);
        assertEquals(results.size(), 2);
        Set<XrefLinkSet> results1 = results.get(map1xref1);
        assertThat(results1.size(), greaterThanOrEqualTo(2));
        Set<XrefLinkSet> results2 = results.get(map2xref2);
        assertThat(results2.size(), greaterThanOrEqualTo(2));
        for (XrefLinkSet result:results1){
            assertThat(result.getId(), anyOf(equalTo(map1xref1.getId()), equalTo(map1xref2.getId()), equalTo(map1xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map1xref1.getDataSource()), 
                    equalTo(map1xref2.getDataSource()), equalTo(map1xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
        for (XrefLinkSet result:results2){
            assertThat(result.getId(), anyOf(equalTo(map2xref1.getId()), equalTo(map2xref2.getId()), equalTo(map2xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map2xref1.getDataSource()), 
                    equalTo(map2xref2.getDataSource()), equalTo(map2xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
      }
    
    @Test
    public void testMapIDOneToManyNoDataSources() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        Set<XrefLinkSet> results = linkSetMapper.mapIDwithLinkSet(map1xref1, ALL_PROVENACE, ALL_TARGET_DATA_SOURCE);
        assertThat(results.size(), greaterThanOrEqualTo(2));
        for (XrefLinkSet result:results){
            assertThat(result.getId(), anyOf(equalTo(map1xref1.getId()), equalTo(map1xref2.getId()), equalTo(map1xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map1xref1.getDataSource()), 
                    equalTo(map1xref2.getDataSource()), equalTo(map1xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
        }
    }
    
    @Test
    public void testMapIDOneBad() throws IDMapperException{
        report("MapIDOneToManyNoDataSources");
        ArrayList<String> sourceURLs = new ArrayList<String>();
        Set<XrefLinkSet> results = linkSetMapper.mapIDwithLinkSet(mapBadxref1, ALL_PROVENACE, ALL_TARGET_DATA_SOURCE);
        assertEquals(0, results.size());
    }

    @Test
    public void testMapIDOneToManyWithOneDataSource() throws IDMapperException{
        report("MapIDOneToManyWithOneDataSource");
        ArrayList<DataSource> trgDataSources = new ArrayList<DataSource>();
        trgDataSources.add(DataSource2);
        Set<XrefLinkSet> results = linkSetMapper.mapIDwithLinkSet(map1xref1, ALL_PROVENACE, trgDataSources);
        assertEquals(results.size(), 1);
        for (XrefLinkSet result:results){
            assertEquals(result.getId(), map1xref2.getId());
            assertEquals(result.getDataSource(), map1xref2.getDataSource());
            assertEquals(result.getPredicate(), TEST_PREDICATE);
            assertEquals(link1to2, result.getLinkSetId());
        }
    }
 
    @Test
    public void testMapIDOneToManyWithTwoDataSources() throws IDMapperException{
        report("MapIDOneToManyWithTwoDataSources");
        ArrayList<DataSource> trgDataSources = new ArrayList<DataSource>();
        trgDataSources.add(DataSource2);
        trgDataSources.add(DataSource3);
        Set<XrefLinkSet> results = linkSetMapper.mapIDwithLinkSet(map1xref1, ALL_PROVENACE, trgDataSources);
        assertEquals(results.size(), 2);
        for (XrefLinkSet result:results){
            assertThat(result.getId(), anyOf(equalTo(map1xref2.getId()), equalTo(map1xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map1xref2.getDataSource()), equalTo(map1xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
            assertThat(result.getLinkSetId(), anyOf(equalTo(link1to2), equalTo(link1to3)));
        }
    }
 
    @Test
    public void testMapOneLinkSetOneDataSource() throws IDMapperException{
        report("MapMapOnelinkSetOneDataSource");
        ArrayList<String> linkSetIds = new ArrayList<String>();
        linkSetIds.add(link1to2);
        Set<XrefLinkSet> results = linkSetMapper.mapIDwithLinkSet(map1xref1, linkSetIds, ALL_TARGET_DATA_SOURCE);
        assertEquals(1, results.size());
        for (XrefLinkSet result:results){
            assertEquals(map1xref2.getId(), result.getId());
            assertEquals(map1xref2.getDataSource(), result.getDataSource());
            assertEquals(TEST_PREDICATE, result.getPredicate());
            assertEquals(link1to2, result.getLinkSetId());
        }
    }
    
    @Test
    public void testMapTwoLinkSetOneDataSource() throws IDMapperException{
        report("MapMapOneLinkSetOneDataSource");
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(map1URL1);
        ArrayList<String> linkSetIds = new ArrayList<String>();
        linkSetIds.add(link1to2);
        linkSetIds.add(link1to3);
        Set<XrefLinkSet> results = linkSetMapper.mapIDwithLinkSet(map1xref1, linkSetIds, ALL_TARGET_DATA_SOURCE);
        assertEquals(results.size(), 2);
        for (XrefLinkSet result:results){
            assertThat(result.getId(), anyOf(equalTo(map1xref2.getId()), equalTo(map1xref3.getId())));
            assertThat(result.getDataSource(), anyOf(equalTo(map1xref2.getDataSource()), equalTo(map1xref3.getDataSource())));
            assertEquals(result.getPredicate(), TEST_PREDICATE);
            assertThat(result.getLinkSetId(), anyOf(equalTo(link1to2), equalTo(link1to3)));
        }
    }
        
}
