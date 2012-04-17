/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.statistics.ProvenanceStatistics;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="ProvenanceStatistics")
public class ProvenanceStatisticsBean {
    private ProvenanceBean provenance;
    private Integer numberOfMappings;
    
    public ProvenanceStatisticsBean(){
        
    }

    public ProvenanceStatisticsBean(ProvenanceStatistics provenanceStatistics){
        if (provenanceStatistics != null){
            this.provenance = ProvenanceBeanFactory.asBean(provenanceStatistics.getProvenanceLink());
            this.numberOfMappings = provenanceStatistics.getNumberOfMappings();
        } else{
            this.provenance = new ProvenanceBean();
        }
    }

    public ProvenanceStatistics asProvenanceStatistics() {
        return new ProvenanceStatistics(ProvenanceBeanFactory.asProvenance(provenance), this.numberOfMappings);
    }

    /**
     * @return the provenance
     */
    public ProvenanceBean getProvenance() {
        return provenance;
    }

    /**
     * @param provenance the provenance to set
     */
    public void setProvenance(ProvenanceBean provenance) {
        this.provenance = provenance;
    }

    /**
     * @return the numberOfMappings
     */
    public Integer getNumberOfMappings() {
        return numberOfMappings;
    }

    /**
     * @param numberOfMappings the numberOfMappings to set
     */
    public void setNumberOfMappings(Integer numberOfMappings) {
        this.numberOfMappings = numberOfMappings;
    }
    
    public String toString(){
        return "Found " + this.numberOfMappings + " mappings for " + this.provenance;
    }
}
