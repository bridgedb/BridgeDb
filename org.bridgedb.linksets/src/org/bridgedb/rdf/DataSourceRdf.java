/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.bridgedb.DataSource;
import org.bridgedb.bio.Organism;
import org.bridgedb.metadata.constants.BridgeDBConstants;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class DataSourceRdf extends RdfBase  {
    
    public static String getRdfLabel(DataSource dataSource) {
        return scrub(dataSource.getFullName());        
    }
    
    public static String getRdfId(DataSource dataSource) {
        return ":" + BridgeDBConstants.DATA_SOURCE + "_" + getRdfLabel(dataSource);
    }

    public static void writeAllAsRDF(BufferedWriter writer) throws IOException {
        for (DataSource dataSource:DataSource.getDataSources()){
            writeAsRDF(writer, dataSource);
        }
    }

    public static void writeAsRDF(BufferedWriter writer, DataSource dataSource) throws IOException {
        writer.write(getRdfId(dataSource)); 
        writer.write(" a ");
        writer.write(BridgeDBConstants.DATA_SOURCE_SHORT);        
        writer.write("; ");        
        writer.newLine();
         
        if (dataSource.getSystemCode() != null && (!dataSource.getSystemCode().trim().isEmpty())){
            writer.write("         ");
            writer.write(BridgeDBConstants.SYSTEM_CODE_SHORT);        
            writer.write(" \"");
            writer.write(dataSource.getSystemCode());
            writer.write("\";");
            writer.newLine();
        }

        if (dataSource.getMainUrl() != null){
            writer.write("         ");
            writer.write(BridgeDBConstants.MAIN_URL_SHORT);        
            writer.write(" \"");
            writer.write(dataSource.getMainUrl());
            writer.write("\";");
            writer.newLine();
        }

       if (dataSource.getExample() != null && dataSource.getExample().getId() != null){
            writer.write("         ");
            writer.write(BridgeDBConstants.ID_EXAMPLE_SHORT);
            writer.write(" \"");
            writer.write(dataSource.getExample().getId());
            writer.write("\";");
            writer.newLine();
        }

        writer.write("         ");
        writer.write(BridgeDBConstants.PRIMAY_SHORT);
        if (dataSource.isPrimary()){
            writer.write(" \"true\"^^xsd:boolean;");
        } else {
            writer.write(" \"false\"^^xsd:boolean;");            
        }
        writer.newLine();

        if (dataSource.getType() != null){
            writer.write("         ");
            writer.write(BridgeDBConstants.TYPE_SHORT);
            writer.write(" \"");
            writer.write(dataSource.getType());
            writer.write("\";");
            writer.newLine();
       }

        if (!VERSION2){
            String urlPattern = dataSource.getUrl("$id");
            if (urlPattern.length() > 3){
                writer.write("         ");
                writer.write(BridgeDBConstants.URL_PATTERN_SHORT);
                writer.write(" \"");
                writer.write(urlPattern);
                writer.write("\";");
                writer.newLine();
            }
        }

        if (!VERSION2){
            String urnPattern = dataSource.getURN("");
            if (urnPattern.length() > 1){
                writer.write("         ");
                writer.write(BridgeDBConstants.URN_BASE_SHORT);
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
            writer.write("         ");
            writer.write(BridgeDBConstants.ORGANISM_SHORT);
            writer.write(" ");
            writer.write(OrganismRdf.getRdfId(organism));
            writer.write(";");    
            writer.newLine();
        }

        writer.write("         ");
        writer.write(BridgeDBConstants.FULL_NAME_SHORT);
        writer.write(" \"");
        writer.write(dataSource.getFullName());
        writer.write("\".");
        writer.newLine();
                
    }

 
}
