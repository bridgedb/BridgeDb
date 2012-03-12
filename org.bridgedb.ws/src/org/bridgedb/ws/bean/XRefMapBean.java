package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

@XmlRootElement(name="XrefMapping")
public class XRefMapBean {
    private XrefBean source;
    //Names of list are singular as they appear in the xml individually
    private List<XrefBean> target;
    
    public XRefMapBean(){}

    public XRefMapBean(XrefBean source, List<XrefBean> target){
        this.source = source;
        this.target = target;
    }

    public XRefMapBean(Xref source, Set<Xref> tgtXrefs){
        this.source = new XrefBean(source);
        this.target = new ArrayList<XrefBean>();
        for (Xref tgt:tgtXrefs){
           this.target.add(new XrefBean(tgt));
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
     * @return the target(s)
     */
    public List<XrefBean> getTarget() {
        return target;
    }

    /**
     * @param target the target(s) to set
     */
    public void setTarget(List<XrefBean> target) {
        this.target = target;
    }

    public Xref getKey() throws IDMapperException {
        return source.asXref();
    }

    public Set<Xref> getMappedSet() throws IDMapperException {
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefBean trg:target){
            results.add(trg.asXref());
        }
        return results;
    }
    
}
