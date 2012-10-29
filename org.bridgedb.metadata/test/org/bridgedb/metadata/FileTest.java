/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.io.File;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.validator.MetaDataSpecificationRegistry;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.utils.Reporter;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Statement;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public class FileTest extends TestUtils{
    
    public static boolean FILE_HAS_EXTRA_RDF = false;
    public static boolean FILE_HAS_ONLY_EXPECTED_RDF = true;
    public static String LINK_FILE = "test-data/chemspider2chemblrdf-linkset.ttl";
    
    private void checkFile(String fileName, int numberOfIds, boolean checkAllStatements, MetaDataSpecification registry) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        MetaDataCollection metaData = new MetaDataCollection(statements, registry);
        checkCorrectNumberOfIds (metaData, numberOfIds);
        checkRequiredValues(metaData);
        checkCorrectTypes(metaData);
        if (checkAllStatements){
            checkAllStatementsUsed(metaData);
        }
        metaData.validate();
    }
    
    private void validateFile(String fileName, int numberOfIds, boolean checkAllStatements, MetaDataSpecification registry) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        MetaDataCollection metaData = new MetaDataCollection(statements, registry);
        checkCorrectNumberOfIds (metaData, numberOfIds);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }

    private void validateFile(String fileName, boolean checkAllStatements, MetaDataSpecification registry) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        Reporter.report("Read " + fileName);
        MetaDataCollection metaData = new MetaDataCollection(statements, registry);
        Reporter.report("Loaded " + fileName);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }

    @Test
    public void testChemspider() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkFile("test-data/chemspider-void.ttl", 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    @Ignore
    public void testChemspiderSmall() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkFile("test-data/chemspider-void-small.ttl", 4, FILE_HAS_ONLY_EXPECTED_RDF, dataSetRegistry);
   } 

    @Test
    public void testChemblRdfVoidTtl() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkFile("test-data/chembl-rdf-void.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testLINK_FILE() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.LINKS);
        checkFile(LINK_FILE, 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    @Ignore
    public void testLinksetFirstTtl() throws MetaDataException, IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.LINKSMINIMAL);
        checkFile("test-data/linksetFirst.ttl", 3, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 
}
