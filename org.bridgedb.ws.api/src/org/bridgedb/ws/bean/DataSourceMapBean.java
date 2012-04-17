package org.bridgedb.ws.bean;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DataSourceMapping")
public class DataSourceMapBean {
    DataSourceBean source;
    //Names of list are singular as they appear in the xml individually
    List<DataSourceBean> target;
    
    public DataSourceMapBean(){}

    /**
     * @return the source
     */
    public DataSourceBean getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(DataSourceBean source) {
        this.source = source;
    }

    /**
     * @return the target(s)
     */
    public List<DataSourceBean> getTarget() {
        return target;
    }

    /**
     * @param targets the targets to set
     */
    public void setTarget(List<DataSourceBean> target) {
        this.target = target;
    }
    
}
