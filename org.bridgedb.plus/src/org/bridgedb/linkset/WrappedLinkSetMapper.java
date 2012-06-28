package org.bridgedb.linkset;

import org.bridgedb.linkset.LinkSetMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.result.URLMapping;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.WrapperURLMapper;

/**
 *
 * @author Christian
 */
public class WrappedLinkSetMapper implements LinkSetMapper{

    private IDMapper idMapper;
    private URLMapper urlMapper;
    public static final String DEFAULT_PREDICATE = "http://www.bridgedb.org/mapsto";
    public static final String ID_DIVIDER = "->";
    private final String predicate;
    
    public WrappedLinkSetMapper(IDMapper idMapper){
        this(idMapper, DEFAULT_PREDICATE);
    }
    
    public WrappedLinkSetMapper(IDMapper idMapper, String linkSet){
        this.idMapper = idMapper;
        if (idMapper instanceof URLMapper){
            urlMapper = (URLMapper)idMapper;
        } else {
            urlMapper = new WrapperURLMapper(idMapper);
        }
        this.predicate = linkSet;
    }

    @Override
    public Map<Xref, Set<XrefLinkSet>> mapIDwithLinkSet(List<Xref> srcXrefs, 
            List<String> linksetIds, List<DataSource> tgtDataSources) throws IDMapperException {
        Map<Xref, Set<Xref>> plainResults = idMapper.mapID(srcXrefs, tgtDataSources.toArray(new DataSource[0]));
        Map<Xref, Set<XrefLinkSet>> results = new HashMap<Xref, Set<XrefLinkSet>>();
        for (Xref key:plainResults.keySet()){
            Set<Xref> plain = plainResults.get(key);
            results.put(key, convertToXrefLinkSet(key, plain, linksetIds));
        }
        return results;
    }

    private Set<XrefLinkSet> convertToXrefLinkSet(Xref ref, Set<Xref> plainResults,
            List<String> linkSetIds){
        Set<XrefLinkSet> with = new HashSet<XrefLinkSet>();
        for (Xref xref:plainResults){
            String linkSetId = createLinkSetId(ref.getDataSource(), xref.getDataSource());
            if (linkSetIds.isEmpty() || linkSetIds.contains(linkSetId)){
                with.add(new XrefLinkSet(xref, linkSetId, predicate));
            }
        }
        return with;
    }
    
    @Override
    public Set<XrefLinkSet> mapIDwithLinkSet(Xref ref, List<String> linkSetIds, 
            List<DataSource> tgtDataSources) throws IDMapperException {
        Set<Xref> plain = idMapper.mapID(ref, tgtDataSources.toArray(new DataSource[0]));
        return convertToXrefLinkSet(ref, plain, linkSetIds);
    }

    @Override
    public Set<URLMapping> mapURL(List<String> sourceURLs, List<String> linkSetIds, 
            List<String> targetURISpaces) throws IDMapperException {
        Map<String, Set<String>> plainResults = urlMapper.mapURL(sourceURLs, targetURISpaces.toArray(new String[0]));
        Set<URLMapping> results = new HashSet<URLMapping>();
        for (String key:plainResults.keySet()){
            Set<String> plain = plainResults.get(key);
            results.addAll(convertToURLMapping(key, plain, linkSetIds));
        }
        return results;
    }

    private Set<URLMapping> convertToURLMapping(String ref, Set<String> plainResults,
            Collection<String> linkSetIds){
        Set<URLMapping> with = new HashSet<URLMapping>();
        for (String url:plainResults){
            DataSource sourceDS = DataSource.getByURL(ref);
            DataSource targetDS = DataSource.getByURL(url);
            String linkSetId = createLinkSetId(sourceDS, targetDS);
            if (linkSetIds.isEmpty() || linkSetIds.contains(linkSetId)){
                with.add(new URLMapping(0, ref, url, linkSetId, predicate));
            } 
        }
        return with;
    }

    //@Override
    public Set<String> getLinkSetIds() throws IDMapperException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String createLinkSetId(DataSource source, DataSource target){
        return source.getURISpace() + ID_DIVIDER + target.getURISpace();
    }
 }
