package org.bridgedb.url;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains the information held for a particular mapping.
 * <p>
 * @See getMethods for what is returned.
 * <p>
 * A few things that are not returned and why included:
 * <ul>
 * <li>UriSpace: 
 * @author Christian
 */
public class URLMapping {
 
    private Integer id;
    private Set<String> sourceURLs;
    private Set<String> targetURLs;
    private Integer mappingSetId;
    private String predicate;
    
    public URLMapping (Integer id, String sourceURL, String predicate, String targetURL, Integer mappingSetId){
        this.id = id;
        this.sourceURLs = new HashSet<String>();
        sourceURLs.add(sourceURL);
        this.targetURLs = new HashSet<String>();
        targetURLs.add(targetURL);
        this.mappingSetId = mappingSetId;
        this.predicate = predicate;
    }

    public URLMapping (Integer id, Set<String> sourceURLs, String predicate, Set<String> targetURLs, Integer mappingSetId){
        this.id = id;
        this.sourceURLs = sourceURLs;
        this.mappingSetId = mappingSetId;
        this.targetURLs = targetURLs;
        this.predicate = predicate;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the sourceURLs
     */
    public Set<String> getSourceURLs() {
        return sourceURLs;
    }

    public void addSourceURL(String sourceURL){
        sourceURLs.add(sourceURL);
    }
    
    /**
     * @return the target URLs
     */
    public Set<String> getTargetURLs() {
        return targetURLs;
    }

    public void addTargetURL(String targetURL){
        targetURLs.add(targetURL);
    }

    public String toString(){
        StringBuilder output = new StringBuilder("mapping ");
        output.append(this.id);
        for (String sourceURL:sourceURLs){
            output.append("\n\tSourceURL: ");
            output.append(sourceURL);
        }
        output.append("\n\tPredicate(): ");
        output.append(predicate);
        for (String targetURL:targetURLs){
            output.append("\n\tTargetURL: ");
            output.append(targetURL);
        }
        output.append("\n\tMappingSet(id): ");
        output.append(mappingSetId);
        return output.toString();
    }
   
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other instanceof URLMapping){
            URLMapping otherMapping = (URLMapping)other;
            if (otherMapping.id != id) return false;
            if (!otherMapping.sourceURLs.equals(sourceURLs)) return false;
            if (!otherMapping.targetURLs.equals(targetURLs)) return false;
            if (!otherMapping.getMappingSetId().equals(getMappingSetId())) return false;
            //No need to check predicate as by defintion one id has one predicate
            return true;
         } else {
            return false;
        }
    }

    /**
     * @return the mappingSetId
     */
    public Integer getMappingSetId() {
        return mappingSetId;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }
}
