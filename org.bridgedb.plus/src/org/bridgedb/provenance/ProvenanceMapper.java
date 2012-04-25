/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import java.util.Collection;
import java.util.List;
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
    
    public Map<Xref, Set<XrefProvenance>> mapIDProvenance(List<Xref> srcXrefs, 
            List<String> provenanceIds, List<DataSource> targetDataSources) throws IDMapperException;

	public Set<XrefProvenance> mapIDProvenance (Xref ref, List<String> provenanceIds, 
            List<DataSource> targetDataSources) throws IDMapperException;
	
    public Set<URLMapping> mapURL(List<String> sourceURLs, List<String> provenanceIds, 
            List<String> targetNameSpaces) throws IDMapperException;

//    public ProvenanceLink getProvenance(String id) throws IDMapperException;
}
