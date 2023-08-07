// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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
package org.bridgedb.rdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

/**
 * SimpleGdb is the main implementation of the Gdb interface,
 * for dealing with single SQL-based pgdb's.
 * It's responsible for creating and querying a single 
 * pgdb relational database through the JDBC interface.
 * <p>
 * It wraps SQL statements in methods, 
 * so the rest of the apps don't need to know the
 * details of the Database schema.
 * <p>
 * It delegates dealing with the differences between 
 * various RDBMS's (Derby, Hsqldb etc.)
 * to a DBConnector instance.
 * A correct DBConnector instance needs to be 
 * passed to the constructor of SimpleGdb. 
 * <p>
 * In the PathVisio GUI environment, use GdbManager
 * to create and connect one or two centralized Gdb's. 
 * This will also automatically
 * find the right DBConnector from the preferences.
 * <p>
 * In a head-less or test environment, you can bypass GdbManager
 * and use SimpleGdb directly 
 * to create or connect to one or more pgdb's of any type.
 */
public abstract class SimpleGdb extends IDMapperRdb
{
	private final String connectionString;
	/**
	 * Create IDMapper based on an existing SQL connection.
	 * @param con Existing SQL Connection.
	 */
	SimpleGdb(String dbName, String connectionString)
	{
		this.connectionString = connectionString;
		this.dbName = dbName;
	}

	private boolean singleConnection = true;
	private boolean neverCloseConnection = true;
	
	/**
	 * helper class that handles the life cycle of a connection, query and resultset.
	 * <p>
	 * The sql for a query is passed in at construction time.
	 * Before each query, call init(). This will lead to lazy initialization of the
	 * connection and preparedstatement objects, if necessary. Set the query parameters
	 * using setString(int, String). Get the resultSet using 
	 * Do not close the resultset! This will be closed for you when you call cleanup().
	 * Always call cleanup() in a finally block.
	 * <p>
	 * The advantages of using QueryLifeCycle are:
	 * <ul>
	 * <li>guarantee to close preparedstatement, resultset and connection if necessary.
	 * <li>in case of connection pooling, preparedstatement and connection are kept together as long
	 *   as possible.
	 * <li>lazy initialization of prepared statement
	 * <li>always uses preparedstatement, so safe from SQL injection.
	 * </ul> 
	 * <p>
	 * This class is not static because it needs SimpleGdb.getConnection().
	 */
	final class QueryLifeCycle
	{
		/**
		 * Initialize with given SQL string, but don't create PreparedStatement yet.
		 * Valid to call before database connection is created.
		 * @param aSql SQL query
		 */
		public QueryLifeCycle(String aSql)
		{
			sql = aSql;
		}
		
		private Connection con = null;
		private ResultSet rs = null;
		private PreparedStatement pst = null;
		private final String sql;
		private boolean inited = false;

		public static final int QUERY_TIMEOUT = 20; //seconds
		public static final int NO_LIMIT = 0;
		public static final int NO_TIMEOUT = 0;

		public void init(int limit) throws SQLException
		{
			init();
			pst.setQueryTimeout(QUERY_TIMEOUT);			
			if(limit > NO_LIMIT) 
			{
				pst.setMaxRows(limit);
			}
		}
		
		/**
		 * Initialize connection and PreparedStatement lazily.
		 * <p>
		 * @throws SQLException when a PreparedStatement could not be created
		 */
		public void init() throws SQLException
		{
			if (inited) throw new IllegalStateException("Must call cleanup() between two init() calls");
			try
			{
				if (con == null) con = getConnection();
				if (pst == null)
				{
					pst = con.prepareStatement(sql);
				}
			}
			finally { inited = true; }
		}
		
		public void setString (int index, String val) throws SQLException
		{
			if (!inited) throw new IllegalStateException("Must call init() before setString()");
			pst.setString(index, val);
		}
		
		public ResultSet executeQuery() throws SQLException
		{
			if (!inited) throw new IllegalStateException("Must call init() before executeQuery()");
			rs = pst.executeQuery();
			return rs;
		}

		/** 
		 * Clean up resultset. If keepConnection is false, preparedstatement
		 * and connection are cached. If keepConnection is true, they are closed as well.
		 * The later is useful when using connection pooling.
		 * <p>
		 * Always call this in a finally block! 
		 * */
		public void cleanup()
		{
			if (!inited) throw new IllegalStateException("Must call init() before cleanup()");
			inited = false;
			if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
			if (neverCloseConnection) return;
			if (pst != null) try { pst.close(); } catch (SQLException ignore) {}
			pst = null;
			if (con != null) try { con.close(); } catch (SQLException ignore) {}
			con = null;
		}
	}

	private Connection con = null;
	
	synchronized public Connection getConnection() throws SQLException
	{
		// if singleConnection is true, each call to getConnection() will return the same object.
		// if singleConnection is false, each call to getConneciton() will lead to a new connection object being created.
		if (!singleConnection || con == null)
		{
			con = DriverManager.getConnection(connectionString); 
			con.setReadOnly(true);
		}
		return con;
	}
	
	/**
	 * The {@link Connection} to the Gene Database.
	 */
	
	//private Connection con = null;

	/** {@inheritDoc} */
	final public boolean isConnected() { 
		//return con != null; 
		return true;
	}

	protected final String dbName;
	
	/** {@inheritDoc} */
	@Override final public String getDbName() { return dbName; }
	
	/** {@inheritDoc} */
	final public void close() throws IDMapperException 
	{
//		try
//		{
//			con.close();
//		}
//		catch (SQLException ex)
//		{
//			throw new IDMapperException (ex);
//		}
//		con = null;
	}
	
	public static final int NO_LIMIT = 0;
	public static final int NO_TIMEOUT = 0;
	public static final int QUERY_TIMEOUT = 5; //seconds

	/**
	   @return number of rows in gene table.
	   @throws IDMapperException on failure
	 */
	final public int getGeneCount() throws IDMapperException
	{
		int result = 0;
		try
		{
			ResultSet r = getConnection().createStatement().executeQuery("SELECT COUNT(*) FROM " + "datanode");
			r.next();
			result = r.getInt (1);
			r.close();
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
		return result;
	}

	/**
	   @return number of rows in link table.
	   @throws IDMapperException on failure
	 */
	final public int getLinkCount() throws IDMapperException
	{
		int result = 0;
		try
		{
			ResultSet r = getConnection().createStatement().executeQuery("SELECT COUNT(*) FROM " + "link");
			r.next();
			result = r.getInt (1);
			r.close();
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
		return result;
	}

	/**
	 * @param ds DataSource to count identifiers for.
	   @return number of identifiers table for the given datasource
	   @throws IDMapperException on failure
	 */
	final public int getGeneCount(DataSource ds) throws IDMapperException
	{
		int result = 0;
		try
		{
			ResultSet r = getConnection().createStatement().executeQuery(
					"SELECT COUNT(*) FROM datanode WHERE code = '" + ds.getSystemCode() + "'");
			r.next();
			result = r.getInt (1);
			r.close();
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
		return result;
	}
	
	/**
	 * @param ds DataSource to count identifiers for.
	   @return number of identifiers table for the given datasource
	   @throws IDMapperException on failure
	 */
	final public int getPrimaryIDCount(DataSource ds) throws IDMapperException
	{
		int result = 0;
		try
		{
			ResultSet r = getConnection().createStatement().executeQuery(
					"SELECT COUNT(*) FROM datanode WHERE code = '" + ds.getSystemCode() + "' AND isPrimary = 1");
			r.next();
			result = r.getInt (1);
			r.close();
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
		return result;
	}

}
