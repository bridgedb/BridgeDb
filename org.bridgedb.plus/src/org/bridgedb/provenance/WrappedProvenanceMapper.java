package org.bridgedb.provenance;

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
public class WrappedProvenanceMapper implements ProvenanceMapper{

    private IDMapper idMapper;
    private URLMapper urlMapper;
    public static final String DEFAULT_PREDICATE = "http://www.bridgedb.org/mapsto";
    public static final String ID_DIVIDER = "->";
    private final String predicate;
    
    public WrappedProvenanceMapper(IDMapper idMapper){
        this(idMapper, DEFAULT_PREDICATE);
    }
    
    public WrappedProvenanceMapper(IDMapper idMapper, String provenance){
        this.idMapper = idMapper;
        if (idMapper instanceof URLMapper){
            urlMapper = (URLMapper)idMapper;
        } else {
            urlMapper = new WrapperURLMapper(idMapper);
        }
        this.predicate = provenance;
    }

    @Override
    public Map<Xref, Set<XrefProvenance>> mapIDProvenance(Collection<Xref> srcXrefs, 
            Collection<String> provenanceIds, Collection<DataSource> tgtDataSources) throws IDMapperException {
        Map<Xref, Set<Xref>> plainResults = idMapper.mapID(srcXrefs, tgtDataSources.toArray(new DataSource[0]));
        Map<Xref, Set<XrefProvenance>> results = new HashMap<Xref, Set<XrefProvenance>>();
        for (Xref key:plainResults.keySet()){
            Set<Xref> plain = plainResults.get(key);
            results.put(key, convertToXrefProvenance(key, plain, provenanceIds));
        }
        return results;
    }

    private Set<XrefProvenance> convertToXrefProvenance(Xref ref, Set<Xref> plainResults,
            Collection<String> provenanceIds){
        Set<XrefProvenance> with = new HashSet<XrefProvenance>();
        for (Xref xref:plainResults){
            ProvenanceLink provenanceLink = new ProvenanceLink(
                    //The source nameSpace
                    ref.getDataSource().getNameSpace() + ID_DIVIDER + xref.getDataSource().getNameSpace(), 
                    ref.getDataSource(), predicate, xref.getDataSource());
            if (provenanceIds.isEmpty()){
                with.add(new XrefProvenance(xref, provenanceLink));
            } else {
                for (String id: provenanceIds){
                    if (id.equals(provenanceLink.getId())){
                        with.add(new XrefProvenance(xref, provenanceLink));
                    }
                }
            }
        }
        return with;
    }
    
    @Override
    public Set<XrefProvenance> mapIDProvenance(Xref ref, Collection<String> provenanceIds, 
            Collection<DataSource> tgtDataSources) throws IDMapperException {
        Set<Xref> plain = idMapper.mapID(ref, tgtDataSources.toArray(new DataSource[0]));
        return convertToXrefProvenance(ref, plain, provenanceIds);
    }

    @Override
    public Set<URLMapping> mapURL(Collection<String> srcURLs, Collection<String> provenanceIds, 
            Collection<String> tgtNameSpaces) throws IDMapperException {
        Map<String, Set<String>> plainResults = urlMapper.mapURL(srcURLs, tgtNameSpaces.toArray(new String[0]));
        Set<URLMapping> results = new HashSet<URLMapping>();
        for (String key:plainResults.keySet()){
            Set<String> plain = plainResults.get(key);
            results.addAll(convertToURLMapping(key, plain, provenanceIds));
        }
        return results;
    }

    private Set<URLMapping> convertToURLMapping(String ref, Set<String> plainResults,
            Collection<String> provenanceIds){
        Set<URLMapping> with = new HashSet<URLMapping>();
        for (String url:plainResults){
            DataSource sourceDS = DataSource.getByURL(ref);
            DataSource targetDS = DataSource.getByURL(url);
            ProvenanceLink provenanceLink = new ProvenanceLink(
                    sourceDS.getNameSpace() + ID_DIVIDER + targetDS.getNameSpace(), sourceDS, predicate, targetDS);
            if (provenanceIds.isEmpty()){
                with.add(new URLMapping(0, ref, url, provenanceLink));
            } else {
                for (String id:provenanceIds){
                    if (id.equals(provenanceLink.getId())){
                        with.add(new URLMapping(0, ref, url, provenanceLink));
                    }
                }
            }
        }
        return with;
    }

 }
