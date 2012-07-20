package org.bridgedb.file;

//import buildsystem.Measure;

import java.io.File;
import java.net.MalformedURLException;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Christian
 */
public class TestIDMapperText extends IDMapperTest {
	
	private static final File INTERFACE_TEST_FILE = new File ("test-data/interfaceTest.txt");
	
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
		idMapper = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        capabilities = idMapper.getCapabilities();
	}
	
	@Test 
    public void testFileExists()
	{
        report("FileExists");
		Assert.assertTrue (INTERFACE_TEST_FILE.exists());
	}	
	
}
