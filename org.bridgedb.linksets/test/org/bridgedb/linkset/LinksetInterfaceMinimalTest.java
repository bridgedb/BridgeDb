/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.bridgedb.rdf.IDMapperLinksetException;
import org.junit.Ignore;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.IDMapperException;
import org.bridgedb.tools.metadata.AppendBase;
import org.bridgedb.tools.metadata.MetaDataTestBase;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.rdf.LinksetStatementReaderTest;
import org.bridgedb.tools.metadata.rdf.StringOutputStream;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public abstract class LinksetInterfaceMinimalTest extends MetaDataTestBase{
    
    LinksetInterfaceMinimal linksetInterfaceMinimal;
    
    public LinksetInterfaceMinimalTest(LinksetInterfaceMinimal instance) 
            throws DatatypeConfigurationException, BridgeDBException {
        this.linksetInterfaceMinimal = instance;
    }

    String getRDF(Set<Statement> statements) throws IDMapperException {
        StringOutputStream stringOutputStream = new StringOutputStream();            
        RDFXMLWriter writer = new RDFXMLWriter(stringOutputStream);
        writer.startRDF();
        try {
            for (Statement st:statements){
                writer.handleStatement(st);
            }
            writer.endRDF();
            return stringOutputStream.toString();
        } catch (Throwable ex) {
            throw new IDMapperLinksetException ("Error extracting rdf.", ex);
        }
    }

    /**
     * Test of validateString method, of class LinksetInterface.
     */
    @Test
    public void testValidateString() throws Exception {
        report("validateString");
        boolean includeWarnings = false;
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        String result = linksetInterfaceMinimal.validateString("LinksetStatementReaderTest.INFO1",
                LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 3 links"));
    }

    /**
     * Test of validateString method, of class LinksetInterface.
     */
    @Test
    public void testValidateInputStream() throws Exception {
        report("validateInputStream");
        boolean includeWarnings = false;
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        InputStream is = new ByteArrayInputStream(LinksetStatementReaderTest.INFO1.getBytes());
        String result = linksetInterfaceMinimal.validateInputStream("validateInputStream", is, format, StoreType.TEST, 
                ValidationType.LINKSMINIMAL, includeWarnings);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 3 links"));
    }

    /**
     * Test of validateStringAsDatasetVoid method, of class LinksetInterface.
     */
    @Test
    public void testValidateStringAsDatasetVoid() throws Exception {
        report("validateStringAsDatasetVoid");
        String info = getRDF(loadMayDataSet1());
        String mimeType = "application/xml";
        RDFFormat format = StatementReader.getRDFFormatByMimeType(mimeType);
        String result = linksetInterfaceMinimal.validateString("loadMayDataSet1()", info, format, StoreType.TEST, 
                ValidationType.VOID, false);
        assertThat(result, not(containsString("ERROR")));
    }

    /**
     * Test of validateStringAsDatasetVoid method, of class LinksetInterface.
     */
    @Test
    public void testValidateInputStreamAsDatasetVoid() throws Exception {
        report("validateInputStreamAsDatasetVoid");
        boolean includeWarnings = false;
        String info = getRDF(loadMayDataSet1());
        String mimeType = "application/xml";
        RDFFormat format = StatementReader.getRDFFormatByMimeType(mimeType);
        InputStream is = new ByteArrayInputStream(info.getBytes());
        String result = linksetInterfaceMinimal.validateInputStream("validateInputStream", is, format, StoreType.TEST, 
                ValidationType.VOID, includeWarnings);
        assertThat(result, not(containsString("ERROR")));
    }

    /**
     * Test of validateStringAsLinkset method, of class LinksetInterface.
     */
    @Test
    public void testValidateStringAsLinks() throws Exception {
        report("validateStringAsLinkset");
        String info = getRDF(loadLinkSetwithLinks());
        String mimeType = "application/xml";;
        RDFFormat format = StatementReader.getRDFFormatByMimeType(mimeType);
        String result = linksetInterfaceMinimal.validateString("loadMayDataSet1()", info, format, StoreType.TEST, 
                ValidationType.LINKS, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 2 links"));
    }

     /**
     * Test of load method, of class LinksetInterface.
     */
    @Test
    public void testLoadStrings() throws Exception {
        report("load from String");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        StoreType storeType = null;
        ValidationType validationType = null;
        linksetInterfaceMinimal.loadString("LinksetStatementReaderTest.INFO1", 
                LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }

   /**
     * Test of validate method, of class LinksetInterface.
     */
    @Test
    public void testCheckStringValid() throws Exception {
        report("CheckStringValid");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        linksetInterfaceMinimal.checkStringValid("LinksetStatementReaderTest.INFO1", 
                LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }

}
