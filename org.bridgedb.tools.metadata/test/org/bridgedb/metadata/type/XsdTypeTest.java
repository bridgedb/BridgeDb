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
package org.bridgedb.metadata.type;

import org.bridgedb.rdf.constants.XMLSchemaConstants;
import org.bridgedb.tools.metadata.type.XsdType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;

/**
 *
 * @author Christian
 */
public class XsdTypeTest extends TestUtils {
    
    public XsdTypeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of correctType method, of class XsdType.
     */
    @Test
    public void testIntegerTypes() throws BridgeDBException {
        report("IntegerTypes");
        XsdType instance = XsdType.getByType(XMLSchemaConstants.INTEGER.stringValue());
        Value value = new LiteralImpl("10",XMLSchemaConstants.INTEGER);;
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("10",XMLSchemaConstants.NON_NEGATIVE_INTEGER);
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("10",XMLSchemaConstants.UNISGNED_BYTE);
        assertTrue(instance.correctType(value));
        value = new LiteralImpl("10");
        assertFalse(instance.correctType(value));
     }

    /**
     * Test of getCorrectType method, of class XsdType.
     */
    @Test
    public void testGetCorrectType() throws BridgeDBException {
        report("getCorrectType");
        XsdType instance = XsdType.getByType(XMLSchemaConstants.INTEGER.stringValue());
        String expResult = XMLSchemaConstants.INTEGER.stringValue();
        String result = instance.getCorrectType();
        assertEquals(expResult, result);
     }
}
