/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
public class DataSourceExporter implements Comparator<DataSource>{

    private HashSet<Organism> organisms;
    private BufferedWriter writer;
    private HashMap<String,String> mappings;
    static final Logger logger = Logger.getLogger(DataSourceExporter.class);
    static final boolean VERSION2 = true;
    
    private DataSourceExporter(BufferedWriter buffer){
        organisms = new HashSet<Organism>();
        mappings = AndraIndetifiersOrg.getAndraMappings();
        writer = buffer;
    }
    
    public static void main(String[] args) throws IOException, IDMapperException {
        ConfigReader.logToConsole();
        SQLUrlMapper mapper = new SQLUrlMapper(false, StoreType.LIVE);
        BioDataSource.init();
        UriMapping.init();
        AndraIndetifiersOrg.init();
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
            printDataSources();
            printOrganisms();
            if (VERSION2){
                printUriPatterns();
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

    private void printDataSources() throws BridgeDBException, IOException{
        ArrayList<DataSource> dataSources = new ArrayList(DataSource.getDataSources());
        Collections.sort(dataSources, this);
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
    }

    private void printOrganisms() throws IOException {
        writer.write("#WARNING: Organism are hard coded into BridgeDB.");
        writer.newLine();
        writer.write("#WARNING: below is for reference and NON BridgeDB use only!");
        writer.newLine();
        writer.write("#WARNING: Any changes could cause a BridgeDBException.");
        writer.newLine();
        for (Organism organism:organisms){
            printOrganism(organism);
        }
        writer.newLine();
    }

    private void printUriPatterns() throws IOException {
        Set<UriPattern> uriPatterns = UriPattern.getAllUriPatterns();
        for (UriPattern  uriPattern:uriPatterns){
            printUriPattern(uriPattern);
        }
    }

    public static String scrub(String original){
        String result = original.replaceAll("\\W", "_");
        while(result.contains("__")){
            result = result.replace("__", "_");
        }
        if (result.endsWith("_")){
            result = result.substring(0, result.length()-1);
        }
        return result;
    }
    
    private void printDataSourceID(DataSource dataSource) throws IOException{
        String name = scrub(dataSource.getFullName());        
        writer.write(":DataSource_");
        writer.write(name);
    }
    
    private void printDataSource(DataSource dataSource) throws IOException {
        printDataSourceID(dataSource); 
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

        if (!VERSION2){
            String urlPattern = dataSource.getUrl("$id");
            if (urlPattern.length() > 3){
                writer.write("         bridgeDB:");
                writer.write(BridgeDBConstants.URL_PATTERN);
                writer.write(" \"");
                writer.write(urlPattern);
                writer.write("\";");
                writer.newLine();
            }
        }

        if (!VERSION2){
            String urnPattern = dataSource.getURN("");
            if (urnPattern.length() > 1){
                writer.write("         bridgeDB:");
                writer.write(BridgeDBConstants.URN_BASE);
                writer.write(" \"");
                writer.write(urnPattern.substring(0, urnPattern.length()-1));
                writer.write("\";");
                writer.newLine();
                //if (urnPattern.length() >= 11){
                    //String identifersOrgBase = "http://identifiers.org/" + urnPattern.substring(11, urnPattern.length()-1) + "/";
                    //writer.write("         bridgeDB:");
                    //writer.write(BridgeDBConstants.IDENTIFIERS_ORG_BASE);
                    //writer.write(" \"");
                    //writer.write(identifersOrgBase);
                    //writer.write("\";");
                    //writer.newLine();
            }
        }

        /*String wikiNameSpace = mappings.get(dataSource.getFullName());
        if (wikiNameSpace != null){
            writer.write("         bridgeDB:");
            writer.write(BridgeDBConstants.WIKIPATHWAYS_BASE);
            writer.write(" \"");
            writer.write(wikiNameSpace);
            writer.write("\";");
            writer.newLine();
        }
        */
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

    private void printUriPattern(UriPattern uriPattern) throws IOException {
        writer.write(uriPattern.getRdfId());
        writer.write(" a bridgeDB:");
        writer.write(BridgeDBConstants.URI_PATTERN);
        writer.write("; ");
        writer.newLine();

        writer.write("         bridgeDB:");
        writer.write(BridgeDBConstants.URI_PATTERN);
        writer.write(" \"");
        writer.write(uriPattern.getUriPattern());
        writer.write("\".");
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
    
    @Override
    public int compare(DataSource ds1, DataSource ds2) {
        String id1 = mappings.get(ds1.getFullName());
        if (id1 == null){
            String urnPattern = ds1.getURN("");
            if (urnPattern.length() >= 11){
                id1 = "http://identifiers.org/" + urnPattern.substring(11, urnPattern.length()-1) + "/";
            }
        }
        String id2 = mappings.get(ds2.getFullName());
        if (id2 == null){
            String urnPattern = ds2.getURN("");
            if (urnPattern.length() >= 11){
                id2 = "http://identifiers.org/" + urnPattern.substring(11, urnPattern.length()-1) + "/";
            }
        }
        if (compare(id1, id2) != 0){
            return compare(id1, id2);
        }
        id1 = ds1.getUrl("$id");
        id2 = ds2.getUrl("$id");
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
