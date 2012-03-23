package org.bridgedb.provenance;

import java.util.Date;
import org.bridgedb.DataSource;

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

    private DataSource source;
    private String predicate;
    private DataSource target;
    private String createdBy;
    private long creationDate;
    private long uploadDate;
    private int id;
    
    /**
     * Constructor. However recommended call method is via the ProvenanceFactory
     * @param createdBy
     * @param predicate
     * @param creationDate
     * @param uploadDate 
     */
    public SimpleProvenance (DataSource source, String predicate, DataSource target, 
            String createdBy, long creation, long upload){
        this(Provenance.NO_ID_ASSIGNED, source, predicate, target, createdBy, creation, upload);
    }
    
    /**
     * Constructor. However recommended call method is via the ProvenanceFactory
     * @param id  (Optional)
     * @param createdBy
     * @param predicate
     * @param creationDate
     * @param uploadDate 
     */
    public SimpleProvenance (int id, DataSource source, String predicate, DataSource target, 
            String createdBy, long creation, long upload){
        this.id = id;
        this.source = source;
        this.predicate = predicate;
        this.target = target;
        this.createdBy = createdBy;
        this.creationDate = creation;
        this.uploadDate = upload;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof SimpleProvenance){
            SimpleProvenance sp = (SimpleProvenance)other;
            if (this.id != sp.id) return false;
            if (this.source !=  sp.source) return false;
            if (!this.predicate.equals(sp.predicate)) return false;
            if (this.target !=  sp.target) return false;
            if (!this.createdBy.equals(sp.createdBy)) return false;
            if (this.creationDate != sp.creationDate) return false;
            return true;
        }
        return false;
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
    public long getCreation() {
        return creationDate;
    }

    @Override
    public long getUpload() {
        return uploadDate;
    }

    @Override
    public boolean isTransative() {
        return false;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public DataSource getSource() {
        return source;
    }

    @Override
    public DataSource getTarget() {
        return target;
    }

    @Override
    public String toString(){
        return source + " " + predicate + " " + target + " " + createdBy + " " + new Date(this.creationDate);
    }
}
