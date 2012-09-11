/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.metadata.constants.*;
import org.bridgedb.metadata.utils.Reporter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class DataSetMetaDataTest extends MetaDataTestBase{
    
    public DataSetMetaDataTest() throws DatatypeConfigurationException{        
    }
    
    @Test
    public void testShowAll() throws MetaDataException{
        Reporter.report("ShowAll");
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        String showAll = metaData.showAll(RequirementLevel.MAY);
        //ystem.out.println(showAll);
    } 
    
    @Test
    public void testHasRequiredValues() throws MetaDataException{
        Reporter.report("HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        checkRequiredValues(metaData, RequirementLevel.MUST);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY));
    } 

    @Test
    public void testAutoFindId() throws MetaDataException{
        Reporter.report("AutoFindId");
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        checkRequiredValues(metaData, RequirementLevel.MUST);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY));
    } 

    @Test
    public void testMissingRequiredValue() throws MetaDataException{
        Reporter.report("HasMissingRequiredValues");
        d1LlicenseStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        checkRequiredValues(metaData, RequirementLevel.TECHNICAL_MUST);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MUST));
    } 

    @Test
    public void testAlternative1MissingRequiredValue() throws MetaDataException{
        Reporter.report("HasMissingRequiredValues");
        d1PublishedStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        checkRequiredValues(metaData, RequirementLevel.MUST);
    } 

    @Test
    public void testAlternativeAllMissingRequiredValue() throws MetaDataException{
        Reporter.report("HasMissingRequiredValues");
        d1PublishedStatement = null;
        d1RetreivedOn = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MUST));
    } 

    @Test
    public void testHasCorrectTypes() throws MetaDataException{
        Reporter.report("HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        checkCorrectTypes(metaData);
    }
    
    @Test
    public void testHasCorrectTypesBadDate() throws MetaDataException{
        Reporter.report("isHasCorrectTypesBadDate");
        d1ModifiedStatement = new StatementImpl(D1_ID, DctermsConstants.MODIFIED, TITLE);  
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        assertFalse(metaData.hasCorrectTypes());
    }
 
    @Test
    public void testValidityReport() throws MetaDataException{
        Reporter.report("ValidityReport");
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        assertEquals(AppendBase.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MUST, INCLUDE_WARNINGS));
    }

    @Test
    public void testMissingValidityReport() throws MetaDataException{
        Reporter.report("MissingValidityReport");
        d1TitleStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        assertNotSame(AppendBase.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MUST, INCLUDE_WARNINGS));
    }

    @Test
    public void testGroupValidityReport() throws MetaDataException{
        Reporter.report("MissingValidityReport");
        d1ModifiedStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());        
        assertNotSame(AppendBase.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MUST, INCLUDE_WARNINGS));
        assertEquals(AppendBase.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MUST, NO_WARNINGS));
    }

    @Test
    public void testAlternativeValidityReport() throws MetaDataException{
        Reporter.report("MissingValidityReport");
        d1ModifiedStatement = null;
        d1RetreivedOn = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());        
        assertNotSame(AppendBase.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MUST, NO_WARNINGS));
    }
}
