package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="URLMapping")
public class URLMapBean {
    private String source;
    //Names of list are singular as they appear in the xml individually
    private List<String> target;
    
    public URLMapBean(){}

    public URLMapBean(String source, List<String> target){
        this.source = source;
        this.target = target;
    }

    public URLMapBean(String source, Set<String> target){
        this(source, new ArrayList(target));
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the target(s)
     */
    public List<String> getTarget() {
        return target;
    }

    /**
     * @return the target(s)
     */
    public Set<String> getTargetsSet() {
        if (target == null || target.isEmpty()){
            return new HashSet<String>();
        }
        return new HashSet(target);
    }

    /**
     * @param target the target(s) to set
     */
    public void setTarget(List<String> target) {
        this.target = target;
    }

}
