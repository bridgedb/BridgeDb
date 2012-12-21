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
import org.bridgedb.metadata.constants.BridgeDBConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;

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
        return ":" + BridgeDBConstants.ORGANISM + "_" + getRdfLabel(organism);
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
