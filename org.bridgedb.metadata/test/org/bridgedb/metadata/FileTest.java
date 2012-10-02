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
    
    
    private void checkFile(String fileName, int numberOfIds, boolean checkAllStatements, MetaDataRegistry registry) throws MetaDataException{
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
    
    private void validateFile(String fileName, int numberOfIds, boolean checkAllStatements, MetaDataRegistry registry) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        MetaDataCollection metaData = new MetaDataCollection(statements, registry);
        checkCorrectNumberOfIds (metaData, numberOfIds);
        Reporter.report(metaData.validityReport(INCLUDE_WARNINGS));
    }

    private void validateFile(String fileName, boolean checkAllStatements, MetaDataRegistry registry) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        Reporter.report("Read " + fileName);
        MetaDataCollection metaData = new MetaDataCollection(statements, registry);
        Reporter.report("Loaded " + fileName);
        Reporter.report(metaData.validityReport(INCLUDE_WARNINGS));
    }

    @Test
    public void testChemspider() throws MetaDataException{
        MetaDataRegistry dataSetRegistry = new MetaDataRegistry("file:resources/shouldDataSet.owl");
        checkFile("test-data/chemspider-void.ttl", 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testChemspiderSmall() throws MetaDataException{
        MetaDataRegistry dataSetRegistry = new MetaDataRegistry("file:resources/shouldDataSet.owl");
        checkFile("test-data/chemspider-void-small.ttl", 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
        //checkFile("test-data/chemspider-void-small.ttl", 4, FILE_HAS_ONLY_EXPECTED_RDF, dataSetRegistry);
    } 

    @Test
    public void testChemblRdfVoidTtl() throws MetaDataException{
        MetaDataRegistry dataSetRegistry = new MetaDataRegistry("file:resources/shouldDataSet.owl");
        checkFile("test-data/chembl-rdf-void.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testl() throws MetaDataException{
        MetaDataRegistry dataSetRegistry = new MetaDataRegistry("file:resources/shouldLinkSet.owl");
        checkFile("C://temp/cs-chebi-stereo_2012-07-31.ttl", 4, FILE_HAS_ONLY_EXPECTED_RDF, dataSetRegistry);
    } 

    @Test
    @Ignore
    public void testchemspider2chemblrdfLinksetTtl() throws MetaDataException{
        MetaDataRegistry dataSetRegistry = new MetaDataRegistry("file:resources/shouldOwl.owl");
        checkFile("test-data/chemspider2chemblrdf-linkset.ttl", 2, FILE_HAS_ONLY_EXPECTED_RDF, dataSetRegistry);
    } 
 }
