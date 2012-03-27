/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;

/**
 * @author Christian
 */
public class IDMapperLinkset implements IDMapper, LinkListener{

    private Set<DataSource> sources;
    private Set<DataSource> targets;
    private Map<Xref,Set<Xref>> sourceToTarget;
    int linkCount;
    
    public IDMapperLinkset() {
        openInput();
    }

    @Override
    public void openInput() {
        isConnected = true; 
        sources = new HashSet<DataSource>();
        targets = new HashSet<DataSource>();
        sourceToTarget = new HashMap<Xref,Set<Xref>>();
        linkCount = 0;
    }

//    @Override
/*    public void insertLink(URI source, String predicate, URI target) throws IDMapperLinksetException{
        DataSource sourceDataSource = DataSource.getByURL(source.stringValue());
        Xref sourceXref = new Xref(source.getLocalName(), sourceDataSource);

        if (this.predicate == null){
            this.predicate = predicate;
        } else if (!this.predicate.equals(predicate)){
            throw new IDMapperLinksetException (this.getClass() + " is unbale to handle multiple predicates. "
                    + "Found " + predicate + " but already had " + this.predicate);
        }
        
        DataSource targetDataSource = DataSource.getByURL(target.stringValue());        
        Xref targetXref = new Xref(target.getLocalName(), targetDataSource);
        
        insertLink(sourceXref, targetXref);
    }
*/
    public void insertLink(Xref source, Xref target) throws IDMapperLinksetException{
        linkCount ++;
        if (linkCount % 100000 == 0){
            System.out.println("Processeed " + linkCount + " link so far");
        }
        //ystem.out.println(source);
        sources.add(source.getDataSource());
        Set<Xref> targetSet = sourceToTarget.get(source);
        //ystem.out.println(targetSet);
        if (targetSet == null){
            targetSet = new HashSet<Xref>();
        }
        
        targets.add(target.getDataSource());
        targetSet.add(target);
        sourceToTarget.put(source, targetSet);    
    }
    
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    private Set<Xref> filterByTargetDataSources (Set<Xref> possible, DataSource... tgtDataSources){
        if (possible == null){
            return new HashSet<Xref>();
        }
        if (tgtDataSources.length == 0){
            //returns a shallow clone
            return new HashSet(possible);
        }
        Set<Xref> result = new HashSet<Xref>();
        Set<DataSource> tgtDss = new HashSet<DataSource>(Arrays.asList(tgtDataSources));
        for (Xref destRef : possible)
        {
            if (tgtDss.contains(destRef.getDataSource()))
            {
                result.add (destRef);
            }
        }
        return result;
    }
    
    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        Set<Xref> possible = sourceToTarget.get(ref);
        return filterByTargetDataSources(possible, tgtDataSources);
    }

    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        return sourceToTarget.containsKey(xref);
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return new IDMapperLinksetCapabilities();
    }

    private boolean isConnected = true;

    @Override
    public void close() throws IDMapperException {
        isConnected = false;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
        
    private Set<DataSource> getDataSources() {
        HashSet<DataSource> result = new HashSet<DataSource>(sources);
        result.addAll(targets);
        return result;
    }
    
    public void printStats () {
        System.out.println ("Processed " + linkCount + " links");

        System.out.println ("Found " + sources.size() + " sources");
        System.out.println ("Found " + targets.size() + " targets");
        System.out.println ("Found " + sourceToTarget.values().size() + " links");
        System.out.println(sourceToTarget.size() + " source Xrefs");
        
        Collection<Set<Xref>> targetsXrefs = sourceToTarget.values();
        HashSet<Xref> targetHash = new HashSet (targetsXrefs);
        System.out.println(targetHash.size() + " targets groups");
        targetHash = new HashSet<Xref>();
        for (Set<Xref> xrefs:targetsXrefs){
            targetHash.addAll(xrefs);
        }
        System.out.println(targetHash.size() + " targets xrefs found");       
    }

   @Override
    public void closeInput() {
        //do nothing;
    }

    private class IDMapperLinksetCapabilities extends AbstractIDMapperCapabilities {
        
        private IDMapperLinksetCapabilities() {
            super(IDMapperLinkset.this.getDataSources(), false, null);
        }
    }
    

} 
