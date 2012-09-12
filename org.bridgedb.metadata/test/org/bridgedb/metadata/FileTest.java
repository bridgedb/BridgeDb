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
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class FileTest extends TestUtils{
    
    public static boolean FILE_HAS_EXTRA_RDF = false;
    public static boolean FILE_HAS_ONLY_EXPECTED_RDF = true;
    
    
    private void checkFile(String fileName, boolean checkAllStatements) throws MetaDataException{
        Reporter.report("Checking " + fileName);
        File input = new File(fileName);
        Set<Statement> statements = StatementReader.extractStatements(input);
        MetaDataCollection metaData = new MetaDataCollection(statements);
        checkRequiredValues(metaData, RequirementLevel.MUST);
        checkCorrectTypes(metaData);
        if (checkAllStatements){
            checkAllStatementsUsed(metaData);
        }
    }
    
    @Test
    public void testChebiHasPartsLinkset() throws MetaDataException{
        checkFile("test-data/chebiHasPartsLinkset.ttl", FILE_HAS_EXTRA_RDF);
    } 

    @Test
    public void testChebiHasPartsLinksetSmall() throws MetaDataException{
        checkFile("test-data/chebiHasPartsLinksetSmall.ttl", FILE_HAS_ONLY_EXPECTED_RDF);
    } 
}
