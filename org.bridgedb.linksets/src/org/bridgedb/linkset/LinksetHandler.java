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
package org.bridgedb.linkset;

import java.io.File;
import org.bridgedb.linkset.constants.VoidConstants;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RdfLoader;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.utils.Reporter;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.openrdf.rio.turtle.TurtleParser;

/**
 * Reads and RDF linkset file and passes the information on to a RdfLoader.
 *
 * Sperates the data into the different types supported by RDFLoader abd calls the appropriate methods
 *
 * What actually happens then is RDFLoader specific.
 *
 * @author Christian
 */
public class LinksetHandler extends RDFHandlerBase{
    boolean processingHeader = true;
    private List<String> datasets = new ArrayList<String>(2);
    URI linkPredicate;
 //   int linksetId;
 //   int inverseLinksetId;       
    
    RdfLoader rdfLoader;
    
    /**
     * Creates the Handler based on the RdfLoader to receive the data
     * @param rdfLoader
     */
    LinksetHandler(RdfLoader rdfLoader) {
        this.rdfLoader = rdfLoader;
    }

    /**
     * Parse file called by LinkSetLoader.parse to parse the file.
     *
     * It is not recommended to call this method directly so it is not public.
     *
     * Uses the BaseURI found in the configs read by the RdfWrapper
     *
     * @param file
     * @throws IDMapperException
     */
    void parse (File file) throws IDMapperException  {
        if (!file.isFile()){
            throw new IDMapperException (file.getAbsolutePath() + " is not a file");
        }
        Reporter.report("Parsing file:\n\t" + file.getAbsolutePath());
        rdfLoader.setSourceFile(file.getAbsolutePath());
        FileReader reader = null;
        try {
            RDFParser parser = new TurtleParser();
            parser.setRDFHandler(this);
            parser.setParseErrorListener(new LinksetParserErrorListener());
            parser.setVerifyData(true);
            reader = new FileReader(file);
            parser.parse (reader, RdfWrapper.getBaseURI());
        } catch (IOException ex) {
            throw new IDMapperLinksetException("Error reading file " + 
            		file.getAbsolutePath() + " " + ex.getMessage(), ex);
        } catch (OpenRDFException ex) {
            throw new IDMapperLinksetException("Error parsing file " + 
            		file.getAbsolutePath()+ " " + ex.getMessage(), ex);
        } finally {
            try {
                if (reader != null){
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LinksetHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void handleStatement(Statement st) throws RDFHandlerException {
        if (processingHeader) {
            processHeaderStatement(st);
        } else {
            if (st.getPredicate().equals(linkPredicate)) {
                /* Only store those statements that correspond to the link predicate */
                rdfLoader.insertURLMapping(st);
            }
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
    private void processHeaderStatement(Statement st) throws RDFHandlerException{
        Resource subject = st.getSubject();
        final URI predicate = st.getPredicate();
        final Value object = st.getObject();
        if (linkPredicate != null && predicate.equals(linkPredicate)) {
            /* Assumes all metadata is declared before the links */
            processingHeader = false;
            rdfLoader.processFirstNoneHeader(st);
            rdfLoader.insertURLMapping(st);
            return;
        }
        if (predicate.equals(VoidConstants.LINK_PREDICATE)) {
            if (linkPredicate != null) {
                throw new RDFHandlerException("Linkset can only be declared to have one link predicate.");
            }
            linkPredicate = (URI) object;
        }
        rdfLoader.addHeaderStatement(st);
    }
   
    @Override
    public void startRDF() throws RDFHandlerException{
        super.startRDF();
    } 
    
    @Override
    public void endRDF() throws RDFHandlerException{
        super.endRDF();
        try {
            rdfLoader.closeInput();
        } catch (IDMapperException ex) {
            throw new RDFHandlerException("Error endingRDF ", ex);
        }
        if (this.processingHeader){
            throw new RDFHandlerException("Linkset error! End of void header not found.\n" +
            		"\t If the linkset file contains links, check that the declared link " +
            		"predicate and the used link predicate are the same.");
        }
    }

}
