package org.bridgedb.linkset;

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
public interface LinkSetMapper {
    
    public Map<Xref, Set<XrefLinkSet>> mapIDwithLinkSet(List<Xref> srcXrefs, 
            List<String> linkSetIds, List<DataSource> targetDataSources) throws IDMapperException;

	public Set<XrefLinkSet> mapIDwithLinkSet (Xref ref, List<String> linkSetIds, 
            List<DataSource> targetDataSources) throws IDMapperException;
	
    public Set<URLMapping> mapURL(List<String> sourceURLs, List<String> linkSetIds, 
            List<String> targetURISpaces) throws IDMapperException;

}
