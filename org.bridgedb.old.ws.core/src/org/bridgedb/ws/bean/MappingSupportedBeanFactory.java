/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="FreeSearchSupported")
public class MappingSupportedBeanFactory {
    
    public static MappingSupportedBean asBean(DataSource sourceDataSource, DataSource targetDataSource, boolean supported){
        MappingSupportedBean bean = new MappingSupportedBean();
        bean.source = DataSourceBeanFactory.asBean(sourceDataSource);
        bean.target = DataSourceBeanFactory.asBean(targetDataSource);
        bean.isMappingSupported = supported;
        return bean;
    }
    
}
