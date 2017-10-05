
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
 *         &lt;element name="sequence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="searchDatabases" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="identityValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identityTaxon" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="filterType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="blastDB" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taxonId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="onlyActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="blastParameters" type="{http://model.picr.ebi.ac.uk}BlastParameter"/>
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
    "sequence",
    "searchDatabases",
    "identityValue",
    "identityTaxon",
    "filterType",
    "blastDB",
    "taxonId",
    "onlyActive",
    "blastParameters"
})
@XmlRootElement(name = "getUPIForBlastSequence")
public class GetUPIForBlastSequence {

    @XmlElement(required = true)
    protected String sequence;
    @XmlElement(required = true)
    protected List<String> searchDatabases;
    @XmlElement(required = true, nillable = true)
    protected String identityValue;
    @XmlElement(required = true, nillable = true)
    protected String identityTaxon;
    @XmlElement(required = true)
    protected String filterType;
    @XmlElement(required = true)
    protected String blastDB;
    @XmlElement(required = true)
    protected String taxonId;
    protected boolean onlyActive;
    @XmlElement(required = true, nillable = true)
    protected BlastParameter blastParameters;

    /**
     * Gets the value of the sequence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequence(String value) {
        this.sequence = value;
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
     * Gets the value of the identityValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentityValue() {
        return identityValue;
    }

    /**
     * Sets the value of the identityValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentityValue(String value) {
        this.identityValue = value;
    }

    /**
     * Gets the value of the identityTaxon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentityTaxon() {
        return identityTaxon;
    }

    /**
     * Sets the value of the identityTaxon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentityTaxon(String value) {
        this.identityTaxon = value;
    }

    /**
     * Gets the value of the filterType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilterType() {
        return filterType;
    }

    /**
     * Sets the value of the filterType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilterType(String value) {
        this.filterType = value;
    }

    /**
     * Gets the value of the blastDB property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBlastDB() {
        return blastDB;
    }

    /**
     * Sets the value of the blastDB property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBlastDB(String value) {
        this.blastDB = value;
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

    /**
     * Gets the value of the blastParameters property.
     * 
     * @return
     *     possible object is
     *     {@link BlastParameter }
     *     
     */
    public BlastParameter getBlastParameters() {
        return blastParameters;
    }

    /**
     * Sets the value of the blastParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link BlastParameter }
     *     
     */
    public void setBlastParameters(BlastParameter value) {
        this.blastParameters = value;
    }

}
