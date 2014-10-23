package org.bridgedb.uri.ws.bean;

import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="URISpacesInGraph")
public class URISpacesInGraphBean {
    
    private String graph;
    
    private Set<String> URISpace;
    
    public URISpacesInGraphBean(){
    }

    public URISpacesInGraphBean(String graph, Set<String> URISpaces){
        this.graph = graph;
        this.URISpace = URISpaces;
    }
    
    /**
     * @return the graph
     */
    public String getGraph() {
        return graph;
    }

    /**
     * @param graph the graph to set
     */
    public void setGraph(String Graph) {
        this.graph = Graph;
    }

    /**
     * @return the URISpace
     */
    public Set<String> getURISpace() {
        return URISpace;
    }

    /**
     * @param URISpace the URISpace to set
     */
    public void setURISpace(Set<String> URISpace) {
        this.URISpace = URISpace;
    }

}
