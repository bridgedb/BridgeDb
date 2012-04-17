package org.bridgedb.provenance;

//import buildsystem.Measure;
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
public class ProvenaceMapperTextWrapperTest extends ProvenanceMapperTest {
	
	private static final File INTERFACE_TEST_FILE = new File ("../org.bridgedb/test-data/interfaceTest.txt");
	
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
		IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE.toURL());
        provenaceMapper = new WrappedProvenanceMapper(inner, TEST_PREDICATE);
	}
	
	
}
