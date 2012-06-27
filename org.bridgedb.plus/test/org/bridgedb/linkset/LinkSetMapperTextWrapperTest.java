package org.bridgedb.linkset;

//import buildsystem.Measure;
import org.bridgedb.linkset.WrappedLinkSetMapper;
import org.bridgedb.url.*;
import org.bridgedb.file.*;

import java.io.File;
import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.junit.BeforeClass;

/**
 * 
 * @author Christian
 */
public class LinkSetMapperTextWrapperTest extends LinkSetMapperTest {
	
	private static final File INTERFACE_TEST_FILE = new File ("../org.bridgedb/test-data/interfaceTest.txt");
	
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
		IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        linkSetMapper = new WrappedLinkSetMapper(inner, TEST_PREDICATE);
	}
	
	
}
