// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.virtuoso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;

/**
 * Virtuosos specif wrapper.
 * 
 * Still in test so user name and password hard coded in.
 * 
 * @author Christian
 */
public class VirtuosoAccess implements SQLAccess{
    /** JDBC URL for the database */
    private String dbUrl;// = "jdbc:mysql://localhost:3306/irs";
    /** username for the database */
    private String username;// = "irs";
    /** password for the database */
    private String password;// = "irs";
    
    /**
     * Instantiate a connection to the database
     * 
     * @throws IMSException If there is a problem connecting to the database.
     */
    public VirtuosoAccess() throws BridgeDbSqlException {
        try {
            Class.forName("virtuoso.jdbc4.Driver");
            //if (dbUrl.equals("jdbc:mysql://localhost:3306/irs")){
            //    throw new BridgeDbSqlException ("Saftey Error! "
            //            + "jdbc:mysql://localhost:3306/irs is resevered for March 2012 version");
            //}
            this.dbUrl = "jdbc:virtuoso://localhost:1111";
            this.username = "dba";
            this.password = "dba";
        //} catch (SQLError er){
        //    String msg = "Problem loading in MySQL JDBC driver.";
        //    throw new BridgeDbSqlException(msg);
        } catch (ClassNotFoundException ex) {
            String msg = "Problem loading in virtuoso JDBC driver.";
            //Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, msg, ex);
            throw new BridgeDbSqlException(msg, ex);
        }
    }

    /**
     * Retrieve an active connection to the database
     * 
     * @return database connection
     * @throws IMSException if there is a problem establishing a connection
     */
    @Override
    public Connection getConnection() throws BridgeDbSqlException {
        try {
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            return conn;
        } catch (SQLException ex) {
            System.err.println(ex);
            final String msg = "Problem connecting to database.";
            //Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, msg, ex);
            throw new BridgeDbSqlException(msg, ex);
        }
    }
    
}
