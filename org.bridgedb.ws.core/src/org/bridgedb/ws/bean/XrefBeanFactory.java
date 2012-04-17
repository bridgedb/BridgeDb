package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.provenance.XrefProvenance;

public class XrefBeanFactory {
    
    public static XrefBean asBean(Xref xref){
        XrefBean bean = new XrefBean();
        bean.id = xref.getId();
        bean.dataSource = DataSourceBeanFactory.asBean(xref.getDataSource());
        return bean;
    }
    
    public static Xref asXref(XrefBean bean) {
        DataSource ds = DataSourceBeanFactory.asDataSource(bean.dataSource);
        return new Xref(bean.id, ds);
    }

}
