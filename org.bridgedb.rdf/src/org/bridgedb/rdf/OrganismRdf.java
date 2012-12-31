/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

/**
 *
 * @author Christian
 */
public class OrganismRdf extends RdfBase{

    private static OrganismRdf singleton = null;
    
    static HashMap<Value,Object> organisms = new HashMap<Value,Object>();
    
    private OrganismRdf(){
        for (Organism organism:Organism.values()){
            organisms.put(new URIImpl(getRdfId(organism)), organism);
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
    
    public static final String getRdfId(Organism organism) {
        return ":" + BridgeDBConstants.ORGANISM_LABEL + "_" + getRdfLabel(organism);
    }

    public static final URI getResourceId(Organism organism){
        return new URIImpl(BridgeDBConstants.ORGANISM1 + "_" + getRdfLabel(organism));
    }
    
    public static void addAll(RepositoryConnection repositoryConnection) throws IOException, RepositoryException {
        for (Organism organism:Organism.values()){
            add(repositoryConnection, organism);
        }        
    }
    
    public void addComments(RDFHandler handler) throws RDFHandlerException{
        handler.handleComment("WARNING: Organism are hard coded into BridgeDB.");   
        handler.handleComment("WARNING: below is for reference and NON BridgeDB use only!");   
        handler.handleComment("WARNING: Any changes could cause a BridgeDBException.");   
    }
    
    public static void add(RepositoryConnection repositoryConnection, Organism organism) 
            throws IOException, RepositoryException {
        URI id = getResourceId(organism);
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.ORGANISM_URI);
        repositoryConnection.add(id, BridgeDBConstants.CODE_URI,  new LiteralImpl(organism.code()));
        repositoryConnection.add(id, BridgeDBConstants.SHORT_NAME_URI,  new LiteralImpl(organism.shortName()));
        repositoryConnection.add(id, BridgeDBConstants.LATIN_NAME_URI,  new LiteralImpl(organism.latinName()));
    }

    public static void writeAllAsRDF(BufferedWriter writer) throws IOException {
        writer.write("#WARNING: Organism are hard coded into BridgeDB.");
        writer.newLine();
        writer.write("#WARNING: below is for reference and NON BridgeDB use only!");
        writer.newLine();
        writer.write("#WARNING: Any changes could cause a BridgeDBException.");
        writer.newLine();
        for (Organism organism:Organism.values()){
            writeAsRDF(writer, organism);
        }
        writer.newLine();
    }

    public static void writeAsRDF(BufferedWriter writer, Organism organism) throws IOException {
        writer.write(getRdfId(organism));
        writer.write(" a ");
        writer.write(BridgeDBConstants.ORGANISM_SHORT);
        writer.write("; ");
        writer.newLine();

        writer.write("         ");
        writer.write(BridgeDBConstants.CODE_SHORT);
        writer.write(" \"");
        writer.write(organism.code());
        writer.write("\";");
        writer.newLine();

        writer.write("         ");
        writer.write(BridgeDBConstants.SHORT_NAME_SHORT);
        writer.write(" \"");
        writer.write(organism.shortName());
        writer.write("\";");
        writer.newLine();
        
        writer.write("         ");
        writer.write(BridgeDBConstants.LATIN_NAME_SHORT);
        writer.write(" \"");
        writer.write(organism.latinName());
        writer.write("\".");
        writer.newLine();
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

    static Object byRdfResource(Value organismId) {
        OrganismRdf organismRdf = factory();
        Object result = organismRdf.organisms.get(organismId);
        return result;
    }

}
