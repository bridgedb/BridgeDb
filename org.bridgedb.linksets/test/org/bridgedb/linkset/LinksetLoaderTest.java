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

import org.bridgedb.rdf.IDMapperLinksetException;
import org.bridgedb.linkset.rdf.RdfReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.utils.BridgeDBException;
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
public class LinksetLoaderTest {
       
    private static StoreType VALIDATE_ONLY = null;
    private static final boolean LOAD_DATA = true;
   private static final boolean DO_NOT_LOAD_DATA = true;
    
    //Unsure if this is still needed or even desirable!
    @BeforeClass
    public static void testLoader() throws IDMapperException, IOException, OpenRDFException, FileNotFoundException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
        
        LinksetLoader linksetLoader = new LinksetLoader();
        linksetLoader.clearExistingData(StoreType.TEST);
        ValidationType validationType = ValidationType.LINKSMINIMAL;
        linksetLoader.loadFile("../org.bridgedb.linksets/test-data/sample1to2.ttl", StoreType.TEST, validationType);
        linksetLoader.loadFile("../org.bridgedb.linksets/test-data/sample1to3.ttl", StoreType.TEST, validationType);
        linksetLoader.loadFile("../org.bridgedb.linksets/test-data/sample2to3.ttl", StoreType.TEST, validationType);
 	}

    @Test
    public void testMappingInfo() throws IDMapperException {
        TestSqlFactory.checkSQLAccess();
        SQLUrlMapper sqlUrlMapper = new SQLUrlMapper(false, StoreType.TEST);
        
        MappingSetInfo info = sqlUrlMapper.getMappingSetInfo(1);
        assertEquals ("TestDS1", info.getSourceSysCode());
        assertEquals ("TestDS2", info.getTargetSysCode());
        assertEquals ("http://www.bridgedb.org/test#testPredicate", info.getPredicate());
        //ystem.out.println(info);
    }
    
    @Test
    public void testCheckRDF() throws IDMapperException {
        RdfReader reader = new RdfReader(StoreType.TEST);
        String result = reader.getLinksetRDF(5);
        assertThat(result, containsString("linkset/5/TestDS3"));
        assertThat(result, not(containsString("linkset/2/")));
        assertThat(result, not(containsString("linkset/Test")));
        assertThat(result, not(containsString("OPS-IMS/#Test")));
    }
    
    @Test
    public void testCheckRDF2() throws IDMapperException {
        RdfReader reader = new RdfReader(StoreType.TEST);
        String result = reader.getLinksetRDF(6);
        //Inverse use ids of none inverse
        assertThat(result, containsString("linkset/5/TestDS3"));
        assertThat(result, not(containsString("linkset/6/TestDS3")));
    }
    
    @Test(expected=IDMapperLinksetException.class)
    public void testFileNotFound() throws IDMapperException, FileNotFoundException, BridgeDbSqlException, BridgeDBException {
        new LinksetLoader().validateFile("noFile.xyz", VALIDATE_ONLY, ValidationType.LINKSMINIMAL, false);
    }

}
