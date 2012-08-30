/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.FoafConstants;
import org.bridgedb.linkset.constants.FrequencyOfChange;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.SkosConstants;
import org.bridgedb.linkset.constants.VoagConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.bridgedb.utils.Reporter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.IntegerLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class LinkSetMetaDataTest extends DataSetMetaDataTest{
    
    static final Resource D2_ID = new URIImpl ("http://www.example.com/test/d2");
    static final Value D2_TITLE = new LiteralImpl("The second data set");
    static final String D2_DESCRIPTION_STRING = "The second dataset description";
    static final Value D2_DESCRIPTION_VALUE = new LiteralImpl(D2_DESCRIPTION_STRING);
    static final String D2_NAME_SPACE_STRING = "http://www.example.org/data#";
    static final Value D2_NAME_SPACE_VALUE = new LiteralImpl(D2_NAME_SPACE_STRING);
    static final URI D2_EXAMPLE1 = new URIImpl("http://www.example.org/data#1A");
    static final URI D2_EXAMPLE2 = new URIImpl("http://www.example.org/data#2B");
    static final URI D2_EXAMPLE3 = new URIImpl("http://www.example.org/data#3C");

    Statement d2IdStatement = new StatementImpl(D2_ID, RdfConstants.TYPE_URI, VoidConstants.DATASET); 
    Statement d2TitleStatement = new StatementImpl(D2_ID, DctermsConstants.TITLE, D2_TITLE);
    Statement d2DescriptionStatement = new StatementImpl(D2_ID, DctermsConstants.DESCRIPTION, D2_DESCRIPTION_VALUE);
    Statement d2HomePageStatement = new StatementImpl(D2_ID, FoafConstants.HOMEPAGE, HOME_PAGE);
    Statement d2LicenseStatement = new StatementImpl(D2_ID, DctermsConstants.LICENSE, LICENSE);
    Statement d2NameSpaceStatement = new StatementImpl(D2_ID, VoidConstants.URI_SPACE, D2_NAME_SPACE_VALUE);
    Statement d2VersionStatement = new StatementImpl(D2_ID, PavConstants.VERSION, VERSION_VALUE);
    Statement d2DataDumpStatement = new StatementImpl(D2_ID, VoidConstants.DATA_DUMP, DATA_DUMP);  
    Statement d2ModifiedStatement;  // incontrustor due to date!
    Statement d2VocabularyStatement1 = new StatementImpl(D2_ID, VoidConstants.VOCABULARY, VOCABULARY1);
    Statement d2VocabularyStatement2 = new StatementImpl(D2_ID, VoidConstants.VOCABULARY, VOCABULARY2);
    Statement d2TopicStatement = new StatementImpl(D2_ID, DctermsConstants.SUBJECT, TOPIC);
    Statement d2ExampleStatement1 = new StatementImpl(D2_ID, VoidConstants.EXAMPLE_RESOURCE, D2_EXAMPLE1);
    Statement d2ExampleStatement2 = new StatementImpl(D2_ID, VoidConstants.EXAMPLE_RESOURCE, D2_EXAMPLE2);
    Statement d2ExampleStatement3 = new StatementImpl(D2_ID, VoidConstants.EXAMPLE_RESOURCE, D2_EXAMPLE3);
    Statement d2FocStatement = new StatementImpl(D2_ID, VoagConstants.FREQUENCY_OF_CHANGE, FrequencyOfChange.ANNUAL.getURI());
    
    static final Resource LINK_ID = new URIImpl ("http://www.example.com/test/LinkId");
    static final Value LINK_TITLE = new LiteralImpl("The linkset");
    static final String LINK_DESCRIPTION_STRING = "The linkset tester";
    static final Value LINK_DESCRIPTION_VALUE = new LiteralImpl(LINK_DESCRIPTION_STRING);
    static final URI LINK_PERSON = new URIImpl ("http://www.example.com/test/LinkPerson");

    Statement linkIdStatement = new StatementImpl(LINK_ID, RdfConstants.TYPE_URI, VoidConstants.LINKSET); 
    Statement linkTitleStatement = new StatementImpl(LINK_ID, DctermsConstants.TITLE, LINK_TITLE);
    Statement linkDescriptionStatement = new StatementImpl(LINK_ID, DctermsConstants.DESCRIPTION, LINK_DESCRIPTION_VALUE);
    Statement linkLicenseStatement = new StatementImpl(LINK_ID, DctermsConstants.LICENSE, LICENSE);
    Statement linkAuthoredByStatement = new StatementImpl(LINK_ID, PavConstants.AUTHORED_BY, LINK_PERSON);
    Statement linkAuthoredOnStatement;
    Statement linkCreatedByStatement = new StatementImpl(LINK_ID, PavConstants.CREATED_BY, LINK_PERSON);
    Statement linkCreatedOnStatement;
    Statement linkPredicateStatement = new StatementImpl(LINK_ID, VoidConstants.LINK_PREDICATE, SkosConstants.CLOSE_MATCH);
    BigInteger TEN = new BigInteger("10");
    Statement linkNumberStatement = new StatementImpl(LINK_ID, VoidConstants.TRIPLES, new IntegerLiteralImpl(TEN));
    Statement subjectStatement = new StatementImpl(LINK_ID, VoidConstants.SUBJECTSTARGET, ID);
    Statement objectStatement = new StatementImpl(LINK_ID, VoidConstants.OBJECTSTARGET, D2_ID);
    
    public LinkSetMetaDataTest() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        Value now = new CalendarLiteralImpl(date2);
        linkAuthoredOnStatement = new StatementImpl(LINK_ID, PavConstants.AUTHORED_ON, now);  
        linkCreatedOnStatement = new StatementImpl(LINK_ID, PavConstants.CREATED_ON, now);  
    }

    /**
     * Intentionally not in the constructor so tests can change or remove a statement before loading.
     * @return 
     */
    RDFData loadRDFData(){
        //Add first dataset
        RDFData data = super.loadRDFData();
        
        //Add second dataset
        data.addStatement(d2IdStatement);
        data.addStatement(d2TitleStatement);
        data.addStatement(d2DescriptionStatement);
        data.addStatement(d2HomePageStatement);
        data.addStatement(d2LicenseStatement);
        data.addStatement(d2NameSpaceStatement);
        data.addStatement(d2VersionStatement);
        data.addStatement(d2DataDumpStatement);
        data.addStatement(d2ModifiedStatement);
        data.addStatement(d2VocabularyStatement1);
        data.addStatement(d2VocabularyStatement2);
        data.addStatement(d2TopicStatement);
        data.addStatement(d2ExampleStatement1);
        data.addStatement(d2ExampleStatement2);
        data.addStatement(d2ExampleStatement3);
        data.addStatement(d2FocStatement);
        
        //Add linkset
        data.addStatement(linkIdStatement); 
        data.addStatement(linkTitleStatement);
        data.addStatement(linkDescriptionStatement );
        data.addStatement(linkLicenseStatement);
        data.addStatement(linkAuthoredByStatement);
        data.addStatement(linkAuthoredOnStatement);
        data.addStatement(linkCreatedByStatement);
        data.addStatement(linkCreatedOnStatement);
        data.addStatement(linkPredicateStatement);
        data.addStatement(linkNumberStatement);
        data.addStatement(subjectStatement);
        data.addStatement(objectStatement);
        return data;
    }
    
    @Test
    public void testShowAll(){
        Reporter.report("ShowAll");
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        String showAll = metaData.showAll(RequirementLevel.MAY);
        System.out.println(showAll);
    } 

    @Test
    public void testHasRequiredValues(){
        Reporter.report("HasRequiredValues");
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertTrue(metaData.hasRequiredValues(RequirementLevel.MUST, ALLOW_ALTERATIVES));
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY, ALLOW_ALTERATIVES));
    } 

    @Test
    public void testHasNoSubject(){
        Reporter.report("HasNoSubject");
        subjectStatement = null;
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MUST, ALLOW_ALTERATIVES));
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY, ALLOW_ALTERATIVES));
    } 

    @Test
    public void testHasNoObject(){
        Reporter.report("HasNoObject");
        objectStatement = null;
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MUST, ALLOW_ALTERATIVES));
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY, ALLOW_ALTERATIVES));
    } 

    @Test
    public void testAutoFindId(){
        Reporter.report("AutoFindId");
        LinkSetMetaData metaData = new LinkSetMetaData(loadRDFData());
        assertTrue(metaData.hasRequiredValues(RequirementLevel.MUST, ALLOW_ALTERATIVES));
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY, ALLOW_ALTERATIVES));
    } 

    @Test
    public void testMissingRequiredValue(){
        Reporter.report("HasMissingRequiredValues");
        linkPredicateStatement = null;
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertFalse(metaData.hasRequiredValues(RequirementLevel.TECHNICAL_MUST, ALLOW_ALTERATIVES));
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MUST, ALLOW_ALTERATIVES));
    } 

    @Test
    public void testMissingRequiredValue2(){
        Reporter.report("HasMissingRequiredValues2");
        licenseStatement = null;
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertTrue(metaData.hasRequiredValues(RequirementLevel.TECHNICAL_MUST, ALLOW_ALTERATIVES));
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MUST, ALLOW_ALTERATIVES));
    } 

    @Test
    public void testAlternativengRequiredValue(){
        Reporter.report("HasRequiredValues");
        versionStatement = null;
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertTrue(metaData.hasRequiredValues(RequirementLevel.MUST, ALLOW_ALTERATIVES));
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MUST, NO_ALTERATIVES));
    } 

    @Test
    public void testHasCorrectTypes(){
        Reporter.report("HasCorrectTypes");
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertTrue(metaData.hasCorrectTypes());
    }
    
    @Test
    public void testHasCorrectTypesBadDate(){
        Reporter.report("isHasCorrectTypesBadDate");
        modifiedStatement = new StatementImpl(ID, DctermsConstants.MODIFIED, TITLE);  
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertFalse(metaData.hasCorrectTypes());
    }
 
    @Test
    public void testValidityReport(){
        Reporter.report("ValidityReport");
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertEquals(MetaData.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MUST, ALLOW_ALTERATIVES, INCLUDE_WARNINGS));
    }

    @Test
    public void testMissingValidityReport(){
        Reporter.report("MissingValidityReport");
        titleStatement = null;
        LinkSetMetaData metaData = new LinkSetMetaData(LINK_ID, loadRDFData());
        assertNotSame(MetaData.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MUST, ALLOW_ALTERATIVES, INCLUDE_WARNINGS));
    }
}
