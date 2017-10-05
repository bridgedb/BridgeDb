
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
 *         &lt;element name="getUPIForAccessionReturn" type="{http://model.picr.ebi.ac.uk}UPEntry" maxOccurs="unbounded"/>
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
    "getUPIForAccessionReturn"
})
@XmlRootElement(name = "getUPIForAccessionResponse")
public class GetUPIForAccessionResponse {

    @XmlElement(required = true)
    protected List<UPEntry> getUPIForAccessionReturn;

    /**
     * Gets the value of the getUPIForAccessionReturn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getUPIForAccessionReturn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetUPIForAccessionReturn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UPEntry }
     * 
     * 
     */
    public List<UPEntry> getGetUPIForAccessionReturn() {
        if (getUPIForAccessionReturn == null) {
            getUPIForAccessionReturn = new ArrayList<UPEntry>();
        }
        return this.getUPIForAccessionReturn;
    }

}
