package org.bridgedb.ws.bean;

import org.bridgedb.Xref;

public class XrefMapBeanFactory {
    public static XrefMapBean asBean(XrefBean source, XrefBean target){
        XrefMapBean bean = new XrefMapBean();
        bean.source = source;
        bean.target = target;
        return bean;
    }

    public static XrefMapBean asBean(Xref source, Xref target) {
        XrefMapBean bean = new XrefMapBean();
        bean.source = XrefBeanFactory.asBean(source);
        bean.target = XrefBeanFactory.asBean(target);
        return bean;
    }

}
