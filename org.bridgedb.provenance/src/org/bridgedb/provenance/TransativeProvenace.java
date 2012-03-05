/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import java.util.Date;
import org.bridgedb.IDMapperException;

/**
 * Warning under development. Will change without notice.
 *
 * @author Christian
 */
public class TransativeProvenace implements Provenance{

    private Provenance first;
    private Provenance second;
    
    /**
     * Constructor. However recommended call method is via the ProvenanceFactory
     * @param first
     * @param second 
     */
    TransativeProvenace(Provenance first, Provenance second){
        this.first = first;
        this.second = second;
    }
    
    @Override
    public String getPredicate() throws IDMapperException {
        if (first.getPredicate().equals(second.getPredicate())){
            return first.getPredicate();
        }
        throw new IDMapperException("Unable to create Provenace over different predicates!");
    }

    @Override
    public String getCreatedBy() {
        return first.getCreatedBy() + " and " + second.getCreatedBy();
    }

    @Override
    public Date getCreationDate() {
        if (first.getCreationDate().after(second.getCreationDate())){
            return first.getCreationDate();
        } else {
            return second.getCreationDate();
        }
    }

    @Override
    public Date getUploadDate() {
        if (first.getUploadDate().after(second.getUploadDate())){
            return first.getUploadDate();
        } else {
            return second.getUploadDate();
        }
    }

    @Override
    public boolean isTransative() {
        return true;
    }
    
    @Override
    public int getId() {
        return Provenance.NO_ID_ASSIGNED;
    }

}
