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
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bridgedb.IDMapperException;


//import org.pathvisio.debug.Logger;
//import org.pathvisio.debug.StopWatch;

/**
   DBConnector implementation using the Derby driver, with the database in a
   single, uncompressed zip archive.
   While creating, the data is stored in a temporary directory. 
   This directory will be cleaned up when the database is finalized.
*/
//TODO: make sure the temp directory is cleaned up also when this is not finalized
public class DataDerby extends DBConnector
{
	public static final String DB_FILE_EXT_GDB = "bridge";
	public static final String DB_FILE_EXT_GEX = "pgex";

	String getDbExt() {
		switch(getDbType()) {
		case TYPE_GDB: return DB_FILE_EXT_GDB;
		case TYPE_GEX: return DB_FILE_EXT_GEX;
		default: return "";
		}
	}
	
	private static final String DB_NAME_IN_ZIP = "database";
	
	// name of db, what it will be when the db is finalized.
	private String finalDbName;
	
	// while making a database, it is created in a temporary directory,
	private File tempDbSubdir = null;
	private File tempDbParentdir = null;
	
	private boolean finalized;
	
	/**
	 * Generates a correct JDBC connection string.
	 * @return the JDBC connection string. 
	 */
	private String getDbUrl()
	{
		String url = "jdbc:derby:";
		if (finalized)
		{
			url += "jar:(" + finalDbName + ")" + DB_NAME_IN_ZIP;
		} 
		else 
		{
			url += tempDbSubdir;
		}
		return url;
	}
	
	/**
	 * @param props one of PROP_NONE or PROP_RECREATE. PROP_NONE will
	 *   lead to a normal connection, PROP_RECREATE will lead to destroying
	 *   the old database and creating a clean new one.
	 * @param dbName is the file that will be produced finally.
	 * If dbName doesn't end with the right extension, the right extension will be added.
	 * @return the JDBC database Connection.
	 * @throws IDMapperException if the database connection could not be made,
	 * 	or if the Derby driver could not be loaded.
	 */
	public Connection createConnection(String dbName, int props) throws IDMapperException 
	{
		boolean recreate = (props & PROP_RECREATE) != 0;

		// make sure the final Db name ends with the right extension.
		finalDbName = dbName;
		finalized = !recreate;
		
		if(recreate) 
		{
			finalDbName = dbName.endsWith(getDbExt()) ? dbName : dbName + "." + getDbExt();
			try
			{
				tempDbParentdir = FileUtilsGdb.createTempDir("derby", ".tmp");
				tempDbSubdir = new File (tempDbParentdir, "database");
			}
			catch (IOException e)
			{
				throw new IDMapperException (e);
			}
		}
		
		Properties sysprop = System.getProperties();
		
		try
		{
			sysprop.setProperty("derby.storage.tempDirectory", System.getProperty("java.io.tmpdir"));
			sysprop.setProperty("derby.stream.error.file", File.createTempFile("derby",".log").toString());
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		}
		catch (ClassNotFoundException e)
		{
			throw new IDMapperException (e);
		}
		catch (IOException f)
		{
			throw new IDMapperException (f);
		}
		Properties prop = new Properties();
		prop.setProperty("create", Boolean.toString(recreate));
		
//		StopWatch timer = new StopWatch();
//		timer.start();
		
		String url = getDbUrl();

		Connection con;
		try
		{
			con = DriverManager.getConnection(url, prop);
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e);
		}
		
//		Logger.log.info("Connecting with derby to " + url + ":\t" + timer.stop());		
		return con;
	}
	
	public Connection createNewDatabaseConnection(String dbName) throws IDMapperException 
	{
		return createConnection(FileUtilsGdb.removeExtension(dbName), PROP_RECREATE);
	}
	
	public String finalizeNewDatabase(String dbName) throws IDMapperException 
	{
		if (finalized) return finalDbName; // already finalized.
		
		//Transfer db to zip and clear old dbfiles
		toZip (new File (finalDbName), tempDbSubdir);
		
		FileUtilsGdb.deleteRecursive(tempDbParentdir);
		
		//Return new database file
		return finalDbName;
	}
	
	public void closeConnection(Connection con) throws IDMapperException 
	{
		closeConnection(con, PROP_NONE);
	}
	
	/**
	 * Close the connection to this database.
	 * @param con JDBC Connection object
	 * @param props Passing PROP_FINALIZE for props will cause a full shutdown, which is necessary
	 * after creating a fresh database.
	 * @throws IDMapperException if the database could not be closed.
	 */
	public void closeConnection(Connection con, int props) throws IDMapperException 
	{
		if(con != null) 
		{
			if (props == PROP_FINALIZE)
			{
				//shutdown only necessary after modifying database!!!
				//Otherwise causes problems when loading same database twice.
				try
				{
					DriverManager.getConnection(getDbUrl() + ";shutdown=true");
				}
				catch (SQLException se)  
				{	
					/*
					 In this case a thrown exception signals success. See:
				      http://db.apache.org/derby/docs/10.3/getstart/rwwdactivity3.html
				      if (e.getSQLState().equals("XJ015")) shutdownSuccess= true; //Derby engine
					 */
					if ( se.getSQLState().equals("08006") ) // single file
					{		
//						Logger.log.info ("Database " + getDbUrl() + " shutdown cleanly");
					}
					else throw new IDMapperException (se);
				}
			}
			try
			{
				con.close();
				con = null;
			}
			catch (SQLException se)
			{
				throw new IDMapperException (se);
			}
		}
	}
	
	//TODO: I wonder if this is possible for zipped databases...
	// if not, this can be done together with finalizing.
	public void compact(Connection con) throws IDMapperException 
	{
		try
		{
			con.commit();
			con.setAutoCommit(true);
	
			if (getDbType() == DBConnector.TYPE_GDB)
			{
				CallableStatement cs = con.prepareCall
				("CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE(?, ?, ?)");
				//Gene table
				cs.setString(1, "APP");
				cs.setString(2, "DATANODE");
				cs.setShort(3, (short) 1);
				cs.execute();
				
				//Link table
				cs.setString(1, "APP");
				cs.setString(2, "LINK");
				cs.setShort(3, (short) 1);
				cs.execute();

				cs.setString(1, "APP");
				cs.setString(2, "ATTRIBUTE");
				cs.setShort(3, (short) 1);
				cs.execute();

			}
			else if (getDbType() == DBConnector.TYPE_GEX)
			{
				CallableStatement cs = con.prepareCall
				("CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE(?, ?, ?)");
				//Expression table
				cs.setString(1, "APP");
				cs.setString(2, "EXPRESSION");
				cs.setShort(3, (short) 1);
				cs.execute();
			}
			con.commit(); //Just to be sure...
		}
		catch (SQLException e)
		{
			throw new IDMapperException (e); 
		}
	}
		
	/**
	 * create a zip file from a directory.
	 * @param zipFile output file
	 * @param dbDir input dir
	 */
	private void toZip(File zipFile, File dbDir) 
	{
		try {			
			if(zipFile.exists()) zipFile.delete();
			
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
			out.setMethod(ZipOutputStream.STORED);
			for(File f : dbDir.listFiles()) addFiles(f, DB_NAME_IN_ZIP + '/', out);
			out.closeEntry();
			out.close();
			
			String zipPath = zipFile.getAbsolutePath().replace(File.separatorChar, '/');
			String url = "jdbc:derby:jar:(" + zipPath + ")" + DB_NAME_IN_ZIP;

			DriverManager.getConnection(url);
		
		} catch(IOException e) {
			e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private byte[] buf = new byte[1024];
	
	/**
	 * recursively add files or directory to ZipOutputStream. Skips directories
	 * named "tmp" or files ending with "lck".
	 * 
	 * @param file file or directory to add.
	 * @param dir base directory inside zip
	 * @param out target zip output stream.
	 * @throws IOException on IO error
	 */
	private void addFiles(File file, String dir, ZipOutputStream out) throws IOException 
	{
		if(file.isDirectory()) 
		{
			if(file.getName().equals("tmp")) return; //Skip 'tmp' directory
			
			String newDir = dir + file.getName() + '/';
			ZipEntry add = new ZipEntry(newDir);
			setZipEntryAttributes(file, add);
			out.putNextEntry(add);
			
			for(File f : file.listFiles()) addFiles(f, newDir,out);
		} 
		else 
		{
			if(file.getName().endsWith(".lck")) return; //Skip '*.lck' files
			ZipEntry add = new ZipEntry(dir + file.getName());
			
			setZipEntryAttributes(file, add);
			
			// real clever: putNextEntry has to be called before writing to out,
			// and CRC has to be calculated before putNextEntry
			// so we end up reading all files twice.
			out.putNextEntry(add);
				        
			FileInputStream in = new FileInputStream(file);
			int len;
			while ((len = in.read(buf)) > 0) 
			{
				out.write(buf, 0, len);
			}
			in.close();
		}
	}
	
	/**
	 * Calculates required attributes for ZipEntry.
	 * <ul>
	 * <li>method: ZipEntry.STORED (meaning uncompressed)
	 * <li>crc: calculated
	 * <li>size: file size
	 * <li>compressedSize: also file size, since uncompressed
	 * </ul>
	 * @param z the ZipEntry to set attributes on
	 * @param f the file to calculate the attributes for
	 * @throws IOException when the file couldn't be read  
	 */
	private void setZipEntryAttributes(File f, ZipEntry z) throws IOException 
	{
		z.setTime(f.lastModified());
		z.setMethod(ZipEntry.STORED);
				
		if(f.isDirectory()) 
		{
			z.setCrc(0);
			z.setSize(0);
			z.setCompressedSize(0);
		} 
		else 
		{			
			z.setSize(f.length());
			z.setCompressedSize(f.length());
			z.setCrc(computeCheckSum(f));
		}
	}
	
	/**
	 * Calculate the 32-bit CRC code for a file,
	 * Suitable for ZipEntry.setCrc.
	 * @param f file to check
	 * @throws IOException on file read error
	 * @return CRC code
	 */
	private long computeCheckSum(File f) throws IOException 
	{
		CheckedInputStream cis = new CheckedInputStream(
				new FileInputStream(f), new CRC32());
		byte[] tempBuf = new byte[128];
		while (cis.read(tempBuf) >= 0) { }
		long result = cis.getChecksum().getValue();
		cis.close();
		return result;
	}
	
}
