package org.bridgedb.ops;

import org.junit.BeforeClass;
import java.util.Set;
import org.bridgedb.result.URLMapping;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.Xref;
import org.bridgedb.DataSource;
import java.util.List;
import java.util.ArrayList;
import org.bridgedb.IDMapperException;
import org.bridgedb.url.URLMapperTestBase;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

/**
 * @author Christian
 */
public abstract class OpsMapperTest extends URLMapperTestBase {
    
    protected static OpsMapper opsMapper; 

    //Statics for easy readability of method calls
    private static final ArrayList<String> ALL_URLs = new ArrayList<String>(); 
    private static final ArrayList<String> ALL_SOURCE_URLs = ALL_URLs; 
    private static final ArrayList<String> ALL_TARGET_URLs = ALL_URLs; 
    private static final ArrayList<String> ALL_NAME_SPACES = ALL_URLs; 
    private static final ArrayList<String> ALL_SOURCE_NAME_SPACES = ALL_URLs; 
    private static final ArrayList<String> ALL_TARGET_NAME_SPACES = ALL_URLs; 
    private static final ArrayList<DataSource> ALL_DATA_SOURCES = new ArrayList<DataSource>();
    private static final ArrayList<String> ALL_PROVENANCE_IDS = ALL_URLs;
    private static final ArrayList<String> NONE_BY_ID = ALL_URLs; 
    private static final int JUST_ONE = 1;

    @BeforeClass
    public static void setupURLs() throws IDMapperException{
        URLMapperTestBase.setupURLs();
        link1to2 = "http://localhost:8080/OPS-IMS/linkset/1/#Test1_2";
        link1to3 = "http://localhost:8080/OPS-IMS/linkset/2/#Test1_3";
        link2to1 = "http://localhost:8080/OPS-IMS/linkset/3/#Test2_1";
        link2to3 = "http://localhost:8080/OPS-IMS/linkset/4/#Test2_3";
        link3to1 = "http://localhost:8080/OPS-IMS/linkset/5/#Test3_1";
        link3to2 = "http://localhost:8080/OPS-IMS/linkset/6/#Test3_2";
    }
    
    @Test 
    public void testGetMappings() throws IDMapperException{
        report ("testGetMappings");
        List<URLMapping> mappings = opsMapper.getMappings(ALL_URLs, ALL_SOURCE_URLs, ALL_TARGET_URLs,
                ALL_NAME_SPACES, ALL_SOURCE_NAME_SPACES, ALL_TARGET_NAME_SPACES, ALL_PROVENANCE_IDS, 0, 10);
        assertEquals(10, mappings.size());
    }
    
    @Test 
    public void testGetMappingsByURL() throws IDMapperException{
        report ("testGetMappingsByURL");
        ArrayList<String> URLs = new ArrayList<String>();
        URLs.add(map1URL1);
        List<URLMapping> mappings = opsMapper.getMappings(URLs , ALL_SOURCE_URLs, ALL_TARGET_URLs,
                ALL_NAME_SPACES, ALL_SOURCE_NAME_SPACES, ALL_TARGET_NAME_SPACES, ALL_PROVENANCE_IDS, 0, 3);
        assertEquals(3, mappings.size());
        for (URLMapping mapping: mappings){
            assertThat(map1URL1, anyOf(equalTo(mapping.getSourceURL()), equalTo(mapping.getTargetURL())));
        }
    }

    @Test 
    public void testGetMappingsBySourceURL() throws IDMapperException{
        report ("testGetMappingsBySourceURL");
        ArrayList<String> sourceURLs = new ArrayList<String>();
        sourceURLs.add(map1URL1);
        List<URLMapping> mappings = opsMapper.getMappings(ALL_URLs, sourceURLs, ALL_TARGET_URLs,
                ALL_NAME_SPACES, ALL_SOURCE_NAME_SPACES, ALL_TARGET_NAME_SPACES, ALL_PROVENANCE_IDS, 0, 2);
        assertEquals(2, mappings.size());
        for (URLMapping mapping: mappings){
            assertEquals(map1URL1, mapping.getSourceURL());
        }
    }

    @Test 
    public void testGetMappingsByTargetURL() throws IDMapperException{
        report ("testGetMappingsByTargetURL");
        ArrayList<String> targetURLs = new ArrayList<String>();
        targetURLs.add(map1URL1);
        List<URLMapping> mappings = opsMapper.getMappings(ALL_URLs, ALL_SOURCE_URLs, targetURLs,
                ALL_NAME_SPACES, ALL_SOURCE_NAME_SPACES, ALL_TARGET_NAME_SPACES, ALL_PROVENANCE_IDS, 0, 2);
        assertEquals(2, mappings.size());
        for (URLMapping mapping: mappings){
            assertEquals(map1URL1, mapping.getTargetURL());
        }
    }

    @Test 
    public void testGetMappingsByNameSpace() throws IDMapperException{
        report ("testGetMappingsByNameSpace");
        ArrayList<String> nameSpaces = new ArrayList<String>();
        nameSpaces.add(nameSpace1);
        List<URLMapping> mappings = opsMapper.getMappings(ALL_URLs, ALL_SOURCE_URLs, ALL_TARGET_URLs,
                nameSpaces, ALL_SOURCE_NAME_SPACES, ALL_TARGET_NAME_SPACES, ALL_PROVENANCE_IDS, 0, 12);
        assertEquals(12, mappings.size());
        for (URLMapping mapping: mappings){
            assertTrue(mapping.getSourceURL().startsWith(nameSpace1) || mapping.getTargetURL().startsWith(nameSpace1));
        }
    }

    @Test 
    public void testGetMappingsBySourceNameSpace() throws IDMapperException{
        report ("testGetMappingsBySourceNameSpace");
        ArrayList<String> sourceNameSpaces = new ArrayList<String>();
        sourceNameSpaces.add(nameSpace1);
        List<URLMapping> mappings = opsMapper.getMappings(ALL_URLs, ALL_SOURCE_URLs, ALL_TARGET_URLs,
                ALL_NAME_SPACES, sourceNameSpaces, ALL_TARGET_NAME_SPACES, ALL_PROVENANCE_IDS, 0, 6);
        assertEquals(6, mappings.size());
        for (URLMapping mapping: mappings){
            assertTrue(mapping.getSourceURL().startsWith(nameSpace1));
        }
    }

    @Test 
    public void testGetMappingsByTargetNameSpace() throws IDMapperException{
        report ("testGetMappingsByTargetNameSpace");
        ArrayList<String> targetNameSpaces = new ArrayList<String>();
        targetNameSpaces.add(nameSpace1);
        List<URLMapping> mappings = opsMapper.getMappings(ALL_URLs, ALL_SOURCE_URLs, ALL_TARGET_URLs,
                ALL_NAME_SPACES, ALL_SOURCE_NAME_SPACES, targetNameSpaces, ALL_PROVENANCE_IDS, 0, 6);
        assertEquals(6, mappings.size());
        for (URLMapping mapping: mappings){
            assertTrue(mapping.getTargetURL().startsWith(nameSpace1));
        }
    }

    @Test 
    public void testGetMappingsByProvenanceId() throws IDMapperException{
        report ("testGetMappingsByProvenanceId");
        ArrayList<String> provenanceIds = new ArrayList<String>();
        provenanceIds.add(link1to2);
        System.out.println(link1to2);
        List<URLMapping> mappings = opsMapper.getMappings(ALL_URLs, ALL_SOURCE_URLs, ALL_TARGET_URLs,
                ALL_NAME_SPACES, ALL_SOURCE_NAME_SPACES, ALL_TARGET_NAME_SPACES, provenanceIds, 0, 3);
        assertEquals(3, mappings.size());
        for (URLMapping mapping: mappings){
            assertEquals(link1to2, mapping.getProvenanceId());
        }
    }

    @Test 
    public void testGetMapping() throws IDMapperException{
        report ("testGetMapping");
        List<URLMapping> mappings = opsMapper.getMappings(ALL_URLs, ALL_SOURCE_URLs, ALL_TARGET_URLs,
                ALL_NAME_SPACES, ALL_SOURCE_NAME_SPACES, ALL_TARGET_NAME_SPACES, ALL_PROVENANCE_IDS, 0, 1);
        URLMapping first = mappings.get(0);
        URLMapping mapping = opsMapper.getMapping(first.getId());
        assertEquals(first, mapping);
    }
 
    /* Removed do to scale problem
    @Test
    public void testGetURLsByPosition() throws IDMapperException{
        report("testGetURLsByPosition");
        String result0 = opsMapper.getURLs(ALL_NAME_SPACES, ALL_PROVENANCE_IDS, 0, JUST_ONE).get(0);
        assertNotNull(result0);
        String result1 = opsMapper.getURLs(ALL_NAME_SPACES, ALL_PROVENANCE_IDS, 1, JUST_ONE).get(0);
        assertThat(result1, not(result0));
        String result2 = opsMapper.getURLs(ALL_NAME_SPACES, ALL_PROVENANCE_IDS, 2, JUST_ONE).get(0);
        assertThat(result2, not(result0));
        assertThat(result2, not(result1));
    }

    @Test
    public void testGetURLsByPositionAndLimit() throws IDMapperException{
        report("testGetURLsByPositionAndLimit");
        List<String> results1 = opsMapper.getURLs(ALL_NAME_SPACES, ALL_PROVENANCE_IDS, 0, 5);
        assertEquals(5, results1.size());
        //Only 4 in the seocnd ones as there may be only 9 mappings
        List<String> results2 = opsMapper.getURLs(ALL_NAME_SPACES, ALL_PROVENANCE_IDS, 5, 4);
        assertEquals(4, results2.size());
        for (String url: results2){
            assertFalse(results1.contains(url));
        }
    }

    @Test
    public void testGetURLsByPositionAndDataSource() throws IDMapperException{
        report("testGetURLsByPositionAndDataSource");
        ArrayList<String> nameSpaces2 = new ArrayList<String>();
        nameSpaces2.add(nameSpace2);
        String result0 = opsMapper.getURLs(nameSpaces2, ALL_PROVENANCE_IDS, 0, JUST_ONE).get(0);
        assertNotNull(result0);
        assertThat(result0, startsWith(nameSpace2));
        ArrayList<String> nameSpaces3 = new ArrayList<String>();
        nameSpaces3.add(nameSpace3);
        String result1 = opsMapper.getURLs(nameSpaces3, ALL_PROVENANCE_IDS, 0, JUST_ONE).get(0);
        assertThat(result1, startsWith(nameSpace3));
        assertThat(result1, not(result0));
        String result2 = opsMapper.getURLs(nameSpaces2, ALL_PROVENANCE_IDS, 1, JUST_ONE).get(0);
        assertThat(result2, not(result0));
        assertThat(result2, not(result1));
        assertThat(result2, startsWith(nameSpace2));
    }
        
    @Test
    public void testGetURLsByPositionLimitAndDataSource() throws IDMapperException{
        report("testGetURLsByPositionLimitAndDataSource");
        ArrayList<String> nameSpaces2 = new ArrayList<String>();
        nameSpaces2.add(nameSpace2);
        //There may be only three for 
        List<String> results = opsMapper.getURLs(nameSpaces2, ALL_PROVENANCE_IDS, 0, 3);
        assertEquals(3, results.size());
        for (String url: results){
            assertThat(url, startsWith(nameSpace2));
        }
    }
    
    @Test
    public void testbyPosition() throws IDMapperException{
        report("testbyPosition");
        Xref result0 = opsMapper.getXrefs(ALL_DATA_SOURCES, ALL_PROVENANCE_IDS, 0, JUST_ONE).get(0);
        assertNotNull(result0);
        Xref result1 = opsMapper.getXrefs(ALL_DATA_SOURCES, ALL_PROVENANCE_IDS, 1, JUST_ONE).get(0);
        assertFalse(result1.equals(result0));
        Xref result2 = opsMapper.getXrefs(ALL_DATA_SOURCES, ALL_PROVENANCE_IDS, 2, JUST_ONE).get(0);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
    }

    @Test
    public void testbyPositionAndLimit() throws IDMapperException{
        report("testbyPositionAndLimit");
        List<Xref> results1 = opsMapper.getXrefs(ALL_DATA_SOURCES, ALL_PROVENANCE_IDS, 0, 5);
        assertEquals(5, results1.size());
        //Only 4 in the seocnd ones as there may be only 9 mappings
        List<Xref> results2 = opsMapper.getXrefs(ALL_DATA_SOURCES, ALL_PROVENANCE_IDS, 5, 4);
        assertEquals(4, results2.size());
        for (Xref xref: results2){
            assertFalse(results1.contains(xref));
        }
    }

    @Test
    public void testbyPositionAndDataSource() throws IDMapperException{
        report("testbyPositionAndDataSource");
        ArrayList<DataSource> dataSources1 = new ArrayList<DataSource>();
        dataSources1.add(DataSource1);
        Xref result0 = opsMapper.getXrefs(dataSources1, ALL_PROVENANCE_IDS, 0, JUST_ONE).get(0);
        assertNotNull(result0);
        assertEquals(DataSource1, result0.getDataSource());
        ArrayList<DataSource> dataSources2 = new ArrayList<DataSource>();
        dataSources2.add(DataSource2);
        Xref result1 = opsMapper.getXrefs(dataSources2, ALL_PROVENANCE_IDS, 0, JUST_ONE).get(0);
        assertEquals(result1.getDataSource(), DataSource2);
        assertFalse(result1.equals(result0));
        Xref result2 = opsMapper.getXrefs(dataSources1, ALL_PROVENANCE_IDS, 1, JUST_ONE).get(0);
        assertFalse(result2.equals(result0));
        assertFalse(result2.equals(result1));
        assertEquals(result2.getDataSource(), DataSource1);
    }
        
    @Test
    public void testbyPositionLimitAndDataSource() throws IDMapperException{
        report("testbyPositionLimitAndDataSource");
        //There may be only three for 
        ArrayList<DataSource> dataSources1 = new ArrayList<DataSource>();
        dataSources1.add(DataSource1);
        List<Xref> results = opsMapper.getXrefs(dataSources1, ALL_PROVENANCE_IDS, 0, 10);
        assertEquals(3, results.size());
        for (Xref xref: results){
            assertEquals(xref.getDataSource(), DataSource1);
        }
    }*/

    @Test
    public void testGetOverallStatistics()  throws IDMapperException{
        report("GetOverallStatistics");
        OverallStatistics results = opsMapper.getOverallStatistics();
        assertThat (results.getNumberOfMappings(), greaterThanOrEqualTo(6));
        assertThat (results.getNumberOfPredicates(), greaterThanOrEqualTo(1));
        assertThat (results.getNumberOfProvenances(), greaterThan(1));
        assertThat (results.getNumberOfTargetDataSources(), greaterThanOrEqualTo(3));
        assertThat (results.getNumberOfSourceDataSources(), greaterThanOrEqualTo(3));
    }

    @Test
    public void testGetProvenanceInfos() throws IDMapperException{
        report("GetProvenanceInfos");
        List<ProvenanceInfo> results = opsMapper.getProvenanceInfos();
        assertThat (results.size(), greaterThanOrEqualTo(6));
        boolean found = false;
        for (ProvenanceInfo info:results){
            if (info.getId().equals(link3to2)){
                found = true;
                assertEquals(TEST_PREDICATE, info.getPredicate());
                assertEquals(nameSpace3, info.getSourceNameSpace());
                assertEquals(nameSpace2, info.getTargetNameSpace());
                assertEquals(new Integer(3), info.getNumberOfLinks());
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void testGetSampleURLs() throws IDMapperException{
        List<String> results = opsMapper.getSampleSourceURLs();
        assertFalse(results.isEmpty());
    }
}
