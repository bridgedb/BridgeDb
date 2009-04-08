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
package org.bridgedb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SimpleGdb is the main implementation of the Gdb interface,
 * for dealing with single SQL-based pgdb's.
 * It's responsible for creating and querying a single 
 * pgdb relational database through the JDBC interface.
 *  
 * It wraps SQL statements in methods, 
 * so the rest of the apps don't need to know the
 * details of the Database schema.
 * 
 * It delegates dealing with the differences between 
 * various RDBMS's (Derby, Hsqldb etc.)
 * to a DBConnector instance.
 * A correct DBConnector instance needs to be 
 * passed to the constructor of SimpleGdb. 
 * 
 * In the PathVisio GUI environment, use GdbManager
 * to create and connect one or two centralized Gdb's. 
 * This will also automatically
 * find the right DBConnector from the preferences.
 *  
 * In a head-less or test environment, you can bypass GdbManager
 * and use SimpleGdb directly 
 * to create or connect to one or more pgdb's of any type.
 */
public abstract class SimpleGdb implements Gdb
{		
	SimpleGdb()
	{
	}
	
	/**
	 * The {@link Connection} to the Gene Database
	 */
	// SQL connection
	protected Connection con = null;
	// dbConnector, helper class for dealing with RDBMS specifcs.
	protected DBConnector dbConnector;

	/**
	 * Check whether a connection to the database exists
	 * @return	true is a connection exists, false if not
	 */
	final public boolean isConnected() { return con != null; }

	protected String dbName;
	/**
	 * Gets the name of te currently used gene database
	 * @return the database name as specified in the connection string
	 */
	final public String getDbName() { return dbName; }

	/**
	 * @param id The gene id to get the symbol info for
	 * @param code systemcode of the gene identifier
	 * @return The gene symbol, or null if the symbol could not be found
	 * @throws DataException 
	 */
	abstract public String getGeneSymbol(Xref ref) throws DataException; 
		
	/**
	 * Simply checks if an xref occurs in the datanode table.
	 * @throws DataException 
	 */
	abstract public boolean xrefExists(Xref xref) throws DataException; 

	/**
	 * Gets the backpage info for the given gene id for display on BackpagePanel
	 * @param ref The gene to get the backpage info for
	 * @return String with the backpage info, null if the gene was not found
	 * @throws DataException 
	 */
	abstract public String getBpInfo(Xref ref) throws DataException; 

	/**
	 * Get all cross-references for the given id/code pair, restricting the
	 * result to contain only references from database with the given system
	 * code
	 * @param idc The id/code pair to get the cross references for
	 * @return An {@link ArrayList} containing the cross references, or an empty
	 * ArrayList when no cross references could be found
	 */
	final public List<Xref> getCrossRefs(Xref idc) throws DataException
	{
		return getCrossRefs(idc, null);
	}

	/**
	 * Get all cross-references for the given id/code pair, restricting the
	 * result to contain only references from database with the given system
	 * code
	 * @param idc The id/code pair to get the cross references for
	 * @param resultCode The system code to restrict the results to
	 * @return An {@link ArrayList} containing the cross references, or an empty
	 * ArrayList when no cross references could be found
	 */
	abstract public List<Xref> getCrossRefs (Xref idc, DataSource resultDs) throws DataException; 

	abstract public List<Xref> getCrossRefsByAttribute(String attrName, String attrValue) throws DataException;
	
	/**
	 * Closes the {@link Connection} to the Gene Database if possible
	 */
	final public void close() throws DataException 
	{
		if (con == null) throw new DataException("Database connection already closed");
		dbConnector.closeConnection(con);
		try
		{
			con.close();
		}
		catch (SQLException ex)
		{
			throw new DataException (ex);
		}
		con = null;
	}

	/**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @param convertCon	The connection to the database the tables are created in
	 * Note: Official GDB's are created by AP, not with this code.
	 * This is just here for testing purposes.
	 */
	abstract public void createGdbTables();
	
	public static final int NO_LIMIT = 0;
	public static final int NO_TIMEOUT = 0;
	public static final int QUERY_TIMEOUT = 5; //seconds

	/**
	 * Get up to limit suggestions for a symbol autocompletion
	 * case Insensitive 
	 * 
	 * @param text The text to base the suggestions on
	 * @param limit The number of results to limit the search to
	 * 
	 * @throws DataException 
	 */
	abstract public List<String> getSymbolSuggestions(String text, int limit) throws DataException;

	/**
	 * Get up to limit suggestions for a identifier autocompletion
	 * case Insensitive
	 * 
	 * @param text The text to base the suggestions on
	 * @param limit The number of results to limit the search to
	 * @throws DataException 
	 */
	abstract public List<Xref> getIdSuggestions(String text, int limit) throws DataException; 

	/**
	 * free text search for matching symbols or identifiers
	 * @param text The text to base the suggestions on
	 * @param limit The number of results to limit the search to
	 * @throws DataException 
	 */
	abstract public List<XrefWithSymbol> freeSearch (String text, int limit) throws DataException; 
	/**
	 * Add a gene to the gene database
	 */
	abstract public int addGene(Xref ref, String bpText); 
    
    abstract public int addAttribute(Xref ref, String attr, String val);

    /**
     * Add a link to the gene database
     */
    abstract public int addLink(Xref left, Xref right); 

	/**
	   Create indices on the database
	   You can call this at any time after creating the tables,
	   but it is good to do it only after inserting all data.
	 */
	abstract public void createGdbIndices() throws DataException;

	/**
	   prepare for inserting genes and/or links
	 */
	abstract public void preInsert() throws DataException;

	/**
	   commit inserted data
	 */
	final public void commit() throws DataException
	{
		try
		{
			con.commit();
		}
		catch (SQLException e)
		{
			throw new DataException (e);
		}
	}

	/**
	   returns number of rows in gene table
	 */
	final public int getGeneCount() throws DataException
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
			throw new DataException (e);
		}
		return result;
	}

	final public void compact() throws DataException
	{
		dbConnector.compact(con);
	}
	
	final public void finalize() throws DataException
	{
		dbConnector.compact(con);
		createGdbIndices();
		dbConnector.closeConnection(con, DBConnector.PROP_FINALIZE);
		String newDb = dbConnector.finalizeNewDatabase(dbName);
		dbName = newDb;
	}

	
}
