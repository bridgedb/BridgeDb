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
    
    
    private void checkFile(String fileName, int numberOfIds, boolean checkAllStatements) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        MetaDataCollection metaData = new MetaDataCollection(statements);
        checkCorrectNumberOfIds (metaData, numberOfIds);
        checkRequiredValues(metaData, RequirementLevel.SHOULD);
        checkCorrectTypes(metaData);
        if (checkAllStatements){
            checkAllStatementsUsed(metaData);
        }
    }
    
    private void validateFile(String fileName, int numberOfIds, boolean checkAllStatements) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        MetaDataCollection metaData = new MetaDataCollection(statements);
        checkCorrectNumberOfIds (metaData, numberOfIds);
        Reporter.report(metaData.validityReport(RequirementLevel.SHOULD, INCLUDE_WARNINGS));
    }

    private void validateFile(String fileName, boolean checkAllStatements) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        Reporter.report("Read " + fileName);
        MetaDataCollection metaData = new MetaDataCollection(statements);
        Reporter.report("Loaded " + fileName);
        Reporter.report(metaData.validityReport(RequirementLevel.SHOULD, INCLUDE_WARNINGS));
    }

    @Test
    public void testChemspider() throws MetaDataException{
        checkFile("test-data/chemspider-void.ttl", 4, FILE_HAS_EXTRA_RDF);
    } 

    @Test
    public void testChemspiderSmall() throws MetaDataException{
        checkFile("test-data/chemspider-void-small.ttl", 4, FILE_HAS_ONLY_EXPECTED_RDF);
    } 

    @Test
    @Ignore
    public void testChemblRdfVoidTtl() throws MetaDataException{
        checkFile("test-data/chembl-rdf-void.ttl", 5, FILE_HAS_ONLY_EXPECTED_RDF);
    } 

    @Test
    @Ignore
    public void testchemspider2chemblrdfLinksetTtl() throws MetaDataException{
        checkFile("test-data/chemspider2chemblrdf-linkset.ttl", 2, FILE_HAS_ONLY_EXPECTED_RDF);
    } 
 }
