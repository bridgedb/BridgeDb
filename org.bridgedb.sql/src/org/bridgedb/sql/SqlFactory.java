/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

/**
 *
 * @author Christian
 */
public class SqlFactory {

    public static SQLAccess createIDSQLAccess() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess("jdbc:mysql://localhost:3306/imsidtest", "imstest", "imstest");
        sqlAccess.getConnection();
        return sqlAccess;
    } 

}
