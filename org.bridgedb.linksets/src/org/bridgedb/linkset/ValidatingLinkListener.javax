/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public class ValidatingLinkListener implements URLLinkListener{

    HashMap<String,String> sourceNameSpaces = new HashMap<String,String>();
    HashMap<String,String> targetNameSpaces = new HashMap<String,String>();
    
    @Override
    public void openInput() throws IDMapperException {
        //Do nothing
    }

    @Override
    public void closeInput() throws IDMapperException {
        //Do nothing
    }

    @Override
    public void registerLinkSet(String linkSetId, DataSource source, String predicate, DataSource target,
            boolean isTransitive) throws IDMapperException {
        String sourceNameSpace = source.getURISpace();
        sourceNameSpaces.put(linkSetId, sourceNameSpace);
        String targetNameSpace = target.getURISpace();
        targetNameSpaces.put(linkSetId, targetNameSpace);    
    }

    @Override
    public void insertLink(String source, String target, String forwardlinkSetId, String inverselinkSetId) 
            throws IDMapperException {
        String sourceNameSpace = sourceNameSpaces.get(forwardlinkSetId);
        if (sourceNameSpace == null) {
            throw new IDMapperLinksetException("Illegal attempt to use linkSetId " + forwardlinkSetId + 
                    " without first registering it");
        }
        if (!(source.startsWith(sourceNameSpace))){
            throw new IDMapperLinksetException("Source UriSpace missmatch. For linkSetId " + forwardlinkSetId + 
                    " the expected URISpace is " + sourceNameSpace + " but found " + source);
        }
        String targetNameSpace = targetNameSpaces.get(forwardlinkSetId);
        if (targetNameSpace == null) {
            throw new IDMapperLinksetException("Illegal attempt to use linkSetId " + forwardlinkSetId + 
                    " without first registering it");
        }
        if (!(target.startsWith(targetNameSpace))){
            throw new IDMapperLinksetException("Target UriSpace missmatch. For linkSetId " + forwardlinkSetId + 
                    " the expected URISpace is " + targetNameSpace + " but found " + target);
        }
        sourceNameSpace = sourceNameSpaces.get(inverselinkSetId);
        if (sourceNameSpace == null) {
            throw new IDMapperLinksetException("Illegal attempt to use inverse linkSetId " + inverselinkSetId + 
                    " without first registering it");
        }
        if (!(target.startsWith(sourceNameSpace))){
            throw new IDMapperLinksetException("Target UriSpace missmatch. For inverse linkSetId " + inverselinkSetId + 
                    " the expected URISpace is " + sourceNameSpace + " but found " + target);
        }
        targetNameSpace = targetNameSpaces.get(inverselinkSetId);
        if (targetNameSpace == null) {
            throw new IDMapperLinksetException("Illegal attempt to use inverse linkSetId " + inverselinkSetId + 
                    " without first registering it");
        }
        if (!(source.startsWith(targetNameSpace))){
            throw new IDMapperLinksetException("Source UriSpace missmatch. For inverse linkSetId " + inverselinkSetId + 
                    " the expected URISpace is " + targetNameSpace + " but found " + source);
        }
    }

    @Override
    public Set<String> getLinkSetIds() throws IDMapperException {
        return new HashSet<String>();
    }
    
}
