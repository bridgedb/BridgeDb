package org.bridgedb.provenance;

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
    public SimpleProvenance (String createdBy, String predicate, long creation, long upload){
        this(Provenance.NO_ID_ASSIGNED, createdBy, predicate, creation, upload);
    }
    
    /**
     * Constructor. However recommended call method is via the ProvenanceFactory
     * @param id  (Optional)
     * @param createdBy
     * @param predicate
     * @param creationDate
     * @param uploadDate 
     */
    public SimpleProvenance (int id, String createdBy, String predicate, long creation, long upload){
        this.id = id;
        this.createdBy = createdBy;
        this.predicate = predicate;
        this.creationDate = creation;
        this.uploadDate = upload;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof SimpleProvenance){
            SimpleProvenance sp = (SimpleProvenance)other;
            if (this.id != sp.id) return false;
            if (!this.createdBy.equals(sp.createdBy)) return false;
            if (!this.predicate.equals(sp.predicate)) return false;
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
    
}
