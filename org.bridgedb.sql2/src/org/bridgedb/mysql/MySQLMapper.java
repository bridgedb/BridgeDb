package org.bridgedb.mysql;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.bridgedb.Xref;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.newSQLBase;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
// removed Iterators due to scale issues URISpace, XrefIterator,
public class MySQLMapper extends newSQLBase {
    
    private static final int SQL_TIMEOUT = 2;
    StringBuilder insertQuery;

    public MySQLMapper(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(false, sqlAccess);
     }   

    public MySQLMapper(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
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
    protected void insertLink(String sourceId, String targetId, int mappingSetId) throws IDMapperException {
        System.out.println(blockCount);
        if (blockCount >= BLOCK_SIZE){
            runInsert();
            insertQuery = new StringBuilder("INSERT INTO mapping (sourceId, targetId, mappingSetId) VALUES ");
        } else {
            try {
                insertQuery.append(", ");        
            } catch (NullPointerException ex){
                throw new BridgeDbSqlException("Please run openInput() before insertLink");
            }
        }
        blockCount++;
        insertQuery.append("('");
        insertQuery.append(sourceId);
        insertQuery.append("', '");
        insertQuery.append(targetId);
        insertQuery.append("', ");
        insertQuery.append(mappingSetId);
        insertQuery.append(")");
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
    
    //@Override
    public void closeInput() throws IDMapperException {
        runInsert();
        super.closeInput();
        insertQuery = null;
    }

    //@Override
    public void openInput() throws BridgeDbSqlException {
        //Starting with a block will cause a new query to start.
        blockCount = BLOCK_SIZE ;
        insertCount = 0;
        doubleCount = 0;    
 	}


    @Override
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