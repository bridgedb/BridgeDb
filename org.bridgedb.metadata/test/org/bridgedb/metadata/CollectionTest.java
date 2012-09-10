/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.constants.PavConstants;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.metadata.constants.*;
import org.bridgedb.metadata.utils.Reporter;
import static org.junit.Assert.*;
import org.junit.Test;
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
public class CollectionTest {
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
    
    public CollectionTest() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        Value now = new CalendarLiteralImpl(date2);
        d1ModifiedStatement = new StatementImpl(D1_ID, DctermsConstants.MODIFIED, now);  
    }

    /**
     * Intentionally not in the constructor so tests can change or remove a statement before loading.
     * @return 
     */
    Set<Statement> loadDataSet1(){
        Set<Statement> data = new HashSet<Statement>();
        data.add(idStatement);
        data.add(titleStatement);
        data.add(descriptionStatement);
        data.add(homePageStatement);
        data.add(licenseStatement);
        data.add(nameSpaceStatement);
        data.add(versionStatement);
        data.add(dataDumpStatement);
        data.add(d1ModifiedStatement);
        data.add(vocabularyStatement1);
        data.add(vocabularyStatement2);
        data.add(topicStatement);
        data.add(exampleStatement1);
        data.add(exampleStatement2);
        data.add(exampleStatement3);
        //data.add(focStatement);
        return data;
    }
    
    public static void checkRequiredValues(MetaData metaData, RequirementLevel forceLevel){
        boolean ok = metaData.hasRequiredValues(forceLevel);
        if (!ok){
            //This test will fail but with extra info
            assertEquals(MetaDataBase.CLEAR_REPORT, metaData.validityReport(forceLevel, NO_WARNINGS));
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
    public void testShowDataSet() throws Exception {
        Reporter.report("ShowDataSet");
        MetaDataCollection metaDataCollection = new MetaDataCollection(loadDataSet1());
        System.out.println(metaDataCollection.Schema());
        System.out.println(metaDataCollection.toString());
        checkRequiredValues(metaDataCollection, RequirementLevel.MUST);
        checkCorrectTypes(metaDataCollection);
    }

}
