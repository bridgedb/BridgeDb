
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
 *         &lt;element name="getUPIForBlastSequenceReturn" type="{http://model.picr.ebi.ac.uk}UPEntry" maxOccurs="unbounded"/>
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
    "getUPIForBlastSequenceReturn"
})
@XmlRootElement(name = "getUPIForBlastSequenceResponse")
public class GetUPIForBlastSequenceResponse {

    @XmlElement(required = true)
    protected List<UPEntry> getUPIForBlastSequenceReturn;

    /**
     * Gets the value of the getUPIForBlastSequenceReturn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getUPIForBlastSequenceReturn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetUPIForBlastSequenceReturn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UPEntry }
     * 
     * 
     */
    public List<UPEntry> getGetUPIForBlastSequenceReturn() {
        if (getUPIForBlastSequenceReturn == null) {
            getUPIForBlastSequenceReturn = new ArrayList<UPEntry>();
        }
        return this.getUPIForBlastSequenceReturn;
    }

}
