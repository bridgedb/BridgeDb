package org.bridgedb.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
public class IDMapperSQL extends CommonSQL {
    
    //Numbering should not clash with any GDB_COMPAT_VERSION;
	private static final int SQL_COMPAT_VERSION = 4;
         
    private PreparedStatement pstInsertLink = null;
    private PreparedStatement pstCheckLink = null;

    public IDMapperSQL(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(sqlAccess);
    }   

    public IDMapperSQL(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(dropTables, sqlAccess);
    }   

    @Override
    boolean correctVersion(int currentVersion) {
        return currentVersion == SQL_COMPAT_VERSION;
    }
    
    /**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @throws IDMapperException 
	 */
	void createSQLTables() throws BridgeDbSqlException
	{
        super.createSQLTables();
		try 
		{
			Statement sh = createStatement();
			sh.execute( //Add compatibility version of GDB
					"INSERT INTO info VALUES ( " + SQL_COMPAT_VERSION + ")");
			sh.execute("CREATE TABLE                        "
                    + "IF NOT EXISTS                        "
                    + "link                                 " 
					+ " (   idLeft VARCHAR(50) NOT NULL,	" 
					+ "     codeLeft VARCHAR(100) NOT NULL,	" 
					+ "     idRight VARCHAR(50) NOT NULL,	" 
					+ "     codeRight VARCHAR(100) NOT NULL," 
					+ "     PRIMARY KEY (idLeft, codeLeft,  " 
					+ "		idRight, codeRight) 			" 
					+ " )									");
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}

    @Override
    public void openInput() throws BridgeDbSqlException {
        super.openInput();
		try
		{
			pstInsertLink = possibleOpenConnection.prepareStatement("INSERT INTO link    "
                    + "(idLeft, codeLeft,                       "   
                    + " idRight, codeRight)                     " 
                    + "VALUES (?, ?, ?, ?)                      ");
			pstCheckLink = possibleOpenConnection.prepareStatement("SELECT EXISTS "
                    + "(SELECT * FROM link      "
                    + "where                    "
                    + "   idLeft = ?            "
                    + "   AND codeLeft = ?      "   
                    + "   AND idRight = ?       "
                    + "   AND codeRight = ?    )");
		}
		catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error creating prepared statements", e);
		}
	}

    @Override
    public void insertLink(Xref source, Xref target) throws BridgeDbSqlException {
        checkDataSourceInDatabase(source.getDataSource());
        checkDataSourceInDatabase(target.getDataSource());
        boolean exists = false;
        try {
            pstCheckLink.setString(1, source.getId());
            pstCheckLink.setString(2, source.getDataSource().getSystemCode());
            pstCheckLink.setString(3, target.getId());
            pstCheckLink.setString(4, target.getDataSource().getSystemCode());
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

    //***** IDMapper funtctions  *****
    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        if (ref.getId() == null || ref.getDataSource() == null){
            return new HashSet<Xref>();
        }
        if (tgtDataSources.length == 0){
            return mapID(ref);
        }
        String query = "SELECT idRight as id, codeRight as code "
                + "FROM link      "
                + "where                    "
                + "   idLeft = \"" + ref.getId() + "\""
                + "   AND codeLeft = \"" + ref.getDataSource().getSystemCode() + "\""
                + "   AND (" 
                + "      codeRight = \"" + tgtDataSources[0].getSystemCode() + "\"";
        for (int i = 1; i < tgtDataSources.length; i++){
            query+= "      OR "
                   + "      codeRight = \"" + tgtDataSources[i].getSystemCode() + "\"";
        }
        query+= "   )";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    private Set<Xref> mapID(Xref ref) throws IDMapperException {
        String query = "SELECT distinct idRight as id, codeRight as code  "
                + "FROM link      "
                + "where                    "
                + "   idLeft = \"" + ref.getId() + "\""
                + "   AND codeLeft = \"" + ref.getDataSource().getSystemCode() + "\"";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    private Set<Xref> resultSetToXrefSet(ResultSet rs ) throws IDMapperException{
        HashSet<Xref> results = new HashSet<Xref>();
        try {
            while (rs.next()){
                String id = rs.getString("id");
                String sysCode = rs.getString("code");
                DataSource ds = DataSource.getBySystemCode(sysCode);
                Xref xref = new Xref(id, ds);
                results.add(xref);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }
    
    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        if (xref.getId() == null || xref.getDataSource() == null){
            return false;
        }
        String query = "SELECT EXISTS "
                + "(SELECT * FROM link      "
                + "where                    "
                + "   idLeft = \"" + xref.getId() + "\""
                + "   AND codeLeft = \"" + xref.getDataSource().getSystemCode() + "\""   
                + ")";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        String query = "SELECT distinct idRight as id, codeRight as code  "
                + "FROM link      "
                + "where                    "
                + "   idRight = \"" + text + "\" "
                + "UNION "
                + "SELECT distinct idLeft as id, codeLeft as code  "
                + "FROM link      "
                + "where                    "
                + "   idLeft = \"" + text + "\" "
                + "LIMIT " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }
    
    //***** IDMapperCapabilities funtctions  *****
    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT codeLeft as code "
                + "FROM link      ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToDataSourceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    private Set<DataSource> resultSetToDataSourceSet(ResultSet rs ) throws IDMapperException{
        HashSet<DataSource> results = new HashSet<DataSource>();
        try {
            while (rs.next()){
                String sysCode = rs.getString("code");
                DataSource ds = DataSource.getBySystemCode(sysCode);
                results.add(ds);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT codeRight as code "
                + "FROM link      ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToDataSourceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        String query = "SELECT EXISTS "
                + "(SELECT * FROM link      "
                + "where                    "
                + "   codeLeft = \"" + src.getSystemCode() + "\""
                + "   AND codeRight = \"" + tgt.getSystemCode() + "\""   
                + ")";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    //*** Support method for iteration ****
    /**
     * Gets the Xref currently at this possition in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each possition can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param possition
     * @return
     * @throws IDMapperException 
     */
    public Set<Xref> getXrefByPossition(int possition, int limit) throws IDMapperException {
        String query = "SELECT distinct idRight as id, codeRight as code  "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct idLeft as id, codeLeft as code  "
                + "FROM link      "
                + "LIMIT " + possition + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<Xref> results = new HashSet<Xref>();
            while (rs.next()){
                String id = rs.getString("id");
                String sysCode = rs.getString("code");
                DataSource ds = DataSource.getBySystemCode(sysCode);
                results.add(new Xref(id, ds));
            } 
            return results;
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    //*** Support method for interation ****
    /**
     * Gets the Xref currently at this possition in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each possition can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param possition
     * @return
     * @throws IDMapperException 
     */
    public Xref getXrefByPossition(int possition) throws IDMapperException {
        String query = "SELECT distinct idRight as id, codeRight as code  "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct idLeft as id, codeLeft as code  "
                + "FROM link      "
                + "LIMIT " + possition + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                String id = rs.getString("id");
                String sysCode = rs.getString("code");
                DataSource ds = DataSource.getBySystemCode(sysCode);
                return new Xref(id, ds);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    /**
     * Gets the Xref currently at this possition in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each possition can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param possition
     * @return
     * @throws IDMapperException 
     */
    public Set<Xref> getXrefByPossition(DataSource ds, int possition, int limit) throws IDMapperException {
        String query = "SELECT distinct idRight as id "
                + "FROM link      "
                + "WHERE "
                + "codeRight = \"" + ds.getSystemCode() + "\" "
                + "UNION "
                + "SELECT distinct idLeft as id  "
                + "FROM link      "
                + "WHERE "
                + "codeLeft = \"" + ds.getSystemCode() + "\" "
                + "LIMIT " + possition + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<Xref> results = new HashSet<Xref>();
            while (rs.next()){
                String id = rs.getString("id");
                results.add(new Xref(id, ds));
            } 
            return results;
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    /**
     * Gets the Xref currently at this possition in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each possition can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param possition
     * @return
     * @throws IDMapperException 
     */
    public Xref getXrefByPossition(DataSource ds, int possition) throws IDMapperException {
        String query = "SELECT distinct idRight as id "
                + "FROM link      "
                + "WHERE "
                + "codeRight = \"" + ds.getSystemCode() + "\" "
                + "UNION "
                + "SELECT distinct idLeft as id  "
                + "FROM link      "
                + "WHERE "
                + "codeLeft = \"" + ds.getSystemCode() + "\" "
                + "LIMIT " + possition + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                String id = rs.getString("id");
                return new Xref(id, ds);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

}
