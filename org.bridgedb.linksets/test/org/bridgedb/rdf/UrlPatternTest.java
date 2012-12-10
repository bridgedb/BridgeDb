package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.StringWriter;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class UrlPatternTest extends TestUtils{
    
    public UrlPatternTest() {
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
     * Test of byNameSpace method, of class UrlPattern.
     */
    @Test
    public void testByNameSpace() {
        report("byNameSpace");
        String nameSpace = "http://UrlPattern.example.com/Test1/";
        UrlPattern expResult = UrlPattern.byNameSpace(nameSpace);
        UrlPattern result = UrlPattern.byNameSpace(nameSpace);
        assertEquals(expResult, result);
    }

    /**
     * Test of byNameSpace method, of class UrlPattern.
     */
    @Test
    public void testByNameSpaceA() {
        report("byNameSpaceA");
        String nameSpace = "http://UrlPattern.example.com/Test1a/";
        UrlPattern other = UrlPattern.byNameSpace(nameSpace);
        UrlPattern expResult = UrlPattern.byNameSpace(nameSpace);
        UrlPattern result = UrlPattern.byNameSpace(nameSpace);
        assertEquals(expResult, result);
    }

    /**
     * Test of byNameSpace method, of class UrlPattern.
     */
    @Test
    public void testByNameSpaceB() {
        report("byNameSpaceB");
        String nameSpace = "http://UrlPattern.example.com/Test1b/";
        UrlPattern expResult = UrlPattern.byNameSpace(nameSpace);
        UrlPattern other = UrlPattern.byNameSpace("http://UrlPattern.example.com/Test1b/1");
        UrlPattern result = UrlPattern.byNameSpace(nameSpace);
        assertEquals(expResult, result);
    }

    /**
     * Test of byUrlPattern method, of class UrlPattern.
     */
    @Test
    public void testByUrlPattern() throws Exception {
        report("byUrlPattern");
        String nameSpace = "http://UrlPattern.example.com/Test2/";
        UrlPattern expResult = UrlPattern.byNameSpace(nameSpace);
        String urlPattern = "http://UrlPattern.example.com/Test2/$id";
        UrlPattern result = UrlPattern.byUrlPattern(urlPattern);
        assertEquals(expResult, result);
    }

    /**
     * Test of byUrlPattern method, of class UrlPattern.
     */
    @Test
    public void testByUrlPatternA() throws Exception {
        report("byUrlPattern");
        String urlPattern = "http://UrlPattern.example.com/Test2a/$id/postfix";
        UrlPattern expResult = UrlPattern.byUrlPattern(urlPattern);
        UrlPattern result = UrlPattern.byUrlPattern(urlPattern);
        assertEquals(expResult, result);
    }

    /**
     * Test of byUrlPattern method, of class UrlPattern.
     */
    @Test
    public void testByUrlPatternB() throws Exception {
        report("byUrlPattern");
        String urlPattern = "http://UrlPattern.example.com/Test2b/$id/postfix";
        UrlPattern expResult = UrlPattern.byUrlPattern(urlPattern);
        String urlPattern2 = "http://UrlPattern.example.com/Test2b/$id/postfix2";
        UrlPattern other = UrlPattern.byUrlPattern(urlPattern2);
        UrlPattern result = UrlPattern.byUrlPattern(urlPattern);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRdfId method, of class UrlPattern.
     */
    @Test
    public void testGetRdfId() {
        report("getRdfId");
        String nameSpace = "http://UrlPattern.example.com/Test3/";
        UrlPattern instance = UrlPattern.byNameSpace(nameSpace);
        String expResult = ":UrlPattern_http_UrlPatternexamplecom_Test3";
        String result = instance.getRdfId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRdfId method, of class UrlPattern.
     */
    @Test
    public void testGetRdfIdA() throws BridgeDBException {
        report("getRdfIdA");
        String urlPattern = "http://UrlPattern.example.com/Test3a/$id/postfix";
        UrlPattern instance = UrlPattern.byUrlPattern(urlPattern);
        String expResult = ":UrlPattern_http_UrlPatternexamplecom_Test3a_postfix";
        String result = instance.getRdfId();
        assertEquals(expResult, result);
    }

    /**
     * Test of writeAsRDF method, of class UrlPattern.
     */
    @Test
    public void testWriteAsRDF() throws Exception {
        report("writeAsRDF");
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        String nameSpace = "http://UrlPattern.example.com/Test4/";
        UrlPattern instance = UrlPattern.byNameSpace(nameSpace);
        instance.writeAsRDF(writer);
        writer.flush();
        String result = sw.toString();

        //Doing the Expected also using BufferedWriter to avoid different systems having different newLines 
        StringWriter wsExpected = new StringWriter();
        BufferedWriter expectedBuffer = new BufferedWriter(wsExpected);
        expectedBuffer.write(":UrlPattern_http_UrlPatternexamplecom_Test4 a bridgeDB:urlPattern;");
        expectedBuffer.newLine();
        expectedBuffer.flush();
        assertEquals(wsExpected.toString(), result);
    }

}
