package org.bridgedb.ws.bean;

import org.bridgedb.Xref;
import org.bridgedb.provenance.ProvenanceLink;
import org.bridgedb.provenance.XrefProvenance;

public class XrefProvenanceBeanFactory {
    
    public static XrefProvenanceBean asBean(XrefProvenance xrefProvenance){
        XrefProvenanceBean bean = new XrefProvenanceBean();
        bean.Xref =  XrefBeanFactory.asBean(xrefProvenance);
        bean.provenance = ProvenanceBeanFactory.asBean(xrefProvenance.getProvenanceLink());
        return bean;
    }
    
    public static  XrefProvenance asXrefProvenance(XrefProvenanceBean bean) {
        Xref xref = XrefBeanFactory.asXref(bean.Xref);
        ProvenanceLink provenanceLink = ProvenanceBeanFactory.asProvenance(bean.provenance);
        return new XrefProvenance(xref, provenanceLink);
    }

    public static Xref asXref(XrefProvenanceBean bean) {
        return XrefBeanFactory.asXref(bean.Xref);
    }

}
