/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.bridgedb.utils.Reporter;
import org.junit.Ignore;
import org.bridgedb.linkset.constants.PavConstants;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Christian
 */
public class ValidatorTest extends LinksetLoaderTest{
    
    @Test
    public void testValidateGood() throws IDMapperException, OpenRDFException, IOException{
        Reporter.report("sample2to1.ttl");
        String[] args1 = {"../org.bridgedb.linksets/test-data/sample1to2.ttl", "validate"};
        LinksetLoader.main (args1);
        Reporter.report("sample1to3.ttl");
        String[] args2 = {"../org.bridgedb.linksets/test-data/sample1to3.ttl", "validate"};
        LinksetLoader.main (args2);
    }

    @Test
    public void testValidateMissingDataSetDeclaration() {
        String[] args = {"../org.bridgedb.linksets/test-data/missingDatasetDeclaration.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString(VoidConstants.DATASET.stringValue()));
        }
    }

    @Test
    public void testValidateMissingDataSetLiscense() {
        String[] args = {"../org.bridgedb.linksets/test-data/missingDatasetLicense.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString(DctermsConstants.LICENSE.stringValue()));
        }
    }

    @Test
    public void testValidateMissingDataSetVersion() {
        String[] args = {"../org.bridgedb.linksets/test-data/missingDatasetVersion.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString(PavConstants.VERSION.stringValue()));
        }
    }

    @Test
    public void testValidateMissingDataSetURISpace() {
        String[] args = {"../org.bridgedb.linksets/test-data/missingDatasetUrispace.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString(VoidConstants.URI_SPACE.stringValue()));
        }
    }
    
    @Test
    public void testValidateIncorrectURISpace() {
        String[] args = {"../org.bridgedb.linksets/test-data/incorrectUrispace.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString("Declared URISpace"));
        }
    }

    @Test
    public void testValidateMissingDataSetSubject() {
        String[] args = {"../org.bridgedb.linksets/test-data/missingDatasetSubject.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString(DctermsConstants.SUBJECT.stringValue()));
        }
    }

    @Test
    public void testValidateMissingLinksetDeclaration() {
        String[] args = {"../org.bridgedb.linksets/test-data/missingLinksetDeclaration.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString(VoidConstants.LINKSET.stringValue()));
        }
    }

    @Test
    public void testValidateMissingLinksetLiscense() {
        String[] args = {"../org.bridgedb.linksets/test-data/missingLinksetLicense.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString(DctermsConstants.LICENSE.stringValue()));
        }
    }

    @Test
    public void testValidateMissingLinksetCreatedBy() {
        String[] args = {"../org.bridgedb.linksets/test-data/missingLinksetCreatedBy.ttl", "validate"};
        try {
            LinksetLoader.main (args);
            assertTrue(false);
        } catch (IDMapperException ex) {
            assertThat(ex.getMessage(), containsString(PavConstants.CREATED_BY.stringValue()));
        }
    }
}
