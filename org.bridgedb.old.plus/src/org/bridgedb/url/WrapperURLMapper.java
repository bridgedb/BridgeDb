package org.bridgedb.url;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * Provides the URLMapper interface by wrapping an IDMapper
 * 
 * @author Christian
 */
public class WrapperURLMapper implements URLMapper{

    IDMapper idMapper;
            
    public WrapperURLMapper (IDMapper idMapper){
        this.idMapper = idMapper;
    }
    
    @Override
    public Map<String, Set<String>> mapURL(Collection<String> sourceURLs, String... targetURISpaces) throws IDMapperException {
        HashSet<Xref> srcXrefs = new HashSet<Xref>();
        for (String sourceURL: sourceURLs){
            srcXrefs.add(DataSource.uriToXref(sourceURL));
        }
        DataSource[] tgtDataSources = new DataSource[targetURISpaces.length];
        for (int i = 0; i < targetURISpaces.length; i++){
            tgtDataSources[i] = DataSource.getByURISpace(targetURISpaces[i]);
        }
        Map<Xref, Set<Xref>> mapID = idMapper.mapID(srcXrefs, tgtDataSources);
        HashMap<String, Set<String>> result = new HashMap<String, Set<String>>();
        for (Xref src: mapID.keySet()){
            HashSet<String> map = new HashSet<String>();
            Set<Xref> tgtXrefs = mapID.get(src);
            for (Xref tgt: tgtXrefs){
                map.add(tgt.getUrl());
            }
            result.put(src.getUrl(), map);
        }
        return result;
    }

    @Override
    public Set<String> mapURL(String sourceURL, String... targetURISpaces) throws IDMapperException {
        Xref ref = DataSource.uriToXref(sourceURL);
        DataSource[] tgtDataSources = new DataSource[targetURISpaces.length];
        for (int i = 0; i < targetURISpaces.length; i++){
            tgtDataSources[i] = DataSource.getByURISpace(targetURISpaces[i]);
        }
        Set<Xref> mapID = idMapper.mapID(ref, tgtDataSources);
        HashSet<String> result = new HashSet<String>();
        for (Xref tgt: mapID){
            result.add(tgt.getUrl());
        }
        return result;
    }

    @Override
    public boolean uriExists(String URL) throws IDMapperException {
        Xref ref = DataSource.uriToXref(URL);
        return idMapper.xrefExists(ref);
    }

    @Override
    public Set<String> urlSearch(String text, int limit) throws IDMapperException {
        Set<Xref> xrefs = idMapper.freeSearch(text, limit);
        HashSet<String> result = new HashSet<String>();
        for (Xref tgt: xrefs){
            result.add(tgt.getUrl());
        }
        return result;
    }

    @Override
    /**
     * Passthrough method to the underlying IDMapper.
     */
    public IDMapperCapabilities getCapabilities() {
        return idMapper.getCapabilities();
    }

    @Override
    /**
     * Passthrough method to the underlying IDMapper.
     */
    public void close() throws IDMapperException {
        idMapper.close();
    }

    @Override
    /**
     * Passthrough method to the underlying IDMapper.
     */
    public boolean isConnected() {
        return idMapper.isConnected();
    }
    
}
