package org.bridgedb.rdb.construct;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

public class GdbConstructImpl3 implements GdbConstruct
{
	private static int GDB_COMPAT_VERSION = 3;

	private final Connection con; //TODO: initialize
	private final DBConnector dbConnector; //TODO
	private String dbName; //TODO
	private Exception recentException = null;
	
	public GdbConstructImpl3(String dbName, DBConnector dbConnector, int props) throws IDMapperException
	{
		con = dbConnector.createConnection(dbName, props);
		this.dbConnector = dbConnector;
		this.dbName = dbName;
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
		recentException = null;
	}

    private PreparedStatement pstGene = null;
    private PreparedStatement pstLink = null;
    private PreparedStatement pstAttr = null;

	/** {@inheritDoc} */
	public int addGene(Xref ref)
	{
		recentException = null;
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
			recentException = e;
			return 1;
		}
		return 0;
    }
    
	/** {@inheritDoc} */
    public int addAttribute(Xref ref, String attr, String val)
    {
    	recentException = null;
    	try {
    		pstAttr.setString(1, attr);
			pstAttr.setString(2, val);
			pstAttr.setString(3, ref.getId());
			pstAttr.setString(4, ref.getDataSource().getSystemCode());
			pstAttr.executeUpdate();
		} catch (SQLException e) {
			recentException = e;
			return 1;
		}
		return 0;
    }

	/** {@inheritDoc} */
    public int addLink(Xref left, Xref right) 
    {
    	if (pstLink == null) throw new NullPointerException();
    	recentException = null;
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
    		recentException = e;
			return 1;
		}
		return 0;
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

	public void setInfo(String key, String value) throws IDMapperException
	{
		try
		{
			/** 
			 * This is a bit awkward because we store keys as columns.
			 * TODO: in a future schema version this should be a regular 2-column table.
			 */
			if (!key.matches("^\\w+$")) throw new IllegalArgumentException("key: '" + key + "' contains invalid characters");
			PreparedStatement pstInfo1 = con.prepareStatement (
					"ALTER TABLE info " +
					"ADD COLUMN " + key + " VARCHAR (50)"
		 		);
			pstInfo1.execute();
			PreparedStatement pstInfo2 = con.prepareStatement (
						"UPDATE info SET " + key + " = ? " +
						"WHERE schemaversion = " + GDB_COMPAT_VERSION
			 		);
			pstInfo2.setString(1, value);
			pstInfo2.execute();
		}
		catch (SQLException ex)
		{
			throw new IDMapperException(ex);
		}
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
	 * Executes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * Note: Official GDB's are created by AP, not with this code.
	 * This is just here for testing purposes.
	 * @throws IDMapperException 
	 */
	public void createGdbTables() throws IDMapperException 
	{
//		Logger.log.info("Info:  Creating tables");
		try 
		{
			Statement sh = con.createStatement();
//			sh.execute("DROP TABLE info");
//			sh.execute("DROP TABLE link");
//			sh.execute("DROP TABLE datanode");
//			sh.execute("DROP TABLE attribute");
//			sh.close();
			
//			sh = con.createStatement();
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
			throw new IDMapperException (e);
		}
	}


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
	 * Older method to open a connection to a Gene database
	 * using a DBConnector to handle differences
	 * between different RDBMS-es. The other createInstance() is preferred.
	 * <p>
	 * Use this instead of constructor to create an instance of SimpleGdb that matches the schema version.
	 * @param dbName The file containing the Gene Database. 
	 * @param newDbConnector handles the differences between types of RDBMS.
	 * A new instance of DbConnector class is instantiated automatically.
	 * @param props PROP_RECREATE if you want to create a new database, overwriting any existing ones. Otherwise, PROP_NONE.
	 * @return a new Gdb
	 * @throws IDMapperException on failure
	*/
	public static GdbConstruct createInstance(String dbName, DBConnector newDbConnector, int props) throws IDMapperException
	{
		try
		{
			// create a fresh db connector of the correct type.
			DBConnector dbConnector = newDbConnector.getClass().newInstance();
			return new GdbConstructImpl3(dbName, dbConnector, props);
		}
		catch (InstantiationException e)
		{
			throw new IDMapperException (e);
		} 
		catch (IllegalAccessException e) 
		{
			throw new IDMapperException (e);
		}		
	}

	@Override
	public Exception recentException() {
		return recentException;
	}

}
