package org.bridgedb.sql;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.ws.ByPossitionIterator;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.linkset.LinkListener;
import org.bridgedb.provenance.Provenance;
import org.bridgedb.provenance.ProvenanceException;
import org.bridgedb.provenance.ProvenanceFactory;
import org.bridgedb.provenance.SimpleProvenance;
import org.bridgedb.ws.XrefByPossition;

/**
 * UNDER DEVELOPMENT
 * See package.html
 * 
 * @author Christian
 */
public abstract class CommonSQL implements IDMapper, IDMapperCapabilities, LinkListener, ProvenanceFactory, 
        XrefIterator, XrefByPossition{
        
    static final int BLOCK_SIZE = 10000;
    
    private static final int SQL_TIMEOUT = 2;
    static final int SYSCODE_LENGTH = 100;
    private static final int FULLNAME_LENGTH = 100;
    private static final int MAINURL_LENGTH = 100;
    private static final int URLPATTERN_LENGTH = 100;
    static final int ID_LENGTH = 100;
    private static final int TYPE_LENGTH = 100;
    private static final int URNBASE_LENGTH = 100;
    private static final int PREDICATE_LENGTH = 100;
    private static final int CREATOR_LENGTH = 100;
    
    Connection possibleOpenConnection;
    private SQLAccess sqlAccess;
    int insertCount = 0;
    int doubleCount = 0;
    
    public CommonSQL(SQLAccess sqlAccess) throws BridgeDbSqlException{
        if (sqlAccess == null){
            throw new IllegalArgumentException("sqlAccess can not be null");
        }
        this.sqlAccess = sqlAccess;
      //  checkVersion();
    }   

    Statement createStatement() throws BridgeDbSqlException{
        try {
            if (possibleOpenConnection == null){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (possibleOpenConnection.isClosed()){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (!possibleOpenConnection.isValid(SQL_TIMEOUT)){
                possibleOpenConnection.close();
                possibleOpenConnection = sqlAccess.getConnection();
            }  
            return possibleOpenConnection.createStatement();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException ("Error creating a new statement");
        }
    }
    
    abstract boolean correctVersion(int currentVersion);
    
    /**
     * Checks that the schema is for this version.
     * 
     * @throws BridgeDbSqlException If the schema version is not the expected one.
     */
	public void checkVersion() throws BridgeDbSqlException
	{
        Statement stmt = createStatement();
        ResultSet r = null;
        int version = 0;
        try {
            r = stmt.executeQuery("SELECT schemaversion FROM info");
            if(r.next()) version = r.getInt(1);
        } catch (SQLException e) {
            //probably new databse do nothing.
            return;
        }
		finally
		{
            if (r != null) try { r.close(); } catch (SQLException ignore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ignore) {}
		}
        if (correctVersion(version)) return;
 		switch (version)
		{
    		case 2:
        		throw new BridgeDbSqlException("Please use the SimpleGdbFactory in the org.bridgedb.rdb package");
            case 3:
                throw new BridgeDbSqlException("Please use the SimpleGdbFactory in the org.bridgedb.rdb package");
            case 4:
                throw new BridgeDbSqlException("Please use the IDMapperSQL class");
            case 5:
                throw new BridgeDbSqlException("Please use the URlMapperSQL class");
            //NB add future schema versions here
            default:
                throw new BridgeDbSqlException ("Unrecognized schema version '" + version + "', please make sure you have the latest " +
					"version of this software and databases");
		}		
	}

    /**
	 * Excecutes several SQL statements to create the tables and indexes in the database the given
	 * connection is connected to
	 * @throws IDMapperException 
	 */
	public void createSQLTables() throws BridgeDbSqlException
	{
		try 
		{
			Statement sh = createStatement();
 			sh.execute("CREATE TABLE                            "
                    + "IF NOT EXISTS                            "
					+ "info                                     " 
					+ "(    schemaversion INTEGER PRIMARY KEY	"
                    + ")");
            //provenance table sitll under development.
            //TODO add organism as required
            sh.execute("CREATE TABLE  "
                    + "IF NOT EXISTS  "
                    + "     DataSource "
                    + "  (  sysCode VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,   "
                    + "     isPrimary BOOLEAN,                              "
                    + "     fullName VARCHAR(" + FULLNAME_LENGTH + "),      "
                    + "     mainUrl VARCHAR(" + MAINURL_LENGTH + "),        "
                    + "     urlPattern VARCHAR(" + URLPATTERN_LENGTH + "),  "
                    + "     idExample VARCHAR(" + ID_LENGTH + "),           "
                    + "     type VARCHAR(" + TYPE_LENGTH + "),              "
                    + "     urnBase VARCHAR(" + URNBASE_LENGTH + ")         "
                    + "  ) ");
        	sh.execute(	"CREATE TABLE                                                   "    
                    + "IF NOT EXISTS                                                    "
					+ "		provenance                                                  " 
					+ " (   id INT AUTO_INCREMENT PRIMARY KEY,                          " 
                    + "     source VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,            "
                    + "     linkPredicate VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL,   "
                    + "     target VARCHAR(" + SYSCODE_LENGTH+ ")  NOT NULL,            "
					+ "     creator VARCHAR (" + CREATOR_LENGTH + "),                   "
                    + "     dateCreated DATE NOT NULL,                                  "
                    + "     dateUploaded DATE NOT NULL                                  "
					+ " ) ");   
           sh.close();
		} catch (SQLException e)
		{
            System.err.println(e);
            e.printStackTrace();
			throw new BridgeDbSqlException ("Error creating the tables ", e);
		}
	}

    /**
	 * Excecutes several SQL statements to drop the tables 
	 * @throws IDMapperException 
	 */
	public void dropSQLTables() throws BridgeDbSqlException
	{
    	Statement sh = createStatement();
		try 
		{
 			sh.execute("DROP TABLE  "
                    + "IF EXISTS    "
					+ "info         ");
			sh.execute("DROP TABLE  "
                    + "IF EXISTS    "
                    + "link         ");
			sh.execute("DROP TABLE  "
                    + "IF EXISTS    "
                    + "DataSource   ");
            //provenance table sitll under development.
			sh.execute(	"DROP TABLE " 
                    + "IF EXISTS    "
					+ "provenance   ");   
            sh.close();
		} catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error dropping the tables ", e);
		}
	}

    public void init() throws BridgeDbSqlException {
		try
		{
            if (possibleOpenConnection == null){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (possibleOpenConnection.isClosed()){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (!possibleOpenConnection.isValid(SQL_TIMEOUT)){
                possibleOpenConnection.close();
                possibleOpenConnection = sqlAccess.getConnection();
            }  
  			possibleOpenConnection.setAutoCommit(false);
		}
		catch (SQLException e)
		{
			throw new BridgeDbSqlException ("Error creating prepared statements", e);
		}
        insertCount = 0;
        doubleCount = 0;
	}

/*    @Override
    public void insertLink(URI source, String predicate, URI target) throws BridgeDbSqlException {
        DataSource sourceDataSource = DataSource.getByNameSpace(source.getNamespace());
        String sourceCode = sourceDataSource.getSystemCode();
        DataSource targetDataSource = DataSource.getByNameSpace(target.getNamespace());
        String targetCode = targetDataSource.getSystemCode();
        try {
            pstInsertLink.setString(1, source.getLocalName());
            pstInsertLink.setString(2, sourceDataSource.getSystemCode());
            pstInsertLink.setString(3, target.getLocalName());
            pstInsertLink.setString(4, targetDataSource.getSystemCode());
            pstInsertLink.setInt(5, 0);
            pstInsertLink.executeUpdate();
            insertCount++;
            if (insertCount % BLOCK_SIZE == 0){
                System.out.println("Inserted " + insertCount + " links loaded so far");
                possibleOpenConnection.commit();
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            throw new BridgeDbSqlException ("Error inserting link");
        }
    }
*/

    @Override
    public void closeInput() throws BridgeDbSqlException {
        System.out.println ("Inserted " + this.insertCount + " links");
        System.out.println ("Skipped " + this.doubleCount + " links that where already there");
        if (possibleOpenConnection != null){
            try {
                possibleOpenConnection.commit();
                possibleOpenConnection.close();
            } catch (SQLException ex) {
               throw new BridgeDbSqlException ("Error closing connection ", ex);
            }
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
                + "  AND dateCreated = \"" + new Date(creation).toString() + "\"";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()){
                return new SimpleProvenance(rs.getInt("id"), 
                                            DataSource.getBySystemCode(rs.getString("source")),
                                            rs.getString("linkPredicate"), 
                                            DataSource.getBySystemCode(rs.getString("target")),
                                            rs.getString("creator"), 
                                            rs.getDate("dateCreated").getTime(), 
                                            rs.getDate("dateUploaded").getTime());
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
                + "\"" + new Date(creation).toString() + "\", "
                + "\"" + new Date(uploaded).toString() + "\")";
        try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            System.err.println(update);
            ex.printStackTrace();
            throw new ProvenanceException ("Unable to check if Provenance already exists ", ex);
        }
    }

    private void checkDataSourceInDatabase(DataSource source) throws BridgeDbSqlException{
        Statement statement = this.createStatement();
        String sysCode  = source.getSystemCode();
        if (sysCode == null) {
            throw new BridgeDbSqlException ("Currently unable to handle Datasources with null systemCode");
        }
        if (sysCode.isEmpty()) {
            throw new BridgeDbSqlException ("Currently unable to handle Datasources with empty systemCode");
        }
        String query = "SELECT EXISTS"
                + "(  SELECT sysCode"
                + "   from DataSource "
                + "   where "
                + "      sysCode = \"" + source.getSystemCode() + "\")"; 
        boolean found;
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            found = rs.getBoolean(1);
        } catch (SQLException ex) {
            System.out.println(query);
            throw new BridgeDbSqlException("Unable to check provenace", ex);
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
        values.append("\"");
        values.append(source.getSystemCode());
        values.append("\" , ");
        values.append (source.isPrimary());
        String value = source.getFullName(); 
        if (value != null && !value.isEmpty()){
            if (value.length() > FULLNAME_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for fullName is " + FULLNAME_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", fullName ");
            values.append (", \"");
            values.append (value);
            values.append ("\" ");
        }
        value = source.getMainUrl();
        if (value != null && !value.isEmpty()){
            if (value.length() > MAINURL_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for mainUrl is " + MAINURL_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", mainUrl ");
            values.append (", \"");
            values.append (value);
            values.append ("\" ");
        }
        value = source.getUrl("$id");
        if (value != null && !value.isEmpty()){
            if (value.length() > URLPATTERN_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for URLPattern is " + URLPATTERN_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", urlPattern ");
            values.append (", \"");
            values.append (value);
            values.append ("\" ");
        }
        value = source.getExample().getId();
        if (value != null && !value.isEmpty()){
            if (value.length() > ID_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for exampleId is " + ID_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", idExample ");
            values.append (", \"");
            values.append (value);
            values.append ("\" ");
        }
        value = source.getType();
        if (value != null && !value.isEmpty()){
            if (value.length() > TYPE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for type is " + TYPE_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append (", type ");
            values.append (", \"");
            values.append (value);
            values.append ("\" ");
        }
        value = source.getURN("");
        //remove the :
        value = value.substring(0, value.length()-1);
        if (value != null && !value.isEmpty()){
            if (value.length() > URNBASE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for urnBase is " + URNBASE_LENGTH + 
                        " so unable to save " + value);
            }
            insert.append ("SET urnBase = \"");
            values.append (", \"");
            values.append (value);
            values.append ("\" ");
        }
        if (source.getOrganism() != null){
            throw new BridgeDbSqlException("Sorry DataSource oraginism filed is upsupported");
        }
        Statement statement = this.createStatement();
        try {
            statement.executeUpdate(insert.toString() + ") " + values.toString() + " )");
        } catch (SQLException ex) {
            System.out.println(insert.toString() + ") " + values.toString() + " )");
            throw new BridgeDbSqlException("Unable to writeDataSource", ex);
        }
    }

    private void updateDataSource(DataSource source) throws BridgeDbSqlException{
        StringBuilder update = new StringBuilder("UPDATE DataSource ");
        update.append ("SET isPrimary = ");
        update.append (source.isPrimary());
        update.append (" ");       
        String value = source.getFullName();
        if (value != null && !value.isEmpty()){
            if (value.length() > FULLNAME_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for fullName is " + FULLNAME_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", fullName = \"");
            update.append (value);
            update.append ("\" ");
        }       
        value = source.getMainUrl();
        if (value != null && !value.isEmpty()){
            if (value.length() > MAINURL_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for mainUrl is " + MAINURL_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", mainUrl = \"");
            update.append (value);
            update.append ("\" ");
        }
        value = source.getUrl("$id");
        if (value != null && !value.isEmpty()){
            if (value.length() > URLPATTERN_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for URLPattern is " + URLPATTERN_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", urlPattern = \"");
            update.append (value);
            update.append ("\" ");
        }
        value = source.getExample().getId();
        if (value != null && !value.isEmpty()){
            if (value.length() > ID_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for exampleId is " + ID_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", idExample = \"");
            update.append (value);
            update.append ("\" ");
        }
        value = source.getType();
        if (value != null && !value.isEmpty()){
            if (value.length() > TYPE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for type is " + TYPE_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", type = \"");
            update.append (value);
            update.append ("\" ");
        }
        value = source.getURN("");
        //remove the :
        value = value.substring(0, value.length()-1);
        if (value != null && !value.isEmpty()){
            if (value.length() > URNBASE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for urnBase is " + URNBASE_LENGTH + 
                        " so unable to save " + value);
            }
            update.append ("SET urnBase = \"");
            update.append (value);
            update.append ("\" ");
        }
        if (source.getSystemCode().length() > SYSCODE_LENGTH ){
            throw new BridgeDbSqlException("Maximum length supported for SystemCode is " + SYSCODE_LENGTH + 
                    " so unable to save " + source.getSystemCode());
        }
        update.append ("WHERE sysCode  = \"");
        update.append (source.getSystemCode());
        update.append ("\" ");
        if (source.getOrganism() != null){
            throw new BridgeDbSqlException("Sorry DataSource oraginism filed is upsupported");
        }
        Statement statement = this.createStatement();
        try {
            statement.executeUpdate(update.toString());
        } catch (SQLException ex) {
            System.out.println(update);
            throw new BridgeDbSqlException("Unable to updateDataSource", ex);
        }
    }
    
    //***** IDMapper funtctions  *****
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return this;
    }

    private boolean isConnected = true;
    // In the case of DataCollection, there is no need to discard associated resources.
    
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
        return true;
    }

    @Override
    public String getProperty(String key) {
        return null;
    }

    @Override
    public Set<String> getKeys() {
        return new HashSet<String>();
    }

    //*** Support method for iteration ****

    @Override
    public Iterable<Xref> getIterator(DataSource ds) throws IDMapperException {
        return new ByPossitionIterator(this, ds);
    }

    @Override
    public Iterable<Xref> getIterator() throws IDMapperException {
        return new ByPossitionIterator(this);
    }
}
