/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.metadata.constants.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public class RDFMetaDataTest extends MetaDataTestBase{
    
    public RDFMetaDataTest() throws DatatypeConfigurationException, MetaDataException{        
    }
    
    @Test
    public void testMissingRequiredValue() throws MetaDataException{
        report("HasMissingRequiredValues");
        d1LicenseStatement = null;
        MetaDataCollection metaData = new MetaDataCollection("loadDirectDataSet1()", loadDirectDataSet1(), rdfRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testAlternativeAllMissingRequiredValue() throws MetaDataException{
        report("HasMissingRequiredValues");
        d1PublishedStatement = null;
        d1RetreivedOn = null;
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet1()", loadMayDataSet1(), rdfRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testTooManyValues() throws MetaDataException{
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
    public void testMissingValidityReport() throws MetaDataException{
        report("MissingValidityReport");
        d1TitleStatement = null;
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet1()", loadMayDataSet1(), rdfRegistry);
        checkRequiredValues(metaData);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }

    @Test
    public void testAlternativeValidityReport() throws MetaDataException{
        report("MissingValidityReport");
        d1ModifiedStatement = null;
        d1RetreivedOn = null;
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet1()", loadMayDataSet1(), rdfRegistry);        
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }
    
    @Test
    public void testGetRDF() throws MetaDataException{
        report("getRdf");
        Set<Statement> data = loadMayDataSet1();
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet1()", data, rdfRegistry);
        Set<Statement> rewriteData = metaData.getRDF();
        assertEquals(loadMayDataSet1(), rewriteData);
    }
    
}
