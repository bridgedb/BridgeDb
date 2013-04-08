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

import java.io.File;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.LinksetStatementReaderTest;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.bridgedb.utils.TestUtils;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openrdf.rio.RDFFormat;

/**
 * @author Christian
 */
public class LinkSetLoaderUsingStringTest extends TestUtils{
       
    public LinkSetLoaderUsingStringTest() 
            throws DatatypeConfigurationException, BridgeDBException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
  
        SQLUriMapper mapper = SQLUriMapper.factory(false, StoreType.TEST);
        DataSource foo = DataSource.register("foo", "foo").asDataSource();
        mapper.registerUriPattern(foo, "http://www.foo.com/$id");
        DataSource example = DataSource.register("example.com", "example.com").asDataSource();
        mapper.registerUriPattern(example, "http://www.example.com/$id");
    }
    
    @Test
    public void testcheckStringValid() throws BridgeDBException {
        report("CheckStringValid");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        new LinksetLoader().checkStringValid("LinksetStatementReaderTest.INFO1", 
                LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }
    
    @Test
    public void testvalidityReport() throws BridgeDBException {
        report("validityReport");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle"); 
        String result = new LinksetLoader().validateString("LinksetStatementReaderTest.INFO1", 
                LinksetStatementReaderTest.INFO1, format, 
               StoreType.TEST, ValidationType.LINKSMINIMAL, false);
        assertThat(result, not(containsString("ERROR")));
        assertThat(result, containsString("Found 3 links"));
    }

    @Test
    public void testLoad() throws BridgeDBException {
        report("Load");
        RDFFormat format = StatementReader.getRDFFormatByMimeType("text/turtle");
        new LinksetLoader().loadString("LinksetStatementReaderTest.INFO1", 
                LinksetStatementReaderTest.INFO1, format, StoreType.TEST, ValidationType.LINKSMINIMAL);
    }
    
    @Test
    public void testLoadString() throws BridgeDBException{
        report("LoadString");
        File test = LinksetLoader.saveString("This is a test", RDFFormat.TURTLE, ValidationType.LINKS);
    }

}
