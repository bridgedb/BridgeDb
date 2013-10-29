
package org.bridgedb.webservice.cronos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cronosWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cronosWS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="input_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="organism_3_letter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="query_int_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="target_int_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cronosWS", propOrder = {
    "inputId",
    "organism3Letter",
    "queryIntId",
    "targetIntId"
})
public class CronosWS_Type {

    @XmlElement(name = "input_id")
    protected String inputId;
    @XmlElement(name = "organism_3_letter")
    protected String organism3Letter;
    @XmlElement(name = "query_int_id")
    protected int queryIntId;
    @XmlElement(name = "target_int_id")
    protected int targetIntId;

    /**
     * Gets the value of the inputId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInputId() {
        return inputId;
    }

    /**
     * Sets the value of the inputId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInputId(String value) {
        this.inputId = value;
    }

    /**
     * Gets the value of the organism3Letter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganism3Letter() {
        return organism3Letter;
    }

    /**
     * Sets the value of the organism3Letter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganism3Letter(String value) {
        this.organism3Letter = value;
    }

    /**
     * Gets the value of the queryIntId property.
     * 
     */
    public int getQueryIntId() {
        return queryIntId;
    }

    /**
     * Sets the value of the queryIntId property.
     * 
     */
    public void setQueryIntId(int value) {
        this.queryIntId = value;
    }

    /**
     * Gets the value of the targetIntId property.
     * 
     */
    public int getTargetIntId() {
        return targetIntId;
    }

    /**
     * Sets the value of the targetIntId property.
     * 
     */
    public void setTargetIntId(int value) {
        this.targetIntId = value;
    }

}
