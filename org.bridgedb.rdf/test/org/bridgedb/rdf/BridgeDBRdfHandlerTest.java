/*
 * BridgeDb,
 * An abstraction layer for identifier mapping services, both local and online.
 * Copyright (c) 2006 - 2009  BridgeDb Developers
 * Copyright (c) 2012-2013 Christian Y. A. Brenninkmeiier
 * Copyright (c) 2012 - 2013 OpenPhacts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.bridgedb.rdf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.SortedSet;

import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.utils.Reporter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Disabled;

/**
 *
 * @author Christian
 */
public class BridgeDBRdfHandlerTest {
    
   /**
     * Test of init method, of class BridgeDBRdfHandler.
     */
	@Disabled("Sensitive to test order")
    @Test
    public void testInit() throws Exception {
        Reporter.println("init");
        DataSourceTxt.init();
        BridgeDBRdfHandler.init();
        UriPattern.registerUriPatterns();
         
        SortedSet<UriPattern> result = UriPattern.byCodeAndType("M", UriPatternType.mainUrlPattern);
        UriPattern expected = UriPattern.byPattern("http://www.informatics.jax.org/marker/$id");
        assertThat(result, hasItem(expected));
        assertEquals(1, result.size());
        
        result = UriPattern.byCodeAndType("M", UriPatternType.identifiersOrgPatternSimple);
        expected = UriPattern.byPattern("http://identifiers.org/mgd/$id");
        assertThat(result, hasItem(expected));
        assertEquals(1, result.size());
        
        result = UriPattern.byCodeAndType("M", UriPatternType.codeMapperPattern);
        expected = UriPattern.byPattern("http://purl.uniprot.org/mgi/$id");
        assertThat(result, hasItem(expected));
        assertThat(result.size(), greaterThanOrEqualTo(1));
        
        File file = File.createTempFile("GeneratedDataSource", ".ttl");
        Reporter.println("writing to " + file.getAbsolutePath());
        BridgeDBRdfHandler.writeRdfToFile(file);
        
        Reporter.println("Reading back in " + file.getAbsolutePath());
        BridgeDBRdfHandler.parseRdfFile(file);
    }

}
