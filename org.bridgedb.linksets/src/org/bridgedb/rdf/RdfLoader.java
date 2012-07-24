/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.bridgedb.IDMapperException;
import org.bridgedb.url.URLListener;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public interface RdfLoader {
    
    public void processFirstNoneHeader(Statement firstMap) throws RDFHandlerException;

    public void addHeaderStatement(Statement st) throws RDFHandlerException;

    public void insertURLMapping(Statement st) throws RDFHandlerException;

    public void closeInput()throws IDMapperException;

    public void setSourceFile(String absolutePath);

}
