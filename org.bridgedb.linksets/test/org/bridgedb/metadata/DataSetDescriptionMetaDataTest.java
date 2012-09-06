/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.FoafConstants;
import org.bridgedb.linkset.constants.FrequencyOfChange;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.VoagConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.bridgedb.metadata.reader.MetaDataFactory;
import org.bridgedb.utils.Reporter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
@Ignore
public class DataSetDescriptionMetaDataTest extends DataSetMetaDataTest{
    
    static final Resource DESCRIPTION_ID = new URIImpl ("http://www.example.com/test/desc");
    static final Value DESCRIPTION_TITLE = new LiteralImpl("The Void description");
    static final String DESCRIPTION_DESCRIPTION_STRING = "The void header stuff.";
    static final Value DESCRIPTION_DESCRIPTION_VALUE = new LiteralImpl(DESCRIPTION_DESCRIPTION_STRING);
    static final URI CREATED_BY = new URIImpl("http://www.example.com/test/Joe");
    static final URI PRIMARY_TOPIC = new URIImpl("http://www.example.com/test/PrimaryTopic");

    Statement descriptionIdStatement = new StatementImpl(DESCRIPTION_ID, RdfConstants.TYPE_URI, VoidConstants.DATASET_DESCRIPTION); 
    Statement descriptionTitleStatement = new StatementImpl(DESCRIPTION_ID, DctermsConstants.TITLE, TITLE);
    Statement descriptionDescriptionStatement = new StatementImpl(DESCRIPTION_ID, DctermsConstants.DESCRIPTION, DESCRIPTION_VALUE);
    Statement descriptionCreatedByStatement = new StatementImpl(DESCRIPTION_ID, PavConstants.CREATED_BY, CREATED_BY);
    Statement descriptionCreatedOnStatement;
    Statement descriptionPrimaryTopicStatement = new StatementImpl(DESCRIPTION_ID, FoafConstants.PRIMARY_TOPIC, PRIMARY_TOPIC);
    

    public DataSetDescriptionMetaDataTest() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        Value now = new CalendarLiteralImpl(date2);
        descriptionCreatedOnStatement = new StatementImpl(DESCRIPTION_ID, PavConstants.CREATED_ON, now);  
    }

    /**
     * Intentionally not in the constructor so tests can change or remove a statement before loading.
     * @return 
     */
    @Override
    RDFData loadRDFData(){
        RDFData data = super.loadRDFData();
        data.addStatement(descriptionIdStatement);
        data.addStatement(descriptionTitleStatement);
        data.addStatement(descriptionDescriptionStatement);
        data.addStatement(descriptionCreatedByStatement);
        data.addStatement(descriptionCreatedOnStatement);
        data.addStatement(descriptionPrimaryTopicStatement);
        return data;
    }
    
    @Test 
    public void testLoadWithfactory(){
        Reporter.report("LoadWithfactory");
        MetaData metaData = MetaDataFactory.makeSpecific(loadRDFData());
        assertTrue(metaData instanceof DescriptionMetaData);
    } 

    @Test
    public void testHasRequiredValues(){
        Reporter.report("HasRequiredValues");
        DescriptionMetaData metaData = new DescriptionMetaData(DESCRIPTION_ID, loadRDFData());
        checkRequiredValues(metaData, RequirementLevel.MUST);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY));
    } 

    @Test
    public void testMissingRequiredValue(){
        Reporter.report("HasMissingRequiredValues");
        descriptionTitleStatement = null;
        DescriptionMetaData metaData = new DescriptionMetaData(DESCRIPTION_ID, loadRDFData());
        checkRequiredValues(metaData, RequirementLevel.TECHNICAL_MUST);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY));
    } 

    @Test
    public void testAutoFindId(){
        Reporter.report("AutoFindId");
        DescriptionMetaData metaData = new DescriptionMetaData(loadRDFData());
        checkRequiredValues(metaData, RequirementLevel.MUST);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY));
    } 

    @Test
    public void testAlternativeRequiredValue(){
        Reporter.report("AlternativeRequiredValue");
        versionStatement = null;
        DescriptionMetaData metaData = new DescriptionMetaData(DESCRIPTION_ID, loadRDFData());
        checkRequiredValues(metaData, RequirementLevel.MUST);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MUST));
    } 
    
    @Test
    public void testHasCorrectTypes(){
        Reporter.report("HasCorrectTypes");
        DescriptionMetaData metaData = new DescriptionMetaData(DESCRIPTION_ID, loadRDFData());
        assertTrue(metaData.hasCorrectTypes());
    }
    
    @Test
    public void testHasCorrectTypesBadDate(){
        Reporter.report("isHasCorrectTypesBadDate");
        d1ModifiedStatement = new StatementImpl(D1_ID, DctermsConstants.MODIFIED, TITLE);  
        DescriptionMetaData metaData = new DescriptionMetaData(DESCRIPTION_ID, loadRDFData());
        assertFalse(metaData.hasCorrectTypes());
    }
 
    @Test
    public void testMissingValidityReport(){
        Reporter.report("MissingValidityReport");
        descriptionTitleStatement = null;
        DescriptionMetaData metaData = new DescriptionMetaData(DESCRIPTION_ID, loadRDFData());
        assertNotSame(MetaData.CLEAR_REPORT, metaData.validityReport(RequirementLevel.MAY, INCLUDE_WARNINGS));
    }
}
