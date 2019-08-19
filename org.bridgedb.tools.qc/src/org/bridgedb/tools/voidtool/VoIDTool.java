package org.bridgedb.tools.voidtool;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility to extract RDF data from derby database and store it as text file.
 *  Run with two parameters: [database] and [filename]
 *  database already contains the RDF data.
 */
public class VoIDTool {
    private final File Db;

    private SimpleGdb Gdb;
    public FileWriter file ;

    private String fileName;

    /**
     * Reads the database file passed and creates a text file with the name passes containing the RDF data from the database.
     * @param f1 database file
     * @param fileName name of the file to be create
     * @throws IOException
     */
    public VoIDTool(File f1, String fileName) throws  IOException {
        Db = f1;
        file = new FileWriter(fileName+"txt", true);
        this.fileName = fileName;
    }

    /**
      *This function adds data to a file without overriding the existing data
      * @param fileName the file in which the rdf data is to be added.
      * @param str the string that is to be added to the file.
      */
    public void appendStrToFile(String fileName,
                                String str)
    {
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(fileName, true));
            out.write(str);
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occurred" + e);
        }
    }

    public void initDatabases() throws IDMapperException{
        String url = "jdbc:derby:jar:(" + Db + ")database";
        Gdb = SimpleGdbFactory.createInstance("db1", url);
    }

    /**
     * this method creates RDF file from the derby database passed through the CLI Tool
     * @throws SQLException
     * @throws IDMapperException
     */
    public void createRDF() throws SQLException, IDMapperException {
        Connection con = Gdb.getConnection();
        Statement st= con.createStatement();
        boolean isSchemaUpdated = false;
        String sqlSchema = "SELECT schemaversion FROM info ";
        ResultSet schema = st.executeQuery(sqlSchema);
        while (schema.next()) {
            if (schema.getInt("schemaversion") == 4) {
                isSchemaUpdated = true;
            }
        }
        if (isSchemaUpdated) {
            for (DataSource ds : Gdb.getCapabilities().getSupportedSrcDataSources()) {
                String sql = "SELECT * from " + (ds.getFullName()).toUpperCase();
                ResultSet resultSet = st.executeQuery(sql);
                while (resultSet.next()) {
                    appendStrToFile(fileName, resultSet.getString("ATTRIB") + " " + resultSet.getString("VAL"));
                }
            }
        }
        else
            System.out.println("Schema version less than 4 isn't supported");
    }

    public void run() throws IDMapperException, SQLException {
        initDatabases();
        createRDF();
    }
    public static void printUsage(){
        System.out.println ("Expected 2 argument: <database> <fileName>");
    }
    public static void main(String[] args) throws IOException, IDMapperException, SQLException {
        if (args.length != 2) {
            printUsage(); return;
        }
        VoIDTool main = new VoIDTool(new File(args[0]), args[1]);
        DataSourceTxt.init();
        main.run();
    }
}
