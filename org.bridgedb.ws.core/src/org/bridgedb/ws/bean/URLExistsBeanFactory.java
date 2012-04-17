package org.bridgedb.ws.bean;

import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public class URLExistsBeanFactory {
    
    public static URLExistsBean asBean(String URL, boolean exists){
        URLExistsBean bean = new URLExistsBean();
        bean.exists = exists;
        bean.URL = URL;
        return bean;
    }
    
}
