package org.bridgedb.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;

/**
 * MYSQL specific wrapper.
 * 
 * @author Christian
 */
public class MySQLAccess implements SQLAccess{
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
    public MySQLAccess(String dbUrl, String username, String password) throws BridgeDbSqlException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (dbUrl.equals("jdbc:mysql://localhost:3306/irs")){
                throw new BridgeDbSqlException ("Saftey Error! "
                        + "jdbc:mysql://localhost:3306/irs is resevered for March 2012 version");
            }
            this.dbUrl = dbUrl;
            this.username = username;
            this.password = password;
        //} catch (SQLError er){
        //    String msg = "Problem loading in MySQL JDBC driver.";
        //    throw new BridgeDbSqlException(msg);
        } catch (ClassNotFoundException ex) {
            String msg = "Problem loading in MySQL JDBC driver.";
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
