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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefWithSymbol;



//import org.pathvisio.debug.Logger;

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
 * to a {@link DBConnector} instance.
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
class SimpleGdbImpl2 extends SimpleGdb
{		
	private static final int GDB_COMPAT_VERSION = 2; //Preferred schema version

	/**
	 * helper class that handles lazy initialization of all prepared statements used by SimpleGdbImpl2
	 * Non-static, because it needs the con database connection field.
	 */
	private class LazyPst
	{
		/**
		 * Initialize with given SQL string, but don't create PreparedStatement yet.
		 * Valid to call before database connection is created.
		 */
		private LazyPst(String aSql)
		{
			sql = aSql;
		}
		
		private PreparedStatement pst = null;
		private final String sql;
		
		/**
		 * returns a prepared statement for the given query.
		 * Uses lazy initialization.
		 * 
		 * Assumes SimpleGdbImpl2.con is already valid
		 */
		public PreparedStatement getPreparedStatement() throws SQLException
		{
			if (pst == null)
			{
				pst = con.prepareStatement(sql);
			}
			return pst;
		}
	}
	
	final LazyPst pstDatasources = new LazyPst(
			"SELECT codeRight FROM link GROUP BY codeRight"
		);
	final LazyPst pstXrefExists = new LazyPst(
			"SELECT id FROM " + "datanode" + " WHERE " +
			"id = ? AND code = ?"
		);
	final LazyPst pstGeneSymbol = new LazyPst(
			"SELECT attrvalue FROM attribute WHERE " +
			"attrname = 'Symbol' AND id = ? " +
			"AND code = ?"
		);
	final LazyPst pstBackpage = new LazyPst(
			"SELECT backpageText FROM datanode " +
			" WHERE id = ? AND code = ?"
		);
	final LazyPst pstCrossRefs = new LazyPst (
			"SELECT dest.idRight, dest.codeRight FROM link AS src JOIN link AS dest " +
			"ON src.idLeft = dest.idLeft and src.codeLeft = dest.codeLeft " +
			"WHERE src.idRight = ? AND src.codeRight = ?"
		);
	final LazyPst pstCrossRefsWithCode = new LazyPst (
			"SELECT dest.idRight, dest.codeRight FROM link AS src JOIN link AS dest " +
			"ON src.idLeft = dest.idLeft and src.codeLeft = dest.codeLeft " +
			"WHERE src.idRight = ? AND src.codeRight = ? AND dest.codeRight = ?"
		);
	final LazyPst pstRefsByAttribute = new LazyPst (
			"SELECT datanode.id, datanode.code FROM datanode " +
			" LEFT JOIN attribute ON attribute.code = datanode.code AND attribute.id = datanode.id " +
			"WHERE attrName = ? AND attrValue = ?"
		);
	final LazyPst pstIdSuggestions = new LazyPst (
			"SELECT id, code FROM datanode WHERE " +
			"LOWER(ID) LIKE ?"
		);
	final LazyPst pstSymbolSuggestions = new LazyPst (
			"SELECT attrvalue FROM attribute WHERE " +
			"attrname = 'Symbol' AND LOWER(attrvalue) LIKE ?"
		);
	
	/**
	 * @param id The gene id to get the symbol info for
	 * @param code systemcode of the gene identifier
	 * @return The gene symbol, or null if the symbol could not be found
	 */
	public String getGeneSymbol(Xref ref) throws IDMapperException 
	{
		try {
			PreparedStatement pst = pstGeneSymbol.getPreparedStatement();
			pst.setString(1, ref.getId());
			pst.setString(2, ref.getDataSource().getSystemCode());
			ResultSet r = pst.executeQuery();

			while(r.next()) 
			{
				return r.getString(1);
			}
		} catch (SQLException e) {
			throw new IDMapperException (e);
		}
		return null;
	}
	
	/**
	 * Simply checks if an xref occurs in the datanode table.
	 * @throws IDMapperException 
	 */
	public boolean xrefExists(Xref xref) throws IDMapperException 
	{
		try 
		{
			PreparedStatement pst = pstXrefExists.getPreparedStatement();
			pst.setString(1, xref.getId());
			pst.setString(2, xref.getDataSource().getSystemCode());
			ResultSet r = pst.executeQuery();

			while(r.next()) 
			{
				return true;
			}
		} 
		catch (SQLException e) 
		{
			throw new IDMapperException (e);
		}
		return false;
	}

	/**
	 * Gets the backpage info for the given gene id for display on BackpagePanel
	 * @param ref The gene to get the backpage info for
	 * @return String with the backpage info, null if no info was found
	 */
	public String getBpInfo(Xref ref) throws IDMapperException 
	{
		try {
			PreparedStatement pst = pstBackpage.getPreparedStatement();
			pst.setString (1, ref.getId());
			pst.setString (2, ref.getDataSource().getSystemCode());
			ResultSet r = pst.executeQuery();
			String result = null;
			if (r.next())
			{
				result = r.getString(1);
			}
			return result;
		} catch	(SQLException e) { throw new IDMapperException (e); } //Gene not found
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
	@Override
	public List<Xref> getCrossRefs (Xref idc, DataSource resultDs) throws IDMapperException 
	{
//		Logger.log.trace("Fetching cross references");

		List<Xref> refs = new ArrayList<Xref>();
		
		try
		{
			PreparedStatement pst;
			if (resultDs == null)
			{
				pst = pstCrossRefs.getPreparedStatement();
			}
			else
			{
				pst = pstCrossRefsWithCode.getPreparedStatement();
				pst.setString(3, resultDs.getSystemCode());
			}
			
			pst.setString(1, idc.getId());
			pst.setString(2, idc.getDataSource().getSystemCode());
			
			ResultSet rs = pst.executeQuery();
			while (rs.next())
			{
				refs.add (new Xref (
						rs.getString(1), 
						DataSource.getBySystemCode(rs.getString(2))
					));
			}
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
		
		return refs;
	}

	public List<Xref> getCrossRefsByAttribute(String attrName, String attrValue) throws IDMapperException {
//		Logger.log.trace("Fetching cross references by attribute: " + attrName + " = " + attrValue);
		List<Xref> refs = new ArrayList<Xref>();

		try {
			PreparedStatement pst = pstRefsByAttribute.getPreparedStatement();
			pst.setString(1, attrName);
			pst.setString(2, attrValue);
			ResultSet r = pst.executeQuery();
			while(r.next()) {
				Xref ref = new Xref(r.getString(1), DataSource.getBySystemCode(r.getString(2)));
				refs.add(ref);
			}
		} catch(SQLException e) {
			throw new IDMapperException (e);
		}
//		Logger.log.trace("End fetching cross references by attribute");
		return refs;
	}

	/**
	 * Opens a connection to the Gene Database located in the given file
	 * @param dbName The file containing the Gene Database. 
	 * @param connector An instance of DBConnector, to determine the type of database (e.g. DataDerby).
	 * A new instance of this class is created automatically.
	 */
	public SimpleGdbImpl2(String dbName, DBConnector newDbConnector, int props) throws IDMapperException
	{
		if(dbName == null) throw new NullPointerException();

		this.dbName = dbName;
		try
		{
			// create a fresh db connector of the correct type.
			this.dbConnector = newDbConnector.getClass().newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IDMapperException (e);
		} 
		catch (IllegalAccessException e) 
		{
			throw new IDMapperException (e);
		}

//		Logger.log.trace("Opening connection to Gene Database " + dbName);

		con = dbConnector.createConnection(dbName, props);
		if ((props & DBConnector.PROP_RECREATE) == 0)
		{
			try
			{
				con.setReadOnly(true);
			}
			catch (SQLException e)
			{
				throw new IDMapperException (e);
			}
			checkSchemaVersion();
		}
	}
	
	private void checkSchemaVersion() throws IDMapperException 
	{
		int version = 0;
		try 
		{
			ResultSet r = con.createStatement().executeQuery("SELECT schemaversion FROM info");
			if(r.next()) version = r.getInt(1);
		} 
		catch (SQLException e) 
		{
			//Ignore, older db's don't even have schema version
		}
		if(version != GDB_COMPAT_VERSION) 
		{
			throw new IDMapperException ("Implementation and schema version mismatch");
		}
	}

	/**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @param convertCon	The connection to the database the tables are created in
	 * Note: Official GDB's are created by AP, not with this code.
	 * This is just here for testing purposes.
	 */
	public void createGdbTables() 
	{
//		Logger.log.info("Info:  Creating tables");
		try 
		{
			Statement sh = con.createStatement();
			sh.execute("DROP TABLE info");
			sh.execute("DROP TABLE link");
			sh.execute("DROP TABLE datanode");
			sh.execute("DROP TABLE attribute");
		} 
		catch(Exception e) 
		{
//			Logger.log.error("Unable to drop gdb tables (ignoring): " + e.getMessage());
		}

		try
		{
			Statement sh = con.createStatement();
			sh.execute(
					"CREATE TABLE					" +
					"		info							" +
					"(	  schemaversion INTEGER PRIMARY KEY		" +
			")");
//			Logger.log.info("Info table created");
			sh.execute( //Add compatibility version of GDB
					"INSERT INTO info VALUES ( " + GDB_COMPAT_VERSION + ")");
//			Logger.log.info("Version stored in info");
			sh.execute(
					"CREATE TABLE					" +
					"		link							" +
					" (   idLeft VARCHAR(50) NOT NULL,		" +
					"     codeLeft VARCHAR(50) NOT NULL,	" +
					"     idRight VARCHAR(50) NOT NULL,		" +
					"     codeRight VARCHAR(50) NOT NULL,	" +
					"     bridge VARCHAR(50),				" +
					"     PRIMARY KEY (idLeft, codeLeft,    " +
					"		idRight, codeRight) 			" +
					" )										");
//			Logger.log.info("Link table created");
			sh.execute(
					"CREATE TABLE					" +
					"		datanode						" +
					" (   id VARCHAR(50),					" +
					"     code VARCHAR(50),					" +
					"     backpageText VARCHAR(800),		" +
					"     PRIMARY KEY (id, code)    		" +
					" )										");
//			Logger.log.info("DataNode table created");
			sh.execute(
					"CREATE TABLE							" +
					"		attribute 						" +
					" (   id VARCHAR(50),					" +
					"     code VARCHAR(50),					" +
					"     attrname VARCHAR(50),				" +
					"	  attrvalue VARCHAR(255)			" +
					" )										");
//			Logger.log.info("Attribute table created");
		} 
		catch (Exception e)
		{
//			Logger.log.error("while creating gdb tables: " + e.getMessage(), e);
		}
	}

	
	public static final int NO_LIMIT = 0;
	public static final int NO_TIMEOUT = 0;
	public static final int QUERY_TIMEOUT = 20; //seconds

	/**
	 * Get up to limit suggestions for a symbol autocompletion
	 * case Insensitive 
	 * 
	 * @param text The text to base the suggestions on
	 * @param limit The number of results to limit the search to
	 * 
	 * @throws IDMapperException 
	 */
	public List<String> getSymbolSuggestions(String text, int limit) throws IDMapperException 
	{		
		List<String> result = new ArrayList<String>();
		try {

			PreparedStatement pst = pstSymbolSuggestions.getPreparedStatement();
			pst.setQueryTimeout(QUERY_TIMEOUT);
			if(limit > NO_LIMIT) pst.setMaxRows(limit);
			pst.setString(1, text.toLowerCase() + "%");
			ResultSet r = pst.executeQuery();

			while(r.next()) 
			{
				String symbol = r.getString("attrValue");
				result.add(symbol);
			}
		} catch (SQLException e) {
			throw new IDMapperException (e);
		}
		return result;
	}
	
	/**
	 * Get up to limit suggestions for a identifier autocompletion
	 * @param text The text to base the suggestions on
	 * @param limit The number of results to limit the search to
	 * @param caseSensitive if true, the search will be case sensitive
	 */
	public List<Xref> getIdSuggestions(String text, int limit) throws IDMapperException 
	{		
		List<Xref> result = new ArrayList<Xref>();
		try {
			PreparedStatement pst = pstIdSuggestions.getPreparedStatement();
			pst.setQueryTimeout(QUERY_TIMEOUT);
			if(limit > NO_LIMIT) pst.setMaxRows(limit);
			pst.setString (1, text.toLowerCase() + "%");
			ResultSet r = pst.executeQuery();

			while(r.next()) {
				String id = r.getString(1);
				DataSource ds = DataSource.getBySystemCode(r.getString(2));
				Xref ref = new Xref(id, ds);
				result.add (ref);
			}
		} catch (SQLException e) {
			throw new IDMapperException (e);
		}
//		if(limit > NO_LIMIT && sugg.size() == limit) sugg.add("...results limited to " + limit);
		return result;
	}

	/**
	 * free text search for matching symbols or identifiers
	 * @param text The text to base the suggestions on
	 * @param limit The number of results to limit the search to
	 * @throws IDMapperException 
	 */
	public List<XrefWithSymbol> freeSearchWithSymbol (String text, int limit) throws IDMapperException 
	{		
		List<XrefWithSymbol> result = new ArrayList<XrefWithSymbol>();
		try {
			PreparedStatement ps1 = con.prepareStatement(
					"SELECT dn.id, dn.code, attr.attrvalue " +
					"FROM datanode AS dn LEFT JOIN attribute AS attr ON " +
					"	dn.id = attr.id AND dn.code = attr.code " +
					"WHERE " +
					"		LOWER(dn.id) LIKE ?" +
					"	AND " +
					"			(attr.attrname = 'Symbol' " +
					"	OR attr.attrname IS NULL) "
					);
			ps1.setQueryTimeout(QUERY_TIMEOUT);
			if(limit > NO_LIMIT) 
			{
				ps1.setMaxRows(limit);
			}

			ps1.setString(1, text.toLowerCase() + "%");
			ResultSet r = ps1.executeQuery();
			while(r.next()) {
				String id = r.getString(1);
				DataSource ds = DataSource.getBySystemCode(r.getString(2));
				String sym = r.getString(3);
				XrefWithSymbol ref = new XrefWithSymbol (new Xref(id, ds), sym);
				result.add (ref);
			}
			
			if (result.size() >= limit)
			{
				return result;
			}
			
			PreparedStatement ps2 = con.prepareStatement(
					"SELECT attr.id, attr.code, attr.attrvalue " +
					"FROM attribute AS attr " +
					"WHERE " +
					"	LOWER(attr.attrvalue) LIKE ?"
			);
			ps2.setString(1, "%" + text.toLowerCase() + "%");
			ps2.setQueryTimeout(QUERY_TIMEOUT);
			r = ps2.executeQuery();

			while(r.next()) {
				String id = r.getString(1);
				DataSource ds = DataSource.getBySystemCode(r.getString(2));
				String sym = r.getString(3);
				XrefWithSymbol ref = new XrefWithSymbol (new Xref(id, ds), sym);
				result.add (ref);
			}
			
		} catch (SQLException e) {
			throw new IDMapperException(e);
		}
		return result;
	}
	
    PreparedStatement pstGene = null;
    PreparedStatement pstLink = null;
    PreparedStatement pstAttr = null;

	/**
	 * Add a gene to the gene database
	 */
	public int addGene(Xref ref, String bpText) 
	{
    	if (pstGene == null) throw new NullPointerException();
		try 
		{
			pstGene.setString(1, ref.getId());
			pstGene.setString(2, ref.getDataSource().getSystemCode());
			pstGene.setString(3, bpText);
			pstGene.executeUpdate();
		} 
		catch (Exception e) 
		{ 
//			Logger.log.error("" + ref, e);
			return 1;
		}
		return 0;
    }
    
    public int addAttribute(Xref ref, String attr, String val)
    {
    	try {
    		pstAttr.setString(1, attr);
			pstAttr.setString(2, val);
			pstAttr.setString(3, ref.getId());
			pstAttr.setString(4, ref.getDataSource().getSystemCode());
			pstAttr.executeUpdate();
		} catch (Exception e) {
//			Logger.log.error(attr + "\t" + val + "\t" + ref, e);
			return 1;
		}
		return 0;
    }

    /**
     * Add a link to the gene database
     */
    public int addLink(Xref left, Xref right) 
    {
    	if (pstLink == null) throw new NullPointerException();
    	try 
    	{
			pstLink.setString(1, left.getId());
			pstLink.setString(2, left.getDataSource().getSystemCode());
			pstLink.setString(3, right.getId());
			pstLink.setString(4, right.getDataSource().getSystemCode());
			pstLink.executeUpdate();
		} 
		catch (Exception e)
		{
//			Logger.log.error(left + "\t" + right , e);
			return 1;
		}
		return 0;
	}

	/**
	   Create indices on the database
	   You can call this at any time after creating the tables,
	   but it is good to do it only after inserting all data.
	 */
	public void createGdbIndices() throws IDMapperException 
	{
		try
		{
			Statement sh = con.createStatement();
			sh.execute(
					"CREATE INDEX i_codeLeft" +
					" ON link(codeLeft)"
			);
			sh.execute(
					"CREATE INDEX i_idRight" +
					" ON link(idRight)"
			);
			sh.execute(
					"CREATE INDEX i_codeRight" +
					" ON link(codeRight)"
			);
			sh.execute(
					"CREATE INDEX i_code" +
					" ON " + "datanode" + "(code)"
			);
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
	}

	/**
	   prepare for inserting genes and/or links
	 */
	public void preInsert() throws IDMapperException
	{
		try
		{
			con.setAutoCommit(false);
			pstGene = con.prepareStatement(
				"INSERT INTO datanode " +
				"	(id, code," +
				"	 backpageText)" +
				"VALUES (?, ?, ?)"
	 		);
			pstLink = con.prepareStatement(
				"INSERT INTO link " +
				"	(idLeft, codeLeft," +
				"	 idRight, codeRight)" +
				"VALUES (?, ?, ?, ?)"
	 		);
			pstAttr = con.prepareStatement(
					"INSERT INTO attribute " +
					"	(attrname, attrvalue, id, code)" +
					"VALUES (?, ?, ?, ?)"
					);
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
	}

	private Set<DataSource> getDataSources()
	{
		Set<DataSource> result = new HashSet<DataSource>();
    	try
    	{
    	 	PreparedStatement pst = pstDatasources.getPreparedStatement();
    	 	ResultSet rs = pst.executeQuery();
    	 	while (rs.next())
    	 	{
    	 		DataSource ds = DataSource.getBySystemCode(rs.getString(1)); 
    	 		System.out.println (ds + "\t" + ds.getSystemCode());
    	 		result.add (ds);
    	 	}
    	}
    	catch (SQLException ignore)
    	{
    		ignore.printStackTrace(); 
    		// we return an empty list if there was an exception
    	}
    	return result;
	}
	
	private final IDMapperCapabilities caps = new IDMapperCapabilities()
	{
	    public boolean isFreeSearchSupported() { return true; }

	    public Set<DataSource> getSupportedSrcDataSources()
	    {
	    	return getDataSources(); 
	    }

	    public Set<DataSource> getSupportedTgtDataSources()
	    {
	    	return getDataSources(); 
	    }
	};

	public IDMapperCapabilities getCapabilities() 
	{
		return caps;
	}
}
