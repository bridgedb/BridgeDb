// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import org.bridgedb.utils.TestUtils;
import org.bridgedb.rdf.LinksetStatementReaderTest;
import org.openrdf.rio.RDFFormat;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.utils.StoreType;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Christian
 */
public class LinkSetLoaderUsingInputStream extends TestUtils{
       
    @Test
    public void testcheckInputStreamValid() throws IDMapperException {
        report("CheckInputStreamValid");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        InputStream inputStream = new ByteArrayInputStream(LinksetStatementReaderTest.INFO1.getBytes());
        new LinksetLoader().checkInputStreamValid("LinksetStatementReaderTest.INFO1", 
                inputStream, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }
    
    @Test
    public void testvalidityReport() throws IDMapperException {
        report("validityReport");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle"); 
        InputStream inputStream = new ByteArrayInputStream(LinksetStatementReaderTest.INFO1.getBytes());
        String result = new LinksetLoader().validateInputStream("LinksetStatementReaderTest.INFO1", 
               inputStream, format, StoreType.TEST, ValidationType.LINKSMINIMAL, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 3 links"));
    }

    @Test
    public void testLoad() throws IDMapperException {
        report("Load");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        InputStream inputStream = new ByteArrayInputStream(LinksetStatementReaderTest.INFO1.getBytes());
        new LinksetLoader().loadInputStream("LinksetStatementReaderTest.INFO1", 
                inputStream, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }
    
    @Test
    public void testLoadInputStream() throws IDMapperException{
        report("SaveInputStream");
        InputStream inputStream = new ByteArrayInputStream("This is a test".getBytes());
        File test = LinksetLoader.saveInputStream(inputStream, RDFFormat.TURTLE, ValidationType.LINKS);
    }

}
