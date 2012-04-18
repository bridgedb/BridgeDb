package org.bridgedb.ws.bean;
import org.bridgedb.result.URLMapping;

/**
 *
 * @author Christian
 */
public class URLMappingBeanFactory extends URLMappingBean{
    
   public static URLMappingBean asBean(URLMapping mapping, boolean full){
        URLMappingBean bean = new URLMappingBean();
        if (mapping.isValid()) {           
            bean.id = mapping.getId();
            bean.sourceURL = mapping.getSourceURL();
            bean.targetURL = mapping.getTargetURL();
            //if (full){
            //    bean.provenance =  ProvenanceBeanFactory.asBean(mapping.getProvenanceLink());
            //} else {
                bean.provenanceId = mapping.getProvenanceId();
                bean.predicate = mapping.getPredicate();
            //}
        } else {
            bean.Error = mapping.getErrorMessage();
        }
        return bean;
    }

    public static URLMappingBean asBean(String errorMessage){
        URLMappingBean bean = new URLMappingBean();
        bean.Error = errorMessage;
        return bean;
    }
    
    public static URLMapping asURLMapping(URLMappingBean bean){
        if (bean.Error != null && !bean.Error.isEmpty()){
            return new URLMapping(bean.Error);
        }
        return new URLMapping(bean.getId(), bean.getSourceURL(), bean.getTargetURL(), bean.provenanceId, bean.predicate);
    }
         
    
}
