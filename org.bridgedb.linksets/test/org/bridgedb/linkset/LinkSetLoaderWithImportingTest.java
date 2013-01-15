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

import org.bridgedb.utils.TestUtils;
import org.bridgedb.linkset.rdf.RdfReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.IDMapperException;
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
import org.junit.Test;
import org.openrdf.OpenRDFException;
import static org.junit.Assert.*;

/**
 * @author Christian
 */
public class LinkSetLoaderWithImportingTest extends TestUtils{
        
    private static final boolean LOAD_DATA = true;
    
    //Unsure if this is still needed or even desirable!
    @BeforeClass
    public static void testLoader() throws IDMapperException, IOException, OpenRDFException, FileNotFoundException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
        
        LinksetLoader linksetLoader = new LinksetLoader();
        linksetLoader.clearExistingData(StoreType.TEST);
        linksetLoader.loadFile("../org.bridgedb.tools.metadata/test-data/chemspider-void.ttl", StoreType.TEST, ValidationType.VOID);
        linksetLoader.loadFile("../org.bridgedb.tools.metadata/test-data/chembl-rdf-void.ttl", StoreType.TEST, ValidationType.VOID);
        //CB Jan 15 2013 Line below is different to none profile test
        linksetLoader.loadFile("test-data/chemspider2chemblrdf-linkset.ttl", StoreType.TEST, ValidationType.LINKS);
	}

    @Test
    public void testVoidInfo() throws IDMapperException {
        RdfReader reader = new RdfReader(StoreType.TEST);
        String result = reader.getVoidRDF(1);
        assertTrue(result.contains("ChemSpider"));
        assertFalse(result.contains("http://linkedchemistry.info/chembl/molecule/"));
    }
    
    @Test
    public void testMappingInfo() throws IDMapperException {
        report("MappingInfo");
        TestSqlFactory.checkSQLAccess();
        SQLUrlMapper sqlUrlMapper = new SQLUrlMapper(false, StoreType.TEST);
        
        MappingSetInfo info = sqlUrlMapper.getMappingSetInfo(1);
        //CB Jan 15 2013 Line below is different to none profile test
        assertEquals ("Chembl 13 Molecule", info.getSourceSysCode());
        assertEquals ("ChemSpider", info.getTargetSysCode());
        assertEquals ("http://www.w3.org/2004/02/skos/core#exactMatch", info.getPredicate());
        //ystem.out.println(info);
    }
    

}
