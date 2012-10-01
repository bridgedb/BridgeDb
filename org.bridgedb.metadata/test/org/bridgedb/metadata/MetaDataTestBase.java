/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.metadata.constants.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
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
public class MetaDataTestBase extends TestUtils{
    
    static final BigInteger TEN = new BigInteger("10");

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
    static final URI SOFTWARE = new URIImpl("http://www.example.com/SomeSoftware");
    static final URI EXAMPLE1 = new URIImpl("http://www.example.com/data#A1");
    static final URI EXAMPLE2 = new URIImpl("http://www.example.com/data#B2");
    static final URI EXAMPLE3 = new URIImpl("http://www.example.com/data#C3");
    static final URI PERSON = new URIImpl("http://www.example.com/person#Joe");
    static final Resource D2_ID = new URIImpl ("http://www.example.com/test/dataset2");
    static final Value D2_TITLE = new LiteralImpl("The second data set");
    static final String D2_DESCRIPTION_STRING = "The second dataset description";
    static final Value D2_DESCRIPTION_VALUE = new LiteralImpl(D2_DESCRIPTION_STRING);
    static final String D2_NAME_SPACE_STRING = "http://www.example.org/data#";
    static final Value D2_NAME_SPACE_VALUE = new LiteralImpl(D2_NAME_SPACE_STRING);
    static final URI D2_EXAMPLE1 = new URIImpl("http://www.example.org/data#1A");
    static final URI D2_EXAMPLE2 = new URIImpl("http://www.example.org/data#2B");
    static final URI D2_EXAMPLE3 = new URIImpl("http://www.example.org/data#3C");
    static final Resource LINK_ID = new URIImpl ("http://www.example.com/test/linkset1");
    static final Value LINK_TITLE = new LiteralImpl("The linkset");
    static final String LINK_DESCRIPTION_STRING = "The linkset tester";
    static final Value LINK_DESCRIPTION_VALUE = new LiteralImpl(LINK_DESCRIPTION_STRING);
    static final URI LINK_PERSON = new URIImpl ("http://www.example.com/test/LinkPerson");
    static final URI JUSTIFICATION = new URIImpl ("http://www.example.com/test/Justification");
    static final URI BIWEEKLY = new URIImpl ("http://purl.org/cld/freq/biennial");
      
    Statement d1IdStatement = new StatementImpl(D1_ID, RdfConstants.TYPE_URI, VoidConstants.DATASET); 
    Statement d1TitleStatement = new StatementImpl(D1_ID, DctermsConstants.TITLE, TITLE);
    Statement d1DescriptionStatement = new StatementImpl(D1_ID, DctermsConstants.DESCRIPTION, DESCRIPTION_VALUE);
    Statement d1HomePageStatement = new StatementImpl(D1_ID, FoafConstants.HOMEPAGE, HOME_PAGE);
    Statement d1LlicenseStatement = new StatementImpl(D1_ID, DctermsConstants.LICENSE, LICENSE);
    Statement d1NnameSpaceStatement = new StatementImpl(D1_ID, VoidConstants.URI_SPACE, NAME_SPACE_VALUE);
    Statement d1VersionStatement = new StatementImpl(D1_ID, PavConstants.VERSION, VERSION_VALUE);
    Statement d1DataDumpStatement = new StatementImpl(D1_ID, VoidConstants.DATA_DUMP, DATA_DUMP);  
    Statement d1PublishedStatement = new StatementImpl(D1_ID, DctermsConstants.PUBLISHER, DATA_DUMP);  
    Statement d1ModifiedStatement;  // incontrustor due to date!
    Statement d1CreatedStatement;  // incontrustor due to date!
    Statement d1RetreivedFromStatement = new StatementImpl(D1_ID, PavConstants.RETRIEVED_FROM, HOME_PAGE);
    Statement d1RetreivedOn;  // incontrustor due to date!
    Statement d1RetreivedByStatement = new StatementImpl(D1_ID, PavConstants.RETRIEVED_BY, PERSON);
    Statement d1CreatedWithStatement = new StatementImpl(D1_ID, PavConstants.CREATED_WITH, SOFTWARE);
    Statement d1VocabularyStatement1 = new StatementImpl(D1_ID, VoidConstants.VOCABULARY, VOCABULARY1);
    Statement d1VocabularyStatement2 = new StatementImpl(D1_ID, VoidConstants.VOCABULARY, VOCABULARY2);
    Statement d1TopicStatement = new StatementImpl(D1_ID, DctermsConstants.SUBJECT, TOPIC);
    Statement d1ExampleStatement1 = new StatementImpl(D1_ID, VoidConstants.EXAMPLE_RESOURCE, EXAMPLE1);
    Statement d1ExampleStatement2 = new StatementImpl(D1_ID, VoidConstants.EXAMPLE_RESOURCE, EXAMPLE2);
    Statement d1ExampleStatement3 = new StatementImpl(D1_ID, VoidConstants.EXAMPLE_RESOURCE, EXAMPLE3);
    Statement d1FocStatement = new StatementImpl(D1_ID, VoagConstants.FREQUENCY_OF_CHANGE, BIWEEKLY);
    //Data Set 2
    Statement d2IdStatement = new StatementImpl(D2_ID, RdfConstants.TYPE_URI, VoidConstants.DATASET); 
    Statement d2TitleStatement = new StatementImpl(D2_ID, DctermsConstants.TITLE, D2_TITLE);
    Statement d2DescriptionStatement = new StatementImpl(D2_ID, DctermsConstants.DESCRIPTION, D2_DESCRIPTION_VALUE);
    Statement d2HomePageStatement = new StatementImpl(D2_ID, FoafConstants.HOMEPAGE, HOME_PAGE);
    Statement d2LicenseStatement = new StatementImpl(D2_ID, DctermsConstants.LICENSE, LICENSE);
    Statement d2NameSpaceStatement = new StatementImpl(D2_ID, VoidConstants.URI_SPACE, D2_NAME_SPACE_VALUE);
    Statement d2VersionStatement = new StatementImpl(D2_ID, PavConstants.VERSION, VERSION_VALUE);
    Statement d2DataDumpStatement = new StatementImpl(D2_ID, VoidConstants.DATA_DUMP, DATA_DUMP);  
    Statement d2ImportedFromStatement = new StatementImpl(D2_ID, PavConstants.IMPORTED_FROM, HOME_PAGE);
    Statement d2ImportedOnStatement;  // incontrustor due to date!
    Statement d2ImportedByStatement = new StatementImpl(D2_ID, PavConstants.IMPORTED_BY, PERSON);
    Statement d2CreatedWithStatement = new StatementImpl(D2_ID, PavConstants.CREATED_WITH, SOFTWARE);
    Statement d2VocabularyStatement1 = new StatementImpl(D2_ID, VoidConstants.VOCABULARY, VOCABULARY1);
    Statement d2VocabularyStatement2 = new StatementImpl(D2_ID, VoidConstants.VOCABULARY, VOCABULARY2);
    Statement d2TopicStatement = new StatementImpl(D2_ID, DctermsConstants.SUBJECT, TOPIC);
    Statement d2ExampleStatement1 = new StatementImpl(D2_ID, VoidConstants.EXAMPLE_RESOURCE, D2_EXAMPLE1);
    Statement d2ExampleStatement2 = new StatementImpl(D2_ID, VoidConstants.EXAMPLE_RESOURCE, D2_EXAMPLE2);
    Statement d2ExampleStatement3 = new StatementImpl(D2_ID, VoidConstants.EXAMPLE_RESOURCE, D2_EXAMPLE3);
    Statement d2FocStatement = new StatementImpl(D2_ID, VoagConstants.FREQUENCY_OF_CHANGE, BIWEEKLY);

    //Linkset
    Statement linkIdStatement = new StatementImpl(LINK_ID, RdfConstants.TYPE_URI, VoidConstants.LINKSET); 
    Statement linkTitleStatement = new StatementImpl(LINK_ID, DctermsConstants.TITLE, LINK_TITLE);
    Statement linkDescriptionStatement = new StatementImpl(LINK_ID, DctermsConstants.DESCRIPTION, LINK_DESCRIPTION_VALUE);
    Statement linkLicenseStatement = new StatementImpl(LINK_ID, DctermsConstants.LICENSE, LICENSE);
    Statement linkAuthoredByStatement = new StatementImpl(LINK_ID, PavConstants.AUTHORED_BY, LINK_PERSON);
    Statement linkAuthoredOnStatement;
    Statement linkCreatedByStatement = new StatementImpl(LINK_ID, PavConstants.CREATED_BY, LINK_PERSON);
    Statement linkCreatedOnStatement;
    Statement linkPredicateStatement = new StatementImpl(LINK_ID, VoidConstants.LINK_PREDICATE, SkosConstants.CLOSE_MATCH);
    Statement linkJustificationStatement = new StatementImpl(LINK_ID, DulConstants.EXPRESSES, JUSTIFICATION);
    
    Statement linkNumberStatement = new StatementImpl(LINK_ID, VoidConstants.TRIPLES, new IntegerLiteralImpl(TEN));
    Statement subjectStatement = new StatementImpl(LINK_ID, VoidConstants.SUBJECTSTARGET, D1_ID);
    Statement objectStatement = new StatementImpl(LINK_ID, VoidConstants.OBJECTSTARGET, D2_ID);

    static MetaDataRegistry dataSetRegistry;
    static MetaDataRegistry linksetSetRegistry;
         
    @BeforeClass
    public static void loadRegistries() throws MetaDataException{
        dataSetRegistry = new MetaDataRegistry("file:resources/shouldDataSet.owl");        
    }
    
    public MetaDataTestBase() throws DatatypeConfigurationException, MetaDataException {
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        Value now = new CalendarLiteralImpl(date2);
        d1ModifiedStatement = new StatementImpl(D1_ID, DctermsConstants.MODIFIED, now);  
        d1CreatedStatement = new StatementImpl(D1_ID, DctermsConstants.CREATED, now);  
        d1RetreivedOn = new StatementImpl(D1_ID, PavConstants.RETRIEVED_ON, now);          
        d2ImportedOnStatement = new StatementImpl(D2_ID, PavConstants.IMPORTED_ON, now);          
        linkAuthoredOnStatement = new StatementImpl(LINK_ID, PavConstants.AUTHORED_ON, now);  
        linkCreatedOnStatement = new StatementImpl(LINK_ID, PavConstants.CREATED_ON, now);
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
        addStatement(data, d1IdStatement);
        addStatement(data, d1TitleStatement);
        addStatement(data, d1DescriptionStatement);
        addStatement(data, d1HomePageStatement);
        addStatement(data, d1LlicenseStatement);
        addStatement(data, d1NnameSpaceStatement);
        //addStatement(data, d1VersionStatement);
        addStatement(data, d1DataDumpStatement);
        addStatement(data, d1PublishedStatement);
        addStatement(data, d1ModifiedStatement);
        addStatement(data, d1CreatedStatement);
        addStatement(data, d1RetreivedFromStatement);
        addStatement(data, d1RetreivedOn);
        addStatement(data, d1RetreivedByStatement);
        addStatement(data, d1CreatedWithStatement);
        addStatement(data, d1VocabularyStatement1);
        addStatement(data, d1VocabularyStatement2);
        addStatement(data, d1TopicStatement);
        addStatement(data, d1ExampleStatement1);
        addStatement(data, d1ExampleStatement2);
        addStatement(data, d1ExampleStatement3);
        data.add(d1FocStatement);
        return data;
    }
    
    /**
     * Intentionally not in the constructor so tests can change or remove a statement before loading.
     * @return 
     */
    Set<Statement> loadDataSet2(){
        Set<Statement> data = new HashSet<Statement>();
        addStatement(data, d2IdStatement);
        addStatement(data, d2TitleStatement);
        addStatement(data, d2DescriptionStatement);
        addStatement(data, d2HomePageStatement);
        addStatement(data, d2LicenseStatement);
        addStatement(data, d2NameSpaceStatement);
        //addStatement(data, d2VersionStatement);
        addStatement(data, d2DataDumpStatement);
        addStatement(data, d2ImportedOnStatement);
        addStatement(data, d2ImportedByStatement);
        addStatement(data, d2ImportedFromStatement);
        addStatement(data, d2CreatedWithStatement);
        addStatement(data, d2VocabularyStatement1);
        addStatement(data, d2VocabularyStatement2);
        addStatement(data, d2TopicStatement);
        addStatement(data, d2ExampleStatement1);
        addStatement(data, d2ExampleStatement2);
        addStatement(data, d2ExampleStatement3);
        addStatement(data, d2FocStatement);
        return data;
    }
    
    /**
     * Intentionally not in the constructor so tests can change or remove a statement before loading.
     * @return 
     */
    Set<Statement> loadLinkSet(){
        Set<Statement> data = loadDataSet1();
        data.addAll(loadDataSet2());
        addStatement(data, linkIdStatement); 
        addStatement(data, linkTitleStatement);
        addStatement(data, linkDescriptionStatement );
        addStatement(data, linkLicenseStatement);
        addStatement(data, linkAuthoredByStatement);
        addStatement(data, linkAuthoredOnStatement);
        addStatement(data, linkCreatedByStatement);
        addStatement(data, linkCreatedOnStatement);
        addStatement(data, linkPredicateStatement);
        addStatement(data, linkJustificationStatement);
        addStatement(data, linkNumberStatement);
        addStatement(data, subjectStatement);
        addStatement(data, objectStatement);
        return data;
    }


}
