/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.url;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public interface URLListener {
    public void registerUriSpace(DataSource source, String uriSpace) throws IDMapperException;

    public int registerMappingSet(String sourceUriSpace, String predicate, String targetUriSpace, 
        boolean symetric, boolean transative) throws IDMapperException;

    public void insertURLMapping(String sourceURL, String targetURL, int mappingSet, boolean symetric) 
            throws IDMapperException;
    
    public void closeInput()throws IDMapperException;

}
