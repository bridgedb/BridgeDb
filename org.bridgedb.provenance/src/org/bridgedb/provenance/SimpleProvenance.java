package org.bridgedb.provenance;

import java.util.Date;
import org.bridgedb.provenance.Provenance;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Warning under development. Will change without notice.
 *
 * @author Christian
 */
public class SimpleProvenance implements Provenance{

    private String createdBy;
    private String predicate;
    private Date creationDate;
    private Date uploadDate;
    
    public SimpleProvenance (String createdBy, String predicate, Date creationDate, Date uploadDate){
        this.createdBy = createdBy;
        this.predicate = predicate;
        this.creationDate = creationDate;
        this.uploadDate = uploadDate;
    }
    
    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public Date getUploadDate() {
        return uploadDate;
    }

    @Override
    public boolean isTransative() {
        return false;
    }
    
}
