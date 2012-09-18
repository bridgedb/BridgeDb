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
        checkRequiredValues(metaData, RequirementLevel.MUST);
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
        MetaDataCollection metaData = new MetaDataCollection(statements);
        Reporter.report(metaData.validityReport(RequirementLevel.SHOULD, INCLUDE_WARNINGS));
    }

    @Test
    @Ignore
    public void testChebiHasPartsLinkset() throws MetaDataException{
        checkFile("test-data/chebiHasPartsLinkset.ttl", 3, FILE_HAS_EXTRA_RDF);
    } 

    @Test
    @Ignore
    public void testChebiHasPartsLinksetSmall() throws MetaDataException{
        checkFile("test-data/chebiHasPartsLinksetSmall.ttl", 3, FILE_HAS_ONLY_EXPECTED_RDF);
    } 

    @Test
    public void testChristine() throws MetaDataException{
        validateFile("test-data/Christine.ttl", 5, FILE_HAS_EXTRA_RDF);
    } 

    //@Test
    //public void testAndra() throws MetaDataException{
    //    validateFile("test-data/Andra.ttl", FILE_HAS_EXTRA_RDF);
    //} 
}
