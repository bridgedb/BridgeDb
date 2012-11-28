/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.bio.Organism;
import org.bridgedb.metadata.constants.BridgeDBConstants;
import org.bridgedb.metadata.constants.XMLSchemaConstants;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
public class DataSourceExporter {
    
    private HashSet<Organism> organisms;
    private BufferedWriter writer;
    
    static final Logger logger = Logger.getLogger(DataSourceExporter.class);
    
    private DataSourceExporter(BufferedWriter buffer){
        organisms = new HashSet<Organism>();
        writer = buffer;
    }
    
    public static void main(String[] args) throws IOException, IDMapperException {
        ConfigReader.logToConsole();
        SQLUrlMapper mapper = new SQLUrlMapper(false, StoreType.LIVE);
        //BioDataSource.init();
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
            Set<DataSource> dataSources = DataSource.getDataSources();
            writer.write("@prefix : <> .");
            writer.newLine();
            writer.write("@prefix bridgeDB: <http://openphacts.cs.man.ac.uk:9090//ontology/DataSource.owl#> .");
            writer.newLine();
            writer.write("@prefix xsd: <");
            writer.write(XMLSchemaConstants.PREFIX);
            writer.write("> .");
            writer.newLine();
            writer.newLine();
            for (DataSource dataSource:dataSources){
                printDataSource(dataSource);
            }
            writer.newLine();
            writer.write("#WARNING: Organism are hard coded into BridgeDB.");
            writer.newLine();
            writer.write("#WARNING: below is for reference and NON BridgeDB use only!");
            writer.newLine();
            writer.write("#WARNING: Any changes could cause a BridgeDBException.");
            writer.newLine();
            for (Organism organism:organisms){
                printOrganism(organism);
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

    private void printDataSource(DataSource dataSource) throws IOException {
        String name = dataSource.getFullName();
        name = name.replace(" ", "_");
        name = name.replace(".", "");
        name = name.replace(":", "");
        name = name.replace("/", "_");
        writer.write(":DataSource_");
        writer.write(name);
        writer.write(" a bridgeDB:");
        writer.write(BridgeDBConstants.DATA_SOURCE);        
        writer.write("; ");        
        writer.newLine();
         
        if (dataSource.getSystemCode() != null && (!dataSource.getSystemCode().trim().isEmpty())){
            writer.write("         bridgeDB:");
            writer.write(BridgeDBConstants.SYSTEM_CODE);        
            writer.write(" \"");
            writer.write(dataSource.getSystemCode());
            writer.write("\";");
            writer.newLine();
        }

        if (dataSource.getMainUrl() != null){
            writer.write("         bridgeDB:");
            writer.write(BridgeDBConstants.MAIN_URL);
            writer.write(" \"");
            writer.write(dataSource.getMainUrl());
            writer.write("\";");
            writer.newLine();
        }

       if (dataSource.getExample() != null && dataSource.getExample().getId() != null){
            writer.write("         bridgeDB:");
            writer.write(BridgeDBConstants.ID_EXAMPLE);
            writer.write(" \"");
            writer.write(dataSource.getExample().getId());
            writer.write("\";");
            writer.newLine();
        }

        writer.write("         bridgeDB:");
        writer.write(BridgeDBConstants.PRIMAY);
        if (dataSource.isPrimary()){
            writer.write(" \"true\"^^xsd:boolean;");
        } else {
            writer.write(" \"false\"^^xsd:boolean;");            
        }
        writer.newLine();

        if (dataSource.getType() != null){
            writer.write("         bridgeDB:");
            writer.write(BridgeDBConstants.TYPE);
            writer.write(" \"");
            writer.write(dataSource.getType());
            writer.write("\";");
            writer.newLine();
       }

        String urlPattern = dataSource.getUrl("$id");
        if (urlPattern.length() > 3){
            writer.write("         bridgeDB:");
            writer.write(BridgeDBConstants.URL_PATTERN);
            writer.write(" \"");
            writer.write(urlPattern);
            writer.write("\";");
            writer.newLine();
        }

        String urnPattern = dataSource.getURN("");
        if (urnPattern.length() > 1){
            writer.write("         bridgeDB:");
            writer.write(BridgeDBConstants.URN_BASE);
            writer.write(" \"");
            writer.write(urnPattern.substring(0, urnPattern.length()-1));
            writer.write("\";");
            writer.newLine();
            String urn = dataSource.getURN("");
            if (urn.length() >= 11){
                String identifersOrgBase = "http://identifiers.org/" + urn.substring(11, urn.length()-1) + "/";
                writer.write("         bridgeDB:");
                writer.write(BridgeDBConstants.IDENTIFIERS_ORG_BASE);
                writer.write(" \"");
                writer.write(identifersOrgBase);
                writer.write("\";");
                writer.newLine();
            }
        }

        if (dataSource.getOrganism() != null){
            Organism organism = (Organism)dataSource.getOrganism();
            organisms.add(organism);
            writer.write("         bridgeDB:");
            writer.write(BridgeDBConstants.ORGANISM);
            writer.write(" :");
            writer.write(BridgeDBConstants.ORGANISM);
            writer.write("_");
            writer.write(organism.code());
            writer.write(";");    
            writer.newLine();
        }

        writer.write("         bridgeDB:");
        writer.write(BridgeDBConstants.WIKIPATHWAYS_BASE);
        writer.write(" \"");
        writer.write(AndraIndetifiersOrg.getNameSpace(dataSource));
        writer.write("\";");
        writer.newLine();
        
        writer.write("         bridgeDB:");
        writer.write(BridgeDBConstants.FULL_NAME);
        writer.write(" \"");
        writer.write(dataSource.getFullName());
        writer.write("\".");
        writer.newLine();
                
    }

    private void printOrganism(Organism organism) throws IOException {
        writer.write(":");
        writer.write(BridgeDBConstants.ORGANISM);
        writer.write("_");
        writer.write(organism.code());
        writer.write(" a bridgeDB:");
        writer.write(BridgeDBConstants.ORGANISM);
        writer.write("; ");
        writer.newLine();

        writer.write("         bridgeDB:");
        writer.write(BridgeDBConstants.CODE);
        writer.write(" \"");
        writer.write(organism.code());
        writer.write("\";");
        writer.newLine();

        writer.write("         bridgeDB:");
        writer.write(BridgeDBConstants.SHORT_NAME);
        writer.write(" \"");
        writer.write(organism.shortName());
        writer.write("\";");
        writer.newLine();
        
        writer.write("         bridgeDB:");
        writer.write(BridgeDBConstants.LATIN_NAME);
        writer.write(" \"");
        writer.write(organism.latinName());
        writer.write("\".");
        writer.newLine();
    }

}
