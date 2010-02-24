package org.bridgedb.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Script to copy a derby bridgedb database to MySQL.
 * 
 * Note: this script doesn't create the tables and indexes. You need
 * to setup an empty MySQL database with table and index structure first. To base
 * this on an existing derby database, use the dblook tool distributed with Derby, e.g.:
 * 
 * dblook -d "jdbc:derby:jar:(~/Mm_Derby_20090720.bridge)database" -o ~/Mm_Derby_20090720.sql
 * 
 * The resulting sql file needs to be reformatted a bit to make it work with MySQL:
 * - remove all '"' and '"APP".'
 * - change everything to lowercase
 * 
 * @author thomas
 */
public class Derby2MySQL {
	public static void main(String[] args) {
		try {
			String url_derby = "jdbc:derby:jar:(/home/thomas/data/bridgedb/Mm_Derby_20090720.bridge)database";
			String url_mysql = "jdbc:mysql://localhost/bridge_Mm_20090720";

			// Connect to the derby database
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Connection con_derby = DriverManager.getConnection(url_derby, "",
					"");
			// Connect to the mysql database
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection con_mysql = DriverManager.getConnection(url_mysql,
					"root", "");

			// Copy over each table

			ResultSet r = null;

			// Table DATANODE
			System.err.println("Processing DATANODE");
			r = con_derby.createStatement().executeQuery(
					"SELECT * FROM datanode");
			PreparedStatement pstDatanode = con_mysql
					.prepareStatement("INSERT IGNORE INTO datanode (id, code) VALUES (?, ?)");
			while (r.next()) {
				pstDatanode.setString(1, r.getString("id"));
				pstDatanode.setString(2, r.getString("code"));
				pstDatanode.execute();
			}

			pstDatanode.close();
			r.close();

			// Table LINK
			System.err.println("Processing LINK");
			r = con_derby.createStatement().executeQuery("SELECT * FROM link");
			PreparedStatement pstLink = con_mysql
					.prepareStatement("INSERT IGNORE INTO link (idLeft, codeLeft, idRight, codeRight) VALUES (?, ?, ?, ?)");
			while (r.next()) {
				pstLink.setString(1, r.getString("idLeft"));
				pstLink.setString(2, r.getString("codeLeft"));
				pstLink.setString(3, r.getString("idRight"));
				pstLink.setString(4, r.getString("codeRight"));
				pstLink.execute();
			}

			pstLink.close();
			r.close();
			
			// Table ATTRIBUTE
			System.err.println("Processing ATTRIBUTE");
			r = con_derby.createStatement().executeQuery("SELECT * FROM attribute");
			PreparedStatement pstAttr = con_mysql
					.prepareStatement("INSERT IGNORE INTO attribute (id, code, attrName, attrValue) VALUES (?, ?, ?, ?)");
			while (r.next()) {
				pstAttr.setString(1, r.getString("id"));
				pstAttr.setString(2, r.getString("code"));
				pstAttr.setString(3, r.getString("attrName"));
				pstAttr.setString(4, r.getString("attrValue"));
				pstAttr.execute();
			}

			pstAttr.close();
			r.close();
			
			// Table INFO
			System.err.println("Processing INFO");
			r = con_derby.createStatement().executeQuery("SELECT * FROM info");
			PreparedStatement pstInfo = con_mysql
					.prepareStatement("INSERT IGNORE INTO info " +
							"(buildDate, schemaVersion, dataSourceName, dataSourceVersion, species, dataType) " +
							"VALUES (?, ?, ?, ?, ?, ?)");
			while (r.next()) {
				pstInfo.setString(1, r.getString("buildDate"));
				pstInfo.setString(2, r.getString("schemaVersion"));
				pstInfo.setString(3, r.getString("dataSourceName"));
				pstInfo.setString(4, r.getString("dataSourceVersion"));
				pstInfo.setString(5, r.getString("species"));
				pstInfo.setString(6, r.getString("dataType"));
				pstInfo.execute();
			}

			pstInfo.close();
			r.close();
			
			//Close connections
			con_derby.close();
			con_mysql.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
