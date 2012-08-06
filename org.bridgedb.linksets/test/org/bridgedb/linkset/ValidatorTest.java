package org.bridgedb.linkset;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RDFValidator;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.utils.Reporter;
import org.junit.Test;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
/**
 * @author Alasdair
 *
 */
public class ValidatorTest {
	    
    @Test
    public void testValidateGood1to2() 
    		throws IDMapperException, OpenRDFException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/sample1to2.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test
    public void testValidateGood1to3() 
    		throws IDMapperException, OpenRDFException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/sample1to3.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    /**************************************************************************
     * Validate VoID Header Description
     *************************************************************************/
    
    @Test(expected=IDMapperException.class)
    public void testValidateMissingVoidDescription() 
    		throws IDMapperException, IOException {
    	String filename = "../org.bridgedb.linksets/test-data/missingVoidDescription.ttl";
    	LinksetLoader validator = new LinksetLoader();
    	validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingVoidDescriptionType() 
    		throws IDMapperException, IOException {
    	String filename = "../org.bridgedb.linksets/test-data/missingVoidDescriptionType.ttl";
    	LinksetLoader validator = new LinksetLoader();
    	validator.parse(filename, "validate");
    }
    
    @Test(expected=IDMapperException.class)
    public void testValidateMissingVoidDescriptionTitle() 
    		throws IDMapperException, IOException {
    	String filename = "../org.bridgedb.linksets/test-data/missingVoidDescriptionTitle.ttl";
    	LinksetLoader validator = new LinksetLoader();
    	validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingVoidDescriptionDescription() 
    		throws IDMapperException, IOException {
    	String filename = "../org.bridgedb.linksets/test-data/missingVoidDescriptionDescription.ttl";
    	LinksetLoader validator = new LinksetLoader();
    	validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingVoidDescriptionCreator() 
    		throws IDMapperException, IOException {
    	String filename = "../org.bridgedb.linksets/test-data/missingVoidDescriptionCreator.ttl";
    	LinksetLoader validator = new LinksetLoader();
    	validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingVoidDescriptionCreated() 
    		throws IDMapperException, IOException {
    	String filename = "../org.bridgedb.linksets/test-data/missingVoidDescriptionCreated.ttl";
    	LinksetLoader validator = new LinksetLoader();
    	validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingVoidDescriptionTopic() 
    		throws IDMapperException, IOException {
    	String filename = "../org.bridgedb.linksets/test-data/missingVoidDescriptionTopic.ttl";
    	LinksetLoader validator = new LinksetLoader();
    	validator.parse(filename, "validate");
    }

    /**************************************************************************
     * Validate VoID Header 
     *************************************************************************/
    
    @Test(expected=IDMapperException.class)
    public void testValidateMissingDataSetDeclaration() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingDatasetDeclaration.ttl";
        LinksetLoader validator = new LinksetLoader();
        validator.parse(filename, "validate");
    }

    @Test(expected=IDMapperException.class)
    public void testValidateMissingDataSetTitle() 
    		throws BridgeDbSqlException, IDMapperException, IOException {
        String filename = "../org.bridgedb.linksets/test-data/missingDatasetTitle.ttl";
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
