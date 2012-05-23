package org.bridgedb.ops;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.url.URLIterator;

/*
 * This is a quick hack.
 * Better would be to use proper BridgeDb transativity
 */

/**
 *
 * @author Christian
 */
public class Transitive {
        
    SQLAccess sqlAccess;
    URLMapperSQL urlMapperSQL;
    String LINK_PREDICATE = "<http://www.bridgedb.org/transitiveMapping>";
    
    public Transitive() throws BridgeDbSqlException{
        sqlAccess = SqlFactory.createSQLAccess();
        urlMapperSQL = new URLMapperSQL(false, sqlAccess);
    }
    
    public void createVoid(String filePath, String sourceNameSpace, String middleNameSpace, String targetNameSpace) 
            throws IDMapperException, IOException{
        File outputFile = new File(filePath);
//        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out));
        BufferedWriter output = new BufferedWriter(new FileWriter(outputFile));
        writeVoidHeader(output, sourceNameSpace, middleNameSpace, targetNameSpace);
        writeVoidBody(output, sourceNameSpace, middleNameSpace, targetNameSpace);
        output.flush();
        output.close();
    }
    
    public void writeVoidHeader(BufferedWriter output, 
            String sourceNameSpace, String middleNameSpace, String targetNameSpace) throws IOException {
        output.write("@prefix : <#> .");
        output.newLine();
        output.write("@prefix void: <http://rdfs.org/ns/void#> .");
        output.newLine();
        output.newLine();
        output.write(":transitive a void:Linkset .");
        output.newLine();
        output.write(":transitive void:subjectsTarget :source .");
        output.newLine();
        output.write(":transitive void:objectsTarget :target .");
        output.newLine();
        output.write(":transitive void:linkPredicate ");
        output.write(LINK_PREDICATE);
        output.write(" . ");
        output.newLine();
        output.write(":transative  <http://www.bridgedb.org/transitiveNameSpace> <");
        output.write(middleNameSpace);
        output.write("> . ");
        output.newLine();
        output.write(":source a void:Dataset  .");
        output.newLine();
        output.write(":source void:uriSpace <");
        output.write(sourceNameSpace);
        output.write("> . ");        
        output.newLine();
        output.write(":target a void:Dataset  .");
        output.newLine();
        output.write(":target void:uriSpace <");
        output.write(targetNameSpace);
        output.write("> . ");        
        output.newLine();
        output.newLine();
    }
    
    public void writeVoidBody(BufferedWriter output, 
            String sourceNameSpace, String middleNameSpace, String targetNameSpace) 
            throws IDMapperException, IOException{
        long start = new Date().getTime();
        System.out.println("Start query");
        Iterable<String> iterable = urlMapperSQL.getURLIterator(sourceNameSpace);
        System.out.println("Got Iterable");
        System.out.println(new Date().getTime() - start);
        Iterator<String> sources = iterable.iterator();
        while (sources.hasNext()){
            String source = sources.next();
            Set<String> middleSet = urlMapperSQL.mapURL(source, middleNameSpace);
            Iterator<String> middles = middleSet.iterator();
            while (middles.hasNext()){
                String middle = middles.next();
                Set<String> targetSet = urlMapperSQL.mapURL(middle, targetNameSpace);
                Iterator<String> targets = targetSet.iterator();
                while (targets.hasNext()){
                    String target = targets.next();
                    //System.out.println("<" + source + "> <http://www.bridgedb.org/transitiveMapping> <" + target + "> .");
                    output.write("<" + source + "> " + LINK_PREDICATE + " <" + target + "> .");
                    output.newLine();
                }
            }    
        }       
    }
    
    public static void main(String[] args) throws BridgeDbSqlException, IDMapperException, IOException {
        Transitive worker = new Transitive();
        worker.createVoid("D:/OpenPhacts/linksets/chemspider_chembl_transitive.ttl", 
                "http://rdf.chemspider.com/", "http://data.kasabi.com/dataset/chembl-rdf/chemblid/", "http://data.kasabi.com/dataset/chembl-rdf/molecule/");
                //"http://www.foo.com/", "http://www.example.com/", "http://www.example.org#");
        //Iterable<String> iterable = urlMapperSQL.getURLIterator("http://data.kasabi.com/dataset/chembl-rdf/chemblid/");
    }
 
}
