package org.bridgedb.mysql;

import java.sql.Connection;
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
public class URLMapperSQL extends SQLBase implements IDMapper, IDMapperCapabilities, URLLinkListener, URLMapper, ProvenanceMapper, 
        OpsMapper, URLIterator {
    
    private static final int SQL_TIMEOUT = 2;
    StringBuilder insertQuery;

    public URLMapperSQL(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(sqlAccess);
     }   

    public URLMapperSQL(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(dropTables, sqlAccess);
    }   

	protected void dropTable(String name) throws BridgeDbSqlException
	{
    	Statement sh = createStatement();
		try 
		{
 			sh.execute("DROP TABLE  "
                    + "IF EXISTS    "
					+ name);
            sh.close();
		} catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error dropping table " + name, e);
		}
	}

    protected Statement createAStatement() throws SQLException{
        if (possibleOpenConnection == null){
            possibleOpenConnection = sqlAccess.getAConnection();
        } else if (possibleOpenConnection.isClosed()){
            possibleOpenConnection = sqlAccess.getAConnection();
        } else if (!possibleOpenConnection.isValid(SQL_TIMEOUT)){
            possibleOpenConnection.close();
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
            //TODO add organism as required
            sh.execute("CREATE TABLE  "
                    + "IF NOT EXISTS  "
                    + "     DataSource "
                    + "  (  sysCode VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,   "
                    + "     isPrimary BOOLEAN,                              "
                    + "     fullName VARCHAR(" + FULLNAME_LENGTH + "),      "
                    + "     mainUrl VARCHAR(" + MAINURL_LENGTH + "),        "
                    + "     urlPattern VARCHAR(" + URLPATTERN_LENGTH + "),  "
                    + "     idExample VARCHAR(" + ID_LENGTH + "),           "
                    + "     type VARCHAR(" + TYPE_LENGTH + "),              "
                    + "     urnBase VARCHAR(" + URNBASE_LENGTH + ")         "
                    + "  ) ");
 			sh.execute("CREATE TABLE                                                    "
                    + "IF NOT EXISTS                                                    "
                    + "link                                                             " 
                            //As most search are on full url full url is stored in one column
					+ " (   id INT AUTO_INCREMENT PRIMARY KEY,                          " 
					+ "     sourceURL VARCHAR(150) NOT NULL,                            "
                            //Again a speed for space choice.
					+ "     targetURL VARCHAR(150) NOT NULL,                            " 
					+ "     provenance_id VARCHAR(" + PROVENANCE_ID_LENGTH + ")         "
					+ " )									                            ");
            sh.execute("CREATE INDEX sourceFind ON link (sourceURL) ");
            sh.execute("CREATE INDEX sourceProvenaceFind ON link (sourceURL, provenance_id) ");
         	sh.execute(	"CREATE TABLE                                                       "    
                    + "IF NOT EXISTS                                                        "
					+ "		provenance                                                      " 
					+ " (   id VARCHAR(" + PROVENANCE_ID_LENGTH + ") PRIMARY KEY,           " 
                    + "     sourceNameSpace VARCHAR(" + NAME_SPACE_LENGTH + ") NOT NULL,    "
                    + "     linkPredicate VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL,       "
                    + "     targetNameSpace VARCHAR(" + NAME_SPACE_LENGTH + ")  NOT NULL,    "
                    + "     linkCount INT                                                   "
					+ " ) "); 
            sh.execute ("CREATE TABLE  "
                    + "IF NOT EXISTS "
                    + "    properties "
                    + "(   thekey      VARCHAR(" + KEY_LENGTH + ") NOT NULL, "
                    + "    property    VARCHAR(" + PROPERTY_LENGTH + ") NOT NULL, "
                    + "    isPublic      BOOLEAN "
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
        if (blockCount >= BLOCK_SIZE){
            runInsert();
            insertQuery = new StringBuilder("INSERT INTO link (sourceURL, targetURL, provenance_id) VALUES ");
        } else {
            insertQuery.append(", ");        
        }
        blockCount++;
        insertQuery.append("('");
        insertQuery.append(source);
        insertQuery.append("', '");
        insertQuery.append(target);
        insertQuery.append("', '");
        insertQuery.append(forwardProvenanceId);
        insertQuery.append("'),");
        blockCount++;
        insertQuery.append("('");
        insertQuery.append(target);
        insertQuery.append("', '");
        insertQuery.append(source);
        insertQuery.append("', '");
        insertQuery.append(inverseProvenanceId);
        insertQuery.append("')");
    }

    private void runInsert() throws BridgeDbSqlException{
        if (insertQuery != null) {
           try {
                Statement statement = createStatement();
                long start = new Date().getTime();
                int changed = statement.executeUpdate(insertQuery.toString());
                Reporter.report("insertTook " + (new Date().getTime() - start));
                insertCount += changed;
                doubleCount += blockCount - changed;
                Reporter.report("Inserted " + insertCount + " links and ingnored " + doubleCount + " so far");
            } catch (SQLException ex) {
                System.err.println(ex);
                throw new BridgeDbSqlException ("Error inserting link ", ex, insertQuery.toString());
            }
        }   
        insertQuery = null;
        blockCount = 0;
    }
    
    @Override
    public void closeInput() throws IDMapperException {
        runInsert();
        super.closeInput();
    }

    @Override
    public void openInput() throws BridgeDbSqlException {
        //Starting with a block will cause a new query to start.
        blockCount = BLOCK_SIZE ;
        insertCount = 0;
        doubleCount = 0;    
 	}

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        String query = "SELECT * FROM provenance "
                + "WHERE sourceNameSpace = '" + src.getNameSpace() + "'"
                + "AND targetNameSpace = '" + tgt.getNameSpace() + "'"
                + "LIMIT 1";
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
        String query1 = "SELECT * FROM link      "
                + "where                    "
                + "       sourceURL = '" + URL + "'" 
                + "LIMIT 1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query1);
            if (rs.next()) return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query1, ex);
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
        String query1 = "SELECT distinct sourceURL as url  "
                + "FROM link      "
                + "where                    "
                + "   sourceURL LIKE '" + text + "' "
                + "LIMIT " + limit;
        Statement statement = this.createStatement();
        Set<String> foundSoFar;
        try {
            ResultSet rs = statement.executeQuery(query1);
            foundSoFar = resultSetToURLSet(rs);
            if (foundSoFar.size() == limit){
                return foundSoFar;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query1, ex);
        }
        //Try source without using index so joker at the front
        String query2 = "SELECT distinct sourceURL as url  "
                + "FROM link      "
                + "where                    "
                + "   sourceURL LIKE '%" + text + "' "
                + "LIMIT " + limit;
        try {
            ResultSet rs = statement.executeQuery(query2);
            foundSoFar.addAll(resultSetToURLSet(rs));
            if (foundSoFar.size() == limit){
                return foundSoFar;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query2, ex);
        }
        return foundSoFar;
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

    protected void appendMySQLLimitConditions(StringBuilder query, Integer position, Integer limit){
        if (position == null) {
            position = 0;
        }
        if (limit == null){
            limit = DEFAULT_LIMIT;
        }
        query.append("LIMIT " + position + ", " + limit);       
    }

    @Override
    protected void appendVirtuosoTopConditions(StringBuilder query, Integer position, Integer limit) {
        //do nothing at all!
    }

    @Override
    public List<String> getSampleSourceURLs() throws IDMapperException {
        String query = "SELECT sourceURL as url FROM link LIMIT 5";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLList(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }        
    }


}