// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
package org.bridgedb.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourceOverwriteLevel;
import org.bridgedb.mapping.MappingListener;
import org.bridgedb.rdf.BridgeDBRdfHandler;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;

/**
 * This is the root class of the SQL stack.
 * It handles the creation of all the tables and handles all inserts, except those only required for URL and UriSpaces
 * 
 * See CreateSQLTables method for an explanation of the tables.
 * 
 * @author Christian
 */
public class SQLBase {
    
    private static final int SQL_TIMEOUT = 2;

    protected SQLAccess sqlAccess;
    protected Connection possibleOpenConnection;
    private final boolean supportsIsValid;
    
    static final Logger logger = Logger.getLogger(SQLBase.class);

    public SQLBase(StoreType storeType) throws BridgeDBException{
       this.sqlAccess = SqlFactory.createTheSQLAccess(storeType);
       this.supportsIsValid = SqlFactory.supportsIsValid();
   }
        	
	private void checkConnection() throws BridgeDBException, SQLException {
		if (possibleOpenConnection == null){
			possibleOpenConnection = sqlAccess.getConnection();
		} else if (possibleOpenConnection.isClosed()){
			possibleOpenConnection = sqlAccess.getConnection();
		} else if (supportsIsValid && !possibleOpenConnection.isValid(SQL_TIMEOUT)){
			possibleOpenConnection.close();
			possibleOpenConnection = sqlAccess.getConnection();
		}
	}
  
    /**
     * 
     * @return
     * @throws BridgeDBException 
     */
    public final Statement createStatement() throws BridgeDBException{
        try {
            checkConnection();  
            return possibleOpenConnection.createStatement();
        } catch (SQLException ex) {
            throw new BridgeDBException ("Error creating a new statement ", ex);
        }
    }
    
    public final PreparedStatement createPreparedStatement(String sql) throws BridgeDBException {
    	try {
    		checkConnection();
    		return possibleOpenConnection.prepareStatement(sql);
    	} catch (SQLException ex) {
    		throw new BridgeDBException ("Error creating a new prepared statement " + sql, ex);
    	}
    }
    
    public final void startTransaction() throws BridgeDBException {
    	try {
			checkConnection();
			possibleOpenConnection.setAutoCommit(false);
		} catch (SQLException ex) {
			throw new BridgeDBException("Error starting transaction.", ex);
		}
    }
    
    public final void commitTransaction() throws BridgeDBException {
    	try {
			possibleOpenConnection.commit();
		} catch (SQLException ex) {
			throw new BridgeDBException("Error commiting transaction.", ex);
		}
    }
    
    public final void rollbackTransaction() throws BridgeDBException {
    	try {
    		possibleOpenConnection.rollback();    		
    	} catch (SQLException ex) {
    		throw new BridgeDBException("Error rolling back transaction.", ex);
    	}
    }
    
    public final String insertEscpaeCharacters(String original) {
       String result = original.replaceAll("\\\\", "\\\\\\\\");
       result = result.replaceAll("'", "\\\\'");
       result = result.replaceAll("\"", "\\\\\"");
       return result;
    }


}
