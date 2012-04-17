/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.result;

import org.bridgedb.provenance.ProvenanceLink;

/**
 *
 * @author Christian
 */
public class URLMapping extends ResultBase{
 
    private int id;
    private String sourceURL;
    private String targetURL;
    private ProvenanceLink provenanceLink;
    private static String SAFE_MESSAGE = "Error generating Mapping. The administrstor has been informed";
    
    public URLMapping (int id, String sourceURL, String targetURL, ProvenanceLink provenanceLink){
        errorMessage = "";
        this.id = id;
        if (sourceURL == null || targetURL == null){
            errorMessage+= "Sorry Error Generating the mapping for id " + id;
        } 
        this.sourceURL = sourceURL;
        this.targetURL = targetURL;
        this.provenanceLink = provenanceLink;
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

    /**
     * @return the provenance
     */
    public ProvenanceLink getProvenanceLink() {
        return provenanceLink;
    }
    
    public String toString(){
        if (isValid()){
            if (provenanceLink == null){
                return this.id  + ": " + sourceURL + " " + provenanceLink.getPredicate() + " " + this.targetURL + 
                    " No provenance. ";
            } else {
                return this.id  + ": " + sourceURL + " " + provenanceLink.getPredicate() + " " + this.targetURL + 
                    " provenance: " + this.provenanceLink.getId();
            }
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
            if (otherMapping.sourceURL.equals(sourceURL)) return false;
            if (otherMapping.targetURL.equals(targetURL)) return false;
            if (provenanceLink == null){
                return otherMapping.provenanceLink == null;
            } else {
                return provenanceLink.equals(otherMapping.provenanceLink);
            }
        } else {
            return false;
        }
    }
}
