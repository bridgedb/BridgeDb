package org.bridgedb.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.MappingSetStatistics;
import org.bridgedb.url.UriSpaceMapper;
import org.bridgedb.url.URLListener;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.URLMapping;

/**
 *
 * @author Christian
 */
public class SQLUrlMapper extends SQLIdMapper implements URLMapper, URLListener {

    private static final int URI_SPACE_LENGTH = 100;

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
    
	protected void dropSQLTables() throws BridgeDbSqlException
	{
        super.dropSQLTables();
 		dropTable("url");
    }
 
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
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}
    
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, String... targetURISpaces) throws BridgeDbSqlException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
        for (String ref:sourceURLs){
            Set<String> mapped = this.mapURL(ref, targetURISpaces);
            results.put(ref, mapped);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String sourceURL, String... targetURISpaces) throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder("SELECT targetId as id, target.uriSpace as uriSpace ");
        finishMappingQuery(query, sourceURL, targetURISpaces); 
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        Set<String> results = resultSetToURLsSet(rs);
        if (targetURISpaces.length == 0){
           results.add(sourceURL); 
        } else {
            String uriSpace = getUriSpace(sourceURL);
            for (String targetURISpace: targetURISpaces){
                if (uriSpace.equals(targetURISpaces)){
                    results.add(sourceURL);
                }
            }
        }
        return results;       
    }

    private void finishMappingQuery(StringBuilder query, String sourceURL, String... targetURISpaces) {
        //ystem.out.println("mapping: " + sourceURL);
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
    }

    public Set<URLMapping> mapURLFull(String sourceURL, String... targetURISpaces) throws BridgeDbSqlException {
        StringBuilder query = new StringBuilder("SELECT mapping.id as mappingId, targetId as id, predicate, ");
        query.append("mappingSet.id as mappingSetId, target.uriSpace as uriSpace ");
        finishMappingQuery(query, sourceURL, targetURISpaces); 
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }    
        Set<URLMapping> results = resultSetToURLMappingSet(sourceURL, rs);
        URLMapping toSelf = new URLMapping(null, sourceURL, null, sourceURL, null);
        if (targetURISpaces.length == 0){
           results.add(toSelf); 
        } else {
            String uriSpace = getUriSpace(sourceURL);
            for (String targetURISpace: targetURISpaces){
                if (uriSpace.equals(targetURISpaces)){
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
        query.append("SELECT ");
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
        query.append("SELECT ");
        appendTopConditions(query, 0, limit); 
        query.append(" targetId as id, target.uriSpace as uriSpace ");
        query.append("FROM mapping, mappingSet, url as target ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mappingSet.targetDataSource = target.dataSource ");
        query.append("AND sourceId = '");
            query.append(text);
            query.append("' ");
        //use grouop by as do not know how to do distinct in Virtuoso
        query.append("GROUP BY targetId, target.uriSpace ");        
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
        StringBuilder query = new StringBuilder("SELECT ");
        this.appendTopConditions(query, 0, 5);
        query.append("sourceId as id, source.uriSpace as uriSpace ");
        query.append("FROM mapping, mappingSet, url as source ");
        query.append("WHERE mappingSetId = mappingSet.id ");
        query.append("AND mappingSet.sourceDataSource = source.dataSource ");
        this.appendLimitConditions(query, 0, 5);
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
    public MappingSetStatistics getMappingSetStatistics() throws BridgeDbSqlException {
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
                return new MappingSetStatistics(numberOfMappings, numberOfMappingSets, 
                        numberOfSourceDataSources, numberOfPredicates, numberOfTargetDataSources);
            } else {
                System.err.println(linkSetQuery);
                throw new BridgeDbSqlException("no Results for query. " + linkSetQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + linkSetQuery, ex);
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
    public Set<MappingSetInfo> getMappingSetInfos() throws BridgeDbSqlException {
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
    public Set<String> getUriSpaces(String sysCode) throws BridgeDbSqlException {
        String query = ("SELECT uriSpace FROM url "
                + " WHERE dataSource = '" + sysCode + "'");
        Statement statement = this.createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
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

    // **** URLListener Methods
    
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
    public int registerMappingSet(String sourceUriSpace, String predicate, String targetUriSpace, boolean symetric, boolean transative) 
            throws BridgeDbSqlException {
        DataSource source = getDataSource(sourceUriSpace);
        DataSource target = getDataSource(targetUriSpace);      
        return registerMappingSet(source, predicate, target, symetric, transative);
    }

    @Override
    public void insertURLMapping(String sourceURL, String targetURL, int mappingSet, boolean symetric) throws IDMapperException {
        String sourceId = getId(sourceURL);
        String targetId = getId(targetURL);
        this.insertLink(sourceId, targetId, mappingSet, symetric);
    }


    public final static String getUriSpace(String url){
        String prefix = null;
        url = url.trim();
        if (url.contains("#")){
            prefix = url.substring(0, url.lastIndexOf("#")+1);
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
    
    public final static String getId(String url){
        url = url.trim();
        if (url.contains("#")){
            return url.substring(url.lastIndexOf("#")+1, url.length());
        } else if (url.contains("/")){
            return url.substring(url.lastIndexOf("/")+1, url.length());
        } else if (url.contains(":")){
            return url.substring(url.lastIndexOf(":")+1, url.length());
        }
        throw new IllegalArgumentException("Url should have a '#', '/, or a ':' in it.");
    }

    private Set<String> resultSetToURLsSet(ResultSet rs) throws BridgeDbSqlException {
        HashSet<String> results = new HashSet<String>();
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

    private URLMapping resultSetToURLMapping(ResultSet rs) throws BridgeDbSqlException {
        try {
            if (rs.next()){
                Integer mappingId = rs.getInt("mapping.id"); 
                String sourceURL = rs.getString("source.uriSpace") + rs.getString("sourceId");
                String targetURL = rs.getString("target.uriSpace") + rs.getString("targetId");
                Integer mappingSetId = rs.getInt("mappingSet.id");
                String predicate = rs.getString("predicate");
                return new URLMapping (mappingId, sourceURL, predicate, targetURL, mappingSetId);       
            }
            return null;
       } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

    private Set<MappingSetInfo> resultSetToMappingSetInfos(ResultSet rs ) throws BridgeDbSqlException{
        HashSet<MappingSetInfo> results = new HashSet<MappingSetInfo>();
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
            throw new BridgeDbSqlException("No DataSource known for " + uriSpace);
       } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to parse results.", ex);
       }
    }

}
