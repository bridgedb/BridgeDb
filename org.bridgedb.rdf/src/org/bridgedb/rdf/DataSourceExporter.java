/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.rdf.constants.XMLSchemaConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;

/**
 *
 * @author Christian
 */
public class DataSourceExporter extends RdfBase {

    private HashSet<Organism> organisms;
    private BufferedWriter writer;
    static final Logger logger = Logger.getLogger(DataSourceExporter.class);
    
    private DataSourceExporter(BufferedWriter buffer){
        organisms = new HashSet<Organism>();
        writer = buffer;
    }
    
    public static void main(String[] args) throws IOException, IDMapperException {
        ConfigReader.logToConsole();
        BioDataSource.init();
        UriMapping.init();
        UriMapping.showSharedUriPatterns();
        File file = new File("../org.bridgedb.utils/resources/BioDataSource.ttl");
        logger.info("Exporting DataSources to "+ file.getAbsolutePath());
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        DataSourceExporter exporter = new DataSourceExporter(buffer);
        exporter.export();
    }

    public static void export(File file) throws BridgeDBException, IOException{
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter buffer = new BufferedWriter(fileWriter);
        DataSourceExporter exporter = new DataSourceExporter(buffer);
        exporter.export();        
    }
    
    private void export() throws BridgeDBException{
        try {
            printPrefix();
            DataSourceRdf.writeAllAsRDF(writer);
            OrganismRdf.writeAllAsRDF(writer);
            if (VERSION2){
                UriPattern.writeAllAsRDF(writer);
                UriMapping.writeAllAsRDF(writer);
            }
        } catch (IOException ex) {
            throw new BridgeDBException("Error exporting DataSources. ", ex);
        } finally {
            if (writer != null){
                try {
                    writer.close();
                } catch (IOException ex) {
                    throw new BridgeDBException("Error closing. ", ex);
                }
            }
        }
    }

    private void printPrefix() throws IOException {
        writer.write("@prefix : <> .");
        writer.newLine();
        writer.write("@prefix ");
        writer.write(BridgeDBConstants.PREFIX_NAME1);        
        writer.write(" <http://openphacts.cs.man.ac.uk:9090//ontology/DataSource.owl#> .");
        writer.newLine();
        writer.write("@prefix ");
        writer.write(VoidConstants.PREFIX_NAME);
        writer.write(" <");
        writer.write(VoidConstants.voidns);
        writer.write("> .");
        writer.newLine();
        writer.write("@prefix xsd: <");
        writer.write(XMLSchemaConstants.PREFIX);
        writer.write("> .");
        writer.newLine();
        writer.newLine();
    }

    private int compare(String s1, String s2){
        if (s1 == null){
            if (s2 == null){
                return 0;
            } else {
                return 1;
            }
        } else {
            if (s2 == null){
                return -1;
            } else {
                return s1.compareTo(s2);
            }            
        }
    }
    
    public int compare(DataSource ds1, DataSource ds2) {
        String id1 = ds1.getUrl("$id");
        String id2 = ds2.getUrl("$id");
        if (compare(id1, id2) != 0){
            return compare(id1, id2);
        }
        id1 = ds1.getSystemCode();
        id2 = ds2.getSystemCode();
        if (compare(id1, id2) != 0){
            return compare(id1, id2);
        }
        id1 = ds1.getFullName();
        id2 = ds2.getFullName();
        return id1.compareTo(id2);
     }

}
