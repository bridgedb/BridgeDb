package org.bridgedb.url;

import org.bridgedb.provenance.ProvenanceFactoryTest;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.url.ProvenanceStatistics;
import org.bridgedb.provenance.url.DataSourceStatistics;
import java.util.List;
import org.bridgedb.DataSource;
import org.junit.Ignore;
import org.bridgedb.provenance.url.URLMapperProvenance;
import org.bridgedb.provenance.url.URLMapping;
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
public abstract class URLMapperProvenanceTest extends URLMapperTest{
            
    protected static URLMapperProvenance urlMapperProvenance;
               
    @Test
    public void testMapURLMappingOneToManyNoDataSources() throws IDMapperException{
        System.out.println("MapURLMappingOneToManyNoDataSources");
        Set<URLMapping> results = urlMapperProvenance.getURLMappings(map1URL1);
        Set<String> targets = new HashSet<String>();
        for (URLMapping mapping:results){
            assertEquals(ProvenanceFactoryTest.TEST_PREDICATE, mapping.getProvenance().getPredicate());
            assertEquals(map1URL1, mapping.getSourceURL());
            targets.add(mapping.getTargetURL());
        }
        assertTrue(targets.contains(map1URL2));
        assertTrue(targets.contains(map1URL3));
        assertFalse(targets.contains(map2URL1));
        assertFalse(targets.contains(map2URL2));
        assertFalse(targets.contains(map2URL2));
    }
    
    @Test
    public void testMapURLMappingOneBad() throws IDMapperException{
        System.out.println("MapURLMappingOneToManyNoDataSources");
        Set<URLMapping> results = urlMapperProvenance.getURLMappings(mapBadURL1);
        assertEquals(0, results.size());
    }

    @Test
    public void testMapURLMappingOneToManyWithOneDataSource() throws IDMapperException{
        System.out.println("MapURLMappingOneToManyWithOneDataSource");
        Set<URLMapping> results = urlMapperProvenance.getURLMappings(map1URL1, nameSpace2);
        Set<String> targets = new HashSet<String>();
        for (URLMapping mapping:results){
            assertEquals(ProvenanceFactoryTest.TEST_PREDICATE, mapping.getProvenance().getPredicate());
            assertEquals(nameSpace2, mapping.getProvenance().getTarget().getNameSpace());
            assertEquals(map1URL1, mapping.getSourceURL());
            targets.add(mapping.getTargetURL());
        }
        assertTrue(targets.contains(map1URL2));
        assertFalse(targets.contains(map1URL3));
        assertFalse(targets.contains(map2URL1));
        assertFalse(targets.contains(map2URL2));
        assertFalse(targets.contains(map2URL2));
    }
 
    @Test
    public void testMapURLMappingOneToManyWithTwoDataSources() throws IDMapperException{
        System.out.println("MapURLMappingOneToManyWithTwoDataSources");
        Set<URLMapping> results = urlMapperProvenance.getURLMappings(map1URL1, nameSpace2, nameSpace3);
        Set<String> targets = new HashSet<String>();
        for (URLMapping mapping:results){
            assertEquals(ProvenanceFactoryTest.TEST_PREDICATE, mapping.getProvenance().getPredicate());
            assertThat(mapping.getProvenance().getTarget().getNameSpace(), anyOf(equalTo(nameSpace2), equalTo(nameSpace3)));
            assertEquals(map1URL1, mapping.getSourceURL());
            targets.add(mapping.getTargetURL());
        }
        assertTrue(targets.contains(map1URL2));
        assertTrue(targets.contains(map1URL3));
        assertFalse(targets.contains(map2URL1));
        assertFalse(targets.contains(map2URL2));
        assertFalse(targets.contains(map2URL2));
    } 
    
    @Test
    public void testGetMapping() throws IDMapperException{
        System.out.println("testGetMapping");
        Set<URLMapping> mappings = urlMapperProvenance.getURLMappings(map1URL1, nameSpace2);
        int id = mappings.iterator().next().getId();
        URLMapping result = urlMapperProvenance.getMapping(id);
        assertEquals(id, result.getId());
        assertEquals(map1URL1, result.getSourceURL());
        assertEquals(nameSpace2, DataSource.uriToXref(result.getTargetURL()).getDataSource().getNameSpace());
        assertEquals(ProvenanceFactoryTest.TEST_PREDICATE, result.getProvenance().getPredicate());
    }
    
    @Test
    public void testGetProvenance() throws IDMapperException{
        System.out.println("testGetProvenance");
        Set<URLMapping> mappings = urlMapperProvenance.getURLMappings(map1URL1, nameSpace2);
        int id = mappings.iterator().next().getProvenance().getId();
        ProvenanceStatistics result = urlMapperProvenance.getProvenance(id);
        assertEquals(result.getProvenance().getSource().getNameSpace(), 
                DataSource.uriToXref(map1URL1).getDataSource().getNameSpace());
        assertEquals(ProvenanceFactoryTest.TEST_PREDICATE, result.getProvenance().getPredicate());
        assertEquals(result.getProvenance().getTarget().getNameSpace(), nameSpace2); 
        assertThat(result.getNumberOfMappings(), greaterThanOrEqualTo(3));
    }
        
    @Test
    public void testGetProvenanceByPosition() throws IDMapperException{
        System.out.println("testGetProvenanceByPosition");
        ProvenanceStatistics result0 = urlMapperProvenance.getProvenanceByPosition(0);
        assertNotNull(result0);
        ProvenanceStatistics result1 = urlMapperProvenance.getProvenanceByPosition(1);
        assertThat(result1, not(result0));
        ProvenanceStatistics result2 = urlMapperProvenance.getProvenanceByPosition(2);
        assertThat(result2, not(result0));
        assertThat(result2, not(result1));
    }

    @Test
    public void testProvenancebyPositionAndLimit() throws IDMapperException{
        System.out.println("testProvenancebyPositionAndLimit");
        List<ProvenanceStatistics> results1 = urlMapperProvenance.getProvenanceByPosition(0, 2);
        assertEquals(2, results1.size());
        //Only 1 in the seocnd ones as there may be only 3 DataSources
        List<ProvenanceStatistics> results2 = urlMapperProvenance.getProvenanceByPosition(2, 1);
        assertEquals(1, results2.size());
        for (ProvenanceStatistics stats: results2){
            assertFalse(results1.contains(stats));
        }
    }
    
    @Test
    public void testGetDataSourceStatistics() throws IDMapperException{
        System.out.println("testGetDataSourceStatistics");
        DataSourceStatistics result = urlMapperProvenance.getDataSourceStatistics(DataSource1);
        assertEquals(DataSource1, result.dataSource);
        assertThat(result.numberOfSourceMappings, greaterThanOrEqualTo(3));
        assertThat(result.numberOfTargetMappings, greaterThanOrEqualTo(3));
        assertThat(result.numberOfSourceProvenances, greaterThanOrEqualTo(3));
        assertThat(result.numberOfTargetProvenances, greaterThanOrEqualTo(3));
    }
    
    @Test
    public void testGetDataSourceStatisticsByPosition() throws IDMapperException{
        System.out.println("testGetDataSourceStatisticsByPosition");
        DataSourceStatistics result0 = urlMapperProvenance.getDataSourceStatisticsByPosition(0);
        assertNotNull(result0);
        DataSourceStatistics result1 = urlMapperProvenance.getDataSourceStatisticsByPosition(1);
        assertThat(result1, not(result0));
        DataSourceStatistics result2 = urlMapperProvenance.getDataSourceStatisticsByPosition(2);
        assertThat(result2, not(result0));
        assertThat(result2, not(result1));
    }

    @Test
    public void testDataSourceStatisticsbyPositionAndLimit() throws IDMapperException{
        System.out.println("testDataSourceStatisticsbyPositionAndLimit");
        List<DataSourceStatistics> results1 = urlMapperProvenance.getDataSourceStatisticsByPosition(0, 2);
        assertEquals(2, results1.size());
        //Only 1 in the seocnd ones as there may be only 3 DataSources
        List<DataSourceStatistics> results2 = urlMapperProvenance.getDataSourceStatisticsByPosition(2, 1);
        assertEquals(1, results2.size());
        for (DataSourceStatistics DataSourceStatistics: results2){
            assertFalse(results1.contains(DataSourceStatistics));
        }
    }

    @Test
    public void testSourceProvenanceByNameSpace() throws IDMapperException{
        Set<ProvenanceStatistics> results = urlMapperProvenance.getSourceProvenanceByNameSpace(nameSpace1);
        assertNotNull(results);
        assertThat(results.size(), greaterThanOrEqualTo(2));
        for (ProvenanceStatistics provenanceStatistics: results){
            Provenance provenance = provenanceStatistics.getProvenance();
            assertEquals(DataSource.getByNameSpace(nameSpace1), provenance.getSource());
            assertEquals(ProvenanceFactoryTest.TEST_PREDICATE, provenance.getPredicate());
        }
    }

    @Test
    public void testTargetProvenanceByNameSpace() throws IDMapperException{
        Set<ProvenanceStatistics> results = urlMapperProvenance.getTargetProvenanceByNameSpace(nameSpace1);
        assertNotNull(results);
        assertThat(results.size(), greaterThanOrEqualTo(2));
        for (ProvenanceStatistics provenanceStatistics: results){
            Provenance provenance = provenanceStatistics.getProvenance();
            assertEquals(DataSource.getByNameSpace(nameSpace1), provenance.getTarget());
            assertEquals(ProvenanceFactoryTest.TEST_PREDICATE, provenance.getPredicate());
        }
    }
}
