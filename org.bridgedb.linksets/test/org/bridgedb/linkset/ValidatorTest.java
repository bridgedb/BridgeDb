package org.bridgedb.linkset;

import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.utils.Reporter;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public class ValidatorTest {
	    
    @Test
    public void testValidateGood2to1() 
    		throws IDMapperException, OpenRDFException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/sample1to2.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test
    public void testValidateGood1to3() 
    		throws IDMapperException, OpenRDFException, IOException {
        Reporter.report("sample1to3.ttl");
        String filename = "../org.bridgedb.linksets/test-data/sample1to3.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingDataSetDeclaration() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingDatasetDeclaration.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingDataSetLiscense() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingDatasetLicense.ttl";
                LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingDataSetVersion() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingDatasetVersion.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingDataSetURISpace() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingDatasetUrispace.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }
    
    @Test(expected=IDMapperException.class)
    public void testValidateIncorrectURISpace() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/incorrectUrispace.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingDataSetSubject() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingDatasetSubject.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingLinksetDeclaration() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingLinksetDeclaration.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingLinksetLiscense() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingLinksetLicense.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingLinksetCreatedBy() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingLinksetCreatedBy.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

}
