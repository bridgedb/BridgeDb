package org.bridgedb.ws.bean;

import org.bridgedb.DataSource;
import org.bridgedb.DataSource.Builder;

public class DataSourceBeanFactory{

    //Webservice constructor
    public DataSourceBeanFactory(){
    }
    
    public static DataSourceBean asBean(DataSource dataSource){
        DataSourceBean bean = new DataSourceBean();
        bean.sysCode = dataSource.getSystemCode();
        bean.fullName = dataSource.getFullName();
        String urlPattern = dataSource.getUrl("$id");
        if (urlPattern.length() > 3 ){
            bean.urlPattern = urlPattern;
        } else {
            bean.urlPattern = null;
        }
        bean.idExample = dataSource.getExample().getId();
        bean.isPrimary = dataSource.isPrimary();
        bean.type = dataSource.getType();
    	bean.organism = dataSource.getOrganism();
        String emptyUrn = dataSource.getURN("");
        if (emptyUrn.length() > 1){
            bean.urnBase = emptyUrn.substring(0, emptyUrn.length()-1);    
        } else {
            bean.urnBase = null;
        }
        bean.mainUrl = dataSource.getMainUrl(); 
        return bean;
    }
    
    /**
     * @return the ds
    */
    public static DataSource asDataSource(DataSourceBean bean) {
        bean.toString();
        Builder builder = DataSource.register(bean.sysCode, bean.fullName);
        if (bean.urlPattern != null){
            builder = builder.urlPattern(bean.urlPattern);
        }
        if (bean.idExample != null){
            builder = builder.idExample(bean.idExample);
        }
        builder = builder.primary(bean.isPrimary);
        builder = builder.type(bean.type);
        if (bean.organism != null){
            builder = builder.organism(bean.organism);
        }
        if (bean.urnBase != null){
            builder = builder.urnBase(bean.type);
        }
        if (bean.mainUrl != null){
            builder = builder.mainUrl(bean.mainUrl);
        }
        return builder.asDataSource();
    }

}
