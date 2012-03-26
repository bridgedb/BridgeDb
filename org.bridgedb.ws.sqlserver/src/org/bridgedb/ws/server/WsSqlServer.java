/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.server;


import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.ws.WSService;

/**
 *
 * @author Christian
 */
public class WsSqlServer extends WSService{
    
    public WsSqlServer() throws BridgeDbSqlException  {
        SQLAccess sqlAccess = SqlFactory.createURLSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        idMapper = urlMapperSQL;
        urlMapper = urlMapperSQL;
        byXrefPossition = urlMapperSQL;
    }

    

}


