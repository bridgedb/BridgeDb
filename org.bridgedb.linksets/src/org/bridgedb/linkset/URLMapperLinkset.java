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
import org.bridgedb.Reporter;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.provenance.XrefProvenance;

/**
 * @author Christian
 */
public class URLMapperLinkset implements IDMapper, URLLinkListener{

    private Set<DataSource> sources;
    private Set<DataSource> targets;
    private Map<Xref,Set<XrefProvenance>> sourceToTarget;
    private Map<String, String> predicates;
    int linkCount;
    
    public URLMapperLinkset() {
//        openInput();
        isConnected = true; 
        sources = new HashSet<DataSource>();
        targets = new HashSet<DataSource>();
        sourceToTarget = new HashMap<Xref,Set<XrefProvenance>>();
        predicates = new HashMap<String, String>();
        linkCount = 0;
    }

    @Override
    public void openInput() {
    }

    @Override
    public void insertLink(String source, String target, String provenanceId) throws IDMapperException{
        linkCount ++;
        if (linkCount % 100000 == 0){
            Reporter.report("Processeed " + linkCount + " link so far");
        }
        Xref sourceXref = DataSource.uriToXref(source);
        Xref targetXref = DataSource.uriToXref(target);
        sources.add(sourceXref.getDataSource());
        Set<XrefProvenance> targetSet = sourceToTarget.get(sourceXref);
        //ystem.out.println(targetSet);
        if (targetSet == null){
            targetSet = new HashSet<XrefProvenance>();
        }
        
        targets.add(targetXref.getDataSource());
        targetSet.add(new XrefProvenance(targetXref, provenanceId, predicates.get(provenanceId)));
        sourceToTarget.put(sourceXref, targetSet);    
    }

    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    private Set<XrefProvenance> filterByTargetDataSources (Set<XrefProvenance> possible, DataSource... tgtDataSources){
        if (possible == null){
            return new HashSet<XrefProvenance>();
        }
        if (tgtDataSources.length == 0){
            //returns a shallow clone
            return new HashSet(possible);
        }
        Set<XrefProvenance> result = new HashSet<XrefProvenance>();
        Set<DataSource> tgtDss = new HashSet<DataSource>(Arrays.asList(tgtDataSources));
        for (XrefProvenance destRef : possible)
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
        Set<XrefProvenance> possible = sourceToTarget.get(ref);
        Set<XrefProvenance> filtered =  filterByTargetDataSources(possible, tgtDataSources);
        Set<Xref> results = new HashSet<Xref>();
        for (XrefProvenance xref:filtered){
            results.add(xref);
        }
        return results;
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
        Reporter.report ("Processed " + linkCount + " links");

        Reporter.report ("Found " + sources.size() + " sources");
        Reporter.report ("Found " + targets.size() + " targets");
        //ystem.out.println ("Found " + sourceToTarget.values().size() + " links");
        Reporter.report(sourceToTarget.size() + " source Xrefs");
        
        Collection<Set<XrefProvenance>> targetsXrefs = sourceToTarget.values();
        HashSet<Xref> targetHash = new HashSet (targetsXrefs);
        Reporter.report(targetHash.size() + " targets groups");
        targetHash = new HashSet<Xref>();
        for (Set<XrefProvenance> xrefs:targetsXrefs){
            targetHash.addAll(xrefs);
        }
        Reporter.report(targetHash.size() + " targets xrefs found");       
    }

   @Override
    public void closeInput() {
       //do nothing;
    }

    @Override
    public void registerProvenanceLink(String provenanceId, DataSource source, String predicate, DataSource target)
            throws IDMapperException {
        String oldPredicate = predicates.get(provenanceId);
        if (oldPredicate != null && !oldPredicate.equals(predicate)){
            throw new IDMapperException ("Provenance " + provenanceId + " already has predicate " + oldPredicate + 
                    " so can not be registered with " + predicate);
        }
        predicates.put(provenanceId, predicate);
    }

    private class IDMapperLinksetCapabilities extends AbstractIDMapperCapabilities {
        
        private IDMapperLinksetCapabilities() {
            super(URLMapperLinkset.this.getDataSources(), false, null);
        }
    }
    

} 
