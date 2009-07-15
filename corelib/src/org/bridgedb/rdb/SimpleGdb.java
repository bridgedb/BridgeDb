// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

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
public abstract class SimpleGdb extends IDMapperRdb implements GdbConstruct
{		
	/**
	 * The {@link Connection} to the Gene Database.
	 */
	protected Connection con = null;
	// dbConnector, helper class for dealing with RDBMS specifcs.
	protected DBConnector dbConnector;

	/** {@inheritDoc} */
	final public boolean isConnected() { return con != null; }

	protected String dbName;
	
	/** {@inheritDoc} */
	@Override final public String getDbName() { return dbName; }

	/**
	 * @param ref The reference  to get the symbol info for
	 * @return The gene symbol, or null if the symbol could not be found
	 * @throws IDMapperException when the database is unavailable
	 * @deprecated use getAttribute (ref, "Symbol") instead
	 */
	abstract public String getGeneSymbol(Xref ref) throws IDMapperException; 
		
	/** {@inheritDoc} */
	abstract public boolean xrefExists(Xref xref) throws IDMapperException; 

	/** {@inheritDoc} */
	abstract public String getBpInfo(Xref ref) throws IDMapperException; 

	/**
	 * Get all cross-references for the given id/code pair, restricting the
	 * result to contain only references from database with the given system
	 * code.
	 * @param idc The id/code pair to get the cross references for
	 * @return An {@link List} containing the cross references, or an empty
	 * ArrayList when no cross references could be found
	 * @throws IDMapperException when the database is unavailable
	 */
	final public List<Xref> mapID(Xref idc) throws IDMapperException
	{
		return mapID(idc, null);
	}

	/** {@inheritDoc} */
	abstract public List<Xref> mapID (Xref idc, DataSource resultDs) throws IDMapperException; 

	/** {@inheritDoc} */
	abstract public List<Xref> getCrossRefsByAttribute(String attrName, String attrValue) throws IDMapperException;
	
	/** {@inheritDoc} */
	final public void close() throws IDMapperException 
	{
		if (con == null) throw new IDMapperException("Database connection already closed");
		dbConnector.closeConnection(con);
		try
		{
			con.close();
		}
		catch (SQLException ex)
		{
			throw new IDMapperException (ex);
		}
		con = null;
	}

	/**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * Note: Official GDB's are created by Alex Pico's script, not with this code.
	 * This is just here for testing purposes.
	 */
	abstract public void createGdbTables();
	
	public static final int NO_LIMIT = 0;
	public static final int NO_TIMEOUT = 0;
	public static final int QUERY_TIMEOUT = 5; //seconds

	/**
	   prepare for inserting genes and/or links.
	   @throws IDMapperException on failure
	 */
	abstract public void preInsert() throws IDMapperException;

	/**
	   commit inserted data.
	   @throws IDMapperException on failure
	 */
	final public void commit() throws IDMapperException
	{
		try
		{
			con.commit();
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
	}

	/**
	   @return number of rows in gene table.
	   @throws IDMapperException on failure
	 */
	final public int getGeneCount() throws IDMapperException
	{
		int result = 0;
		try
		{
			ResultSet r = con.createStatement().executeQuery("SELECT COUNT(*) FROM " + "datanode");
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
	   compact the database.
	   @throws IDMapperException on failure
	 */
	final public void compact() throws IDMapperException
	{
		dbConnector.compact(con);
	}
	
	/**
	   finalize the database.
	   @throws IDMapperException on failure
	 */
	final public void finalize() throws IDMapperException
	{
		dbConnector.compact(con);
		createGdbIndices();
		dbConnector.closeConnection(con, DBConnector.PROP_FINALIZE);
		String newDb = dbConnector.finalizeNewDatabase(dbName);
		dbName = newDb;
	}

	
}
