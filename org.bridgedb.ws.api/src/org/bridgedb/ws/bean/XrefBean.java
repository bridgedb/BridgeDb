package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Xref")
public class XrefBean {
    String id;
    DataSourceBean dataSource;
    
    public XrefBean(){}
        
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public DataSourceBean getDataSource(){
        return dataSource;
    }
    
    public void setDataSource(DataSourceBean dataSource){
        this.dataSource = dataSource;
    }

}
