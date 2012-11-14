/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.constants.VoidConstants;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class StatementReaderAndImporter implements VoidStatements{
    
    private Set<Resource> loadedURIs;
    protected Set<Statement> voidStatements;

    /**
     * This constructor should on be called by super classes.
     * 
     * Super classes MUST set voidStatements and MUST call loadInfo
     */
    protected StatementReaderAndImporter(){
    }
    
    public StatementReaderAndImporter(File file, StoreType storeType) throws IDMapperException{
        StatementReader reader = new StatementReader(file);
        voidStatements = reader.getVoidStatements();
        loadInfo(storeType);
    }

    public StatementReaderAndImporter(String fileName, StoreType storeType) throws IDMapperException{
        StatementReader reader = new StatementReader(fileName);
        voidStatements = reader.getVoidStatements();
        loadInfo(storeType);
    }
    
    public StatementReaderAndImporter(String info, RDFFormat format, StoreType storeType) throws IDMapperException{
        StatementReader reader = new StatementReader(info, format);
        voidStatements = reader.getVoidStatements();
        loadInfo(storeType);
    }

    public StatementReaderAndImporter(InputStream inputStream, RDFFormat format, StoreType storeType) throws IDMapperException{
        StatementReader reader = new StatementReader(inputStream, format);
        voidStatements = reader.getVoidStatements();
        loadInfo(storeType);
    }

    /**
     * MUST be called by ALL constructors incl superClasses AFTER they have set voidStatements 
     * @param storeType
     * @throws IDMapperException 
     */
    protected void loadInfo(StoreType storeType) throws IDMapperException {
        loadedURIs = new HashSet<Resource>();
        Set<Resource> toLoadURIs = getToLoadResources(voidStatements);
        while (!toLoadURIs.isEmpty()){
            toLoadURIs = loadExtrenalResources(toLoadURIs, storeType);
        }
    }

    private Set<Resource> getToLoadResources(Collection<Statement> statements){
        Set<Resource> toLoadURIs = new HashSet<Resource>();
        for (Statement statement:statements){
            URI predicate = statement.getPredicate();
            if (predicate.equals(VoidConstants.SUBSET)){
                toLoadURIs.add(statement.getSubject());
            }
            if (predicate.equals(VoidConstants.SUBJECTSTARGET) || (predicate.equals(VoidConstants.OBJECTSTARGET))){
                Value value = statement.getObject();
                if (value instanceof URI){
                    URI object = (URI)value;
                    toLoadURIs.add(object);
                }
            }
        }
        return toLoadURIs;
    }

    private Set<Resource> loadExtrenalResources(Set<Resource> toLoadURIs, StoreType storeType) throws IDMapperException {
        Set<Resource> extraLoadURIs = new HashSet<Resource>();
        RdfReader liveReader = new RdfReader(StoreType.LIVE);
        boolean useLoad = storeType != StoreType.LIVE && RdfConfig.uniqueLoadRepository();
        RdfReader loadReader = new RdfReader(StoreType.LOAD);
        RdfReader testReader = new RdfReader(StoreType.TEST);
        for (Resource resource:toLoadURIs){
            if (!loadedURIs.contains(resource)){
                List<Statement> newStatements = liveReader.getStatementsForResource(resource); 
                 if (useLoad && (newStatements == null || newStatements.isEmpty())){
                    newStatements = loadReader.getStatementsForResource(resource);
                }
                if (storeType == StoreType.TEST && (newStatements == null || newStatements.isEmpty())){
                    newStatements = testReader.getStatementsForResource(resource);
                }
                newStatements.addAll(liveReader.getSuperSet(resource));
                if (useLoad) {
                    newStatements.addAll(loadReader.getSuperSet(resource));
                    }
                if (storeType == StoreType.TEST){
                    newStatements.addAll(testReader.getSuperSet(resource));
                }
                voidStatements.addAll(newStatements);
                extraLoadURIs.addAll(getToLoadResources(newStatements));
                loadedURIs.add(resource);
            }
        }
        return extraLoadURIs;
    }

    @Override
    public Set<Statement> getVoidStatements() {
        return this.voidStatements;
    }

    @Override
    public void resetBaseURI(String newBaseURI) {
        voidStatements = StatementReader.resetBaseURI(newBaseURI, voidStatements);
    }

}
