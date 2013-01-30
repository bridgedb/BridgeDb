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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 *
 * @author Christian
 */
public class RdfFactory {
    private static final Resource[] ALL_RESOURCES = new Resource[0];

    public synchronized static void clear(StoreType storeType) throws BridgeDBException{
        RdfWrapper rdfWrapper;
        try {
            rdfWrapper = setupConnection(storeType, false);
        } catch (RDFHandlerException ex) {
            throw new BridgeDBException("Unable to clear repository ", ex);
        }
        rdfWrapper.clear();
   }

    /**
     * Calling methods have a STRONG obligation to shut down the Repository! Even on Exceptions!
     * 
     * @param rdfStoreType
     * @return
     * @throws BridgeDBException 
     */
    private static Repository getRepository(StoreType storeType, boolean exisiting) throws BridgeDBException {
        File dataDir = RdfConfig.getDataDir(storeType);
        if (exisiting) {
            if (!dataDir.exists()){
                throw new BridgeDBException ("Please check RDF settings File " + dataDir + " does not exist");
            }
            if (!dataDir.isDirectory()){
               throw new BridgeDBException ("Please check RDF settings File " + dataDir + " is not a directory");
            }
        }
        Repository repository = new SailRepository(new NativeStore(dataDir));
        try {
            repository.initialize();
        } catch (Throwable ex) {
            File testLockDir = new File(dataDir, "lock");
            if (!testLockDir.canWrite()){
                try {
                    String path = RdfConfig.getProperty(ConfigReader.CONFIG_FILE_PATH_PROPERTY);
                    String source = RdfConfig.getProperty(ConfigReader.CONFIG_FILE_PATH_SOURCE_PROPERTY);
                    throw new BridgeDBException ("Unable to open repository. Possible cause is unable to write to " +
                            testLockDir.getAbsolutePath() + " Please check " + path + " set by " + source);
                } catch (BridgeDBException ex1) {
                    Logger.getLogger(RdfFactory.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            try {
                repository.shutDown();
                repository = new SailRepository(new NativeStore(dataDir));
                repository.initialize();
            } catch (Throwable ex1) {
                throw new BridgeDBException ("Error initializing and now shutting down repository ", ex1);
            }
            //throw new BridgeDBException ("Error initializing repository ", ex);
        }
        return repository;
    }

    private static boolean repositoryExists(StoreType storeType) throws BridgeDBException {
        File dataDir = RdfConfig.getDataDir(storeType);
        return dataDir.exists();
    }
    
    public static URI getLinksetURL(int linksetId) throws BridgeDBException{
        return new URIImpl(RdfConfig.getTheBaseURI() + "linkset/" + linksetId);  
    }
  
    public static URI getVoidURL(int voidId) throws BridgeDBException{
        return new URIImpl(RdfConfig.getTheBaseURI() + "void/" + voidId);  
    }

    private static RepositoryConnection getConnection(Repository repository) throws BridgeDBException{
        try {
            return repository.getConnection();
        } catch (Throwable ex) {
            try {
                repository.shutDown();
            } catch (Throwable ex1) {
                throw new BridgeDBException ("Unable to get a connection and error shuting down. ", ex1);
            }
            throw new BridgeDBException ("Unable to get a connection. ", ex);
        }      
    }
    
    public static RdfWrapper setupConnection(StoreType storeType) throws RDFHandlerException{
        return setupConnection(storeType, true);
    }
    
    public static RdfWrapper setupConnection(StoreType storeType, boolean existing) throws RDFHandlerException{
        Repository repository;
        try {
            repository = getRepository (storeType, existing);
        } catch (BridgeDBException ex) {
            try {
                String path = RdfConfig.getProperty(ConfigReader.CONFIG_FILE_PATH_PROPERTY);
                String source = RdfConfig.getProperty(ConfigReader.CONFIG_FILE_PATH_SOURCE_PROPERTY);
                throw new RDFHandlerException("Setup error " + ex + " Please check " + path + " set by " + source, ex);
            } catch (BridgeDBException ex1) {
                throw new RDFHandlerException("Setup error " + ex + " unable to dettermine source", ex);
            }
        }
        try {
            RepositoryConnection connection = repository.getConnection();
            return new RdfWrapper(connection);
        } catch (Throwable ex) {
            try {
                repository.shutDown();
            } catch (Throwable ex1) {
                throw new RDFHandlerException ("Unable to get a connection and error shuting down. ", ex1);
            }
            throw new RDFHandlerException ("Unable to get a connection. ", ex);
        }      
    }
    

}
