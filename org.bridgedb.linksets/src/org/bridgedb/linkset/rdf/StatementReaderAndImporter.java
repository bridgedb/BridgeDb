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
package org.bridgedb.linkset.rdf;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.tools.metadata.rdf.VoidStatements;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;

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
    
    public StatementReaderAndImporter(File file, StoreType storeType) throws BridgeDBException{
        StatementReader reader = new StatementReader(file);
        voidStatements = reader.getVoidStatements();
        loadInfo(storeType);
    }

    public StatementReaderAndImporter(String fileName, StoreType storeType) throws BridgeDBException{
        StatementReader reader = new StatementReader(fileName);
        voidStatements = reader.getVoidStatements();
        loadInfo(storeType);
    }
    
    public StatementReaderAndImporter(String info, RDFFormat format, StoreType storeType) throws BridgeDBException{
        StatementReader reader = new StatementReader(info, format);
        voidStatements = reader.getVoidStatements();
        loadInfo(storeType);
    }

    public StatementReaderAndImporter(InputStream inputStream, RDFFormat format, StoreType storeType) throws BridgeDBException{
        StatementReader reader = new StatementReader(inputStream, format);
        voidStatements = reader.getVoidStatements();
        loadInfo(storeType);
    }

    /**
     * MUST be called by ALL constructors incl superClasses AFTER they have set voidStatements 
     * @param storeType
     * @throws BridgeDBException 
     */
    protected void loadInfo(StoreType storeType) throws BridgeDBException {
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
            if (predicate.equals(VoidConstants.SUBJECTSTARGET) || predicate.equals(VoidConstants.OBJECTSTARGET)
                    || predicate.equals(VoidConstants.IN_DATASET)){
                Value value = statement.getObject();
                if (value instanceof URI){
                    URI object = (URI)value;
                    toLoadURIs.add(object);
                }
            }
        }
        return toLoadURIs;
    }

    private Set<Resource> loadExtrenalResources(Set<Resource> toLoadURIs, StoreType storeType) throws BridgeDBException {
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
