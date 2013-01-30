// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.hack.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.DataSource.Builder;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
public class Test {

    static final Logger logger = Logger.getLogger(Test.class);

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

    private static void registerUriSpaces(SQLUrlMapper sqlMapper, Set<DataSource> dataSources) throws BridgeDBException{
        for (DataSource source: dataSources){
            String uriSpace = source.getUrl("$id").trim();
            if (uriSpace.equals("$id")){
                uriSpace = "www.example.com/" + source.getSystemCode() + "/";
            }
            sqlMapper.registerUriPattern(source, uriSpace);        
        }
    }
    
    /**
     * 
     * @param mapper MUST implement XrefIterator
     */
    public static void printLinksetLines(IDMapper mapper) throws BridgeDBException{
        SQLUrlMapper sqlMapper = new SQLUrlMapper(false, StoreType.TEST);

        XrefIterator iterator = (XrefIterator)mapper;
        Set<DataSource> srcDataSources;
        Set<DataSource> tgtDataSources;
        try {
            srcDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
            tgtDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
        int setCount = 0;
        int linkTotal = 0;
        
        for (DataSource srcDataSource:srcDataSources){
            for (DataSource tgtDataSource:tgtDataSources){
                int sourceCount = 0;
                int linkCount = 0;
                int unmapped = 0;
                try { 
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
                } catch (IDMapperException e){
                    throw BridgeDBException.convertToBridgeDB(e);
                }
                logger.info(srcDataSource.getSystemCode() + " -> " + tgtDataSource.getSystemCode() + 
                        " " + sourceCount + " " + linkCount + " " + unmapped);
                if (linkCount > 0){
                    setCount++;
                    linkTotal += linkCount;
                }
            }
        }
        logger.info("set Count = " + setCount + "  Links = " + linkTotal);
    }
    
    /**
     * 
     * @param mapper MUST implement XrefIterator
     */
    public static void findDataSources(IDMapper mapper) throws BridgeDBException{
        Set<DataSource> srcDataSources;
        Set<DataSource> tgtDataSources;
        try {
            srcDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
            tgtDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
        for (DataSource dataSource:srcDataSources) {
            if (dataSource != null && dataSource.getUrl("").trim().isEmpty()){
                logger.info(dataSource.getSystemCode() + ": " + dataSource + " " + dataSource.getMainUrl());
            }
        }
        allSource.addAll(srcDataSources);
        for (DataSource dataSource:tgtDataSources) {
            if (dataSource != null && dataSource.getUrl("").trim().isEmpty()){
                logger.info(dataSource.getSystemCode() + ": " + dataSource.getURN("") + " " + dataSource.getMainUrl());
            }
        }
        allSource.addAll(tgtDataSources);
    }

    /**
     * 
     * @param mapper MUST implement XrefIterator
     */
    public static void printLinksets(IDMapper mapper, String name) throws BridgeDBException, IOException{
        XrefIterator iterator = (XrefIterator)mapper;
        Set<DataSource> srcDataSources;
        Set<DataSource> tgtDataSources;
        try {
            srcDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
            tgtDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        } catch (IDMapperException e){
            throw BridgeDBException.convertToBridgeDB(e);
        }
        File directory = new File("C:/OpenPhacts/linksets/" + name);
        if (!directory.exists()){
            directory.mkdir();
        }
        for (DataSource srcDataSource:srcDataSources){
            if (srcDataSource != null && srcDataSource.getURN("").length() > 11){               
                for (DataSource tgtDataSource:tgtDataSources){
                    if (tgtDataSource != null && srcDataSource != tgtDataSource && tgtDataSource.getURN("").length() > 11){
                        String fileName = srcDataSource.getSystemCode() + "_" + tgtDataSource.getSystemCode() + ".ttl";
                        File linksetFile = new File(directory, fileName);
                        FileWriter writer = new FileWriter(linksetFile);
                        buffer = new BufferedWriter(writer);
                        writeVoidHeader(srcDataSource, tgtDataSource);
                        writeln(srcDataSource.getSystemCode() + " " + srcDataSource.getURN("$id"));
                        writeln(tgtDataSource.getSystemCode() + " " + tgtDataSource.getURN("$id"));
                        buffer.close();
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
    
    
    public static void printLinksets(File file) 
    		throws BridgeDBException, IOException {
    	if (!file.exists()) {
    		throw new BridgeDBException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            StringBuilder builder = new StringBuilder();
            File[] children = file.listFiles();
            for (File child:children){
                printLinksets(child);
            }
        } else { 
            String name = file.getName();
            name = name.substring(0, name.indexOf('.'));
            System.out.println("£"+ name);
            System.out.println(file.getAbsolutePath());
            IDMapper mapper;
            try{
                mapper = BridgeDb.connect("idmapper-pgdb:"+file.getAbsolutePath());
            } catch (IDMapperException e){
                throw BridgeDBException.convertToBridgeDB(e);
            }
            printLinksets(mapper, name);
            System.out.println("done file");
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, BridgeDBException, IOException {
        ConfigReader.logToConsole();
        BioDataSource.init();
        Class.forName("org.bridgedb.rdb.IDMapperRdb");
        /*File directory = new File("C:/OpenPhacts/andra");
        File[] children = directory.listFiles();
        for (File child:children){
            logger.info(child.getAbsolutePath());
            IDMapper mapper = BridgeDb.connect("idmapper-pgdb:" + child.getAbsolutePath());
            findDataSources(mapper);
        }
        for (DataSource ds:allSource){
             if (ds != null){
                logger.info("SystemCode: " + ds.getSystemCode());
                logger.info("   Fullname: " + ds.getSystemCode());
                logger.info("   UriPattern :" + ds.getUrl("$id"));
                logger.info("   Main URL :" + ds.getMainUrl());
                logger.info("   Mairan base :" + ds.getURN(""));
            }
        }
        logger.info("SystemCode, Fullname, UriPattern, Main URL, Mairan base");
        for (DataSource ds:allSource){
             if (ds != null){
                logger.info(ds.getSystemCode() + ", " + ds.getSystemCode() + ", " + ds.getUrl("$id") + ", "
                        + ds.getMainUrl() + ", " + ds.getURN(""));
            }
        }
        */
        File file = new File("C:/OpenPhacts/andra/");
        printLinksets(file);
//        IDMapper mapper = BridgeDb.connect("idmapper-pgdb:C:/OpenPhacts/andra/"+"Ag_Derby_20120602.bridge");
//        printLinksets(mapper, "Ag_Derby_20120602");
        //Set<DataSource> dataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        //for (DataSource dataSource:dataSources){
        //    logger.info(dataSource);
        //    logger.info(dataSource.getUrl(""));
        //}
    }


}
