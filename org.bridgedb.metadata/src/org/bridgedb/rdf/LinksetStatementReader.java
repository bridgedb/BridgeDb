/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.constants.VoidConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class LinksetStatementReader extends StatementReader implements LinksetStatements{
    
    Set<Statement> linkStatements;
    
    URI linkPredicate = null;
    
    public LinksetStatementReader(String fileName) throws MetaDataException{
        super(fileName);
    }
    
    public LinksetStatementReader(File file) throws MetaDataException{
        super(file);
    }

    public LinksetStatementReader(String info, RDFFormat format) throws MetaDataException{
        super(info, format);
    }

    public LinksetStatementReader(InputStream inputStream, RDFFormat format) throws MetaDataException{
        super(inputStream, format);
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
        if (linkStatements == null){
            linkStatements = new HashSet<Statement>();
        }
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
