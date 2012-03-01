/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb;

import java.util.List;
import java.util.Set;
import org.bridgedb.DataSource.Builder;
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
public class DataSourceTest {
    
    public DataSourceTest() {
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
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPattern() throws Exception {
        String urlProfile = "http://www.example1.com/$id";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource result = DataSource.getByURL(urlProfile);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLDifferentURL() throws Exception {
        String urlProfile = "http://www.example2.com/$id";
        String url = "http://www.example2.com/12345";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource result = DataSource.getByURL(url);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLChangedPattern() throws Exception {
        String urlProfile1 = "http://www.example3.com/Pizza/$id";
        String urlProfile2 = "http://www.example3.com/Bread/$id";
        String url1 = "http://www.example3.com/Pizza/12345";
        String url2 = "http://www.example3.com/Bread/12345";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile1).asDataSource();
        DataSource result = DataSource.getByURL(url1);
        assertEquals(expResult, result);
        expResult = DataSource.register("test1", "test1").urlPattern(urlProfile2).asDataSource();
        result = DataSource.getByURL(url1);
        assertNotNull(result);
        result = DataSource.getByURL(url2);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLSeveralURL() throws Exception {
        String urlProfile = "http://www.example4.com/$id";
        String url = "http://www.example4.com/12345";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource.register("testx1", "testx1").urlPattern("http://www.example4.com/Pizza#$id").asDataSource();
        DataSource.register("testx2", "testx2").urlPattern("http://www.example4.com/Bread#$id").asDataSource();
        DataSource.register("testx3", "testx3").urlPattern("http://www.example4.com/Pizza/$id").asDataSource();
        DataSource.register("testx4", "testx4").urlPattern("http://www.example4.com/Pizza:$id").asDataSource();
        DataSource result = DataSource.getByURL(url);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test(expected = IDMapperException.class)
    public void testByURLNoDuplicates() throws Exception {
        String urlProfile = "http://www.example4.com/$id";
        String url = "http://www.example4.com/12345";
        DataSource.register("testx1", "testx1").urlPattern("http://www.example4.com/Pizza#$id").asDataSource();
        DataSource.register("testx2", "testx2").urlPattern("http://www.example4.com/Pizza#$id").asDataSource();
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLWithPostFix() throws Exception {
        String urlProfile = "http://www.example5.com/$id/more";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource result = DataSource.getByURLPattern(urlProfile);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLDifferentURWithPostFix() throws Exception {
        String urlProfile = "http://www.example6.com/$id/more";
        String url = "http://www.example6.com/12345/more";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource result = DataSource.getByURL(url);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLChangedPatternWithPostFix() throws Exception {
        String urlProfile1 = "http://www.example7.com/Pizza/$id/more";
        String urlProfile2 = "http://www.example7.com/Bread/$id/more";
        String url1 = "http://www.example7.com/Pizza/12345/more";
        String url2 = "http://www.example7.com/Bread/12345/more";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile1).asDataSource();
        DataSource result = DataSource.getByURL(url1);
        assertEquals(expResult, result);
        expResult = DataSource.register("test1", "test1").urlPattern(urlProfile2).asDataSource();
        result = DataSource.getByURL(url1);
        assertNotNull(result);
        assertFalse(expResult.equals(result));
        result = DataSource.getByURL(url2);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLSeveralURLWithPostFix() throws Exception {
        String urlProfile = "http://www.example8.com/$id/more";
        String url = "http://www.example8.com/12345/more";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource.register("testy1", "testy1").urlPattern("http://www.example8.org/Pizza#$id/more").asDataSource();
        DataSource.register("testy2", "testy2").urlPattern("http://www.example8.org/Bread#$id/more").asDataSource();
        DataSource.register("testy3", "testy3").urlPattern("http://www.example8.org/$id#more").asDataSource();
        DataSource.register("testy4", "testy4").urlPattern("http://www.example8.org/$id/more").asDataSource();
        DataSource.register("testy5", "testy5").urlPattern("http://www.example8.org/$id:more").asDataSource();
        DataSource result = DataSource.getByURL(url);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test(expected = IDMapperException.class)
    public void testByRegisterNoDuplicatesWithPostFix() throws Exception {
        DataSource.register("testx1", "testx1").urlPattern("http://www.example9.com/Pizza#$id/more").asDataSource();
        DataSource.register("testx2", "testx2").urlPattern("http://www.example9.com/Pizza#$id/more").asDataSource();
    }
    
    @Test
    public void testByURLDirectly(){
        String urlPattern = "http://www.example10.com/$id";
        DataSource expected = DataSource.getByURLPattern(urlPattern);
        DataSource result = DataSource.getByURLPattern(urlPattern);
        assertEquals(expected, result);
    }

    @Test
    public void testByURLDirectlyDifferentPatterns(){
        String urlPattern1 = "http://www.example11.com/Pizza/$id";
        String urlPattern2 = "http://www.example11.com/bread/$id";
        DataSource expected = DataSource.getByURLPattern(urlPattern1);
        DataSource result = DataSource.getByURLPattern(urlPattern2);
        assertNotNull(result);
        assertFalse(expected.equals(result));
    }

    @Test
    public void testByURLDirectlyWithPostFix(){
        String urlPattern = "http://www.example12.com/$id/More";
        DataSource expected = DataSource.getByURLPattern(urlPattern);
        DataSource result = DataSource.getByURLPattern(urlPattern);
        assertEquals(expected, result);
    }

    @Test
    public void testByURLDirectlyDifferentPatternsWithPostFix(){
        String urlPattern1 = "http://www.example13.com/Pizza/$id/more";
        String urlPattern2 = "http://www.example13.com/bread/$id/more";
        DataSource expected = DataSource.getByURLPattern(urlPattern1);
        DataSource result = DataSource.getByURLPattern(urlPattern2);
        assertNotNull(result);
        assertFalse(expected.equals(result));
    }

    @Test
    public void testByURLDirectlyPatternsWithDifferentPostFix(){
        String urlPattern1 = "http://www.example14.com/$id/more";
        String urlPattern2 = "http://www.example14.com/$id/fluff";
        DataSource expected = DataSource.getByURLPattern(urlPattern1);
        DataSource result = DataSource.getByURLPattern(urlPattern2);
        assertNotNull(result);
        System.out.println(expected);
        System.out.println(result);
        assertFalse(expected.equals(result));
    }
}
