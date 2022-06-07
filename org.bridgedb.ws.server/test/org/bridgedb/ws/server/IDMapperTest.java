/* BridgeDb,
 * An abstraction layer for identifier mapping services, both local and online.
 *
 * Copyright 2006-2009  BridgeDb developers
 * Copyright 2012-2013  Christian Y. A. Brenninkmeijer
 * Copyright 2012-2013  OpenPhacts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bridgedb.ws.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.file.IDMapperText;
import org.bridgedb.ws.WSCoreInterface;
import org.bridgedb.ws.WSCoreMapper;
import org.bridgedb.ws.WSCoreService;
import org.bridgedb.ws.bean.CapabilitiesBean;
import org.bridgedb.ws.bean.DataSourcesBean;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.XrefExistsBean;
import org.bridgedb.ws.bean.XrefMapsBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.bridgedb.utils.BridgeDBException;


/**
 *
 * @author Christian
 */
@Tag("mysql")
public class IDMapperTest extends org.bridgedb.utils.IDMapperTest{
    
    @BeforeAll
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{
    	URL INTERFACE_TEST_FILE = IDMapperCapabilitiesTest.class.getResource("/interfaceTest.txt");
        assertNotNull("Can't find test-data/interfaceTest.txt", INTERFACE_TEST_FILE);
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE);
        WSCoreInterface  webService = new WSCoreService(inner);
        idMapper = new WSCoreMapper(webService);
        capabilities = idMapper.getCapabilities();  
        
    }

    @org.junit.jupiter.api.Test
    public void test() throws Exception {
    	DataSourceTxt.init();
        URL INTERFACE_TEST_FILE = IDMapperCapabilitiesTest.class.getResource("/interfaceTest2.txt");
        URL INTERFACE_TEST_FILE_EMPTY = IDMapperCapabilitiesTest.class.getResource("/interfaceTest3.txt");
        assertNotNull("Can't find test-data/interfaceTest2.txt", INTERFACE_TEST_FILE);
        assertNotNull("Can't find test-data/interfaceTest3.txt", INTERFACE_TEST_FILE_EMPTY);
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE);
        IDMapper empty = new IDMapperText(INTERFACE_TEST_FILE_EMPTY);
        WSCoreService  webService = new WSCoreService(inner);
        WSCoreService  webServiceEmpty = new WSCoreService(empty);
		
        List<String> id = new ArrayList<String>();
        id.add("YHR055C");
        List<String> scrCode = new ArrayList<String>();
        scrCode.add("En");
        List<String> targetCode = new ArrayList<String>();
        targetCode.add("L");
        
		DataSource.register("En", "Ensembl").asDataSource();
		DataSource.register("L", "Entrez Gene").asDataSource();
		
		Response respMapping = webService.mapID(id, scrCode, targetCode);
		XrefMapsBean xmb = (XrefMapsBean) respMapping.getEntity();
		assertFalse(xmb.isEmpty());
		
		Response respMappingJson = webService.mapIDJson(id, scrCode, targetCode);
		XrefMapsBean xmbj = (XrefMapsBean) respMappingJson.getEntity();
		assertFalse(xmbj.isEmpty());
		
		Response respSupportedDS = webServiceEmpty.getSupportedSrcDataSources();
		assertNull(respSupportedDS.getEntity());	
		
		Response respSupportedDSJson = webServiceEmpty.getSupportedSrcDataSourcesJson();
		assertNull(respSupportedDSJson.getEntity());
		
		Response respSupportedTgtDS = webServiceEmpty.getSupportedTgtDataSources();
		assertNull(respSupportedTgtDS.getEntity());	
		
		Response respSupportedTgtDSJson = webServiceEmpty.getSupportedTgtDataSourcesJson();
		assertNull(respSupportedTgtDSJson.getEntity());
		
    }
    
    @org.junit.jupiter.api.Test
    public void testXrefExists() throws Exception {
    	DataSourceTxt.init();
        URL INTERFACE_TEST_FILE = IDMapperCapabilitiesTest.class.getResource("/interfaceTest2.txt");
        assertNotNull("Can't find test-data/interfaceTest2.txt", INTERFACE_TEST_FILE);
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE);
        WSCoreService  webService = new WSCoreService(inner);
        List<String> id = new ArrayList<String>();
        id.add("YHR055C");
        List<String> scrCode = new ArrayList<String>();
        scrCode.add("En");
        List<String> targetCode = new ArrayList<String>();
        targetCode.add("L");
        DataSource.register("En", "Ensembl").asDataSource();
        DataSource.register("L", "Entrez Gene").asDataSource();
		
		Response respXrefExists = webService.xrefExists("YHR055C","En");
		XrefExistsBean xb = (XrefExistsBean) respXrefExists.getEntity();
		assertTrue(xb.getExists());
		
		Response respXrefExistsJson = webService.xrefExistsJson("YHR055C","En");
		XrefExistsBean xb2 = (XrefExistsBean) respXrefExistsJson.getEntity();
		assertTrue(xb2.getExists());
		
    }
    
    @org.junit.jupiter.api.Test
    public void testGetTgtDS() throws Exception {
    	DataSourceTxt.init();
        URL INTERFACE_TEST_FILE = IDMapperCapabilitiesTest.class.getResource("/interfaceTest2.txt");
        assertNotNull("Can't find test-data/interfaceTest2.txt", INTERFACE_TEST_FILE);
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE);
        WSCoreService  webService = new WSCoreService(inner);
        List<String> id = new ArrayList<String>();
        id.add("YHR055C");
        List<String> scrCode = new ArrayList<String>();
        scrCode.add("En");
        List<String> targetCode = new ArrayList<String>();
        targetCode.add("L");
        DataSource.register("En", "Ensembl").asDataSource();
        DataSource.register("L", "Entrez Gene").asDataSource();
		
		Response respSupportedTgtDataSources = webService.getSupportedTgtDataSources();
		DataSourcesBean ds = (DataSourcesBean) respSupportedTgtDataSources.getEntity();
		Set<DataSource> dsSet = ds.getDataSources();
		assertFalse(dsSet.isEmpty());
		
		Response respSupportedTgtDataSourcesJson = webService.getSupportedTgtDataSourcesJson();
		DataSourcesBean ds2 = (DataSourcesBean) respSupportedTgtDataSourcesJson.getEntity();
		Set<DataSource> dsSet2 = ds2.getDataSources();
		assertFalse(dsSet2.isEmpty());
    }	    
    
    @org.junit.jupiter.api.Test
	   public void testFreeSearchSupported() throws Exception {
    	DataSourceTxt.init();
        URL INTERFACE_TEST_FILE = IDMapperCapabilitiesTest.class.getResource("/interfaceTest2.txt");
        assertNotNull("Can't find test-data/interfaceTest2.txt", INTERFACE_TEST_FILE);
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE);
        WSCoreService  webService = new WSCoreService(inner);
        List<String> id = new ArrayList<String>();
        id.add("YHR055C");
        List<String> scrCode = new ArrayList<String>();
        scrCode.add("En");
        List<String> targetCode = new ArrayList<String>();
        targetCode.add("L");
        DataSource.register("En", "Ensembl").asDataSource();
        DataSource.register("L", "Entrez Gene").asDataSource();
		
		Response respFreeSearchSupported = webService.isFreeSearchSupported();
		FreeSearchSupportedBean fsb = (FreeSearchSupportedBean) respFreeSearchSupported.getEntity();
		assertFalse(fsb.getIsFreeSearchSupported());
		
		Response isFreeSearchSupportedJson = webService.isFreeSearchSupportedJson();
		FreeSearchSupportedBean fsbj = (FreeSearchSupportedBean) isFreeSearchSupportedJson.getEntity();
		assertFalse(fsbj.getIsFreeSearchSupported());

    }    
    
    @org.junit.jupiter.api.Test
	   public void testGetCapabilities() throws Exception {
    	DataSourceTxt.init();
        URL INTERFACE_TEST_FILE = IDMapperCapabilitiesTest.class.getResource("/interfaceTest2.txt");
        assertNotNull("Can't find test-data/interfaceTest2.txt", INTERFACE_TEST_FILE);
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE);
        WSCoreService  webService = new WSCoreService(inner);
        List<String> id = new ArrayList<String>();
        id.add("YHR055C");
        List<String> scrCode = new ArrayList<String>();
        scrCode.add("En");
        List<String> targetCode = new ArrayList<String>();
        targetCode.add("L");
        DataSource.register("En", "Ensembl").asDataSource();
        DataSource.register("L", "Entrez Gene").asDataSource();
        
		Response respGetPropertyNoContent= webService.getProperty("ID");
		assertNull(respGetPropertyNoContent.getEntity());
		
		Response respGetPropertyJsonNoContent= webService.getPropertyJson("ID");
		assertNull(respGetPropertyJsonNoContent.getEntity());
		
		Response respGetKeys= webService.getKeys();
		assertNull(respGetKeys.getEntity());
		
		Response respGetKeysJson= webService.getKeysJson();
		assertNull(respGetKeysJson.getEntity());
		
		Response respCapabilities= webService.getCapabilities();
		CapabilitiesBean cb = (CapabilitiesBean) respCapabilities.getEntity();
		assertNotNull(cb.getSupportedSrcDataSources());
		
		Response respCapabilitiesJson= webService.getCapabilitiesJson();
		CapabilitiesBean cbj = (CapabilitiesBean) respCapabilitiesJson.getEntity();
		assertNotNull(cbj.getSupportedSrcDataSources());
    }
    
    @org.junit.jupiter.api.Test
	   public void testMappingSupported() throws Exception {
    	DataSourceTxt.init();
        URL INTERFACE_TEST_FILE = IDMapperCapabilitiesTest.class.getResource("/interfaceTest2.txt");
        assertNotNull("Can't find test-data/interfaceTest2.txt", INTERFACE_TEST_FILE);
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE);
        WSCoreService  webService = new WSCoreService(inner);
        List<String> id = new ArrayList<String>();
        id.add("YHR055C");
        List<String> scrCode = new ArrayList<String>();
        scrCode.add("En");
        List<String> targetCode = new ArrayList<String>();
        targetCode.add("L");
        DataSource.register("En", "Ensembl").asDataSource();
        DataSource.register("L", "Entrez Gene").asDataSource();
        
		Response respMappingSupported = webService.isMappingSupported("En", "L");
		assertNotNull(respMappingSupported.getEntity());
		
		Response respMappingSupportedJson = webService.isMappingSupportedJson("En", "L");
		assertNotNull(respMappingSupportedJson.getEntity());
    }
    
    @org.junit.jupiter.api.Test
	   public void testExceptions() throws Exception {
    	DataSourceTxt.init();
        URL INTERFACE_TEST_FILE = IDMapperCapabilitiesTest.class.getResource("/interfaceTest2.txt");
        assertNotNull("Can't find test-data/interfaceTest2.txt", INTERFACE_TEST_FILE);
        IDMapper inner = new IDMapperText(INTERFACE_TEST_FILE);
        WSCoreService  webService = new WSCoreService(inner);
        List<String> id = new ArrayList<String>();
        List<String> idEmpty = new ArrayList<String>();
        id.add("YHR055C");
        id.add("YPR161C");
        List<String> scrCode = new ArrayList<String>();
        List<String> scrCodeEmpty = new ArrayList<String>();
        scrCode.add("En");
        List<String> targetCode = new ArrayList<String>();
        targetCode.add("L");
        DataSource.register("En", "Ensembl").asDataSource();
        DataSource.register("L", "Entrez Gene").asDataSource();
      
				
		Assertions.assertThrows(BridgeDBException.class,
				() -> {
					Response respMapping = webService.mapID(id, scrCode, targetCode);
				});
		
		Assertions.assertThrows(BridgeDBException.class,
				() -> {
					Response respMapping = webService.mapID(null, scrCode, targetCode);
				});
		Assertions.assertThrows(BridgeDBException.class,
				() -> {
					Response respMapping = webService.mapID(idEmpty, scrCode, targetCode);
				});
		Assertions.assertThrows(BridgeDBException.class,
				() -> {
					Response respMapping = webService.mapID(id, null, targetCode);
				});
		Assertions.assertThrows(BridgeDBException.class,
				() -> {
					Response respMapping = webService.mapID(id, scrCodeEmpty, targetCode);
				});
		
		Assertions.assertThrows(BridgeDBException.class,
				() -> {
					Response respMapping = webService.isMappingSupported(null, "L");
				});
		Assertions.assertThrows(BridgeDBException.class,
				() -> {
					Response respMapping = webService.isMappingSupported("En", null);
				});
    }    
}
