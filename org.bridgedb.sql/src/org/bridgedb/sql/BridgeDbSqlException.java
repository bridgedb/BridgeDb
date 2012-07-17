package org.bridgedb.sql;

import org.bridgedb.IDMapperException;

/**
 * A BridgeDB Exception thrown by the SQL modules.
 * @author Christian
 */
public class BridgeDbSqlException extends IDMapperException{

    public BridgeDbSqlException(String msg, Exception ex) {
        super(msg, ex);
    }

    public BridgeDbSqlException(String msg, Exception ex, String query) {
        super(msg + ":" + query, ex);
        System.err.println(query);
    }

    public BridgeDbSqlException(String msg) {
        super (msg);
    }

}
