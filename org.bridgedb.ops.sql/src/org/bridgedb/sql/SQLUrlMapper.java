// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.url.UriSpaceMapper;
import org.bridgedb.url.URLListener;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;

/**
 * Implements the URLMapper and URLListener interfaces using SQL.
 *
 * Takes into accounts the specific factors for teh SQL version being used.
 *
 * @author Christian
 */
public class SQLUrlMapper extends SQLIdMapper implements URLMapper, URLListener {

    private static final int URI_SPACE_LENGTH = 100;	
	
    /**
     * Creates a new URLMapper including BridgeDB implementation based on a connection to the SQL Database.
     *
     * @param dropTables Flag to determine if any existing tables should be dropped and new empty tables created.
     * @param sqlAccess The connection to the actual database. This could be MySQL, Virtuoso ect.
     *       It could also be the live database, the loading database or the test database.
     * @param specific Code to hold the things that are different between different SQL implementaions.
     * @throws BridgeDbSqlException
     */
     public SQLUrlMapper(boolean dropTables, SQLAccess sqlAccess, SQLSpecific specific) throws IDMapperException{
        super(dropTables, sqlAccess, specific);
        if (dropTables){
            try {
                Map<String,DataSource> mappings = UriSpaceMapper.getUriSpaceMappings();
                for (String uriSpace:mappings.keySet()){
                    this.registerUriSpace(mappings.get(uriSpace), uriSpace);
                }
            } catch (IDMapperException ex) {
                throw new BridgeDbSqlException("Error setting up urispace mappings");
            }
        }
    }   
    
    @Override
	protected void dropSQLTables() throws BridgeDbSqlException
	{
        super.dropSQLTables();
 		dropTable("url");
 		dropTable("profileJustifications");
 		dropTable("profile");
    }
 
    @Override
	protected void createSQLTables() throws BridgeDbSqlException
	{
        super.createSQLTables();
		try 
		{
			Statement sh = createStatement();
            sh.execute("CREATE TABLE url"
                    + "  (  dataSource VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,   "
                    + "     uriSpace VARCHAR(" + URI_SPACE_LENGTH + ")  "
                    + "  ) ");
            sh.execute("CREATE TABLE profile ( " +
            		"profileId INT " + autoIncrement + " PRIMARY KEY, " +
            		"name VARCHAR(" + FULLNAME_LENGTH + ") NOT NULL, " +
            		"createdOn DATETIME, " +
            		"createdBy VARCHAR(" + PREDICATE_LENGTH + ") " +
            		")");
            sh.execute("CREATE TABLE profileJustifications ( " +
            		"profileId INT NOT NULL, " +
            		"justificationURI VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL " +
//            		"CONSTRAINT fk_profileId FOREIGN KEY (profileId) REFERENCES profile(profileId) ON DELETE CASCADE " +
            		")");
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}
    
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> URLs, String profileURL, String... targetURISpaces) 
            throws IDMapperException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
        for (String ref:URLs){
            Set<String> mapped = this.mapURL(ref, profileURL, targetURISpaces);
            results.put(ref, mapped);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String URL, String profileURL, String... targetURISpaces) throws IDMapperException {
        StringBuilder query = new StringBuilder("SELECT targetId as id, target.uriSpace as uriSpace ");
        finishMappingQuery(query, URL, profileURL, targetURISpaces); 
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        Set<String> results = resultSetToURLsSet(rs);
        if (targetURISpaces.length == 0){
           results.add(URL); 
        } else {
            String uriSpace = getUriSpace(URL);
            for (String targetURISpace: targetURISpaces){
                if (uriSpace.equals(targetURISpace)){
                    results.add(URL);
                }
            }
        }
        return results;       
    }

    /**
     * Adds the FROM and Where clauses to queries to get mappings.
     *
     * @param query Query with the select clause only
	 * @param sourceURL the sourceURL to get mappings/cross-references for.
     * @param profileURL the URL of the profile to use
     * @param target URISpaces
     * @throws BridgeDbSqlException profile does not exist
     */
    private void finishMappingQuery(StringBuilder query, String sourceURL, String profileURL, 
    		String... targetURISpaces) throws IDMapperException {
        //System.out.println("mapping: " + sourceURL);
        String id = getId(sourceURL);
        String uriSpace = getUriSpace(sourceURL);
        query.append("FROM mapping, mappingSet, url as source, url as target ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mappingSet.sourceDataSource = source.dataSource ");
        query.append("AND mappingSet.targetDataSource = target.dataSource ");
        query.append("AND sourceId = '");
            query.append(id);
            query.append("' ");
        query.append("AND source.uriSpace = '");
            query.append(uriSpace);
            query.append("' ");
         if (targetURISpaces.length > 0){    
            query.append("AND ( target.uriSpace = '");
                query.append(targetURISpaces[0]);
                query.append("' ");
            for (int i = 1; i < targetURISpaces.length; i++){
                query.append("OR target.uriSpace = '");
                    query.append(targetURISpaces[i]);
                    query.append("'");
            }
            query.append(")");
        }
        addProfileClause(query, profileURL);
    }

    /**
     * Adds the WHERE clause conditions for ensuring that the returned mappings
     * are from active linksets.
     * 
     * @param query Query with WHERE clause started
     * @param profileURL URL of the profile to use
     * @throws BridgeDbSqlException if the profile does not exist
     */
    private void addProfileClause(StringBuilder query, String profileURL) throws IDMapperException {
        String profileJustificationQuery = "SELECT justificationURI FROM profileJustifications WHERE profileId = ";
        int profileID = extractIDFromURI(profileURL);
        if (profileID != 0) {
        	try {
        		Statement statement = this.createStatement();    		
        		ResultSet rs = statement.executeQuery(profileJustificationQuery + "'" + profileID + "'");
        		if (!rs.next()) throw new BridgeDbSqlException("Unknown profile identifier " + profileURL);
        		query.append(" AND mappingSet.justification IN (");
        		do {
        			query.append("'").append(rs.getString("justificationURI")).append("'");
        			if (!rs.isLast()) query.append(", ");
        		} while (rs.next());
        		query.append(")");
        	} catch (SQLException ex) {
        		throw new BridgeDbSqlException("Error retrieving profile justifications for profileId " + profileURL, ex);
        	}
        }
	}

	private int extractIDFromURI(String profileURL) throws IDMapperException {
		try {
			URI profileURI = new URIImpl(profileURL);
			if (!profileURI.getNamespace().equals(RdfConfig.getProfileBaseURI())) {
 				throw new BridgeDbSqlException("Invalid namespace for profile URI: " + profileURL);
			}
			int profileID = Integer.parseInt(profileURI.getLocalName());
			return profileID;
		} catch (IllegalArgumentException e) {
			throw new BridgeDbSqlException("Invalid URI form for a profileURL: " + profileURL);
		}
	}

	@Override
    public Set<URLMapping> mapURLFull(String URL, String profileURL, String... targetURISpaces) throws IDMapperException {    	
        StringBuilder query = new StringBuilder("SELECT mapping.id as mappingId, targetId as id, predicate, ");
        query.append("mappingSet.id as mappingSetId, target.uriSpace as uriSpace ");
        finishMappingQuery(query, URL, profileURL, targetURISpaces); 
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        Set<URLMapping> results = resultSetToURLMappingSet(URL, rs);
        URLMapping toSelf = new URLMapping(null, URL, null, URL, null);
        if (targetURISpaces.length == 0){
           results.add(toSelf); 
        } else {
            String uriSpace = getUriSpace(URL);
            for (String targetURISpace: targetURISpaces){
                if (uriSpace.equals(targetURISpace)){
                    results.add(toSelf);
                }
            }
        }
        return results;       
    }

    @Override
    public boolean uriExists(String URL) throws BridgeDbSqlException {
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
        query.append("AND source.uriSpace = '");
            query.append(uriSpace);
            query.append("' ");
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
    public Set<String> urlSearch(String text, int limit) throws BridgeDbSqlException {
        //ystem.out.println("mapping: " + sourceURL);
        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT ");
        //TODO get DISTINCT working on Virtuosos
        appendTopConditions(query, 0, limit); 
        query.append(" targetId as id, target.uriSpace as uriSpace ");
        query.append("FROM mapping, mappingSet, url as target ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mappingSet.targetDataSource = target.dataSource ");
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
        Set<String> results = resultSetToURLsSet(rs);
        return results;       
    }
    
    @Override
    public Xref toXref(String URL) throws BridgeDbSqlException {
        String id = getId(URL);
        String uriSpace = getUriSpace(URL);
        DataSource dataSource = getDataSource(uriSpace);
        return new Xref(id, dataSource);
    }

    @Override
    public URLMapping getMapping(int id) throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder("SELECT mapping.id, sourceId, targetId, predicate, ");
        query.append("mappingSet.id, source.uriSpace, target.uriSpace ");
        query.append("FROM mapping, mappingSet, url as source, url as target ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mappingSet.sourceDataSource = source.dataSource ");
        query.append("AND mappingSet.targetDataSource = target.dataSource ");
        query.append("AND mapping.id = ");
            query.append(id);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        return resultSetToURLMapping(rs);
    }

    @Override
    public Set<String> getSampleSourceURLs() throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder("SELECT DISTINCT ");
        //TODO get DISTINCT working on Virtuosos
        this.appendTopConditions(query, 0, 5);
        query.append("sourceId as id, source.uriSpace as uriSpace ");
        query.append("FROM mapping, mappingSet, url as source ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mappingSet.sourceDataSource = source.dataSource ");
        this.appendLimitConditions(query, 0, 5);
        //ystem.out.println(query);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        return resultSetToURLsSet(rs);
    }

    @Override
    public OverallStatistics getOverallStatistics() throws BridgeDbSqlException {
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
                throw new BridgeDbSqlException("no Results for query. " + linkSetQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + linkSetQuery, ex);
        }
    }

    private int getNumberOfProfiles() throws BridgeDbSqlException {
    	String profileCountQuery = "SELECT count(*) as numberOfProfiles " +
    			"FROM profile";
    	Statement statement = this.createStatement();
    	try {
    		ResultSet rs = statement.executeQuery(profileCountQuery);
    		if (rs.next()) {
    			return rs.getInt("numberOfProfiles");
    		} else {
    			System.err.println(profileCountQuery);
                throw new BridgeDbSqlException("No Results for query. " + profileCountQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + profileCountQuery, ex);
        }      
	}

	private int getMappingsCount() throws BridgeDbSqlException{
        String linkQuery = "SELECT count(*) as numberOfMappings "
                + "FROM mapping";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(linkQuery);
            if (rs.next()){
                return rs.getInt("numberOfMappings");
            } else {
                System.err.println(linkQuery);
                throw new BridgeDbSqlException("No Results for query. " + linkQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + linkQuery, ex);
        }      
    }

    @Override
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws IDMapperException {
        String query = "SELECT *"
                + " FROM mappingSet "
                + "WHERE id = " + mappingSetId;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            List<MappingSetInfo> results = resultSetToMappingSetInfos(rs);
            if (results.isEmpty()){
                throw new IDMapperException ("No mappingSet found with id " + mappingSetId);
            }
            if (results.size() > 1){
                throw new IDMapperException (results.size() + " mappingSets found with id " + mappingSetId);
            }
            return results.get(0);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public List<MappingSetInfo> getMappingSetInfos() throws BridgeDbSqlException {
        String query = ("SELECT * FROM mappingSet ");
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToMappingSetInfos(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
    }
    
    @Override
    public Set<String> getUriSpaces(String dataSource) throws BridgeDbSqlException {
        String query = ("SELECT uriSpace FROM url "
                + " WHERE dataSource = '" + dataSource + "'");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
        return resultsetToUriSpaces(rs);
    }
    
    @Override
    public Set<String> getSourceUriSpace(int mappingSetId) throws IDMapperException {
        String query = ("SELECT uriSpace FROM url, mappingSet  "
                + " WHERE dataSource = sourceDataSource"
                + " AND mappingSet.id = " + mappingSetId);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
        return resultsetToUriSpaces(rs);
    }
    
    @Override
    public List<ProfileInfo> getProfiles() throws IDMapperException {
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
			Reporter.report(e.getLocalizedMessage());
			throw new BridgeDbSqlException("Unable to retrieve profiles.", e);
		}
    	return profiles;
    }
    
    @Override
    public ProfileInfo getProfile(String profileURI) throws IDMapperException {
    	int profileID = extractIDFromURI(profileURI);
    	String query = ("SELECT profileId, name, createdOn, createdBy " +
    			"FROM profile WHERE profileId = " + profileID);
    	Statement statement = this.createStatement();
    	ProfileInfo profile = null;
		try {
			ResultSet rs = statement.executeQuery(query);
			if (!rs.next()) {
				throw new BridgeDbSqlException("No profile with the URI " + profileURI);
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
			Reporter.report(e.getLocalizedMessage());
			throw new BridgeDbSqlException("Unable to retrieve profiles.", e);
		}
    	return profile;
    }

    @Override
    public Set<String> getTargetUriSpace(int mappingSetId) throws IDMapperException {
        String query = ("SELECT uriSpace FROM url, mappingSet  "
                + " WHERE dataSource = targetDataSource"
                + " AND mappingSet.id = " + mappingSetId);
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
        return resultsetToUriSpaces(rs);
    }


    // **** URLListener Methods
    
    private Set<String> getJustificationsForProfile(int profileId) throws BridgeDbSqlException {
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
			Reporter.report(e.getLocalizedMessage());
			throw new BridgeDbSqlException("Unable to retrieve profile justifications.", e);
		}
    	return justifications;
	}

	@Override
    public void registerUriSpace(DataSource source, String uriSpace) throws BridgeDbSqlException {
        checkDataSourceInDatabase(source);
        String sysCode = getSysCode(uriSpace);
        if (sysCode != null){
            if (source.getSystemCode().equals(sysCode)) return; //Already known so fine.
            throw new BridgeDbSqlException ("UriSpace " + uriSpace + " already mapped to " + sysCode 
                    + " Which does not match " + source.getSystemCode());
        }
        String query = "INSERT INTO url (dataSource, uriSpace) VALUES "
                + " ('" + source.getSystemCode() + "', "
                + "  '" + uriSpace + "'"
                + ")";  
        Statement statement = createStatement();
        try {
            int changed = statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new BridgeDbSqlException ("Error inserting UriSpace ", ex, query);
        }
    }

    @Override
    public int registerMappingSet(String sourceUriSpace, String predicate, 
    		String justification, String targetUriSpace, boolean symetric, 
    		boolean transative) 
            throws BridgeDbSqlException {
        DataSource source = getDataSource(sourceUriSpace);
        DataSource target = getDataSource(targetUriSpace);      
        return registerMappingSet(source, predicate, justification, target, symetric, transative);
    }

    @Override
    public void insertURLMapping(String sourceURL, String targetURL, int mappingSet, boolean symetric) throws IDMapperException {
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
     * @throws BridgeDbSqlException
     */
    private Set<String> resultSetToURLsSet(ResultSet rs) throws BridgeDbSqlException {
        Set<String> results = new HashSet<String>();
        try {
            while (rs.next()){
                String id = rs.getString("id");
                String uriSpace = rs.getString("uriSpace");
                String uri = uriSpace + id;
                results.add(uri);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

   /**
     * Generates a set of UriSpaces a ResultSet.
     *
     * This implementation just extracts the URISpace
     *
     * @param rs Result Set holding the information
     * @return UriSpaces generated
     * @throws BridgeDbSqlException
     */
     private Set<String> resultsetToUriSpaces(ResultSet rs) throws BridgeDbSqlException {
        try {
            HashSet<String> uriSpaces = new HashSet<String>();
            while (rs.next()){
                uriSpaces.add(rs.getString("uriSpace"));
            }
            return uriSpaces;
       } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

   /**
     *
     * Generates a set of mappings from a ResultSet.
     *
     * This implementation just concats the URISpace and Id
     *
     * Ideally this would be replaced by a method from Identifiers.org
     *    based on their knoweldge or ULI/URLs
     * This may require the method to be exstended with the Target NameSpaces.
     *
     * @param sourceURL URL to be mapped to
     * @param rs Result Set holding the information
     * @return Set of mappings from the source to thie URL in the results.
     * @throws BridgeDbSqlException
     */
    private Set<URLMapping> resultSetToURLMappingSet(String sourceURL, ResultSet rs) throws BridgeDbSqlException {
        HashSet<URLMapping> results = new HashSet<URLMapping>();
        try {
            while (rs.next()){
                Integer mappingId = rs.getInt("mappingId"); 
                String targetURL = rs.getString("uriSpace") + rs.getString("id");
                Integer mappingSetId = rs.getInt("mappingSetId");
                String predicate = rs.getString("predicate");
                URLMapping urlMapping = new URLMapping (mappingId, sourceURL, predicate, targetURL, mappingSetId);       
                results.add(urlMapping);
            }
            return results;
       } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

    /**
     * Generates a single mapping from an id.
     *
     * This method (an probaly the calling methods) needs replacing with one created by identifiers.org
     *
     * @param rs Result set with exactky one result
     * @return The mapping or null
     * @throws BridgeDbSqlException
     */
    private URLMapping resultSetToURLMapping(ResultSet rs) throws BridgeDbSqlException {
        try {
            URLMapping urlMapping;
            if (rs.next()){
                Integer mappingId = rs.getInt("mapping.id"); 
                String sourceURL = rs.getString("source.uriSpace") + rs.getString("sourceId");
                String targetURL = rs.getString("target.uriSpace") + rs.getString("targetId");
                Integer mappingSetId = rs.getInt("mappingSet.id");
                String predicate = rs.getString("predicate");
                urlMapping = new URLMapping (mappingId, sourceURL, predicate, targetURL, mappingSetId);       
            } else {
                return null;
            }
            while (rs.next()){
                String sourceURL = rs.getString("source.uriSpace") + rs.getString("sourceId");
                urlMapping.addSourceURL(sourceURL);
                String targetURL = rs.getString("target.uriSpace") + rs.getString("targetId"); 
                urlMapping.addTargetURL(targetURL);
            }
            return urlMapping;
       } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

    /**
     * Generates the meta info from the result of a query
     * @param rs
     * @return
     * @throws BridgeDbSqlException
     */
    private List<MappingSetInfo> resultSetToMappingSetInfos(ResultSet rs ) throws BridgeDbSqlException{
        ArrayList<MappingSetInfo> results = new ArrayList<MappingSetInfo>();
        try {
            while (rs.next()){
                Integer count = rs.getInt("mappingCount");
                results.add(new MappingSetInfo(rs.getString("id"), rs.getString("sourceDataSource"), 
                        rs.getString("predicate"), rs.getString("targetDataSource"), count, 
                        rs.getBoolean("isTransitive")));
            }
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
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
     * @throws BridgeDbSqlException
     */
    private String getSysCode(String uriSpace) throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT dataSource ");
        query.append("FROM url ");
        query.append("WHERE uriSpace = '");
        query.append(uriSpace);
        query.append("' ");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        try {
            if (rs.next()){
                return rs.getString("dataSource");
            }
            return null;
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to get uriSpace. " + query, ex);
        }    
    }

    /**
     * Returns the DataSource associated with a URISpace.
     *
     * Throws an exception if the URISpace is unknown.
     *
     * @param uriSpace A Known URISpace
     * @return A DataSource. Never null, instead an Exception is thrown
     * @throws BridgeDbSqlException For example if the uriSpace is not known.
     */
    private DataSource getDataSource(String uriSpace) throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT dataSource ");
        query.append("FROM url ");
        query.append("WHERE uriSpace = '");
            query.append(uriSpace);
            query.append("' ");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
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
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

    public int registerProfile(String name, String createdOn, String createdBy, 
    		List<String> justificationUris) 
            throws BridgeDbSqlException {
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
			throws BridgeDbSqlException {
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
            	throw new BridgeDbSqlException ("No result registering new profile " + insertStatement);
            }            
        } catch (BridgeDbSqlException ex) {
        	rollbackTransaction();
        	throw ex;
        } catch (SQLException ex) {
        	rollbackTransaction();
            throw new BridgeDbSqlException ("Error registering new profile " + insertStatement, ex);
        }
        return profileId;
	}
	
	private void insertJustifications(int profileId,
			List<String> justificationUris) throws BridgeDbSqlException {
		String sql = "INSERT INTO profileJustifications " +
				"(profileId, justificationURI) " +
				"VALUES ( " + profileId + ", " + "?)";
		try {
			PreparedStatement statement = createPreparedStatement(sql);
			for (String uri : justificationUris) {
				statement.setString(1, uri);
				statement.execute();
			}
		} catch (BridgeDbSqlException ex) {
			rollbackTransaction();
			throw ex;
		} catch (SQLException ex) {
			rollbackTransaction();
			throw new BridgeDbSqlException("Error inserting justification.", ex);
		}
	}


}
