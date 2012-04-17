/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian
 */
@XmlRootElement(name="URLsList")
public class URLsBean {
    private List<String> url;

    public URLsBean(){
    }
    
    public URLsBean(String url){
        this.url = new ArrayList<String>();
        this.url.add(url);
    }
   
    public URLsBean(Set<String> urls){
        this.url = new ArrayList<String>(urls);
    }

    /**
     * @return the url(s) as a List
     */
    public List<String> getUrl() {
        return url;
    }

    /**
     * @return the url(s) as a set
     */
    public Set<String> getUrlSet() {
        return new HashSet(url);
    }

    /**
     * @param url the url to set
     */
    public void setUrl(List<String> url) {
        this.url = url;
    }

}
