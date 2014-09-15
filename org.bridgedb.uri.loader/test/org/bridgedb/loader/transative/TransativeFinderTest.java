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
import org.bridgedb.sql.transative.ExtendableTransitiveChecker;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.loader.LinksetListener;
import org.bridgedb.uri.loader.transative.TransativeFinder;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
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
    static final boolean SYMETRIC = true;
    
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
        ExtendableTransitiveChecker.addAcceptableVai(dataSource);
        UriPattern uriPattern = UriPattern.register(pattern, name, UriPatternType.dataSourceUriPattern);
    }
    
    protected void load(String path, boolean symetric) throws BridgeDBException{
        LinksetListener listener = new LinksetListener(mapper);
        File file = new File(path);
        URI predicate = new URIImpl("http://www.bridgedb.org/test#testPredicate");
        String justification = "http://www.bridgedb.org/test#justification1";
        listener.parse(file, predicate, justification, symetric);
    }
    
    @Test
	public void testFinder1() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder1");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleEToD.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToC.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToD.ttl",SYMETRIC);
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        assertEquals(20, results.getNumberOfMappingSets());
        Reporter.println("testFinder Done");
	}
	
    @Test
 	public void testFinder2() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder2");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToC.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToD.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleEToD.ttl",SYMETRIC);
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        assertEquals(20, results.getNumberOfMappingSets());
        Reporter.println("testFinder2 Done");
	}

    @Test
 	public void testFinder3() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder3");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleBToC.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleEToD.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleCToD.ttl",SYMETRIC);
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        assertEquals(20, results.getNumberOfMappingSets());
        Reporter.println("testFinder3Done");
	}

    @Test
 	public void testFinder4() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder4");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleBToC.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleEToD.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleEToF.ttl",SYMETRIC);
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        load("../org.bridgedb.uri.loader/test-data/sampleCToD.ttl",SYMETRIC);
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        assertEquals(30, results.getNumberOfMappingSets());
        Reporter.println("testFinder4Done");
	}

    @Test
 	public void testFinder5() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder5");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToA.ttl", !SYMETRIC);
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        //A <-> B (2) / A -> A' (3) 
        // B -> A -> A' (4) 
        // A -> A' -> B (3)
        // B -> A -> A' -> B
        assertEquals(6, results.getNumberOfMappingSets());
        Reporter.println("testFinder5Done");
	}

    @Test
    public void testFinder6() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder6");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToA.ttl",!SYMETRIC);
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        load("../org.bridgedb.uri.loader/test-data/sampleAToC.ttl",SYMETRIC);
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        //A <-> B (2) / A <-> C (8)  / A -> A' (3) 
        //B <-> A <-> C (10)
        // B -> A -> A' (4) / C -> A -> A' (11)
        // A -> A' -> B (5) / A -> A' -> C (12) 
        // B -> A -> A' -> B (6) / B -> A -> A' -> C (13) / B -> A -> A' / C -> A -> A' -> B (14) / C -> A -> A' -> C
        assertEquals(15, results.getNumberOfMappingSets());
        Reporter.println("testFinder6Done");
	}

    @Test
    public void testFinder7() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder7");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToA.ttl",!SYMETRIC);
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        load("../org.bridgedb.uri.loader/test-data/sampleBToC.ttl",SYMETRIC);
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        //A <-> B (2) / B <-> C (8) / A -> A' (3) 
        // A <-> B <-> C (10)
        // B -> A -> A' (4) / C -> B -> A -> A' (11)
        // A -> A' -> B (5) / A -> A' -> B -> C (12) 
        // B-> A -> A' -> B (6) / B-> A -> A' -> B -> C (13) / C -> B -> A -> A' -> B (14) / C -> B-> A -> A' -> B -> C (15) 
        assertEquals(15, results.getNumberOfMappingSets()); //14 is correct as coded but should we do C -> C'?
        Reporter.println("testFinder7Done");
	}
    
    @Test
    @Ignore
 	public void testFinder8() throws BridgeDBException, RDFHandlerException, IOException {	
        Reporter.println("testFinder8");
        load("../org.bridgedb.uri.loader/test-data/sampleAToB.ttl",SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToA.ttl",!SYMETRIC);
        load("../org.bridgedb.uri.loader/test-data/sampleAToA_1.ttl",!SYMETRIC);
        TransativeFinder transativeFinder = new TransativeFinder();
        transativeFinder.UpdateTransative();
        OverallStatistics results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        assertEquals(8, results.getNumberOfMappingSets());
        // A <-> B / A -> A' / A -> A''
        // none
        // B -> A -> A' / B -> A -> A''
        // B -> A -> A' -> B / B -> A -> A'' -> B
        transativeFinder.UpdateTransative();
        results = mapper.getOverallStatistics(Lens.ALL_LENS_NAME);
        Reporter.println("testFinder8Done");
	}

}
