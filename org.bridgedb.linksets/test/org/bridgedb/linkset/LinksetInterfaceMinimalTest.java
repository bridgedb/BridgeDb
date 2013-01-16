// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.linkset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.IDMapperLinksetException;
import org.bridgedb.rdf.LinksetStatementReaderTest;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.tools.metadata.MetaDataTestBase;
import org.bridgedb.tools.metadata.rdf.StringOutputStream;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.rdfxml.RDFXMLWriter;

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
