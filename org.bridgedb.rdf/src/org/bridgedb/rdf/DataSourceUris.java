/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.HashMap;
import org.bridgedb.DataSource;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class DataSourceUris {
    
    private final DataSource inner;
    private DataSource uriParent = null;
    
    private static final HashMap<DataSource, DataSourceUris> byDataSource = new HashMap<DataSource, DataSourceUris>();
    
    private DataSourceUris(DataSource wraps){
        inner = wraps;
        byDataSource.put(inner, this);
    }
    
    public static DataSourceUris byDataSource(DataSource dataSource){
        DataSourceUris result = byDataSource.get(dataSource);
        if (result == null){
            result = new DataSourceUris(dataSource);
        }
        return result;
    }
    
    public void setUriParent(DataSource parent) throws BridgeDBException{
        if (parent.equals(uriParent)){
            return;  //already set. Also checks that replacedBy is not null
        }
        if (uriParent != null) {
            throw new BridgeDBException("Illegal attempt to set UriParent of  " + inner + " with "  + parent
                    + ". Uri Parent was previously set to " + uriParent);             
        }
        if (inner.equals(parent)){
            throw new BridgeDBException("Illegal attempt to set UriParent of  " + inner + " with itself ");
        }
        DataSourceUris parentPlus = byDataSource(parent);
        if (parentPlus.uriParent != null){
            throw new BridgeDBException("Illegal attempt to set UriParent of  " + inner + " with "  + parent
                    + ". As parent has a UriParent of " + parentPlus.uriParent + " set.");             
        }
        for (DataSourceUris plus: byDataSource.values()){
            if (plus.uriParent != null){
                if (plus.uriParent.equals(inner)){
                    throw new BridgeDBException("Illegal attempt to set UriParent of  " + inner + " with "  + parent
                        + ". As " + inner + " is itself a UriParent of " + plus.inner);             
                }
            }
        }
        uriParent = parent;
    }
    
}
