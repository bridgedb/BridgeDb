package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="URLSearch")
public class URLSearchBean {
    private List<String> URL;

    private String term;

    public URLSearchBean(){
    }
    
    public URLSearchBean(String term, Set<String> URLs){
        if (URLs == null || URLs.isEmpty()){
            URL = new ArrayList<String>();
        } else {
            URL = new ArrayList(URLs);
        }
        this.term = term;
    }

    /**
     * @return the URL
     */
    public List<String> getURL() {
        return URL;
    }

    /**
     * @param URL the URL to set
     */
    public void setURL(List<String> URL) {
        this.URL = URL;
    }

    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @param term the term to set
     */
    public void setTerm(String term) {
        this.term = term;
    }

    public Set<String> getURLSet() {
        if (URL == null || URL.isEmpty()){
            return new HashSet<String>();
        } else {
            return new HashSet(URL);
        }
    }
 
    
}
