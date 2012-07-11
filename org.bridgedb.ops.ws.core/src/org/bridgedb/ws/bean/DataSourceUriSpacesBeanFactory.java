package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.DataSource.Builder;

public class DataSourceUriSpacesBeanFactory{

    //Webservice constructor
    public DataSourceUriSpacesBeanFactory(){
    }
    
    public static DataSourceUriSpacesBean asBean(DataSource dataSource, Set<String> uriSpaces){
        DataSourceUriSpacesBean bean = new DataSourceUriSpacesBean();
        bean.setSysCode(dataSource.getSystemCode());
        bean.setFullName(dataSource.getFullName());
        String urlPattern = dataSource.getUrl("$id");
        if (urlPattern.length() > 3 ){
            bean.setUrlPattern(urlPattern);
        } else {
            bean.setUrlPattern(null);
        }
        bean.setIdExample(dataSource.getExample().getId());
        bean.setIsPrimary(dataSource.isPrimary());
        bean.setType(dataSource.getType());
    	bean.setOrganism(dataSource.getOrganism());
        String emptyUrn = dataSource.getURN("");
        if (emptyUrn.length() > 1){
            bean.setUrnBase(emptyUrn.substring(0, emptyUrn.length()-1));    
        } else {
            bean.setUrnBase(null);
        }
        bean.setMainUrl(dataSource.getMainUrl()); 
        List<UriSpaceBean> beans = new ArrayList<UriSpaceBean>();
        for (String uriSpace: uriSpaces){
            beans.add(new UriSpaceBean(uriSpace));
        }
        bean.setUriSpace(beans);
        return bean;
    }
    
}
