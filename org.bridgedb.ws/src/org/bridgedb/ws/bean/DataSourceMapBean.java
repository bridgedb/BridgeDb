package org.bridgedb.ws.bean;

import org.bridgedb.ws.bean.DataSourceBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.bridgedb.DataSource;

@XmlRootElement(name="DataSourceMapping")
public class DataSourceMapBean {
    private DataSourceBean source;
    private List<DataSourceBean> targets;
    
    public DataSourceMapBean(){}

    public DataSourceMapBean(DataSourceBean source, List<DataSourceBean> targets){
        this.source = source;
        this.targets = targets;
    }

    public DataSourceMapBean(DataSource source, Set<DataSource> tgtDataSource){
        this.source = new DataSourceBean(source);
        this.targets = new ArrayList<DataSourceBean>();
        for (DataSource tgt:tgtDataSource){
           this.targets.add(new DataSourceBean(tgt));
        }
    }

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
     * @return the targets
     */
    public List<DataSourceBean> getTargets() {
        return targets;
    }

    /**
     * @param targets the targets to set
     */
    public void setTargets(List<DataSourceBean> targets) {
        this.targets = targets;
    }

    DataSource getKey() {
        return source.asDataSource();
    }

    Set<DataSource> getMappedSet() {
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean target:targets){
            results.add(target.asDataSource());
        }
        return results;
    }
    
}
