package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="XrefMapping")
public class XrefMapBean {
    String linkSetId;
    XrefBean source;
    String predicate;
    XrefBean target;
    
    public XrefMapBean(){}

    /**
     * @return the linkSetId
     */
    public String getLinkSetId() {
        return linkSetId;
    }

    /**
     * @param linkSetId the linkSetId to set
     */
    public void setLinkSetId(String linkSetId) {
        this.linkSetId = linkSetId;
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
        return linkSetId + " " + source + " " + predicate + " " + target;
    }
}
