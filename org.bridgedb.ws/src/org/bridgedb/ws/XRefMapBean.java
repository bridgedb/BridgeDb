package org.bridgedb.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.Xref;

@XmlRootElement(name="XrefMapping")
public class XRefMapBean {
    private XrefBean source;
    private List<XrefBean> targets;
    
    public XRefMapBean(){}

    public XRefMapBean(XrefBean source, List<XrefBean> targets){
        this.source = source;
        this.targets = targets;
    }

    public XRefMapBean(Xref source, Set<Xref> tgtXrefs){
        this.source = new XrefBean(source);
        this.targets = new ArrayList<XrefBean>();
        for (Xref tgt:tgtXrefs){
           this.targets.add(new XrefBean(tgt));
        }
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
     * @return the targets
     */
    public List<XrefBean> getTargets() {
        return targets;
    }

    /**
     * @param targets the targets to set
     */
    public void setTargets(List<XrefBean> targets) {
        this.targets = targets;
    }
    
}
