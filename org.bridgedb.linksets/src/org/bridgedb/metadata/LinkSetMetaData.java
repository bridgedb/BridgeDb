/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.linkset.constants.VoidConstants;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public class LinkSetMetaData extends MetaData{

    DataSetMetaData sourceDataSet;
    DataSetMetaData targetDataSet;
    
    public LinkSetMetaData(Resource id, RDFData input){
        super(id, input);
    }

    @Override
    void setupValues() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    URI getResourceType() {
         return VoidConstants.LINKSET;
    }
    
}
