/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bridgedb.loader;

import java.io.File;
import java.io.FileNotFoundException;
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

/**
 *
 * @author Christian
 */
public class Test {

    static HashSet<DataSource> allSource = new HashSet<DataSource>();
    
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
        SQLAccess sqlAccess = SqlFactory.createSQLAccess();
        SQLUrlMapper sqlMapper = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());

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
        System.out.println(srcDataSources);
        allSource.addAll(srcDataSources);
        Set<DataSource> tgtDataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        System.out.println(tgtDataSources);
        allSource.addAll(tgtDataSources);
    }

    public static void main(String[] args) throws ClassNotFoundException, IDMapperException {
        BioDataSource.init();
        Class.forName("org.bridgedb.rdb.IDMapperRdb");
        File directory = new File("C:/OpenPhacts/andra");
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
            }
        }
        //IDMapper mapper = BridgeDb.connect("idmapper-pgdb:C:/OpenPhacts/andra/"+"Ag_Derby_20120602.bridge");
        //printLinksetLines(mapper);
        //Set<DataSource> dataSources = mapper.getCapabilities().getSupportedSrcDataSources();
        //for (DataSource dataSource:dataSources){
        //    System.out.println(dataSource);
        //    System.out.println(dataSource.getUrl(""));
        //}
    }


}
