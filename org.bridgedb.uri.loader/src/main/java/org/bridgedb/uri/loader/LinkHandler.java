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
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerBase;

/**
 * Reads an RDF linkset file and passes the information on to a RdfLoader.
 *
 * Seperates the data into the different types supported by RDFLoader and calls the appropriate methods
 *
 * What actually happens then is RDFLoader specific.
 *
 * @author Christian
 */
public class LinkHandler extends RDFHandlerBase{
    
    protected final URI linkPredicate;
    private boolean symetric;
    protected final UriListener uriListener;

    protected int mappingSet;
    
    static final Logger logger = Logger.getLogger(LinkHandler.class);

    public LinkHandler(UriListener uriListener, URI linkPredicate, boolean symetric){
        this.uriListener = uriListener;
        this.linkPredicate = linkPredicate;
        this.symetric = symetric;
    }
    
    public LinkHandler(UriListener uriListener, URI linkPredicate){
        this (uriListener, linkPredicate, true);
    }
    
    /**
     * @param symetric the symetric to set
     */
    protected void setSymetric(boolean symetric) {
        this.symetric = symetric;
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        if (st.getPredicate().equals(linkPredicate)) {
            /* Only store those statements that correspond to the link predicate */
            insertUriMapping(st);
        }
    }
          
    @Override
    public void endRDF() throws RDFHandlerException{
        super.endRDF();
        try {
            uriListener.closeInput();
        } catch (BridgeDBException ex) {
            throw new RDFHandlerException("Error endingRDF ", ex);
        }
    }

    private void insertUriMapping(Statement st) throws RDFHandlerException {
        Resource subject = st.getSubject();
        Value object = st.getObject();
        if (!(subject instanceof URI)){
            throw new RDFHandlerException ("None URI subject in " + st);
        }
        if (!(object instanceof URI)){
            throw new RDFHandlerException ("None URI object in " + st);
        }
        String sourceUri = subject.stringValue();
        String targetUri = object.stringValue();
        try {
            uriListener.insertUriMapping(sourceUri, targetUri, mappingSet, symetric);
        } catch (BridgeDBException ex) {
            throw new RDFHandlerException("Error inserting statement " + st, ex);
        }
    }

    public int getMappingsetId() {
        return mappingSet;
    }

}
