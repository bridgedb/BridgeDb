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
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.rdf.reader.UrlReader;
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

public class UrlTest extends TestUtils{
    
    public static boolean FILE_HAS_EXTRA_RDF = false;
    public static boolean FILE_HAS_ONLY_EXPECTED_RDF = true;
    public static String LINK_FILE = "test-data/chemspider2chemblrdf-linkset.ttl";

    private void checkUrl(String address, int numberOfIds, boolean checkAllStatements, MetaDataSpecification registry) throws BridgeDBException 
            {
        try{
            report("Checking " + address);
            StatementReader reader = new StatementReader(address);
            Set<Statement> statements = reader.getVoidStatements();
            MetaDataCollection metaData = new MetaDataCollection(address, statements, registry);
            checkCorrectNumberOfIds (metaData, numberOfIds);
            checkRequiredValues(metaData);
            checkCorrectTypes(metaData);
            if (checkAllStatements){
                checkAllStatementsUsed(metaData);
            }
            metaData.validate();
        } catch (BridgeDBException ex){
            UrlReader reader = new UrlReader(address);
            InputStream stream = null;
            boolean ioError = false;
            try {
                stream = reader.getInputStream();
            } catch (IOException ex1) {
                ioError = true;
                System.err.println("**** SKIPPPING tests due to Connection error.");
                System.err.println (ex1.toString());
                org.junit.Assume.assumeTrue(false); 
            } finally {
                if (stream != null){
                    try {
                        stream.close();
                    } catch (IOException ex1) {
                        ioError = true;
                    }
                }
            }
            throw ex;
        }
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
    public void testDrugbank() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/drugbank_void.ttl#db-drugs", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testChebi93() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi93_void.ttl", 2, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 
 
    @Test
    public void testChebi99() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi99_void.ttl", 2, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    @Ignore //Uses in void:inDataset
    public void testChebiHasPartsLinkset100() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebiHasPartsLinkset100.ttl", 2, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    public void testChebiVoid100() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebiVoid100.ttl", 3, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 
    
    @Test
    public void testChemblRdfVoid() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chembl-rdf-void.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 
    
    @Test
    public void testChemspiderVoid() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider-void.ttl", 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    @Ignore //remote subjects
    public void testChemspider2ChemblrdfLinkset() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider2chemblrdf-linkset.ttl", 4, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 

    @Test
    @Ignore //remote subjects and BNodeImpl instead of URI
    public void testChemspider2DrugbankLinkset() throws IDMapperException{
        MetaDataSpecification dataSetRegistry = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
        checkUrl("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider2drugbank-linkset.ttl", 5, FILE_HAS_EXTRA_RDF, dataSetRegistry);
    } 
}
