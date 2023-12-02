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
package org.bridgedb.rdf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bridgedb.bio.Organism;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.utils.BridgeDBException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class OrganismRdf extends RdfBase{

    private static OrganismRdf singleton = null;
    private HashMap<Value,Object> organisms;;
    
    private OrganismRdf(){
        organisms = new HashMap<Value,Object>();
        for (Organism organism:Organism.values()){
            organisms.put(getResourceId(organism), organism);
        }
    }
    
    public static OrganismRdf factory(){
        if (singleton == null){
            singleton = new OrganismRdf();
        }
        return singleton;
    }
    
    public static final String getRdfLabel(Organism organism) {
        return scrub(organism.code());   
    }
    
    public static final IRI getResourceId(Organism organism){
        return SimpleValueFactory.getInstance().createIRI(BridgeDBConstants.ORGANISM1 + "_" + getRdfLabel(organism));
    }
    
    public static void addAll(RepositoryConnection repositoryConnection) throws IOException, RepositoryException {
        for (Organism organism:Organism.values()){
            add(repositoryConnection, organism);
        }        
    }
    
    public void addComments(RDFHandler handler) throws RDFHandlerException{
        handler.handleComment("WARNING: Organism are hard coded into BridgeDb.");   
        handler.handleComment("WARNING: below is for reference and NON BridgeDb use only!");   
        handler.handleComment("WARNING: Any changes could cause a BridgeDBException.");   
    }
    
    public static void add(RepositoryConnection repositoryConnection, Organism organism) 
            throws IOException, RepositoryException {
        IRI id = getResourceId(organism);
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.ORGANISM_URI);
        repositoryConnection.add(id, BridgeDBConstants.CODE_URI,  SimpleValueFactory.getInstance().createLiteral(organism.code()));
        repositoryConnection.add(id, BridgeDBConstants.SHORT_NAME_URI,  SimpleValueFactory.getInstance().createLiteral(organism.shortName()));
        repositoryConnection.add(id, BridgeDBConstants.LATIN_NAME_URI,  SimpleValueFactory.getInstance().createLiteral(organism.latinName()));
    }

   public static Object readRdf(Resource organismId, Set<Statement> allStatements) throws BridgeDBException {
        for (Statement statement:allStatements){
            if (statement.getPredicate().equals(BridgeDBConstants.LATIN_NAME_URI)){
                String latinName = statement.getObject().stringValue();
                Organism orgamism =  Organism.fromLatinName(latinName);
                if (orgamism != null){
                    return orgamism;
                }
                throw new BridgeDBException("No Orgamism with LatinName " + latinName + " for " + organismId);
            }
        }
        throw new BridgeDBException("No Orgamism found for " + organismId);
    }

    static Object byRdfResource(Value organismId) throws BridgeDBException {
        OrganismRdf organismRdf = factory();
        Object result = organismRdf.organisms.get(organismId);
        if (result == null){
            throw new BridgeDBException("No Orgamism known for " +  organismId);
        }
        return result;
    }

}
