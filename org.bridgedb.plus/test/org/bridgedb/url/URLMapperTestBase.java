package org.bridgedb.url;

import org.junit.BeforeClass;
import org.bridgedb.IDMapperException;
import org.bridgedb.MapperTestBase;

/**
 *
 * @author Christian
 */
public abstract class URLMapperTestBase extends MapperTestBase {
            
    //Must be instantiated by implementation of these tests.
    protected static URLMapper urlMapper;

    //Used by subsets during settup to check connection is ok.
    //If set to false will skip testClose(). 
    protected static boolean connectionOk = true;
    
    protected static String nameSpace2;
    protected static String nameSpace3;
    
    protected static String goodId1;
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

    @BeforeClass
    public static void LoadMappings() throws IDMapperException{
      
        nameSpace2 = "www.example.com/";
        nameSpace3 = "www.example.org#";
         
        goodId1 = "123";

        map1URL1 = "example:123";
        map1URL2 = "www.example.com/123";
        map1URL3 = "www.example.org#123";
        //Second set of URLs that are expected to map together.
        map2URL1 = "example:456";
        map2URL2 = "www.example.com/456";
        map2URL3 = "www.example.org#456";
        //Third Set of URLs which again should map to each other but not the above
        map3URL1 = "example:789";
        map3URL2 = "www.example.com/789";
        map3URL3 = "www.example.org#789";
        //Add an id that does not exist and can not be used in freesearch
        //Or null if all Strings can be used.
        badID = "abc";
        //And a few URLs also not used
        mapBadURL1 = "www.notInURLMapper.com#123";
        mapBadURL2 = "www.example.com/abc";
        mapBadURL3 = "www.notInURLMapper.com#789";
    }
    
}
