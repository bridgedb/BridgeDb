/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.ops.LinkSetStore;

/**
 *
 * @author Christian
 */
public class RdfReader implements LinkSetStore{

    private final RdfStoreType type;
    
    public RdfReader(RdfStoreType type){
        this.type = type;
    }
    
    @Override
    public List<String> getLinksetNames() throws IDMapperException {
        return RdfWrapper.getContextNames(type);
    }

    @Override
    public String getRDF(int id) throws IDMapperException {
        return RdfWrapper.getRDF(type, id); 
    }
    
}
