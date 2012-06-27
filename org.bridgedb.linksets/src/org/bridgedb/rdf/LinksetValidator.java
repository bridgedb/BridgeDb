/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.linkset.IDMapperLinksetException;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class LinksetValidator extends RDFBase implements RdfLoader{

    @Override
    void saveStatements() throws RDFHandlerException {
        //Do nothing
    }

    @Override
    public void clear() throws IDMapperLinksetException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDefaultBaseURI() {
        return "http://www.bridgebd.org/valiator/";
    }

    @Override
    public String getLinksetid() throws RDFHandlerException {
        return "forward";
    }

    @Override
    public String getInverseLinksetid() throws RDFHandlerException {
        return "inverse";
    }

    @Override
    public boolean isTransative() throws RDFHandlerException {
        return false;
    }
    
}
