package org.bridgedb.virtuoso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.linkset.URLLinkListener;
import org.bridgedb.ops.OpsMapper;
import org.bridgedb.ops.ProvenanceInfo;
import org.bridgedb.provenance.ProvenanceMapper;
import org.bridgedb.provenance.XrefProvenance;
import org.bridgedb.result.URLMapping;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLBase;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.ByNameSpaceIterable;
import org.bridgedb.url.URLIterator;
import org.bridgedb.url.URLMapper;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
// removed Iterators due to scale issues URLIterator, XrefIterator,
public class URLMapperVirtuoso extends SQLBase implements IDMapper, IDMapperCapabilities, URLLinkListener, URLMapper, ProvenanceMapper, 
        OpsMapper, URLIterator {
    
    private PreparedStatement pstLink;
    
    public URLMapperVirtuoso(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(sqlAccess);
     }   

    public URLMapperVirtuoso(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(dropTables, sqlAccess);
    }   
    
  	protected void dropTable(String name) throws BridgeDbSqlException{
        //"IF NOT EXISTS" is unsupported 
        String query = "select * from information_schema.tables where table_name='" + name + "'";
        Statement sh = createStatement();
        boolean found;
        try {
            ResultSet rs = sh.executeQuery(query);
            found = rs.next();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to check provenace " +  query, ex);
        }
        if (found) {
            try 
            {
                sh.execute("DROP TABLE " + name);
                sh.close();
            } catch (SQLException e) {
                throw new BridgeDbSqlException ("Error dropping table " + name, e);
            }
        } 
    }

    protected Statement createAStatement() throws SQLException{
        if (possibleOpenConnection == null){
            possibleOpenConnection = sqlAccess.getAConnection();
        } else if (possibleOpenConnection.isClosed()){
            possibleOpenConnection = sqlAccess.getAConnection();
        }  
        return possibleOpenConnection.createStatement();
    }

    /**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @throws IDMapperException 
	 */
	protected void createSQLTables() throws BridgeDbSqlException
	{
        //"IF NOT EXISTS " is not supported
		try 
		{
			Statement sh = createStatement();
 			sh.execute("CREATE TABLE                            "
					+ "info                                     " 
					+ "(    schemaversion INTEGER PRIMARY KEY	"
                    + ")");
  			sh.execute( //Add compatibility version of GDB
					"INSERT INTO info VALUES ( " + SQL_COMPAT_VERSION + ")");
            //TODO add organism as required
            sh.execute("CREATE TABLE  "
                    + "     DataSource "
                    + "  (  sysCode VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,   "
                    + "     isPrimary SMALLINT,                                  "
                    + "     fullName VARCHAR(" + FULLNAME_LENGTH + "),      "
                    + "     mainUrl VARCHAR(" + MAINURL_LENGTH + "),        "
                    + "     urlPattern VARCHAR(" + URLPATTERN_LENGTH + "),  "
                    + "     idExample VARCHAR(" + ID_LENGTH + "),           "
                    + "     type VARCHAR(" + TYPE_LENGTH + "),              "
                    + "     urnBase VARCHAR(" + URNBASE_LENGTH + ")         "
                    + "  ) ");
 			sh.execute("CREATE TABLE                                                    "
                    + "link                                                             " 
                            //As most search are on full url full url is stored in one column
					+ " (   id INT IDENTITY PRIMARY KEY,                          " 
					+ "     sourceURL VARCHAR(150) NOT NULL,                            "
                            //Again a speed for space choice.
					+ "     targetURL VARCHAR(150) NOT NULL,                            " 
					+ "     provenance_id VARCHAR(" + PROVENANCE_ID_LENGTH + ")         "
					+ " )									                            ");
            sh.execute("CREATE INDEX sourceFind ON link (sourceURL) ");
            sh.execute("CREATE INDEX sourceProvenaceFind ON link (sourceURL, provenance_id) ");
         	sh.execute(	"CREATE TABLE                                                       "    
					+ "		provenance                                                      " 
					+ " (   id VARCHAR(" + PROVENANCE_ID_LENGTH + ") PRIMARY KEY,           " 
                    + "     sourceNameSpace VARCHAR(" + NAME_SPACE_LENGTH + ") NOT NULL,    "
                    + "     linkPredicate VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL,       "
                    + "     targetNameSpace VARCHAR(" + NAME_SPACE_LENGTH + ")  NOT NULL,    "
                    + "     linkCount INT                                                   "
					+ " ) "); 
            sh.execute ("CREATE TABLE  "
                    + "    properties "
                    + "(   thekey      VARCHAR(" + KEY_LENGTH + ") NOT NULL, "
                    + "    property    VARCHAR(" + PROPERTY_LENGTH + ") NOT NULL, "
                    + "    isPublic    SMALLINT "
					+ " ) "); 
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}
     
    @Override
    public void insertLink(String source, String target, String forwardProvenanceId, String inverseProvenanceId)
            throws IDMapperException {
        try {
            pstLink.setString(1, source);
			pstLink.setString(2, target);
			pstLink.setString(3, forwardProvenanceId);
			pstLink.executeUpdate();
        } catch (SQLException ex) {
			throw new BridgeDbSqlException ("Error inserting forward link ", ex);
        }
        try {
            pstLink.setString(1, target);
			pstLink.setString(2, source);
			pstLink.setString(3, inverseProvenanceId);
			pstLink.executeUpdate();
        } catch (SQLException ex) {
			throw new BridgeDbSqlException ("Error inserting inverse link ", ex);
        }
    }
    
    @Override
    public void openInput() throws BridgeDbSqlException {
        try {
            pstLink = sqlAccess.getAConnection().prepareStatement(
                	"INSERT INTO link (sourceURL, targetURL, provenance_id) VALUES  (?, ?, ?)");
        } catch (SQLException ex) {
			throw new BridgeDbSqlException ("Error preparing inserting link statement", ex);
        }
 	}

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        String query = "SELECT TOP 1 * FROM provenance "
                + "WHERE sourceNameSpace = '" + src.getNameSpace() + "'"
                + "AND targetNameSpace = '" + tgt.getNameSpace() + "'";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return (rs.next());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }
    
    @Override
    public boolean uriExists(String URL) throws IDMapperException {
        if (URL == null) return false;
        if (URL.isEmpty()) return false;
        String query = "SELECT TOP 1 * FROM link      "
                + "where                    "
                + "       sourceURL = '" + URL + "'";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
        return false;
        /* No need for target as all links bi directional 
        String query2 = "SELECT * FROM link      "
                + "where                    "
                + "       targetURL = '" + URL + "'"   
                + "LIMIT 1";
        statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query2);
            return (rs.next());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query2, ex);
        }*/
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws IDMapperException {
        //Try source using index so no joker at the front
        String query1 = "SELECT sourceURL as url  "
                + "FROM link      "
                + "where                    "
                + "   sourceURL LIKE '" + text + "' ";
        Statement statement = this.createStatement();
        Set<String> foundSoFar;
        try {
            ResultSet rs = statement.executeQuery(query1);
            foundSoFar = resultSetToURLSet(rs);
            if (foundSoFar.size() >= limit){
                return trimSet(foundSoFar, limit);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query1, ex);
        }
        //Try source without using index so joker at the front
        String query2 = "SELECT sourceURL as url  "
                + "FROM link      "
                + "where                    "
                + "   sourceURL LIKE '%" + text + "' ";
        try {
            ResultSet rs = statement.executeQuery(query2);
            foundSoFar.addAll(resultSetToURLSet(rs));
            if (foundSoFar.size() >= limit){
                return trimSet(foundSoFar, limit);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query2, ex);
        }
        return trimSet(foundSoFar, limit);
        /* No need as all links are loaded both ways
         * Try target without using index so joker at the front
        String query3 = "SELECT distinct targetURL as url  "
                + "FROM link      "
                + "where                    "
                + "   targetURL LIKE '%" + text + "' "
                + "LIMIT " + limit;
        try {
            ResultSet rs = statement.executeQuery(query3);
            foundSoFar.addAll(resultSetToURLSet(rs));
            return foundSoFar;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query3, ex);
        }*/
    }

    private Set<String> trimSet(Set<String> original, int limit){
        if (original.size() <= limit) {
            return original;
        }
        Set<String> smaller = new HashSet<String>();
        Iterator<String> iterator = original.iterator();
        for (int i = 0; i< limit; i++){
            smaller.add(iterator.next());
        }
        return smaller;
    }
}