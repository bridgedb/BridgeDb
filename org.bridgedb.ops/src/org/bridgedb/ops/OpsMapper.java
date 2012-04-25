package org.bridgedb.ops;

import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.result.URLMapping;
import org.bridgedb.statistics.OverallStatistics;

/**
 *
 * @author Christian
 */
public interface OpsMapper {
    public List<URLMapping> getMappings(List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces, 
            List<String> provenanceIds, Integer position, Integer limit);

    public URLMapping getMapping(int id);

    public List<Xref> getXrefs(List<DataSource> dataSources, List<String> provenanceIds, Integer position, Integer limit) 
            throws IDMapperException;

    public List<String> getURLs(List<String> nameSpaces, List<String> provenanceIds, Integer position, Integer limit) 
            throws IDMapperException;
    
    public  OverallStatistics getOverallStatistics() throws IDMapperException;
    
    public List<ProvenanceInfo> getProvenanceInfos() throws IDMapperException;


}
