/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.IDMapperLinksetException;
import org.bridgedb.linkset.constants.VoidConstants;
import org.bridgedb.url.URLListener;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class RDFWriter implements RdfLoader{

    private final RdfStoreType type;
    private final ArrayList<Statement> statements;
    private final int mappingId;
    private final URI linksetContext;
    private final URI inverseContext;
    private final boolean symmetric;
    private final URLListener urlListener;
    private static final URI HIGHEST_LINKSET_ID_PREDICATE = new URIImpl("http://www.bridgedb.org/highested_linkset_id");
    private static final Resource ANY_RESOURCE = null;

    public RDFWriter(RdfStoreType type, RDFValidator validator, URLListener listener) throws IDMapperException{
        this.type = type;
        urlListener = listener;
        statements = new ArrayList<Statement>();
        try {
            String subjectUriSpace = validator.getSubjectUriSpace();
            String targetUriSpace = validator.getTargetUriSpace();
            String predicate = validator.getPredicate();
            symmetric = validator.isSymmetric();
            boolean transative = validator.isTransative();
            mappingId = urlListener.registerMappingSet(subjectUriSpace, predicate, targetUriSpace, 
                    symmetric, transative);            
            linksetContext = RdfWrapper.getLinksetURL(mappingId);
            if (symmetric) {
                inverseContext = RdfWrapper.getLinksetURL(mappingId + 1);             
            } else {
                inverseContext = null;
            }
        } catch (RDFHandlerException ex) {
            throw new IDMapperLinksetException("Error setting the context", ex);
        }
     }

    @Override
    public void processFirstNoneHeader(Statement firstMap) throws RDFHandlerException {
        RepositoryConnection connection = RdfWrapper.setupConnection(type);
        for (Statement st:statements){
            RdfWrapper.add(connection, st.getSubject(), st.getPredicate(), st.getObject(), linksetContext);
            if (st.getPredicate().equals(VoidConstants.SUBJECTSTARGET)){
                RdfWrapper.add(connection, st.getSubject(), VoidConstants.OBJECTSTARGET, st.getObject(), inverseContext);    
            } else if (st.getPredicate().equals(VoidConstants.OBJECTSTARGET)){
                RdfWrapper.add(connection, st.getSubject(), VoidConstants.SUBJECTSTARGET, st.getObject(), inverseContext); 
                System.out.println("***");
                System.out.println( st.getSubject() + ", " + VoidConstants.SUBJECTSTARGET + ", " + st.getObject() + ", " + inverseContext);
            } else {
                RdfWrapper.add(connection, st.getSubject(), st.getPredicate(), st.getObject(), inverseContext);                
            }
        }
        RdfWrapper.shutdown(connection);
        try {
            urlListener.openInput();
        } catch (IDMapperException ex) {
            throw new RDFHandlerException(ex.getMessage(), ex);
        }
    }

    @Override
    public void addHeaderStatement(Statement st) throws RDFHandlerException {
        statements.add(st);
    }

    @Override
    public void closeInput() throws IDMapperException {
        urlListener.closeInput();
    }

    @Override
    public void insertURLMapping(Statement st) throws RDFHandlerException {
        String sourceURL = st.getSubject().stringValue();
        String targetURL = st.getObject().stringValue();
        try {
            urlListener.insertURLMapping(sourceURL, targetURL, mappingId, symmetric);
        } catch (IDMapperException ex) {
            throw new RDFHandlerException ("Error inserting mapping. ", ex);
        }
    }

}
