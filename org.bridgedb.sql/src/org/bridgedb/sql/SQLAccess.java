/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.sql.Connection;

/**
 *
 * @author Christian
 */
public interface SQLAccess {

    public Connection getConnection()  throws BridgeDbSqlException;
    
}
