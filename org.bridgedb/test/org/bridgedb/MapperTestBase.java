package org.bridgedb;

import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public abstract class MapperTestBase {
    
    //DataSource that MUST be supported.
    protected static DataSource DataSource1;
    protected static DataSource DataSource2;
    protected static DataSource DataSource3;
    //This DataSource MUST not be supported
    protected static DataSource DataSourceBad;
      
    @BeforeClass
    /**
     * Class to set up the variables.
     * 
     * Should be overrided to change all of the variables.
     * To change some over write it. Call super.setupVariables() and then change the few that need fixing.
     * <p>
     * Note: According to the Junit api 
     * "The @BeforeClass methods of superclasses will be run before those the current class."
     */
    public static void loadDataSources() throws IDMapperException{
        //If the actual source to be tested does not contain these please overwrite with ones that do exist.
        DataSource1 = DataSource.register("TestDS1", "TestDS1").
                urlPattern("example:$id").asDataSource();
        DataSource2 = DataSource.register("TestDS2", "TestDS2").urlPattern("www.example.com/$id").asDataSource();
        DataSource3 = DataSource.register("TestDS3", "TestDS3").nameSpace("www.example.org#").asDataSource();
        //This DataSource MUST not be supported
        DataSourceBad = DataSource.register("TestDSBad", "TestDSBad")
                .nameSpace("www.NotInTheURlMapper.com#").asDataSource();
      }
    
  
}
