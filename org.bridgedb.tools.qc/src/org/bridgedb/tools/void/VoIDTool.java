package org.bridgedb.tools.void;

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

public class VoIDTool {
    private final File oldDb;

    private SimpleGdb oldGdb;
    public FileWriter file ;

    private String fileName;
    public VoIDTool(File f1, String fileName) throws  IOException {
        oldDb = f1;
        file = new FileWriter(fileName+"txt", true);
        this.fileName = fileName;
    }

    /**
     *
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
        String url = "jdbc:derby:jar:(" + oldDb+ ")database";
        oldGdb = SimpleGdbFactory.createInstance("db1", url);
    }

    /**
     * this method creates RDF file from the derby database passed through the CLI Tool
     * @throws SQLException
     * @throws IDMapperException
     */
    public void createRDF() throws SQLException, IDMapperException {
        Connection con = oldGdb.getConnection();
        Statement st= con.createStatement();
        for(DataSource ds : oldGdb.getCapabilities().getSupportedSrcDataSources()) {
            String sql = "SELECT * from "+(ds.getFullName()).toUpperCase();
            ResultSet resultSet = st.executeQuery(sql);
            while (resultSet.next()) {
                appendStrToFile(fileName, resultSet.getString("ATTRIB") + " " + resultSet.getString("VAL"));
            }
        }
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
