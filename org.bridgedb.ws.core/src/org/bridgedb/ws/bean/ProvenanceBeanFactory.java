package org.bridgedb.ws.bean;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.bridgedb.provenance.ProvenanceLink;
import org.bridgedb.ws.bean.DataSourceBeanFactory;
import org.bridgedb.ws.bean.ProvenanceBean;

/**
 *
 * @author Christian
 */
public class ProvenanceBeanFactory extends ProvenanceBean{

    public static ProvenanceBean asBean(ProvenanceLink provenance) {
        ProvenanceBean bean = new ProvenanceBean();
        bean.id = provenance.getId();
        bean.source = DataSourceBeanFactory.asBean(provenance.getSource());
        bean.predicate = provenance.getPredicate();
        bean.target = DataSourceBeanFactory.asBean(provenance.getTarget());
        //bean.createdBy = provenance.getCreatedBy();
        //bean.creationTime = provenance.getCreation();
        //bean.creationDate = new Date(bean.creationTime);
        //bean.uploadTime = provenance.getUpload();
        //bean.uploadDate = new Date(bean.uploadTime);
        return bean;
    }

    public static ProvenanceLink asProvenance(ProvenanceBean bean){
        //Date fields are ignored as they are only there for Humans
        return new ProvenanceLink(bean.id,
                DataSourceBeanFactory.asDataSource(bean.source),
                bean.predicate,
                DataSourceBeanFactory.asDataSource(bean.target));
    }
        
 }
