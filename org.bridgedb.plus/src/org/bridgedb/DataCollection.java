package org.bridgedb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bridgedb.impl.InternalUtils;

/**
 * This prototype ignores the side aspects of name, identifier Miram URN ect.
 * @author Christian
 */
public class DataCollection implements IDMapper{

    private Set<DataSource> mappedSources;
    private String patternString = null;
    
    public DataCollection(Set<DataSource> mappedSources){
        this.mappedSources = mappedSources;
    }
    
    public DataCollection(Set<DataSource> mappedSources, String patternString){
        this.mappedSources = mappedSources;
        this.patternString = patternString;
    }

    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) throws IDMapperException {
        return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {        
        Set<Xref> results = new HashSet<Xref>();
        if (!this.xrefExists(ref)){
            return results;
        }
        if (tgtDataSources.length == 0){
            for (DataSource dataSource:mappedSources) {
                results.add(new Xref (ref.getId(), dataSource));
            }
        } else {
            for (DataSource dataSource:mappedSources) {
                for (DataSource tgtDataSource:tgtDataSources) {
                    if (dataSource == tgtDataSource) {
                        results.add(new Xref (ref.getId(), dataSource));
                    }
                }
            }            
        }
        return results;
    }

    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        if (xref.getId() == null || xref.getDataSource() == null){
            return false;
        }
        if (patternString != null){
            Pattern p = Pattern.compile(patternString);
            Matcher m = p.matcher(xref.getId());
            if (!m.matches()) return false;
        }

        //Maybe add some id pattern checking
        if (mappedSources.contains(xref.getDataSource())){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        if (patternString == null) {
            throw new UnsupportedOperationException("Not supported yet.");        
        } 
        Pattern p = Pattern.compile(patternString);
        Matcher m = p.matcher(text);
        boolean allowedId = m.matches();
        if (allowedId){
            Xref temp = new Xref(text, mappedSources.iterator().next());
            Set<Xref> possible = mapID(temp);
            if (possible.size() <= limit){
                return possible;
            } else {
                Set<Xref> smaller = new HashSet<Xref>();
                Iterator<Xref> iterator = possible.iterator();
                for (int i = 0; i < limit; i++){
                    smaller.add(iterator.next());
                }
                return smaller;
            }
        } else {
            return new HashSet<Xref>();
        } 
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return new DataCollectionCapabilities(patternString != null);
    }

    private boolean isConnected = true;
    // In the case of DataCollection, there is no need to discard associated resources.
    
    @Override
    /** {@inheritDoc} */
    public void close() throws IDMapperException { 
        isConnected = false; 
        
    }
    
    @Override
    /** {@inheritDoc} */
    public boolean isConnected() { return isConnected; }

    private class DataCollectionCapabilities extends AbstractIDMapperCapabilities {

        public DataCollectionCapabilities(final boolean freeSearch) 
        {
            super (mappedSources, freeSearch, null);
        }

    }

}
