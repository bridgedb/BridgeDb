package org.bridgedb.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.Xref;
import org.bridgedb.linkset.LinkListener;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceException;
import org.bridgedb.provenance.ProvenanceFactory;
import org.bridgedb.provenance.SimpleProvenance;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
public class IDMapperSQL implements LinkListener, ProvenanceFactory{
    
    //Numbering should not clash with any GDB_COMPAT_VERSION;
	private static final int SQL_COMPAT_VERSION = 4;
    private static final int BLOCK_SIZE = 10000;
    
    private static final int SQL_TIMEOUT = 2;

    private Connection possibleOpenConnection;
    private PreparedStatement pstInsertLink = null;
    private PreparedStatement pstCheckLink = null;
    private SQLAccess sqlAccess;
    private int insertCount = 0;
    private int doubleCount = 0;
    
    public IDMapperSQL(SQLAccess sqlAccess) throws BridgeDbSqlException{
        if (sqlAccess == null){
            throw new IllegalArgumentException("sqlAccess can not be null");
        }
        this.sqlAccess = sqlAccess;
        checkVersion();
    }   

    private Statement createStatement() throws BridgeDbSqlException{
        try {
            if (possibleOpenConnection == null){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (possibleOpenConnection.isClosed()){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (!possibleOpenConnection.isValid(SQL_TIMEOUT)){
                possibleOpenConnection.close();
                possibleOpenConnection = sqlAccess.getConnection();
            }  
            return possibleOpenConnection.createStatement();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException ("Error creating a new statement");
        }
    }
    
    /**
     * Checks that the schema is for this version.
     * 
     * @throws BridgeDbSqlException If the schema version is not the expected one.
     */
	public void checkVersion() throws BridgeDbSqlException
	{
        Statement stmt = createStatement();
        ResultSet r = null;
        int version = 0;
        try {
            r = stmt.executeQuery("SELECT schemaversion FROM info");
            if(r.next()) version = r.getInt(1);
        } catch (SQLException e) {
            //probably new databse do nothing.
            version = SQL_COMPAT_VERSION;
        }
		finally
		{
            if (r != null) try { r.close(); } catch (SQLException ignore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ignore) {}
		}
 		switch (version)
		{
            case SQL_COMPAT_VERSION:
                return;
    		case 2:
        		throw new BridgeDbSqlException("Please use the SimpleGdbFactory in the org.bridgedb.rdb package");
            case 3:
                throw new BridgeDbSqlException("Please use the SimpleGdbFactory in the org.bridgedb.rdb package");
            //NB add future schema versions here
            default:
                throw new BridgeDbSqlException ("Unrecognized schema version '" + version + "', please make sure you have the latest " +
					"version of this software and databases");
		}		
	}

    /**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @throws IDMapperException 
	 */
	public void createSQLTables() throws BridgeDbSqlException
	{
		try 
		{
			Statement sh = createStatement();
 			sh.execute("CREATE TABLE                            "
                    + "IF NOT EXISTS                            "
					+ "info                                     " 
					+ "(    schemaversion INTEGER PRIMARY KEY	"
                    + ")");
			sh.execute( //Add compatibility version of GDB
					"INSERT INTO info VALUES ( " + SQL_COMPAT_VERSION + ")");
			sh.execute("CREATE TABLE                        "
                    + "IF NOT EXISTS                        "
                    + "link                                 " 
					+ " (   idLeft VARCHAR(50) NOT NULL,	" 
					+ "     codeLeft VARCHAR(100) NOT NULL,	" 
					+ "     idRight VARCHAR(50) NOT NULL,	" 
					+ "     codeRight VARCHAR(100) NOT NULL," 
					+ "     provenance INT,                 "  //Type still under review
					+ "     PRIMARY KEY (idLeft, codeLeft,  " 
					+ "		idRight, codeRight, provenance) 			" 
					+ " )									");
            //provenance table sitll under development.
			sh.execute(	"CREATE TABLE                           " 
                    + "IF NOT EXISTS                            "
					+ "		provenance                          " 
					+ " (   id INT AUTO_INCREMENT PRIMARY KEY,  " 
					+ "     creator VARCHAR(100),                "
                    + "     linkPredicate VARCHAR(50) NOT NULL, "
                    + "     dateCreated DATE NOT NULL,          "
                    + "     dateUploaded DATE NOT NULL         "
					+ " )                                       ");   
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}

    /**
	 * Excecutes several SQL statements to drop the tables 
	 * @throws IDMapperException 
	 */
	public void dropSQLTables() throws BridgeDbSqlException
	{
    	Statement sh = createStatement();
		try 
		{
 			sh.execute("DROP TABLE  "
                    + "IF EXISTS    "
					+ "info         ");
			sh.execute("DROP TABLE  "
                    + "IF EXISTS    "
                    + "link         ");
            //provenance table sitll under development.
			sh.execute(	"DROP TABLE " 
                    + "IF EXISTS    "
					+ "provenance   ");   
            sh.close();
		} catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error dropping the tables ", e);
		}
	}

    @Override
    public void init(Provenance provenance) throws BridgeDbSqlException {
		try
		{
            if (possibleOpenConnection == null){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (possibleOpenConnection.isClosed()){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (!possibleOpenConnection.isValid(SQL_TIMEOUT)){
                possibleOpenConnection.close();
                possibleOpenConnection = sqlAccess.getConnection();
            }  
  			possibleOpenConnection.setAutoCommit(false);
			pstInsertLink = possibleOpenConnection.prepareStatement("INSERT INTO link    "
                    + "(idLeft, codeLeft,                       "   
                    + " idRight, codeRight,                     "
                    + " provenance )                            " 
                    + "VALUES (?, ?, ?, ?, ?)                   ");
			pstCheckLink = possibleOpenConnection.prepareStatement("SELECT EXISTS "
                    + "(SELECT * FROM link      "
                    + "where                    "
                    + "   idLeft = ?            "
                    + "   AND codeLeft = ?      "   
                    + "   AND idRight = ?       "
                    + "   AND codeRight = ?     "
                    + "   AND provenance = ?)   ");
		}
		catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error creating prepared statements", e);
		}
        insertCount = 0;
        doubleCount = 0;
	}

/*    @Override
    public void insertLink(URI source, String predicate, URI target) throws BridgeDbSqlException {
        DataSource sourceDataSource = DataSource.getByNameSpace(source.getNamespace());
        String sourceCode = sourceDataSource.getSystemCode();
        DataSource targetDataSource = DataSource.getByNameSpace(target.getNamespace());
        String targetCode = targetDataSource.getSystemCode();
        try {
            pstInsertLink.setString(1, source.getLocalName());
            pstInsertLink.setString(2, sourceDataSource.getSystemCode());
            pstInsertLink.setString(3, target.getLocalName());
            pstInsertLink.setString(4, targetDataSource.getSystemCode());
            pstInsertLink.setInt(5, 0);
            pstInsertLink.executeUpdate();
            insertCount++;
            if (insertCount % BLOCK_SIZE == 0){
                System.out.println("Inserted " + insertCount + " links loaded so far");
                possibleOpenConnection.commit();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            throw new BridgeDbSqlException ("Error inserting link");
        }
    }
*/
    @Override
    public void insertLink(Xref source, Xref target) throws BridgeDbSqlException {
        boolean exists = false;
        try {
            pstCheckLink.setString(1, source.getId());
            pstCheckLink.setString(2, source.getDataSource().getSystemCode());
            pstCheckLink.setString(3, target.getId());
            pstCheckLink.setString(4, target.getDataSource().getSystemCode());
            pstCheckLink.setInt(5, 0);
            ResultSet rs = pstCheckLink.executeQuery();
            if (rs.next()) {
                exists = rs.getBoolean(1);
            }
            if (exists){
                doubleCount++;
                if (doubleCount % BLOCK_SIZE == 0){
                    System.out.println("Already skipped " + doubleCount + " links that already exist with this provenance");
                }
            } else {
                pstInsertLink.setString(1, source.getId());
                pstInsertLink.setString(2, source.getDataSource().getSystemCode());
                pstInsertLink.setString(3, target.getId());
                pstInsertLink.setString(4, target.getDataSource().getSystemCode());
                pstInsertLink.setInt(5, 0);
                pstInsertLink.executeUpdate();
                insertCount++;
                if (insertCount % BLOCK_SIZE == 0){
                    System.out.println("Inserted " + insertCount + " links loaded so far");
                    possibleOpenConnection.commit();
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            throw new BridgeDbSqlException ("Error inserting link");
        }
    }

    @Override
    public void closeInput() throws BridgeDbSqlException {
        System.out.println ("Inserted " + this.insertCount + " links");
        System.out.println ("Skipped " + this.doubleCount + " links that where already there");
        if (possibleOpenConnection != null){
            try {
                possibleOpenConnection.commit();
                possibleOpenConnection.close();
            } catch (SQLException ex) {
               throw new BridgeDbSqlException ("Error closing connection ", ex);
            }
        }
    }

    // **** ProvenanceFactory Methods ****
    @Override
    public Provenance createProvenance(String createdBy, String predicate, long creation, long upload) 
            throws ProvenanceException{
        Provenance result = findProvenanceNumber(createdBy, predicate, creation);
        if (result != null){
            return result;
        }
        createMissingProvenance(createdBy, predicate, creation, upload);
        return findProvenanceNumber(createdBy, predicate, creation);
    }
    
    @Override
    public Provenance createProvenance(String createdBy, String predicate, long creation) throws ProvenanceException {
        Provenance result = findProvenanceNumber(createdBy, predicate, creation);
        if (result != null){
            return result;
        }
        createMissingProvenance(createdBy, predicate, creation, new GregorianCalendar().getTimeInMillis());
        return findProvenanceNumber(createdBy, predicate, creation);
    }

    @Override
    public Provenance createProvenace(Provenance first, Provenance second) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Provenance findProvenanceNumber(String createdBy, String predicate, long creation) throws ProvenanceException{
        Statement statement;
        try {
            statement = this.createStatement();
        } catch (BridgeDbSqlException ex) {
            throw new ProvenanceException ("Unable to create the statement ", ex);
        }
        String query = "SELECT id, creator, linkPredicate, dateCreated, dateUploaded from provenance "
                + "where creator = \"" + createdBy + "\""
                + "  AND linkPredicate = \"" + predicate +"\"" 
                + "  AND dateCreated = \"" + new Date(creation).toString() + "\"";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return new SimpleProvenance(rs.getInt("id"), 
                                            rs.getString("creator"), 
                                            rs.getString("linkPredicate"), 
                                            rs.getDate("dateCreated").getTime(), 
                                            rs.getDate("dateUploaded").getTime());
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.err.println(query);
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to check if Provenance already exists ", ex);
        }
    }

    private void createMissingProvenance(String createdBy, String predicate, long creation, long uploaded) throws ProvenanceException {
        Statement statement;
        try {
            statement = this.createStatement();
        } catch (BridgeDbSqlException ex) {
            throw new ProvenanceException ("Unable to create the statement ", ex);
        }
        String update = "INSERT INTO provenance "
                + "(creator, linkPredicate, dateCreated, dateUploaded) "
                + "VALUES ( \"" + createdBy + "\", \"" + predicate + "\", \"" 
                + new Date(creation).toString() + "\", \"" + new Date(uploaded).toString() + "\")";
        try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            System.err.println(update);
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to check if Provenance already exists ", ex);
        }
    }
}
