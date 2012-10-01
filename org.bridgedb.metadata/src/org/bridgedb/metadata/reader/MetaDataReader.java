/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.reader;

import java.io.File;
import java.util.Set;
import org.bridgedb.metadata.MetaDataCollection;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.rdf.RdfException;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.rdf.RdfController;
import org.bridgedb.rdf.RepositoryFactory;
import org.bridgedb.rdf.WrappedRepository;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class MetaDataReader {
    
 /*   public String readMetaData(String fileName, RdfStoreType rdfStoreType) throws MetaDataException, RdfException{
        File input = new File(fileName);
        String validatebase = RdfController.getValidateBase();
        Set<Statement> statements = StatementReader.extractStatements(input, validatebase);
        MetaDataCollection metaData = new MetaDataCollection(statements);
        metaData.validate();
        String contextString = RdfController.getNextContext(rdfStoreType);
        validatebase = contextString + "/";
        statements = StatementReader.extractStatements(input, validatebase);
        metaData = new MetaDataCollection(statements);
        URI context = new URIImpl(contextString);
        Set<Statement> voidStatements = metaData.getRDF();
        WrappedRepository repository = RepositoryFactory.getRepository(rdfStoreType);
        repository.addStatements(voidStatements, context);
        String loadSummary = "Loaded " + context + "\n";
        String summary = metaData.summary();
        return loadSummary + summary;
    }*/
}
