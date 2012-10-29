/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.junit.Ignore;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.AppendBase;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.MetaDataTestBase;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.rdf.LinksetStatementReaderTest;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.rdf.StringOutputStream;
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
            throws DatatypeConfigurationException, MetaDataException {
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
            throw new IDMapperException ("Error extracting rdf.", ex);
        }
    }

    /**
     * Test of validateString method, of class LinksetInterface.
     */
    @Test
    public void testValidateString() throws Exception {
        Reporter.report("validateString");
        boolean includeWarnings = false;
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        String result = linksetInterfaceMinimal.validateString(LinksetStatementReaderTest.INFO1, format, StoreType.TEST, 
                ValidationType.LINKSMINIMAL, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 3 links"));
    }

    /**
     * Test of validateStringAsDatasetVoid method, of class LinksetInterface.
     */
    @Test
    public void testValidateStringAsDatasetVoid() throws Exception {
        Reporter.report("validateStringAsDatasetVoid");
        String info = getRDF(loadMayDataSet1());
        String mimeType = "application/xml";
        String result = linksetInterfaceMinimal.validateStringAsVoid(info, mimeType);
        assertThat(result, not(containsString("ERROR")));
    }

    /**
     * Test of validateStringAsLinksetVoid method, of class LinksetInterface.
     * /
    @Test
    public void testValidateStringAsLinksetVoid() throws Exception {
        Reporter.report("validateStringAsLinksetVoid");
        String info = getRDF(loadMayLinkSet());
        String mimeType = "application/xml";
        String expResult = AppendBase.CLEAR_REPORT;
        String result = linksetInterfaceMinimal.validateStringAsLinksetVoid(info, mimeType);
        assertThat(result, not(containsString("ERROR")));
    }*/

    /**
     * Test of validateStringAsLinkset method, of class LinksetInterface.
     */
    @Test
    public void testValidateStringAsLinks() throws Exception {
        Reporter.report("validateStringAsLinkset");
        String info = getRDF(loadLinkSetwithLinks());
        String mimeType = "application/xml";;
        String result = linksetInterfaceMinimal.validateStringAsLinks(info, mimeType);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 2 links"));
    }

     /**
     * Test of load method, of class LinksetInterface.
     */
    @Test
    public void testLoadStrings() throws Exception {
        Reporter.report("load from String");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        StoreType storeType = null;
        ValidationType validationType = null;
        linksetInterfaceMinimal.loadString(LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }

   /**
     * Test of validate method, of class LinksetInterface.
     */
    @Test
    public void testCheckStringValid() throws Exception {
        Reporter.report("CheckStringValid");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        linksetInterfaceMinimal.checkStringValid(LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }

}
