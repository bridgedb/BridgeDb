package org.bridgedb.sql;

/**
 *
 * @author Christian
 */
public interface SQLSpecific {
    
    public boolean supportsIsValid();
    
    public boolean supportsMultipleInserts();
    
    public boolean supportsTop();
    
    public boolean supportsLimit();
    
    public String getAutoIncrementCommand();
}
