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
package org.bridgedb.uri;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.sql.SQLListener;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.transative.DirectMapping;
import org.bridgedb.sql.transative.SelfMapping;
import org.bridgedb.sql.transative.TransitiveMapping;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.SourceInfo;
import org.bridgedb.statistics.SourceTargetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.tools.DirectStatementMaker;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.StatementMaker;
import org.bridgedb.utils.BridgeDBException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Statement;

/**
 * Tests the UriMapper interface (and by loading the UriListener interface)
 *
 * Should be passable by any implementation of UriMapper that has the test data loaded.
 * 
 * @author Christian
 */
public abstract class UriMapperRdfTest extends UriListenerTest{
           
    public static final String NULL_GRAPH = null;
    public static final Set<String> NULL_PATTERNS = null;
    public static final Set<DataSource> NO_TARGET_DATASOURCE = null;
    public static final Boolean DEFAULT_IGNORE_XREF = null;
    public static final String NULL_LENS = null;
    
    public static StatementMaker statementMaker;
    
    public void checkMapping(Mapping mapping){
        if (mapping instanceof SelfMapping){
            return;
        } else if (mapping instanceof DirectMapping){
            checkDirect((DirectMapping)mapping);
        } else if (mapping instanceof TransitiveMapping) {
            for (DirectMapping via:((TransitiveMapping)mapping).getVia()){
                checkDirect(via);
            }
        }
    }
    
    protected void checkDirect(DirectMapping directMapping) {
        if (!directMapping.hasMappingToSelf()){
            assertNotNull(directMapping.getMappingSource());
        }
    }
    
    @Test 
    public void testMapSetInfo1() throws BridgeDBException {
        report("MapSetInfo1");
        MappingSetInfo info = uriMapper.getMappingSetInfo(1);
        statementMaker.asRDF(info, "http://example.com/testBase", "http://example.com/testContext");
    }

    @Test 
    public void testMappingNoLink() throws BridgeDBException {
        report("MapSetInfoNoLink");
        Set<Mapping> mappings = uriMapper.mapFull(map1Uri1,  null, false, null, null);
        statementMaker.asRDF(mappings, "http://example.com/testContext", false);
    }

    @Test 
    public void testMappingWithLink() throws BridgeDBException {
        report("MapSetInfoWithLink");
        Set<Mapping> mappings = uriMapper.mapFull(map1Uri1,  null, true, null, null);
        statementMaker.asRDF(mappings, "http://example.com/testContext", false);
    }
}
