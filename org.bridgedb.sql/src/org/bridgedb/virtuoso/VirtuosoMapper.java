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
public class VirtuosoMapper extends SQLBase implements IDMapper, IDMapperCapabilities, URLLinkListener, URLMapper, ProvenanceMapper, 
        OpsMapper, URLIterator {
    
    private PreparedStatement pstLink;
    
    public VirtuosoMapper(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(sqlAccess);
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
        blockCount++;
        try {
            pstLink.setString(1, target);
			pstLink.setString(2, source);
			pstLink.setString(3, inverseProvenanceId);
			pstLink.executeUpdate();
        } catch (SQLException ex) {
			throw new BridgeDbSqlException ("Error inserting inverse link ", ex);
        }
        blockCount++;
        if (blockCount >= BLOCK_SIZE){
            Reporter.report("Inserted " + insertCount + " links ");
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