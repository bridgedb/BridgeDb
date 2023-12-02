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
package org.bridgedb.uri.loader;

import org.apache.log4j.Logger;
import org.bridgedb.sql.justification.OpsJustificationMaker;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.UriListener;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFHandlerException;

/**
 * Reads an RDF linkset file and passes the information on to a RdfLoader.
 *
 * Seperates the data into the different types supported by RDFLoader and calls the appropriate methods
 *
 * What actually happens then is RDFLoader specific.
 *
 * @author Christian
 */
public class LinksetHandler extends LinkHandler{
    
    private boolean processingFirstStatement = true;
    protected final String justification;
    protected final String backwardJustification;

    protected final IRI mappingSource;

    private int noneLinkStatements;
    static final Logger logger = Logger.getLogger(LinksetHandler.class);
    
    public LinksetHandler(UriListener uriListener, IRI linkPredicate, String justification, IRI mappingSource){
        super(uriListener, linkPredicate);
        this.justification = justification;
        this.mappingSource = mappingSource;
        backwardJustification = OpsJustificationMaker.getInstance().getInverse(justification);
    }
    
    /**
     * @deprecated 
     * @param uriListener
     * @param linkPredicate
     * @param justification
     * @param mappingSource
     * @param ignore 
     */
    public LinksetHandler(UriListener uriListener, IRI linkPredicate, String justification, IRI mappingSource, boolean ignore){
        this(uriListener, linkPredicate, justification, mappingSource);
    }
        
    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        if (processingFirstStatement) {
            processFirstStatement(st);
        } else {
            super.handleStatement(st);
        }
    }
       
    /**
     * Process an RDF statement that forms part of the VoID header for the 
     * linkset file.
     * 
     * Once the header processor detects that it is starting to process links
     * it sets a flag, inserts the VoID header information into the database,
     * and then goes into a link insert only mode.
     * 
     * @param st an RDF statement
     * @throws RDFHandlerException
     */
    private void processFirstStatement(Statement st) throws RDFHandlerException{
        final IRI predicate = st.getPredicate();
        if (predicate.equals(linkPredicate)) {
            Resource subject = st.getSubject();
            Value object = st.getObject();
            try {
                processingFirstStatement = false;
                if (!(subject instanceof IRI)){
                    Reporter.error("None URI subject in " + st);
                    return;
                }
                if (!(object instanceof IRI)){
                    Reporter.error("None URI object in " + st);
                    return;
                }
                RegexUriPattern sourcePattern = uriListener.toUriPattern(subject.stringValue());
                if (sourcePattern == null){
                     Reporter.error("Unable to get a pattern for subject in " + st);
                     return;
                }
                RegexUriPattern targetPattern = uriListener.toUriPattern(object.stringValue());
                if (targetPattern == null){
                    Reporter.error("Unable to get a pattern for " + object.stringValue());
                    return;
                }
                registerMappingSet(sourcePattern, targetPattern);
            } catch (BridgeDBException ex) {
                Reporter.error("Error handling: " + st, ex);
            }
            super.handleStatement(st);
        } else {
            this.noneLinkStatements++;
        }
    }

    protected void registerMappingSet(RegexUriPattern sourcePattern, RegexUriPattern targetPattern ) 
            throws BridgeDBException{
        if (backwardJustification == null){
            mappingSet = uriListener.registerMappingSet(sourcePattern, linkPredicate.stringValue(), 
                    justification, targetPattern, mappingSource, false);
            this.setSymetric(false);
        } else {
            mappingSet = uriListener.registerMappingSet(sourcePattern, linkPredicate.stringValue(), 
                    justification, backwardJustification, targetPattern, mappingSource);
        }
    }

    @Override
    public void endRDF() throws RDFHandlerException{
        super.endRDF();
        if (this.processingFirstStatement){
            throw new RDFHandlerException("No Valid Statements found with predicate: " + linkPredicate);
        }
    }

 }
