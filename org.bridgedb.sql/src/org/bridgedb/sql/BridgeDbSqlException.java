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

    BridgeDbSqlException(String msg, Exception ex) {
        super(msg, ex);
    }

    BridgeDbSqlException(String msg, Exception ex, String query) {
        super(msg + ":" + query, ex);
        System.err.println(query);
    }

    BridgeDbSqlException(String msg) {
        super (msg);
    }

}
