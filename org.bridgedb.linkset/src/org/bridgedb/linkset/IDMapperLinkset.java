/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.util.Arrays;
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
import org.bridgedb.impl.InternalUtils;
import org.openrdf.model.URI;

/**
 *
 * @author Christian
 */
public class IDMapperLinkset implements IDMapper, LinkListener{

    private Set<DataSource> sources;
    private Set<DataSource> targets;
    private String predicate;
    private Map<Xref,Set<Xref>> sourceToTarget;
    int linkCount;
    
    public IDMapperLinkset() {
        reset();
    }

    private void reset() {
        sources = new HashSet<DataSource>();
        targets = new HashSet<DataSource>();
        predicate = null;;
        sourceToTarget = new HashMap<Xref,Set<Xref>>();
        linkCount = 0;
    }

    @Override
    public void insertLink(URI source, String predicate, URI target) throws IDMapperLinksetException{
        linkCount ++;
        if (linkCount % 100000 == 0){
            System.out.println("Processeed " + linkCount + " link so far");
        }
        //ystem.out.println(source);
        DataSource sourceDataSource = DataSource.getByURL(source.stringValue());
        sources.add(sourceDataSource);
        Xref sourceXref = new Xref(source.getLocalName(), sourceDataSource);
        Set<Xref> targetSet = sourceToTarget.get(sourceXref);
        //ystem.out.println(targetSet);
        if (targetSet == null){
            targetSet = new HashSet<Xref>();
        }
        
        if (this.predicate == null){
            this.predicate = predicate;
        } else if (!this.predicate.equals(predicate)){
            throw new IDMapperLinksetException (this.getClass() + " is unbale to handle multiple predicates. "
                    + "Found " + predicate + " but already had " + this.predicate);
        }
        
        DataSource targetDataSource = DataSource.getByURL(target.stringValue());        
        targets.add(targetDataSource);
        Xref targetXref = new Xref(target.getLocalName(), targetDataSource);
        targetSet.add(targetXref);
        sourceToTarget.put(sourceXref, targetSet);
        //String nameSpace = getNameSpace(targetURI);
        //System.out.println(sourceXref + "   "+ targetXref);
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

    @Override
    public void close() throws IDMapperException {
        reset();
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

    private class IDMapperLinksetCapabilities extends AbstractIDMapperCapabilities {
        
        private IDMapperLinksetCapabilities() {
            super(IDMapperLinkset.this.getDataSources(), false, null);
        }
    }
    

} 
