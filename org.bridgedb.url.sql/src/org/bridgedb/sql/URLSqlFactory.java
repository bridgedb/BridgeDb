/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

/**
 *
 * @author Christian
 */
public class URLSqlFactory {

    public static SQLAccess createSQLAccess() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess("jdbc:mysql://localhost:3306/imsurltest", "imstest", "imstest");
        sqlAccess.getConnection();
        return sqlAccess;
    } 
}
