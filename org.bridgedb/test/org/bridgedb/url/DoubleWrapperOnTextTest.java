package org.bridgedb.url;

//import buildsystem.Measure;
import org.bridgedb.file.*;

import java.io.File;
import java.net.MalformedURLException;
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
	
}
