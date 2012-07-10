package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="MappingSetInfo")
public class MappingSetInfoBean {
    private String id;
    private String sourceSysCode;
    private String predicate;
    private String targetSysCode;
    private Integer numberOfLinks;
    private boolean isTransitive;

    /**
     * WS Constructor
     */
    public MappingSetInfoBean(){
    }
    
    public MappingSetInfoBean(String id, String sourceSysCode, String predicate, String targetSysCode, 
            Integer numberOfLinks, boolean isTransitive){
        this.id = id;
        this.sourceSysCode = sourceSysCode;
        this.predicate = predicate;
        this.targetSysCode = targetSysCode;
        this.numberOfLinks = numberOfLinks;
        this.isTransitive = isTransitive;
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

    /**
     * @return the sourceSysCode
     */
    public String getSourceSysCode() {
        return sourceSysCode;
    }

    /**
     * @param sourceSysCode the sourceSysCode to set
     */
    public void setSourceSysCode(String sourceSysCode) {
        this.sourceSysCode = sourceSysCode;
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
     * @return the targetSysCode
     */
    public String getTargetSysCode() {
        return targetSysCode;
    }

    /**
     * @param targetSysCode the targetSysCode to set
     */
    public void setTargetSysCode(String targetSysCode) {
        this.targetSysCode = targetSysCode;
    }

    /**
     * @return the numberOfLinks
     */
    public Integer getNumberOfLinks() {
        return numberOfLinks;
    }

    /**
     * @param numberOfLinks the numberOfLinks to set
     */
    public void setNumberOfLinks(Integer numberOfLinks) {
        this.numberOfLinks = numberOfLinks;
    }

    /**
     * @return the isTransitive
     */
    public boolean isIsTransitive() {
        return isTransitive;
    }

    /**
     * @param isTransitive the isTransitive to set
     */
    public void setIsTransitive(boolean isTransitive) {
        this.isTransitive = isTransitive;
    }
    
}
