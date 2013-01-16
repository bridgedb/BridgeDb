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

import org.bridgedb.utils.TestUtils;
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
public class UriPatternTest extends TestUtils{
    
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
     * Test of byNameSpace method, of class UriPattern.
     * /
    @Test
    public void testByNameSpace() {
        System.out.println("byNameSpace");
        String nameSpace = "";
        UriPattern expResult = null;
        UriPattern result = UriPattern.byNameSpace(nameSpace);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of byPattern method, of class UriPattern.
     * 
    @Test
    public void testByPattern() throws Exception {
        System.out.println("byPattern");
        String urlPattern = "";
        UriPattern expResult = null;
        UriPattern result = UriPattern.byPattern(urlPattern);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of byNameSpaceAndPostFix method, of class UriPattern.
     * /
    @Test
    public void testByNameSpaceAndPostFix() throws Exception {
        System.out.println("byNameSpaceAndPostFix");
        String nameSpace = "";
        String postfix = "";
        UriPattern expResult = null;
        UriPattern result = UriPattern.byNameSpaceAndPostFix(nameSpace, postfix);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPrimaryDataSource method, of class UriPattern.
     * /
    @Test
    public void testSetPrimaryDataSource() throws Exception {
        System.out.println("setPrimaryDataSource");
        DataSourceUris dsu = null;
        UriPattern instance = null;
        instance.setPrimaryDataSource(dsu);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDataSource method, of class UriPattern.
     * /
    @Test
    public void testSetDataSource() throws Exception {
        System.out.println("setDataSource");
        DataSourceUris dsu = null;
        UriPattern instance = null;
        instance.setDataSource(dsu);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDataSource method, of class UriPattern.
     * /
    @Test
    public void testGetDataSource() {
        System.out.println("getDataSource");
        UriPattern instance = null;
        DataSource expResult = null;
        DataSource result = instance.getDataSource();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMainDataSourceUris method, of class UriPattern.
     * /
    @Test
    public void testGetMainDataSourceUris() {
        System.out.println("getMainDataSourceUris");
        UriPattern instance = null;
        DataSourceUris expResult = null;
        DataSourceUris result = instance.getMainDataSourceUris();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getResourceId method, of class UriPattern.
     * /
    @Test
    public void testGetResourceId() {
        System.out.println("getResourceId");
        UriPattern instance = null;
        URI expResult = null;
        URI result = instance.getResourceId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUriPattern method, of class UriPattern.
     * /
    @Test
    public void testGetUriPattern() {
        System.out.println("getUriPattern");
        UriPattern instance = null;
        String expResult = "";
        String result = instance.getUriPattern();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addAll method, of class UriPattern.
     * /
    @Test
    public void testAddAll() throws Exception {
        System.out.println("addAll");
        RepositoryConnection repositoryConnection = null;
        boolean addPrimaries = false;
        UriPattern.addAll(repositoryConnection, addPrimaries);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class UriPattern.
     * /
    @Test
    public void testAdd() throws Exception {
        System.out.println("add");
        RepositoryConnection repositoryConnection = null;
        boolean addPrimaries = false;
        UriPattern instance = null;
        instance.add(repositoryConnection, addPrimaries);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readAllUriPatterns method, of class UriPattern.
     * /
    @Test
    public void testReadAllUriPatterns() throws Exception {
        System.out.println("readAllUriPatterns");
        RepositoryConnection repositoryConnection = null;
        UriPattern.readAllUriPatterns(repositoryConnection);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readUriPattern method, of class UriPattern.
     * /
    @Test
    public void testReadUriPattern_5args() throws Exception {
        System.out.println("readUriPattern");
        RepositoryConnection repositoryConnection = null;
        Resource dataSourceId = null;
        DataSourceUris parent = null;
        URI primary = null;
        URI shared = null;
        UriPattern expResult = null;
        UriPattern result = UriPattern.readUriPattern(repositoryConnection, dataSourceId, parent, primary, shared);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readUriPattern method, of class UriPattern.
     * /
    @Test
    public void testReadUriPattern_6args() throws Exception {
        System.out.println("readUriPattern");
        RepositoryConnection repositoryConnection = null;
        Resource dataSourceId = null;
        DataSourceUris parent = null;
        URI primary = null;
        URI shared = null;
        URI old = null;
        UriPattern expResult = null;
        UriPattern result = UriPattern.readUriPattern(repositoryConnection, dataSourceId, parent, primary, shared, old);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readUriPattern method, of class UriPattern.
     * /
    @Test
    public void testReadUriPattern_RepositoryConnection_Resource() throws Exception {
        System.out.println("readUriPattern");
        RepositoryConnection repositoryConnection = null;
        Resource uriPatternId = null;
        UriPattern expResult = null;
        UriPattern result = UriPattern.readUriPattern(repositoryConnection, uriPatternId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class UriPattern.
     * /
    @Test
    public void testToString() {
        System.out.println("toString");
        UriPattern instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasPostfix method, of class UriPattern.
     * /
    @Test
    public void testHasPostfix() {
        System.out.println("hasPostfix");
        UriPattern instance = null;
        boolean expResult = false;
        boolean result = instance.hasPostfix();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUriSpace method, of class UriPattern.
     * /
    @Test
    public void testGetUriSpace() throws Exception {
        System.out.println("getUriSpace");
        UriPattern instance = null;
        String expResult = "";
        String result = instance.getUriSpace();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkAllDataSourceUris method, of class UriPattern.
     * /
    @Test
    public void testCheckAllDataSourceUris() throws Exception {
        System.out.println("checkAllDataSourceUris");
        UriPattern.checkAllDataSourceUris();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
*/
    /**
     * Test of compareTo method, of class UriPattern.
     */
    @Test
    public void testCompareTo() {
        report("compareTo");
        UriPattern pattern1 = UriPattern.byNameSpace("http://www.example.com/UriPatternTest/testCompareTo/1");
        UriPattern pattern1s = UriPattern.byNameSpace("https://www.example.com/UriPatternTest/testCompareTo/1");
        UriPattern pattern2 = UriPattern.byNameSpace("http://www.example.com/UriPatternTest/testCompareTo/2");
        assertThat(pattern2.compareTo(pattern1), greaterThan(0));
        assertThat(pattern1.compareTo(pattern2), lessThan(0));
        assertThat(pattern2.compareTo(pattern1s), greaterThan(0));
        assertThat(pattern1s.compareTo(pattern2), lessThan(0));
    }
}
