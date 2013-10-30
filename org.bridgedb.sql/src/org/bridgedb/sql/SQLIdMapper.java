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
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.pairs.CodeMapper;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.utils.BridgeDBException;

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
    /**
     * Map between IdSysCodePair and Xref or DataSources 
     */
    protected final CodeMapper codeMapper1;
    
    private static final Logger logger = Logger.getLogger(SQLIdMapper.class);

    public SQLIdMapper(boolean dropTables, CodeMapper codeMapper) throws BridgeDBException{
        super(dropTables);
        useLimit = SqlFactory.supportsLimit();
        useTop = SqlFactory.supportsTop();
        this.codeMapper1 = codeMapper;
     }   

    //*** IDMapper Methods 
    
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    @Override
    public Set<Xref> mapID(Xref xref, DataSource... tgtDataSources) throws BridgeDBException {
        IdSysCodePair ref = toIdSysCodePair(xref);
        if (ref == null) {
            logger.warn("mapId called with a badXref " + xref);
            return new HashSet<Xref>();
        }
        String[] tgtSysCodes = toCodes(tgtDataSources);
        Set<IdSysCodePair> pairs = mapID(ref, tgtSysCodes);
        return toXrefs(pairs);
    }

    private Set<IdSysCodePair> mapID(IdSysCodePair ref, String... tgtSysCodes) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
            query.append(TARGET_ID_COLUMN_NAME);
                query.append(", ");
            query.append(TARGET_DATASOURCE_COLUMN_NAME);
        query.append(" FROM ");
            query.append(MAPPING_TABLE_NAME);
                query.append(", ");
            query.append(MAPPING_SET_TABLE_NAME);
        appendMappingJoinMapping(query);
        appendSourceIdSysCodePair(query, ref);
        if (tgtSysCodes != null &&tgtSysCodes.length > 0){    
            query.append(" AND ( ");
            query.append(TARGET_DATASOURCE_COLUMN_NAME);
            query.append(" = '");
            query.append(tgtSysCodes[0]);
            query.append("' ");
            for (int i = 1; i < tgtSysCodes.length; i++){
                query.append(" OR ");
                query.append(TARGET_DATASOURCE_COLUMN_NAME);
                query.append(" = '");
                query.append(tgtSysCodes[i]);
                query.append("'");
            }
            query.append(")");
        }
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Set<IdSysCodePair> results = resultSetToIdSysCodePairSet(rs);
        if (tgtSysCodes.length == 0){
           results.add(ref); 
        } else {
            for (String tgtSysCode: tgtSysCodes){
                if (ref.getSysCode().equals(tgtSysCode)){
                    results.add(ref);
                }
            }
        }
        if (results.size() <= 1){
            String targets = "";
            for (String tgtSysCode: tgtSysCodes){
                targets+= tgtSysCode + ", ";
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

 	/**
	 * Get all cross-references for the given entity, restricting the
	 * result to contain only references from the given set of data sources.
	 * @param ref the entity to get cross-references for. 
     * @param tgtDataSources target ID types/data source. Can not be null
	 * @return A Set containing the cross references, or an empty
	 * Set when no cross references could be found. This method does not return null.
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
    public Set<Xref> mapID(Xref xref, DataSource tgtDataSource) throws BridgeDBException {
        IdSysCodePair ref = toIdSysCodePair(xref);
        if (ref == null) {
            logger.warn("mapId called with a badXref " + xref);
            return new HashSet<Xref>();
        }
        if (tgtDataSource == null){
            throw new BridgeDBException("Target DataSource can not be null");
        }
        String tgtSysCode = toCode(tgtDataSource);
        Set<IdSysCodePair> pairs = mapID(ref, tgtSysCode);
        return toXrefs(pairs);
    }

    private Set<IdSysCodePair> mapID(IdSysCodePair ref, String tgtSysCode) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
            query.append(TARGET_ID_COLUMN_NAME);
                query.append(", ");
            query.append(TARGET_DATASOURCE_COLUMN_NAME);
        query.append(" FROM ");
            query.append(MAPPING_TABLE_NAME);
                query.append(", ");
            query.append(MAPPING_SET_TABLE_NAME);
        appendMappingJoinMapping(query);
        appendSourceIdSysCodePair(query, ref);
        query.append(" AND ");
            query.append(TARGET_DATASOURCE_COLUMN_NAME);
            query.append(" = '");
            query.append(tgtSysCode);
            query.append("' ");

        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Set<IdSysCodePair> pairs = resultSetToIdSysCodePairSet(rs);
        if (ref.getSysCode().equals(tgtSysCode)){
            pairs.add(ref);
        }
        return pairs;
    }

    @Override
    public boolean xrefExists(Xref xref) throws BridgeDBException {
        IdSysCodePair ref = toIdSysCodePair(xref);
        if (ref == null) {
            return false;
        }
        return IdSysCodePairExists(ref);
   }

   protected boolean IdSysCodePairExists(IdSysCodePair ref) throws BridgeDBException {
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
        appendSourceIdSysCodePair(query, ref);
        appendLimitConditions(query,0, 1);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
            boolean result = rs.next();
            if (logger.isDebugEnabled()){
                logger.debug(ref + " exists = " + result);
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
        query.append(TARGET_ID_COLUMN_NAME);
        query.append(", ");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        query.append(" as ");
        query.append(TARGET_DATASOURCE_COLUMN_NAME);
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
        Set<IdSysCodePair> pairs = resultSetToIdSysCodePairSet(rs);
        if (logger.isDebugEnabled()){
            logger.debug("Freesearch for " + text + " gave " + pairs.size() + " results");
        }
        return toXrefs(pairs);
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return this;
    }

    //BridgeDB expects that once close is called isConnected will return false
    private boolean isConnected = true;
    
    @Override
    /** {@inheritDoc} */
    public void close() {
        isConnected = false;
        closeConnection();
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
        query.append(ID_COLUMN_NAME);
        query.append(" FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        query.append(" = '");
            query.append(getDataSourceKey(src));
            query.append("' ");        
        query.append(" AND ");
        query.append(TARGET_DATASOURCE_COLUMN_NAME);
        query.append(" = '");
            query.append(getDataSourceKey(tgt));
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
     * Add a condition to the query that only mappings with a specific source IdSysCodePair should be used.
     * @param query Query to add to.
     * @param ref IdSysCodePair that forms the base of the condition.
     */
    protected final void appendSourceIdSysCodePair(StringBuilder query, IdSysCodePair ref){
        query.append(" AND ");
            query.append(SOURCE_ID_COLUMN_NAME);
            query.append(" = '");
            query.append(ref.getId());
            query.append("' ");
        query.append(" AND ");
            query.append(SOURCE_DATASOURCE_COLUMN_NAME);
            query.append(" = '");
            query.append(ref.getSysCode());
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
    final Set<IdSysCodePair> resultSetToIdSysCodePairSet(ResultSet rs) throws BridgeDBException {
        HashSet<IdSysCodePair> results = new HashSet<IdSysCodePair>();
        try {
            while (rs.next()){
                String id = rs.getString(TARGET_ID_COLUMN_NAME);
                String sysCode = rs.getString(TARGET_DATASOURCE_COLUMN_NAME);
                IdSysCodePair pair = new IdSysCodePair(id, sysCode);
                results.add(pair);
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
                results.add(DataSource.getExistingBySystemCode(sysCode));
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
            query.append(" LIMIT " + position + ", " + limit);       
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

    protected final  void appendMappingJoinMapping(StringBuilder query){ 
        query.append(" WHERE ");
        query.append(MAPPING_SET_ID_COLUMN_NAME);
        query.append(" = ");
        query.append(MAPPING_SET_DOT_ID_COLUMN_NAME);
     }

    protected final String[] toCodes(DataSource[] tgtDataSources) {
        if (tgtDataSources == null || tgtDataSources.length == 0){
            return new String[0];
        }
        String[] results = new String[tgtDataSources.length];
        for (int i = 0; i < tgtDataSources.length; i++){
            results[i] =  tgtDataSources[i].getSystemCode();
        }
        return results;
    }

    protected final IdSysCodePair toIdSysCodePair(Xref xref) throws BridgeDBException {
        if (xref == null || xref.getId() == null || xref.getDataSource() == null){
            return null;
        }
        return codeMapper1.toIdSysCodePair(xref);
    }

    protected final Set<Xref> toXrefs(Set<IdSysCodePair> pairs) throws BridgeDBException {
        HashSet<Xref> results = new HashSet<Xref>();
        for (IdSysCodePair pair:pairs){
            results.add(codeMapper1.toXref(pair));
        }
        return results;
    }

    protected final String toCode(DataSource tgtDataSource) {
        if (tgtDataSource == null){
            return null;
        }
        return tgtDataSource.getSystemCode();
    }

 
}
