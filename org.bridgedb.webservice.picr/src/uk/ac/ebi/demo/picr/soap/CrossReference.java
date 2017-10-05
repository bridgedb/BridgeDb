
package uk.ac.ebi.demo.picr.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CrossReference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CrossReference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accession" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accessionVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="databaseDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="databaseName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateAdded" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dateDeleted" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="deleted" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="gi" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taxonId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CrossReference", namespace = "http://model.picr.ebi.ac.uk", propOrder = {
    "accession",
    "accessionVersion",
    "databaseDescription",
    "databaseName",
    "dateAdded",
    "dateDeleted",
    "deleted",
    "gi",
    "taxonId"
})
public class CrossReference {

    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String accession;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String accessionVersion;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String databaseDescription;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String databaseName;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected XMLGregorianCalendar dateAdded;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected XMLGregorianCalendar dateDeleted;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk")
    protected boolean deleted;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String gi;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String taxonId;

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
     * Gets the value of the accessionVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessionVersion() {
        return accessionVersion;
    }

    /**
     * Sets the value of the accessionVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessionVersion(String value) {
        this.accessionVersion = value;
    }

    /**
     * Gets the value of the databaseDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatabaseDescription() {
        return databaseDescription;
    }

    /**
     * Sets the value of the databaseDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatabaseDescription(String value) {
        this.databaseDescription = value;
    }

    /**
     * Gets the value of the databaseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets the value of the databaseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatabaseName(String value) {
        this.databaseName = value;
    }

    /**
     * Gets the value of the dateAdded property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateAdded() {
        return dateAdded;
    }

    /**
     * Sets the value of the dateAdded property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateAdded(XMLGregorianCalendar value) {
        this.dateAdded = value;
    }

    /**
     * Gets the value of the dateDeleted property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateDeleted() {
        return dateDeleted;
    }

    /**
     * Sets the value of the dateDeleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateDeleted(XMLGregorianCalendar value) {
        this.dateDeleted = value;
    }

    /**
     * Gets the value of the deleted property.
     * 
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the value of the deleted property.
     * 
     */
    public void setDeleted(boolean value) {
        this.deleted = value;
    }

    /**
     * Gets the value of the gi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGi() {
        return gi;
    }

    /**
     * Sets the value of the gi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGi(String value) {
        this.gi = value;
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

}
