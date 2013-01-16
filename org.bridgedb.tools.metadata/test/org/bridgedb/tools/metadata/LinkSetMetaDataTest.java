// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
package org.bridgedb.tools.metadata;

import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;

/**
 *
 * @author Christian
 */
public class LinkSetMetaDataTest extends MetaDataTestBase{
    
    public LinkSetMetaDataTest() throws DatatypeConfigurationException, BridgeDBException{        
    }
    
    @Test
    public void testHasRequiredValues() throws BridgeDBException{
        report("Linkset HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection("loadMustLinkSet()", loadMustLinkSet(), linksetSetRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testTooManyValues() throws BridgeDBException{
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
    public void testExtraSubjectWithBadId() throws BridgeDBException{
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
    public void testHasCorrectTypes() throws BridgeDBException{
        report("Linkset HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection("loadMayLinkSet()", loadMayLinkSet(), linksetSetRegistry);
        checkCorrectTypes(metaData);
    }
 
    @Test
    @Ignore
    public void testAllStatementsUsed() throws BridgeDBException{
        report("LinkSet AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection("loadMayLinkSet()", loadMayLinkSet(), linksetSetRegistry);
        checkAllStatementsUsed(metaData);
    }

    @Test
    public void testValidateOk() throws BridgeDBException{
        report("LinkSet Validate OK");
        MetaDataCollection metaData = new MetaDataCollection("loadMayLinkSet()", loadMayLinkSet(), linksetSetRegistry);
        metaData.validate();
    }

    @Test
    public void testValidateReport() throws BridgeDBException{
        report("LinkSet Report");
        MetaDataCollection metaData = new MetaDataCollection("loadMustLinkSet()", loadMustLinkSet(), linksetSetRegistry);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
        report = metaData.validityReport(INCLUDE_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
        assertThat(report, containsString("WARNING"));
    }
    
    @Test
    public void testSummary() throws BridgeDBException{
        report("LinkSet Summary");
        MetaDataCollection metaData = new MetaDataCollection("loadMayLinkSet()", loadMayLinkSet(), linksetSetRegistry);
        String expected = "(Linkset) http://www.example.com/test/linkset1 OK!\n"
                + "(Dataset) http://www.example.com/test/dataset2 OK!\n"
                + "(Dataset) http://www.example.com/test/dataset1 OK!\n";
        String summary = metaData.summary();
        assertEquals(expected, summary);
    }
}
