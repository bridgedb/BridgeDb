/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import org.bridgedb.DataSource;
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
    TransativeProvenace(Provenance first, Provenance second) throws ProvenanceException{
        if (first.getTarget().equals(second.getTarget())){
            this.first = first;
            this.second = second;
        } else {
            throw new ProvenanceException("Tagrget of " + first + " does not match the source of " + second);
        }
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
    public long getCreation() {
        if (first.getCreation() > second.getCreation()){
            return first.getCreation();
        } else {
            return second.getCreation();
        }
    }

    @Override
    public long getUpload() {
        if (first.getUpload()> second.getUpload()){
            return first.getUpload();
        } else {
            return second.getUpload();
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

    @Override
    public DataSource getSource() {
        return first.getSource();
    }

    @Override
    public DataSource getTarget() {
        return second.getTarget();
    }

}
