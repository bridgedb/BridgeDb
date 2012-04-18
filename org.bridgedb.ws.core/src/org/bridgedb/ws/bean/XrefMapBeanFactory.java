package org.bridgedb.ws.bean;

import org.bridgedb.Xref;
import org.bridgedb.provenance.XrefProvenance;

public class XrefMapBeanFactory {
    public static XrefMapBean asBean(String provenanceId, XrefBean source, String predicate, XrefBean target){
        XrefMapBean bean = new XrefMapBean();
        bean.provenanceId = provenanceId;
        bean.source = source;
        bean.predicate = predicate;
        bean.target = target;
        return bean;
    }

    public static XrefProvenance asXrefProvenance(XrefMapBean bean) {
        Xref xref = XrefBeanFactory.asXref(bean.target);
        return new XrefProvenance(xref, bean.provenanceId, bean.predicate);
    }

    public static XrefMapBean asBean(Xref source, XrefProvenance target) {
        XrefMapBean bean = new XrefMapBean();
        bean.provenanceId = target.getProvenanceId();
        bean.source = XrefBeanFactory.asBean(source);
        bean.predicate = target.getPredicate();
        bean.target = XrefBeanFactory.asBean(target);
        return bean;
    }

}
