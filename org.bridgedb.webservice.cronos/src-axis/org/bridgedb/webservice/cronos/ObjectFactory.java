
package org.bridgedb.webservice.cronos;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.bridgedb.webservice.cronos package. 
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

    private final static QName _IsinRedListResponse_QNAME = new QName("http://webservice.cronos/", "isinRedListResponse");
    private final static QName _CronosWSResponse_QNAME = new QName("http://webservice.cronos/", "cronosWSResponse");
    private final static QName _IsinRedList_QNAME = new QName("http://webservice.cronos/", "isinRedList");
    private final static QName _CronosWS_QNAME = new QName("http://webservice.cronos/", "cronosWS");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.bridgedb.webservice.cronos
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IsinRedList }
     * 
     */
    public IsinRedList createIsinRedList() {
        return new IsinRedList();
    }

    /**
     * Create an instance of {@link CronosWS_Type }
     * 
     */
    public CronosWS_Type createCronosWS_Type() {
        return new CronosWS_Type();
    }

    /**
     * Create an instance of {@link CronosWSResponse }
     * 
     */
    public CronosWSResponse createCronosWSResponse() {
        return new CronosWSResponse();
    }

    /**
     * Create an instance of {@link IsinRedListResponse }
     * 
     */
    public IsinRedListResponse createIsinRedListResponse() {
        return new IsinRedListResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsinRedListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.cronos/", name = "isinRedListResponse")
    public JAXBElement<IsinRedListResponse> createIsinRedListResponse(IsinRedListResponse value) {
        return new JAXBElement<IsinRedListResponse>(_IsinRedListResponse_QNAME, IsinRedListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CronosWSResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.cronos/", name = "cronosWSResponse")
    public JAXBElement<CronosWSResponse> createCronosWSResponse(CronosWSResponse value) {
        return new JAXBElement<CronosWSResponse>(_CronosWSResponse_QNAME, CronosWSResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IsinRedList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.cronos/", name = "isinRedList")
    public JAXBElement<IsinRedList> createIsinRedList(IsinRedList value) {
        return new JAXBElement<IsinRedList>(_IsinRedList_QNAME, IsinRedList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CronosWS_Type }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.cronos/", name = "cronosWS")
    public JAXBElement<CronosWS_Type> createCronosWS(CronosWS_Type value) {
        return new JAXBElement<CronosWS_Type>(_CronosWS_QNAME, CronosWS_Type.class, null, value);
    }

}
