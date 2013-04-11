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

import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.linkset.rdf.RdfReader;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.OpenRDFException;

/**
 * @author Christian
 */
public class LinksetLoaderTest {
       
    private static StoreType VALIDATE_ONLY = null;
    private static final boolean LOAD_DATA = true;
   private static final boolean DO_NOT_LOAD_DATA = true;
    
    //Unsure if this is still needed or even desirable!
    @BeforeClass
    public static void testLoader() throws BridgeDBException, IOException, OpenRDFException, FileNotFoundException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
        
        LinksetLoader linksetLoader = new LinksetLoader();
        linksetLoader.clearExistingData(StoreType.TEST);
        ValidationType validationType = ValidationType.LINKSMINIMAL;
        linksetLoader.load("../org.bridgedb.linksets/test-data/cw-cs.ttl", StoreType.TEST, validationType);
        linksetLoader.load("../org.bridgedb.linksets/test-data/cw-cm.ttl", StoreType.TEST, validationType);
        linksetLoader.load("../org.bridgedb.linksets/test-data/cs-cm.ttl", StoreType.TEST, validationType);
 	}

    @Test
    public void testMappingInfo() throws BridgeDBException {
        TestSqlFactory.checkSQLAccess();
        SQLUriMapper sqlUriMapper = SQLUriMapper.factory(false, StoreType.TEST);
        
        MappingSetInfo info = sqlUriMapper.getMappingSetInfo(1);
        assertEquals ("ConceptWiki", info.getSource().getSysCode());
        assertEquals ("Cs", info.getTarget().getSysCode());
        assertEquals ("http://www.w3.org/2004/02/skos/core#exactMatch", info.getPredicate());
        //ystem.out.println(info);
    }
    
    @Test
    public void testCheckRDF() throws BridgeDBException {
        RdfReader reader = new RdfReader(StoreType.TEST);
        String result = reader.getLinksetRDF(5);
        assertThat(result, containsString("linkset/5/conceptwiki_chemblMolecule"));
        assertThat(result, not(containsString("linkset/2/")));
        assertThat(result, not(containsString("linkset/Test")));
        assertThat(result, not(containsString("OPS-IMS/#Test")));
    }
    
    @Test
    public void testCheckRDF2() throws BridgeDBException {
        RdfReader reader = new RdfReader(StoreType.TEST);
        String result = reader.getLinksetRDF(6);
        //Inverse use ids of none inverse
        assertThat(result, containsString("linkset/5/conceptwiki_chemblMolecule"));
        assertThat(result, not(containsString("linkset/6/conceptwiki_chemblMolecule")));
    }
    
    @Test(expected=BridgeDBException.class)
    public void testFileNotFound() throws BridgeDBException, FileNotFoundException, BridgeDBException {
        new LinksetLoader().validateAddress("noFile.xyz", VALIDATE_ONLY, ValidationType.LINKSMINIMAL, false);
    }

}
