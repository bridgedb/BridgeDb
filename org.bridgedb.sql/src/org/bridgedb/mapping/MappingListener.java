package org.bridgedb.mapping;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public interface MappingListener {
    
    public int registerMappingSet(DataSource source, DataSource target, String predicate, 
            boolean symetric, boolean transative) throws IDMapperException;
    
    public void insertLink(String sourceId, String targetId, int mappingSet, boolean symetric) throws IDMapperException;

    public void openInput() throws IDMapperException;

    public void closeInput()throws IDMapperException;
}
