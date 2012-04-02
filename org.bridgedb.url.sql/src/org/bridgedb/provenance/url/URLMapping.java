/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance.url;

import org.bridgedb.provenance.Provenance;

/**
 *
 * @author Christian
 */
public class URLMapping {
 
    private int id;
    private String sourceURL;
    private String targetURL;
    private Provenance provenance;
    
    public URLMapping (int id, String sourceURL, String targetURL, Provenance provenance){
        this.id = id;
        this.sourceURL = sourceURL;
        this.targetURL = targetURL;
        this.provenance = provenance;
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
    public Provenance getProvenance() {
        return provenance;
    }
    
}
