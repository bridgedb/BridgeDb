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
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;

/**
 *
 * @author Christian
 */
public class RDFMetaDataTest extends MetaDataTestBase{
    
    public RDFMetaDataTest() throws DatatypeConfigurationException, BridgeDBException{        
    }
    
    @Test
    public void testMissingRequiredValue() throws BridgeDBException{
        report("HasMissingRequiredValues");
        d1LicenseStatement = null;
        MetaDataCollection metaData = new MetaDataCollection("loadDirectDataSet1()", loadDirectDataSet1(), rdfRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testAlternativeAllMissingRequiredValue() throws BridgeDBException{
        report("HasMissingRequiredValues");
        d1PublishedStatement = null;
        d1RetreivedOn = null;
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet1()", loadMayDataSet1(), rdfRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testTooManyValues() throws BridgeDBException{
        report("TooManyValues");
        Set<Statement> statements = loadDirectDataSet1();
        Statement extra = new StatementImpl(D1_ID, VoidConstants.URI_SPACE_URI, D2_NAME_SPACE_VALUE);
        statements.add(extra);
        MetaDataCollection metaData = new MetaDataCollection("testTooManyValues()", statements, rdfRegistry);
        checkRequiredValues(metaData);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }
    
    @Test
    public void testMissingValidityReport() throws BridgeDBException{
        report("MissingValidityReport");
        d1TitleStatement = null;
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet1()", loadMayDataSet1(), rdfRegistry);
        checkRequiredValues(metaData);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }

    @Test
    public void testAlternativeValidityReport() throws BridgeDBException{
        report("MissingValidityReport");
        d1ModifiedStatement = null;
        d1RetreivedOn = null;
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet1()", loadMayDataSet1(), rdfRegistry);        
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }
    
    @Test
    public void testGetRDF() throws BridgeDBException{
        report("getRdf");
        Set<Statement> data = loadMayDataSet1();
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet1()", data, rdfRegistry);
        Set<Statement> rewriteData = metaData.getRDF();
        assertEquals(loadMayDataSet1(), rewriteData);
    }
    
}
