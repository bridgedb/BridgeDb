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

import org.bridgedb.metadata.TestUtils;
import org.bridgedb.rdf.LinksetStatementReaderTest;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.rdf.RdfReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Christian
 */
public class LinkSetLoaderUsingStringTest extends TestUtils{
       
    @Test
    public void testcheckStringValid() throws IDMapperException {
        report("CheckStringValid");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        new LinksetLoader().checkStringValid("LinksetStatementReaderTest.INFO1", 
                LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }
    
    @Test
    public void testvalidityReport() throws IDMapperException {
        report("validityReport");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle"); 
        String result = new LinksetLoader().validateString("LinksetStatementReaderTest.INFO1", 
                LinksetStatementReaderTest.INFO1, format, 
               StoreType.TEST, ValidationType.LINKSMINIMAL, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 3 links"));
    }

    @Test
    public void testLoad() throws IDMapperException {
        report("Load");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        new LinksetLoader().loadString("LinksetStatementReaderTest.INFO1", 
                LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }
    
}
