// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
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
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;

/**
 * Builds on the SQLListener to implement the Standard BridgeDB functions of IDMapper and IDMapperCapabilities.
 *
 * This Allows the OPS version to function as any other BridgeDB implementation
 *
 * @author Christian
 */
public class SQLIdMapper extends SQLListener implements IDMapper, IDMapperCapabilities {

    /** 
     * FreeSearch has proven to be very slow over large database so for large database we say it is unsupported.
     */
    private static final int FREESEARCH_CUTOFF = 100000;      
    //Internal parameters
    protected static final int DEFAULT_LIMIT = 1000;
    /**
     * This identifies version of SQL such as MySQL that use "LIMIT" to restrict the number of tuples returned.
     */
    private final boolean useLimit;
    /**
     * This identifies version of SQL such as Virtuoso that use "TOP" to restrict the number of tuples returned.
     */
    private final boolean useTop;
    
    public SQLIdMapper(boolean dropTables, StoreType storeType) throws BridgeDBException{
        super(dropTables, storeType);
        useLimit = SqlFactory.supportsLimit();
        useTop = SqlFactory.supportsTop();
     }   

    //*** IDMapper Methods 
    
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws BridgeDBException {
        if (badXref(ref)) {
            logger.warn("mapId called with a badXref " + ref);
            return new HashSet<Xref>();
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
            query.append(TARGET_ID_COLUMN_NAME);
                query.append(" as ");
                query.append(ID_COLUMN_NAME);
                query.append(", ");
            query.append(TARGET_DATASOURCE_COLUMN_NAME);
                query.append(" as ");
                query.append(SYSCODE_COLUMN_NAME);
        query.append(" FROM ");
            query.append(MAPPING_TABLE_NAME);
                query.append(", ");
            query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
            query.append(MAPPING_SET_ID_COLUMN_NAME);
                query.append(" = ");
                query.append(MAPPING_SET_TABLE_NAME);
                query.append(".");
                query.append(ID_COLUMN_NAME);
        appendSourceXref(query, ref);
        if (tgtDataSources.length > 0){    
            query.append(" AND ( ");
            query.append(TARGET_DATASOURCE_COLUMN_NAME);
            query.append(" = '");
            query.append(tgtDataSources[0].getSystemCode());
            query.append("' ");
            for (int i = 1; i < tgtDataSources.length; i++){
                query.append(" OR ");
                query.append(TARGET_DATASOURCE_COLUMN_NAME);
                query.append(" = '");
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
            throw new BridgeDBException("Unable to run query. " + query, ex);
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
        if (results.size() <= 1){
            String targets = "";
            for (DataSource tgtDataSource:tgtDataSources){
                targets+= tgtDataSource + ", ";
            }
            if (targets.isEmpty()){
                targets = "all DataSources";
            }
            if (results.isEmpty()){
                logger.warn("Unable to map " + ref + " to any results for " + targets);
            } else {
                logger.warn("Only able to map " + ref + " to itself for " + targets);
            }
        } else {
            logger.info("Mapped " + ref + " to " + results.size() + " results");
        }
        return results;
    }

    @Override
    public boolean xrefExists(Xref xref) throws BridgeDBException {
        if (badXref(xref)) return false;
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        appendTopConditions(query, 0, 1); 
        query.append(TARGET_ID_COLUMN_NAME);
        query.append(" FROM ");
        query.append(MAPPING_TABLE_NAME);
        query.append(", ");
        query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
        query.append(MAPPING_SET_ID_COLUMN_NAME);
        query.append(" = ");
        query.append(MAPPING_SET_DOT_ID_COLUMN_NAME);
        appendSourceXref(query, xref);
        appendLimitConditions(query,0, 1);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
            boolean result = rs.next();
            if (logger.isDebugEnabled()){
                logger.debug(xref + " exists = " + result);
            }
            return result;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
   }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        appendTopConditions(query, 0, limit); 
        query.append(SOURCE_ID_COLUMN_NAME);
        query.append(" as ");
        query.append(ID_COLUMN_NAME);
        query.append(", ");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        query.append(" as ");
        query.append(SYSCODE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(MAPPING_TABLE_NAME);
        query.append(", ");
        query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
        query.append(MAPPING_SET_ID_COLUMN_NAME);
        query.append(" = ");
        query.append(MAPPING_SET_DOT_ID_COLUMN_NAME);
        query.append(" AND ");
        query.append(SOURCE_ID_COLUMN_NAME);
        query.append(" = '");
            query.append(text);
            query.append("' ");
        appendLimitConditions(query,0, limit);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Set<Xref> results = resultSetToXrefSet(rs);
        if (logger.isDebugEnabled()){
            logger.debug("Freesearch for " + text + " gave " + results.size() + " results");
        }
        return results;
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return this;
    }

    //BridgeDB expects that once close is called isConnected will return false
    private boolean isConnected = true;
    
    @Override
    /** {@inheritDoc} */
    public void close() throws BridgeDBException { 
        isConnected = false;
        if (this.possibleOpenConnection != null){
            try {
                this.possibleOpenConnection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        logger.info("close() successful");
    }

    @Override
    /** {@inheritDoc} */
    public boolean isConnected() { 
        if (isConnected){
            try {
                sqlAccess.getConnection();
                return true;
            } catch (BridgeDBException ex) {
                return false;
            }
        }
        if (logger.isDebugEnabled()){
            logger.debug("isConnected() returned  " + isConnected);
        }
        return isConnected; 
    }
    // ***IDMapperCapabilities
    
    @Override
    public boolean isFreeSearchSupported() {
        if (logger.isDebugEnabled()){
            logger.debug("isFreeSearchSupported() returned  true");
        }
        return true;
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        query.append(" as ");
        query.append(SYSCODE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Set<DataSource> results = resultSetToDataSourceSet(rs);
        if (logger.isDebugEnabled()){
            logger.debug("getSupportedSrcDataSources() returned " + results.size() + " results");
        }
        return results;        
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(TARGET_DATASOURCE_COLUMN_NAME);
        query.append(" as ");
        query.append(SYSCODE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Set<DataSource> results = resultSetToDataSourceSet(rs);
        if (logger.isDebugEnabled()){
            logger.debug("getSupportedTgtDataSources() returned " + results.size() + " results");
        }
        return results;        
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(PREDICATE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        query.append(" = '");
            query.append(src.getSystemCode());
            query.append("' ");        
        query.append(" AND ");
        query.append(TARGET_DATASOURCE_COLUMN_NAME);
        query.append(" = '");
            query.append(tgt.getSystemCode());
            query.append("' ");        
        
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
            boolean result = rs.next();
            if (logger.isDebugEnabled()){
                logger.debug("isMappingSupported " + src + " to " + tgt + " is " + result);
            }
            return result;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
    }

    @Override
    public String getProperty(String key) {
        String query = "SELECT DISTINCT " + PROPERTY_COLUMN_NAME 
                + " FROM " + PROPERTIES_TABLE_NAME 
                + " WHERE " + KEY_COLUMN_NAME + " = '" + key + "'";
        try {
            Statement statement = this.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                String result = rs.getString("property");
                if (logger.isDebugEnabled()){
                    logger.debug("property " + key + " is " + result);
                }
                return result;
            } else {
                if (logger.isDebugEnabled()){
                    logger.warn("No property " + key + " found! ");
                }
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
        String query = "SELECT " + KEY_COLUMN_NAME
                + " FROM " + PROPERTIES_TABLE_NAME
                + " WHERE " + IS_PUBLIC_COLUMN_NAME + " = 1"; //one works where isPublic is a boolean
        try {
            Statement statement = this.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                results.add(rs.getString(KEY_COLUMN_NAME));
            }
            if (logger.isDebugEnabled()){
                logger.warn("getKeys() returned " + results.size() + " keys! ");
            }
            return results;
        } catch (Exception ex) {
            logger.error("Error getting keys ", ex);
            return null;
        }
    }

    //**** Support methods 
    
    /**
     * Check if the Xref is invalid in some way.
     * <p>
     * For example if it is null or if either the Id or DataSource part is null. 
     * @param ref
     * @return 
     */
    protected final boolean badXref(Xref ref) {
        if (ref == null) return true;
        if (ref.getId() == null || ref.getId().isEmpty()) return true;
        if (ref.getDataSource() == null ) return true;
        return false;
    }

    /**
     * Add a condition to the query that only mappings with a specific source Xref should be used.
     * @param query Query to add to.
     * @param ref Xref that forms the base of the condition.
     */
    protected final void appendSourceXref(StringBuilder query, Xref ref){
        query.append(" AND ");
            query.append(SOURCE_ID_COLUMN_NAME);
            query.append(" = '");
            query.append(ref.getId());
            query.append("' ");
        query.append(" AND ");
            query.append(SOURCE_DATASOURCE_COLUMN_NAME);
            query.append(" = '");
            query.append(ref.getDataSource().getSystemCode());
            query.append("' ");        
    }
    
    /**
     * Add a condition to the query that only mappings with a specific source Xref should be used.
     * @param query Query to add to.
     * @param ref Xref that forms the base of the condition.
     */
    protected final void appendSourceXref(StringBuilder query, String id, String sysCode){
        query.append(" AND ");
            query.append(SOURCE_ID_COLUMN_NAME);
            query.append(" = '");
            query.append(id);
            query.append("' ");
       query.append(" AND ");
            query.append(SOURCE_DATASOURCE_COLUMN_NAME);
            query.append(" = '");
            query.append(sysCode);
            query.append("' ");        
    }
    
    /**
     * Converts a ResultSet to a Set of individual Xrefs.
     * @throws BridgeDBException 
     */
    private Set<Xref> resultSetToXrefSet(ResultSet rs) throws BridgeDBException {
        HashSet<Xref> results = new HashSet<Xref>();
        try {
            while (rs.next()){
                String id = rs.getString(ID_COLUMN_NAME);
                String sysCode = rs.getString(SYSCODE_COLUMN_NAME);
                DataSource dataSource = DataSource.getBySystemCode(sysCode);
                Xref xref = new Xref(id, dataSource);
                results.add(xref);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
    }

    /**
     * Converts a ResultSet to a Set of BridgeDB DataSources by obtaining the SysCode from the ResultsSet a
     * and looking the DataSource up in the DataSource Registry.
     * <p>
     * All DataSources have been preloaded by loadDataSources() called during the super constructor.
     * @param rs
     * @return
     * @throws BridgeDBException 
     */
    private Set<DataSource> resultSetToDataSourceSet(ResultSet rs) throws BridgeDBException {
        HashSet<DataSource> results = new HashSet<DataSource>();
        try {
            while (rs.next()){
                String sysCode = rs.getString(SYSCODE_COLUMN_NAME);
                DataSource dataSource = DataSource.getBySystemCode(sysCode);
                results.add(dataSource);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
    }

    /**
     * Adds the limit condition if this is the support method for limitng the number of results.
     * <p>
     * System such as MYSQL use a Limit clause at the end of the query. Where applicable this is added here.
     * @param query Query to add Limit to
     * @param position The offset of the fragment of results to return
     * @param limit The size of the fragment of results to return
     */
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

    /**
     * Adds the top condition if this is the support method for limitng the number of results.
     * <p>
     * System such as Virtuosos use a Top clause directly after the select. Where applicable this is added here.
     * @param query Query to add TOP to
     * @param position The offset of the fragment of results to return
     * @param limit The size of the fragment of results to return
     */
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
