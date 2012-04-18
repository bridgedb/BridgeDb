package org.bridgedb.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.bridgedb.Xref;
import org.bridgedb.iterator.ByPositionURLIterator;
import org.bridgedb.iterator.URLByPosition;
import org.bridgedb.linkset.URLLinkListener;
import org.bridgedb.provenance.ProvenanceMapper;
import org.bridgedb.provenance.XrefProvenance;
import org.bridgedb.result.URLMapping;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProvenanceStatistics;
import org.bridgedb.url.OpsMapper;
import org.bridgedb.url.URLIterator;
import org.bridgedb.url.URLMapper;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
public class URLMapperSQL extends CommonSQL 
        implements URLLinkListener, URLMapper, ProvenanceMapper, URLIterator, URLByPosition, OpsMapper{
    
    //Numbering should not clash with any GDB_COMPAT_VERSION;
	private static final int SQL_COMPAT_VERSION = 5;
    
    private static final int PREDICATE_LENGTH = 100;
    private static final int PROVENANCE_ID_LENGTH = 100;
    private static final int NAME_SPACE_LENGTH = 100;

    private static final int DEFAULT_LIMIT = 1000;
    
    private PreparedStatement pstInsertLink = null;
    private PreparedStatement pstCheckLink = null;
    
    private static final String UNSPECIFIED_PREDICATE = "NO predicate provided";
    private static final String UNSPECIFIED_CREATOR = "Unkown";

    public URLMapperSQL(SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(sqlAccess);
    }   

    public URLMapperSQL(boolean dropTables, SQLAccess sqlAccess) throws BridgeDbSqlException{
        super(dropTables, sqlAccess);
    }   

    @Override
    boolean correctVersion(int currentVersion) {
        return currentVersion == SQL_COMPAT_VERSION;
    }

    /**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @throws IDMapperException 
	 */
	void createSQLTables() throws BridgeDbSqlException
	{
        super.createSQLTables();
		try 
		{
			Statement sh = createStatement();
			sh.execute( //Add compatibility version of GDB
					"INSERT INTO info VALUES ( " + SQL_COMPAT_VERSION + ")");
			sh.execute("CREATE TABLE                                                    "
                    + "IF NOT EXISTS                                                    "
                    + "link                                                             " 
                            //As most search are on full url full url is stored in one column
					+ " (   id INT AUTO_INCREMENT PRIMARY KEY,                          " 
					+ "     sourceURL VARCHAR(150) NOT NULL,                            "
                            //Again a speed for space choice.
					+ "     targetURL VARCHAR(150) NOT NULL,                            " 
					+ "     provenance_id VARCHAR(" + PROVENANCE_ID_LENGTH + ")         "  //Type still under review
					+ " )									                            ");
        	sh.execute(	"CREATE TABLE                                                       "    
                    + "IF NOT EXISTS                                                        "
					+ "		provenance                                                      " 
					+ " (   id VARCHAR(" + PROVENANCE_ID_LENGTH + ") PRIMARY KEY,           " 
                    + "     sourceNameSpace VARCHAR(" + NAME_SPACE_LENGTH + ") NOT NULL,    "
                    + "     linkPredicate VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL,       "
                    + "     targetNameSpace VARCHAR(" + NAME_SPACE_LENGTH + ")  NOT NULL   "
					+ " ) ");   
            sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}

    @Override
    public void openInput() throws BridgeDbSqlException {
        super.openInput();
		try
		{
			pstInsertLink = possibleOpenConnection.prepareStatement("INSERT INTO link    "
                    + "(sourceURL, targetURL, provenance_id )                            " 
                    + "VALUES (?, ?, ?)                   ");
			pstCheckLink = possibleOpenConnection.prepareStatement("SELECT EXISTS "
                    + "(SELECT * FROM link      "
                    + "where                    "
                    + "   sourceURL = ?         "
                    + "   AND targetURL = ?     "   
                    + "   AND provenance_id =  ?  )");
		}
		catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error creating prepared statements", e);
		}
 	}

    @Override
    public void registerProvenanceLink(String provenanceId, DataSource source, String predicate, DataSource target) 
            throws BridgeDbSqlException{
        checkDataSourceInDatabase(source);
        checkDataSourceInDatabase(target);
        String query = "SELECT * FROM provenance "
                    + "WHERE id = \"" + provenanceId + "\"";
        try {
			Statement statement = createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                System.out.println("next");
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
                    + "\"" + provenanceId + "\", " 
                    + "\"" + source.getNameSpace() + "\", " 
                    + "\"" + predicate + "\", " 
                    + "\"" + target.getNameSpace() + "\")";
            System.out.println(query);
            statement.executeUpdate(query);
            System.out.println("2");
        } catch (SQLException ex) {
            System.err.println(ex);
            throw new BridgeDbSqlException ("Error inserting link with " + query, ex);
        }
    }

    @Override
    public void insertLink(String source, String target, String provenanceId) throws IDMapperException {
        boolean exists = false;
        try {
            pstCheckLink.setString(1, source);
            pstCheckLink.setString(2, target);
            pstCheckLink.setString(3, provenanceId);
            ResultSet rs = pstCheckLink.executeQuery();
            if (rs.next()) {
                exists = rs.getBoolean(1);
            }
            if (exists){
                doubleCount++;
                if (doubleCount % BLOCK_SIZE == 0){
                   Reporter.report("Already skipped " + doubleCount + " links that already exist with this provenance");
                }
            } else {
                pstInsertLink.setString(1, source);
                pstInsertLink.setString(2, target);
                pstInsertLink.setString(3, provenanceId);
                pstInsertLink.executeUpdate();
                insertCount++;
                if (insertCount % BLOCK_SIZE == 0){
                    Reporter.report("Inserted " + insertCount + " links loaded so far");
                    possibleOpenConnection.commit();
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex);
            throw new BridgeDbSqlException ("Error inserting link ", ex);
        }
    }

    private void checkXrefValidToLoadInURLDataBase(Xref xref) throws BridgeDbSqlException{
        if (xref.getId() == null) throw new BridgeDbSqlException("The id may not be null");
        DataSource ds = xref.getDataSource();
        if (ds == null) throw new BridgeDbSqlException("The DataSource may not be null");
        if (ds.getNameSpace() == null) throw new BridgeDbSqlException("The DataSource's namespace may not be null");
        if (ds.getNameSpace().isEmpty()) throw new BridgeDbSqlException("The DataSource's namespace may not be empty");
        if (!ds.getUrl("$id").equals(ds.getNameSpace() + "$id")) {
            throw new BridgeDbSqlException("The DataSource's urlPattern was " + ds.getUrl("$id") + " "
                    + "However this implemenation does not allow postfixes (the part after the \"$id\"");
        }
    }
    
    @Override
    public void closeInput() throws BridgeDbSqlException {
        Reporter.report ("Inserted " + this.insertCount + " links");
        Reporter.report ("Skipped " + this.doubleCount + " links that where already there");
        if (possibleOpenConnection != null){
            try {
                possibleOpenConnection.commit();
                possibleOpenConnection.close();
            } catch (SQLException ex) {
               throw new BridgeDbSqlException ("Error closing connection ", ex);
            }
        }
    }

    //***** URLMapper funtctions  *****
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> srcURLs, String... tgtNameSpaces) throws IDMapperException {
        HashMap<String, Set<String>> results = new HashMap<String, Set<String>>();
        for (String ref:srcURLs){
            Set<String> mapped = this.mapURL(ref, tgtNameSpaces);
            results.put(ref, mapped);
        }
        return results;
    }

    @Override
    public Set<String> mapURL(String ref, String... tgtNameSpaces) throws IDMapperException {
        if (ref == null) throw new IDMapperException ("Illegal null ref. Please use a URL");
        if (ref.isEmpty()) throw new IDMapperException ("Illegal empty ref. Please use a URL");
        StringBuilder query = new StringBuilder();
        query.append("SELECT targetURL as url ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        query.append("AND sourceURL = \"");
            query.append(ref);
            query.append("\" ");
        if (tgtNameSpaces.length > 0){    
            query.append("AND ( targetNameSpace = \"");
                query.append(tgtNameSpaces[0]);
                query.append("\" ");
            for (int i = 1; i < tgtNameSpaces.length; i++){
                query.append("OR targetNameSpace = \"");
                    query.append(tgtNameSpaces[i]);
                    query.append("\"");
            }
            query.append(")");
        }
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    private Set<String> resultSetToURLSet(ResultSet rs ) throws IDMapperException{
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

    @Override
    public boolean uriExists(String URL) throws IDMapperException {
        if (URL == null) return false;
        if (URL.isEmpty()) return false;
        String query = "SELECT EXISTS "
                + "(SELECT * FROM link      "
                + "where                    "
                + "       sourceURL = \"" + URL + "\"" 
                + "   OR "
                + "       targetURL = \"" + URL + "\""   
                + ")";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url  "
                + "FROM link      "
                + "where                    "
                + "   sourceURL LIKE \"%" + text + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "where                    "
                + "   targetURL LIKE \"%" + text + "\""
                + "LIMIT " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToURLSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    //***** IDMapper funtctions  *****
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
            System.out.println(query);
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }
    
    private void appendSourceXref(StringBuilder query, Xref ref){
        query.append("AND sourceURL = \"");
            query.append(ref.getUrl());
            query.append("\"");        
    }
    
    private void appendTargetDataSources(StringBuilder query, Collection<DataSource> targetDataSources){
        if (!targetDataSources.isEmpty()){    
            Iterator<DataSource> iterator = targetDataSources.iterator();
            query.append("AND (targetNameSpace = \"");
                query.append(iterator.next().getNameSpace());
                query.append("\" ");
            while (iterator.hasNext()){
                query.append("OR targetNameSpace = \"");
                query.append(iterator.next().getNameSpace());
                    query.append("\" ");
            }
            query.append(")");
        }        
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
    
    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        if (xref.getId() == null || xref.getDataSource() == null){
            return false;
        }
        String query = "SELECT EXISTS "
                + "(SELECT * FROM link      "
                + "where                    "
                + "       sourceURL = \"" + xref.getUrl() + "\"" 
                + "   OR "
                + "       targetURL = \"" + xref.getUrl() + "\""   
                + ")";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url  "
                + "FROM link      "
                + "where                    "
                + "   sourceURL LIKE \"%" + text + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "where                    "
                + "   targetURL LIKE \"%" + text + "\""
                + "LIMIT " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    //***** IDMapperCapabilities funtctions  *****
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

    @Override
    public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT targetNameSpace as nameSpace "
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
        String query = "SELECT EXISTS "
                + "(SELECT * FROM provenance "
                + "WHERE sourceNameSpace = \"" + src.getNameSpace() + "\""
                + "AND targetNameSpace = \"" + tgt.getNameSpace() + "\")";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    /*private ProvenanceStatistics resultSetToProvenanceStatistics(ResultSet rs) throws BridgeDbSqlException{
        ProvenanceLink provenanceLink;
        try {
            provenanceLink = resultSetToProvenanceLink(rs);
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unexpected error converting resultSet tp Provenance.", ex);
        }
        Statement statement =  this.createStatement();
        String query = "SELECT count(*) as count "
                + "FROM LINK "
                + "WHERE  provenance_id = " + provenanceLink.getId();
        int count;
        try {
            ResultSet countRs = statement.executeQuery(query);
            if (countRs.next()){
                count =  countRs.getInt("count");
            } else {
                count = 0; 
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
        return new ProvenanceStatistics(provenanceLink, count);
    }
    
    private void createMissingProvenance(DataSource source, String predicate, DataSource target, 
            String createdBy, long creation, long uploaded) throws ProvenanceException  {
        try {
            checkDataSourceInDatabase(source);
            checkDataSourceInDatabase(target);
        } catch (BridgeDbSqlException ex) {
            throw new ProvenanceException ("Error checking DataSource ", ex);
        }
        if (predicate.length() > PREDICATE_LENGTH){
            throw new ProvenanceException("Unable to store predicate longer than " + PREDICATE_LENGTH + 
                    " so unable to store " + predicate);
        }
        if (createdBy.length() > CREATOR_LENGTH){
            throw new ProvenanceException("Unable to store creator longer than " + CREATOR_LENGTH + 
                    " so unable to store " + createdBy);
        }
        Statement statement;
        try {
            statement = this.createStatement();
        } catch (BridgeDbSqlException ex) {
            throw new ProvenanceException ("Unable to create the statement ", ex);
        }
        String update = "INSERT INTO provenance "
                + "(sourceNameSpace, linkPredicate, targetNameSpace, creator, dateCreated, dateUploaded) "
                + "VALUES ( "
                + "\"" + source.getNameSpace() + "\", "
                + "\"" + predicate + "\", "
                + "\"" + target.getNameSpace() + "\", "
                + "\"" + createdBy + "\", "
                + "\"" + creation + "\", "
                + "\"" + uploaded + "\")";
        try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to run update. " + update, ex);
        }
    }

    @Override
    public ProvenanceStatistics getProvenance(String id) throws BridgeDbSqlException {
        Statement statement = this.createStatement();
        String query = "SELECT id, sourceNameSpace, linkPredicate, targetNamespace "
                + "FROM provenance "
                + "WHERE id = " + id;
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return resultSetToProvenanceStatistics(rs);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException ("Unable to run query. " + query, ex);
        }
    }

    @Override
    public ProvenanceStatistics getProvenanceByPosition(int position) throws BridgeDbSqlException {
        Statement statement = this.createStatement();
        String query = "SELECT id, sourceNameSpace, linkPredicate, targetNamespace, creator, dateCreated, dateUploaded "
                + "FROM provenance "
                + "LIMIT " + position + ", 1";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return resultSetToProvenanceStatistics(rs);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException ("Unable to run query. " + query, ex);
        }
    }

    @Override
    public List<ProvenanceStatistics> getProvenanceByPosition(int position, int limit) throws BridgeDbSqlException {
        Statement statement = this.createStatement();
        String query = "SELECT id, sourceNameSpace, linkPredicate, targetNamespace, creator, dateCreated, dateUploaded "
                + "FROM provenance "
                + "LIMIT " + position + ", " + limit;
        ArrayList<ProvenanceStatistics> results = new ArrayList<ProvenanceStatistics>();
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                results.add(resultSetToProvenanceStatistics(rs));
            } 
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException ("Unable to run query. " + query, ex);
        }
        return results;
    }

    @Override
    public Set<ProvenanceStatistics> getSourceProvenanceByNameSpace(String nameSpace) throws IDMapperException {
        Statement statement = this.createStatement();
        String query = "SELECT id, sourceNameSpace, linkPredicate, targetNamespace, creator, dateCreated, dateUploaded "
                + "FROM provenance "
                + "WHERE sourceNameSpace = \"" + nameSpace + "\"";
        HashSet<ProvenanceStatistics> results = new HashSet<ProvenanceStatistics>(); 
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                results.add(resultSetToProvenanceStatistics(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to run query. " + query, ex);
        }
        return results;
    }

    @Override
    public Set<ProvenanceStatistics> getTargetProvenanceByNameSpace(String nameSpace) throws IDMapperException {
        Statement statement = this.createStatement();
        String query = "SELECT id, sourceNameSpace, linkPredicate, targetNamespace, creator, dateCreated, dateUploaded "
                + "FROM provenance "
                + "WHERE targetNameSpace = \"" + nameSpace + "\"";
        HashSet<ProvenanceStatistics> results = new HashSet<ProvenanceStatistics>(); 
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                results.add(resultSetToProvenanceStatistics(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to run query. " + query, ex);
        }
        return results;
    }
     */
    
    //*** Support method for iteration ****
    /**
     * Gets the Xref currently at this position in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each position can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param position
     * @return
     * @throws IDMapperException 
     */
    public Set<Xref> getXrefByPosition(int position, int limit) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "LIMIT " + position + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<Xref> results = new HashSet<Xref>();
            while (rs.next()){
                String url = rs.getString("url");
                results.add(DataSource.uriToXref(url));
            } 
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    //*** Support method for interation ****
    /**
     * Gets the Xref currently at this position in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each position can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param position
     * @return
     * @throws IDMapperException 
     */
    public Xref getXrefByPosition(int position) throws IDMapperException {
        String query = "SELECT distinct  sourceURL as url "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "LIMIT " + position + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                String url = rs.getString("url");
                return DataSource.uriToXref(url);
            } else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    /**
     * Gets the Xref currently at this position in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each position can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param position
     * @return
     * @throws IDMapperException 
     */
    public Set<Xref> getXrefByPosition(DataSource ds, int position, int limit) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id "
                + "AND sourceNameSpace = \"" + ds.getNameSpace() + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id "
                + "AND targetNameSpace = \"" + ds.getNameSpace() + "\" "
                + "LIMIT " + position + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<Xref> results = new HashSet<Xref>();
            while (rs.next()){
                String url = rs.getString("url");
                results.add(DataSource.uriToXref(url));
            } 
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    /**
     * Gets the Xref currently at this position in the database.
     * 
     * The main purposes of this method are to underpin iteration and to give example Xrefs.
     * It is NOT designed to assign Ids to Xrefs as 
     * which Xref is returned for each position can change if the data changes.
     * 
     * WARNING: THIS METHOD DOES NOT PROVIDE IDS TO Xref OBJECTS.
     * @param position
     * @return
     * @throws IDMapperException 
     */
    public Xref getXrefByPosition(DataSource ds, int position) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id "
                + "AND sourceNameSpace = \"" + ds.getNameSpace() + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id "
                + "AND targetNameSpace = \"" + ds.getNameSpace() + "\" "
                + "LIMIT " + position + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                String url = rs.getString("url");
                return DataSource.uriToXref(url);
            } else {
                System.err.println(query);
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public Iterable<String> getURLIterator(String nameSpace) throws IDMapperException {
        return new ByPositionURLIterator(this, nameSpace);
    }

    @Override
    public Iterable<String> getURLIterator() throws IDMapperException {
        return new ByPositionURLIterator(this);
    }

    @Override
    public Set<String> getURLByPosition(int position, int limit) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "LIMIT " + position + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<String> results = new HashSet<String>();
            while (rs.next()){
                results.add(rs.getString("url"));
            } 
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public String getURLByPosition(int position) throws IDMapperException {
        String query = "SELECT distinct  sourceURL as url "
                + "FROM link      "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "LIMIT " + position + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return rs.getString("url");
            } else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public Set<String> getURLByPosition(String nameSpace, int position, int limit) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id "
                + "AND sourceNameSpace = \"" + nameSpace + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id "
                + "AND targetNameSpace = \"" + nameSpace + "\" "
                + "LIMIT " + position + " , " + limit;
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            HashSet<String> results = new HashSet<String>();
            while (rs.next()){
                results.add(rs.getString("url"));
            } 
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public String getURLByPosition(String nameSpace, int position) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id "
                + "AND sourceNameSpace = \"" + nameSpace + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id "
                + "AND targetNameSpace = \"" + nameSpace + "\" "
                + "LIMIT " + position + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return rs.getString("url");
            } else {
                System.err.println(query);
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    @Override
    public List<URLMapping> getMappings(List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces, 
            List<String> provenanceIds, Integer position, Integer limit){
        StringBuilder query = new StringBuilder();
        query.append("SELECT link.id, sourceURL, targetURL, provenance.id as id, sourceNameSpace, linkPredicate, ");
            query.append("targetNameSpace, creator, dateCreated, dateUploaded ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendURLconditions(query, URLs, sourceURLs, targetURLs);
        appendNameSpacesConditions(query, nameSpaces, sourceNameSpaces, targetNameSpaces);
        appendProvenanceCondition(query, provenanceIds);
        appendLimit(query, position, limit);
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
    public OverallStatistics getOverallStatistics() throws IDMapperException {
        String query = "SELECT count(*) as numberOfMappings, count(distinct(provenance_id)) as numberOfProvenances, "
                + "count(provenance.sourceNameSpace) as numberOfSourceDataSources, "
                + "count(provenance.linkPredicate) as numberOfPredicates, "
                + "count(provenance.targetNameSpace) as numberOfTargetDataSources "
                + "FROM link, provenance "
                + "WHERE provenance_id = provenance.id ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                int numberOfMappings = rs.getInt("numberOfMappings");
                int numberOfProvenances = rs.getInt("numberOfProvenances");
                int numberOfSourceDataSources = rs.getInt("numberOfSourceDataSources");
                int numberOfPredicates= rs.getInt("numberOfPredicates");
                int numberOfTargetDataSources = rs.getInt("numberOfTargetDataSources");
                return new OverallStatistics(numberOfMappings, numberOfProvenances, 
                        numberOfSourceDataSources, numberOfPredicates, numberOfTargetDataSources);
            } else {
                System.err.println(query);
                throw new IDMapperException("o Results for query. " + query);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query. " + query, ex);
        }
    }

    private void appendURLconditions(StringBuilder query, 
            List<String> URLs, List<String> sourceURLs, List<String> targetURLs){
        if (!URLs.isEmpty()){
            query.append("AND (sourceURL = \"");
                query.append(URLs.get(0));
                query.append("\" ");
            query.append("OR targetURL = \"");
                query.append(URLs.get(0));
                query.append("\" ");
            for (int i = 1; i < URLs.size(); i++){
                query.append("OR sourceURL = \"");
                    query.append(URLs.get(i));
                    query.append("\" ");                
                query.append("OR targetURL = \"");
                    query.append(URLs.get(i));
                    query.append("\" ");
            }
            query.append(") ");
        }
        appendSourceURLs(query, sourceURLs);
        if (!targetURLs.isEmpty()){
            query.append("AND (targetURL = \"");
                query.append(targetURLs.get(0));
                query.append("\" ");
            for (int i = 1; i < targetURLs.size(); i++){
                query.append("OR targetURL = \"");
                    query.append(URLs.get(i));
                    query.append("\" ");                
            }
            query.append(") ");
        } 
    }
    
    private void appendSourceURLs(StringBuilder query, Collection<String> sourceURLs){
        if (!sourceURLs.isEmpty()){
            query.append("AND (sourceURL = \"");
            Iterator<String> iterator = sourceURLs.iterator();
                query.append(iterator.next());
                query.append("\" ");
            while (iterator.hasNext()){
                query.append("OR sourceURL = \"");
                    query.append(iterator.next());
                    query.append("\" ");                
            }
            query.append(") ");
        }
    }

    private void appendNameSpacesConditions(StringBuilder query, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces){
        if (!nameSpaces.isEmpty()){
            query.append("AND (sourceNameSpace = \"");
                query.append(nameSpaces.get(0));
                query.append("\" ");
            query.append("OR targetNameSpace = \"");
                query.append(nameSpaces.get(0));
                query.append("\" ");
            for (int i = 1; i < nameSpaces.size(); i++){
                query.append("OR sourceNameSpace = \"");
                    query.append(nameSpaces.get(i));
                    query.append("\" ");                
                query.append("OR targetNameSpace = \"");
                    query.append(nameSpaces.get(i));
                    query.append("\" ");
            }
            query.append(") ");
        }
        if (!sourceNameSpaces.isEmpty()){
            query.append("AND (sourceNameSpaceL = \"");
                query.append(sourceNameSpaces.get(0));
                query.append("\" ");
            for (int i = 1; i < sourceNameSpaces.size(); i++){
                query.append("OR sourceNameSpace = \"");
                    query.append(sourceNameSpaces.get(i));
                    query.append("\" ");                
            }
            query.append(")");
        }
        appendTargetNameSpace(query, targetNameSpaces);
        if (!targetNameSpaces.isEmpty()){
            query.append("AND (targetNameSpace = \"");
                query.append(targetNameSpaces.get(0));
                query.append("\" ");
            for (int i = 1; i < targetNameSpaces.size(); i++){
                query.append("OR targetNameSpace = \"");
                    query.append(targetNameSpaces.get(i));
                    query.append("\" ");                
            }
            query.append(") ");
        } 
    }

    private void appendTargetNameSpace(StringBuilder query, Collection<String> targetNameSpaces){
        if (!targetNameSpaces.isEmpty()){
            Iterator<String> iterator = targetNameSpaces.iterator();
            query.append("AND (targetNameSpace = \"");
                query.append(iterator.next());
                query.append("\" ");
            while (iterator.hasNext()){
                query.append("OR targetNameSpace = \"");
                    query.append(iterator.next());
                    query.append("\" ");                
            }
            query.append(") ");
        } 
    }

    private void appendProvenanceCondition(StringBuilder query, Collection<String> provenanceIds){
        if (!provenanceIds.isEmpty()){
            Iterator<String> iterator = provenanceIds.iterator();
            query.append("AND ( provenance.id = \"");
                query.append(iterator.next());
                query.append("\" ");
            while (iterator.hasNext()){
                query.append("OR provenance.id = \"");
                    query.append(iterator.next());
                    query.append("\" ");                
            }
            query.append(")");
        }   
    }
    
    private void appendLimit(StringBuilder query, Integer position, Integer limit){
        if (position == null) {
            position = 0;
        }
        if (limit == null){
            limit = DEFAULT_LIMIT;
        }
        query.append("LIMIT " + position + ", " + limit);       
    }
    
    private Set<URLMapping> URLMappingParameterError(String error){
        HashSet<URLMapping> results = new HashSet<URLMapping>();
        results.add(new URLMapping(error));
        return results;
    }
    
    private URLMapping resultSetToURLMapping(ResultSet rs) throws SQLException {
        return new URLMapping(
                rs.getInt("link.id"), 
                rs.getString("sourceURL"), 
                rs.getString("targetURL"), 
                rs.getString("provenance.id"),
                rs.getString("linkPredicate"));
    }

    private List<URLMapping> resultSetToURLMappingList(ResultSet rs) throws SQLException {
        ArrayList<URLMapping> results = new ArrayList<URLMapping>();
        while (rs.next()){
            results.add(new URLMapping(
                rs.getInt("link.id"), 
                rs.getString("sourceURL"), 
                rs.getString("targetURL"), 
                rs.getString("provenance.id"),
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

    @Override
    public URLMapping getMapping(int id) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT link.id, sourceURL, targetURL, provenance.id as id, sourceNameSpace, linkPredicate, ");
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

    /*
    @Override
    public DataSourceStatistics getDataSourceStatistics(DataSource dataSource) throws IDMapperException {
        Set<ProvenanceStatistics> sourceProvenance = getSourceProvenanceByNameSpace(dataSource.getNameSpace());
        Set<ProvenanceStatistics> targetProvenance = getTargetProvenanceByNameSpace(dataSource.getNameSpace());
        int numberOfSourceProvenances = sourceProvenance.size();
        int numberOfTargetProvenances = targetProvenance.size();
        int numberOfSourceMappings = 0;
        for (ProvenanceStatistics provenance:sourceProvenance){
            numberOfSourceMappings+= provenance.getNumberOfMappings();
        }
        int numberOfTargetMappings = 0;
        for (ProvenanceStatistics provenance:targetProvenance){
            numberOfTargetMappings+= provenance.getNumberOfMappings();
        }
        return new DataSourceStatistics (dataSource, numberOfSourceProvenances, numberOfTargetProvenances, 
                numberOfSourceMappings, numberOfTargetMappings);
    }

    @Override
    public DataSourceStatistics getDataSourceStatisticsByPosition(int position) throws IDMapperException {
        Statement statement = this.createStatement();
        String query = "SELECT sysCode "
                + "FROM DataSource "           
                + "LIMIT " + position + ",1";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                DataSource dataSource = DataSource.getBySystemCode(rs.getString("sysCode"));
                return getDataSourceStatistics(dataSource);
            }  else {
                return null;
            }
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable run run query "+ query);
        }
    }

    @Override
    public List<DataSourceStatistics> getDataSourceStatisticsByPosition(int position, int limit) 
            throws IDMapperException {
        Statement statement = this.createStatement();
        String query = "SELECT sysCode "
                + "FROM DataSource "           
                + "LIMIT " + position + ", " + limit;
        ArrayList<DataSourceStatistics> results = new ArrayList<DataSourceStatistics>();
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                DataSource dataSource = DataSource.getBySystemCode(rs.getString("sysCode"));
                results.add(getDataSourceStatistics(dataSource));                
            }  
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable run run query "+ query);
        }
        return results;
    }

    @Override
    public DataVersion getDataVersion(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DataVersion getDataVersionByPosition(int position) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DataVersion> getDataVersionByPosition(int position, int limit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DataVersion> getDataVersionByNameSpace(String nameSpace) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MapperStatistics getMapperStatistics() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
*/

    @Override
    public Map<Xref, Set<XrefProvenance>> mapIDProvenance(Collection<Xref> srcXrefs, 
            Collection<String> provenanceIds, Collection<DataSource> targetDataSources) throws IDMapperException {
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
    public Set<XrefProvenance> mapIDProvenance(Xref ref, Collection<String> provenanceIds, 
            Collection<DataSource> targetDataSources) throws IDMapperException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT link.id, sourceURL, targetURL, provenance.id as id, sourceNameSpace, linkPredicate, ");
            query.append("targetNameSpace ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendSourceXref(query, ref);
        appendTargetDataSources(query, targetDataSources);
        appendProvenanceCondition(query, provenanceIds);
        try {
            Statement statement = this.createAStatement();
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToXrefProvenanceSet(rs);
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to run mapIDProvenance", ex, query.toString());
        }
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
        return new XrefProvenance(xref, rs.getString("provenance.id"), rs.getString("linkPredicate"));
    }

    @Override
    public Set<URLMapping> mapURL(Collection<String> sourceURLs, 
            Collection<String> provenanceIds, Collection<String> targetNameSpaces) throws IDMapperException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT link.id, sourceURL, targetURL, provenance.id as id, sourceNameSpace, linkPredicate, ");
            query.append("targetNameSpace ");
        query.append("FROM link, provenance ");
        query.append("WHERE provenance_id = provenance.id ");
        appendSourceURLs(query, sourceURLs);
        appendTargetNameSpace(query, targetNameSpaces);
        appendProvenanceCondition(query, provenanceIds);
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


}
