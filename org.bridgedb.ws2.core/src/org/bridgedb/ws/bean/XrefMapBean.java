package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="XrefMapping")
public class XrefMapBean {
    XrefBean source;
    XrefBean target;
    
    public XrefMapBean(){}

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
        return source + " -> " + target;
    }
}
