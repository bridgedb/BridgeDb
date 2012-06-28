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
 * Provides the IDMapper interface by wrapping an URLMapper
 * 
 * @author Christian
 */
public class WappedIDMapper implements IDMapper{

    private URLMapper urlMapper;
    
    public WappedIDMapper(URLMapper urlMapper){
        this.urlMapper = urlMapper;
    }
    
    @Override
    public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources) 
            throws IDMapperException {
        HashSet<String> sourceURLs = new HashSet<String>();
        for (Xref srcXref: srcXrefs){
            String url = srcXref.getUrl();
            if (url != null){
                sourceURLs.add(url);
            }
        }
        String[] targetURISpaces = new String[tgtDataSources.length];
        for (int i = 0; i < tgtDataSources.length; i++){
            targetURISpaces[i] = tgtDataSources[i].getURISpace();
        }
        Map<String, Set<String>> mapURLs = urlMapper.mapURL(sourceURLs, targetURISpaces);
        HashMap<Xref, Set<Xref>> results = new HashMap<Xref, Set<Xref>>();
        for (String src: mapURLs.keySet()){
            HashSet<Xref> mapped = new HashSet<Xref>();
            for (String tgt: mapURLs.get(src)){
                mapped.add(DataSource.uriToXref(tgt));
            }
            results.put(DataSource.uriToXref(src), mapped);
        }      
        return results;
    }

    @Override
    public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException {
        HashSet<Xref> results = new HashSet<Xref>();
        String src = ref.getUrl();
        if (src == null) return results;
        String[] targetURISpaces = new String[tgtDataSources.length];
        for (int i = 0; i < tgtDataSources.length; i++){
            targetURISpaces[i] = tgtDataSources[i].getURISpace();
        }
        for (String tgt: urlMapper.mapURL(src, targetURISpaces)){
            results.add(DataSource.uriToXref(tgt));
        }      
        return results;
    }

    @Override
    public boolean xrefExists(Xref xref) throws IDMapperException {
        String url = xref.getUrl();
        if (url == null) return false;
        return urlMapper.uriExists(url);
    }

    @Override
    public Set<Xref> freeSearch(String text, int limit) throws IDMapperException {
        HashSet<Xref> results = new HashSet<Xref>();
        for (String url: urlMapper.urlSearch(text, limit)){
            results.add(DataSource.uriToXref(url));
        }
        return results;
    }

    @Override
    public IDMapperCapabilities getCapabilities() {
        return urlMapper.getCapabilities();
    }

    @Override
    public void close() throws IDMapperException {
        urlMapper.close();
    }

    @Override
    public boolean isConnected() {
        return urlMapper.isConnected();
    }
    
}
