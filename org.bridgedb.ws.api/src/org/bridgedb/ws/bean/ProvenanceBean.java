package org.bridgedb.ws.bean;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="ProvenanceBean")
@XmlType(propOrder={"id","source", "predicate", "target", "createdBy", "creationDate", "uploadDate", "creationTime", "uploadTime"})
public class ProvenanceBean {
    DataSourceBean source;
    String predicate;
    DataSourceBean target;
    String createdBy;  //currently unused
    //Adding a Date for human readability
    Date creationDate;  //currently unused
    Long creationTime;  //currently unused
    Long uploadTime;  //currently unused
    //Adding a Date for human readability
    Date uploadDate;  //currently unused
    String id;

    //Webservice constructor
    public ProvenanceBean(){
    }
    
    public String toString(){
        return this.getSource().getUrlPattern() + " " + this.getPredicate() + " " + this.getTarget().getUrlPattern() + " " +  
                this.getCreatedBy() + " " + this.getCreationTime() + " " + this.getUploadTime();
        
    }

    /**
     * @return the source
     */
    public DataSourceBean getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(DataSourceBean source) {
        this.source = source;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @param predicate the predicate to set
     */
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    /**
     * @return the target
     */
    public DataSourceBean getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(DataSourceBean target) {
        this.target = target;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the creationTime
     */
    public Long getCreationTime() {
        return creationTime;
    }

    /**
     * @param creationTime the creationTime to set
     */
    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * @return the uploadTime
     */
    public Long getUploadTime() {
        return uploadTime;
    }

    /**
     * @param uploadTime the uploadTime to set
     */
    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * @return the uploadDate
     */
    public Date getUploadDate() {
        return uploadDate;
    }

    /**
     * @param uploadDate the uploadDate to set
     */
    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
