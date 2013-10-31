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
package org.bridgedb.rdf;

import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import static org.hamcrest.number.OrderingComparison.*;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class UriPatternTest {
    
    public UriPatternTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of compareTo method, of class UriPattern.
     */
    @Test 
    public void testCompareTo() throws BridgeDBException {
        Reporter.println("compareTo");
        UriPattern pattern1 = UriPattern.register("http://www.example.com/UriPatternTest/testCompareTo/1$id", "UriPatternTest", UriPatternType.dataSourceUriPattern);
        UriPattern pattern1s = UriPattern.register("https://www.example.com/UriPatternTest/testCompareTo/1$id", "UriPatternTest", UriPatternType.dataSourceUriPattern);
        UriPattern pattern2 = UriPattern.register("http://www.example.com/UriPatternTest/testCompareTo/2$id", "UriPatternTest", UriPatternType.dataSourceUriPattern);
        assertThat(pattern2.compareTo(pattern1), greaterThan(0));
        assertThat(pattern1.compareTo(pattern2), lessThan(0));
        assertThat(pattern2.compareTo(pattern1s), greaterThan(0));
        assertThat(pattern1s.compareTo(pattern2), lessThan(0));
    }
}
