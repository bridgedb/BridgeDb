package org.bridgedb.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * A wrapper around the individual SQL DataBase Drivers.
 * <p>
 * Also serves to hide the specific database name, user and password from the rest of the code.
 * <p>
 * Allows MySQL, Virtuoso and any future required drivers to be used without changing SQL code.
 * Allows test, load, live or any other dataBase to be inserted, again without changing the SQL code.
 * @author Christian
 */
public interface SQLAccess {

    /**
     * Allows SQL code to obtain a new Connection without having access to the Database name, user name and password.
     * @return An open Connection
     * @throws BridgeDbSqlException 
     */
    public Connection getConnection()  throws BridgeDbSqlException;
    
}
