package org.bridgedb.sql;

import java.sql.Connection;
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
public abstract class SQLBase implements IDMapper, IDMapperCapabilities, URLLinkListener, URLMapper, ProvenanceMapper, 
        OpsMapper, URLIterator {
    
    //Numbering should not clash with any GDB_COMPAT_VERSION;
	protected static final int SQL_COMPAT_VERSION = 4;
  
    //Maximumn size in database
    protected static final int SYSCODE_LENGTH = 100;
    protected static final int FULLNAME_LENGTH = 100;
    protected static final int MAINURL_LENGTH = 100;
    protected static final int URLPATTERN_LENGTH = 100;
    protected static final int ID_LENGTH = 100;
    protected static final int TYPE_LENGTH = 100;
    protected static final int URNBASE_LENGTH = 100;
    protected static final int PREDICATE_LENGTH = 100;
    protected static final int PROVENANCE_ID_LENGTH = 100;
    protected static final int NAME_SPACE_LENGTH = 100;
    protected static final int KEY_LENGTH= 100; 
    protected static final int PROPERTY_LENGTH = 100;

    private static final int FREESEARCH_CUTOFF = 100000;      
    //Internal parameters
    protected static final int DEFAULT_LIMIT = 1000;
    protected static final int BLOCK_SIZE = 1000;
    protected int blockCount = 0;
    protected int insertCount = 0;
    protected int doubleCount = 0;    
    
    protected SQLAccess sqlAccess;
    protected Connection possibleOpenConnection;

    public SQLBase(SQLAccess sqlAccess) throws BridgeDbSqlException{
        if (sqlAccess == null){
            throw new IllegalArgumentException("sqlAccess can not be null");
        }
        this.sqlAccess = sqlAccess;
        checkVersion();
        loadDataSources();
    }   

    public SQLBase(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
        if (sqlAccess == null){
            throw new IllegalArgumentException("sqlAccess can not be null");
        }
        this.sqlAccess = sqlAccess;
        if (dropTables){
            this.dropSQLTables();
            this.createSQLTables();
        } else {
            checkVersion();
            loadDataSources();
        }
    }   

    //***** IDMapper funtctions  *****
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }
    
    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        if (ref.getId() == null || ref.getDataSource() == null){
            return new HashSet<Xref>();
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT targetURL as url ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendSourceXref(query, ref);
        Collection<DataSource> targetDataSources = java.util.Arrays.asList(tgtDataSources);
        appendTargetDataSources(query, targetDataSources);
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }
    
    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        if (xref.getId() == null || xref.getDataSource() == null){
            return false;
        }
        return uriExists(xref.getUrl());
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        Set<String> URLS = urlSearch(text, limit);
        HashSet<Xref> results = new HashSet<Xref>();
        for (String URL:URLS){
            results.add(DataSource.uriToXref(URL));
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
    public void close() throws IDMapperException { 
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
    
    //***** IDMapperCapabilities funtctions  *****
    @Override
    public boolean isFreeSearchSupported() {
        //Due to scale issues we will only admit supporting freesearch on small databases;
        try {
            int count = getMappingsCount();
            return (count < FREESEARCH_CUTOFF);
        } catch (IDMapperException ex) {
            return false;
        }
    }

    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT sourceNameSpace as nameSpace "
                + "FROM provenance";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToDataSourceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query." + query, ex);
        }
    }

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT(targetNameSpace) as nameSpace "
                + "FROM provenance ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToDataSourceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        StringBuilder query = new StringBuilder ("SELECT ");
        appendVirtuosoTopConditions(query, 0, 1);
        query.append(" * FROM provenance ");
        query.append("WHERE sourceNameSpace = '");
        query.append(src.getNameSpace());
        query.append("' AND targetNameSpace = '");
        query.append(tgt.getNameSpace());
        query.append("'");
        this.appendMySQLLimitConditions(query,0, 1);
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return (rs.next());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query.toString(), ex);
        }
    }
    
    @Override
    public String getProperty(String key) {
        String query = "SELECT DISTINCT property "
                + "FROM properties "
                + "WHERE thekey = '" + key + "'";
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
        String query = "SELECT thekey "
                + "FROM properties "
                + "WHERE isPublic = 1"; //one works where isPublic is a boolean
        try {
            Statement statement = this.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                results.add(rs.getString("thekey"));
            }
            return results;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**** URLLinkListener Methods ****/
    
    @Override
    public void registerProvenanceLink(String provenanceId, DataSource source, String predicate, DataSource target) 
            throws BridgeDbSqlException{
        checkDataSourceInDatabase(source);
        checkDataSourceInDatabase(target);
        updateLastUpdated();
        String query = "SELECT * FROM provenance "
                    + "WHERE id = '" + provenanceId + "'";
        try {
			Statement statement = createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                String foundID = rs.getString("linkPredicate");
                DataSource foundSource = DataSource.getByNameSpace(rs.getString("sourceNameSpace"));
                String foundPredicate = rs.getString("linkPredicate");
                DataSource foundTarget = DataSource.getByNameSpace(rs.getString("targetNameSpace"));
                if (foundSource != source){
                    throw new BridgeDbSqlException("Error regeitering provenaceId " + provenanceId + 
                            " with source " + source + " it clashes with " + foundSource);
                }
                if (!foundPredicate.endsWith(predicate)){
                    throw new BridgeDbSqlException("Error regeitering provenaceId " + provenanceId + 
                            " with predicate " + predicate + " it clashes with " + foundPredicate);
                }
                if (foundTarget != target){
                    throw new BridgeDbSqlException("Error regeitering provenaceId " + provenanceId + 
                            " with target " + target + " it clashes with " + foundTarget);
                }
                return;
            }
            query = "INSERT INTO provenance "
                    + "(id, sourceNameSpace, linkPredicate, targetNameSpace ) " 
                    + "VALUES (" 
                    + "'" + provenanceId + "', " 
                    + "'" + source.getNameSpace() + "', " 
                    + "'" + predicate + "', " 
                    + "'" + target.getNameSpace() + "')";
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            System.err.println(ex);
            throw new BridgeDbSqlException ("Error inserting link with " + query, ex);
        }
    }

    @Override
    public abstract void insertLink(String source, String target, String forwardProvenanceId, String inverseProvenanceId)
            throws IDMapperException;

    @Override
    public void closeInput() throws IDMapperException {
        Reporter.report ("FInished processing linkset");
        countLinks();
        if (possibleOpenConnection != null){
            try {
                //possibleOpenConnection.commit();
                possibleOpenConnection.close();
            } catch (SQLException ex) {
               throw new BridgeDbSqlException ("Error closing connection ", ex);
            }
        }
    }

    @Override
    public Set<String> getProvenanceIds() throws IDMapperException {
        String query = ("SELECT id FROM provenance ");
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToProvenanceIds(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }
    
    private void countLinks () throws IDMapperException{
        Reporter.report ("Updating link count. Please Wait!");
        Statement countStatement = this.createStatement();
        Statement updateStatement = this.createStatement();
        String query = ("select count(*) as mycount, provenance_id from link group by provenance_id");  
        ResultSet rs;
        try {
            rs = countStatement.executeQuery(query);    
            Reporter.report ("Count query run. Updating link count now");
            while (rs.next()){
                int count = rs.getInt("mycount");
                String provenanceId = rs.getString("provenance_id");  
                String update = "update provenance set linkCount = " + count + " where id = '" + provenanceId + "'";
                try {
                    int updateCount = updateStatement.executeUpdate(update);
                    if (updateCount != 1){
                        throw new IDMapperException("Updated rows <> ! when running " + update);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    throw new IDMapperException("Unable to run update. " + update, ex);
                }
            }
            Reporter.report ("Updating counts finished!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }
    
    //***** URLMapper funtctions  *****
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, String... targetNameSpaces) throws IDMapperException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
        for (String ref:sourceURLs){
            Set<String> mapped = this.mapURL(ref, targetNameSpaces);
            results.put(ref, mapped);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String ref, String... targetNameSpaces) throws IDMapperException {
        if (ref == null) throw new IDMapperException ("Illegal null ref. Please use a URL");
        if (ref.isEmpty()) throw new IDMapperException ("Illegal empty ref. Please use a URL");
        StringBuilder query = new StringBuilder();
        query.append("SELECT targetURL as url ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        query.append("AND sourceURL = '");
            query.append(ref);
            query.append("' ");
        if (targetNameSpaces.length > 0){    
            query.append("AND ( targetNameSpace = '");
                query.append(targetNameSpaces[0]);
                query.append("' ");
            for (int i = 1; i < targetNameSpaces.length; i++){
                query.append("OR targetNameSpace = '");
                    query.append(targetNameSpaces[i]);
                    query.append("'");
            }
            query.append(")");
        }
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            Set<String> results = resultSetToURLSet(rs);
            if (targetNameSpaces.length == 0){
               results.add(ref); 
            } else {
                for (String targetNameSpace: targetNameSpaces){
                    if (ref.startsWith(targetNameSpace)){
                        results.add(ref);
                    }
                }
            }
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public boolean uriExists(String URL) throws IDMapperException {
        if (URL == null) return false;
        if (URL.isEmpty()) return false;
        StringBuilder query = new StringBuilder("SELECT ");
        appendVirtuosoTopConditions(query, 0, 1); 
        query.append(" * FROM link ");
        query.append(" where ");
        query.append("       sourceURL = '");
        query.append(URL);
        query.append("'"); 
        this.appendMySQLLimitConditions(query,0, 1);
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            if (rs.next()) return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query.toString(), ex);
        }
        return false;
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws IDMapperException {
        //Try source using index so no joker at the front
        StringBuilder query = new StringBuilder("SELECT ");
        appendVirtuosoTopConditions(query, 0, limit); 
        query.append(" sourceURL as url ");
        query.append("FROM link ");
        query.append("where ");
        query.append("   sourceURL LIKE '%");
        query.append(text); 
        query.append("%' ");
        query.append(" group by sourceurl"); //This is to provide distinct as top and distinct appear not to go together
        appendMySQLLimitConditions(query, 0, limit);
        Statement statement = this.createStatement();
        Set<String> foundSoFar;
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query.toString(), ex);
        }
    }

    //*** XrefIterator methods ****

    //Removed due to scale problem
    //@Override
    //public Iterable<Xref> getIterator(DataSource ds) throws IDMapperException {
    //    return new ByPositionXrefIterator(this, ds);
    //}

    //Removed due to scale problem
    //@Override
    //public Iterable<Xref> getIterator() throws IDMapperException {
    //    return new ByPositionXrefIterator(this);
    //}

    //*** UrlIterator methods ****

    @Override
    public Iterable<String> getURLIterator(String nameSpace) throws IDMapperException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT sourceURL as url ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        query.append("AND sourceNameSpace = '");
        query.append(nameSpace);
        query.append("'");
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query.toString(), ex);
        }
    }

    @Override
    public Iterable<String> getURLIterator() throws IDMapperException {
        String query = "SELECT sourceNameSpace as nameSpace FROM provenance";
        Statement statement = this.createStatement();
        Set<String> nameSpaces;
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            nameSpaces = resultSetToNameSpaceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query.toString(), ex);
        }
        return new ByNameSpaceIterable(nameSpaces, this);
    }

    //**** OpsMapper Methods  **/ 
    @Override
    public List<URLMapping> getMappings(List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces, 
            List<String> provenanceIds, Integer position, Integer limit){
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        appendVirtuosoTopConditions(query, position, limit);
        query.append("link.id as linkId, sourceURL, targetURL, provenance.id as provenanceId, sourceNameSpace, linkPredicate, ");
        query.append("targetNameSpace ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendAllURLconditions(query, URLs);
        appendSourceURLConditions(query, sourceURLs);
        appendTargetURLConditions(query, targetURLs);
        appendAllNameSpaceConditions(query, nameSpaces);
        appendSourceNameSpaceConditions(query, sourceNameSpaces);
        appendTargetNameSpaceConditions(query, targetNameSpaces);
        appendProvenanceConditions(query, provenanceIds);
        appendMySQLLimitConditions(query, position, limit);
        try {
            Statement statement = this.createAStatement();
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLMappingList(rs);
        } catch (SQLException ex) {
            ArrayList<URLMapping> results = new ArrayList<URLMapping>();
            results.add(new URLMapping(ex, query.toString()));
            return results;
        }
    }

    @Override
    public URLMapping getMapping(int id) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT link.id as linkId, sourceURL, targetURL, provenance.id as provenanceId, sourceNameSpace, linkPredicate, ");
            query.append("targetNameSpace ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        query.append("AND link.id  = ");
            query.append (id);
        try {
            Statement statement = this.createStatement();
            ResultSet rs = statement.executeQuery(query.toString());
            if (rs.next()){
                return resultSetToURLMapping(rs);
            } else {
                return new URLMapping("No mapping found for id: " + id);
            }
        } catch (Exception ex) {
            return new URLMapping(ex, query.toString());
        }
    }

    /**
     * DOES NOT SCALE
     * @param dataSources
     * @param provenanceIds
     * @param position
     * @param limit
     * @return
     * @throws IDMapperException 
     */
    public List<Xref> getXrefs(List<DataSource> dataSources, List<String> provenanceIds, Integer position, Integer limit) 
            throws IDMapperException{
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        appendVirtuosoTopConditions(query, position, limit);
        query.append("sourceURL as url ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendSourceDataSources(query, dataSources);
        appendProvenanceConditions(query, provenanceIds);
        query.append("UNION ");
        query.append("SELECT targetURL as url ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendTargetDataSources(query, dataSources);
        appendProvenanceConditions(query, provenanceIds);
        appendMySQLLimitConditions(query, position, limit);
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToXrefList(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }        
    }

    /**
     * DOES NOT SCALE
     * @param nameSpaces
     * @param provenanceIds
     * @param position
     * @param limit
     * @return
     * @throws IDMapperException 
     */
    public List<String> getURLs(List<String> nameSpaces, List<String> provenanceIds, Integer position, Integer limit) 
            throws IDMapperException{
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        appendVirtuosoTopConditions(query, position, limit);
        query.append("sourceURL as url ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendSourceNameSpaceConditions(query, nameSpaces);
        appendProvenanceConditions(query, provenanceIds);
        query.append("UNION ");
        query.append("SELECT targetURL as url ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendTargetNameSpaceConditions(query, nameSpaces);
        appendProvenanceConditions(query, provenanceIds);
        appendMySQLLimitConditions(query, position, limit);
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLList(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }        
    }
    
    @Override
    public List<String> getSampleSourceURLs() throws IDMapperException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        appendVirtuosoTopConditions(query, 0, 5);
        query.append(" sourceURL as url FROM link ");
        appendMySQLLimitConditions(query, 0, 5);
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLList(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }        
    }

    @Override
    public OverallStatistics getOverallStatistics() throws IDMapperException {
        int numberOfMappings = getMappingsCount();
        String provenanceQuery = "SELECT count(distinct(id)) as numberOfProvenances, "
                + "count(distinct(provenance.sourceNameSpace)) as numberOfSourceDataSources, "
                + "count(distinct(provenance.linkPredicate)) as numberOfPredicates, "
                + "count(distinct(provenance.targetNameSpace)) as numberOfTargetDataSources "
                + "FROM provenance ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(provenanceQuery);
            if (rs.next()){
                int numberOfProvenances = rs.getInt("numberOfProvenances");
                int numberOfSourceDataSources = rs.getInt("numberOfSourceDataSources");
                int numberOfPredicates= rs.getInt("numberOfPredicates");
                int numberOfTargetDataSources = rs.getInt("numberOfTargetDataSources");
                return new OverallStatistics(numberOfMappings, numberOfProvenances, 
                        numberOfSourceDataSources, numberOfPredicates, numberOfTargetDataSources);
            } else {
                System.err.println(provenanceQuery);
                throw new IDMapperException("o Results for query. " + provenanceQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + provenanceQuery, ex);
        }
    }

    private int getMappingsCount() throws IDMapperException{
        String linkQuery = "SELECT count(*) as numberOfMappings "
                + "FROM link";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(linkQuery);
            if (rs.next()){
                return rs.getInt("numberOfMappings");
            } else {
                System.err.println(linkQuery);
                throw new IDMapperException("o Results for query. " + linkQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + linkQuery, ex);
        }      
    }

    @Override
    public List<ProvenanceInfo> getProvenanceInfos() throws IDMapperException {
        String query = ("SELECT * FROM provenance ");
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToProvenanceInfos(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }


    /*** ProvenanceMapper methods ***/
    @Override
    public Map<Xref, Set<XrefProvenance>> mapIDProvenance(List<Xref> srcXrefs, 
            List<String> provenanceIds, List<DataSource> targetDataSources) throws IDMapperException {
        Map<Xref, Set<XrefProvenance>> results = new HashMap<Xref, Set<XrefProvenance>>();
        for (Xref ref: srcXrefs){
            Set<XrefProvenance> result = mapIDProvenance(ref, provenanceIds, targetDataSources);
            if (!result.isEmpty()){
                results.put(ref, mapIDProvenance(ref, provenanceIds, targetDataSources));
            }
        }
        return results;
    }

    @Override
    public Set<XrefProvenance> mapIDProvenance(Xref ref, List<String> provenanceIds, 
            List<DataSource> targetDataSources) throws IDMapperException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT link.id as linkId, sourceURL, targetURL, provenance.id as provenanceId, sourceNameSpace, linkPredicate, ");
            query.append("targetNameSpace ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendSourceXref(query, ref);
        appendTargetDataSources(query, targetDataSources);
        appendProvenanceConditions(query, provenanceIds);
        try {
            Statement statement = this.createAStatement();
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToXrefProvenanceSet(rs);
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run mapIDProvenance", ex, query.toString());
        }
    }

    @Override
    public Set<URLMapping> mapURL(List<String> sourceURLs, 
            List<String> provenanceIds, List<String> targetNameSpaces) throws IDMapperException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT link.id as linkId, sourceURL, targetURL, provenance.id as provenanceId, sourceNameSpace, linkPredicate, ");
            query.append("targetNameSpace ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendSourceURLConditions(query, sourceURLs);
        appendTargetNameSpaceConditions(query, targetNameSpaces);
        appendProvenanceConditions(query, provenanceIds);
        try {
            Statement statement = this.createAStatement();
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLMappingSet(rs);
        } catch (SQLException ex) {
            HashSet<URLMapping> results = new HashSet<URLMapping>();
            results.add(new URLMapping(ex, query.toString()));
            return results;
        }
    }

    /**  Support methods **/
    
    protected Statement createStatement() throws BridgeDbSqlException{
        try {
            return createAStatement();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException ("Error creating a new statement ", ex);
        }
    }
   
    abstract protected Statement createAStatement() throws SQLException;
    
    /** Append methods **/
    
    private void appendSourceXref(StringBuilder query, Xref ref){
        query.append("AND sourceURL = '");
            query.append(ref.getUrl());
            query.append("'");        
    }
    
    private void appendAllDataSourceConditions(StringBuilder query, List<DataSource> dataSources){
        if (!dataSources.isEmpty()){
            query.append("AND (sourceNameSpace = '");
                query.append(dataSources.get(0).getNameSpace());
                query.append("' ");
            query.append("OR targetNameSpace = '");
                query.append(dataSources.get(0).getNameSpace());
                query.append("' ");
            for (int i = 1; i < dataSources.size(); i++){
                query.append("OR sourceNameSpace = '");
                    query.append(dataSources.get(i).getNameSpace());
                    query.append("' ");                
                query.append("OR targetNameSpace = '");
                    query.append(dataSources.get(i).getNameSpace());
                    query.append("' ");
            }
            query.append(") ");
        }
    }

    private void appendSourceDataSources(StringBuilder query, List<DataSource> sourceDataSources){ 
        if (!sourceDataSources.isEmpty()){    
            Iterator<DataSource> iterator = sourceDataSources.iterator();
            query.append("AND (sourceNameSpace = '");
                query.append(iterator.next().getNameSpace());
                query.append("' ");
            while (iterator.hasNext()){
                query.append("OR sourceNameSpace = '");
                query.append(iterator.next().getNameSpace());
                    query.append("' ");
            }
            query.append(")");
        }        
    }

    private void appendTargetDataSources(StringBuilder query, Collection<DataSource> targetDataSources){
        if (!targetDataSources.isEmpty()){    
            Iterator<DataSource> iterator = targetDataSources.iterator();
            query.append("AND (targetNameSpace = '");
                query.append(iterator.next().getNameSpace());
                query.append("' ");
            while (iterator.hasNext()){
                query.append("OR targetNameSpace = '");
                query.append(iterator.next().getNameSpace());
                    query.append("' ");
            }
            query.append(")");
        }        
    }
   
    private void appendAllURLconditions(StringBuilder query, List<String> URLs){
        if (!URLs.isEmpty()){
            query.append("AND (sourceURL = '");
                query.append(URLs.get(0));
                query.append("' ");
            query.append("OR targetURL = '");
                query.append(URLs.get(0));
                query.append("' ");
            for (int i = 1; i < URLs.size(); i++){
                query.append("OR sourceURL = '");
                    query.append(URLs.get(i));
                    query.append("' ");                
                query.append("OR targetURL = '");
                    query.append(URLs.get(i));
                    query.append("' ");
            }
            query.append(") ");
        }
    }
    
    private void appendSourceURLConditions(StringBuilder query, Collection<String> sourceURLs){
        if (!sourceURLs.isEmpty()){
            query.append("AND (sourceURL = '");
            Iterator<String> iterator = sourceURLs.iterator();
                query.append(iterator.next());
                query.append("' ");
            while (iterator.hasNext()){
                query.append("OR sourceURL = '");
                    query.append(iterator.next());
                    query.append("' ");                
            }
            query.append(") ");
        }
    }

    private void appendTargetURLConditions(StringBuilder query, List<String> targetURLs){
        if (!targetURLs.isEmpty()){
            query.append("AND (targetURL = '");
                query.append(targetURLs.get(0));
                query.append("' ");
            for (int i = 1; i < targetURLs.size(); i++){
                query.append("OR targetURL = '");
                    query.append(targetURLs.get(i));
                    query.append("' ");                
            }
            query.append(") ");
        } 
    }
    
    private void appendAllNameSpaceConditions(StringBuilder query,List<String> nameSpaces){
        if (!nameSpaces.isEmpty()){
            query.append("AND (sourceNameSpace = '");
                query.append(nameSpaces.get(0));
                query.append("' ");
            query.append("OR targetNameSpace = '");
                query.append(nameSpaces.get(0));
                query.append("' ");
            for (int i = 1; i < nameSpaces.size(); i++){
                query.append("OR sourceNameSpace = '");
                    query.append(nameSpaces.get(i));
                    query.append("' ");                
                query.append("OR targetNameSpace = '");
                    query.append(nameSpaces.get(i));
                    query.append("' ");
            }
            query.append(") ");
        }
    }

    private void appendSourceNameSpaceConditions(StringBuilder query, List<String> sourceNameSpaces){
        if (!sourceNameSpaces.isEmpty()){
            query.append("AND (sourceNameSpace = '");
                query.append(sourceNameSpaces.get(0));
                query.append("' ");
            for (int i = 1; i < sourceNameSpaces.size(); i++){
                query.append("OR sourceNameSpace = '");
                    query.append(sourceNameSpaces.get(i));
                    query.append("' ");                
            }
            query.append(")");
        }
    }
    
    private void appendTargetNameSpaceConditions(StringBuilder query, Collection<String> targetNameSpaces){
        if (!targetNameSpaces.isEmpty()){
            Iterator<String> iterator = targetNameSpaces.iterator();
            query.append("AND (targetNameSpace = '");
                query.append(iterator.next());
                query.append("' ");
            while (iterator.hasNext()){
                query.append("OR targetNameSpace = '");
                    query.append(iterator.next());
                    query.append("' ");                
            }
            query.append(") ");
        } 
    }

    private void appendProvenanceConditions(StringBuilder query, Collection<String> provenanceIds){
        if (!provenanceIds.isEmpty()){
            Iterator<String> iterator = provenanceIds.iterator();
            query.append("AND ( provenance.id = '");
                query.append(iterator.next());
                query.append("' ");
            while (iterator.hasNext()){
                query.append("OR provenance.id = '");
                    query.append(iterator.next());
                    query.append("' ");                
            }
            query.append(")");
        }   
    }
    
    protected abstract void appendMySQLLimitConditions(StringBuilder query, Integer position, Integer limit);
    
    protected abstract void appendVirtuosoTopConditions(StringBuilder query, Integer position, Integer limit);

    private Set<URLMapping> URLMappingParameterError(String error){
        HashSet<URLMapping> results = new HashSet<URLMapping>();
        results.add(new URLMapping(error));
        return results;
    }
    
    private Set<DataSource> resultSetToDataSourceSet(ResultSet rs ) throws IDMapperException{
        HashSet<DataSource> results = new HashSet<DataSource>();
        try {
            while (rs.next()){
                String nameSpace = rs.getString("nameSpace");
                DataSource ds = DataSource.getByNameSpace(nameSpace);
                results.add(ds);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    private Set<String> resultSetToNameSpaceSet(ResultSet rs ) throws IDMapperException{
        HashSet<String> results = new HashSet<String>();
        try {
            while (rs.next()){
                results.add(rs.getString("nameSpace"));
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    private Set<String> resultSetToProvenanceIds(ResultSet rs ) throws IDMapperException{
        HashSet<String> results = new HashSet<String>();
        try {
            while (rs.next()){
                results.add(rs.getString("id"));
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    private List<ProvenanceInfo> resultSetToProvenanceInfos(ResultSet rs ) throws IDMapperException{
        ArrayList<ProvenanceInfo> results = new ArrayList<ProvenanceInfo>();
        try {
            while (rs.next()){
                Integer count = rs.getInt("linkCount");
                results.add(new ProvenanceInfo(rs.getString("id"), rs.getString("sourceNameSpace"), 
                        rs.getString("linkPredicate"), rs.getString("targetNameSpace"), count));
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    private URLMapping resultSetToURLMapping(ResultSet rs) throws SQLException {
        return new URLMapping(
                rs.getInt("linkId"), 
                rs.getString("sourceURL"), 
                rs.getString("targetURL"), 
                rs.getString("provenanceId"),
                rs.getString("linkPredicate"));
    }

    private List<URLMapping> resultSetToURLMappingList(ResultSet rs) throws SQLException {
        ArrayList<URLMapping> results = new ArrayList<URLMapping>();
        while (rs.next()){
            results.add(new URLMapping(
                rs.getInt("linkId"), 
                rs.getString("sourceURL"), 
                rs.getString("targetURL"), 
                rs.getString("provenanceId"),
                rs.getString("linkPredicate")));
        }
        return results;
    }

    private Set<URLMapping> resultSetToURLMappingSet(ResultSet rs ) throws SQLException {
        HashSet<URLMapping> results = new HashSet<URLMapping>();
        while (rs.next()){
            results.add(resultSetToURLMapping(rs));
        }
        return results;
    }

    protected List<String> resultSetToURLList(ResultSet rs ) throws IDMapperException{
        ArrayList<String> results = new ArrayList<String>();
        try {
            while (rs.next()){
                String url = rs.getString("url");
                results.add(url);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    protected Set<String> resultSetToURLSet(ResultSet rs ) throws IDMapperException{
        HashSet<String> results = new HashSet<String>();
        try {
            while (rs.next()){
                String url = rs.getString("url");
                results.add(url);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    private Set<XrefProvenance> resultSetToXrefProvenanceSet(ResultSet rs ) throws SQLException, IDMapperException {
        HashSet<XrefProvenance> results = new HashSet<XrefProvenance>();
        while (rs.next()){
            results.add(resultSetToXrefProvenance(rs));
        }
        return results;
    }

    private XrefProvenance resultSetToXrefProvenance(ResultSet rs) throws SQLException, IDMapperException {
        Xref xref = DataSource.uriToXref(rs.getString("targetURL"));
        return new XrefProvenance(xref, rs.getString("provenanceId"), rs.getString("linkPredicate"));
    }

    private List<Xref> resultSetToXrefList(ResultSet rs ) throws IDMapperException{
        ArrayList<Xref> results = new ArrayList<Xref>();
        try {
            while (rs.next()){
                String url = rs.getString("url");
                Xref xref = DataSource.uriToXref(url);
                results.add(xref);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    private Set<Xref> resultSetToXrefSet(ResultSet rs ) throws IDMapperException{
        HashSet<Xref> results = new HashSet<Xref>();
        try {
            while (rs.next()){
                String url = rs.getString("url");
                Xref xref = DataSource.uriToXref(url);
                results.add(xref);
            }
        } catch (SQLException ex) {
            throw new IDMapperException("Unable to parse results.", ex);
        }
        return results;
    }

    /**
     * Checks that the schema is for this version.
     * 
     * @throws BridgeDbSqlException If the schema version is not the expected one.
     */
	private void checkVersion() throws BridgeDbSqlException
	{
        Statement stmt = createStatement();
        ResultSet r = null;
        int version = 0;
        try {
            r = stmt.executeQuery("SELECT schemaversion FROM info");
            if(r.next()) version = r.getInt(1);
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Error checking the version. ", ex);
        }
		finally
		{
            if (r != null) try { r.close(); } catch (SQLException ignore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ignore) {}
		}
        if (version == SQL_COMPAT_VERSION) return;
 		switch (version)
		{
    		case 2:
        		throw new BridgeDbSqlException("Please use the SimpleGdbFactory in the org.bridgedb.rdb package");
            case 3:
                throw new BridgeDbSqlException("Please use the SimpleGdbFactory in the org.bridgedb.rdb package");
            //NB add future schema versions here
            default:
                throw new BridgeDbSqlException ("Unrecognized schema version '" + version + "', please make sure you have the latest " +
					"version of this software and databases");
		}		
	}

    private void loadDataSources() throws BridgeDbSqlException{
        try {
            Statement statement = this.createStatement();
            String query = "SELECT sysCode, isPrimary, fullName, mainUrl, urlPattern, idExample, type, urnBase"
                    + "   from DataSource ";           
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                DataSource.register(rs.getString("sysCode"), rs.getString("fullName")).
                        primary(rs.getBoolean("isPrimary")).
                        mainUrl(rs.getString("mainUrl")).
                        urlPattern(rs.getString("urlPattern")).
                        idExample(rs.getString("idExample")).
                        type(rs.getString("type")).
                        urnBase(rs.getString("urnBase"));
            }
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to load DataSources");
        }
    }
    
    /**
	 * Excecutes several SQL statements to drop the tables 
	 * @throws IDMapperException 
	 */
     /**
	 * Excecutes several SQL statements to drop the tables 
	 * @throws IDMapperException 
	 */
	private void dropSQLTables() throws BridgeDbSqlException
	{
 		dropTable("info");
 		dropTable("link");
 		dropTable("DataSource");
 		dropTable("provenance");
 		dropTable("properties");
     }
    
     private void dropTable(String name) throws BridgeDbSqlException{
        //"IF NOT EXISTS" is unsupported 
        String query = "select * from information_schema.tables where table_name='" + name + "'";
        Statement sh = createStatement();
        try 
        {
            sh.execute("DROP TABLE " + name);
            sh.close();
        } catch (SQLException e) {
            System.err.println("Unable to drop table " + name + " assuming it does not exist");
        }
    }

    /**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @throws IDMapperException 
	 */
	private void createSQLTables() throws BridgeDbSqlException
	{
        //"IF NOT EXISTS " is not supported
		try 
		{
			Statement sh = createStatement();
 			sh.execute("CREATE TABLE                            "
					+ "info                                     " 
					+ "(    schemaversion INTEGER PRIMARY KEY	"
                    + ")");
  			sh.execute( //Add compatibility version of GDB
					"INSERT INTO info VALUES ( " + SQL_COMPAT_VERSION + ")");
            //TODO add organism as required
            sh.execute("CREATE TABLE  "
                    + "     DataSource "
                    + "  (  sysCode VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,   "
                    + "     isPrimary SMALLINT,                                  "
                    + "     fullName VARCHAR(" + FULLNAME_LENGTH + "),      "
                    + "     mainUrl VARCHAR(" + MAINURL_LENGTH + "),        "
                    + "     urlPattern VARCHAR(" + URLPATTERN_LENGTH + "),  "
                    + "     idExample VARCHAR(" + ID_LENGTH + "),           "
                    + "     type VARCHAR(" + TYPE_LENGTH + "),              "
                    + "     urnBase VARCHAR(" + URNBASE_LENGTH + ")         "
                    + "  ) ");
 			sh.execute("CREATE TABLE                                                    "
                    + "link                                                             " 
                            //As most search are on full url full url is stored in one column
					+ " (   id INT IDENTITY PRIMARY KEY,                          " 
					+ "     sourceURL VARCHAR(150) NOT NULL,                            "
                            //Again a speed for space choice.
					+ "     targetURL VARCHAR(150) NOT NULL,                            " 
					+ "     provenance_id VARCHAR(" + PROVENANCE_ID_LENGTH + ")         "
					+ " )									                            ");
            sh.execute("CREATE INDEX sourceFind ON link (sourceURL) ");
            sh.execute("CREATE INDEX sourceProvenaceFind ON link (sourceURL, provenance_id) ");
         	sh.execute(	"CREATE TABLE                                                       "    
					+ "		provenance                                                      " 
					+ " (   id VARCHAR(" + PROVENANCE_ID_LENGTH + ") PRIMARY KEY,           " 
                    + "     sourceNameSpace VARCHAR(" + NAME_SPACE_LENGTH + ") NOT NULL,    "
                    + "     linkPredicate VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL,       "
                    + "     targetNameSpace VARCHAR(" + NAME_SPACE_LENGTH + ")  NOT NULL,    "
                    + "     linkCount INT                                                   "
					+ " ) "); 
            sh.execute ("CREATE TABLE  "
                    + "    properties "
                    + "(   thekey      VARCHAR(" + KEY_LENGTH + ") NOT NULL, "
                    + "    property    VARCHAR(" + PROPERTY_LENGTH + ") NOT NULL, "
                    + "    isPublic    SMALLINT "
					+ " ) "); 
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}
     
    void checkDataSourceInDatabase(DataSource source) throws BridgeDbSqlException{
        Statement statement = this.createStatement();
        String sysCode  = source.getSystemCode();
        if (sysCode == null) {
            throw new BridgeDbSqlException ("Currently unable to handle Datasources with null systemCode");
        }
        if (sysCode.isEmpty()) {
            throw new BridgeDbSqlException ("Currently unable to handle Datasources with empty systemCode");
        }
        String query = "SELECT sysCode"
                + "   from DataSource "
                + "   where "
                + "      sysCode = '" + source.getSystemCode() + "'"; 
        boolean found;
        try {
            ResultSet rs = statement.executeQuery(query);
            found = rs.next();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to check provenace " +  query, ex);
        }
        if (found){
            updateDataSource(source);
        } else {
            writeDataSource(source);
        }
    }
    
    private void writeDataSource(DataSource source) throws BridgeDbSqlException{
        StringBuilder insert = new StringBuilder ("INSERT INTO DataSource ( sysCode , isPrimary ");
        StringBuilder values = new StringBuilder ("Values ( ");
        if (source.getSystemCode().length() > SYSCODE_LENGTH ){
            throw new BridgeDbSqlException("Maximum length supported for SystemCode is " + SYSCODE_LENGTH + 
                    " so unable to save " + source.getSystemCode());
        }
        values.append("'");
        values.append(source.getSystemCode());
        values.append("' , ");
        if (source.isPrimary()){
            values.append (1);
        } else {
           values.append (0);
        }
        String value = source.getFullName(); 
        if (value != null && !value.isEmpty()){
            if (value.length() > FULLNAME_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for fullName is " + FULLNAME_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", fullName ");
            values.append (", '");
            values.append (value);
            values.append ("' ");
        }
        value = source.getMainUrl();
        if (value != null && !value.isEmpty()){
            if (value.length() > MAINURL_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for mainUrl is " + MAINURL_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", mainUrl ");
            values.append (", '");
            values.append (value);
            values.append ("' ");
        }
        value = source.getUrl("$id");
        if (value != null && !value.isEmpty()){
            if (value.length() > URLPATTERN_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for URLPattern is " + URLPATTERN_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", urlPattern ");
            values.append (", '");
            values.append (value);
            values.append ("' ");
        }
        value = source.getExample().getId();
        if (value != null && !value.isEmpty()){
            if (value.length() > ID_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for exampleId is " + ID_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", idExample ");
            values.append (", '");
            values.append (value);
            values.append ("' ");
        }
        value = source.getType();
        if (value != null && !value.isEmpty()){
            if (value.length() > TYPE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for type is " + TYPE_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", type ");
            values.append (", '");
            values.append (value);
            values.append ("' ");
        }
        value = source.getURN("");
        //remove the :
        value = value.substring(0, value.length()-1);
        if (value != null && !value.isEmpty()){
            if (value.length() > URNBASE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for urnBase is " + URNBASE_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", urnBase ");
            values.append (", '");
            values.append (value);
            values.append ("' ");
        }
        if (source.getOrganism() != null){
            throw new BridgeDbSqlException("Sorry DataSource oraginism filed is upsupported");
        }
        Statement statement = this.createStatement();
        String update = insert.toString() + ") " + values.toString() + " )";
        try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to writeDataSource " + update, ex);
        }
    }

    private void updateDataSource(DataSource source) throws BridgeDbSqlException{
        StringBuilder update = new StringBuilder("UPDATE DataSource ");
        update.append ("SET isPrimary = ");
        if (source.isPrimary()){
            update.append (1);
        } else {
           update.append (0);
        }
        update.append (" ");       
        String value = source.getFullName();
        if (value != null && !value.isEmpty()){
            if (value.length() > FULLNAME_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for fullName is " + FULLNAME_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", fullName = '");
            update.append (value);
            update.append ("' ");
        }       
        value = source.getMainUrl();
        if (value != null && !value.isEmpty()){
            if (value.length() > MAINURL_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for mainUrl is " + MAINURL_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", mainUrl = '");
            update.append (value);
            update.append ("' ");
        }
        value = source.getUrl("$id");
        if (value != null && !value.isEmpty()){
            if (value.length() > URLPATTERN_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for URLPattern is " + URLPATTERN_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", urlPattern = '");
            update.append (value);
            update.append ("' ");
        }
        value = source.getExample().getId();
        if (value != null && !value.isEmpty()){
            if (value.length() > ID_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for exampleId is " + ID_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", idExample = '");
            update.append (value);
            update.append ("' ");
        }
        value = source.getType();
        if (value != null && !value.isEmpty()){
            if (value.length() > TYPE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for type is " + TYPE_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", type = '");
            update.append (value);
            update.append ("' ");
        }
        value = source.getURN("");
        //remove the :
        value = value.substring(0, value.length()-1);
        if (value != null && !value.isEmpty()){
            if (value.length() > URNBASE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for urnBase is " + URNBASE_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", urnBase = '");
            update.append (value);
            update.append ("' ");
        }
        if (source.getSystemCode().length() > SYSCODE_LENGTH ){
            throw new BridgeDbSqlException("Maximum length supported for SystemCode is " + SYSCODE_LENGTH + 
                    " so unable to save " + source.getSystemCode());
        }
        update.append ("WHERE sysCode  = '");
        update.append (source.getSystemCode());
        update.append ("' ");
        if (source.getOrganism() != null){
            throw new BridgeDbSqlException("Sorry DataSource oraginism filed is upsupported");
        }
        Statement statement = this.createStatement();
        try {
            statement.executeUpdate(update.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to updateDataSource " + update, ex);
        }
    }

    private void updateLastUpdated() throws BridgeDbSqlException {
        String delete = "DELETE from properties where thekey = 'LastUpdates'";
        Statement statement = this.createStatement();
        try {
            statement.executeUpdate(delete.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Error Deleting LastUpDated " + delete, ex);
        }
        String date = new Date().toString();
        String update = "INSERT INTO properties    "
                    + "(thekey, property, isPublic )                            " 
                    + "VALUES ('LastUpdates', '" + date  + "' , 1)  ";
        try {
            statement.executeUpdate(update.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Error insertoing LastUpDated " + update, ex);
        }
    }

}