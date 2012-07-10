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
@XmlRootElement(name="URLSpaces")
public class UriSpacesBean {
   private String sysCode;
   private List<String> UriSpace;

   /** 
    * WS Constructor
    */
   public UriSpacesBean(){
   }
   
   public UriSpacesBean(String sysCode, Set<String> UriSpace){
       this.sysCode = sysCode;
       this.UriSpace = new ArrayList<String>(UriSpace);
   } 

   /**
     * @return the sysCode
     */
    public String getSysCode() {
        return sysCode;
    }

    /**
     * @param sysCode the sysCode to set
     */
    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    /**
     * @return the UriSpace
     */
    public List<String> getUriSpace() {
        return UriSpace;
    }

    /**
     * @return the UriSpace
     */
    public Set<String> getUriSpaceSet() {
        return new HashSet(UriSpace);
    }

    /**
     * @param UriSpace the UriSpace to set
     */
    public void setUriSpace(List<String> UriSpace) {
        this.UriSpace = UriSpace;
    }
   
}
