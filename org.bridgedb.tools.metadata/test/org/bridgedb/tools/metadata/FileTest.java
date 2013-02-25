// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.tools.metadata;

import java.io.File;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.tools.metadata.validator.MetaDataSpecificationRegistry;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import static org.hamcrest.Matchers.*;
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
    public static String LINK_FILE = "test-data/chemspider2chemblrdf-linkset.ttl";

    private void checkFile(String fileName, int numberOfIds, boolean checkAllStatements, MetaDataSpecification registry) 
            throws BridgeDBException{
        report("Checking " + fileName);
        File input = new File(fileName);
        StatementReader reader = new StatementReader(input);
        Set<Statement> statements = reader.getVoidStatements();
        MetaDataCollection metaData = new MetaDataCollection(fileName, statements, registry);
        checkCorrectNumberOfIds (metaData, numberOfIds);
        checkRequiredValues(metaData);
        checkCorrectTypes(metaData);
        if (checkAllStatements){
            checkAllStatementsUsed(metaData);
        }
        metaData.validate();
    }
    
    private void validateFile(String fileName, int numberOfIds, boolean checkAllStatements, MetaDataSpecification registry) throws BridgeDBException{
        report("Checking " + fileName);
        File input = new File(fileName);
        StatementReader reader = new StatementReader(input);
        Set<Statement> statements = reader.getVoidStatements();
        MetaDataCollection metaData = new MetaDataCollection(fileName, statements, registry);
        checkCorrectNumberOfIds (metaData, numberOfIds);
        String report = metaData.validityReport(NO_WARNINGS);
        assertThat(report, not(containsString("ERROR")));
    }

    private void validateFile(String fileName, boolean checkAllStatements, MetaDataSpecification registry) throws BridgeDBException{
        report("Checking " + fileName);
        File input = new File(fileName);
        StatementReader reader = new StatementReader(input);
        Set<Statement> statements = reader.getVoidStatements();
        report("Read " + fileName);
        MetaDataCollection metaData = new MetaDataCollection(fileName, statements, registry);
        report("Loaded " + fileName);
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
   public void testChemblRdfVoidTtl() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkFile("test-data/chembl-rdf-void.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testchemspider2chemblrdflinksetSubSetAsVoid() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkFile("test-data/chemspider2chemblrdf-linksetSubSet.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testchemspider2chemblrdflinksetSubSetAsLinks() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.LINKS);
        checkFile("test-data/chemspider2chemblrdf-linksetSubSet.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
@Ignore    
    public void testchemspider2chemblrdflinksetSubSet_1AsLinks() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.LINKS);
        checkFile("test-data/chemspider2chemblrdf-linksetSubSet_1.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    }  
    @Test
    public void testLINK_FILE() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.LINKS);
        checkFile(LINK_FILE, 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    @Ignore
    public void testLinksetFirstTtl() throws BridgeDBException, IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.LINKSMINIMAL);
        checkFile("test-data/linksetFirst.ttl", 3, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 
   
}
