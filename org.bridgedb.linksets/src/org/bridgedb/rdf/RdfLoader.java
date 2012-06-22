/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.linkset.IDMapperLinksetException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public interface RdfLoader {
    
    public void clear() throws IDMapperLinksetException;
    
    public String getDefaultBaseURI();

    public void validateAndSaveVoid(Statement firstMap) throws RDFHandlerException;

    public String getSubjectUriSpace() throws RDFHandlerException;

    public String getTargetUriSpace() throws RDFHandlerException;

    public String getLinksetid() throws RDFHandlerException;

    public String getInverseLinksetid() throws RDFHandlerException;

    public void addStatement(Statement st) throws RDFHandlerException;

}
