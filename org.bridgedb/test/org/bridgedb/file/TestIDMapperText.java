package org.bridgedb.file;

//import buildsystem.Measure;

import java.io.File;
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
    public static void setUpClass() throws Exception {
		idMapper = new IDMapperText(INTERFACE_TEST_FILE.toURL());
	}
	
	@Test 
    public void testFileExists()
	{
        System.out.println("FileExists");
		Assert.assertTrue (INTERFACE_TEST_FILE.exists());
	}	
	
}
