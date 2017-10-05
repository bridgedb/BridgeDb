
package uk.ac.ebi.demo.picr.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accession" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ac_version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="searchDatabases" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="taxonId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="onlyActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "accession",
    "acVersion",
    "searchDatabases",
    "taxonId",
    "onlyActive"
})
@XmlRootElement(name = "getUPIForAccession")
public class GetUPIForAccession {

    @XmlElement(required = true)
    protected String accession;
    @XmlElement(name = "ac_version", required = true)
    protected String acVersion;
    @XmlElement(required = true)
    protected List<String> searchDatabases;
    @XmlElement(required = true)
    protected String taxonId;
    protected boolean onlyActive;

    /**
     * Gets the value of the accession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Sets the value of the accession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccession(String value) {
        this.accession = value;
    }

    /**
     * Gets the value of the acVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcVersion() {
        return acVersion;
    }

    /**
     * Sets the value of the acVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcVersion(String value) {
        this.acVersion = value;
    }

    /**
     * Gets the value of the searchDatabases property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchDatabases property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchDatabases().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSearchDatabases() {
        if (searchDatabases == null) {
            searchDatabases = new ArrayList<String>();
        }
        return this.searchDatabases;
    }

    /**
     * Gets the value of the taxonId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxonId() {
        return taxonId;
    }

    /**
     * Sets the value of the taxonId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxonId(String value) {
        this.taxonId = value;
    }

    /**
     * Gets the value of the onlyActive property.
     * 
     */
    public boolean isOnlyActive() {
        return onlyActive;
    }

    /**
     * Sets the value of the onlyActive property.
     * 
     */
    public void setOnlyActive(boolean value) {
        this.onlyActive = value;
    }

}
