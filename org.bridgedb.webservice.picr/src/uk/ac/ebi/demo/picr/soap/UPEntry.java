
package uk.ac.ebi.demo.picr.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for UPEntry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UPEntry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CRC64" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="UPI" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identicalCrossReferences" type="{http://model.picr.ebi.ac.uk}CrossReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="logicalCrossReferences" type="{http://model.picr.ebi.ac.uk}CrossReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sequence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UPEntry", namespace = "http://model.picr.ebi.ac.uk", propOrder = {
    "crc64",
    "upi",
    "identicalCrossReferences",
    "logicalCrossReferences",
    "sequence",
    "timestamp"
})
public class UPEntry {

    @XmlElement(name = "CRC64", namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String crc64;
    @XmlElement(name = "UPI", namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String upi;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", nillable = true)
    protected List<CrossReference> identicalCrossReferences;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", nillable = true)
    protected List<CrossReference> logicalCrossReferences;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String sequence;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected XMLGregorianCalendar timestamp;

    /**
     * Gets the value of the crc64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCRC64() {
        return crc64;
    }

    /**
     * Sets the value of the crc64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCRC64(String value) {
        this.crc64 = value;
    }

    /**
     * Gets the value of the upi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUPI() {
        return upi;
    }

    /**
     * Sets the value of the upi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUPI(String value) {
        this.upi = value;
    }

    /**
     * Gets the value of the identicalCrossReferences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identicalCrossReferences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdenticalCrossReferences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CrossReference }
     * 
     * 
     */
    public List<CrossReference> getIdenticalCrossReferences() {
        if (identicalCrossReferences == null) {
            identicalCrossReferences = new ArrayList<CrossReference>();
        }
        return this.identicalCrossReferences;
    }

    /**
     * Gets the value of the logicalCrossReferences property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the logicalCrossReferences property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLogicalCrossReferences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CrossReference }
     * 
     * 
     */
    public List<CrossReference> getLogicalCrossReferences() {
        if (logicalCrossReferences == null) {
            logicalCrossReferences = new ArrayList<CrossReference>();
        }
        return this.logicalCrossReferences;
    }

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
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

}
