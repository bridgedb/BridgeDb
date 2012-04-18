package org.bridgedb.url;

import java.util.List;
import org.bridgedb.IDMapperException;
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
    
    public  OverallStatistics getOverallStatistics() throws IDMapperException;
}
