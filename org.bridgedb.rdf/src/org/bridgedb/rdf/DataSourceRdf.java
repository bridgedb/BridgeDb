/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class DataSourceRdf extends RdfBase  {
    
    private static HashMap<String, DataSource> register = new HashMap<String, DataSource>();

    public static String getRdfLabel(DataSource dataSource) {
        return scrub(dataSource.getFullName());        
    }
    
    public static String getRdfId(DataSource dataSource) {
        return ":" + BridgeDBConstants.DATA_SOURCE + "_" + getRdfLabel(dataSource);
    }

    static DataSource byRdfResource(Value dataSourceId) throws BridgeDBException {
        String shortName = convertToShortName(dataSourceId);
        DataSource result = register.get(shortName);
        if (result == null){
            //Load all Datasource in case it came from elseWhere
            for (DataSource dataSource: DataSource.getDataSources()){
                register.put(getRdfId(dataSource), dataSource);
            }
            //Check again
            result = register.get(shortName);
        }
        if (result == null){
            throw new BridgeDBException("No DataSource known for Id " + dataSourceId + " / " + shortName);
        }
        return result;
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

    public static DataSource readRdf(Resource dataSourceId, Set<Statement> dataSourceStatements) throws BridgeDBException{
        String fullName = null;
        String idExample = null;
        String mainUrl = null;
        Object organism = null;
        String primary = null;
        String systemCode = null;
        String type = null;
        String urlPattern = null;
        String urnBase = null;
        String identifiersOrgBase = null;
        String wikipathwaysBase = null;
        String bio2RDFPattern = null;
        String sourceRDFURIPattern = null;
        HashSet<String> alternativeFullNames = new HashSet<String>();
        
        for (Statement statement:dataSourceStatements){
            if (statement.getPredicate().equals(RdfConstants.TYPE_URI)){
                //Ignore the type statement
            } else if (statement.getPredicate().equals(BridgeDBConstants.ALTERNATIVE_FULL_NAME_URI)){
                alternativeFullNames.add(statement.getObject().stringValue());
            } else if (statement.getPredicate().equals(BridgeDBConstants.FULL_NAME_URI)){
                fullName = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.ID_EXAMPLE_URI)){
                idExample = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.MAIN_URL_URI)){
                mainUrl = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.ORGANISM_URI)){
                Value organismId = statement.getObject();
                Object Organism = OrganismRdf.byRdfResource(organismId);
            } else if (statement.getPredicate().equals(BridgeDBConstants.PRIMAY_URI)){
                primary = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.SYSTEM_CODE_URI)){
                systemCode = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.TYPE_URI)){
                type = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.URL_PATTERN_URI)){
                urlPattern = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.URN_BASE_URI)){
                urnBase = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.IDENTIFIERS_ORG_BASE_URI)){
                identifiersOrgBase = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.WIKIPATHWAYS_BASE_URI)){
                wikipathwaysBase = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.SOURCE_RDF_URI)){
                sourceRDFURIPattern = statement.getObject().stringValue();
            } else if (statement.getPredicate().equals(BridgeDBConstants.BIO2RDF_URI)){
                bio2RDFPattern = statement.getObject().stringValue();
            } else {
                throw new BridgeDBException ("Unexpected Statement " + statement);
            }
        }
        DataSource.Builder builder = DataSource.register(systemCode, fullName);
        for (String alternativeFullName:alternativeFullNames){
            builder.alternativeFullName(alternativeFullName);
        }
        if (mainUrl != null) {
            builder.mainUrl(mainUrl);
        }
        if (urlPattern != null) {
            builder.urlPattern(urlPattern);
        }
        if (idExample != null) {
            builder.idExample(idExample);
        }
        if (type != null) {
            builder.type(type);
        }
        if (organism != null) {
            builder.organism(organism);
        }					      
        if (primary != null) {
            builder.primary (Boolean.parseBoolean(primary));
        }					      
        if (urnBase != null) {
            builder.urnBase(urnBase);
        }
        DataSource dataSource = builder.asDataSource();
        registerUriPattern(dataSource, urlPattern, UriMappingRelationship.DATA_SOURCE_URL_PATTERN);
        registerNameSpace(dataSource, identifiersOrgBase, UriMappingRelationship.IDENTIFERS_ORG);
        registerNameSpace(dataSource, wikipathwaysBase, UriMappingRelationship.WIKIPATHWAYS);
        registerUriPattern(dataSource, sourceRDFURIPattern, UriMappingRelationship.SOURCE_RDF);
        registerUriPattern(dataSource, bio2RDFPattern, UriMappingRelationship.BIO2RDF_URI);
        register.put(getRdfId(dataSource), dataSource);
        return dataSource;
    }

     private static void registerUriPattern(DataSource dataSource, String urlPattern, UriMappingRelationship uriMappingRelationship) throws BridgeDBException {
        if (urlPattern == null || urlPattern.isEmpty()) {
            return;
        }
        UriPattern pattern = UriPattern.byUrlPattern(urlPattern);
        UriMapping.addMapping(dataSource, pattern, uriMappingRelationship);
    }

    private static void registerNameSpace(DataSource dataSource, String nameSpace, UriMappingRelationship uriMappingRelationship) throws BridgeDBException {
        if (nameSpace == null || nameSpace.isEmpty()) {
            return;
        }
        UriPattern pattern = UriPattern.byNameSpace(nameSpace);
        UriMapping.addMapping(dataSource, pattern, uriMappingRelationship);
    }

}
