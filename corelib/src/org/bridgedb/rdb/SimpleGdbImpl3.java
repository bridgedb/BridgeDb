// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/** {@inheritDoc} */
class SimpleGdbImpl3 extends SimpleGdb
{		
	private static final int GDB_COMPAT_VERSION = 3; //Preferred schema version
	
	private final SimpleGdb.LazyPst pstDatasources = new SimpleGdb.LazyPst(
			"SELECT codeRight FROM link GROUP BY codeRight"
		);
	private final SimpleGdb.LazyPst pstInfo = new SimpleGdb.LazyPst(
			"SELECT * FROM info"
		);
	private final SimpleGdb.LazyPst pstXrefExists = new SimpleGdb.LazyPst(
			"SELECT id FROM " + "datanode" + " WHERE " +
			"id = ? AND code = ?"
		);	
	private final SimpleGdb.LazyPst pstAttribute = new SimpleGdb.LazyPst(
			"SELECT attrvalue FROM attribute " +
			" WHERE id = ? AND code = ? AND attrname = ?"
		);
	private final SimpleGdb.LazyPst pstCrossRefs = new SimpleGdb.LazyPst (
			"SELECT dest.idRight, dest.codeRight FROM link AS src JOIN link AS dest " +
			"ON src.idLeft = dest.idLeft and src.codeLeft = dest.codeLeft " +
			"WHERE src.idRight = ? AND src.codeRight = ?"
		);
	private final SimpleGdb.LazyPst pstCrossRefsWithCode = new SimpleGdb.LazyPst (
			"SELECT dest.idRight, dest.codeRight FROM link AS src JOIN link AS dest " +
			"ON src.idLeft = dest.idLeft and src.codeLeft = dest.codeLeft " +
			"WHERE src.idRight = ? AND src.codeRight = ? AND dest.codeRight = ?"
		);
	private final SimpleGdb.LazyPst pstRefsByAttribute = new SimpleGdb.LazyPst (
			"SELECT datanode.id, datanode.code FROM datanode " +
			" LEFT JOIN attribute ON attribute.code = datanode.code AND attribute.id = datanode.id " +
			"WHERE attrName = ? AND attrValue = ?"
		);
	private final SimpleGdb.LazyPst pstFreeSearch = new SimpleGdb.LazyPst (
			"SELECT id, code FROM datanode WHERE " +
			"LOWER(ID) LIKE ?"
		);
	private final SimpleGdb.LazyPst pstAttributeSearch = new SimpleGdb.LazyPst (
			"SELECT id, code, attrvalue FROM attribute WHERE " +
			"attrname = 'Symbol' AND LOWER(attrvalue) LIKE ?"
		);
	
	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	public Set<Xref> mapID (Xref idc, Set<DataSource> resultDs) throws IDMapperException
	{
		Set<Xref> refs = new HashSet<Xref>();
		
		try
		{
			PreparedStatement pst;
			if (resultDs == null || resultDs.size() != 1)
			{
				pst = pstCrossRefs.getPreparedStatement();
			}
			else
			{
				pst = pstCrossRefsWithCode.getPreparedStatement();
				pst.setString(3, resultDs.iterator().next().getSystemCode());
			}
			
			pst.setString(1, idc.getId());
			pst.setString(2, idc.getDataSource().getSystemCode());
			
			ResultSet rs = pst.executeQuery();
			while (rs.next())
			{
				DataSource ds = DataSource.getBySystemCode(rs.getString(2));
				if (resultDs == null || resultDs.contains(ds))
				{
					refs.add (new Xref (rs.getString(1), ds));
				}
			}
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
		
		return refs;
	}

	/** {@inheritDoc} */
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
	 * Opens a connection to the Gene Database located in the given file.
	 * A new instance of this class is created automatically.
	 * @param dbName The file containing the Gene Database. 
	 * @param newDbConnector An instance of DBConnector, to determine the type of database (e.g. DataDerby).
	 * @param props PROP_RECREATE if you want to create a new database (possibly overwriting an existing one) 
	 * 	or PROP_NONE if you want to connect read-only
	 * @throws IDMapperException when the database could not be created or connected to
	 */
	public SimpleGdbImpl3(String dbName, DBConnector newDbConnector, int props) throws IDMapperException
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
		
		caps = new SimpleGdbCapabilities();
	}
	
	/**
	 * look at the info table of the current database to determine the schema version.
	 * @throws IDMapperException when looking up the schema version failed
	 */
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
		catch(SQLException e) 
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
					"     code VARCHAR(50)					" +
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
		catch (SQLException e)
		{
//			Logger.log.error("while creating gdb tables: " + e.getMessage(), e);
		}
	}

	
	public static final int NO_LIMIT = 0;
	public static final int NO_TIMEOUT = 0;
	public static final int QUERY_TIMEOUT = 20; //seconds

	/** {@inheritDoc} */
	public Set<Xref> freeSearch (String text, int limit) throws IDMapperException 
	{		
		Set<Xref> result = new HashSet<Xref>();
		try {
			PreparedStatement ps1 = pstFreeSearch.getPreparedStatement();
			ps1.setQueryTimeout(QUERY_TIMEOUT);
			if(limit > NO_LIMIT) 
			{
				ps1.setMaxRows(limit);
			}

			ps1.setString(1, "%" + text.toLowerCase() + "%");
			ResultSet r = ps1.executeQuery();
			while(r.next()) {
				String id = r.getString(1);
				DataSource ds = DataSource.getBySystemCode(r.getString(2));
				Xref ref = new Xref (id, ds);
				result.add (ref);
			}			
		} 
		catch (SQLException e) 
		{
			throw new IDMapperException(e);
		}
		return result;
	}
	
    private PreparedStatement pstGene = null;
    private PreparedStatement pstLink = null;
    private PreparedStatement pstAttr = null;

	/** {@inheritDoc} */
	public int addGene(Xref ref, String bpText) 
	{
		//TODO: bpText is unused
    	if (pstGene == null) throw new NullPointerException();
		try 
		{
			pstGene.setString(1, ref.getId());
			pstGene.setString(2, ref.getDataSource().getSystemCode());
			pstGene.executeUpdate();
		} 
		catch (SQLException e) 
		{ 
//			Logger.log.error("" + ref, e);
			return 1;
		}
		return 0;
    }
    
	/** {@inheritDoc} */
    public int addAttribute(Xref ref, String attr, String val)
    {
    	try {
    		pstAttr.setString(1, attr);
			pstAttr.setString(2, val);
			pstAttr.setString(3, ref.getId());
			pstAttr.setString(4, ref.getDataSource().getSystemCode());
			pstAttr.executeUpdate();
		} catch (SQLException e) {
//			Logger.log.error(attr + "\t" + val + "\t" + ref, e);
			return 1;
		}
		return 0;
    }

	/** {@inheritDoc} */
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
		catch (SQLException e)
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
	   @throws IDMapperException on failure
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
	   prepare for inserting genes and/or links.
	   @throws IDMapperException on failure
	 */
	public void preInsert() throws IDMapperException
	{
		try
		{
			con.setAutoCommit(false);
			pstGene = con.prepareStatement(
				"INSERT INTO datanode " +
				"	(id, code)" +
				"VALUES (?, ?)"
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

	/**
	 * Read the info table and return as properties.
	 * @return a map where keys are column names and values are the fields in the first row.
	 * @throws IDMapperException when the database became unavailable
	 */
	private Map<String, String> getInfo() throws IDMapperException
	{
		Map<String, String> result = new HashMap<String, String>();
		try
		{
			PreparedStatement pst = pstInfo.getPreparedStatement();
			ResultSet rs = pst.executeQuery();
			
			if (rs.next())
			{
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); ++i)
				{
					String key = rsmd.getColumnName(i);
					String val = rs.getString(i);
					result.put (key, val);
				}
			}
		}
		catch (SQLException ex)
		{
			throw new IDMapperException (ex);
		}
		
		return result;
	}
	
	/**
	 * @return a list of data sources present in this database. 
	   @throws IDMapperException when the database is unavailable
	 */
	private Set<DataSource> getDataSources() throws IDMapperException
	{
		Set<DataSource> result = new HashSet<DataSource>();
    	try
    	{
    	 	PreparedStatement pst = pstDatasources.getPreparedStatement();
    	 	ResultSet rs = pst.executeQuery();
    	 	while (rs.next())
    	 	{
    	 		DataSource ds = DataSource.getBySystemCode(rs.getString(1)); 
    	 		result.add (ds);
    	 	}
    	}
    	catch (SQLException ignore)
    	{
    		throw new IDMapperException(ignore);
    	}
    	return result;
	}
	
	private final IDMapperCapabilities caps;

	private class SimpleGdbCapabilities extends AbstractIDMapperCapabilities
	{
		/** default constructor.
		 * @throws IDMapperException when database is not available */
		public SimpleGdbCapabilities() throws IDMapperException 
		{
			super (SimpleGdbImpl3.this.getDataSources(), true, 
				SimpleGdbImpl3.this.getInfo());
		}
	}

	/**
	 * @return the capabilities of this gene database
	 */
	public IDMapperCapabilities getCapabilities() 
	{
		return caps;
	}

	/** {@inheritDoc} */
	public Set<String> getAttributes(Xref ref, String attrname)
			throws IDMapperException 
	{
		Set<String> result = new HashSet<String>();
		try {
			PreparedStatement pst = pstAttribute.getPreparedStatement();
			pst.setString (1, ref.getId());
			pst.setString (2, ref.getDataSource().getSystemCode());
			pst.setString (3, attrname);
			ResultSet r = pst.executeQuery();
			if (r.next())
			{
				result.add (r.getString(1));
			}
			return result;
		} catch	(SQLException e) { throw new IDMapperException (e); } // Database unavailable
	}

	/**
	 * free text search for matching symbols.
	 * @return references that match the query
	 * @param query The text to search for
	 * @param attrType the attribute to look for, e.g. 'Symbol' or 'Description'.
	 * @param limit The number of results to limit the search to
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Map<Xref, String> freeAttributeSearch (String query, String attrType, int limit) throws IDMapperException
	{
		Map<Xref, String> result = new HashMap<Xref, String>();
		try {

			PreparedStatement pst = pstAttributeSearch.getPreparedStatement();
			pst.setQueryTimeout(QUERY_TIMEOUT);
			if(limit > NO_LIMIT) pst.setMaxRows(limit);
			pst.setString(1, "%" + query.toLowerCase() + "%");
			ResultSet r = pst.executeQuery();

			while(r.next()) 
			{
				String id = r.getString("id");
				String code = r.getString("code");
				String symbol = r.getString("attrValue");
				result.put(new Xref (id, DataSource.getBySystemCode(code)), symbol);
			}
		} catch (SQLException e) {
			throw new IDMapperException (e);
		}
		return result;		
	}
}
