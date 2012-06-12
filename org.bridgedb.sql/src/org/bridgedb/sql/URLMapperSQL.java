package org.bridgedb.sql;

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

	void dropTable(String name) throws BridgeDbSqlException
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

    Statement createAStatement() throws SQLException{
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
	void createSQLTables() throws BridgeDbSqlException
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
                System.out.println(new Date().getTime() - start);
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


}