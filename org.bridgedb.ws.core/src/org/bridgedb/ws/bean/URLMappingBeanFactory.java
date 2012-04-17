package org.bridgedb.ws.bean;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.provenance.ProvenanceLink;
import org.bridgedb.result.URLMapping;
import org.bridgedb.ws.bean.ProvenanceBeanFactory;
import org.bridgedb.ws.bean.URLMappingBean;

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
            if (full){
                bean.provenance =  ProvenanceBeanFactory.asBean(mapping.getProvenanceLink());
            } else {
                bean.provenanceId = mapping.getProvenanceLink().getId();
                bean.predicate = mapping.getProvenanceLink().getPredicate();
            }
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
        if (bean.getProvenance() == null){
            try {
                String sourceNameSpace = DataSource.uriToXref(bean.sourceURL).getDataSource().getNameSpace();
                String targetnameSpace = DataSource.uriToXref(bean.targetURL).getDataSource().getNameSpace();
                ProvenanceLink link = 
                        new ProvenanceLink(bean.getProvenanceId(), sourceNameSpace, bean.predicate, targetnameSpace);
                return new URLMapping(bean.getId(), bean.getSourceURL(), bean.getTargetURL(), link);
            } catch (IDMapperException ex) {
                return new URLMapping(ex);
            }
        }
        return new URLMapping(bean.getId(), bean.getSourceURL(), bean.getTargetURL(), 
                ProvenanceBeanFactory.asProvenance(bean.provenance));
    }
         
    
}
