/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.metadata.constants.*;
import org.bridgedb.utils.Reporter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.IntegerLiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public class DataSetMetaDataTest extends MetaDataTestBase{
    
    public DataSetMetaDataTest() throws DatatypeConfigurationException, MetaDataException{        
    }
    
    @Test
    @Ignore
    public void testShowAll() throws MetaDataException{
        Reporter.report("ShowAll");
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);
        String showAll = metaData.showAll();
        //ystem.out.println(showAll);
    } 
    
    @Test
    public void testHasRequiredValues() throws MetaDataException{
        Reporter.report("HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection(loadDirectDataSet1(), dataSetRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testMissingRequiredValue() throws MetaDataException{
        Reporter.report("HasMissingRequiredValues");
        d1LicenseStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDirectDataSet1(), dataSetRegistry);
        assertFalse(metaData.hasRequiredValues());
    } 

    @Test
    public void testAlternative1MissingRequiredValue() throws MetaDataException{
        Reporter.report("HasMissingRequiredValues");
        d1PublishedStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testAlternativeAllMissingRequiredValue() throws MetaDataException{
        Reporter.report("HasMissingRequiredValues");
        d1PublishedStatement = null;
        d1RetreivedOn = null;
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);
        assertFalse(metaData.hasRequiredValues());
    } 

    @Test
    public void testHasCorrectTypes() throws MetaDataException{
        Reporter.report("HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);
        checkCorrectTypes(metaData);
    }
    
    @Test
    public void testHasCorrectTypesBadDate() throws MetaDataException{
        Reporter.report("isHasCorrectTypesBadDate");
        d1ModifiedStatement = new StatementImpl(D1_ID, DctermsConstants.MODIFIED, TITLE);  
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);
        assertFalse(metaData.hasCorrectTypes());
    }
 
    @Test
    public void testMustValidityReport() throws MetaDataException{
        Reporter.report("MustValidityReport");
        MetaDataCollection metaData = new MetaDataCollection(loadDirectDataSet1(), dataSetRegistry);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
        report = metaData.validityReport(INCLUDE_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }

    @Test
    public void testMissingValidityReport() throws MetaDataException{
        Reporter.report("MissingValidityReport");
        d1TitleStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);
        assertFalse(metaData.hasRequiredValues());
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, containsString("ERROR"));
    }

    @Test
    public void testGroupValidityReport() throws MetaDataException{
        Reporter.report("MissingValidityReport");
        d1ModifiedStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);        
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
        report = metaData.validityReport(INCLUDE_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
        assertThat(report, containsString("WARNING"));
    }

    @Test
    public void testAlternativeValidityReport() throws MetaDataException{
        Reporter.report("MissingValidityReport");
        d1ModifiedStatement = null;
        d1RetreivedOn = null;
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);        
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, containsString("ERROR"));
    }
    
    @Test
    @Ignore
    public void testAllStatementsUsed() throws MetaDataException{
        Reporter.report("AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);
        checkAllStatementsUsed(metaData);
    }
    
    @Test
    public void testNotAllStatementsUsedDifferentPredicate() throws MetaDataException{
        Reporter.report("NotAllStatementsUsedDifferentPredicate");
        Set<Statement> data = loadMayDataSet1();
        Statement unusedStatement = 
                new StatementImpl(D1_ID, new URIImpl("http://www.example.org/NotARealURI"), NAME_SPACE_VALUE);
        data.add(unusedStatement);
        MetaDataCollection metaData = new MetaDataCollection(data, dataSetRegistry);
        assertFalse(metaData.allStatementsUsed());
    }

    @Test
    public void testNotAllStatementsUsedDifferentResource() throws MetaDataException{
        Reporter.report("NotAllStatementsUsedDifferentResource");
        Set<Statement> data = loadMayDataSet1();
        Statement unusedStatement = new StatementImpl(
                new URIImpl("http://www.example.org/NotARealURI"), DctermsConstants.TITLE, NAME_SPACE_VALUE);
        data.add(unusedStatement);
        MetaDataCollection metaData = new MetaDataCollection(data, dataSetRegistry);
        assertFalse(metaData.allStatementsUsed());
    }

    @Test
    public void testGetRDF() throws MetaDataException{
        Reporter.report("getRdf");
        Set<Statement> data = loadMayDataSet1();
        MetaDataCollection metaData = new MetaDataCollection(data, dataSetRegistry);
        Set<Statement> rewriteData = metaData.getRDF();
        assertEquals(loadMayDataSet1(), rewriteData);
    }
    
    @Test
    public void testSummary() throws MetaDataException{
        Reporter.report("Dataset Summary");
        MetaDataCollection metaData = new MetaDataCollection(loadMayDataSet1(), dataSetRegistry);
        String expected = "(Dataset) http://www.example.com/test/dataset1 OK!\n";
        String summary = metaData.summary();
        assertEquals(expected, summary);
    }

    @Test
    public void testSummaryExtra() throws MetaDataException{
        Reporter.report("Dataset Summary Extra");
        Set<Statement> data = loadMayDataSet1();
        data.add(personIdStatement);
        data.add(personNameStatement);
        MetaDataCollection metaData = new MetaDataCollection(data, dataSetRegistry);
        String expected = "http://www.example.com/person#Joe has an unspecified type of http://www.example.com/Person\n"
                + "(Dataset) http://www.example.com/test/dataset1 OK!\n";
        String summary = metaData.summary();
        assertEquals(expected, summary);
    }

}
