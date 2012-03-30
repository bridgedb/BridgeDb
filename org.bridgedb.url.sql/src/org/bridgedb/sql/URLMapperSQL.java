package org.bridgedb.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.iterator.ByPositionURLIterator;
import org.bridgedb.iterator.URLByPosition;
import org.bridgedb.linkset.URLLinkListener;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceException;
import org.bridgedb.provenance.ProvenanceFactory;
import org.bridgedb.provenance.SimpleProvenance;
import org.bridgedb.url.URLIterator;
import org.bridgedb.url.URLMapper;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
public class URLMapperSQL extends CommonSQL 
        implements URLLinkListener, URLMapper, URLIterator, URLByPosition, ProvenanceFactory{
    
    //Numbering should not clash with any GDB_COMPAT_VERSION;
	private static final int SQL_COMPAT_VERSION = 5;
    
    private static final int PREDICATE_LENGTH = 100;
    private static final int CREATOR_LENGTH = 100;

    private PreparedStatement pstInsertLink = null;
    private PreparedStatement pstCheckLink = null;

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
					+ " (   sourceURL VARCHAR(150) NOT NULL,                            "
                            //for functions that require the sourceNameSpace/ DataSource
                    + "     sourceNameSpace VARCHAR(100) NOT NULL,                      "
                            //Again a speed for space choice.
					+ "     targetURL VARCHAR(150) NOT NULL,                               " 
                            //Targets are oftne filtered on NameSpace
					+ "     targetNameSpace VARCHAR(100) NOT NULL,                         "        
					+ "     provenance INT,                                             "  //Type still under review
					+ "     PRIMARY KEY (sourceURL, targetURL, provenance)                 " 
					+ " )									                            ");
        	sh.execute(	"CREATE TABLE                                                   "    
                    + "IF NOT EXISTS                                                    "
					+ "		provenance                                                  " 
					+ " (   id INT AUTO_INCREMENT PRIMARY KEY,                          " 
                    + "     source VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,            "
                    + "     linkPredicate VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL,   "
                    + "     target VARCHAR(" + SYSCODE_LENGTH+ ")  NOT NULL,            "
					+ "     creator VARCHAR (" + CREATOR_LENGTH + "),                   "
                    + "     dateCreated BIGINT NOT NULL,                                  "
                    + "     dateUploaded BIGINT NOT NULL                                  "
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
                    + "(sourceURL, sourceNameSpace,                       "   
                    + " targetURL, targetNameSpace,                     "
                    + " provenance )                            " 
                    + "VALUES (?, ?, ?, ?, ?)                   ");
			pstCheckLink = possibleOpenConnection.prepareStatement("SELECT EXISTS "
                    + "(SELECT * FROM link      "
                    + "where                    "
                    + "   sourceURL = ?         "
                    + "   AND targetURL = ?     "   
                    + "   AND provenance =  ?  )");
		}
		catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error creating prepared statements", e);
		}
 	}

    @Override
    public void insertLink(String source, String target, Provenance provenace) throws IDMapperException {
        boolean exists = false;
        try {
            pstCheckLink.setString(1, source);
            pstCheckLink.setString(2, target);
            pstCheckLink.setInt(3, provenace.getId());
            ResultSet rs = pstCheckLink.executeQuery();
            if (rs.next()) {
                exists = rs.getBoolean(1);
            }
            if (exists){
                doubleCount++;
                if (doubleCount % BLOCK_SIZE == 0){
                    System.out.println("Already skipped " + doubleCount + " links that already exist with this provenance");
                }
            } else {
                pstInsertLink.setString(1, source);
                pstInsertLink.setString(2, provenace.getSource().getNameSpace());
                pstInsertLink.setString(3, target);
                pstInsertLink.setString(4, provenace.getTarget().getNameSpace());
                pstInsertLink.setInt(5, provenace.getId());
                pstInsertLink.executeUpdate();
                insertCount++;
                if (insertCount % BLOCK_SIZE == 0){
                    System.out.println("Inserted " + insertCount + " links loaded so far");
                    possibleOpenConnection.commit();
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            throw new BridgeDbSqlException ("Error inserting link ", ex);
        }
    }

    @Override
    public void insertLink(Xref source, Xref target) throws BridgeDbSqlException {
        boolean exists = false;
        checkXrefValidToLoadInURLDataBase(source);
        checkXrefValidToLoadInURLDataBase(target);
        checkDataSourceInDatabase(source.getDataSource());
        checkDataSourceInDatabase(target.getDataSource());
        try {
            pstCheckLink.setString(1, source.getUrl());
            pstCheckLink.setString(2, target.getUrl());
            pstCheckLink.setInt(3, 0);
            ResultSet rs = pstCheckLink.executeQuery();
            if (rs.next()) {
                exists = rs.getBoolean(1);
            }
            if (exists){
                doubleCount++;
                if (doubleCount % BLOCK_SIZE == 0){
                    System.out.println("Already skipped " + doubleCount + " links that already exist with this provenance");
                }
            } else {
                pstInsertLink.setString(1, source.getUrl());
                pstInsertLink.setString(2, source.getDataSource().getNameSpace());
                pstInsertLink.setString(3, target.getUrl());
                pstInsertLink.setString(4, target.getDataSource().getNameSpace());
                pstInsertLink.setInt(5, 0);
                pstInsertLink.executeUpdate();
                insertCount++;
                if (insertCount % BLOCK_SIZE == 0){
                    System.out.println("Inserted " + insertCount + " links loaded so far");
                    possibleOpenConnection.commit();
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
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
        query.append("SELECT targetURL as url  ");
        query.append("FROM link      ");
        query.append("where                    ");
        query.append("   sourceURL = \"");
            query.append(ref);
            query.append("\"");
        if (tgtNameSpaces.length > 0){    
            query.append("   AND ( "); 
            query.append("      targetNameSpace = \"");
                query.append(tgtNameSpaces[0]);
                query.append("\" ");
            for (int i = 1; i < tgtNameSpaces.length; i++){
                query.append("      OR   ");     
                query.append("      targetNameSpace = \"");
                    query.append(tgtNameSpaces[i]);
                    query.append("\"  ");
            }
            query.append("   )");
        }
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToURLSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(query);
            throw new IDMapperException("Unable to run query.", ex);
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
            throw new IDMapperException("Unable to run query.", ex);
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
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    //***** IDMapper funtctions  *****
    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        if (ref.getId() == null || ref.getDataSource() == null){
            return new HashSet<Xref>();
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT targetURL as url  ");
        query.append("FROM link      ");
        query.append("where                    ");
        query.append("   sourceURL = \"");
            query.append(ref.getUrl());
            query.append("\"");
        if (tgtDataSources.length > 0){    
            query.append("   AND ( "); 
            query.append("      targetNameSpace = \"");
                query.append(tgtDataSources[0].getNameSpace());
                query.append("\" ");
            for (int i = 1; i < tgtDataSources.length; i++){
                query.append("      OR   ");     
                query.append("      targetNameSpace = \"");
                    query.append(tgtDataSources[i].getNameSpace());
                    query.append("\"  ");
            }
            query.append("   )");
        }
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return resultSetToXrefSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(query);
            throw new IDMapperException("Unable to run query.", ex);
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
            throw new IDMapperException("Unable to run query.", ex);
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
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    //***** IDMapperCapabilities funtctions  *****
    @Override
    public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
        String query = "SELECT DISTINCT sourceNameSpace as nameSpace "
                + "FROM link      ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToDataSourceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
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
                + "FROM link      ";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            return resultSetToDataSourceSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException {
        String query = "SELECT EXISTS "
                + "(SELECT * FROM link      "
                + "where                    "
                + "   sourceNameSpace = \"" + src.getNameSpace() + "\""
                + "   AND targetNameSpace = \"" + tgt.getNameSpace() + "\""   
                + ")";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    // **** ProvenanceFactory Methods ****
    @Override
    public Provenance createProvenance(DataSource source, String predicate, DataSource target, 
            String createdBy, long creation, long upload) throws ProvenanceException{
        Provenance result = findProvenanceNumber(source, predicate, target, createdBy, creation);
        if (result != null){
            return result;
        }
        createMissingProvenance(source, predicate, target, createdBy, creation, upload);
        return findProvenanceNumber(source, predicate, target, createdBy, creation);
    }
    
    @Override
    public Provenance createProvenance(DataSource source, String predicate, DataSource target,
            String createdBy, long creation) throws ProvenanceException {
        Provenance result = findProvenanceNumber(source, predicate, target, createdBy, creation);
        if (result != null){
            return result;
        }
        createMissingProvenance(source, predicate, target, createdBy, creation, new GregorianCalendar().getTimeInMillis());
        return findProvenanceNumber(source, predicate, target, createdBy, creation);
    }

    @Override
    public Provenance createProvenace(Provenance first, Provenance second) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Provenance findProvenanceNumber(DataSource source, String predicate, DataSource target, 
            String createdBy, long creation) throws ProvenanceException{
        Statement statement;
        try {
            statement = this.createStatement();
        } catch (BridgeDbSqlException ex) {
            throw new ProvenanceException ("Unable to create the statement ", ex);
        }
        String query = "SELECT id, source, linkPredicate, target, creator, dateCreated, dateUploaded from provenance "
                + "where "
                + "      source = \"" + source.getSystemCode() + "\""
                + "  AND linkPredicate = \"" + predicate +"\"" 
                + "  AND target = \"" + target.getSystemCode() + "\""
                + "  AND creator = \"" + createdBy + "\""
                + "  AND dateCreated = \"" + creation + "\"";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return new SimpleProvenance(rs.getInt("id"), 
                                            DataSource.getBySystemCode(rs.getString("source")),
                                            rs.getString("linkPredicate"), 
                                            DataSource.getBySystemCode(rs.getString("target")),
                                            rs.getString("creator"), 
                                            rs.getLong("dateCreated"), 
                                            rs.getLong("dateUploaded"));
            } else {
                return null;
            }
        } catch (SQLException ex) {
            System.err.println(query);
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to check if Provenance already exists ", ex);
        }
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
                + "(source, linkPredicate, target, creator, dateCreated, dateUploaded) "
                + "VALUES ( "
                + "\"" + source.getSystemCode() + "\", "
                + "\"" + predicate + "\", "
                + "\"" + target.getSystemCode() + "\", "
                + "\"" + createdBy + "\", "
                + "\"" + creation + "\", "
                + "\"" + uploaded + "\")";
        try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            System.err.println(update);
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to check if Provenance already exists ", ex);
        }
    }

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
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
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
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
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
                + "FROM link      "
                + "WHERE "
                + "sourceNameSpace = \"" + ds.getNameSpace() + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "WHERE "
                + "targetNameSpace = \"" + ds.getNameSpace() + "\" "
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
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
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
                + "FROM link      "
                + "WHERE "
                + "sourceNameSpace = \"" + ds.getNameSpace() + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "WHERE "
                + "targetNameSpace = \"" + ds.getNameSpace() + "\" "
                + "LIMIT " + position + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                String url = rs.getString("url");
                return DataSource.uriToXref(url);
            } else {
                System.out.println(query);
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
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
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
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
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public Set<String> getURLByPosition(String nameSpace, int position, int limit) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url "
                + "FROM link      "
                + "WHERE "
                + "sourceNameSpace = \"" + nameSpace + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "WHERE "
                + "targetNameSpace = \"" + nameSpace + "\" "
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
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

    @Override
    public String getURLByPosition(String nameSpace, int position) throws IDMapperException {
        String query = "SELECT distinct sourceURL as url "
                + "FROM link      "
                + "WHERE "
                + "sourceNameSpace = \"" + nameSpace + "\" "
                + "UNION "
                + "SELECT distinct targetUrl as url  "
                + "FROM link      "
                + "WHERE "
                + "targetNameSpace = \"" + nameSpace + "\" "
                + "LIMIT " + position + ",1";
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return rs.getString("url");
            } else {
                System.out.println(query);
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(query);
            ex.printStackTrace();
            throw new IDMapperException("Unable to run query.", ex);
        }
    }

}
