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
package org.bridgedb.mysql;

import java.util.List;
import java.util.Set;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
public class LensTest extends org.bridgedb.uri.UriListenerTest {
    
    static SQLUriMapper sqlUriMapper;
    
    @BeforeClass
    public static void setupIDMapper() throws BridgeDBException{
        connectionOk = false;
        TestSqlFactory.checkSQLAccess();
        connectionOk = true;
        ConfigReader.useTest();
        sqlUriMapper = SQLUriMapper.createNew();
        listener = sqlUriMapper;
        loadData();
        uriMapper = sqlUriMapper;
    }
        
    @Test
    public void testDefaultAndAllLens() throws Exception {
        report("DefaultAndAllLens");
        Lens defaultLens = LensTools.byId(Lens.DEFAULT_LENS_NAME);
        assertThat (defaultLens.getJustifications().size(), greaterThanOrEqualTo(1));
        Lens allLens = LensTools.byId(Lens.ALL_LENS_NAME);
        assertThat (allLens.getJustifications().size(), greaterThanOrEqualTo(defaultLens.getJustifications().size()));
        Set<String> justifications = sqlUriMapper.getJustifications();
        assertThat (allLens.getJustifications().size(), greaterThanOrEqualTo(justifications.size()));       
    }

    @Test
    public void testCHEMINF_000514() throws Exception {
        report("CHEMINF_000514");
        Lens defaultLens = LensTools.byId(Lens.DEFAULT_LENS_NAME);
        Lens SpecialLens = LensTools.byId("CHEMINF_000514");
        //Note if test is for a special lens which loads defauls not this specific lens.
        assertThat (SpecialLens.getJustifications().size(), greaterThan(defaultLens.getJustifications().size()));
    }

    @Test
    public void testRDF() throws Exception {
        report("RDF");
        Set<Statement> statements = LensTools.getLensAsRdf(null, LensTools.PUBLIC_GROUP_NAME);
        List<Lens> lens = LensTools.getLens(LensTools.PUBLIC_GROUP_NAME);
        assertThat(statements.size(), greaterThanOrEqualTo(lens.size() * 6));
    }
 }
