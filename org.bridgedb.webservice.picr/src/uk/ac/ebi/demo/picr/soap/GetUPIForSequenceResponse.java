
package uk.ac.ebi.demo.picr.soap;

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
 *         &lt;element name="getUPIForSequenceReturn" type="{http://model.picr.ebi.ac.uk}UPEntry"/>
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
    "getUPIForSequenceReturn"
})
@XmlRootElement(name = "getUPIForSequenceResponse")
public class GetUPIForSequenceResponse {

    @XmlElement(required = true)
    protected UPEntry getUPIForSequenceReturn;

    /**
     * Gets the value of the getUPIForSequenceReturn property.
     * 
     * @return
     *     possible object is
     *     {@link UPEntry }
     *     
     */
    public UPEntry getGetUPIForSequenceReturn() {
        return getUPIForSequenceReturn;
    }

    /**
     * Sets the value of the getUPIForSequenceReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link UPEntry }
     *     
     */
    public void setGetUPIForSequenceReturn(UPEntry value) {
        this.getUPIForSequenceReturn = value;
    }

}
