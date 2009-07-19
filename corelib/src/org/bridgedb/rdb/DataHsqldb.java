// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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
package org.bridgedb.rdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.bridgedb.IDMapperException;


//import org.pathvisio.debug.Logger;
//import org.pathvisio.debug.StopWatch;

/**
   DBConnector implementation using the hsqldb driver
*/
public class DataHsqldb extends DBConnector
{
	static final String DB_FILE_EXT = "properties";
	static final String[] DB_EXT = new String[] { "*." + DB_FILE_EXT };
	static final String[] DB_EXT_NAMES = new String[] { "Hsqldb Database" };
		
	public Connection createConnection(String dbName, int props) throws IDMapperException 
	{
		Connection con;
		try
		{
			boolean recreate = (props & PROP_RECREATE) != 0;
			if(recreate) {
				File dbFile = dbName2File(dbName);
				if(dbFile.exists()) dbFile.delete();
			}
			
			dbName = file2DbName(dbName);
			
			Class.forName("org.hsqldb.jdbcDriver");
			Properties prop = new Properties();
			prop.setProperty("user","sa");
			prop.setProperty("password","");
			prop.setProperty("hsqldb.default_table_type", "cached");
			prop.setProperty("ifexists", Boolean.toString(!recreate));
			
//			StopWatch timer = new StopWatch();
//			timer.start();
			con = DriverManager.getConnection("jdbc:hsqldb:file:" + dbName, prop);
//			Logger.log.info("Connecting with hsqldb to " + dbName + ":\t" + timer.stop());
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
		catch (ClassNotFoundException f)
		{
			throw new IDMapperException (f);
		}
	
		return con;
	}

	public void closeConnection(Connection con) throws IDMapperException 
	{	
		closeConnection(con, PROP_NONE);
	}
	
	public void closeConnection(Connection con, int props) throws IDMapperException 
	{
		try
		{
			boolean compact = (props & PROP_FINALIZE) != 0;
			if(con != null) {
				Statement sh = con.createStatement();
				sh.executeQuery("SHUTDOWN" + (compact ? " COMPACT" : ""));
				sh.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
	}
	
	File dbName2File(String dbName) {
		return new File(dbName + '.' + DB_FILE_EXT);
	}
	
	String file2DbName(String fileName) {
		String end = '.' + DB_FILE_EXT;
		return fileName.endsWith(end) ? 
				fileName.substring(0, fileName.length() -  end.length()) : fileName;
	}
	
	public void setDatabaseReadonly(String dbName, boolean readonly) {
		 setPropertyReadOnly(dbName, readonly);
	}
	
	void setPropertyReadOnly(String dbName, boolean readonly) {
    	Properties prop = new Properties();
		try {
			File propertyFile = dbName2File(dbName);
			prop.load(new FileInputStream(propertyFile));
			prop.setProperty("hsqldb.files_readonly", Boolean.toString(readonly));
			prop.store(new FileOutputStream(propertyFile), "HSQL Database Engine");
			} catch (Exception e) {
//				Logger.log.error("Unable to set database properties to readonly", e);
				//TODO: Better handle execption
			}
	}

	Connection newDbCon;
	public Connection createNewDatabaseConnection(String dbName) throws IDMapperException 
	{
		newDbCon = createConnection(dbName, PROP_RECREATE);
		return newDbCon;
	}

	public String finalizeNewDatabase(String dbName) throws IDMapperException 
	{
		if(newDbCon != null) closeConnection(newDbCon, PROP_FINALIZE);
		setPropertyReadOnly(dbName, true);
		return dbName;
	}
}
