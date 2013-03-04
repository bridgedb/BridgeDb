/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.File;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.tools.metadata.MetaDataCollection;
import org.bridgedb.tools.metadata.MetaDataSpecification;
import org.bridgedb.tools.metadata.validator.MetaDataSpecificationRegistry;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class TestRemoteVoidsAndLinksets {
 
    private static boolean NO_WARNINGS = false;
    private MetaDataSpecification registry;
    
    static final Logger logger = Logger.getLogger(TestRemoteVoidsAndLinksets.class);
    
    public TestRemoteVoidsAndLinksets() throws IDMapperException{
        registry = MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(ValidationType.VOID);
    }
        
    private void checkFile(String fileName, int numberOfIds) throws BridgeDBException{
        logger.info("Checking " + fileName);
        File input = new File(fileName);
        StatementReader reader = new StatementReader(input);
        check(fileName, reader, numberOfIds); 
    }
    
    private void checkUrl(String url, int numberOfIds) throws BridgeDBException{
        logger.info("Checking " + url);
        StatementReader reader = new StatementReader(url);
        check(url, reader, numberOfIds); 
    }
    
    private void check(String SourceName, StatementReader reader, int numberOfIds) throws BridgeDBException{       
        Set<Statement> statements = reader.getVoidStatements();
        MetaDataCollection metaData = new MetaDataCollection(SourceName, statements, registry);
        Set<Resource> ids = metaData.getIds();
        boolean ok = (ids.size() == numberOfIds);
        if (!ok){
            logger.error("Incorrect number of Ids found in " + SourceName + " Expected " + numberOfIds + " found " + ids.size());
        }        
        if (!metaData.hasRequiredValuesOrIsSuperset()){
            logger.error("Missing values in " + SourceName);
            ok = false;
        }             
        if (! metaData.hasCorrectTypes()){
            logger.error("Incorrect Types in " + SourceName);
            ok = false;
        }             
        String report = metaData.validityReport(NO_WARNINGS);
        if (report.contains("ERROR")){
            logger.error("Validation error in " + SourceName);
            logger.error(report);
            ok = false;
        }             
        if (ok){
            logger.info("No problems found with  " + SourceName);
        }
    }
 
    public static void main(String[] args) throws IDMapperException {
        ConfigReader.logToConsole();
        TestRemoteVoidsAndLinksets checker = new TestRemoteVoidsAndLinksets();
        checker.checkFile("../org.bridgedb.tools.metadata/test-data/chemspider-void.ttl", 4);
        checker.checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/drugbank_void.ttl#db-drugs", 5);
        checker.checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi93_void.ttl", 2);
        checker.checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebi99_void.ttl", 2);
        checker.checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebiHasPartsLinkset100.ttl", 2);
        checker.checkUrl("https://github.com/openphacts/ops-platform-setup/blob/master/void/chebi/chebiVoid100.ttl", 3);
        checker.checkUrl("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chembl-rdf-void.ttl", 5);
        checker.checkUrl("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider-void.ttl", 4);
        checker.checkUrl("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider2chemblrdf-linkset.ttl", 4);
        checker.checkUrl("https://github.com/openphacts/Documentation/blob/master/datadesc/examples/chemspider2drugbank-linkset.ttl", 5);
        checker.checkUrl("ftp://ftp.rsc-us.org/OPS/20130117/void_2013-01-17.ttl#chemSpiderDataset", 21);

        checker.checkUrl("http://ops-virtuoso.scai.fraunhofer.de/download/peregrine-dict-void.ttl", 2);
        checker.checkUrl("http://ops-virtuoso.scai.fraunhofer.de/download/pm-dict-void.ttl", 2);
    }
}
