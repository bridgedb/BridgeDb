package org.bridgedb.ws.bean;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.bridgedb.DataSource;
import org.bridgedb.DataSource.Builder;
import org.bridgedb.IDMapperException;

//TODO: Hide the linkset ID but leave it available for internal use but have URI available for external display
//@XmlTransient is not working :(
@XmlRootElement(name="DataSource")
public class DataSourceBean {
  	private String sysCode = null;
	private String fullName = null;
    private String urlPattern = null;
	private String idExample = null;
	private boolean isPrimary = true;
	private String type = "unknown";
    //I wonder how to do this?
	private Object organism = null;
	private String urnBase = null;    
	private String mainUrl = null;

    //Webservice constructor
    public DataSourceBean(){
    }
    
    public DataSourceBean(DataSource dataSource){
        this.sysCode = dataSource.getSystemCode();
        this.fullName = dataSource.getFullName();
        String urlPattern = dataSource.getUrl("$id");
        if (urlPattern.length() > 3 ){
            this.urlPattern = urlPattern;
        } else {
            this.urlPattern = null;
        }
        this.idExample = dataSource.getExample().getId();
        this.isPrimary = dataSource.isPrimary();
        this.type = dataSource.getType();
    	this.organism = dataSource.getOrganism();
        String emptyUrn = dataSource.getURN("");
        if (emptyUrn.length() > 1){
            this.urnBase = emptyUrn.substring(0, emptyUrn.length()-1);    
        } else {
            this.urnBase = null;
        }
        this.mainUrl = dataSource.getMainUrl();      
    }
    
    /**
     * @return the ds
     */
    public DataSource asDataSource() throws IDMapperException {
        Builder builder = DataSource.register(sysCode, fullName);
        if (urlPattern != null){
            builder = builder.urlPattern(urlPattern);
        }
        if (idExample != null){
            builder = builder.idExample(idExample);
        }
        builder = builder.primary(isPrimary);
        builder = builder.type(type);
        if (organism != null){
            builder = builder.organism(organism);
        }
        if (urnBase != null){
            builder = builder.urnBase(type);
        }
        if (mainUrl != null){
            builder = builder.mainUrl(mainUrl);
        }
        return builder.asDataSource();
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
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the urlPattern
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * @param urlPattern the urlPattern to set
     */
    public void setUrlPattern(String urlPattern) {
        if (urlPattern != null && urlPattern.length() <= 3){
            this.urlPattern = "STRANGE:" + urlPattern;
        } else {
            this.urlPattern = urlPattern;
        }
    }

    /**
     * @return the idExample
     */
    public String getIdExample() {
        return idExample;
    }

    /**
     * @param idExample the idExample to set
     */
    public void setIdExample(String idExample) {
        this.idExample = idExample;
    }

    /**
     * @return the isPrimary
     */
    public boolean isIsPrimary() {
        return isPrimary;
    }

    /**
     * @param isPrimary the isPrimary to set
     */
    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the organism
     */
    public Object getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(Object organism) {
        this.organism = organism;
    }

    /**
     * @return the urnBase
     */
    public String getUrnBase() {
        return urnBase;
    }

    /**
     * @param urnBase the urnBase to set
     */
    public void setUrnBase(String urnBase) {
        this.urnBase = urnBase;
    }

    /**
     * @return the mainUrl
     */
    public String getMainUrl() {
        return mainUrl;
    }

    /**
     * @param mainUrl the mainUrl to set
     */
    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    
}
