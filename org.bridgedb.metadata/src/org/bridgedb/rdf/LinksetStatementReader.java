/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.constants.VoidConstants;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class LinksetStatementReader extends StatementReader{
    
    Set<Statement> linkStatements = new HashSet<Statement>();
    
    URI linkPredicate = null;
    
    public LinksetStatementReader(String fileName) throws MetaDataException{
        this(new File(fileName), DEFAULT_BASE_URI);
    }
    
    public LinksetStatementReader(File file) throws MetaDataException{
        this(file, DEFAULT_BASE_URI);
    }

    public LinksetStatementReader(String fileName, String baseURI) throws MetaDataException{
        this(new File(fileName), baseURI);
    }
    
    public LinksetStatementReader(File file, String baseURI) throws MetaDataException{
        parse(file, baseURI);
        if (linkPredicate == null){
            throw new MetaDataException("Predicate " + VoidConstants.LINK_PREDICATE + " not found so unable to split void from links");
        }
        if (linkStatements.isEmpty()){
            throw new MetaDataException("No links found with linkPredicate " + linkPredicate);
        }
    }
    
    public Set<Statement> getVoidStatements(){
        return statements;
    }
    
    public Set<Statement> getLinkStatements(){
        return linkStatements;
    }

    public void handleStatement(Statement statement) throws RDFHandlerException {
        if (linkPredicate == null){
            if (statement.getPredicate().equals(VoidConstants.LINK_PREDICATE)){
                Value value = statement.getObject();
                if (value instanceof URI){
                    linkPredicate = (URI)value;
                    moveLinksAlreadyFound();
                } else {
                    throw new RDFHandlerException("Predicate " + VoidConstants.LINK_PREDICATE + " muts have a URI object.");
                }
            }
            statements.add(statement);
        } else {
             if (statement.getPredicate().equals(linkPredicate)){
                 linkStatements.add(statement);
             } else {
                if (statement.getPredicate().equals(VoidConstants.LINK_PREDICATE)){
                    throw new RDFHandlerException("Two statements found with predicate " + VoidConstants.LINK_PREDICATE);
                }
                statements.add(statement);         
             }
        }
    }

    private void moveLinksAlreadyFound() {
       Iterator<Statement> iterator = statements.iterator();
       while (iterator.hasNext()){
           Statement statement = iterator.next();
           if (statement.getPredicate().equals(linkPredicate)){
               linkStatements.add(statement);
               iterator.remove();
           }
       }      
    }

}
