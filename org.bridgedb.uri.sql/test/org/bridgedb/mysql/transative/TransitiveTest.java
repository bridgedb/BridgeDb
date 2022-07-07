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

package org.bridgedb.mysql.transative;

import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.UriPatternType;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.sql.transative.DirectMapping;
import static org.bridgedb.uri.UriListenerTest.SYMETRIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.URIImpl;

/**
 * Tests the UriMapper interface (and by loading the UriListener interface)
 *
 * Should be passable by any implementation of UriMapper that has the test data loaded.
 * 
 * @author Christian
 */
@Tag("mysql")
public class TransitiveTest {
   
    private static SQLUriMapper sqlUriMapper;
    private static final String TEST_PREDICATE = "http://www.w3.org/2004/02/skos/core#exactMatch";
    private static final String TEST_PREDICATE2 = "http://www.example.com/different_predicate";
    private static final String TEST_JUSTIFICATION9 = "http://www.example.com/different_jutification";

    private static final String sysCodeA = "TransitiveTestA";
    private static final String sysCodeB = "TransitiveTestB";
    private static final String sysCodeC = "TransitiveTestC";
    private static final String sysCodeD = "TransitiveTestD";
    private static final String sysCodeE = "TransitiveTestE";
    private static final String sysCodeF = "TransitiveTestF";
    private static final String sysCodeX = "TransitiveTestX";
    private static final String sysCodeY = "TransitiveTestY";
           
    private static final DataSource dsA = DataSource.register(sysCodeA, sysCodeA).asDataSource();
    private static final DataSource dsB = DataSource.register(sysCodeB, sysCodeB).asDataSource();
    private static final DataSource dsC = DataSource.register(sysCodeC, sysCodeC).asDataSource();
    private static final DataSource dsD = DataSource.register(sysCodeD, sysCodeD).asDataSource();
    private static final DataSource dsE = DataSource.register(sysCodeE, sysCodeE).asDataSource();
    private static final DataSource dsF = DataSource.register(sysCodeF, sysCodeF).asDataSource();
    private static final DataSource dsX = DataSource.register(sysCodeX, sysCodeX).asDataSource();
    private static final DataSource dsY = DataSource.register(sysCodeY, sysCodeY).asDataSource();
   
    private static RegexUriPattern regexUriPatternA;
    private static RegexUriPattern regexUriPatternB;
    private static RegexUriPattern regexUriPatternC;
    private static RegexUriPattern regexUriPatternD;
    private static RegexUriPattern regexUriPatternE;
    private static RegexUriPattern regexUriPatternF;
    private static RegexUriPattern regexUriPatternX;
    private static RegexUriPattern regexUriPatternY;

    private static final String prefixA = "http://example.com/dsA#";
    private static final String prefixB = "http://example.com/dsB#";
    private static final String prefixC = "http://example.com/dsC#";
    private static final String prefixD = "http://example.com/dsD#";
    private static final String prefixE = "http://example.com/dsE#";
    private static final String prefixF = "http://example.com/dsF#";
    private static final String prefixX = "http://example.com/dsX#";
    private static final String prefixY = "http://example.com/dsY#";
    
    private static int mappingSetAB;
    private static int mappingSetAB2;
    private static int mappingSetAC;
    private static int mappingSetAD;
    private static int mappingSetAE;
    private static int mappingSetAF;
    private static int mappingSetBC;
    private static int mappingSetBC2;
    private static int mappingSetBC9;
    private static int mappingSetBD;
    private static int mappingSetBE;
    private static int mappingSetBF;
    private static int mappingSetCD;
    private static int mappingSetCD2;
    private static int mappingSetCD9;
    private static int mappingSetCE;
    private static int mappingSetCF;
    private static int mappingSetDE;
    private static int mappingSetDF;
    private static int mappingSetEE;
    private static int mappingSetEF;
    private static int mappingSetFF;
    private static int mappingSetAX;
    private static int mappingSetCX;
    private static int mappingSetAY;
    private static int mappingSetCY;
    
    
    @BeforeAll
    public static void setupIDMapper() throws BridgeDBException{
        TestSqlFactory.checkSQLAccess();
        ConfigReader.useTest();
        
        Lens testLens = LensTools.byId(Lens.TEST_LENS_NAME);
        testLens.addAllowedMiddleSource(dsA);
        testLens.addAllowedMiddleSource(dsB);
        testLens.addAllowedMiddleSource(dsC);
        testLens.addAllowedMiddleSource(dsD);
        testLens.addAllowedMiddleSource(dsE);
        //Do not add X and Y they should not be transative
        
        UriPattern pattern = UriPattern.register(prefixA + "$id", sysCodeA, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixB + "$id", sysCodeB, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixC + "$id", sysCodeC, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixD + "$id", sysCodeD, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixE + "$id", sysCodeE, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixF + "$id", sysCodeF, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixX + "$id", sysCodeX, UriPatternType.dataSourceUriPattern);
        pattern = UriPattern.register(prefixY + "$id", sysCodeY, UriPatternType.dataSourceUriPattern);
        
        sqlUriMapper = SQLUriMapper.createNew();

        regexUriPatternA = RegexUriPattern.factory(prefixA, "", sysCodeA);
        regexUriPatternB = RegexUriPattern.factory(prefixB, "", sysCodeB);
        regexUriPatternC = RegexUriPattern.factory(prefixC, "", sysCodeC);
        regexUriPatternD = RegexUriPattern.factory(prefixD, "", sysCodeD);
        regexUriPatternE = RegexUriPattern.factory(prefixE, "", sysCodeE);
        regexUriPatternF = RegexUriPattern.factory(prefixF, "", sysCodeF);
        regexUriPatternX = RegexUriPattern.factory(prefixX, "", sysCodeX);
        regexUriPatternY = RegexUriPattern.factory(prefixY, "", sysCodeY);
        loadData();
        
        sqlUriMapper = SQLUriMapper.getExisting();
    }
    
    public static void loadData() throws BridgeDBException{
        
        URI source = new URIImpl("http://example.com/TransitiveTest/AtoB");
        mappingSetAB = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternB, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/AtoB2");
        mappingSetAB2 = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE2, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternB, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/AtoC");
        mappingSetAC = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternC, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/AtoD");
        mappingSetAD = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternD, source);

        source = new URIImpl("http://example.com/TransitiveTest/AtoE");
        mappingSetAE = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternE, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/AtoF");
        mappingSetAF = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternF, source);

        source = new URIImpl("http://example.com/TransitiveTest/BtoC");
        mappingSetBC = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternC, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/BtoC2");
        mappingSetBC2 = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE2, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternC, source);

        source = new URIImpl("http://example.com/TransitiveTest/BtoC9");
        mappingSetBC9 = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                TEST_JUSTIFICATION9, TEST_JUSTIFICATION9, regexUriPatternC, source);

        source = new URIImpl("http://example.com/TransitiveTest/BtoD");
        mappingSetBD = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternD, source);

        source = new URIImpl("http://example.com/TransitiveTest/BtoE");
        mappingSetBE = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternE, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/BtoF");
        mappingSetBF = sqlUriMapper.registerMappingSet(regexUriPatternB, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternF, source);

        source = new URIImpl("http://example.com/TransitiveTest/CtoD");
        mappingSetCD = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternD, source);

        source = new URIImpl("http://example.com/TransitiveTest/CtoD2");
        mappingSetCD2 = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE2, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternD, source);

        source = new URIImpl("http://example.com/TransitiveTest/CtoD9");
        mappingSetCD9 = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                TEST_JUSTIFICATION9, TEST_JUSTIFICATION9, regexUriPatternD, source);

        source = new URIImpl("http://example.com/TransitiveTest/CtoE");
        mappingSetCE = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternE, source);

        source = new URIImpl("http://example.com/TransitiveTest/CtoF");
        mappingSetCF = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternF, source);

        source = new URIImpl("http://example.com/TransitiveTest/DtoE");
        mappingSetDE = sqlUriMapper.registerMappingSet(regexUriPatternD, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternE, source);

        source = new URIImpl("http://example.com/TransitiveTest/DtoF");
        mappingSetDF = sqlUriMapper.registerMappingSet(regexUriPatternD, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternF, source);

        source = new URIImpl("http://example.com/TransitiveTest/EtoE");
        mappingSetEE = sqlUriMapper.registerMappingSet(regexUriPatternE, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternE, source);

        source = new URIImpl("http://example.com/TransitiveTest/EtoF");
        mappingSetEF = sqlUriMapper.registerMappingSet(regexUriPatternE, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternF, source);

        source = new URIImpl("http://example.com/TransitiveTest/FtoF");
        mappingSetFF = sqlUriMapper.registerMappingSet(regexUriPatternF, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternF, source);

        source = new URIImpl("http://example.com/TransitiveTest/AtoX");
        mappingSetAX = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternX, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/CtoX");
        mappingSetCX = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternX, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/AtoY");
        mappingSetAY = sqlUriMapper.registerMappingSet(regexUriPatternA, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternY, source);
        
        source = new URIImpl("http://example.com/TransitiveTest/CtoY");
        mappingSetCY = sqlUriMapper.registerMappingSet(regexUriPatternC, TEST_PREDICATE, 
                Lens.getTestJustifictaion(), Lens.getTestJustifictaion(), regexUriPatternY, source);

        //A1 -> B1 -> C1 -> D1
        sqlUriMapper.insertUriMapping(prefixA+"1", prefixB+"1", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"1", prefixC+"1", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"1", prefixD+"1", mappingSetCD, SYMETRIC);

        //A2 -> B2 -> C2 -> D2 -> A2
        sqlUriMapper.insertUriMapping(prefixA+"2", prefixB+"2", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"2", prefixC+"2", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"2", prefixD+"2", mappingSetCD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"2", prefixD+"2", mappingSetAD, SYMETRIC);

        //A3a -> B3a -> C3 -> D3 -> A3b
        sqlUriMapper.insertUriMapping(prefixA+"3a", prefixB+"3", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"3", prefixC+"3", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"3", prefixD+"3", mappingSetCD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"3b", prefixD+"3", mappingSetAD, SYMETRIC);

        //A4 -> B4  A4 -> C4  A4 -> D4
        sqlUriMapper.insertUriMapping(prefixA+"4", prefixB+"4", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"4", prefixC+"4", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"4", prefixD+"4", mappingSetAD, SYMETRIC);

        //A5 -> B5a,B5b, C5a, c5b, D5a, D5b
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixB+"5a", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixB+"5b", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixC+"5a", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixC+"5b", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixD+"5a", mappingSetAD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"5", prefixD+"5b", mappingSetAD, SYMETRIC);

        //A6a -> B6a -> C6 -> B6b -> A6b
        sqlUriMapper.insertUriMapping(prefixA+"6a", prefixB+"6a", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"6b", prefixB+"6b", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"6a", prefixC+"6", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"6b", prefixC+"6", mappingSetBC, SYMETRIC);
        
        //A7a -> B7 -> A7b
        sqlUriMapper.insertUriMapping(prefixA+"7a", prefixB+"7", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"7b", prefixB+"7", mappingSetAB, SYMETRIC);

        //A8 -> E8a -> E8b
        sqlUriMapper.insertUriMapping(prefixA+"8", prefixE+"8a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"8a", prefixE+"8b", mappingSetEE, SYMETRIC);
        
        //A9a -> E9a -> E9b -A9b
        sqlUriMapper.insertUriMapping(prefixA+"9a", prefixE+"9a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"9a", prefixE+"9b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"9b", prefixE+"9b", mappingSetAE, SYMETRIC);
        
        //A10 -> E10a -> E10b -A10
        sqlUriMapper.insertUriMapping(prefixA+"10", prefixE+"10a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"10a", prefixE+"10b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"10", prefixE+"10b", mappingSetAE, SYMETRIC);
        
         //A11 -> E11a -> E11b - B11
        sqlUriMapper.insertUriMapping(prefixA+"11", prefixE+"11a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"11a", prefixE+"11b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"11", prefixE+"11b", mappingSetBE, SYMETRIC);
        
         // E12a -> E12b - A12a   E12b - A12b
        sqlUriMapper.insertUriMapping(prefixA+"12a", prefixE+"12b", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"12a", prefixE+"12b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"12b", prefixE+"12b", mappingSetAE, SYMETRIC);

         // A13a -> E13a   A13b - E13a - E13b - B13a  E13b - B13b
        sqlUriMapper.insertUriMapping(prefixA+"13a", prefixE+"13a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"13b", prefixE+"13a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"13a", prefixE+"13b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"13a", prefixE+"13b", mappingSetBE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"13b", prefixE+"13b", mappingSetBE, SYMETRIC);

         // A14a -> E14a   A14b - E14a - E14b - B14a  E14a - E14c - B14b
        sqlUriMapper.insertUriMapping(prefixA+"14a", prefixE+"14a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"14b", prefixE+"14a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"14a", prefixE+"14b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"14a", prefixE+"14b", mappingSetBE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"14a", prefixE+"14c", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"14b", prefixE+"14c", mappingSetBE, SYMETRIC);
        
        //A15a - E15a - E15b - B15a  A15b - E15c - E15d - B15b  E15a - E15d E15c - E15b
        sqlUriMapper.insertUriMapping(prefixA+"15a", prefixE+"15a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"15a", prefixE+"15b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"15a", prefixE+"15b", mappingSetBE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"15b", prefixE+"15c", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"15c", prefixE+"15d", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"15b", prefixE+"15d", mappingSetBE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"15c", prefixE+"15b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"15a", prefixE+"15d", mappingSetEE, SYMETRIC);

        //C16a - A16a  C16b - A16a - E16a - E16b - B16a    A16b - E16c - E16d - B16b - D16   E16a - E16d   E16c - E16b
        sqlUriMapper.insertUriMapping(prefixA+"16a", prefixC+"16a", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"16b", prefixC+"16b", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"16a", prefixE+"16a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"16a", prefixE+"16b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"16a", prefixE+"16b", mappingSetBE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"16a", prefixD+"16", mappingSetBD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"16b", prefixD+"16", mappingSetBD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"16b", prefixE+"16c", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"16c", prefixE+"16d", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"16b", prefixE+"16d", mappingSetBE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"16c", prefixE+"16b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"16a", prefixE+"16d", mappingSetEE, SYMETRIC);
        
        //C17 - A17a  E17a - E17b - B17 -D17
        sqlUriMapper.insertUriMapping(prefixA+"17", prefixC+"17", mappingSetAC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"17", prefixE+"17a", mappingSetAE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixE+"17a", prefixE+"17b", mappingSetEE, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"17", prefixD+"17", mappingSetBD, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"17", prefixE+"17b", mappingSetBE, SYMETRIC);
        
        //None transitive middle
        //A18 - X18  // X18 - C18
        sqlUriMapper.insertUriMapping(prefixA+"18", prefixX+"18", mappingSetAX, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"18", prefixX+"18", mappingSetCX, SYMETRIC);

        //A19 - X19  A19 - Y19 // X19 - C19  Y19 - C19
        sqlUriMapper.insertUriMapping(prefixA+"19", prefixX+"19", mappingSetAX, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"19", prefixX+"19", mappingSetCX, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"19", prefixY+"19", mappingSetAY, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"19", prefixY+"19", mappingSetCY, SYMETRIC);

        //A20 - B20 - C20 A20 - X20  // X20 - C20
        sqlUriMapper.insertUriMapping(prefixA+"20", prefixB+"20", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixA+"20", prefixX+"20", mappingSetAX, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"20", prefixC+"20", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"20", prefixX+"20", mappingSetCX, SYMETRIC);

        //Different predicate
        //A21 -> B21 -> C21 => D21
        sqlUriMapper.insertUriMapping(prefixA+"21", prefixB+"21", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"21", prefixC+"21", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"21", prefixD+"21", mappingSetCD2, SYMETRIC);

        //A22 -> B22 => C21 => D21
        sqlUriMapper.insertUriMapping(prefixA+"22", prefixB+"22", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"22", prefixC+"22", mappingSetBC2, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"22", prefixD+"22", mappingSetCD2, SYMETRIC);

        //Different justification
        //A23 -> B23 -> C23 => D23
        sqlUriMapper.insertUriMapping(prefixA+"23", prefixB+"23", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"23", prefixC+"23", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"23", prefixD+"23", mappingSetCD9, SYMETRIC);

        //A24 -> B24 => C24 => D24
        sqlUriMapper.insertUriMapping(prefixA+"24", prefixB+"24", mappingSetAB, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"24", prefixC+"24", mappingSetBC9, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"24", prefixD+"24", mappingSetCD9, SYMETRIC);

        //Both different predicate and justification
        //A25 ..> B25 -> C25 => D25
        sqlUriMapper.insertUriMapping(prefixA+"25", prefixB+"25", mappingSetAB2, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixB+"25", prefixC+"25", mappingSetBC, SYMETRIC);
        sqlUriMapper.insertUriMapping(prefixC+"25", prefixD+"25", mappingSetCD9, SYMETRIC);
        sqlUriMapper.closeInput();
}

    @org.junit.jupiter.api.Test
    public void testDirectMappings1AtoB() throws Exception{
        Reporter.println("DirectMappings1AtoB");
        IdSysCodePair source = new IdSysCodePair("1", sysCodeA);
        Set<DirectMapping> mappings = sqlUriMapper.getDirectMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size());
    }

    @org.junit.jupiter.api.Test
    public void testDirectMappings1BtoAC() throws Exception{
        Reporter.println("DirectMappings1BtoAC");
        IdSysCodePair source = new IdSysCodePair( "1", sysCodeB);
        Set<DirectMapping> mappings = sqlUriMapper.getDirectMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size());
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings1A() throws Exception{
        Reporter.println("TransitiveMappings1A");
        IdSysCodePair source = new IdSysCodePair("1", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size());
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings1C() throws Exception{
        Reporter.println("TransitiveMappings1C");
        IdSysCodePair source = new IdSysCodePair("1", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size());
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings2A() throws Exception{
        Reporter.println("TransitiveMappings2A");
        IdSysCodePair source = new IdSysCodePair("2", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size());
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings2C() throws Exception{
        Reporter.println("TransitiveMappings2C");
        IdSysCodePair source = new IdSysCodePair("2", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size());
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings3A() throws Exception{
        Reporter.println("TransitiveMappings3A");
        IdSysCodePair source = new IdSysCodePair("3a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //B3, C3, D3
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings3C() throws Exception{
        Reporter.println("TransitiveMappings3C");
        IdSysCodePair source = new IdSysCodePair("3", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(4, mappings.size()); //A3a, A3b, B3, C3, D3
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings4A() throws Exception{
        Reporter.println("TransitiveMappings4A");
        IdSysCodePair source = new IdSysCodePair("4", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //B4, C4, D4
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings4C() throws Exception{
        Reporter.println("TransitiveMappings4C");
        IdSysCodePair source = new IdSysCodePair("4", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //A4, B4, D4
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings6A() throws Exception{
        Reporter.println("TransitiveMappings6A");
        IdSysCodePair source = new IdSysCodePair("6a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //B6a, C6
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings6B() throws Exception{
        Reporter.println("TransitiveMappings6B");
        IdSysCodePair source = new IdSysCodePair("6a", sysCodeB);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A6a, C6
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings6C() throws Exception{
        Reporter.println("TransitiveMappings6C");
        IdSysCodePair source = new IdSysCodePair("6", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(4, mappings.size()); //A6a, A6b, B6a, B6b
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings7A() throws Exception{
        Reporter.println("TransitiveMappings7A");
        IdSysCodePair source = new IdSysCodePair("7a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size()); //B7
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings7B() throws Exception{
        Reporter.println("TransitiveMappings6B");
        IdSysCodePair source = new IdSysCodePair("7", sysCodeB);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A7a, A7b
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings8A() throws Exception{
        Reporter.println("TransitiveMappings8A");
        IdSysCodePair source = new IdSysCodePair("8", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //8E1, 8E2
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings8Ea() throws Exception{
        Reporter.println("TransitiveMappings8E1");
        IdSysCodePair source = new IdSysCodePair("8a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A8, E8b
    }
    
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings8Eb() throws Exception{
        Reporter.println("TransitiveMappings8E1");
        IdSysCodePair source = new IdSysCodePair("8b", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A8, E8a
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings9A() throws Exception{
        Reporter.println("TransitiveMappings9A");
        IdSysCodePair source = new IdSysCodePair("9a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //9Ab, 9E1, 9E2
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings9E() throws Exception{
        Reporter.println("TransitiveMappings9E");
        IdSysCodePair source = new IdSysCodePair("9a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //A9a, A9b,, E8b
    }
    
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings10A() throws Exception{
        Reporter.println("TransitiveMappings10A");
        IdSysCodePair source = new IdSysCodePair("10", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //10E1, 10E2
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings10E() throws Exception{
        Reporter.println("TransitiveMappings10E");
        IdSysCodePair source = new IdSysCodePair("10a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A10, E8b
    }

    //A11 -> E11a -> E11b - B11
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings11A() throws Exception{
        Reporter.println("TransitiveMappings11A");
        IdSysCodePair source = new IdSysCodePair("11", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //B11 E11a, E11b
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings11E() throws Exception{
        Reporter.println("TransitiveMappings11E");
        IdSysCodePair source = new IdSysCodePair("11a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); // A11, B11, E11b
    }

     // E12a -> E12b - A12a   E12b - A12b
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings12A() throws Exception{
        Reporter.println("TransitiveMappings12A");
        IdSysCodePair source = new IdSysCodePair("12a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //E12a, E12b
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings12E() throws Exception{
        Reporter.println("TransitiveMappings12E");
        IdSysCodePair source = new IdSysCodePair("12a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); // A12a, A12b, E12b
    }
    
    // A13a -> E13a   A13b - E13a - E13b - B13a  E13b - B13b
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings13A() throws Exception{
        Reporter.println("TransitiveMappings13A");
        IdSysCodePair source = new IdSysCodePair("13a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(4, mappings.size()); //E13a, E13b B13a B13b
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings13E() throws Exception{
        Reporter.println("TransitiveMappings13E");
        IdSysCodePair source = new IdSysCodePair("13a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(5, mappings.size()); //A13b A13b, E13b B13a B13bb
    }
    
    // A14a -> E14a   A14b - E14a - E14b - B14a  E14a - E14c - B14b
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings14A() throws Exception{
        Reporter.println("TransitiveMappings14A");
        IdSysCodePair source = new IdSysCodePair("14a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(5, mappings.size()); //E14a, E14b E14c B13a B13b
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings14Ea() throws Exception{
        Reporter.println("TransitiveMappings14Ea");
        IdSysCodePair source = new IdSysCodePair("14a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(6, mappings.size()); //A13b A13b, E13b E13b B13a B13bb
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings14Eb() throws Exception{
        Reporter.println("TransitiveMappings14Eb");
        IdSysCodePair source = new IdSysCodePair("14b", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(4, mappings.size()); //A13b A13b, E13a B13a 
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings14B() throws Exception{
        Reporter.println("TransitiveMappings14B");
        IdSysCodePair source = new IdSysCodePair("14a", sysCodeB);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(4, mappings.size()); //A13b A13b, E13a E13b 
    }
    
     //A14a - E15a - E15b - B15a  A15b - E15c - E15d - B15b  E15a - E15d E15c - E15b
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings15A() throws Exception{
        Reporter.println("TransitiveMappings15A");
        IdSysCodePair source = new IdSysCodePair("15a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(5, mappings.size()); //E14a, E14b E14d B13a B13b
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings15Ea() throws Exception{
        Reporter.println("TransitiveMappings15Ea");
        IdSysCodePair source = new IdSysCodePair("15a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(5, mappings.size()); //A15a E13b E15b B15a B15b
    }
    
    //C16a - A16a  C16b - A16a - E16a - E16b - B16a    A16b - E16c - E16d - B16b - D16   E16a - E16d   E16c - E16b
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings16A() throws Exception{
        Reporter.println("TransitiveMappings16A");
        IdSysCodePair source = new IdSysCodePair("16a", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(7, mappings.size()); //C16a E16a, E16b E16d B16a B16b D16
    }

   @org.junit.jupiter.api.Test
    public void testTransitiveMappings16E() throws Exception{
        Reporter.println("TransitiveMappings16E");
        IdSysCodePair source = new IdSysCodePair("16a", sysCodeE);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(7, mappings.size()); //C16a A16b E16b E16b B16a B16b D16
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings16C() throws Exception{
        Reporter.println("TransitiveMappings16C");
        IdSysCodePair source = new IdSysCodePair("16a", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(7, mappings.size()); //A16a, E16a E16b E16d B16a B16b D16
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings16D() throws Exception{
        Reporter.println("TransitiveMappings16D");
        IdSysCodePair source = new IdSysCodePair("16", sysCodeD);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(10, mappings.size()); //C16a C16b A16a A16b E16a E16b E16c E16d B16a B16b 
    }
 
    //C17 - A17 - E17a - E17b - B17 -D17
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings17A() throws Exception{
        Reporter.println("TransitiveMappings17A");
        IdSysCodePair source = new IdSysCodePair("17", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(5, mappings.size()); //C17 E17a E17b B17 D17
    }
  
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings17C() throws Exception{
        Reporter.println("TransitiveMappings17C");
        IdSysCodePair source = new IdSysCodePair("17", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(5, mappings.size()); //CA7 E17a E17b B17 D17
    }
    
    //A18 - X18  // X18 - C18
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings18A() throws Exception{
        Reporter.println("TransitiveMappings18A");
        IdSysCodePair source = new IdSysCodePair("18", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size()); //X18 ONLY
    }

    //A19 - X19  A19 - Y19 // X19 - C19  Y19 - C19
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings19A() throws Exception{
        Reporter.println("TransitiveMappings19A");
        IdSysCodePair source = new IdSysCodePair("19", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //X19 and Y19 ONLY
    }

    //A20 - B20 - C20 A20 - X20  // X20 - C20
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings20A() throws Exception{
        Reporter.println("TransitiveMappings20A");
        IdSysCodePair source = new IdSysCodePair("20", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //B20 C20 X20
    }

    //A21 -> B21 -> C21 => D21
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings21A() throws Exception{
        Reporter.println("TransitiveMappings21A");
        IdSysCodePair source = new IdSysCodePair("21", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //B21 C21
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings21B() throws Exception{
        Reporter.println("TransitiveMappings21B");
        IdSysCodePair source = new IdSysCodePair("21", sysCodeB);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A21 C21
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings21C() throws Exception{
        Reporter.println("TransitiveMappings21C");
        IdSysCodePair source = new IdSysCodePair("21", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //A21 B21 C21
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings21D() throws Exception{
        Reporter.println("TransitiveMappings21D");
        IdSysCodePair source = new IdSysCodePair("21", sysCodeD);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size()); //C21
    }

    //A22 -> B22 => C21 => D21
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings22A() throws Exception{
        Reporter.println("TransitiveMappings22A");
        IdSysCodePair source = new IdSysCodePair("22", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size()); //B22
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings22B() throws Exception{
        Reporter.println("TransitiveMappings22B");
        IdSysCodePair source = new IdSysCodePair("22", sysCodeB);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(3, mappings.size()); //A22 C22 D22
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings22C() throws Exception{
        Reporter.println("TransitiveMappings22C");
        IdSysCodePair source = new IdSysCodePair("22", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //B21 D21
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings22D() throws Exception{
        Reporter.println("TransitiveMappings22D");
        IdSysCodePair source = new IdSysCodePair("22", sysCodeD);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //B22 C22
    }

    //A23 -> B23 -> C23 => D23
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings23A() throws Exception{
        Reporter.println("TransitiveMappings23A");
        IdSysCodePair source = new IdSysCodePair("23", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //B23 C23
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings23B() throws Exception{
        Reporter.println("TransitiveMappings23B");
        IdSysCodePair source = new IdSysCodePair("23", sysCodeB);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A23 C23
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings23C() throws Exception{
        Reporter.println("TransitiveMappings23C");
        IdSysCodePair source = new IdSysCodePair("23", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A23 B23 
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings23D() throws Exception{
        Reporter.println("TransitiveMappings23D");
        IdSysCodePair source = new IdSysCodePair("23", sysCodeD);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(0, mappings.size()); //None due to Lens
    }

    //A24 -> B24 => C24 => D24
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings24A() throws Exception{
        Reporter.println("TransitiveMappings24A");
        IdSysCodePair source = new IdSysCodePair("24", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size()); //B24
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings24B() throws Exception{
        Reporter.println("TransitiveMappings24B");
        IdSysCodePair source = new IdSysCodePair("24", sysCodeB);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size()); //A24 
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings24C() throws Exception{
        Reporter.println("TransitiveMappings24C");
        IdSysCodePair source = new IdSysCodePair("24", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(0, mappings.size()); //None due to Lens
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings24D() throws Exception{
        Reporter.println("TransitiveMappings24D");
        IdSysCodePair source = new IdSysCodePair("24", sysCodeD);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(0, mappings.size()); //None due to Lens
    }

    //A25 ..> B24 -> C24 => D24
    @org.junit.jupiter.api.Test
    public void testTransitiveMappings25A() throws Exception{
        Reporter.println("TransitiveMappings25A");
        IdSysCodePair source = new IdSysCodePair("25", sysCodeA);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size()); //B25
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings25B() throws Exception{
        Reporter.println("TransitiveMappings25B");
        IdSysCodePair source = new IdSysCodePair("25", sysCodeB);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(2, mappings.size()); //A24 C24 
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings25CAll() throws Exception{
        Reporter.println("TransitiveMappings25CAll");
        IdSysCodePair source = new IdSysCodePair("25", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.ALL_LENS_NAME);
        assertEquals(2, mappings.size()); //B24 D24
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings25C() throws Exception{
        Reporter.println("TransitiveMappings25C");
        IdSysCodePair source = new IdSysCodePair("25", sysCodeC);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(1, mappings.size()); //B24 
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings25DAll() throws Exception{
        Reporter.println("TransitiveMappings25DAll");
        IdSysCodePair source = new IdSysCodePair("25", sysCodeD);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.ALL_LENS_NAME);
        assertEquals(1, mappings.size()); //C25
    }

    @org.junit.jupiter.api.Test
    public void testTransitiveMappings25D() throws Exception{
        Reporter.println("TransitiveMappings25D");
        IdSysCodePair source = new IdSysCodePair("25", sysCodeD);
        Set<? extends Mapping> mappings = sqlUriMapper.getTransitiveMappings(source, Lens.TEST_LENS_NAME);
        assertEquals(0, mappings.size());  //None due to Lens
    }



}
