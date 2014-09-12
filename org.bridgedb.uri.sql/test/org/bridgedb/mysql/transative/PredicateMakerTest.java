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
package org.bridgedb.mysql.transative;

import org.bridgedb.sql.transative.PredicateMaker;
import org.bridgedb.rdf.constants.SkosConstants;
import org.bridgedb.rdf.constants.OWLConstants;
import org.bridgedb.rdf.constants.OboConstants;
import java.util.ArrayList;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.rio.RDFHandlerException;

public class PredicateMakerTest {
    
    private static ArrayList<String> equivelentPredicates;
    private static ArrayList<String> skosPredicates;
    private static ArrayList<String> rankedPredicates;
    private static ArrayList<String> otherPredicates;
    
    @BeforeClass
    public static void setUpClass() {
        equivelentPredicates = new ArrayList<String>();
        equivelentPredicates.add(OWLConstants.SAME_AS);
        equivelentPredicates.add(OWLConstants.EQUIVALENT_CLASS);
        skosPredicates = new ArrayList<String>();
        skosPredicates.add(SkosConstants.EXACT_MATCH);
        skosPredicates.add(SkosConstants.CLOSE_MATCH);
        skosPredicates.add(SkosConstants.MAPPING_RELATION);
        rankedPredicates = new ArrayList<String>(equivelentPredicates);
        rankedPredicates.addAll(skosPredicates);
        otherPredicates = new ArrayList<String>();
        otherPredicates.add(SkosConstants.MAPPING_RELATION);
        otherPredicates.add(SkosConstants.RELATED_MATCH);
        otherPredicates.add(SkosConstants.BROAD_MATCH);
        otherPredicates.add(SkosConstants.NARROW_MATCH);
    }
    
 	@Test
	public void testCombine_same() throws BridgeDBException {		
		String predicate = "http://www.example.org/test#match";
		assertEquals(predicate, PredicateMaker.combine(predicate , predicate));
	}
	
	@Test(expected=BridgeDBException.class)
	public void testCombine_diff() throws BridgeDBException {		
		String predicate1 = "http://www.example.org/test#match";
		String predicate2 = "http://www.example.com/test#equivalent";
		PredicateMaker.combine(predicate1, predicate2);
	}	

	//TODO: Need to write tests for the logic in the PredicateMaker
	@Test
	public void testCombine_sameAs_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OWLConstants.SAME_AS, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_sameAs() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, OWLConstants.SAME_AS));
	}
	
	@Test
	public void testCombine_eqClass_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OWLConstants.EQUIVALENT_CLASS, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_eqClass() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, OWLConstants.EQUIVALENT_CLASS));
	}
	
	@Test
	public void testCombine_exactMatch_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(SkosConstants.EXACT_MATCH, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_exactMatch() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.EXACT_MATCH));
	}
	
	@Test
	public void testCombine_related_exactMatch() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.EXACT_MATCH));
	}

    @Test
	public void testCombine_closeMatch_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(SkosConstants.CLOSE_MATCH, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_closeMatch() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.CLOSE_MATCH));
	}
	
	@Test(expected=BridgeDBException.class)
	public void testCombine_broadMatch_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(SkosConstants.BROAD_MATCH, OboConstants.HAS_PART));
	}

	@Test(expected=BridgeDBException.class)
	public void testCombine_oboHasParts_broadMatch() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.BROAD_MATCH));
	}
	
	@Test(expected=BridgeDBException.class)
	public void testCombine_narrowMatch_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(SkosConstants.NARROW_MATCH, OboConstants.HAS_PART));
	}

	@Test(expected=BridgeDBException.class)
	public void testCombine_oboHasParts_narrowMatch() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.NARROW_MATCH));
	}
	
    /**
     * Test of combine method, of class PredicateMaker.
     */
    @Test
    public void testCombineOrder() throws BridgeDBException {
        Reporter.println("combine Order");
        for (int i = 0; i< rankedPredicates.size(); i++) {
            for (int j = 0; j< rankedPredicates.size(); j++){
                String result = PredicateMaker.combine(rankedPredicates.get(i), rankedPredicates.get(j));
                if (i < j){
                    assertEquals(rankedPredicates.get(j), result);
                } else {
                     assertEquals(rankedPredicates.get(i), result);
                }
            }
        }
    }

   /**
     * Test of combine method, of class PredicateMaker.
     */
    @Test
    public void testOtherWithEquivellent() throws BridgeDBException {
        Reporter.println("combine Order with equivelent");
        for (int i = 0; i< equivelentPredicates.size(); i++) {
            for (int j = 0; j< otherPredicates.size(); j++){
                String result = PredicateMaker.combine(equivelentPredicates.get(i), otherPredicates.get(j));
                assertEquals(otherPredicates.get(j), result);
                result = PredicateMaker.combine(otherPredicates.get(j), equivelentPredicates.get(i));
                assertEquals(otherPredicates.get(j), result);
            }
        }
    }
    
   /**
     * Test of combine method, of class PredicateMaker.
     */
    @Test
    public void testOtherWithSkos() throws BridgeDBException {
        Reporter.println("combine Order with skos");
        for (int i = 0; i< skosPredicates.size(); i++) {
            for (int j = 0; j< otherPredicates.size(); j++){
                String result = PredicateMaker.combine(skosPredicates.get(i), otherPredicates.get(j));
                assertEquals(SkosConstants.MAPPING_RELATION, result);
                result = PredicateMaker.combine(otherPredicates.get(j), skosPredicates.get(i));
                assertEquals(SkosConstants.MAPPING_RELATION, result);
            }
        }
    }

    /**
     * Test of combine method, of class PredicateMaker.
     */
    @Test
    public void testOtherWithOther() throws BridgeDBException {
        Reporter.println("combine Other with Other");
        for (int i = 0; i< otherPredicates.size(); i++) {
            for (int j = 0; j< otherPredicates.size(); j++){
                String result = PredicateMaker.combine(otherPredicates.get(i), otherPredicates.get(j));
                if (i == j){
                    assertEquals(otherPredicates.get(i), result);                
                } else {
                    assertEquals(SkosConstants.MAPPING_RELATION, result);
                }
            }
        }
    }
}
