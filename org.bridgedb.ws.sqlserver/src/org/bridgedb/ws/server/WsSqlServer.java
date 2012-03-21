/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.server;


import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.IDMapperSQL;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.ws.WSService;
import org.bridgedb.ws.XrefByPossition;

/**
 *
 * @author Christian
 */
public class WsSqlServer extends WSService{
    
    public WsSqlServer() throws BridgeDbSqlException  {
        SQLAccess sqlAccess = SqlFactory.createIDSQLAccess();
        idMapper = new IDMapperSQL(sqlAccess);
        byPossition = (XrefByPossition)idMapper;
    }

    

}


