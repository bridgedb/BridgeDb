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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPattern() throws Exception {
        String urlProfile = "http://www.example.com/$id";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource result = DataSource.getByURLPattern(urlProfile);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPatternDifferentURL() throws Exception {
        String urlProfile = "http://www.example.com/$id";
        String url = "http://www.example.com/12345";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource result = DataSource.getByURLPattern(url);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPatternChangedPattern() throws Exception {
        String urlProfile1 = "http://www.example.com/Pizza/$id";
        String urlProfile2 = "http://www.example.com/Bread/$id";
        String url1 = "http://www.example.com/Pizza/12345";
        String url2 = "http://www.example.com/Bread/12345";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile1).asDataSource();
        DataSource result = DataSource.getByURLPattern(url1);
        assertEquals(expResult, result);
        expResult = DataSource.register("test1", "test1").urlPattern(urlProfile2).asDataSource();
        result = DataSource.getByURLPattern(url1);
        assertNull(result);
        result = DataSource.getByURLPattern(url2);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPatternSeveralURL() throws Exception {
        String urlProfile = "http://www.example.com/$id";
        String url = "http://www.example.com/12345";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource.register("testx1", "testx1").urlPattern("http://www.example.com/Pizza#$id").asDataSource();
        DataSource.register("testx2", "testx2").urlPattern("http://www.example.com/Bread#$id").asDataSource();
        DataSource.register("testx3", "testx3").urlPattern("http://www.example.com/Pizza/$id").asDataSource();
        DataSource.register("testx4", "testx4").urlPattern("http://www.example.com/Pizza:$id").asDataSource();
        DataSource result = DataSource.getByURLPattern(url);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test(expected = IDMapperException.class)
    public void testByURLPatternNoDuplicates() throws Exception {
        String urlProfile = "http://www.example.com/$id";
        String url = "http://www.example.com/12345";
        DataSource.register("testx1", "testx1").urlPattern("http://www.example.com/Pizza#$id").asDataSource();
        DataSource.register("testx2", "testx2").urlPattern("http://www.example.com/Pizza#$id").asDataSource();
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPatternWithPostFix() throws Exception {
        String urlProfile = "http://www.example.com/$id/more";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource result = DataSource.getByURLPattern(urlProfile);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPatternDifferentURWithPostFix() throws Exception {
        String urlProfile = "http://www.example.com/$id/more";
        String url = "http://www.example.com/12345/more";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource result = DataSource.getByURLPattern(url);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPatternChangedPatternWithPostFix() throws Exception {
        String urlProfile1 = "http://www.example.com/Pizza/$id/more";
        String urlProfile2 = "http://www.example.com/Bread/$id/more";
        String url1 = "http://www.example.com/Pizza/12345/more";
        String url2 = "http://www.example.com/Bread/12345/more";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile1).asDataSource();
        DataSource result = DataSource.getByURLPattern(url1);
        assertEquals(expResult, result);
        expResult = DataSource.register("test1", "test1").urlPattern(urlProfile2).asDataSource();
        result = DataSource.getByURLPattern(url1);
        assertNull(result);
        result = DataSource.getByURLPattern(url2);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test
    public void testGetByURLPatternSeveralURLWithPostFix() throws Exception {
        String urlProfile = "http://www.example.com/$id/more";
        String url = "http://www.example.com/12345/more";
        DataSource expResult = DataSource.register("test1", "test1").urlPattern(urlProfile).asDataSource();
        DataSource.register("testy1", "testy1").urlPattern("http://www.example.org/Pizza#$id/more").asDataSource();
        DataSource.register("testy2", "testy2").urlPattern("http://www.example.org/Bread#$id/more").asDataSource();
        DataSource.register("testy3", "testy3").urlPattern("http://www.example.org/$id#more").asDataSource();
        DataSource.register("testy4", "testy4").urlPattern("http://www.example.org/$id/more").asDataSource();
        DataSource.register("testy5", "testy5").urlPattern("http://www.example.org/$id:more").asDataSource();
        DataSource result = DataSource.getByURLPattern(url);
        assertEquals(expResult, result);
    }

    /**
     * Test of getByURLPattern method, of class DataSource.
     */
    @Test(expected = IDMapperException.class)
    public void testByURLPatternNoDuplicatesWithPostFix() throws Exception {
        String urlProfile = "http://www.example.com/$id";
        String url = "http://www.example.com/12345";
        DataSource.register("testx1", "testx1").urlPattern("http://www.example.com/Pizza#$id/more").asDataSource();
        DataSource.register("testx2", "testx2").urlPattern("http://www.example.com/Pizza#$id/more").asDataSource();
    }

}
