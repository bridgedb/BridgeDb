/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.DataSource.Builder;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
public class Test {

    static HashSet<DataSource> allSource = new HashSet<DataSource>();
    static BufferedWriter buffer;
    
    public static String linksetLine(Xref source, Xref target){
        return "<" + source.getUrl() + "> skos:exactMatch <" + target.getUrl() + ">";
    }
    
    private void checkDataSource(DataSource ds){
        if (ds.getUrl("").toString().trim().isEmpty()){
            Builder builder = DataSource.register(ds.getSystemCode(), ds.getFullName());
            String pattern = getPatternFromMirian(ds);
            builder.urlPattern(null);
        }
    }

    private String getPatternFromMirian(DataSource ds) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static void registerUriSpaces(SQLUrlMapper sqlMapper, Set<DataSource> dataSources) throws BridgeDbSqlException{
        for (DataSource source: dataSources){
            String uriSpace = source.getUrl("").trim();
            if (uriSpace.isEmpty()){
                uriSpace = "www.example.com/" + source.getSystemCode() + "/";
            }
            sqlMapper.registerUriSpace(source, uriSpace);        
        }
    }
    
    /**
     * 
     * @param mapper MUST implement XrefIterator
     */
    public static void printLinksetLines(IDMapper mapper) throws IDMapperException{
        SQLUrlMapper sqlMapper = new SQLUrlMapper(false, StoreType.TEST);

        XrefIterator iterator = (XrefIterator)mapper;
        Set<DataSource> srcDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
//        registerUriSpaces(sqlMapper, srcDataSources);
        Set<DataSource> tgtDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
//        registerUriSpaces(sqlMapper, srcDataSources);
        int setCount = 0;
        int linkTotal = 0;
        
        for (DataSource srcDataSource:srcDataSources){
            for (DataSource tgtDataSource:tgtDataSources){
                int sourceCount = 0;
                int linkCount = 0;
                int unmapped = 0;
                Iterator<Xref> xrefIterator = iterator.getIterator(srcDataSource).iterator();
                while (xrefIterator.hasNext()){
                    Xref sourceXref = xrefIterator.next();
                    Set<Xref> targetXrefs = mapper.mapID(sourceXref, tgtDataSource);
                    if (targetXrefs.isEmpty()){
                        unmapped++;
                    } else {
                        sourceCount ++;
                        linkCount += targetXrefs.size();
                    }
                }
                System.out.println(srcDataSource.getSystemCode() + " -> " + tgtDataSource.getSystemCode() + 
                        " " + sourceCount + " " + linkCount + " " + unmapped);
                if (linkCount > 0){
                    setCount++;
                    linkTotal += linkCount;
                }
            }
        }
        System.out.println("set Count = " + setCount + "  Links = " + linkTotal);
    }
    
    /**
     * 
     * @param mapper MUST implement XrefIterator
     */
    public static void findDataSources(IDMapper mapper) throws IDMapperException{
        Set<DataSource> srcDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        for (DataSource dataSource:srcDataSources) {
            if (dataSource != null && dataSource.getUrl("").trim().isEmpty()){
                System.out.println(dataSource.getSystemCode() + ": " + dataSource + " " + dataSource.getMainUrl());
            }
        }
        allSource.addAll(srcDataSources);
        Set<DataSource> tgtDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        for (DataSource dataSource:tgtDataSources) {
            if (dataSource != null && dataSource.getUrl("").trim().isEmpty()){
                System.out.println(dataSource.getSystemCode() + ": " + dataSource.getURN("") + " " + dataSource.getMainUrl());
            }
        }
        allSource.addAll(tgtDataSources);
    }

    /**
     * 
     * @param mapper MUST implement XrefIterator
     */
    public static void printLinksets(IDMapper mapper, String name) throws IDMapperException, IOException{
        XrefIterator iterator = (XrefIterator)mapper;
        Set<DataSource> srcDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        Set<DataSource> tgtDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        File directory = new File("C:/OpenPhacts/linksets/" + name);
        if (!directory.exists()){
            directory.mkdir();
        }
        for (DataSource srcDataSource:srcDataSources){
            if (srcDataSource.getURN("").length() > 11){               
                for (DataSource tgtDataSource:tgtDataSources){
                    if (srcDataSource != tgtDataSource && tgtDataSource.getURN("").length() > 11){
                        String fileName = srcDataSource.getSystemCode() + "_" + tgtDataSource.getSystemCode() + ".ttl";
                        File linksetFile = new File(directory, fileName);
                        FileWriter writer = new FileWriter(linksetFile);
                        buffer = new BufferedWriter(writer);
                        writeVoidHeader(srcDataSource, tgtDataSource);
                        writeln(srcDataSource.getSystemCode() + " " + srcDataSource.getURN("$id"));
                        writeln(tgtDataSource.getSystemCode() + " " + tgtDataSource.getURN("$id"));
                        buffer.close();
                        System.exit(0);
                    }
                }
            }
        }
    }
    
    private static void writeVoidHeader (DataSource srcDataSource, DataSource tgtDataSource) throws IOException{
        String urn = srcDataSource.getURN("");
        String sourceUriSpace = "http://identifiers.org/" + urn.substring(11, urn.length()-1) + "/";
        urn = tgtDataSource.getURN("");
        String targetUriSpace = "http://identifiers.org/" + urn.substring(11, urn.length()-1) + "/";
        writeln("@prefix : <#> .");
        writeln("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .");
        writeln("@prefix void: <http://rdfs.org/ns/void#> .");
        writeln("@prefix skos: <http://www.w3.org/2004/02/skos/core#> .");
        writeln("@prefix idOrg" + srcDataSource.getSystemCode() + ": <" + sourceUriSpace + "> .");
        writeln("@prefix idOrg" + tgtDataSource.getSystemCode() + ": <" + targetUriSpace + "> .");
        writeln("");
        writeln(":DataSource_" + srcDataSource.getSystemCode() + " a void:Dataset  ;");
        writeln("    void:uriSpace <" + sourceUriSpace + ">.");
        writeln("");
        writeln(":DataSource_" + tgtDataSource.getSystemCode() + " a void:Dataset  ;");
        writeln("    void:uriSpace <" + targetUriSpace + ">.");
        writeln(":Test" + srcDataSource.getSystemCode() + "_" + tgtDataSource.getSystemCode() + " a void:Linkset  ;");
        writeln("    void:subjectsTarget :DataSource_" + srcDataSource.getSystemCode() + " ;");
        writeln("    void:objectsTarget :DataSource_" + tgtDataSource.getSystemCode() + " ;");
        writeln("    void:linkPredicate skos:relatedMatch .");
        writeln("");                
    }
    
    private static void writeln(String message) throws IOException{
        buffer.write(message);
        buffer.newLine();
    }

    public static void main(String[] args) throws ClassNotFoundException, IDMapperException, IOException {
        ConfigReader.logToConsole();
        BioDataSource.init();
        Class.forName("org.bridgedb.rdb.IDMapperRdb");
        /*File directory = new File("C:/OpenPhacts/andra");
        File[] children = directory.listFiles();
        for (File child:children){
            System.out.println(child.getAbsolutePath());
            IDMapper mapper = BridgeDb.connect("idmapper-pgdb:" + child.getAbsolutePath());
            findDataSources(mapper);
        }
        for (DataSource ds:allSource){
             if (ds != null){
                System.out.println("SystemCode: " + ds.getSystemCode());
                System.out.println("   Fullname: " + ds.getSystemCode());
                System.out.println("   UriPattern :" + ds.getUrl("$id"));
                System.out.println("   Main URL :" + ds.getMainUrl());
                System.out.println("   Mairan base :" + ds.getURN(""));
            }
        }
        System.out.println("SystemCode, Fullname, UriPattern, Main URL, Mairan base");
        for (DataSource ds:allSource){
             if (ds != null){
                System.out.println(ds.getSystemCode() + ", " + ds.getSystemCode() + ", " + ds.getUrl("$id") + ", "
                        + ds.getMainUrl() + ", " + ds.getURN(""));
            }
        }
        */
        IDMapper mapper = BridgeDb.connect("idmapper-pgdb:C:/OpenPhacts/andra/"+"Ag_Derby_20120602.bridge");
        printLinksets(mapper, "Ag_Derby_20120602");
        //Set<DataSource> dataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        //for (DataSource dataSource:dataSources){
        //    System.out.println(dataSource);
        //    System.out.println(dataSource.getUrl(""));
        //}
    }


}
