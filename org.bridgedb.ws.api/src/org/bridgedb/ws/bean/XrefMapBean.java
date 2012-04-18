package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="XrefMapping")
public class XrefMapBean {
    String provenanceId;
    XrefBean source;
    String predicate;
    XrefBean target;
    
    public XrefMapBean(){}

    /**
     * @return the provenanceId
     */
    public String getProvenanceId() {
        return provenanceId;
    }

    /**
     * @param provenanceId the provenanceId to set
     */
    public void setProvenanceId(String provenanceId) {
        this.provenanceId = provenanceId;
    }

    /**
     * @return the source
     */
    public XrefBean getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(XrefBean source) {
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
    public XrefBean getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(XrefBean target) {
        this.target = target;
    }

    public String toString(){
        return provenanceId + " " + source + " " + predicate + " " + target;
    }
}
