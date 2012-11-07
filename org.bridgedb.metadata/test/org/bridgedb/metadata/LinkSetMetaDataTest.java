/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.openrdf.model.impl.StatementImpl;
import org.bridgedb.metadata.constants.VoidConstants;
import org.openrdf.model.Statement;
import java.util.Set;
import org.bridgedb.utils.Reporter;
import org.junit.Ignore;
import javax.xml.datatype.DatatypeConfigurationException;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public class LinkSetMetaDataTest extends MetaDataTestBase{
    
    public LinkSetMetaDataTest() throws DatatypeConfigurationException, MetaDataException{        
    }
    
    @Test
    public void testHasRequiredValues() throws MetaDataException{
        report("Linkset HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection("loadMustLinkSet()", loadMustLinkSet(), linksetSetRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testTooManyValues() throws MetaDataException{
        report("TooManyValues");
        Set<Statement> statements = loadMustLinkSet();
        Statement extra = new StatementImpl(LINK_ID, VoidConstants.SUBJECTSTARGET, D2_ID);
        statements.add(extra);
        MetaDataCollection metaData = new MetaDataCollection("testTooManyValues()", statements, linksetSetRegistry);
        assertFalse(metaData.hasRequiredValues());
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, containsString("ERROR"));
    }

    @Test
    public void testExtraSubjectWithBadId() throws MetaDataException{
        report("ExtraSubjectWithBadId");
        Set<Statement> statements = loadMustLinkSet();
        Statement extra = new StatementImpl(D2_ID, VoidConstants.SUBJECTSTARGET, D2_ID);
        statements.add(extra);
        MetaDataCollection metaData = new MetaDataCollection("testTooManyValues()", statements, linksetSetRegistry);
        assertTrue(metaData.hasRequiredValues());
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }

    @Test
    public void testHasCorrectTypes() throws MetaDataException{
        report("Linkset HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection("loadMayLinkSet()", loadMayLinkSet(), linksetSetRegistry);
        checkCorrectTypes(metaData);
    }
 
    @Test
    @Ignore
    public void testAllStatementsUsed() throws MetaDataException{
        report("LinkSet AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection("loadMayLinkSet()", loadMayLinkSet(), linksetSetRegistry);
        checkAllStatementsUsed(metaData);
    }

    @Test
    public void testValidateOk() throws MetaDataException{
        report("LinkSet Validate OK");
        MetaDataCollection metaData = new MetaDataCollection("loadMayLinkSet()", loadMayLinkSet(), linksetSetRegistry);
        metaData.validate();
    }

    @Test
    public void testValidateReport() throws MetaDataException{
        report("LinkSet Report");
        MetaDataCollection metaData = new MetaDataCollection("loadMustLinkSet()", loadMustLinkSet(), linksetSetRegistry);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
        report = metaData.validityReport(INCLUDE_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
        assertThat(report, containsString("WARNING"));
    }
    
    @Test
    public void testSummary() throws MetaDataException{
        report("LinkSet Summary");
        MetaDataCollection metaData = new MetaDataCollection("loadMayLinkSet()", loadMayLinkSet(), linksetSetRegistry);
        String expected = "(Linkset) http://www.example.com/test/linkset1 OK!\n"
                + "(Dataset) http://www.example.com/test/dataset2 OK!\n"
                + "(Dataset) http://www.example.com/test/dataset1 OK!\n";
        String summary = metaData.summary();
        assertEquals(expected, summary);
    }
}
