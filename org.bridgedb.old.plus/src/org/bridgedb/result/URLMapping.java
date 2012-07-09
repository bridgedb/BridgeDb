/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.result;

/**
 *
 * @author Christian
 */
public class URLMapping extends ResultBase{
 
    private int id;
    private String sourceURL;
    private String targetURL;
    private String linkSetId;
    private String predicate;
    private static String SAFE_MESSAGE = "Error generating Mapping. The administrstor has been informed";
    
    public URLMapping (int id, String sourceURL, String targetURL, String linkSetId, String predicate){
        errorMessage = "";
        this.id = id;
        if (sourceURL == null || targetURL == null){
            errorMessage+= "Sorry Error Generating the mapping for id " + id;
        } 
        this.sourceURL = sourceURL;
        this.targetURL = targetURL;
        this.linkSetId = linkSetId;
        this.predicate = predicate;
    }

    public URLMapping (String errorMessage){
        super(errorMessage);
    }
        
    public URLMapping(Exception ex){
        super(ex, SAFE_MESSAGE);
    }
    
    public URLMapping(Exception ex, String query){
        super(ex, query, SAFE_MESSAGE);
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the source
     */
    public String getSourceURL() {
        return sourceURL;
    }

    /**
     * @return the target
     */
    public String getTargetURL() {
        return targetURL;
    }

    public String toString(){
        if (isValid()){
            return this.id  + ": " + this.sourceURL + " " + this.getPredicate() + " " + this.targetURL + 
                    " linkSet: " + this.getLinkSetId();
        } else {
            return "URLMapping: Error " + errorMessage;
        }
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other instanceof URLMapping){
            URLMapping otherMapping = (URLMapping)other;
            if (super.sameError(otherMapping)) return true;
            if (otherMapping.id != id) return false;
            if (!otherMapping.sourceURL.equals(sourceURL)) return false;
            if (!otherMapping.targetURL.equals(targetURL)) return false;
            if (!otherMapping.getLinkSetId().equals(getLinkSetId())) return false;
            //No need to check predicate as by defintion one id has one predicate
            return true;
         } else {
            return false;
        }
    }

    /**
     * @return the linkSetId
     */
    public String getLinkSetId() {
        return linkSetId;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }
}
