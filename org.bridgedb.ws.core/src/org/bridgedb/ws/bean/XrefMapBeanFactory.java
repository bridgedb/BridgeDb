package org.bridgedb.ws.bean;

import org.bridgedb.Xref;
import org.bridgedb.linkset.XrefLinkSet;

public class XrefMapBeanFactory {
    public static XrefMapBean asBean(String linkSetId, XrefBean source, String predicate, XrefBean target){
        XrefMapBean bean = new XrefMapBean();
        bean.linkSetId = linkSetId;
        bean.source = source;
        bean.predicate = predicate;
        bean.target = target;
        return bean;
    }

    public static XrefLinkSet asXrefLinkSet(XrefMapBean bean) {
        Xref xref = XrefBeanFactory.asXref(bean.target);
        return new XrefLinkSet(xref, bean.linkSetId, bean.predicate);
    }

    public static XrefMapBean asBean(Xref source, XrefLinkSet target) {
        XrefMapBean bean = new XrefMapBean();
        bean.linkSetId = target.getLinkSetId();
        bean.source = XrefBeanFactory.asBean(source);
        bean.predicate = target.getPredicate();
        bean.target = XrefBeanFactory.asBean(target);
        return bean;
    }

}
