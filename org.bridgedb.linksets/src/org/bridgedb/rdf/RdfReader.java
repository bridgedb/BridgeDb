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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinkSetStore;
import org.bridgedb.metadata.constants.VoidConstants;
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
        if (storeType == null){
            throw new NullPointerException("StoreType = null");
        }
        this.storeType = storeType;
    }
    
    //@Override
    //public List<String> getLinksetNames() throws IDMapperException {
    //    return RdfWrapper.getContextNames(storeType);
    //}

    @Override
    public String getLinksetRDF(int linksetId) throws IDMapperException{
        Resource linkSetGraph = RdfFactory.getLinksetURL(linksetId); 
        return getRDF(linkSetGraph);
    }

    @Override
    public String getVoidRDF(int voidId) throws IDMapperException{
        Resource voidGraph = RdfFactory.getVoidURL(voidId); 
        return getRDF(voidGraph);
    }

    private String getRDF(Resource resource) throws IDMapperException {
        RdfWrapper rdfWrapper = null;
        try {
            rdfWrapper = RdfFactory.setupConnection(storeType);
            String result = rdfWrapper.getRDF(resource);
            return result;
        } catch (RDFHandlerException ex) {
            throw new IDMapperLinksetException("Unable to read RDF", ex);
        } finally {
            shutDown(rdfWrapper);
        }
    }

    @Override
    public List<Statement> getStatementsForResource(Resource resource) throws IDMapperException{
        RdfWrapper rdfWrapper = null;
        try {
            rdfWrapper = RdfFactory.setupConnection(storeType);
            List<Statement> result = rdfWrapper.getStatementList(resource, RdfWrapper.ANY_PREDICATE, RdfWrapper.ANY_OBJECT);
            return result;
        } catch (RDFHandlerException ex) {
            throw new IDMapperLinksetException("Unable to read RDF", ex);
        } finally {
            shutDown(rdfWrapper);
        }
    }
    
    public Set<Statement> getSuperSet(Resource resource) throws IDMapperException{
        //TODO this will cause endless recursion is two Ids are subsets of each other
        List<Statement> results = new ArrayList<Statement>();
        Set<Resource> allReadyChecked = new HashSet<Resource>();
        RdfWrapper rdfWrapper = null;
        try {
            rdfWrapper = RdfFactory.setupConnection(storeType);       
            return getSuperSet(resource, rdfWrapper, allReadyChecked);
        } catch (RDFHandlerException ex) {
            throw new IDMapperLinksetException("Unable to read RDF", ex);
        } finally {
            shutDown(rdfWrapper);
        }
    }
    
    private Set<Statement> getSuperSet(Resource resource, RdfWrapper rdfWrapper, Set<Resource> allReadyChecked) throws RDFHandlerException {
        Set<Statement> results = new HashSet<Statement>();
        List<Statement> subsetStatements = 
                rdfWrapper.getStatementList(RdfWrapper.ANY_SUBJECT, VoidConstants.SUBSET, resource);
        for (Statement subsetStatement:subsetStatements){
            Resource superResource = subsetStatement.getSubject();
            if (!allReadyChecked.contains(superResource)){
                allReadyChecked.add(superResource);
                results.addAll(
                        rdfWrapper.getStatementList(superResource, RdfWrapper.ANY_PREDICATE, RdfWrapper.ANY_OBJECT));
                results.addAll(getSuperSet(superResource, rdfWrapper, allReadyChecked));
            }
        }
        return results;
    }

    private void shutDown(RdfWrapper rdfWrapper) throws IDMapperException{
        if (rdfWrapper != null){
            try {
                rdfWrapper.shutdown();
            } catch (RDFHandlerException ex) {
                throw new IDMapperLinksetException ("Error shuting down RDFWrapper ", ex);
            }
        }
    }

}
