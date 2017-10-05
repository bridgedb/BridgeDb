
package uk.ac.ebi.demo.picr.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Input parameters for the tool
 * 
 * <p>Java class for BlastParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BlastParameter">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="program" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="matrix" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="alignments" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="scores" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="exp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dropoff" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="match_scores" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gapopen" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="gapext" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="filter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="seqrange" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gapalign" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="align" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="stype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BlastParameter", namespace = "http://model.picr.ebi.ac.uk", propOrder = {
    "program",
    "matrix",
    "alignments",
    "scores",
    "exp",
    "dropoff",
    "matchScores",
    "gapopen",
    "gapext",
    "filter",
    "seqrange",
    "gapalign",
    "align",
    "stype"
})
public class BlastParameter {

    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String program;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String matrix;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, type = Integer.class, nillable = true)
    protected Integer alignments;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, type = Integer.class, nillable = true)
    protected Integer scores;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String exp;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, type = Integer.class, nillable = true)
    protected Integer dropoff;
    @XmlElement(name = "match_scores", namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String matchScores;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, type = Integer.class, nillable = true)
    protected Integer gapopen;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, type = Integer.class, nillable = true)
    protected Integer gapext;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String filter;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, nillable = true)
    protected String seqrange;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, type = Boolean.class, nillable = true)
    protected Boolean gapalign;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true, type = Integer.class, nillable = true)
    protected Integer align;
    @XmlElement(namespace = "http://model.picr.ebi.ac.uk", required = true)
    protected String stype;

    /**
     * Gets the value of the program property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgram() {
        return program;
    }

    /**
     * Sets the value of the program property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgram(String value) {
        this.program = value;
    }

    /**
     * Gets the value of the matrix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatrix() {
        return matrix;
    }

    /**
     * Sets the value of the matrix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatrix(String value) {
        this.matrix = value;
    }

    /**
     * Gets the value of the alignments property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAlignments() {
        return alignments;
    }

    /**
     * Sets the value of the alignments property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAlignments(Integer value) {
        this.alignments = value;
    }

    /**
     * Gets the value of the scores property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getScores() {
        return scores;
    }

    /**
     * Sets the value of the scores property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setScores(Integer value) {
        this.scores = value;
    }

    /**
     * Gets the value of the exp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExp() {
        return exp;
    }

    /**
     * Sets the value of the exp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExp(String value) {
        this.exp = value;
    }

    /**
     * Gets the value of the dropoff property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDropoff() {
        return dropoff;
    }

    /**
     * Sets the value of the dropoff property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDropoff(Integer value) {
        this.dropoff = value;
    }

    /**
     * Gets the value of the matchScores property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatchScores() {
        return matchScores;
    }

    /**
     * Sets the value of the matchScores property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatchScores(String value) {
        this.matchScores = value;
    }

    /**
     * Gets the value of the gapopen property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGapopen() {
        return gapopen;
    }

    /**
     * Sets the value of the gapopen property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGapopen(Integer value) {
        this.gapopen = value;
    }

    /**
     * Gets the value of the gapext property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGapext() {
        return gapext;
    }

    /**
     * Sets the value of the gapext property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGapext(Integer value) {
        this.gapext = value;
    }

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilter(String value) {
        this.filter = value;
    }

    /**
     * Gets the value of the seqrange property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeqrange() {
        return seqrange;
    }

    /**
     * Sets the value of the seqrange property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeqrange(String value) {
        this.seqrange = value;
    }

    /**
     * Gets the value of the gapalign property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGapalign() {
        return gapalign;
    }

    /**
     * Sets the value of the gapalign property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGapalign(Boolean value) {
        this.gapalign = value;
    }

    /**
     * Gets the value of the align property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAlign() {
        return align;
    }

    /**
     * Sets the value of the align property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAlign(Integer value) {
        this.align = value;
    }

    /**
     * Gets the value of the stype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStype() {
        return stype;
    }

    /**
     * Sets the value of the stype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStype(String value) {
        this.stype = value;
    }

}
