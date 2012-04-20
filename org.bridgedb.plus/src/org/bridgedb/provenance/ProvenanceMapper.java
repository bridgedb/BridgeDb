/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.result.URLMapping;

/**
 *
 * @author Christian
 */
public interface ProvenanceMapper {
    
    public Map<Xref, Set<XrefProvenance>> mapIDProvenance(Collection<Xref> srcXrefs, 
            Collection<String> provenanceIds, Collection<DataSource> targetDataSources) throws IDMapperException;

	public Set<XrefProvenance> mapIDProvenance (Xref ref, Collection<String> provenanceIds, 
            Collection<DataSource> targetDataSources) throws IDMapperException;
	
    public Set<URLMapping> mapURL(Collection<String> sourceURLs, Collection<String> provenanceIds, 
            Collection<String> targetNameSpaces) throws IDMapperException;

//    public ProvenanceLink getProvenance(String id) throws IDMapperException;
}
