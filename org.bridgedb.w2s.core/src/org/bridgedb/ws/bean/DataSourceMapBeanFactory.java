package org.bridgedb.ws.bean;

import org.bridgedb.IDMapperException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;

@XmlRootElement(name="DataSourceMapping")
public class DataSourceMapBeanFactory {

    public static DataSourceMapBean asBean(DataSourceBean source, List<DataSourceBean> targets){
        DataSourceMapBean bean = new DataSourceMapBean();
        bean.source = source;
        bean.target = targets;
        return bean;
    }

    public static DataSourceMapBean asBean(DataSource source, Set<DataSource> tgtDataSource){
        DataSourceMapBean bean = new DataSourceMapBean();
        bean.source = DataSourceBeanFactory.asBean(source);
        bean.target = new ArrayList<DataSourceBean>();
        for (DataSource tgt:tgtDataSource){
           bean.target.add(DataSourceBeanFactory.asBean(tgt));
        }
        return bean;
    }

    public static DataSource getKey(DataSourceMapBean bean) throws IDMapperException {
        return DataSourceBeanFactory.asDataSource(bean.source);
    }

    public static Set<DataSource> getMappedSet(DataSourceMapBean bean) throws IDMapperException {
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean trg:bean.target){
            results.add(DataSourceBeanFactory.asDataSource(trg));
        }
        return results;
    }
    
}
