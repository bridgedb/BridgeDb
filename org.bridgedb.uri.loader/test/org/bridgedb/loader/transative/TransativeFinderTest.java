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
package org.bridgedb.loader.transative;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.UriPatternType;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.uri.Lens;
import org.bridgedb.uri.loader.LinksetListener;
import org.bridgedb.uri.loader.transative.TransativeFinder;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class TransativeFinderTest {
 
    SQLUriMapper mapper;
   
    @Before
    public void testLoader() throws BridgeDBException, IOException, OpenRDFException, FileNotFoundException {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
//        linksetLoader = new LinksetLoader();
//        linksetLoader.clearExistingData( StoreType.TEST);  
        setupPattern("TransativeTestA", "http://www.example.com/DS_A/$id");
        setupPattern("TransativeTestB", "http://www.example.com/DS_B/$id");
        setupPattern("TransativeTestC", "http://www.example.com/DS_C/$id");
        setupPattern("TransativeTestD", "http://www.example.com/DS_D/$id");
        setupPattern("TransativeTestE", "http://www.example.com/DS_E/$id");
        setupPattern("TransativeTestF", "http://www.example.com/DS_F/$id");
        mapper = SQLUriMapper.createNew();
 	}

    private void setupPattern (String name, String pattern) throws BridgeDBException{
        DataSource dataSource = DataSource.register(name, name)
                .urlPattern(pattern)
                .asDataSource();
        TransativeFinder.addAcceptableVai(dataSource);
        UriPattern uriPattern = UriPattern.register(pattern, name, UriPatternType.dataSourceUriPattern);
    }
    
    protected void load(String path) throws BridgeDBException{
        LinksetListener listener = new LinksetListener(mapper);
        File file = new File(path);
        URI predicate = new URIImpl("http://www.bridgedb.org/test#testPredicate");
        String justification = "http://www.bridgedb.org/test#justification1";
        listener.parse(file, predicate, justification);
    }
    
    @Test
	public void testFinder1() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder1");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleEToD.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToC.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToD.ttl");
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.getAllLens());
        assertEquals(20, results.getNumberOfMappingSets());
        Reporter.println("testFinder Done");
	}
	
    @Test
 	public void testFinder2() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder2");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToC.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToD.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleEToD.ttl");
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.getAllLens());
        assertEquals(20, results.getNumberOfMappingSets());
        Reporter.println("testFinder2 Done");
	}

    @Test
 	public void testFinder3() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder3");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleBToC.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleEToD.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleCToD.ttl");
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.getAllLens());
        assertEquals(20, results.getNumberOfMappingSets());
        Reporter.println("testFinder3Done");
	}

    @Test
 	public void testFinder4() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder4");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleBToC.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleEToD.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleEToF.ttl");
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        load("../org.bridgedb.uri.loader/test-data/sampleCToD.ttl");
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.getAllLens());
        assertEquals(30, results.getNumberOfMappingSets());
        Reporter.println("testFinder4Done");
	}

    @Test
 	public void testFinder5() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder5");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToA.ttl");
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.getAllLens());
        assertEquals(8, results.getNumberOfMappingSets());
        Reporter.println("testFinder5Done");
	}

    @Test
    public void testFinder6() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder6");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToA.ttl");
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        load("../org.bridgedb.uri.loader/test-data/sampleAToC.ttl");
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.getAllLens());
        assertEquals(16, results.getNumberOfMappingSets());
        Reporter.println("testFinder6Done");
	}

    @Test
 	public void testFinder7() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder7");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToA.ttl");
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        load("../org.bridgedb.uri.loader/test-data/sampleBToC.ttl");
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.getAllLens());
        assertEquals(18, results.getNumberOfMappingSets()); //14 is correct as coded but should we do C -> C'?
        Reporter.println("testFinder7Done");
	}
    
    @Test
 	public void testFinder8() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder8");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToA.ttl");
        load("../org.bridgedb.uri.loader/test-data/sampleAToA_1.ttl");
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.getAllLens());
        assertEquals(14, results.getNumberOfMappingSets());
        Reporter.println("testFinder8Done");
	}

}
