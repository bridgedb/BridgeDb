/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.List;
import org.bridgedb.linkset.IDMapperLinksetException;
import org.bridgedb.linkset.constants.VoidConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class HoldingRDFStore extends RDFBase implements RdfLoader{

    private RdfStoreType type;
    URI linksetContext;
    URI inverseContext;
    private static final URI HIGHEST_LINKSET_ID_PREDICATE = new URIImpl("http://www.bridgedb.org/highested_linkset_id");
    private static final Resource ANY_RESOURCE = null;

    public HoldingRDFStore(RdfStoreType type) throws IDMapperLinksetException{
        this.type = type;
             setContext();
    }
        
    private synchronized void setContext() throws IDMapperLinksetException{     
        try {
            RepositoryConnection connection = RdfWrapper.setupConnection(type);
            Resource subject = new URIImpl(RdfWrapper.getBaseURI() + "/MetaData");
            RepositoryResult<Statement> rr = RdfWrapper.getStatements(connection, 
                    subject, HIGHEST_LINKSET_ID_PREDICATE, null, ANY_RESOURCE);
            int linksetId = extractLinksetId(connection, rr, subject);
            rr = RdfWrapper.getStatements(connection, 
                    subject, HIGHEST_LINKSET_ID_PREDICATE, null, ANY_RESOURCE);
            RdfWrapper.remove(connection, rr);
            //LastId is plus one as there is also the inverse
            Value lastId = new LiteralImpl(""+(linksetId + 1));
            RdfWrapper.add(connection, subject, HIGHEST_LINKSET_ID_PREDICATE, lastId, ANY_RESOURCE);
            rr = RdfWrapper.getStatements(connection, 
                    subject, HIGHEST_LINKSET_ID_PREDICATE, null, ANY_RESOURCE);
            linksetContext = RdfWrapper.getLinksetURL(linksetId);       
            inverseContext = RdfWrapper.getLinksetURL(linksetId + 1);       
            RdfWrapper.shutdown(connection);
        } catch (RDFHandlerException ex) {
            throw new IDMapperLinksetException("Error setting the context", ex);
        }
     }

    private int extractLinksetId(RepositoryConnection connection, RepositoryResult<Statement> rr, Resource subject) 
            throws RDFHandlerException{
        List<Statement> list = RdfWrapper.asList(connection, rr);
        int linksetId;
        if (list.size() == 1){
           Value lastLinksetId = list.get(0).getObject();
           linksetId = Integer.parseInt(lastLinksetId.stringValue()) + 1;
        } else if (list.isEmpty()){
             linksetId = 1;
        } else {
            RdfWrapper.shutdownAfterError (connection);
            throw new RDFHandlerException("Found more than one statement with subject " + subject + 
                " and predicate " + HIGHEST_LINKSET_ID_PREDICATE);            
        }           
        return linksetId;
    }
    
    @Override
    void saveStatements() throws RDFHandlerException {
        RepositoryConnection connection = RdfWrapper.setupConnection(type);
        for (Statement st:statements){
            RdfWrapper.add(connection, st.getSubject(), st.getPredicate(), st.getObject(), linksetContext);
            if (st.getPredicate().equals(VoidConstants.SUBJECTSTARGET)){
                RdfWrapper.add(connection, st.getSubject(), VoidConstants.OBJECTSTARGET, st.getObject(), inverseContext);    
            } else if (st.getPredicate().equals(VoidConstants.OBJECTSTARGET)){
                RdfWrapper.add(connection, st.getSubject(), VoidConstants.SUBJECTSTARGET, st.getObject(), inverseContext);                    
            } else {
                RdfWrapper.add(connection, st.getSubject(), st.getPredicate(), st.getObject(), inverseContext);                
            }
        }
        statements = null;
        RdfWrapper.shutdown(connection);
    }

    @Override
    public void clear() throws IDMapperLinksetException {
        RdfWrapper.clear(type);
    }

    @Override
    public String getDefaultBaseURI() {
        return linksetContext.stringValue() + "/";        
    }

    @Override
    public String getLinksetid() throws RDFHandlerException {
        if (linksetContext != null){
            return linksetContext.stringValue();
        }
        throw new RDFHandlerException("run validateAndSaveVoid before calling getLinksetid()");
    }

    @Override
    public String getInverseLinksetid() throws RDFHandlerException {
        if (inverseContext != null){
            return inverseContext.stringValue();
        }
        throw new RDFHandlerException("run validateAndSaveVoid before calling getInverseLinksetid()");
    }

}
