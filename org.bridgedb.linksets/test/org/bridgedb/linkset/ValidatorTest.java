// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.linkset;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 * @author Alasdair
 *
 */
@Ignore
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
