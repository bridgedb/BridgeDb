/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.server;

import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.IDMapperSQL;
import org.bridgedb.sql.MySQLAccess;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.ws.WSService;

/**
 *
 * @author Christian
 */
public class WsSqlServer extends WSService{
    
    public WsSqlServer() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess("jdbc:mysql://localhost:3306/imstest", "imstest", "imstest");
        idMapper = new IDMapperSQL(sqlAccess);
    }

    
}
