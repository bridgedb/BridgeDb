package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

@XmlRootElement(name="Xref")
public class XrefBean {
    private String id;
    private DataSourceBean dataSource;
    
    public XrefBean(){}
    
    public XrefBean(Xref xref){
        this.id = xref.getId();
        this.dataSource = new DataSourceBean(xref.getDataSource());
    }
    
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

    public Xref asXref() throws IDMapperException {
        DataSource ds = dataSource.asDataSource();
        return new Xref(id, ds);
    }
}
