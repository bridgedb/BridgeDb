// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.rdf;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinkSetStore;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class RdfReader implements LinkSetStore{

    private final StoreType storeType;
    
    public RdfReader(StoreType storeType){
        this.storeType = storeType;
    }
    
    //@Override
    //public List<String> getLinksetNames() throws IDMapperException {
    //    return RdfWrapper.getContextNames(storeType);
    //}

    @Override
    public String getRDF(int id) throws IDMapperException {
        try {
            RdfWrapper rdfWrapper = RdfFactory.setupConnection(storeType);
            String result = rdfWrapper.getRDF(id);
            rdfWrapper.shutdown();
            return result;
        } catch (RDFHandlerException ex) {
            throw new IDMapperException("Unable to read RDF", ex);
        }
    }
    
    @Override
    public List<Statement> getStatementsForResource(Resource resource) throws IDMapperException{
        try {
            RdfWrapper rdfWrapper = RdfFactory.setupConnection(storeType);
            List<Statement> result = rdfWrapper.getStatementList(resource, RdfWrapper.ANY_PREDICATE, RdfWrapper.ANY_OBJECT);
            rdfWrapper.shutdown();
            return result;
        } catch (RDFHandlerException ex) {
            throw new IDMapperException("Unable to read RDF", ex);
        }
    
    }

}
