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
public class MysqlMapper extends SQLBase implements IDMapper, IDMapperCapabilities, URLLinkListener, URLMapper, ProvenanceMapper, 
        OpsMapper, URLIterator {
    
    private static final int SQL_TIMEOUT = 2;
    StringBuilder insertQuery;

    public MysqlMapper(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(sqlAccess);
     }   

    public MysqlMapper(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(dropTables, sqlAccess);
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
    
 
    @Override
    public void insertLink(String source, String target, String forwardLinkSetId, String inverseLinkSetId)
            throws IDMapperException {
        if (blockCount >= BLOCK_SIZE){
            runInsert();
            insertQuery = new StringBuilder("INSERT INTO link (sourceURL, targetURL, linkSetId) VALUES ");
        } else {
            insertQuery.append(", ");        
        }
        blockCount++;
        insertQuery.append("('");
        insertQuery.append(source);
        insertQuery.append("', '");
        insertQuery.append(target);
        insertQuery.append("', '");
        insertQuery.append(forwardLinkSetId);
        insertQuery.append("'),");
        blockCount++;
        insertQuery.append("('");
        insertQuery.append(target);
        insertQuery.append("', '");
        insertQuery.append(source);
        insertQuery.append("', '");
        insertQuery.append(inverseLinkSetId);
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
    protected String getAUTO_INCREMENT() {
        return "AUTO_INCREMENT";
    }

}