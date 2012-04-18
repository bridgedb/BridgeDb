package org.bridgedb.url;

import java.util.List;
import org.bridgedb.result.URLMapping;

/**
 *
 * @author Christian
 */
public interface FullMapper {
    public List<URLMapping> getMappings(List<String> URLs, List<String> sourceURLs, List<String> targetURLs, 
            List<String> nameSpaces, List<String> sourceNameSpaces, List<String> targetNameSpaces, 
            List<String> provenanceIds, Integer position, Integer limit);

    public URLMapping getMapping(int id);
}
