package org.bridgedb.sql;

/**
 * Specifies how the particular choosen DataBase System acts in cases where not all systems act the same.
 * @author Christian
 */
public interface SQLSpecific {
    
    /**
     * Identifies if the underlying System support checking if a previouly open Connection is still valid.
     * @return True if and only if the connection.isValid() call will not throw an error.
     */
    public boolean supportsIsValid();
    
    /**
     * Identifies if the underlying System is known to support multiple insertions.
     * @return True if and olny if insertions in the format INSERT INTO ... (...) VALUES (...),(...),(...) 
     *     will not throw an error
     */
    public boolean supportsMultipleInserts();
        
    /**
     * This identifies version of SQL such as MySQL that use "LIMIT" to restrict the number of tuples returned.
     */
    public boolean supportsTop();
    
    /**
     * This identifies version of SQL such as Virtuoso that use "TOP" to restrict the number of tuples returned.
     */
    public boolean supportsLimit();
    
    /**
     * Returns the specific String that is used when creating an Auto Increment Column.
     * This identifies a column where the DataBase system will automatically ad the next available id on an insert.
     * @return 
     */
    public String getAutoIncrementCommand();
}
