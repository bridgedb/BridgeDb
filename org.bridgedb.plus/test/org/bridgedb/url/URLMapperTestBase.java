package org.bridgedb.url;

import org.junit.BeforeClass;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTestBase;
import org.bridgedb.provenance.ProvenanceLink;

/**
 *
 * @author Christian
 */
public abstract class URLMapperTestBase extends IDMapperTestBase {
            
    //Must be instantiated by implementation of these tests.
    protected static URLMapper urlMapper;

    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
    
    protected static String nameSpace1;
    protected static String nameSpace2;
    protected static String nameSpace3;
    
    protected static ProvenanceLink link1to2;
    protected static ProvenanceLink link1to3;
    protected static ProvenanceLink link2to1; 
    protected static ProvenanceLink link2to3;
    protected static ProvenanceLink link3to1;
    protected static ProvenanceLink link3to2; 
    
    protected static String map1URL1;
    protected static String map1URL2;
    protected static String map1URL3;
    //Second set of URLs that are expected to map together.
    protected static String map2URL1;
    protected static String map2URL2;
    protected static String map2URL3;
    //Third Set of URLs which again should map to each other but not the above
    protected static String map3URL1;
    protected static String map3URL2;
    protected static String map3URL3;
    //Add an id that does not exist and can not be used in freesearch
    //Or null if all Strings can be used.
    protected static String badID;
    //And a few URLs also not used
    protected static String mapBadURL1;
    protected static String mapBadURL2;
    protected static String mapBadURL3;

    protected static final String TEST_PREDICATE = "www.bridgedb.org/test_predicate";
    
    @BeforeClass
    public static void setupURLs() throws IDMapperException{
      
        nameSpace1 = DataSource1.getNameSpace(); 
        nameSpace2 = DataSource2.getNameSpace(); 
        nameSpace3 = DataSource3.getNameSpace(); 
         
        link1to2 = new ProvenanceLink(
                nameSpace1 + "->" + nameSpace2, nameSpace1, TEST_PREDICATE, nameSpace2);
        link1to3 = new ProvenanceLink(
                nameSpace1 + "->" + nameSpace3, nameSpace1, TEST_PREDICATE, nameSpace3);
        link2to1 = new ProvenanceLink(
                nameSpace2 + "->" + nameSpace1, nameSpace2, TEST_PREDICATE, nameSpace1);
        link2to3 = new ProvenanceLink(
                nameSpace2 + "->" + nameSpace3, nameSpace2, TEST_PREDICATE, nameSpace3);
        link3to1 = new ProvenanceLink(
                nameSpace3 + "->" + nameSpace1, nameSpace3, TEST_PREDICATE, nameSpace1);
        link3to2 = new ProvenanceLink(
                nameSpace3 + "->" + nameSpace3, nameSpace3, TEST_PREDICATE, nameSpace2);

        map1URL1 = map1xref1.getUrl();
        map1URL2 = map1xref2.getUrl();
        map1URL3 = map1xref3.getUrl();
        //Second set of URLs that are expected to map together.
        map2URL1 = map2xref1.getUrl();
        map2URL2 = map2xref2.getUrl();
        map2URL3 = map2xref3.getUrl();
        //Third Set of URLs which again should map to each other but not the above
        map3URL1 = map3xref1.getUrl();
        map3URL2 = map3xref2.getUrl();
        map3URL3 = map3xref3.getUrl();
         //And a few URLs also not used
        mapBadURL1 = "www.notInURLMapper.com#" + goodId1;
        mapBadURL2 = nameSpace2 + badID;
        mapBadURL3 = "www.notInURLMapper.com#789";
    }
    
}
