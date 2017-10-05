
package uk.ac.ebi.demo.picr.soap;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.ac.ebi.demo.picr.soap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.ac.ebi.demo.picr.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UPEntry }
     * 
     */
    public UPEntry createUPEntry() {
        return new UPEntry();
    }

    /**
     * Create an instance of {@link GetUPIForSequenceResponse }
     * 
     */
    public GetUPIForSequenceResponse createGetUPIForSequenceResponse() {
        return new GetUPIForSequenceResponse();
    }

    /**
     * Create an instance of {@link GetUPIForBlastSequenceResponse }
     * 
     */
    public GetUPIForBlastSequenceResponse createGetUPIForBlastSequenceResponse() {
        return new GetUPIForBlastSequenceResponse();
    }

    /**
     * Create an instance of {@link GetUPIForSequence }
     * 
     */
    public GetUPIForSequence createGetUPIForSequence() {
        return new GetUPIForSequence();
    }

    /**
     * Create an instance of {@link CrossReference }
     * 
     */
    public CrossReference createCrossReference() {
        return new CrossReference();
    }

    /**
     * Create an instance of {@link GetMappedDatabaseNames }
     * 
     */
    public GetMappedDatabaseNames createGetMappedDatabaseNames() {
        return new GetMappedDatabaseNames();
    }

    /**
     * Create an instance of {@link GetUPIForAccessionResponse }
     * 
     */
    public GetUPIForAccessionResponse createGetUPIForAccessionResponse() {
        return new GetUPIForAccessionResponse();
    }

    /**
     * Create an instance of {@link GetUPIForBlastSequence }
     * 
     */
    public GetUPIForBlastSequence createGetUPIForBlastSequence() {
        return new GetUPIForBlastSequence();
    }

    /**
     * Create an instance of {@link BlastParameter }
     * 
     */
    public BlastParameter createBlastParameter() {
        return new BlastParameter();
    }

    /**
     * Create an instance of {@link GetMappedDatabaseNamesResponse }
     * 
     */
    public GetMappedDatabaseNamesResponse createGetMappedDatabaseNamesResponse() {
        return new GetMappedDatabaseNamesResponse();
    }

    /**
     * Create an instance of {@link GetUPIForAccession }
     * 
     */
    public GetUPIForAccession createGetUPIForAccession() {
        return new GetUPIForAccession();
    }

}
