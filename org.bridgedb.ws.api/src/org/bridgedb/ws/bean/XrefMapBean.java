package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="XrefMapping")
public class XrefMapBean {
    XrefBean source;
    //Names of list are singular as they appear in the xml individually
    List<XrefProvenanceBean> target;
    
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
     * @return the target(s)
     */
    public List<XrefProvenanceBean> getTarget() {
        return target;
    }

    /**
     * @param target the target(s) to set
     */
    public void setTarget(List<XrefProvenanceBean> target) {
        this.target = target;
    }
    
}
