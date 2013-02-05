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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.rdf.BridgeDBRdfHandler;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.url.Mapping;
import org.bridgedb.url.URLListener;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.UriPatternMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

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
    private static final String PROFILE_JUSTIFICATIONS_TABLE_NAME = "profileJustifications";
    private static final String PROFILE_TABLE_NAME = "profile";
    
    private static final String CREATED_BY_COLUMN_NAME = "createdBy";
    private static final String CREATED_ON_COLUMN_NAME = "createdOn";
    private static final String DATASOURCE_COLUMN_NAME = "dataSource";
    private static final String JUSTIFICTAION_URI_COLUMN_NAME = "justificationURI";
    private static final String PREFIX_COLUMN_NAME = "prefix";
    private static final String PROFILE_ID_COLUMN_NAME = "profileId";
    private static final String POSTFIX_COLUMN_NAME = "postfix";
    private static final String MIMETYPE_COLUMN_NAME = "mimetype";
    private static final String NAME_COLUMN_NAME = "name";
    
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
        if (dropTables){
        //    Map<String,DataSource> mappings = UriPatternMapper.getUriPatternMappings();
        //    for (String uriPattern:mappings.keySet()){
        //        this.registerUriPattern(mappings.get(uriPattern), uriPattern);
        //    }
            BridgeDBRdfHandler.init();
            Collection<UriPattern> patterns = UriPattern.getUriPatterns();
            for (UriPattern pattern:patterns){
                this.registerUriPattern(pattern);
            }           
        }
    }   
    
    @Override
	protected void dropSQLTables() throws BridgeDBException
	{
        super.dropSQLTables();
 		dropTable(URL_TABLE_NAME);
 		dropTable(MIMETYPE_TABLE_NAME);
 		dropTable(PROFILE_TABLE_NAME);
 		dropTable(PROFILE_JUSTIFICATIONS_TABLE_NAME);
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
            sh.execute("CREATE TABLE " + PROFILE_TABLE_NAME + " ( " +
            		PROFILE_ID_COLUMN_NAME + " INT " + autoIncrement + " PRIMARY KEY, " +
            		NAME_COLUMN_NAME + " VARCHAR(" + FULLNAME_LENGTH + ") NOT NULL, " +
            		CREATED_ON_COLUMN_NAME + " DATETIME, " +
            		CREATED_BY_COLUMN_NAME + " VARCHAR(" + PREDICATE_LENGTH + ") " +
            		")");
            sh.execute("CREATE TABLE " + PROFILE_JUSTIFICATIONS_TABLE_NAME + " ( " +
            		PROFILE_ID_COLUMN_NAME + " INT NOT NULL, " +
            		JUSTIFICTAION_URI_COLUMN_NAME + " VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL " +
            		")");
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDBException ("Error creating the tables ", e);
		}
	}
    
    //profileURL can be null    
    public Set<Xref> mapID(Xref ref, String profileURL, DataSource... tgtDataSource) throws BridgeDBException {
        if (tgtDataSource == null || tgtDataSource.length == 0){
            return mapIDSingleSource(ref, profileURL, null);
        }
        if (tgtDataSource.length == 1){
            return mapIDSingleSource(ref, profileURL, tgtDataSource[0]);
        }
        HashSet<Xref> results = new HashSet<Xref>();
        for (DataSource dataSource: tgtDataSource){
            results.addAll(mapIDSingleSource(ref, profileURL, dataSource));
        }
        return results;
    }
    
    //tgtDataSource can be null
    //profileURL can be null
    public Set<Xref> mapIDSingleSource(Xref ref, String profileURL, DataSource tgtDataSource) throws BridgeDBException {
        if (badXref(ref)) {
            logger.warn("mapId called with a badXref " + ref);
            return new HashSet<Xref>();
        }
        StringBuilder query = startMappingQuery(ref);
        appendMappingFromAndWhere(query, ref, profileURL, tgtDataSource);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Set<Xref> results = resultSetToXrefSet(rs);
        if (ref.getDataSource().equals(tgtDataSource)){
             results.add(ref);
        }
        return results;
    }
    
    public Set<Mapping> mapFull (Xref sourceXref, String profileURL) throws BridgeDBException{
        if (badXref(sourceXref)) {
            logger.warn("mapId called with a badXref " + sourceXref);
            return new HashSet<Mapping>();
        }
        StringBuilder query = startMappingQuery(sourceXref);
        appendMappingFromAndWhere(query, sourceXref, profileURL, null);
        appendMappingInfo(query);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Set<Mapping> results = resultSetToMappingSet(sourceXref, rs);
        results.add(new Mapping(sourceXref.getId(), sourceXref.getDataSource().getSystemCode()));
        return results;
    }
    
    //tgtDataSource can be null
    //profileURL can be null
	public Set<Mapping> mapFull (Xref sourceXref, String profileURL, DataSource tgtDataSource) throws BridgeDBException{
        if (badXref(sourceXref)) {
            logger.warn("mapId called with a badXref " + sourceXref);
            return new HashSet<Mapping>();
        }
        StringBuilder query = startMappingQuery(sourceXref);
        appendMappingInfo(query);
        appendMappingFromAndWhere(query, sourceXref, profileURL, tgtDataSource);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
        Set<Mapping> results = resultSetToMappingSet(sourceXref, rs);
        if (sourceXref.getDataSource().equals(tgtDataSource)){
            results.add(new Mapping(sourceXref.getId(), tgtDataSource.getSystemCode()));
        }
       return results;
    }
 
    //tgtDataSources can be null, empty or length1
    //profileURL can be null
    public Set<Mapping> mapFull (Xref ref, String profileURL, Collection<DataSource> tgtDataSources) 
            throws BridgeDBException{
        if (tgtDataSources == null || tgtDataSources.isEmpty()){
            return mapFull (ref, profileURL);
        } else {
            Set<Mapping> results = new HashSet<Mapping>();
            for (DataSource tgtDataSource: tgtDataSources){
                results.addAll(mapFull(ref, profileURL, tgtDataSource));
            }
            return results;
        }
    }

    @Override
    public Map<String, Set<String>> mapURL(Collection<String> URLs, String profileURL, String... targetURISpaces) 
            throws BridgeDBException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
        for (String ref:URLs){
            Set<String> mapped = this.mapURL(ref, profileURL, targetURISpaces);
            results.put(ref, mapped);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String URL, String profileURL, String... targetURISpaces) throws BridgeDBException {
        String id = getId(URL);
        String uriSpace = getUriSpace(URL);
        String sysCode = getSysCode(uriSpace);
        String[] targetSysCodes = getSysCodes(targetURISpaces);
        Set<Mapping> mappings = doMapping(id, sysCode, profileURL, targetSysCodes);
        Set<String> results = new HashSet<String>();
        for (Mapping mapping: mappings){
            results.addAll(getURIs(mapping.getTargetId(), mapping.getTargetSysCode(), targetURISpaces));
        }
        logResults(results, URL, targetURISpaces);
        return results;
    }

    @Override
    public Map<Xref, Set<String>> mapToURLs(Collection<Xref> srcXrefs, String profileURL, String... targetURISpaces) 
            throws BridgeDBException {
        HashMap<Xref, Set<String>> results = new HashMap<Xref, Set<String>>();
        for (Xref ref:srcXrefs){
            Set<String> mapped = this.mapToURLs(ref, profileURL, targetURISpaces);
            results.put(ref, mapped);
        }
        return results;
    }
    
    @Override
    public Set<String> mapToURLs(Xref ref, String profileURL, String... targetURISpaces) throws  BridgeDBException {
        String id = ref.getId();
        String sysCode = ref.getDataSource().getSystemCode();
        String[] targetSysCodes = getSysCodes(targetURISpaces);
        Set<Mapping> mappings = doMapping(id, sysCode, profileURL, targetSysCodes);
        Set<String> results = new HashSet<String>();
        for (Mapping mapping: mappings){
            results.addAll(getURIs(mapping.getTargetId(), mapping.getTargetSysCode(), targetURISpaces));
        }
        this.logResults(results, ref.toString(), targetSysCodes);
        return results;
    }

    private StringBuilder startMappingQuery(Xref ref){
        StringBuilder query = new StringBuilder("SELECT ");
            query.append(TARGET_ID_COLUMN_NAME);
                query.append(" as ");
                query.append(ID_COLUMN_NAME);
                query.append(", ");
            query.append(TARGET_DATASOURCE_COLUMN_NAME);
                query.append(" as ");
                query.append(SYSCODE_COLUMN_NAME);
        return query;
    }
    
    private void appendMappingInfo(StringBuilder query){
        query.append(", ");
        query.append(MAPPING_SET_ID_COLUMN_NAME);
        query.append(", ");
        query.append(PREDICATE_COLUMN_NAME);
        query.append(", ");
        query.append(MAPPING_TABLE_NAME);
        query.append(".");
        query.append(ID_COLUMN_NAME);
        query.append(" as ");
    }
 
    private void appendMappingFromAndWhere(StringBuilder query, Xref ref, String profileURL, DataSource tgtDataSource) 
            throws BridgeDBException{
        query.append(" FROM ");
            query.append(MAPPING_TABLE_NAME);
                query.append(", ");
            query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
            query.append(MAPPING_SET_ID_COLUMN_NAME);
                query.append(" = ");
                query.append(MAPPING_SET_DOT_ID_COLUMN_NAME);
        appendSourceXref(query, ref);
        if (tgtDataSource != null){
            query.append(" AND ");
                query.append(TARGET_DATASOURCE_COLUMN_NAME);
                query.append(" = '");
                query.append(insertEscpaeCharacters(tgtDataSource.getSystemCode()));
                query.append("' ");   
        }
        appendProfileClause(query, profileURL);
    }
    
    /**
     * Adds the WHERE clause conditions for ensuring that the returned mappings
     * are from active linksets.
     * 
     * @param query Query with WHERE clause started
     * @param profileURL URL of the profile to use
     * @throws BridgeDbSqlException if the profile does not exist
     */
    private void appendProfileClause(StringBuilder query, String profileURL) throws BridgeDBException {
        if (profileURL == null){
            return;
        }
        int profileID = extractIDFromURI(profileURL);
        if (profileID != 0) {
            String profileJustificationQuery = "SELECT justificationURI FROM profileJustifications WHERE profileId = ";
            try {
        		Statement statement = this.createStatement();    		
        		ResultSet rs = statement.executeQuery(profileJustificationQuery + "'" + profileID + "'");
        		if (!rs.next()) throw new BridgeDBException("Unknown profile identifier " + profileURL);
        		query.append(" AND mappingSet.justification IN (");
        		do {
        			query.append("'").append(rs.getString("justificationURI")).append("'");
        			if (!rs.isLast()) query.append(", ");
        		} while (rs.next());
        		query.append(")");
        	} catch (SQLException ex) {
        		throw new BridgeDBException("Error retrieving profile justifications for profileId " + profileURL, ex);
        	}
        }
	}

	private int extractIDFromURI(String profileURL) throws BridgeDBException {
		try {
			URI profileURI = new URIImpl(profileURL);
			if (!profileURI.getNamespace().equals(RdfConfig.getProfileBaseURI())) {
 				throw new BridgeDBException("Invalid namespace for profile URI: " + profileURL);
			}
			int profileID = Integer.parseInt(profileURI.getLocalName());
			return profileID;
		} catch (IllegalArgumentException e) {
			throw new BridgeDBException("Invalid URI form for a profileURL: " + profileURL);
		}
	}

    @Override
    public Set<Mapping> mapURLFull(String URL, String profileURL, String... targetURISpaces) throws BridgeDBException {
        String id = getId(URL);
        String uriSpace = getUriSpace(URL);
        String sysCode = getSysCode(uriSpace);
        
        String[] targetSysCodes = getSysCodes(targetURISpaces);
        Set<Mapping> results = doMapping(id, sysCode, profileURL, targetSysCodes);
        for (Mapping mapping:results){
            mapping.addSourceURL(URL);
            addTargetURIs(mapping, targetURISpaces);
        }
        this.logResults(results, URL, targetSysCodes);
        return results;       
    }

    @Override
    public Set<Mapping> mapToURLsFull(Xref ref, String profileURL, String... targetURISpaces) throws  BridgeDBException {
        String[] targetSysCodes = getSysCodes(targetURISpaces);
        Set<Mapping> results = doMapping(ref.getId(), ref.getDataSource().getSystemCode(), profileURL, targetSysCodes);
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
        String prefix = getUriSpace(uri);
        String id = getId(uri);
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(DATASOURCE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(URL_TABLE_NAME);
        query.append(" WHERE '");
        query.append(insertEscpaeCharacters(prefix));
        query.append("' = ");
        query.append(PREFIX_COLUMN_NAME);
        query.append(" AND '");
        query.append(POSTFIX_COLUMN_NAME);
        query.append("' = ''");
                 
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
                if (rs.next()){
                     return toXrefUsingLike(uri);
                }
                return new Xref(id, dataSource);
            } else {
                return toXrefUsingLike(uri);
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to get uriSpace. " + query, ex);
        }        
    }
   
    private Xref toXrefUsingLike(String uri) throws BridgeDBException {
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
        query.append(insertEscpaeCharacters(uri));
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
                String prefix = rs.getString(PREFIX_COLUMN_NAME);
                String postfix = rs.getString(POSTFIX_COLUMN_NAME);
                while(rs.next()){
                    String newPrefix = rs.getString(PREFIX_COLUMN_NAME);
                    String newPostfix = rs.getString(POSTFIX_COLUMN_NAME);
                    //If there is more than one result take the most specific.
                    if (newPrefix.length() > prefix.length() || newPostfix.length() > postfix.length()){
                        prefix = newPrefix;
                        sysCode = rs.getString(DATASOURCE_COLUMN_NAME);
                        postfix = newPostfix;
                    }
                }
                DataSource dataSource = DataSource.getBySystemCode(sysCode);
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
        int numberOfProfiles = getNumberOfProfiles();
        String linkSetQuery = "SELECT count(distinct(mappingSet.id)) as numberOfMappingSets, "
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
                		numberOfSourceDataSources, numberOfPredicates, 
                		numberOfTargetDataSources, numberOfProfiles);
            } else {
                System.err.println(linkSetQuery);
                throw new BridgeDBException("no Results for query. " + linkSetQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + linkSetQuery, ex);
        }
    }

    private int getNumberOfProfiles() throws BridgeDBException {
    	String profileCountQuery = "SELECT count(*) as numberOfProfiles " +
    			"FROM profile";
    	Statement statement = this.createStatement();
    	try {
    		ResultSet rs = statement.executeQuery(profileCountQuery);
    		if (rs.next()) {
    			return rs.getInt("numberOfProfiles");
    		} else {
    			System.err.println(profileCountQuery);
                throw new BridgeDBException("No Results for query. " + profileCountQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + profileCountQuery, ex);
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
    public List<ProfileInfo> getProfiles() throws BridgeDBException {
    	String query = ("SELECT profileId, name, createdOn, createdBy " +
    			"FROM profile");
    	Statement statement = this.createStatement();
    	List<ProfileInfo> profiles = new ArrayList<ProfileInfo>();
    	try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				int profileId = rs.getInt("profileId");
				String name = rs.getString("name");
				String createdOn = rs.getString("createdOn");
				String createdBy = rs.getString("createdBy");
				Set<String> justifications = getJustificationsForProfile(profileId);
				String profileURL = RdfConfig.getProfileURI(profileId);
				profiles.add(new ProfileInfo(profileURL, name, createdOn, createdBy, justifications));
			}
		} catch (SQLException e) {
			throw new BridgeDBException("Unable to retrieve profiles.", e);
		}
    	return profiles;
    }
    
    @Override
    public ProfileInfo getProfile(String profileURI) throws BridgeDBException {
    	int profileID = extractIDFromURI(profileURI);
    	String query = ("SELECT profileId, name, createdOn, createdBy " +
    			"FROM profile WHERE profileId = " + profileID);
    	Statement statement = this.createStatement();
    	ProfileInfo profile = null;
		try {
			ResultSet rs = statement.executeQuery(query);
			if (!rs.next()) {
				throw new BridgeDBException("No profile with the URI " + profileURI);
			}
			do {
				int profileId = rs.getInt("profileId");
				String name = rs.getString("name");
				String createdOn = rs.getString("createdOn");
				String createdBy = rs.getString("createdBy");
				Set<String> justifications = getJustificationsForProfile(profileId);
				profile = new ProfileInfo(profileURI, name, createdOn, createdBy, justifications);
			} while (rs.next());
		} catch (SQLException e) {
			throw new BridgeDBException("Unable to retrieve profiles.", e);
		}
    	return profile;
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
    
    private Set<String> getJustificationsForProfile(int profileId) throws BridgeDBException {
    	String query = ("SELECT justificationURI " +
    			"FROM profileJustifications " +
    			"WHERE profileId = " + profileId);
    	Statement statement = this.createStatement();
    	Set<String> justifications = new HashSet<String>();
    	try {
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String justification = rs.getString("justificationURI");
				justifications.add(justification);
			}
		} catch (SQLException e) {
			throw new BridgeDBException("Unable to retrieve profile justifications.", e);
		}
    	return justifications;
	}

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

    @Override
    public int registerMappingSet(String sourceUriSpace, String predicate, String justification, String targetUriSpace, 
            boolean symetric, boolean transative) throws BridgeDBException {
        DataSource source = getDataSource(sourceUriSpace);
        DataSource target = getDataSource(targetUriSpace);      
        return registerMappingSet(source, predicate, justification, target, symetric, transative);
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

    private Set<Mapping> resultSetToMappingSet(Xref sourceXref, ResultSet rs) throws BridgeDBException {
        HashSet<Mapping> results = new HashSet<Mapping>();
        try {
            while (rs.next()){
                Integer mappingId = rs.getInt("mappingId"); 
                String targetId = rs.getString("id");
                String targetSysCode = rs.getString("sysCode");
                Integer mappingSetId = rs.getInt("mappingSetId");
                String predicate = rs.getString("predicate");
                Mapping urlMapping = new Mapping (mappingId, sourceXref.getId(), 
                        sourceXref.getDataSource().getSystemCode(), predicate, targetId, targetSysCode, mappingSetId);       
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

    public int registerProfile(String name, String createdOn, String createdBy, 
    		List<String> justificationUris) 
            throws BridgeDBException {
    	//TODO: Need to validate that createdOn is a date
    	//TODO: Need to validate that createdBy is a URI
    	//TODO: Need to validate that justifcationUris is a List of URIs
    	startTransaction();
    	int profileId = createProfile(name, createdOn, createdBy);
    	insertJustifications(profileId, justificationUris);
    	commitTransaction();
    	return profileId;
    }

	private int createProfile(String name, String createdOn, String createdBy)
			throws BridgeDBException {
		String insertStatement = "INSERT INTO profile "
                    + "(name, createdOn, createdBy) " 
                    + "VALUES (" 
                    + "'" + name + "', "
                    + "'" + createdOn + "', " 
                    + "'" + createdBy + "')";
		int profileId = 0;
        try {
        	Statement statement = createStatement();
            statement.executeUpdate(insertStatement, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next())
            {
            	profileId = rs.getInt(1);
            } else {
            	rollbackTransaction();
            	throw new BridgeDBException ("No result registering new profile " + insertStatement);
            }            
        } catch (BridgeDBException ex) {
        	rollbackTransaction();
        	throw ex;
        } catch (SQLException ex) {
        	rollbackTransaction();
            throw new BridgeDBException ("Error registering new profile " + insertStatement, ex);
        }
        return profileId;
	}
	
	private void insertJustifications(int profileId,
			List<String> justificationUris) throws BridgeDBException {
		String sql = "INSERT INTO profileJustifications " +
				"(profileId, justificationURI) " +
				"VALUES ( " + profileId + ", " + "?)";
		try {
			PreparedStatement statement = createPreparedStatement(sql);
			for (String uri : justificationUris) {
				statement.setString(1, uri);
				statement.execute();
			}
		} catch (BridgeDBException ex) {
			rollbackTransaction();
			throw ex;
		} catch (SQLException ex) {
			rollbackTransaction();
			throw new BridgeDBException("Error inserting justification.", ex);
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

    private Set<Mapping> doMapping(String id, String sysCode, String profileURL, String... tgtSysCodes) throws BridgeDBException {
        Set<Mapping> mappings;
        if (id == null || sysCode == null){
            mappings = new HashSet<Mapping>();
        } else {
            mappings = doMappingQuery(id, sysCode, profileURL, tgtSysCodes);
        }
        addMapToSelf(mappings, id, sysCode, tgtSysCodes);
        return mappings;
    }

    private Set<Mapping> doMappingQuery(String id, String sysCode, String profileURL, String... tgtSysCodes) 
            throws BridgeDBException {
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
        appendProfileClause(query, profileURL);
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
