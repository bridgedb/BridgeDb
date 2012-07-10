package org.bridgedb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;

/**
 *
 * @author Christian
 */
public class SQLIdMapper extends SQLListener implements IDMapper, IDMapperCapabilities {

    private static final int FREESEARCH_CUTOFF = 100000;      
    //Internal parameters
    protected static final int DEFAULT_LIMIT = 1000;
    private final boolean useLimit;
    private final boolean useTop;
    
    public SQLIdMapper(boolean dropTables, SQLAccess sqlAccess, SQLSpecific specific) throws BridgeDbSqlException{
        super(dropTables, sqlAccess, specific);
        useLimit = specific.supportsLimit();
        useTop = specific.supportsTop();
     }   

    //*** IDMapper Methods 
    
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws BridgeDbSqlException {
        if (badXref(ref)) return new HashSet<Xref>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT targetId as id, targetDataSource as sysCode ");
        query.append("FROM mapping, mappingSet ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        appendSourceXref(query, ref);
        if (tgtDataSources.length > 0){    
            query.append("AND ( targetDataSource = '");
                query.append(tgtDataSources[0].getSystemCode());
                query.append("' ");
            for (int i = 1; i < tgtDataSources.length; i++){
                query.append("OR targetDataSource = '");
                    query.append(tgtDataSources[i].getSystemCode());
                    query.append("'");
            }
            query.append(")");
        }
        //ystem.out.println(query);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        Set<Xref> results = resultSetToXrefSet(rs);
        if (tgtDataSources.length == 0){
           results.add(ref); 
        } else {
            for (DataSource tgtDataSource: tgtDataSources){
                if (ref.getDataSource().equals(tgtDataSource)){
                    results.add(ref);
                }
            }
        }
        return results;
    }

    @Override
    public boolean xrefExists(Xref xref) throws BridgeDbSqlException {
        if (badXref(xref)) return false;
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        appendTopConditions(query, 0, 1); 
        query.append("targetId ");
        query.append("FROM mapping, mappingSet ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        appendSourceXref(query, xref);
        appendLimitConditions(query,0, 1);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
            return rs.next();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
   }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        appendTopConditions(query, 0, limit); 
        query.append(" targetId as id, targetDataSource as sysCode ");
        query.append("FROM mapping, mappingSet ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND sourceId = '");
            query.append(text);
            query.append("' ");
        appendLimitConditions(query,0, limit);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        return resultSetToXrefSet(rs);
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return this;
    }

    //BridgeDB expects that once close is called isConnected will return false
    private boolean isConnected = true;
    
    @Override
    /** {@inheritDoc} */
    public void close() throws BridgeDbSqlException { 
        isConnected = false;
        if (this.possibleOpenConnection != null){
            try {
                this.possibleOpenConnection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    /** {@inheritDoc} */
    public boolean isConnected() { 
        if (isConnected){
            try {
                sqlAccess.getConnection();
                return true;
            } catch (BridgeDbSqlException ex) {
                return false;
            }
        }
        return isConnected; 
    }
    // ***IDMapperCapabilities
    
    @Override
    public boolean isFreeSearchSupported() {
        return true;
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT sourceDataSource as sysCode ");
        query.append("FROM mappingSet ");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        return resultSetToDataSourceSet(rs);        
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT targetDataSource as sysCode ");
        query.append("FROM mappingSet ");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        return resultSetToDataSourceSet(rs);        
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT predicate ");
        query.append("FROM mappingSet ");
        query.append("WHERE sourceDataSource = '");
            query.append(src.getSystemCode());
            query.append("' ");        
        query.append("AND targetDataSource = '");
            query.append(tgt.getSystemCode());
            query.append("' ");        
        
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
            return rs.next();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
    }

    @Override
    public String getProperty(String key) {
        String query = "SELECT DISTINCT property "
                + "FROM properties "
                + "WHERE theKey = '" + key + "'";
        try {
            Statement statement = this.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return rs.getString("property");
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Set<String> getKeys() {
        HashSet<String> results = new HashSet<String>();
        String query = "SELECT theKey "
                + "FROM properties "
                + "WHERE isPublic = 1"; //one works where isPublic is a boolean
        try {
            Statement statement = this.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                results.add(rs.getString("theKey"));
            }
            return results;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //**** Support methods 
    
    private final boolean badXref(Xref ref) {
        if (ref == null) return true;
        if (ref.getId() == null || ref.getId().isEmpty()) return true;
        if (ref.getDataSource() == null ) return true;
        return false;
    }

    private final void appendSourceXref(StringBuilder query, Xref ref){
        query.append("AND sourceId = '");
            query.append(ref.getId());
            query.append("' ");
       query.append("AND sourceDataSource = '");
            query.append(ref.getDataSource().getSystemCode());
            query.append("' ");        
    }
    
    private Set<Xref> resultSetToXrefSet(ResultSet rs) throws BridgeDbSqlException {
        HashSet<Xref> results = new HashSet<Xref>();
        try {
            while (rs.next()){
                String id = rs.getString("id");
                String sysCode = rs.getString("sysCode");
                DataSource dataSource = DataSource.getBySystemCode(sysCode);
                Xref xref = new Xref(id, dataSource);
                results.add(xref);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

    private Set<DataSource> resultSetToDataSourceSet(ResultSet rs) throws BridgeDbSqlException {
        HashSet<DataSource> results = new HashSet<DataSource>();
        try {
            while (rs.next()){
                String sysCode = rs.getString("sysCode");
                DataSource dataSource = DataSource.getBySystemCode(sysCode);
                results.add(dataSource);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

    protected void appendLimitConditions(StringBuilder query, Integer position, Integer limit){
        if (useLimit){
            if (position == null) {
                position = 0;
            }
            if (limit == null){
                limit = DEFAULT_LIMIT;
            }
            query.append("LIMIT " + position + ", " + limit);       
        }
    }

    protected void appendTopConditions(StringBuilder query, Integer position, Integer limit){
        if (useTop){
            if (position == null) {
                position = 0;
            }
            if (limit == null){
                limit = DEFAULT_LIMIT;
            }
            query.append("TOP " + position + ", " + limit + " ");                
        }
    }



}
