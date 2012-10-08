/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.io.File;
import java.util.Set;
import org.bridgedb.metadata.utils.Reporter;
import org.bridgedb.rdf.StatementReader;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class FileTest extends TestUtils{
    
    public static boolean FILE_HAS_EXTRA_RDF = false;
    public static boolean FILE_HAS_ONLY_EXPECTED_RDF = true;
    
    
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
    }
    
    private void validateFile(String fileName, int numberOfIds, boolean checkAllStatements, MetaDataSpecification registry) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        MetaDataCollection metaData = new MetaDataCollection(statements, registry);
        checkCorrectNumberOfIds (metaData, numberOfIds);
        Reporter.report(metaData.validityReport(INCLUDE_WARNINGS));
    }

    private void validateFile(String fileName, boolean checkAllStatements, MetaDataSpecification registry) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        Reporter.report("Read " + fileName);
        MetaDataCollection metaData = new MetaDataCollection(statements, registry);
        Reporter.report("Loaded " + fileName);
        Reporter.report(metaData.validityReport(INCLUDE_WARNINGS));
    }

    public void testChemspider() throws MetaDataException{
        MetaDataSpecification dataSetRegistry = new MetaDataSpecification("file:resources/shouldDataSet.owl");
        checkFile("test-data/chemspider-void.ttl", 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testChemspiderSmall() throws MetaDataException{
        MetaDataSpecification dataSetRegistry = new MetaDataSpecification("file:resources/shouldDataSet.owl");
        checkFile("test-data/chemspider-void-small.ttl", 4, FILE_HAS_ONLY_EXPECTED_RDF, dataSetRegistry);
   } 

    @Test
    public void testChemblRdfVoidTtl() throws MetaDataException{
        MetaDataSpecification dataSetRegistry = new MetaDataSpecification("file:resources/shouldDataSet.owl");
        checkFile("test-data/chembl-rdf-void.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testchemspider2chemblrdfLinksetTtl() throws MetaDataException{
        MetaDataSpecification dataSetRegistry = new MetaDataSpecification("file:resources/shouldLinkSet.owl");
        checkFile("test-data/chemspider2chemblrdf-linkset.ttl", 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 
 }
