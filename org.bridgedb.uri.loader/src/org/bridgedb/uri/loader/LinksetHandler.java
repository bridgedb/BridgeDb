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
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.UriListener;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * Reads and RDF linkset file and passes the information on to a RdfLoader.
 *
 * Sperates the data into the different types supported by RDFLoader abd calls the appropriate methods
 *
 * What actually happens then is RDFLoader specific.
 *
 * @author Christian
 */
public class LinksetHandler extends LinkHandler{
    
    boolean processingFirstStatement = true;
    private final String justification;
    private final String backwardJustification;
    private final URI mappingSource;

    private int noneLinkStatements;
    
    public LinksetHandler(UriListener uriListener, URI linkPredicate, String justification,  
            URI mappingSource, boolean symetric){
        super(uriListener, linkPredicate, symetric);
        this.justification = justification;
        this.backwardJustification = null;
        this.mappingSource = mappingSource;
    }
    
    public LinksetHandler(UriListener uriListener, URI linkPredicate, String forwardJustification, String backwardJustification, 
            URI mappingSource){
        super(uriListener, linkPredicate, true);
        this.justification = forwardJustification;
        this.backwardJustification = backwardJustification;
        this.mappingSource = mappingSource;
    }

    static final Logger logger = Logger.getLogger(LinksetHandler.class);
        
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
        final URI predicate = st.getPredicate();
        if (predicate.equals(linkPredicate)) {
            Resource subject = st.getSubject();
            Value object = st.getObject();
            try {
                processingFirstStatement = false;
                if (!(subject instanceof URI)){
                    throw new RDFHandlerException ("None URI subject in " + st);
                }
                if (!(object instanceof URI)){
                    throw new RDFHandlerException ("None URI object in " + st);
                }
                RegexUriPattern sourcePattern = uriListener.toUriPattern(subject.stringValue());
                if (sourcePattern == null){
                     throw new RDFHandlerException("Unable to get a pattern for " + subject.stringValue());
                }
                RegexUriPattern targetPattern = uriListener.toUriPattern(object.stringValue());
                if (targetPattern == null){
                    throw new RDFHandlerException("Unable to get a pattern for " + object.stringValue());
                }
                if (backwardJustification == null){
                    mappingSet = uriListener.registerMappingSet(sourcePattern, linkPredicate.stringValue(), 
                            justification, targetPattern, mappingSource, symetric);
                } else {
                    mappingSet = uriListener.registerMappingSet(sourcePattern, linkPredicate.stringValue(), 
                            justification, backwardJustification, targetPattern, mappingSource);
                }
//                if (symetric == null){
//                    //If symetric is undefined assume map to self is not symetric
//                    symetric = (!(sourcePattern.getSysCode().equals(targetPattern.getSysCode())));
//                }
                
            } catch (BridgeDBException ex) {
                throw new RDFHandlerException("Error registering mappingset from " + st, ex);
            }
            super.handleStatement(st);
        } else {
            this.noneLinkStatements++;
        }
    }

    @Override
    public void endRDF() throws RDFHandlerException{
        super.endRDF();
        if (this.processingFirstStatement){
            throw new RDFHandlerException("No Statements found with predicate: " + linkPredicate);
        }
    }

 }
