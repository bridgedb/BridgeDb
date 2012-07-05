package org.bridgedb.virtuoso;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.utils.Reporter;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLIdMapper;
import org.bridgedb.sql.SQLUrlMapper;

public class VirtuosoMapper extends SQLUrlMapper {
    
    private PreparedStatement pstLink;
    
    public VirtuosoMapper(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(false, sqlAccess);
     }   

    public VirtuosoMapper(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(dropTables, sqlAccess);
    }   
    
    protected Statement createAStatement() throws SQLException{
        if (possibleOpenConnection == null){
            possibleOpenConnection = sqlAccess.getAConnection();
        } else if (possibleOpenConnection.isClosed()){
            possibleOpenConnection = sqlAccess.getAConnection();
        }  
        return possibleOpenConnection.createStatement();
    }

    @Override
    protected void insertLink(String sourceId, String targetId, int mappingSetId) throws IDMapperException {
        try {
            pstLink.setString(1, sourceId);
			pstLink.setString(2, targetId);
			pstLink.setInt(3, mappingSetId);
			pstLink.executeUpdate();
        } catch (SQLException ex) {
			throw new BridgeDbSqlException ("Error inserting forward link ", ex);
        }
        blockCount++;
        if (blockCount >= BLOCK_SIZE){
            Reporter.report("Inserted " + insertCount + " links ");
        }
    }
    
    //@Override
    public void openInput() throws BridgeDbSqlException {
        try {
            pstLink = sqlAccess.getAConnection().prepareStatement(
                	"INSERT INTO mapping (sourceId, targetId, mappingSetId) VALUES  (?, ?, ?)");
        } catch (SQLException ex) {
			throw new BridgeDbSqlException ("Error preparing inserting link statement", ex);
        }
        blockCount = 0;
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
    
    protected void appendMySQLLimitConditions(StringBuilder query, Integer position, Integer limit){
        //do nothing at all!
    }

    @Override
    protected void appendVirtuosoTopConditions(StringBuilder query, Integer position, Integer limit) {
        if (position == null) {
            position = 0;
        }
        if (limit == null){
            limit = DEFAULT_LIMIT;
        }
        query.append("TOP " + position + ", " + limit + " ");       
    }

    @Override
    protected String getAUTO_INCREMENT() {
        return "IDENTITY";
    }

}