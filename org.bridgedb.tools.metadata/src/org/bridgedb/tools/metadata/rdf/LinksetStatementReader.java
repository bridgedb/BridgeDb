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
package org.bridgedb.tools.metadata.rdf;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class LinksetStatementReader extends StatementReader implements LinksetStatements{
    
    Set<Statement> linkStatements;
    private Statement firstPossibleLinkStatement;
    private Statement inDataSetStatement; 
    URI linkPredicate = null;
    private State state;
    
    public LinksetStatementReader(String address) throws BridgeDBException{
        super(address);
    }
    
    public LinksetStatementReader(File file) throws BridgeDBException{
        super(file);
    }

    public LinksetStatementReader(String info, RDFFormat format) throws BridgeDBException{
        super(info, format);
    }

    public LinksetStatementReader(InputStream inputStream, RDFFormat format) throws BridgeDBException{
        super(inputStream, format);
    }

    public Set<Statement> getLinkStatements(){
        return linkStatements;
    }

    public void startRDF() throws RDFHandlerException {
        super.startRDF();
        state = State.ORIGINAL;
    }
    
    public void handleStatement(Statement statement) throws RDFHandlerException {
        switch (state){
            case ORIGINAL:
                handleOriginalStatement(statement);
                break;
            case LINK_PREDICATE_FOUND:
                handleLinkPredicateFoundStatement(statement);
                break;
            case IN_DATASET_FOUND:
                findAndSaveLinkStatement(statement);
                break;
            case IN_DATASET_AND_LINKS_FOUND:    
                checkandSaveInDataLinkStatements(statement);
                break;
            default:
                throw new IllegalStateException ("Unexpected State " + state);
        }
    }

    public void handleOriginalStatement(Statement statement) throws RDFHandlerException {
        if (statement.getPredicate().equals(VoidConstants.LINK_PREDICATE)){
            Value value = statement.getObject();
            if (value instanceof URI){
                linkPredicate = (URI)value;
                moveLinksAlreadyFound();
                state = State.LINK_PREDICATE_FOUND;
            } else {
                throw new RDFHandlerException("Predicate " + VoidConstants.LINK_PREDICATE + " muts have a URI object.");
            }
        }
        if (statement.getPredicate().equals(VoidConstants.IN_DATASET1)){
            System.out.println("In with " + statement);
            System.out.println(statements);
            inDataSetStatement = statement;
            findLinkAndMoveStatements();
        }
        statements.add(statement);
    }

    public void handleLinkPredicateFoundStatement(Statement statement) throws RDFHandlerException {
        if (statement.getPredicate().equals(linkPredicate)){
            linkStatements.add(statement);
        } else {
            if (statement.getPredicate().equals(VoidConstants.LINK_PREDICATE)){
                throw new RDFHandlerException("Two statements found with predicate " + VoidConstants.LINK_PREDICATE +
                        "\n\tLinksets can have only one declared " + VoidConstants.LINKSET + " type Resource\n" +
                        "\tAnd that must have the only " +  VoidConstants.LINK_PREDICATE + " statement in the whole file");
            }
            statements.add(statement);         
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

    private void findAndSaveLinkStatement(Statement statement){
        if (linkStatements == null){
            linkStatements = new HashSet<Statement>();
        }
        firstPossibleLinkStatement = statement;
        System.out.println("1 " + firstPossibleLinkStatement);
        linkPredicate = firstPossibleLinkStatement.getPredicate();
        System.out.println ("LP1: " + linkPredicate);
        state = State.IN_DATASET_AND_LINKS_FOUND; 
        linkStatements.add(statement);
    }
    
    private void findLinkAndMoveStatements() throws RDFHandlerException{
        if (statements.isEmpty()){
            state = State.IN_DATASET_FOUND;
            return;
        }
        //State set in below call
        findAndSaveLinkStatement(statements.iterator().next());
        statements.remove(firstPossibleLinkStatement);
        Iterator<Statement> iterator = statements.iterator();
        while (iterator.hasNext()){
            Statement statement = iterator.next();
            checkandSaveInDataLinkStatements(statement);
            iterator.remove();
        }           
    }
    
    private void checkandSaveInDataLinkStatements(Statement statement) throws RDFHandlerException{
        if (statement.getPredicate().equals(linkPredicate)){
            linkStatements.add(statement);
        } else {
        System.out.println ("LP2: " + linkPredicate);
            throw new RDFHandlerException("Found inDataSet statement " + inDataSetStatement 
                    + "\n so expecting all other statements to be links with the same predicate, but found\n"
                    + firstPossibleLinkStatement
                    + "\n and \n"
                    + statement);
        }           
    }

    public void endRDF() throws RDFHandlerException {
        super.endRDF();
        if (linkPredicate == null){
            throw new RDFHandlerException ("Linkset error! linkPredicate never found.\n"
                    + "Please make sure there is exactly one statement with the predicate " + VoidConstants.LINK_PREDICATE);
        }   
        if (linkStatements == null ||  linkStatements.isEmpty()){
            throw new RDFHandlerException ("Linkset error! Found the linkPredicate " + linkPredicate + ".\n"
                    + "But no linkStatements using this predicate");
            
        }
    }
    
    private enum State{
        ORIGINAL, LINK_PREDICATE_FOUND, IN_DATASET_FOUND, IN_DATASET_AND_LINKS_FOUND;
    }
}
