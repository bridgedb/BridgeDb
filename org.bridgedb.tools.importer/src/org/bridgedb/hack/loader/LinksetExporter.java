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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.XrefIterator;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.rdf.BridgeDBRdfHandler;
import org.bridgedb.rdf.DataSourceUris;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
public class LinksetExporter {
    
    private IDMapper mapper;
    private String name;
    private BufferedWriter buffer;
    private String sourceUriSpace;
    private String targetUriSpace;
    private final String LINK_PREDICATE = "skos:relatedMatch";
    
    static final Logger logger = Logger.getLogger(LinksetExporter.class);

    public LinksetExporter(File file) throws IDMapperException{
        mapper = BridgeDb.connect("idmapper-pgdb:" + file.getAbsolutePath());
        name = file.getName();
        if (name.contains(".")){
            name = name.substring(0, name.lastIndexOf("."));
        }
    }
    
    public void exportAll(File directory) throws IDMapperException, IOException{
        Set<DataSource> srcDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        Set<DataSource> tgtDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        for (DataSource srcDataSource:srcDataSources){
            if (canExport(srcDataSource)){
                for (DataSource tgtDataSource:tgtDataSources){
                    if (canExport(tgtDataSource)){
                        exportLinkset (directory, srcDataSource, tgtDataSource);
                    }
                }
            }
        }
    }

    private static boolean canExport(DataSource dataSource) throws BridgeDBException{
        if (dataSource == null){
            System.err.println("Ignoring null DataSource");
            return false;
        }
        DataSourceUris dsu = DataSourceUris.byDataSource(dataSource);
        UriPattern wp = dsu.getWikiPathwaysPattern();
        if (wp == null){
            System.err.println("Skipping DataSource " + dataSource + " as it has no WikiPathways pattern");
            return false;
        }
        if (wp.hasPostfix()){
            System.err.println("Skipping DataSource " + dataSource + " as WikiPathways pattern " + wp.getUriPattern() + " has a postfix");
            return false;
        }
        return true;
    }
    
    public synchronized void exportLinkset(File directory, DataSource srcDataSource, DataSource tgtDataSource) throws IDMapperException, IOException {
        if (!directory.exists()){
            if (directory.getParentFile().exists()){
                directory.mkdir();
            } else {
                throw new BridgeDBException("Unable to find or create the directory " + directory.getAbsolutePath());
            }
        }
        if (!directory.isDirectory()){
                throw new BridgeDBException("Expected a directory at " + directory.getAbsolutePath());            
        }
        if (srcDataSource == null){
            return; // no miram to use
        }               
        if (tgtDataSource == null ){
            return; // no miram to use
        }               
        String fileName = srcDataSource.getSystemCode() + "_" + tgtDataSource.getSystemCode() + ".ttl";
        File linksetFile = new File(directory, fileName);
        FileWriter writer = new FileWriter(linksetFile);
        buffer = new BufferedWriter(writer);
        boolean fileValid = false;
        try{
            fileValid = writeLinkset(srcDataSource, tgtDataSource);
        } finally {
            buffer.close();        
        }
        if (fileValid){
            logger.info("Exported to " + linksetFile.getAbsolutePath());
            LinksetLoader loader = new LinksetLoader();
            logger.info(loader.validityFile(linksetFile, StoreType.TEST, ValidationType.LINKSMINIMAL, false));
        } else {
            logger.info("No link found for " + linksetFile.getAbsolutePath());
            linksetFile.delete();
        }
    }

    private boolean writeLinkset(DataSource srcDataSource, DataSource tgtDataSource) throws IOException, IDMapperException {
        writeVoidHeader(srcDataSource, tgtDataSource);
        return writeLinks(srcDataSource, tgtDataSource);
    }
 
    private void writeln(String message) throws IOException{
        buffer.write(message);
        buffer.newLine();
    }
    
    private String getWikiPathwaysPattern(DataSource dataSource) throws BridgeDBException{
        DataSourceUris dsu = DataSourceUris.byDataSource(dataSource);
        UriPattern pattern = dsu.getWikiPathwaysPattern();
        if (pattern == null){
            throw new BridgeDBException ("No WikiPathways pattern found for " + dataSource);
        }
        return pattern.getUriSpace();
    }
    
    private void writeVoidHeader (DataSource srcDataSource, DataSource tgtDataSource) throws IOException, BridgeDBException{
        sourceUriSpace = getWikiPathwaysPattern(srcDataSource);
        targetUriSpace = getWikiPathwaysPattern(tgtDataSource);
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
        writeln("    void:linkPredicate " + LINK_PREDICATE + " .");
        writeln("");                
    }

    private boolean writeLinks(DataSource srcDataSource, DataSource tgtDataSource) throws IDMapperException, IOException {
        if (srcDataSource == tgtDataSource){
            return writeSelfLinks(srcDataSource, tgtDataSource);
        } else {
            boolean linkFound = false;
            XrefIterator iterator = (XrefIterator)mapper;
            Iterator<Xref> xrefIterator = iterator.getIterator(srcDataSource).iterator();
            while (xrefIterator.hasNext()){
                Xref sourceXref = xrefIterator.next();
                Set<Xref> targetXrefs = mapper.mapID(sourceXref, tgtDataSource);
                for (Xref targetXref:targetXrefs){
                    writeln("<" + sourceUriSpace + scrub(sourceXref.getId()) + "> " + LINK_PREDICATE + 
                            " <" + targetUriSpace +scrub(targetXref.getId()) + "> .");
                    linkFound = true;
                }
            }
            return linkFound;
        }
    }
    
    private String scrub(String id) {
        if (id.equals("CD3<EPSILON>")){
            return "Q7RN2";
        }
        return id;
    }

    private boolean writeSelfLinks(DataSource srcDataSource, DataSource tgtDataSource) throws IDMapperException, IOException {
        boolean linkFound = false;
        XrefIterator iterator = (XrefIterator)mapper;
        Iterator<Xref> xrefIterator = iterator.getIterator(srcDataSource).iterator();
        while (xrefIterator.hasNext()){
            Xref sourceXref = xrefIterator.next();
            Set<Xref> targetXrefs = mapper.mapID(sourceXref, tgtDataSource);
            for (Xref targetXref:targetXrefs){
                if (!sourceXref.getId().equals(targetXref.getId())){
                    writeln("<" + sourceUriSpace + scrub(sourceXref.getId()) + "> " + LINK_PREDICATE + 
                            " <" + targetUriSpace +scrub(targetXref.getId()) + "> .");
                    linkFound = true;
                }
            }
        }
        return linkFound;
    }
    
    public static void exportFile(File file) 
    		throws IDMapperException, IOException {
    	if (!file.exists()) {
    		throw new BridgeDBException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            StringBuilder builder = new StringBuilder();
            File[] children = file.listFiles();
            for (File child:children){
                exportFile(child);
            }
        } else { 
            String name = file.getName();
            name = name.substring(0, name.indexOf('.'));
            LinksetExporter exporter = new LinksetExporter(file);
            File directory = new File("C:/OpenPhacts/linksets/" + name);
            exporter.exportAll(directory);
        }
    }
    
    public static Set<DataSource> extractDataSources(File file) throws IDMapperException {
        HashSet results = new HashSet<DataSource>();
    	if (!file.exists()) {
    		throw new BridgeDBException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            File[] children = file.listFiles();
            for (File child:children){
                results.addAll(extractDataSources(child));
            }
        } else { 
            IDMapper idMpper = BridgeDb.connect("idmapper-pgdb:" + file.getAbsolutePath());
            results.addAll(idMpper .getCapabilities().getSupportedSrcDataSources());
            results.addAll(idMpper .getCapabilities().getSupportedTgtDataSources());
        }
        return results;
    }

    public static void main(String[] args) throws IDMapperException, IOException, ClassNotFoundException{
        ConfigReader.logToConsole();
        BioDataSource.init();
        //File DSfile = new File("../../BioDataSource.ttl");
        //BridgeDBRdfHandler.parseRdfFile(DSfile);             
        System.out.println("Parsing finished");
        
        Class.forName("org.bridgedb.rdb.IDMapperRdb");
        File file = new File("C:/OpenPhacts/andra/");
        //exportFile(file);
        Set<DataSource> used = extractDataSources(file);     
        System.out.println("finished extracting files " + used.size());
        
        for (DataSource ds:used){
            canExport(ds);
        }

        File dsfile = new File("../org.bridgedb.utils/resources/BioDataSource.ttl");
        BridgeDBRdfHandler.writeRdfToFile(dsfile, false);
        
        //LinksetExporter exporter = new LinksetExporter(file);
        //File directory = new File("C:/OpenPhacts/linksets/Ag_Derby_20120602");
        //exporter.exportAll(directory);
    }

}
