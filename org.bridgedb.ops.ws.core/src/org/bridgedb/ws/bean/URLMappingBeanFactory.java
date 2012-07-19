/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import org.bridgedb.url.URLMapping;

/**
 *
 * @author Christian
 */
public class URLMappingBeanFactory {
    
    public static URLMapping asURLMapping(URLMappingBean bean){
        return new URLMapping (bean.getId(), bean.getSourceURL(), bean.getPredicate(), bean.getTargetURL(), 
                bean.getMappingSetId());      
    }

    public static URLMappingBean asBean(URLMapping urlMapping) {
        return new URLMappingBean (urlMapping.getId(), urlMapping.getSourceURLs(), urlMapping.getPredicate(), 
                urlMapping.getTargetURLs(), urlMapping.getMappingSetId());              
    }
}
