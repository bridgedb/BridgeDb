// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Egon Willighagen <egonw@users.sf.net>
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
package org.bridgedb;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataSourceTest {

	@Test
	public void testAsDataSource() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .asDataSource();
		Assert.assertNotNull(source);
	}

	@Test
	public void testBuilding() {
		DataSource source = DataSource.register("X", "Affymetrix").asDataSource();
		Assert.assertEquals("X", source.getSystemCode());
		Assert.assertEquals("Affymetrix", source.getFullName());
	}

	@Test
	public void testBuildingMainUrl() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .mainUrl("http://www.affymetrix.com")
		    .asDataSource();
		Assert.assertEquals("http://www.affymetrix.com", source.getMainUrl());
	}

	@Test
	public void testBuildingType() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .type("probe")
		    .asDataSource();
		Assert.assertEquals("probe", source.getType());
		Assert.assertFalse(source.isMetabolite());
	}

	@Test
	public void testBuildingPrimary() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .primary(false)
		    .asDataSource();
		Assert.assertFalse(source.isPrimary());
		source = DataSource.register("X", "Affymetrix")
			.primary(true)
			.asDataSource();
		Assert.assertTrue(source.isPrimary());
	}

	@Test
	public void testBuildingMetabolite() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .type("metabolite")
		    .asDataSource();
		Assert.assertEquals("metabolite", source.getType());
		Assert.assertTrue(source.isMetabolite());
	}

	@Test
	public void testAsDataSource() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .asDataSource();
		Assert.assertNotNull(source);
	}

	@Test
	public void testBuilding() {
		DataSource source = DataSource.register("X", "Affymetrix").asDataSource();
		Assert.assertEquals("X", source.getSystemCode());
		Assert.assertEquals("Affymetrix", source.getFullName());
	}

	@Test
	public void testBuildingMainUrl() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .mainUrl("http://www.affymetrix.com")
		    .asDataSource();
		Assert.assertEquals("http://www.affymetrix.com", source.getMainUrl());
	}

	@Test
	public void testBuildingType() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .type("probe")
		    .asDataSource();
		Assert.assertEquals("probe", source.getType());
		Assert.assertFalse(source.isMetabolite());
	}

	@Test
	public void testBuildingPrimary() {
		DataSource source = DataSource.register("X", "Affymetrix")
		    .primary(false)
		    .asDataSource();
		Assert.assertFalse(source.isPrimary());
		source = DataSource.register("X", "Affymetrix")
			.primary(true)
			.asDataSource();
		Assert.assertTrue(source.isPrimary());
	}

	@Test
	public void testBuildingMetabolite() {
		DataSource source = DataSource.register("F", "MetaboLoci")
		    .type("metabolite")
		    .asDataSource();
		Assert.assertEquals("metabolite", source.getType());
		Assert.assertTrue(source.isMetabolite());
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
    public void testGetByNameSpace() throws Exception {
        String nameSpace= "http://www.example1.com/";
        String urlProfile = "http://www.example1.com/$id";
        DataSource expResult = DataSource.register("test1", "test1").nameSpace(nameSpace).asDataSource();
        DataSource result = DataSource.getByURL(urlProfile);
        assertEquals(expResult, result);
        result = DataSource.getByNameSpace(nameSpace);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testXrefByURLDifferentURL() throws Exception {
        String urlProfile = "http://www.example2.com/$id";
        String url = "http://www.example2.com/12345";
        DataSource dataSource = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        Xref expResult = new Xref("12345", dataSource);
        Xref result = DataSource.uriToXref(url);
        assertEquals(expResult, result);
        String newURL = result.getUrl();
        assertEquals (url, newURL);
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
        assertTrue(expResult == result);
        expResult = DataSource.register("test1", "test1").urlPattern(urlProfile2).asDataSource();
        result = DataSource.getByURL(url1);
        //there can be a Datasource by Url1 but it can not be the "test1" one.
        assertNotNull(result);
        assertTrue(expResult != result);
        result = DataSource.getByURL(url2);
        assertTrue(expResult == result);
    }
    
    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testXrefByURLChangedPattern() throws Exception {
        String urlProfile1 = "http://www.example3a.com/Pizza/$id";
        String urlProfile2 = "http://www.example3a.com/Bread/$id";
        String url1 = "http://www.example3a.com/Pizza/12345";
        String url2 = "http://www.example3a.com/Bread/12345";
        DataSource dataSource = DataSource.register("test1", "test1").urlPattern(urlProfile1).asDataSource();
        Xref expResult = new Xref("12345", dataSource);
        Xref result = DataSource.uriToXref(url1);
        assertEquals(expResult, result);
        assertEquals(url1, result.getUrl());
        DataSource.register("test1", "test1").urlPattern(urlProfile2);
        result = DataSource.uriToXref(url1);
        assertEquals(url1, result.getUrl());
        result = DataSource.uriToXref(url2);
        assertEquals(expResult, result);
        assertEquals(url2, result.getUrl());
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
    public void testXrefByURLDifferentURWithPostFix() throws Exception {
        String urlProfile = "http://www.example6.com/$id/more";
        String url = "http://www.example6.com/12345/more";
        DataSource dataSource = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        Xref expResult = new Xref("12345", dataSource);
        Xref result = DataSource.uriToXref(url);
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
    public void testXrefByURLChangedPatternWithPostFix() throws Exception {
        String urlProfile1 = "http://www.example7a.com/Pizza/$id/more";
        String urlProfile2 = "http://www.example7a.com/Bread/$id/more";
        String url1 = "http://www.example7a.com/Pizza/12345/more";
        String url2 = "http://www.example7a.com/Bread/12345/more";
        DataSource dataSource = DataSource.register("test1", "test1").urlPattern(urlProfile1).asDataSource();
        Xref expResult = new Xref("12345", dataSource);
        Xref result = DataSource.uriToXref(url1);
        assertEquals(expResult, result);
        assertEquals(url1, result.getUrl());
        DataSource.register("test1", "test1").urlPattern(urlProfile2);
        result = DataSource.uriToXref(url1);
        assertEquals(url1, result.getUrl());
        result = DataSource.uriToXref(url2);
        assertEquals(expResult, result);
        assertEquals(url2, result.getUrl());
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
        assertFalse(expected.equals(result));
    }
    
    public void testNameSpace(){
       String nameSpace = "http://www.example15.com/";
       DataSource expResult = DataSource.register("test1", "test1").nameSpace(nameSpace).asDataSource();
       DataSource result =  DataSource.getByNameSpace(nameSpace);
       assertEquals(expResult, result);
       result =  DataSource.getByURL(nameSpace + "1234");
       assertEquals(expResult, result);
       result =  DataSource.getByURLPattern(nameSpace + "$1d");
       assertEquals(expResult, result);
    }

    public void testByNameSpace(){
       String nameSpace = "http://www.example16.com/";
       DataSource expResult = DataSource.getByNameSpace(nameSpace);
       DataSource result =  DataSource.getByNameSpace(nameSpace);
       assertEquals(expResult, result);
       result =  DataSource.getByURL(nameSpace + "1234");
       assertEquals(expResult, result);
       result =  DataSource.getByURLPattern(nameSpace + "$1d");
       assertEquals(expResult, result);
    }

}
