package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="XrefMapping")
public class XrefProvenanceBean {
    XrefBean Xref;
    ProvenanceBean provenance;
    
    public XrefProvenanceBean(){}

    /**
     * @return the Xref
     */
    public XrefBean getXref() {
        return Xref;
    }

    /**
     * @param Xref the Xref to set
     */
    public void setXref(XrefBean Xref) {
        this.Xref = Xref;
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

    
}
