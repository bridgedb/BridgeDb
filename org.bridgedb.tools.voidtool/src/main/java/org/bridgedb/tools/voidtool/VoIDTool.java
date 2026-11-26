/* Copyright (c) 2019 Manas Awasthi <marvex17@gmail.com>
 *               2026 Egon Willighagen <egonw@users.sf.net>
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */

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
 * Utility to extract the VoID data from a BridgeDb database (schema 4, Derby
 * or DuckDB) and store it as Turtle.
 *  Run with two parameters: [database] and [filename]
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
        file = new FileWriter(fileName, false); // false -> overwrite
        file.close(); // we can close it, because appendStrToFile opens it anew
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

    /**
     * Opens the database, either a Derby or a DuckDB BridgeDb database.
     * The JDBC driver is loaded by name, as the jar-with-dependencies may not
     * register it: its META-INF/services/java.sql.Driver lists only one driver.
     * @throws IDMapperException if the format is not recognized, or the database could not be opened
     */
    public void initDatabases() throws IDMapperException{
        String url;
        switch (DbFormatDetector.detect(Db)) {
        case DERBY:
            System.out.println("INFO: database '" + Db + "' detected as a Derby database");
            loadDriver("org.apache.derby.jdbc.EmbeddedDriver");
            url = "jdbc:derby:jar:(" + Db + ")database";
            break;
        case DUCKDB:
            System.out.println("INFO: database '" + Db + "' detected as a DuckDB database");
            loadDriver("org.duckdb.DuckDBDriver");
            url = "jdbc:duckdb:" + Db;
            break;
        default:
            throw new IDMapperException("Unrecognized database format for '" + Db + "'");
        }
        Gdb = SimpleGdbFactory.createInstance("db1", url);
    }

    private static void loadDriver(String className) throws IDMapperException {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IDMapperException("Could not find the JDBC driver " + className + " on the classpath", e);
        }
    }

    /**
     * The namespaces of the output, as in background/wikipathways-rdf-void.ttl.
     * Attributes and values in the VoID tables can use these prefixes.
     */
    private static final String[][] PREFIXES = {
        {"biopax", "http://www.biopax.org/release/biopax-level3.owl#"},
        {"cito", "http://purl.org/spar/cito/"},
        {"dcat", "http://www.w3.org/ns/dcat#"},
        {"dcterms", "http://purl.org/dc/terms/"},
        {"foaf", "http://xmlns.com/foaf/0.1/"},
        {"freq", "http://purl.org/cld/freq/"},
        {"gpml", "http://vocabularies.wikipathways.org/gpml#"},
        {"hmdb", "https://identifiers.org/hmdb/"},
        {"ncbigene", "https://identifiers.org/ncbigene/"},
        {"owl", "http://www.w3.org/2002/07/owl#"},
        {"pav", "http://purl.org/pav/"},
        {"prov", "http://www.w3.org/ns/prov#"},
        {"pubmed", "http://www.ncbi.nlm.nih.gov/pubmed/"},
        {"rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"},
        {"rdfs", "http://www.w3.org/2000/01/rdf-schema#"},
        {"skos", "http://www.w3.org/2004/02/skos/core#"},
        {"void", "http://rdfs.org/ns/void#"},
        {"wp", "http://vocabularies.wikipathways.org/wp#"},
        {"xsd", "http://www.w3.org/2001/XMLSchema#"},
    };

    /**
     * Writes the VoID data of the database as Turtle. Each VoID table, named
     * after the system code of its data source, becomes one void:Linkset with
     * that system code as relative IRI, and the full name of the data source
     * as dcterms:title. Each row of the table adds one property, in the order
     * of the rows.
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
        if (!isSchemaUpdated) {
            System.out.println("Schema version less than 4 isn't supported");
            return;
        }

        StringBuilder ttl = new StringBuilder();
        for (String[] prefix : PREFIXES) {
            ttl.append(String.format("@prefix %-9s <%s> .%n", prefix[0] + ":", prefix[1]));
        }
        for (DataSource ds : Gdb.getCapabilities().getSupportedSrcDataSources()) {
            String code = ds.getSystemCode();
            StringBuilder linkset = new StringBuilder();
            linkset.append("\n<").append(code).append(">\n");
            linkset.append("        rdf:type             void:Linkset ;\n");
            String title = ds.getFullName() != null && !ds.getFullName().isEmpty() ? ds.getFullName() : code;
            linkset.append("        dcterms:title        ").append(literal(title));
            // the VoID table of a data source is named after its system code
            String sql = "SELECT * from \"" + code + "\"";
            try (ResultSet resultSet = st.executeQuery(sql)) {
                while (resultSet.next()) {
                    String attrib = resultSet.getString("ATTRIB");
                    String predicate = predicate(attrib);
                    if (predicate == null) {
                        System.err.println("WARNING: skipped " + attrib + " of " + code
                            + ", as it is not an IRI or a name with a known prefix");
                        continue;
                    }
                    linkset.append(" ;\n        ").append(String.format("%-20s ", predicate))
                        .append(object(resultSet.getString("VAL")));
                }
            } catch (SQLException e) {
                System.err.println("WARNING: no VoID data for " + code + " (" + title + "): " + e.getMessage());
                continue;
            }
            linkset.append(" .\n");
            ttl.append(linkset);
        }
        appendStrToFile(fileName, ttl.toString());
    }

    private static boolean isPrefixedName(String value) {
        int colon = value.indexOf(':');
        if (colon < 1 || !value.substring(colon + 1).matches("[A-Za-z0-9_][A-Za-z0-9_.\\-]*|")
            || value.endsWith(".")) return false;
        String prefix = value.substring(0, colon);
        for (String[] known : PREFIXES) {
            if (known[0].equals(prefix)) return true;
        }
        return false;
    }

    private static boolean isIRI(String value) {
        return value.matches("https?://[^\\s<>\"{}|^`\\\\]+");
    }

    /** Returns the attribute as a Turtle predicate, or null if it isn't one. */
    private static String predicate(String attrib) {
        if (attrib == null) return null;
        attrib = attrib.trim();
        if (isPrefixedName(attrib)) return attrib;
        if (isIRI(attrib)) return "<" + attrib + ">";
        return null;
    }

    /** Returns the value as a Turtle object: an IRI, a prefixed name, a typed or plain literal. */
    private static String object(String value) {
        if (value == null) return literal("");
        String v = value.trim();
        if (isIRI(v)) return "<" + v + ">";
        if (isPrefixedName(v)) return v;
        if (v.matches("-?\\d+")) return v;
        if (v.matches("\\d{4}-\\d{2}-\\d{2}")) return literal(v) + "^^xsd:date";
        if (v.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?(Z|[+-]\\d{2}:\\d{2})?"))
            return literal(v) + "^^xsd:dateTime";
        return literal(value);
    }

    private static String literal(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }

    public void run() throws IDMapperException, SQLException {
        initDatabases();
        createRDF();
    }
    public static void printUsage(){
        System.out.println ("Expected 2 arguments: <database> <fileName>");
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
