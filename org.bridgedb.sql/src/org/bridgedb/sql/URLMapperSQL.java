package org.bridgedb.sql;

import org.bridgedb.ws.ByPossitionIterator;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.linkset.LinkListener;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceException;
import org.bridgedb.provenance.ProvenanceFactory;
import org.bridgedb.provenance.SimpleProvenance;
import org.bridgedb.ws.XrefByPossition;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
public class URLMapperSQL extends CommonSQL implements IDMapper, IDMapperCapabilities, LinkListener, ProvenanceFactory, 
        XrefIterator, XrefByPossition{
    
    //Numbering should not clash with any GDB_COMPAT_VERSION;
	private static final int SQL_COMPAT_VERSION = 5;
    
    private PreparedStatement pstInsertLink = null;
    private PreparedStatement pstCheckLink = null;

    public URLMapperSQL(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(sqlAccess);
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
	public void createSQLTables() throws BridgeDbSqlException
	{
        super.createSQLTables();
		try 
		{
			Statement sh = createStatement();
			sh.execute( //Add compatibility version of GDB
					"INSERT INTO info VALUES ( " + SQL_COMPAT_VERSION + ")");
			sh.execute("CREATE TABLE                                                    "
                    + "IF NOT EXISTS                                                    "
                    + "link                                                             " 
                            //As most search are on full url full url is stored in one column
					+ " (   sourceURL VARCHAR(150) NOT NULL,                            "
                            //for functions that require the sourceNameSpace/ DataSource
                    + "     sourceNameSpace VARCHAR(100) NOT NULL,                      "
                            //Again a speed for space choice.
					+ "     targetURL VARCHAR(150) NOT NULL,                               " 
                            //Targets are oftne filtered on NameSpace
					+ "     targetNameSpace VARCHAR(100) NOT NULL,                         "        
					+ "     provenance INT,                                             "  //Type still under review
					+ "     PRIMARY KEY (sourceURL, targetURL, provenance)                 " 
					+ " )									                            ");
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}

    @Override
    public void init(Provenance provenance) throws BridgeDbSqlException {
		try
		{
			pstInsertLink = possibleOpenConnection.prepareStatement("INSERT INTO link    "
                    + "(sourceURL, sourceNameSpace,                       "   
                    + " targetURL, targetNameSpace,                     "
                    + " provenance )                            " 
                    + "VALUES (?, ?, ?, ?,                      " 
                    + provenance.getId() + ")                   ");
			pstCheckLink = possibleOpenConnection.prepareStatement("SELECT EXISTS "
                    + "(SELECT * FROM link      "
                    + "where                    "
                    + "   sourceURL = ?            "
                    + "   AND targetURL = ?      "   
                    + "   AND provenance = " + provenance.getId() + ")");
		}
		catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error creating prepared statements", e);
		}
 	}

    @Override
    public void insertLink(Xref source, Xref target) throws BridgeDbSqlException {
        boolean exists = false;
        checkXrefValidToLoadInURLDataBase(source);
        checkXrefValidToLoadInURLDataBase(target);
        try {
            pstCheckLink.setString(1, source.getUrl());
            pstCheckLink.setString(2, target.getUrl());
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
                pstInsertLink.setString(1, source.getUrl());
                pstInsertLink.setString(2, source.getDataSource().getNameSpace());
                pstInsertLink.setString(3, target.getUrl());
                pstInsertLink.setString(4, target.getDataSource().getNameSpace());
                pstInsertLink.executeUpdate();
                insertCount++;
                if (insertCount % BLOCK_SIZE == 0){
                    System.out.println("Inserted " + insertCount + " links loaded so far");
                    possibleOpenConnection.commit();
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            throw new BridgeDbSqlException ("Error inserting link ", ex);
        }
    }

    private void checkXrefValidToLoadInURLDataBase(Xref xref) throws BridgeDbSqlException{
        if (xref.getId() == null) throw new BridgeDbSqlException("The id may not be null");
        DataSource ds = xref.getDataSource();
        if (ds == null) throw new BridgeDbSqlException("The DataSource may not be null");
        if (ds.getNameSpace() == null) throw new BridgeDbSqlException("The DataSource's namespace may not be null");
        if (ds.getNameSpace().isEmpty()) throw new BridgeDbSqlException("The DataSource's namespace may not be empty");
        if (!ds.getUrl("$id").equals(ds.getNameSpace() + "$id")) {
            throw new BridgeDbSqlException("The DataSource's urlPattern was " + ds.getUrl("$id") + " "
                    + "However this implemenation does not allow postfixes (the part after the \"$id\"");
        }
    }
    
    //***** IDMapper funtctions  *****
    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        if (ref.getId() == null || ref.getDataSource() == null){
            return new HashSet<Xref>();
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT targetURL as url  ");
        query.append("FROM link      ");
        query.append("where                    ");
        query.append("   sourceURL = \"");
            query.append(ref.getUrl());
            query.append("\"");
        if (tgtDataSources.length > 0){    
            query.append("   AND ( "); 
            query.append("      targetNameSpace = \"");
                query.append(tgtDataSources[0].getNameSpace());
                query.append("\" ");
            for (int i = 1; i < tgtDataSources.length; i++){
                query.append("      OR   ");     
                query.append("      targetNameSpace = \"");
                    query.append(tgtDataSources[i].getNameSpace());
                    query.append("\"  ");
            }
            query.append("   )");
        }
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(query);
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    private Set<Xref> resultSetToXrefSet(ResultSet rs ) throws IDMapperException{
        HashSet<Xref> results = new HashSet<Xref>();
        try {
            while (rs.next()){
                String url = rs.getString("url");
                Xref xref = DataSource.uriToXref(url);
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
                + "       sourceURL = \"" + xref.getUrl() + "\"" 
                + "   OR "
                + "       targetURL = \"" + xref.getUrl() + "\""   
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
        String query = "SELECT distinct sourceURL as url  "
                + "FROM link      "
                + "where                    "
                + "   sourceURL LIKE \"%" + text + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "where                    "
                + "   targetURL LIKE \"%" + text + "\" ";
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
        String query = "SELECT DISTINCT sourceNameSpace as nameSpace "
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
                String nameSpace = rs.getString("nameSpace");
                DataSource ds = DataSource.getByNameSpace(nameSpace);
                results.add(ds);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT targetNameSpace as nameSpace "
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
                + "   sourceNameSpace = \"" + src.getNameSpace() + "\""
                + "   AND targetNameSpace = \"" + tgt.getNameSpace() + "\""   
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
        String query = "SELECT distinct sourceURL as url "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
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
        String query = "SELECT distinct  sourceURL as url "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
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
        String query = "SELECT distinct sourceURL as url "
                + "FROM link      "
                + "WHERE "
                + "sourceURL = \"" + ds.getNameSpace() + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "WHERE "
                + "targetUrl = \"" + ds.getNameSpace() + "\" "
                + "LIMIT " + possition + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<Xref> results = new HashSet<Xref>();
            while (rs.next()){
                String url = rs.getString("url");
                results.add(DataSource.uriToXref(url));
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
        String query = "SELECT distinct sourceURL as url "
                + "FROM link      "
                + "WHERE "
                + "sourceURL = \"" + ds.getNameSpace() + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "WHERE "
                + "targetUrl = \"" + ds.getNameSpace() + "\" "
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
