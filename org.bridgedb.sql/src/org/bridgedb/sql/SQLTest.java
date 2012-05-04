/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author Christian
 */
public class SQLTest {
    
    public static void main(String[] args) throws BridgeDbSqlException, SQLException{
        long start = new Date().getTime();
        SQLAccess sqlAccess = SqlFactory.createSQLAccess();
        Connection connection = sqlAccess.getAConnection();
        String query = "SELECT SourceURL FROM link UNION SELECT TargetURL from link";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        System.out.println(new Date().getTime() - start);
        //URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);

    }
}
