package org.bridgedb.url;

//import buildsystem.Measure;
import org.bridgedb.file.*;

import java.io.File;
import java.net.MalformedURLException;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.junit.BeforeClass;

/**
 * 
 * @author Christian
 */
public class DoubleWrapperOnTextTest extends IDMapperTest {
	
	private static final File INTERFACE_TEST_FILE = new File ("test-data/interfaceTest.txt");
	
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
		IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        URLMapper urlMapper = new WrapperURLMapper(inner);
        idMapper = new WappedIDMapper(urlMapper);
	}
	
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
    public static void setupVariables2() throws IDMapperException{
         //If the actual source to be tested does not contain these please overwrite with ones that do exist.
        DataSource.register("TestDS1", "TestDS1").nameSpace("www.example.org#");
        DataSource.register("TestDS2", "TestDS2").nameSpace("www.example.com:");
        DataSource.register("TestDS3", "TestDS3").nameSpace("www.myData.com/examples/");
        DataSource.register("TestDSBad", "TestDSBad").nameSpace("www.NotInTheURlMapper.com#");
    }
	
}
