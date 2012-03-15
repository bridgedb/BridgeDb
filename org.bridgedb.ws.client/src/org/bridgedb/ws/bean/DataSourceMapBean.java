package org.bridgedb.ws.bean;

import org.bridgedb.IDMapperException;
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
    //Names of list are singular as they appear in the xml individually
    private List<DataSourceBean> target;
    
    public DataSourceMapBean(){}

    public DataSourceMapBean(DataSourceBean source, List<DataSourceBean> targets){
        this.source = source;
        this.target = targets;
    }

    public DataSourceMapBean(DataSource source, Set<DataSource> tgtDataSource){
        this.source = new DataSourceBean(source);
        this.target = new ArrayList<DataSourceBean>();
        for (DataSource tgt:tgtDataSource){
           this.target.add(new DataSourceBean(tgt));
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

    DataSource getKey() throws IDMapperException {
        return source.asDataSource();
    }

    Set<DataSource> getMappedSet() throws IDMapperException {
        HashSet<DataSource> results = new HashSet<DataSource>();
        for (DataSourceBean trg:target){
            results.add(trg.asDataSource());
        }
        return results;
    }
    
}
