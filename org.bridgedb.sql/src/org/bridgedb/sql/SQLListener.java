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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.mapping.MappingListener;
import org.bridgedb.utils.StoreType;

/**
 * This is the root class of the SQL stack.
 * It handles the creation of all the tables and handles all inserts, except those only required for URL and UriSpaces
 * 
 * See CreateSQLTables method for an explanation of the tables.
 * 
 * @author Christian
 */
public class SQLListener implements MappingListener{

    //Numbering should not clash with any GDB_COMPAT_VERSION;
	public static final int SQL_COMPAT_VERSION = 21;
  
    //Maximumn size in database
    protected static final int SYSCODE_LENGTH = 100;
    private static final int FULLNAME_LENGTH = 100;
    private static final int MAINURL_LENGTH = 100;
    private static final int URLPATTERN_LENGTH = 100;
    private static final int ID_LENGTH = 100;
    private static final int TYPE_LENGTH = 100;
    private static final int URNBASE_LENGTH = 100;
    private static final int PREDICATE_LENGTH = 100;
    private static final int LINK_SET_ID_LENGTH = 100;
    private static final int KEY_LENGTH= 100; 
    private static final int PROPERTY_LENGTH = 100;
    private static final int SQL_TIMEOUT = 2;
    private static final int MAX_BLOCK_SIZE = 1000;
    
    static final String DATASOURCE_TABLE_NAME = "DataSource";
    static final String INFO_TABLE_NAME = "info";
    static final String MAPPING_TABLE_NAME = "mapping";
    static final String MAPPING_SET_TABLE_NAME = "mappingSet";
    static final String PROPERTIES_TABLE_NAME = "properties";

    static final String FULL_NAME_COLUMN_NAME = "fullName";
    static final String ID_COLUMN_NAME = "id";
    static final String ID_EXAMPLE_COLUMN_NAME = "idExample";
    static final String IS_PRIMARY_COLUMN_NAME = "isPrimary";
    static final String IS_PUBLIC_COLUMN_NAME = "isPublic";
    static final String IS_TRANSITIVE_COLUMN_NAME = "isTransitive";  
    static final String KEY_COLUMN_NAME = "theKey";
    static final String MAIN_URL_COLUMN_NAME = "mainUrl";
    static final String MAPPING_COUNT_COLUMN_NAME = "mappingCount";
    static final String MAPPING_SET_ID_COLUMN_NAME = "mappingSetId";
    static final String MAPPING_SET_DOT_ID_COLUMN_NAME = MAPPING_SET_TABLE_NAME + "." + ID_COLUMN_NAME;
    static final String PREDICATE_COLUMN_NAME = "predicate";
    static final String PROPERTY_COLUMN_NAME = "property";
    static final String SCHEMA_VERSION_COLUMN_NAME = "schemaversion";
    static final String SOURCE_DATASOURCE_COLUMN_NAME = "sourceDataSource";
    static final String SOURCE_ID_COLUMN_NAME = "sourceId";
    static final String SYSCODE_COLUMN_NAME = "sysCode";
    static final String TARGET_ID_COLUMN_NAME = "targetId";
    static final String TARGET_DATASOURCE_COLUMN_NAME = "targetDataSource";
    static final String TYPE_COLUMN_NAME = "type";
    static final String URL_PATTERN_COLUMN_NAME = "urlPattern";
    static final String URN_BASE_COLUMN_NAME = "urnBase";

    protected SQLAccess sqlAccess;
    protected Connection possibleOpenConnection;
    private final int blockSize;
    private int blockCount = 0;
    private int insertCount = 0;
    private int doubleCount = 0;  
    private StringBuilder insertQuery;
    private final boolean supportsIsValid;
    private final String autoIncrement;
    private final static long REPORT_DELAY = 10000;
    private long lastUpdate = 0;
    
    static final Logger logger = Logger.getLogger(SQLListener.class);

    public SQLListener(boolean dropTables, StoreType storeType) throws BridgeDbSqlException{
        this.sqlAccess = SqlFactory.createTheSQLAccess(storeType);
        this.supportsIsValid = SqlFactory.supportsIsValid();
        this.autoIncrement = SqlFactory.getAutoIncrementCommand();
        if (dropTables){
            this.dropSQLTables();
            this.createSQLTables();
            logger.info("Recreated all tables!");
        } else {
            checkVersion();
            loadDataSources();
        }
        if (SqlFactory.supportsMultipleInserts()){
            blockSize = MAX_BLOCK_SIZE;
        } else {
            blockSize = 1;
        }
        //Starting with a block will cause a new query to start.
        blockCount = blockSize ;
        insertCount = 0;
        doubleCount = 0;    
    }
        
    @Override
    public synchronized int registerMappingSet(DataSource source, String predicate, DataSource target, 
            boolean symetric, boolean isTransitive) throws BridgeDbSqlException {
        checkDataSourceInDatabase(source);
        checkDataSourceInDatabase(target);
        int forwardId = registerMappingSet(source, target, predicate, isTransitive);
        if (symetric){
            registerMappingSet(target, source, predicate, isTransitive);
        }
        return forwardId;
    }

    /**
     * One way regiistration of Mapping Set.
     * 
     */
    private int registerMappingSet(DataSource source, DataSource target, String predicate, boolean isTransitive) 
            throws BridgeDbSqlException {
        String query = "INSERT INTO " + MAPPING_SET_TABLE_NAME
                + " (" + SOURCE_DATASOURCE_COLUMN_NAME + ", " 
                    + PREDICATE_COLUMN_NAME + ", " 
                    + TARGET_DATASOURCE_COLUMN_NAME + ", " 
                    + IS_TRANSITIVE_COLUMN_NAME + ") " 
                + " VALUES (" 
                + "'" + source.getSystemCode() + "', " 
                + "'" + predicate + "', " 
                + "'" + target.getSystemCode() + "',"
                + "'" + booleanIntoQuery(isTransitive) + "')";
        Statement statement = createStatement();
        try {
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            throw new BridgeDbSqlException ("Error inserting link with " + query, ex);
        }
        statement = createStatement();
        int autoinc = 0;
        query = "SELECT @@identity";
        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next())
            {
                autoinc = rs.getInt(1);
            } else {
                throw new BridgeDbSqlException ("No result getting new indetity with " + query);
            }
        } catch (SQLException ex) {
            throw new BridgeDbSqlException ("Error getting new indetity with " + query, ex);
        }
        lastUpdate = new Date().getTime();
        logger.info("Registered new Mappingset " + autoinc + " from " + source.getSystemCode() + " to " + target.getSystemCode());
        return autoinc;
    }

    @Override
    public void closeInput() throws BridgeDbSqlException {
        runInsert();
        insertQuery = null;
        logger.info("Finished processing linkset");
        countLinks();
        if (possibleOpenConnection != null){
            try {
                //possibleOpenConnection.commit();
                possibleOpenConnection.close();
            } catch (SQLException ex) {
               throw new BridgeDbSqlException ("Error closing connection ", ex);
            }
        }
        //Starting with a block will cause a new query to start.
        blockCount = blockSize ;
        insertCount = 0;
        doubleCount = 0;   
        updateLastUpdated();
        logger.debug("Closed input");
    }
    
    @Override
    public void insertLink(String sourceId, String targetId, int mappingSet, boolean symetric) throws BridgeDbSqlException {
        insertLink(sourceId, targetId, mappingSet);
        if (symetric){
            insertLink(targetId, sourceId, mappingSet + 1);
        }
    }
    
    /**
     * One way insertion of a link.
     * <p>
     * May store link updates in a StringBuilder to make one large call rather than many small calls.
     */
    private void insertLink(String sourceId, String targetId, int mappingSetId) throws BridgeDbSqlException{
        if (blockCount >= blockSize){
            runInsert();
            insertQuery = new StringBuilder("INSERT INTO ");
            insertQuery.append(MAPPING_TABLE_NAME);
            insertQuery.append(" (");
            insertQuery.append(SOURCE_ID_COLUMN_NAME);
            insertQuery.append(", ");
            insertQuery.append(TARGET_ID_COLUMN_NAME);
            insertQuery.append(", ");
            insertQuery.append(MAPPING_SET_ID_COLUMN_NAME);
            insertQuery.append(") VALUES ");
        } else {
            try {
                insertQuery.append(", ");        
            } catch (NullPointerException ex){
                throw new BridgeDbSqlException("Please run openInput() before insertLink");
            }
        }
        blockCount++;
        insertQuery.append("('");
        insertQuery.append(sourceId);
        insertQuery.append("', '");
        insertQuery.append(targetId);
        insertQuery.append("', ");
        insertQuery.append(mappingSetId);
        insertQuery.append(")");

    }

    /**
     * Runs the insert using the StringBuilder built up by one or more Insert calls.
     * @throws BridgeDbSqlException 
     */
    private void runInsert() throws BridgeDbSqlException{
        if (insertQuery != null) {
           try {
                Statement statement = createStatement();
                //long start = new Date().getTime();
                int changed = statement.executeUpdate(insertQuery.toString());
                //Reporter.report("insertTook " + (new Date().getTime() - start));
                insertCount += changed;
                doubleCount += blockCount - changed;
                long now = new Date().getTime();
                if (now - lastUpdate > REPORT_DELAY){
                    logger.debug("Inserted " + insertCount + " links and ignored " + doubleCount + " so far");
                    lastUpdate = now;
                }
            } catch (SQLException ex) {
                System.err.println(ex);
                throw new BridgeDbSqlException ("Error inserting link ", ex, insertQuery.toString());
            }
        }   
        insertQuery = null;
        blockCount = 0;
    }
    
     /**
	 * Excecutes several SQL statements to drop the tables 
	 * @throws IDMapperException 
	 */
	protected void dropSQLTables() throws BridgeDbSqlException
	{
 		dropTable("info");
 		dropTable("mapping");
 		dropTable("DataSource");
 		dropTable("mappingSet");
 		dropTable("properties");
     }
    
    /**
     * Drops a single table if it exists.
     * <p>
     * Virtuosos appears not to have the if exists syntax so errors are assumed to be table not found.
     * @param name
     * @throws BridgeDbSqlException 
     */
    protected void dropTable(String name) throws BridgeDbSqlException{
        //"IF NOT EXISTS" is unsupported 
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
	  * Excecutes several SQL statements to create the tables and indexes in the database.
      * <p>
      * Table "info" is a control table used by all database version of BridgeDB, inlcuing none OPS ones.
      * If verifies that database is called with the code that matches the schema version.
      * <p>
      * Table DataSource holds the org.bridgedb.DataSource registry between deployments of the service.
      * The whole table is loaded into the DataSource.class regisrty in the constructor.
      * @See org.bridgedb.DataSource.
      * <p>
      * Table "mapping" holds the Id part of the mapping. (The DataSource part is handled by MappingSet)
      * The "id" field is purely for provenace tracking. Ie getting a particular mapping based on its Id.
      * Mappings are only looked up in one direction, so ids are specically source and target.
      * "mappingSetId" is a foreign key to the "mappingSet" table.
      * <p>
      * Table "mappingSet" holds the DataSource part of each Mapping.
      * Specifically it holds the SysCodes for which org.bridgedb.DataSource objects can be looked up.
      * The Ops version will also map SysCodes to UriSpace(s).
      * "predicate" is purely for provenace. (but could be used for Ops Profiles)
      * "isTransitive" is a flag set at the time of loading to identify mappinSet generated using transativity of other sets.
      *     Currently only used by OPS to draw a different line in the graphviz but could be used by profiles.
      * "mappingCount" is a precomputed value @see countLinks() method.
      * <p>
      * Table "properties" underpins bridgeDB properties methods.
      * "isPublic" field dettermines if the key will be returned by the getKeys() method.
	  * @throws IDMapperException 
	  */
	protected void createSQLTables() throws BridgeDbSqlException
	{
        //"IF NOT EXISTS " is not supported
		try 
		{
			Statement sh = createStatement();
 			sh.execute("CREATE TABLE                            "
					+ INFO_TABLE_NAME 
					+ " (    " + SCHEMA_VERSION_COLUMN_NAME + " INTEGER PRIMARY KEY	"
                    + ")");
  			sh.execute( //Add compatibility version of GDB
					"INSERT INTO " + INFO_TABLE_NAME + " VALUES ( " + SQL_COMPAT_VERSION + ")");
            //TODO add organism as required
            sh.execute("CREATE TABLE " + DATASOURCE_TABLE_NAME 
                    + "  (  " + SYSCODE_COLUMN_NAME     + " VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,   "
                    + "     " + IS_PRIMARY_COLUMN_NAME  + " SMALLINT,                                  "
                    + "     " + FULL_NAME_COLUMN_NAME   + " VARCHAR(" + FULLNAME_LENGTH + "),      "
                    + "     " + MAIN_URL_COLUMN_NAME    + " VARCHAR(" + MAINURL_LENGTH + "),        "
                    + "     " + URL_PATTERN_COLUMN_NAME + " VARCHAR(" + URLPATTERN_LENGTH + "),  "
                    + "     " + ID_EXAMPLE_COLUMN_NAME  + " VARCHAR(" + ID_LENGTH + "),           "
                    + "     " + TYPE_COLUMN_NAME        + " VARCHAR(" + TYPE_LENGTH + "),              "
                    + "     " + URN_BASE_COLUMN_NAME    + " VARCHAR(" + URNBASE_LENGTH + ")         "
                    + "  ) ");
            String query = "CREATE TABLE " + MAPPING_TABLE_NAME 
                    + "( " + ID_COLUMN_NAME             + " INT " + autoIncrement + " PRIMARY KEY, " 
                    + "  " + SOURCE_ID_COLUMN_NAME      + " VARCHAR(" + ID_LENGTH + ") NOT NULL, "
        			+ "  " + TARGET_ID_COLUMN_NAME      + " VARCHAR(" + ID_LENGTH + ") NOT NULL, " 
                    + "  " + MAPPING_SET_ID_COLUMN_NAME + " INT(" + LINK_SET_ID_LENGTH + ") "
                    + " ) ";
			sh.execute(query);
            sh.execute("CREATE INDEX sourceFind ON " + MAPPING_TABLE_NAME + " (" + SOURCE_ID_COLUMN_NAME + ") ");
            sh.execute("CREATE INDEX sourceMappingSetFind ON " + MAPPING_TABLE_NAME + " (" + MAPPING_SET_ID_COLUMN_NAME + ", " + SOURCE_ID_COLUMN_NAME + ") ");
         	query =	"CREATE TABLE " + MAPPING_SET_TABLE_NAME 
                    + " (" + ID_COLUMN_NAME                   + " INT " + autoIncrement + " PRIMARY KEY, " 
                    + "     " + SOURCE_DATASOURCE_COLUMN_NAME + " VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL, "
                    + "     " + PREDICATE_COLUMN_NAME         + " VARCHAR(" + PREDICATE_LENGTH + ") NOT NULL, "
                    + "     " + TARGET_DATASOURCE_COLUMN_NAME + " VARCHAR(" + SYSCODE_LENGTH + ")  NOT NULL, "
                    + "     " + IS_TRANSITIVE_COLUMN_NAME     + " SMALLINT, "
                    + "     " + MAPPING_COUNT_COLUMN_NAME     + " INT "
					+ " ) "; 
            sh.execute(query);
            sh.execute ("CREATE TABLE  "
                    + "    " + PROPERTIES_TABLE_NAME
                    + "(   " + KEY_COLUMN_NAME +   "      VARCHAR(" + KEY_LENGTH + ") NOT NULL, "
                    + "    " + PROPERTY_COLUMN_NAME + "    VARCHAR(" + PROPERTY_LENGTH + ") NOT NULL, "
                    + "    " + IS_PUBLIC_COLUMN_NAME + "    SMALLINT "
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
  
    /**
     * 
     * @return
     * @throws BridgeDbSqlException 
     */
    protected Statement createStatement() throws BridgeDbSqlException{
        try {
            if (possibleOpenConnection == null){
                possibleOpenConnection = sqlAccess.getConnection();
            } else if (possibleOpenConnection.isClosed()){
                possibleOpenConnection = sqlAccess.getConnection();
            }    else if (supportsIsValid && !possibleOpenConnection.isValid(SQL_TIMEOUT)){
                possibleOpenConnection.close();
                possibleOpenConnection = sqlAccess.getConnection();
            }  
            return possibleOpenConnection.createStatement();
        } catch (SQLException ex) {
            throw new BridgeDbSqlException ("Error creating a new statement ", ex);
        }
    }
    
    /**
     * Verifies that the Data Source is saved in the database.
     * Updating or adding the Data Source as required.
     * <p>
     * This is required to allow the DataSource registry to be rebuilt if the service is restarted.
     * @param source A DataSource to check
     * @throws BridgeDbSqlException 
     */
    void checkDataSourceInDatabase(DataSource source) throws BridgeDbSqlException{
        Statement statement = this.createStatement();
        String sysCode  = source.getSystemCode();
        if (sysCode == null) {
            throw new BridgeDbSqlException ("Currently unable to handle Datasources with null systemCode");
        }
        if (sysCode.isEmpty()) {
            throw new BridgeDbSqlException ("Currently unable to handle Datasources with empty systemCode");
        }
        String query = "SELECT " + SYSCODE_COLUMN_NAME
                + "   from " + DATASOURCE_TABLE_NAME
                + "   where "
                + "      " + SYSCODE_COLUMN_NAME + " = '" + source.getSystemCode() + "'"; 
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
    
    /**
     * Adds a DataSource to the SQL database.
     * <p>
     * By the time this methods is called the assumption is that the DataSource did not yet exist in the database.
     * @param source DataSource to save.
     * @throws BridgeDbSqlException 
     */
    private void writeDataSource(DataSource source) throws BridgeDbSqlException{
        StringBuilder insert = new StringBuilder ("INSERT INTO ");
        insert.append(DATASOURCE_TABLE_NAME);
        insert.append(" ( ");
        insert.append(SYSCODE_COLUMN_NAME);
        insert.append(", ");
        insert.append(IS_PRIMARY_COLUMN_NAME);
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
            insert.append (", ");
            insert.append (FULL_NAME_COLUMN_NAME);
            insert.append (" ");
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
            insert.append (", ");
            insert.append (MAIN_URL_COLUMN_NAME);
            insert.append (" ");
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
            insert.append (", ");
            insert.append (URL_PATTERN_COLUMN_NAME);
            insert.append (" ");
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
            insert.append (", ");
            insert.append (ID_EXAMPLE_COLUMN_NAME);
            insert.append (" ");
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
            insert.append (", ");
            insert.append (TYPE_COLUMN_NAME);
            insert.append (" ");
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
            insert.append (", ");
            insert.append (URN_BASE_COLUMN_NAME);
            insert.append (" ");
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

    /**
     * Writes all booleans as 1 or 0 because Virtuoso appears not able to handle the boolean type.
     * 1 and 0 can be read as booleans even when saved as integers.
     * @param bool Value to be saved.
     * @return 
     */
    private String booleanIntoQuery(boolean bool){
        if (bool) return "1";
        return "0";
    }
    
    /**
     * Updates the DataBase record assoicated with a DataSource that has previous been dettermined to already exist.
     * 
     * @param source DataSource whose info will be updated/ confirmed.
     * @throws BridgeDbSqlException 
     */
    private void updateDataSource(DataSource source) throws BridgeDbSqlException{
        StringBuilder update = new StringBuilder("UPDATE ");
        update.append (DATASOURCE_TABLE_NAME);
        update.append (" SET ");
        update.append (IS_PRIMARY_COLUMN_NAME);
        update.append (" = ");
        update.append (booleanIntoQuery(source.isPrimary()));
        update.append (" ");       
        String value = source.getFullName();
        if (value != null && !value.isEmpty()){
            if (value.length() > FULLNAME_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for fullName is " + FULLNAME_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", ");
            update.append (FULL_NAME_COLUMN_NAME);
            update.append (" = '");
            update.append (value);
            update.append ("' ");
        }       
        value = source.getMainUrl();
        if (value != null && !value.isEmpty()){
            if (value.length() > MAINURL_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for mainUrl is " + MAINURL_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", ");
            update.append (MAIN_URL_COLUMN_NAME);
            update.append (" = '");
            update.append (value);
            update.append ("' ");
        }
        value = source.getUrl("$id");
        if (value != null && !value.isEmpty()){
            if (value.length() > URLPATTERN_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for URLPattern is " + URLPATTERN_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", ");
            update.append (URL_PATTERN_COLUMN_NAME);
            update.append (" = '");
            update.append (value);
            update.append ("' ");
        }
        value = source.getExample().getId();
        if (value != null && !value.isEmpty()){
            if (value.length() > ID_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for exampleId is " + ID_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", ");
        update.append (ID_EXAMPLE_COLUMN_NAME);
        update.append (" = '");
            update.append (value);
            update.append ("' ");
        }
        value = source.getType();
        if (value != null && !value.isEmpty()){
            if (value.length() > TYPE_LENGTH){
                throw new BridgeDbSqlException("Maximum length supported for type is " + TYPE_LENGTH + 
                        " so unable to save " + value);
            }
            update.append (", ");
        update.append (TYPE_COLUMN_NAME);
        update.append (" = '");
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
            update.append (", ");
        update.append (URN_BASE_COLUMN_NAME);
        update.append (" = '");
            update.append (value);
            update.append ("' ");
        }
        if (source.getSystemCode().length() > SYSCODE_LENGTH ){
            throw new BridgeDbSqlException("Maximum length supported for SystemCode is " + SYSCODE_LENGTH + 
                    " so unable to save " + source.getSystemCode());
        }
        update.append ("WHERE ");
        update.append (SYSCODE_COLUMN_NAME);
        update.append ("  = '");
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

    /**
     * Updates the property LastUpdayes with the current date and time.
     * 
     * @throws BridgeDbSqlException 
     */
    private void updateLastUpdated() throws BridgeDbSqlException {
        String date = new Date().toString();
        putProperty("LastUpdates", date);
    }

    public void putProperty(String key, String value) throws BridgeDbSqlException {
        String delete = "DELETE from " + PROPERTIES_TABLE_NAME + " where " + KEY_COLUMN_NAME + " = '" + key + "'";
        Statement statement = this.createStatement();
        try {
            statement.executeUpdate(delete.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Error Deleting property " + delete, ex);
        }
        String update = "INSERT INTO " + PROPERTIES_TABLE_NAME
                    + " (" + KEY_COLUMN_NAME + ", " + PROPERTY_COLUMN_NAME + ", " + IS_PUBLIC_COLUMN_NAME + " )" 
                    + " VALUES ('" + key + "', '" + value  + "' , 1)  ";
        try {
            statement.executeUpdate(update.toString());
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Error inserting Property " + update, ex);
        }
    }

    /**
     * Loads all the DataSources stored in the database into the DataSource registry.
     * <p>
     * This together with checkDataSourceInDatabase ensures that the DataSource registry is constant between runs.
     * @throws BridgeDbSqlException 
     */
    private void loadDataSources() throws BridgeDbSqlException{
        try {
            Statement statement = this.createStatement();
            String query = "SELECT " + SYSCODE_COLUMN_NAME + ", " + IS_PRIMARY_COLUMN_NAME + ", " 
                    + FULL_NAME_COLUMN_NAME + ", " + MAIN_URL_COLUMN_NAME + ", " + URL_PATTERN_COLUMN_NAME + ", " 
                    + ID_EXAMPLE_COLUMN_NAME + ", " + TYPE_COLUMN_NAME + ", " + URN_BASE_COLUMN_NAME
                    + "   from " + DATASOURCE_TABLE_NAME;           
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                String sysCode = rs.getString(SYSCODE_COLUMN_NAME);
                if (sysCode.equals("null")){
                    sysCode = null;
                }
                String fullName = rs.getString(FULL_NAME_COLUMN_NAME);
                DataSource.Builder builder = DataSource.register(sysCode, fullName);
                builder.primary(rs.getBoolean(IS_PRIMARY_COLUMN_NAME));
                String mainUrl = rs.getString(MAIN_URL_COLUMN_NAME);
                if (mainUrl != null && !mainUrl.isEmpty() && mainUrl.equals("null")){
                    builder.mainUrl(mainUrl);
                }
                String urlPattern = rs.getString(URL_PATTERN_COLUMN_NAME);
                if (urlPattern != null && !urlPattern.isEmpty() && urlPattern.equals("null")){
                    builder.urlPattern(urlPattern);
                }
                String idExample = rs.getString(ID_EXAMPLE_COLUMN_NAME);
                if (idExample != null && !idExample.isEmpty() && idExample.equals("null")){
                    builder.idExample(idExample);
                }
                String type = rs.getString(TYPE_COLUMN_NAME);
                if (type != null && !type.isEmpty() && type.equals("null")){
                    builder.type(type);
                }
                String urnBase = rs.getString(URN_BASE_COLUMN_NAME);
                if (urnBase != null && !urnBase.isEmpty() && urnBase.equals("null")){
                    builder.urnBase(urnBase);
                }
            }
        } catch (SQLException ex) {
            throw new BridgeDbSqlException("Unable to load DataSources");
        }
    }
    
    /**
     * Updates the count variable for each Mapping Sets.
     * <p>
     * This allows the counts of the mappings in each Mapping Set to be quickly returned.
     * @throws BridgeDbSqlException 
     */
    private void countLinks () throws BridgeDbSqlException{
        logger.info ("Updating link count. Please Wait!");
        Statement countStatement = this.createStatement();
        Statement updateStatement = this.createStatement();
        String query = ("select count(*) as mycount, " + MAPPING_SET_ID_COLUMN_NAME 
                + " from " + MAPPING_TABLE_NAME 
                + " group by " + MAPPING_SET_ID_COLUMN_NAME);  
        ResultSet rs;
        try {
            rs = countStatement.executeQuery(query);    
            logger.info ("Count query run. Updating link count now");
            while (rs.next()){
                int count = rs.getInt("mycount");
                String mappingSetId = rs.getString(MAPPING_SET_ID_COLUMN_NAME);  
                String update = "update " + MAPPING_SET_TABLE_NAME 
                        + " set " + MAPPING_COUNT_COLUMN_NAME + " = " + count 
                        + " where " + ID_COLUMN_NAME + " = '" + mappingSetId + "'";
                try {
                    int updateCount = updateStatement.executeUpdate(update);
                    if (updateCount != 1){
                        throw new BridgeDbSqlException("Updated rows <> ! when running " + update);
                    }
                } catch (SQLException ex) {
                     throw new BridgeDbSqlException("Unable to run update. " + update, ex);
                }
            }
            logger.info ("Updating counts finished!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
    }
    
}
