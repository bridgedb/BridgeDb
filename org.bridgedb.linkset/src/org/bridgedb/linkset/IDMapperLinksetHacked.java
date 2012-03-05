/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public class IDMapperLinksetHacked implements IDMapper{

    private File linksetFile;
    private Set<DataSource> sources = new HashSet<DataSource>();
    private Set<DataSource> targets = new HashSet<DataSource>();
    private String predicate;
    private Map<Xref,Set<Xref>> sourceToTarget = new HashMap<Xref,Set<Xref>>();
    
    public IDMapperLinksetHacked(File linksetFile) throws IOException, IDMapperException{
        this.linksetFile = linksetFile;
        LoadFile(linksetFile);
    }

    private IDMapperLinksetHacked(String linksetFileName) throws IOException, IDMapperException {
        this(new File(linksetFileName));
    }
    
    private void LoadFile(File linksetFile) throws IOException, IDMapperException {
        FileReader reader = new FileReader(linksetFile);
        BufferedReader buffer = new BufferedReader(reader);
        //Read headers
        String line = buffer.readLine();
        while (! line.startsWith("<")){
            line = buffer.readLine();
        }
        String[] blocks = splitLine(line);
        predicate = blocks[1];
        loadLink(blocks[0], blocks[2]);
        line = buffer.readLine();
        int count = 1;
        while (line != null){
            //ystem.out.println(line);
            blocks = splitLine(line);
            sources.add(uriToDataSource(blocks[0]));
            if (!predicate.equals(blocks[1])){
                throw new IDMapperLinksetException ("Multiple predicates found " + predicate + "   " + blocks[1]);
            }
            loadLink(blocks[0], blocks[2]);
            count++;
            line = buffer.readLine();
        }
        System.out.println ("Found " + sources.size() + " sources");
        System.out.println ("Found " + targets.size() + " targets");
        System.out.println ("Found " + count + " links");
        System.out.println(sourceToTarget.size() + " source Xrefs");
        Set<Xref> targets = new HashSet(sourceToTarget.values());
        System.out.println(targets.size() + " targets ");
   }
    
    private void loadLink(String sourceURI, String targetURI) throws IDMapperException{
        DataSource sourceDataSource = uriToDataSource(sourceURI);
        sources.add(sourceDataSource);
        Xref sourceXref = new Xref(getId(sourceURI), sourceDataSource);
        Set<Xref> targetSet = sourceToTarget.get(sourceXref);
        if (targetSet == null){
            targetSet = new HashSet<Xref>();
        }
        
        DataSource targetDataSource = uriToDataSource(targetURI);        
        targets.add(targetDataSource);
        Xref targetXref = new Xref(getId(targetURI), targetDataSource);
        targetSet.add(targetXref);
        sourceToTarget.put(sourceXref, targetSet);
        //String nameSpace = getNameSpace(targetURI);
        System.out.println(targetXref);
    }
    
    private String[] splitLine(String line) throws IDMapperLinksetException {
        line = line.trim();
        if (line.endsWith(">.")){
            line = line.substring(0, line.length() - 1) + " .";
        }
        String[] blocks = line.split(" ");
        if (blocks.length > 4){
            throw new IDMapperLinksetException ("Unexpected Line Format (number of blocks = " + blocks.length + ") " 
                    + line);
        }
        if (blocks[0].startsWith("<") && blocks[0].endsWith(">")){
            blocks[0] = blocks[0].substring(1, blocks[0].length() -1);
        } else {
            throw new IDMapperLinksetException ("Unexpected Line Format (blocks[0] < or >) " + line);            
        }
        if (blocks[2].startsWith("<") && blocks[2].endsWith(">")){
            blocks[2] = blocks[2].substring(1, blocks[2].length() -1);
        } else {
            throw new IDMapperLinksetException ("Unexpected Line Format (blocks[2] < or >) " + line);            
        }
        if (!blocks[3].equals(".")){
            throw new IDMapperLinksetException ("Unexpected Line Format (blocks[3] not \".\") " + line);   
        }
        return blocks;
    }

    private DataSource uriToDataSource(String url) throws IDMapperException {
        DataSource result = DataSource.getByURLPattern(url);
        if (result != null){
            return result;
        }
        String nameSpace = getNameSpace(url);
        
        result = DataSource.register(nameSpace, nameSpace).urlPattern(nameSpace + "$id").asDataSource();
        return result;
    }

    private String getNameSpace(String url) throws IDMapperLinksetException {
        url = url.trim();
        if (url.contains("#")){
            return url.substring(0, url.lastIndexOf("#")+1);
        } else if (url.contains("/")){
            return url.substring(0, url.lastIndexOf("/")+1);
        } else if (url.contains(":")){
            return url.substring(0, url.lastIndexOf(":")+1);
        }
        throw new IDMapperLinksetException ("Unexpected url Format " + url);   
    }

    private String getId(String url) throws IDMapperLinksetException {
        url = url.trim();
        if (url.contains("#")){
            return url.substring(url.lastIndexOf("#") + 1);
        } else if (url.contains("/")){
            return url.substring(url.lastIndexOf("/") + 1);
        } else if (url.contains(":")){
            return url.substring(url.lastIndexOf(":") + 1);
        }
        throw new IDMapperLinksetException ("Unexpected url Format " + url);   
    }

    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return new IDMapperLinksetCapabilities();
    }

    @Override
    public void close() throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Set<DataSource> getDataSources() {
        HashSet<DataSource> result = new HashSet<DataSource>(sources);
        result.addAll(targets);
        return result;
    }
    
    private class IDMapperLinksetCapabilities extends AbstractIDMapperCapabilities {
        
        private IDMapperLinksetCapabilities() {
            super(IDMapperLinksetHacked.this.getDataSources(), false, null);
        }
    }
    
    public static void main( String[] args ) throws IOException, IDMapperException {
        IDMapperLinksetHacked instance = new IDMapperLinksetHacked("C:/Temp/cs-chembl_small.ttl");
        System.out.println(instance.getCapabilities().getSupportedSrcDataSources());
    }
    

} 
