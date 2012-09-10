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
public class DataSetMetaDataTest {
    
    static final Resource D1_ID = new URIImpl ("http://www.example.com/test/dataset1");
    static final Value TITLE = new LiteralImpl("The title");
    static final String DESCRIPTION_STRING = "The dataset description";
    static final Value DESCRIPTION_VALUE = new LiteralImpl(DESCRIPTION_STRING);
    static final URI HOME_PAGE = new URIImpl("http://www.example.com/test/homepage");
    static final URI LICENSE = new URIImpl("http://www.example.com/test/license");
    static final String NAME_SPACE_STRING = "http://www.example.com/data#";
    static final Value NAME_SPACE_VALUE = new LiteralImpl(NAME_SPACE_STRING);
    static final String VERSION_STRING = "Test Version";
    static final Value VERSION_VALUE = new LiteralImpl(VERSION_STRING);
    static final URI DATA_DUMP = new URIImpl("http://www.example.com/test/data_dump");
    static final URI VOCABULARY1 = new URIImpl("http://www.example.com/test/vocab/foo");
    static final URI VOCABULARY2 = new URIImpl("http://www.example.com/test/vocab/bar");
    static final URI TOPIC = new URIImpl("http://www.example.com/test/topic/bar");
    static final URI EXAMPLE1 = new URIImpl("http://www.example.com/data#A1");
    static final URI EXAMPLE2 = new URIImpl("http://www.example.com/data#B2");
    static final URI EXAMPLE3 = new URIImpl("http://www.example.com/data#C3");

    //Flags for easy reading of tests
    static final boolean INCLUDE_WARNINGS = true;
    static final boolean NO_WARNINGS = false;;
      
    Statement idStatement = new StatementImpl(D1_ID, RdfConstants.TYPE_URI, VoidConstants.DATASET); 
    Statement titleStatement = new StatementImpl(D1_ID, DctermsConstants.TITLE, TITLE);
    Statement descriptionStatement = new StatementImpl(D1_ID, DctermsConstants.DESCRIPTION, DESCRIPTION_VALUE);
    Statement homePageStatement = new StatementImpl(D1_ID, FoafConstants.HOMEPAGE, HOME_PAGE);
    Statement licenseStatement = new StatementImpl(D1_ID, DctermsConstants.LICENSE, LICENSE);
    Statement nameSpaceStatement = new StatementImpl(D1_ID, VoidConstants.URI_SPACE, NAME_SPACE_VALUE);
    Statement versionStatement = new StatementImpl(D1_ID, PavConstants.VERSION, VERSION_VALUE);
    Statement dataDumpStatement = new StatementImpl(D1_ID, VoidConstants.DATA_DUMP, DATA_DUMP);  
    Statement d1ModifiedStatement;  // incontrustor due to date!
    Statement vocabularyStatement1 = new StatementImpl(D1_ID, VoidConstants.VOCABULARY, VOCABULARY1);
    Statement vocabularyStatement2 = new StatementImpl(D1_ID, VoidConstants.VOCABULARY, VOCABULARY2);
    Statement topicStatement = new StatementImpl(D1_ID, DctermsConstants.SUBJECT, TOPIC);
    Statement exampleStatement1 = new StatementImpl(D1_ID, VoidConstants.EXAMPLE_RESOURCE, EXAMPLE1);
    Statement exampleStatement2 = new StatementImpl(D1_ID, VoidConstants.EXAMPLE_RESOURCE, EXAMPLE2);
    Statement exampleStatement3 = new StatementImpl(D1_ID, VoidConstants.EXAMPLE_RESOURCE, EXAMPLE3);
    //Statement focStatement = new StatementImpl(D1_ID, VoagConstants.FREQUENCY_OF_CHANGE, FrequencyOfChange.QUARTERLY.getURI());
    
    public DataSetMetaDataTest() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        Value now = new CalendarLiteralImpl(date2);
        d1ModifiedStatement = new StatementImpl(D1_ID, DctermsConstants.MODIFIED, now);  
    }

    private void addStatement(Set<Statement> data, Statement statement){
        if (statement != null){
            data.add(statement);
        }
    }
    
    /**
     * Intentionally not in the constructor so tests can change or remove a statement before loading.
     * @return 
     */
    Set<Statement> loadDataSet1(){
        Set<Statement> data = new HashSet<Statement>();
        addStatement(data, idStatement);
        addStatement(data, titleStatement);
        addStatement(data, descriptionStatement);
        addStatement(data, homePageStatement);
        addStatement(data, licenseStatement);
        addStatement(data, nameSpaceStatement);
        addStatement(data, versionStatement);
        addStatement(data, dataDumpStatement);
        addStatement(data, d1ModifiedStatement);
        addStatement(data, vocabularyStatement1);
        addStatement(data, vocabularyStatement2);
        addStatement(data, topicStatement);
        addStatement(data, exampleStatement1);
        addStatement(data, exampleStatement2);
        addStatement(data, exampleStatement3);
//        data.add(focStatement);
        return data;
    }
    
     public static void checkRequiredValues(MetaData metaData, RequirementLevel forceLevel){
        boolean ok = metaData.hasRequiredValues(forceLevel);
        if (!ok){
            //This test will fail but with extra info
            assertEquals(AppendBase.CLEAR_REPORT, metaData.validityReport(
                    forceLevel, NO_WARNINGS));
            assertTrue(ok);
        }        
    }

    public static void checkCorrectTypes(MetaData metaData){
        boolean ok = metaData.hasCorrectTypes();
        if (!ok){
            //This test will fail but with extra info
            assertEquals(MetaDataBase.CLEAR_REPORT, metaData.validityReport(RequirementLevel.TECHNICAL_MUST, NO_WARNINGS));
            assertTrue(ok);
        }        
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
        licenseStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        checkRequiredValues(metaData, RequirementLevel.TECHNICAL_MUST);
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
        titleStatement = null;
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet1());
        assertNotSame(AppendBase.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MUST, INCLUDE_WARNINGS));
    }
}
