package org.bridgedb.ws;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;

//TODO: Hide the linkset ID but leave it available for internal use but have URI available for external display
//@XmlTransient is not working :(
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
}
