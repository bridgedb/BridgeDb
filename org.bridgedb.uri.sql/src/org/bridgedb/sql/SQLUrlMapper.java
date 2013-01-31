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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.rdf.BridgeDBRdfHandler;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.Mapping;
import org.bridgedb.url.URLListener;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.UriPatternMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;

// SELECT id FROM test WHERE 'defdghij' like concat(prefix,'%', postfix);

/**
 * Implements the URLMapper and URLListener interfaces using SQL.
 *
 * Takes into accounts the specific factors for teh SQL version being used.
 *
 * @author Christian
 */
public class SQLUrlMapper extends SQLIdMapper implements URLMapper, URLListener {

    private static final int PREFIX_LENGTH = 400;
    private static final int POSTFIX_LENGTH = 100;
    private static final int MIMETYPE_LENGTH = 50;
    
    private static final String URL_TABLE_NAME = "url";
    private static final String MIMETYPE_TABLE_NAME = "mimeType";
   
    private static final String DATASOURCE_COLUMN_NAME = "dataSource";
    private static final String PREFIX_COLUMN_NAME = "prefix";
    private static final String POSTFIX_COLUMN_NAME = "postfix";
    private static final String MIMETYPE_COLUMN_NAME = "mimetype";

    static final Logger logger = Logger.getLogger(SQLListener.class);
    
    /**
     * Creates a new URLMapper including BridgeDB implementation based on a connection to the SQL Database.
     *
     * @param dropTables Flag to determine if any existing tables should be dropped and new empty tables created.
     * @param sqlAccess The connection to the actual database. This could be MySQL, Virtuoso ect.
     *       It could also be the live database, the loading database or the test database.
     * @param specific Code to hold the things that are different between different SQL implementaions.
     * @throws BridgeDBException
     */
     public SQLUrlMapper(boolean dropTables, StoreType storeType) throws BridgeDBException{
        super(dropTables, storeType);
        //if (dropTables){
        //    Map<String,DataSource> mappings = UriPatternMapper.getUriPatternMappings();
        //    for (String uriPattern:mappings.keySet()){
        //        this.registerUriPattern(mappings.get(uriPattern), uriPattern);
        //    }
        //}
        BridgeDBRdfHandler.init();
        Collection<UriPattern> patterns = UriPattern.getUriPatterns();
        for (UriPattern pattern:patterns){
            System.out.println(pattern);
            this.registerUriPattern(pattern);
        }           
    }   
    
    @Override
	protected void dropSQLTables() throws BridgeDBException
	{
        super.dropSQLTables();
 		dropTable(URL_TABLE_NAME);
 		dropTable(MIMETYPE_TABLE_NAME);
    }
 
    @Override
	protected void createSQLTables() throws BridgeDBException
	{
        super.createSQLTables();
		try 
		{
			Statement sh = createStatement();
            sh.execute("CREATE TABLE " + URL_TABLE_NAME
                    + "  (  " + DATASOURCE_COLUMN_NAME + " VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,   "
                    + "     " + PREFIX_COLUMN_NAME + " VARCHAR(" + PREFIX_LENGTH + ") NOT NULL, "
                    + "     " + POSTFIX_COLUMN_NAME + " VARCHAR(" + POSTFIX_LENGTH + ") NOT NULL "
                    + "  ) ");
            sh.execute("CREATE TABLE " + MIMETYPE_TABLE_NAME
                    + "  (  " + PREFIX_COLUMN_NAME + " VARCHAR(" + PREFIX_LENGTH + ") NOT NULL, "
                    + "     " + POSTFIX_COLUMN_NAME + " VARCHAR(" + POSTFIX_LENGTH + ") NOT NULL, "
                    + "     mimeType VARCHAR(" + MIMETYPE_LENGTH + ") NOT NULL "
                    + "  ) ");
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDBException ("Error creating the tables ", e);
		}
	}
    
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> URLs, String... targetURISpaces) throws BridgeDBException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
        for (String ref:URLs){
            Set<String> mapped = this.mapURL(ref, targetURISpaces);
            results.put(ref, mapped);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String URL, String... targetURISpaces) throws BridgeDBException {
        String id = getId(URL);
        String uriSpace = getUriSpace(URL);
        String sysCode = getSysCode(uriSpace);
        String[] targetSysCodes = getSysCodes(targetURISpaces);
        Set<Mapping> mappings = doMapping(id, sysCode, targetSysCodes);
        Set<String> results = new HashSet<String>();
        for (Mapping mapping: mappings){
            results.addAll(getURIs(mapping.getTargetId(), mapping.getTargetSysCode(), targetURISpaces));
        }
        logResults(results, URL, targetURISpaces);
        return results;
    }

    @Override
    public Map<Xref, Set<String>> mapToURLs(Collection<Xref> srcXrefs, String... targetURISpaces) throws BridgeDBException {
        HashMap<Xref, Set<String>> results = new HashMap<Xref, Set<String>>();
        for (Xref ref:srcXrefs){
            Set<String> mapped = this.mapToURLs(ref, targetURISpaces);
            results.put(ref, mapped);
        }
        return results;
    }
    
    @Override
    public Set<String> mapToURLs(Xref ref, String... targetURISpaces) throws BridgeDBException {
        String id = ref.getId();
        String sysCode = ref.getDataSource().getSystemCode();
        String[] targetSysCodes = getSysCodes(targetURISpaces);
        Set<Mapping> mappings = doMapping(id, sysCode, targetSysCodes);
        Set<String> results = new HashSet<String>();
        for (Mapping mapping: mappings){
            results.addAll(getURIs(mapping.getTargetId(), mapping.getTargetSysCode(), targetURISpaces));
        }
        this.logResults(results, ref.toString(), targetSysCodes);
        return results;
    }

    @Override
    public Set<Mapping> mapURLFull(String URL, String... targetURISpaces) throws BridgeDBException {
        String id = getId(URL);
        String uriSpace = getUriSpace(URL);
        String sysCode = getSysCode(uriSpace);
        
        String[] targetSysCodes = getSysCodes(targetURISpaces);
        Set<Mapping> results = doMapping(id, sysCode, targetSysCodes);
        for (Mapping mapping:results){
            mapping.addSourceURL(URL);
            addTargetURIs(mapping, targetURISpaces);
        }
        this.logResults(results, URL, targetSysCodes);
        return results;       
    }

    @Override
    public Set<Mapping> mapToURLsFull(Xref ref, String... targetURISpaces) throws BridgeDBException {
        String[] targetSysCodes = getSysCodes(targetURISpaces);
        Set<Mapping> results = doMapping(ref.getId(), ref.getDataSource().getSystemCode(), targetSysCodes);
        for (Mapping mapping:results){
            addTargetURIs(mapping, targetURISpaces);
        }
        this.logResults(results, ref.toString(), targetSysCodes);
        return results;       
    }

    @Override
    public boolean uriExists(String URL) throws BridgeDBException {
        String id = getId(URL);
        String uriSpace = getUriSpace(URL);
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT ");
        //TODO get DISTINCT working on Virtuosos
        appendTopConditions(query, 0, 1); 
        query.append("targetId ");
        query.append("FROM mapping, mappingSet, url as source ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mappingSet.sourceDataSource = source.dataSource ");
        query.append("AND sourceId = '");
            query.append(id);
            query.append("' ");
        query.append("AND source.");
        query.append(PREFIX_COLUMN_NAME);
        query.append(" = '");
            query.append(uriSpace);
            query.append("' ");
        appendLimitConditions(query,0, 1);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
            boolean result = rs.next();
            if (logger.isDebugEnabled()){
                logger.debug(URL + " exists = " + result);
            }
            return result;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws BridgeDBException {
        //ystem.out.println("mapping: " + sourceURL);
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT ");
        //TODO get DISTINCT working on Virtuosos
        appendTopConditions(query, 0, limit); 
        query.append(" sourceId as id, ");
        query.append(PREFIX_COLUMN_NAME);
        query.append(" FROM mapping, mappingSet, url ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mappingSet.SourceDataSource = url.dataSource ");
        query.append("AND sourceId = '");
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
        Set<String> results = resultSetToURLsSet(rs);
        if (logger.isDebugEnabled()){
            logger.debug("Freesearch for " + text + " gave " + results.size() + " results");
        }
        return results;       
    }
    
    @Override
    public Xref toXref(String uri) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(DATASOURCE_COLUMN_NAME);
        query.append(", ");
        query.append(PREFIX_COLUMN_NAME);
        query.append(", ");
        query.append(POSTFIX_COLUMN_NAME);
        query.append(" FROM ");
        query.append(URL_TABLE_NAME);
        query.append(" WHERE '");
        query.append(uri);
        query.append("' LIKE CONCAT(");
        query.append(PREFIX_COLUMN_NAME);
        query.append(",'%',");
        query.append(POSTFIX_COLUMN_NAME);
        query.append(")");
        
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        try {
            if (rs.next()){
                String sysCode = rs.getString(DATASOURCE_COLUMN_NAME);
                DataSource dataSource = DataSource.getBySystemCode(sysCode);
                String prefix = rs.getString(PREFIX_COLUMN_NAME);
                String postfix = rs.getString(POSTFIX_COLUMN_NAME);
                String id = uri.substring(prefix.length(), uri.length()-postfix.length());
                Xref result =  new Xref(id, dataSource);
                if (logger.isDebugEnabled()){
                    logger.debug(uri + " toXref " + result);
                }
                return result;
            }
            throw new BridgeDBException("No uri pattern regsitered that matches " + uri);
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to get uriSpace. " + query, ex);
        }    
    }

    @Override
    public Mapping getMapping(int id) throws BridgeDBException {
        StringBuilder query = new StringBuilder("SELECT mapping.id, sourceId, sourceDataSource, predicate,  "
                + "targetId, targetDataSource, mappingSet.id ");
        query.append("FROM mapping, mappingSet ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mapping.id = ");
            query.append(id);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Mapping result = resultSetToMapping(rs);
        addSourceURIs(result);
        addTargetURIs(result);      
        if (logger.isDebugEnabled()){
            logger.debug(" mapping " +id + " is " + result);
        }
        return result;    
    }

    @Override
    public List<Mapping> getSampleMapping() throws BridgeDBException {
        StringBuilder query = new StringBuilder("SELECT DISTINCT ");
        //TODO get DISTINCT working on Virtuosos
        this.appendTopConditions(query, 0, 5);
        query.append("mapping.id as id, sourceId, sourceDataSource, predicate, targetId, targetDataSource, mappingSetId ");
        query.append("FROM mapping, mappingSet, url as url1, url as url2 ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND sourceDataSource = url1.dataSource ");
        query.append("AND targetDataSource = url2.dataSource ");
        this.appendLimitConditions(query, 0, 5);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
            return resultSetToURLMappingList(rs);
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        //ystem.out.println(query);
    }


    @Override
    public OverallStatistics getOverallStatistics() throws BridgeDBException {
        int numberOfMappings = getMappingsCount();
        String linkSetQuery = "SELECT count(distinct(id)) as numberOfMappingSets, "
                + "count(distinct(mappingSet.sourceDataSource)) as numberOfSourceDataSources, "
                + "count(distinct(mappingSet.predicate)) as numberOfPredicates, "
                + "count(distinct(mappingSet.targetDataSource)) as numberOfTargetDataSources "
                + "FROM mappingSet ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(linkSetQuery);
            if (rs.next()){
                int numberOfMappingSets = rs.getInt("numberOfMappingSets");
                int numberOfSourceDataSources = rs.getInt("numberOfSourceDataSources");
                int numberOfPredicates= rs.getInt("numberOfPredicates");
                int numberOfTargetDataSources = rs.getInt("numberOfTargetDataSources");
                return new OverallStatistics(numberOfMappings, numberOfMappingSets, 
                        numberOfSourceDataSources, numberOfPredicates, numberOfTargetDataSources);
            } else {
                System.err.println(linkSetQuery);
                throw new BridgeDBException("no Results for query. " + linkSetQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + linkSetQuery, ex);
        }
    }

    private int getMappingsCount() throws BridgeDBException{
        String linkQuery = "SELECT count(*) as numberOfMappings "
                + "FROM mapping";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(linkQuery);
            if (rs.next()){
                return rs.getInt("numberOfMappings");
            } else {
                System.err.println(linkQuery);
                throw new BridgeDBException("No Results for query. " + linkQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + linkQuery, ex);
        }      
    }

    @Override
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws BridgeDBException {
        String query = "SELECT *"
                + " FROM mappingSet "
                + "WHERE id = " + mappingSetId;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            List<MappingSetInfo> results = resultSetToMappingSetInfos(rs);
            if (results.isEmpty()){
                throw new BridgeDBException ("No mappingSet found with id " + mappingSetId);
            }
            if (results.size() > 1){
                throw new BridgeDBException (results.size() + " mappingSets found with id " + mappingSetId);
            }
            return results.get(0);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public List<MappingSetInfo> getMappingSetInfos(String sourceSysCode, String targetSysCode) throws BridgeDBException {
        StringBuilder query = new StringBuilder("select * from mappingSet");
        appendSystemCodes(query, sourceSysCode, targetSysCode);
                
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToMappingSetInfos(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public Set<String> getUriSpaces(String dataSource) throws BridgeDBException {
        String query = ("SELECT " + PREFIX_COLUMN_NAME + " FROM url "
                + " WHERE dataSource = '" + dataSource + "'");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
        return resultSetToUriSpaces(rs);
    }
    
    @Override
    public Set<String> getSourceUriSpace(int mappingSetId) throws BridgeDBException {
        String query = ("SELECT " + PREFIX_COLUMN_NAME + " FROM url, mappingSet  "
                + " WHERE dataSource = sourceDataSource"
                + " AND mappingSet.id = " + mappingSetId);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
        return resultSetToUriSpaces(rs);
    }

    @Override
    public Set<String> getTargetUriSpace(int mappingSetId) throws BridgeDBException {
        String query = ("SELECT " + PREFIX_COLUMN_NAME + " FROM url, mappingSet  "
                + " WHERE dataSource = targetDataSource"
                + " AND mappingSet.id = " + mappingSetId);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
        return resultSetToUriSpaces(rs);
    }


    @Override
    public int getSqlCompatVersion() throws BridgeDBException {
        String query = ("select schemaversion from info");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
            //should always be there unless something has gone majorly wrong.
            rs.next();
            return rs.getInt("schemaversion");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }       
    }

    // **** URLListener Methods
    
    @Override
    public void registerUriPattern(DataSource source, String uriPattern) throws BridgeDBException {
        checkDataSourceInDatabase(source);
        int pos = uriPattern.indexOf("$id");
        if (pos == -1) {
            throw new BridgeDBException ("uriPattern " + uriPattern + " does not contain \"$id\"");
        }
        String prefix = uriPattern.substring(0, pos);
		String postfix = uriPattern.substring(pos + 3);
        this.registerUriPattern(source, prefix, postfix);
    }
    
    private void registerUriPattern (UriPattern uriPattern) throws BridgeDBException{
        DataSource dataSource = uriPattern.getDataSource();
        String prefix = uriPattern.getPrefix();
        String postfix = uriPattern.getPostfix();
        registerUriPattern(dataSource, prefix, postfix);
    }
    
    @Override
    public void registerUriPattern(DataSource dataSource, String prefix, String postfix) throws BridgeDBException {
        checkDataSourceInDatabase(dataSource);
        if (postfix == null){
            postfix = "";
        }
        String sysCode = getSysCode(prefix, postfix);
        if (prefix.length() > PREFIX_LENGTH){
            throw new BridgeDBException("Prefix Length ( " + prefix.length() + ") is too long for " + prefix);
        }
        if (postfix.length() > POSTFIX_LENGTH){
            throw new BridgeDBException("Postfix Length ( " + prefix.length() + ") is too long for " + prefix);
        }

        prefix = insertEscpaeCharacters(prefix);
        postfix = insertEscpaeCharacters(postfix);
        if (sysCode != null){
            if (dataSource.getSystemCode().equals(sysCode)) return; //Already known so fine.
            throw new BridgeDBException ("UriPattern " + prefix + "$id" + postfix + " already mapped to " + sysCode 
                    + " Which does not match " + dataSource.getSystemCode());
        }
        String query = "INSERT INTO " + URL_TABLE_NAME + " (" 
                + DATASOURCE_COLUMN_NAME + ", " 
                + PREFIX_COLUMN_NAME + ", " 
                + POSTFIX_COLUMN_NAME + ") VALUES "
                + " ('" + dataSource.getSystemCode() + "', "
                + "  '" + prefix + "',"
                + "  '" + postfix + "')";
        Statement statement = createStatement();
        try {
            int changed = statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new BridgeDBException ("Error inserting prefix " + prefix + " and postfix " + postfix , ex, query);
        }
    }

    public int registerMappingSet(String sourceUriSpace, String predicate, String targetUriSpace, boolean symetric, boolean transative) 
            throws BridgeDBException {
        DataSource source = getDataSource(sourceUriSpace);
        DataSource target = getDataSource(targetUriSpace);  
        if (source == target){
            throw new BridgeDBException("source == target");
        }
         return registerMappingSet(source, predicate, target, symetric, transative);
    }

    @Override
    public void insertURLMapping(String sourceURL, String targetURL, int mappingSet, boolean symetric) throws BridgeDBException {
        String sourceId = getId(sourceURL);
        String targetId = getId(targetURL);
        this.insertLink(sourceId, targetId, mappingSet, symetric);
    }


    /**
     * Method to split a URL into an URISpace and an ID.
     *
     * Based on OPENRDF version with ":" added as and extra splitter.
     *
     * Ideally this would be replaced by a method from Identifiers.org
     *    based on their knoweldge or ULI/URLs
     * @param url URL to split
     * @return The URISpace of the URL
     */
    public final static String getUriSpace(String url){
        String prefix = null;
        url = url.trim();
        if (url.contains("#")){
            prefix = url.substring(0, url.lastIndexOf("#")+1);
        } else if (url.contains("=")){
            prefix = url.substring(0, url.lastIndexOf("=")+1);
        } else if (url.contains("/")){
            prefix = url.substring(0, url.lastIndexOf("/")+1);
        } else if (url.contains(":")){
            prefix = url.substring(0, url.lastIndexOf(":")+1);
        }
        //ystem.out.println(lookupPrefix);
        if (prefix == null){
            throw new IllegalArgumentException("Url should have a '#', '/, or a ':' in it.");
        }
        if (prefix.isEmpty()){
            throw new IllegalArgumentException("Url should not start with a '#', '/, or a ':'.");            
        }
        return prefix;
    }

    /**
     * Method to split a URL into an URISpace and an ID.
     *
     * Based on OPENRDF version with ":" added as and extra splitter.
     *
     * Ideally this would be replaced by a method from Identifiers.org
     *    based on their knoweldge or ULI/URLs
     * @param url URL to split
     * @return The URISpace of the URL
     */
    public final static String getId(String url){
        url = url.trim();
        if (url.contains("#")){
            return url.substring(url.lastIndexOf("#")+1, url.length());
        } else if (url.contains("=")){
            return url.substring(url.lastIndexOf("=")+1, url.length());
        } else if (url.contains("/")){
            return url.substring(url.lastIndexOf("/")+1, url.length());
        } else if (url.contains(":")){
            return url.substring(url.lastIndexOf(":")+1, url.length());
        }
        throw new IllegalArgumentException("Url should have a '#', '/, or a ':' in it.");
    }

    /**
     * Generates a set of URl from a ResultSet.
     *
     * This implementation just concats the URISpace and Id
     *
     * Ideally this would be replaced by a method from Identifiers.org
     *    based on their knoweldge or ULI/URLs
     * This may require the method to be exstended with the Target NameSpaces.
     *
     * @param rs Result Set holding the information
     * @return URLs generated
     * @throws BridgeDBException
     */
    private Set<String> resultSetToURLsSet(ResultSet rs) throws BridgeDBException {
        HashSet<String> results = new HashSet<String>();
        try {
            while (rs.next()){
                String id = rs.getString("id");
                String uriSpace = rs.getString(PREFIX_COLUMN_NAME);
                String uri = uriSpace + id;
                results.add(uri);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
    }

   /**
     * Generates a set of UriSpaces a ResultSet.
     *
     * This implementation just extracts the URISpace
     *
     * @param rs Result Set holding the information
     * @return UriSpaces generated
     * @throws BridgeDBException
     */
     private Set<String> resultSetToUriSpaces(ResultSet rs) throws BridgeDBException {
        try {
            HashSet<String> uriSpaces = new HashSet<String>();
            while (rs.next()){
                uriSpaces.add(rs.getString(PREFIX_COLUMN_NAME));
            }
            return uriSpaces;
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
    }

    private Set<Mapping> resultSetToURLMappingSet(String id, String sysCode, ResultSet rs) throws BridgeDBException {
        HashSet<Mapping> results = new HashSet<Mapping>();
        try {
            while (rs.next()){
                Integer mappingId = rs.getInt("mappingId"); 
                String targetId = rs.getString("id");
                String targetSysCode = rs.getString("sysCode");
                Integer mappingSetId = rs.getInt("mappingSetId");
                String predicate = rs.getString("predicate");
                Mapping urlMapping = new Mapping (mappingId, id, sysCode, predicate, targetId, targetSysCode, mappingSetId);       
                results.add(urlMapping);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
    }

    private List<Mapping> resultSetToURLMappingList(ResultSet rs) throws BridgeDBException {
        ArrayList<Mapping> results = new ArrayList<Mapping>();
        try {
            while (rs.next()){
                Integer mappingId = rs.getInt("mapping.Id"); 
                String sourceId = rs.getString("sourceId");
                String sourceDataSource = rs.getString("sourceDataSource");
                String targetId = rs.getString("targetId");
                String targetDataSource = rs.getString("targetDataSource");
                Integer mappingSetId = rs.getInt("mappingSetId");
                String predicate = rs.getString("predicate");
                Mapping urlMapping = new Mapping (mappingId, sourceId, sourceDataSource, predicate, 
                        targetId, targetDataSource, mappingSetId);   
                addSourceURIs(urlMapping);
                addTargetURIs(urlMapping);
                results.add(urlMapping);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
    }

    /**
     * Generates a single mapping from an id.
     *
     * This method (an probaly the calling methods) needs replacing with one created by identifiers.org
     *
     * @param rs Result set with exactky one result
     * @return The mapping or null
     * @throws BridgeDBException
     */
    private Mapping resultSetToMapping(ResultSet rs) throws BridgeDBException {
        try {
            Mapping urlMapping;
            if (rs.next()){
                Integer mappingId = rs.getInt("mapping.id"); 
                String sourceId = rs.getString("sourceId");
                String sourceSysCode = rs.getString("sourceDataSource");
                String targetId = rs.getString("targetId");
                String targetSysCode = rs.getString("targetDataSource");
                Integer mappingSetId = rs.getInt("mappingSet.id");
                String predicate = rs.getString("predicate");
                urlMapping = new Mapping (mappingId, sourceId, sourceSysCode, predicate, 
                        targetId, targetSysCode, mappingSetId);       
            } else {
                return null;
            }
            while (rs.next()){
                String sourceURL = rs.getString("source." + PREFIX_COLUMN_NAME) + rs.getString("sourceId");
                urlMapping.addSourceURL(sourceURL);
                String targetURL = rs.getString("target." + PREFIX_COLUMN_NAME) + rs.getString("targetId"); 
                urlMapping.addTargetURL(targetURL);
            }
            return urlMapping;
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
    }

   /**
     * Generates the meta info from the result of a query
     * @param rs
     * @return
     * @throws BridgeDBException
     */
    private List<MappingSetInfo> resultSetToMappingSetInfos(ResultSet rs ) throws BridgeDBException{
        ArrayList<MappingSetInfo> results = new ArrayList<MappingSetInfo>();
        try {
            while (rs.next()){
                Integer count = rs.getInt("mappingCount");
                results.add(new MappingSetInfo(rs.getString("id"), rs.getString("sourceDataSource"), 
                        rs.getString("predicate"), rs.getString("targetDataSource"), count, 
                        rs.getBoolean("isTransitive")));
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
        }
        return results;
    }


    /**
     * Finds the SysCode of the DataSource which includes this URISpace
     *
     * Should be replaced by a more complex method from identifiers.org
     *
     * @param uriSpace to find DataSource for
     * @return sysCode of an existig DataSource or null
     * @throws BridgeDBException
     */
    private String getSysCode(String uriSpace) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT dataSource ");
        query.append("FROM url ");
        query.append("WHERE ");
        query.append(PREFIX_COLUMN_NAME);
        query.append(" = '");
        query.append(uriSpace);
        query.append("' ");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        try {
            if (rs.next()){
                return rs.getString("dataSource");
            }
            return null;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to get uriSpace. " + query, ex);
        }    
    }

    /**
     * Finds the SysCode of the DataSource which includes this prefix and postfix
     *
     * Should be replaced by a more complex method from identifiers.org
     *
     * @param prefix to find DataSource for
     * @param postfix to find DataSource for
     * @return sysCode of an existig DataSource or null
     * @throws BridgeDBException
     */
    private String getSysCode(String prefix, String postfix) throws BridgeDBException {
        if (postfix == null){
            postfix = "";
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(DATASOURCE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(URL_TABLE_NAME);
        query.append(" WHERE ");
        query.append(PREFIX_COLUMN_NAME);
        query.append(" = '");
        query.append(prefix);
        query.append("' ");
        query.append(" AND ");
        query.append(POSTFIX_COLUMN_NAME);
        query.append(" = '");
        query.append(postfix);
        query.append("' ");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        try {
            if (rs.next()){
                return rs.getString(DATASOURCE_COLUMN_NAME);
            }
            return null;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to get SysCode. " + query, ex);
        }    
    }

    /**
     * Returns the DataSource associated with a URISpace.
     *
     * Throws an exception if the URISpace is unknown.
     *
     * @param uriSpace A Known URISpace
     * @return A DataSource. Never null, instead an Exception is thrown
     * @throws BridgeDBException For example if the uriSpace is not known.
     */
    private DataSource getDataSource(String uriSpace) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT dataSource ");
        query.append("FROM url ");
        query.append("WHERE ");
        query.append(PREFIX_COLUMN_NAME);
        query.append(" = '");
            query.append(uriSpace);
            query.append("' ");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        HashSet<String> results = new HashSet<String>();
        try {
            if (rs.next()){
                String sysCode = rs.getString("dataSource");
                return DataSource.getBySystemCode(sysCode);
            }
            DataSource.Builder builder = DataSource.register(uriSpace, uriSpace).urlPattern(uriSpace+"$id");
            return builder.asDataSource();
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
    }

    private void appendSystemCodes(StringBuilder query, String sourceSysCode, String targetSysCode) {
        boolean whereAdded = false;
        if (sourceSysCode != null && !sourceSysCode.isEmpty()){
            whereAdded = true;
            query.append(" WHERE sourceDataSource = \"" );
            query.append(sourceSysCode);
            query.append("\" ");
        }
        if (targetSysCode != null && !targetSysCode.isEmpty()){
            if (whereAdded){
                query.append(" AND " );            
            } else {
                query.append(" WHERE " );            
            }
            query.append("targetDataSource = \"" );
            query.append(targetSysCode);
            query.append("\" ");
        }
    }

    private Set<Mapping> doMapping(String id, String sysCode, String... tgtSysCodes) throws BridgeDBException {
        Set<Mapping> mappings;
        if (id == null || sysCode == null){
            mappings = new HashSet<Mapping>();
        } else {
            mappings = doMappingQuery(id, sysCode, tgtSysCodes);
        }
        addMapToSelf(mappings, id, sysCode, tgtSysCodes);
        return mappings;
    }

    private Set<Mapping> doMappingQuery(String id, String sysCode, String... tgtSysCodes) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT targetId as id, targetDataSource as sysCode, mapping.id as mappingId, predicate, ");
        query.append("mappingSet.id as mappingSetId ");
        query.append("FROM mapping, mappingSet ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        appendSourceXref(query, id, sysCode);
        if (tgtSysCodes.length > 0){    
            query.append("AND ( targetDataSource = '");
                query.append(tgtSysCodes[0]);
                query.append("' ");
            for (int i = 1; i < tgtSysCodes.length; i++){
                query.append("OR targetDataSource = '");
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
        Set<Mapping> results = resultSetToURLMappingSet(id, sysCode, rs);
        return results;
    }
    
    private void addMapToSelf( Set<Mapping> mappings, String id, String sysCode, String... tgtSysCodes) throws BridgeDBException {
        if (tgtSysCodes.length == 0){
           mappings.add(new Mapping (id, sysCode)); 
        } else {
            if (sysCode != null){
                for (String tgtSysCode: tgtSysCodes){
                    if (sysCode.equals(tgtSysCode)){
                        mappings.add(new Mapping(id, sysCode));
                    }
                }
            }
        }
    }

    private Set<String> getURIs(String id, String sysCode, String... targetURISpaces) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT " + PREFIX_COLUMN_NAME);
        query.append(" FROM url ");
        query.append("WHERE dataSource = '");
        query.append(sysCode);
        query.append("' ");
        if (targetURISpaces.length > 0){    
            query.append("AND (");
            query.append(PREFIX_COLUMN_NAME);
            query.append(" = '");
            query.append(targetURISpaces[0]);
            query.append("' ");
            for (int i = 1; i < targetURISpaces.length; i++){
                query.append("OR ");
                query.append(PREFIX_COLUMN_NAME);
                query.append(" = '");
                query.append(targetURISpaces[i]);
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
        HashSet<String> results = new HashSet<String>();
        try {
            while (rs.next()){
                String uriSpace = rs.getString(PREFIX_COLUMN_NAME);
                String uri = uriSpace + id;
                results.add(uri);
            }
       } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
       }
       return results;
    }

    private void addSourceURIs(Mapping mapping) throws BridgeDBException {
        Set<String> URIs = getURIs(mapping.getSourceId(), mapping.getSourceSysCode());
        mapping.addSourceURLs(URIs);
    }

    private void addTargetURIs(Mapping mapping, String... targetURISpaces) throws BridgeDBException {
        Set<String> URIs = getURIs(mapping.getTargetId(), mapping.getTargetSysCode(), targetURISpaces);
        mapping.addTargetURLs(URIs);
    }

    private String[] getSysCodes(String[] URISpaces) throws BridgeDBException{
        String[] sysCodes = new String[URISpaces.length];
        for (int i = 0; i < URISpaces.length; i++){
            sysCodes[i] = getSysCode(URISpaces[i]);
        }
        return sysCodes;
    }

    private void logResults(Set results, String source, String[] targets) throws BridgeDBException {
        if (results.isEmpty()){
            String targetSt= "";
            if (targets.length == 0){
                targetSt = "all DataSources";
            } else {
                for (String targetURISpace:targets){
                    targetSt+= targetURISpace + ", ";
                }
            }
            logger.warn("Unable to map " + source + " to any results for " + targetSt);
        } 
    }

}
