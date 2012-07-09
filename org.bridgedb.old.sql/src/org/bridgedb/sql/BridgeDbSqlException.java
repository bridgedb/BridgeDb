/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import org.bridgedb.IDMapperException;

/**
 *
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
