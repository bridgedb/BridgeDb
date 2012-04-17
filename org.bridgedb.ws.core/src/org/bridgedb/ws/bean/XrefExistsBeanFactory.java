package org.bridgedb.ws.bean;

import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public class XrefExistsBeanFactory {
    
    public static XrefExistsBean asBean(Xref xref, boolean exists){
        XrefExistsBean bean = new XrefExistsBean();
        bean.exists = exists;
        bean.Xref = XrefBeanFactory.asBean(xref);
        return bean;
    }
    
}
