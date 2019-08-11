/*
 * BridgeDb,
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

package org.bridgedb.sql.predicate;

import java.util.ArrayList;
import org.bridgedb.rdf.constants.OWLConstants;
import org.bridgedb.rdf.constants.OboConstants;
import org.bridgedb.rdf.constants.SkosConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import static org.junit.Assert.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

@Tag("mysql")
public class LoosePredicateMakerTest {
    
    private static ArrayList<String> equivelentPredicates;
    private static ArrayList<String> skosPredicates;
    private static ArrayList<String> rankedPredicates;
    private static ArrayList<String> otherPredicates;
    private static LoosePredicateMaker predicateMaker;
    
    @BeforeAll
    public static void setUpClass() throws BridgeDBException {
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
        LoosePredicateMaker.init();
        predicateMaker = LoosePredicateMaker.getInstance();
    }
    
 	@org.junit.jupiter.api.Test
	public void testCombine_same() throws BridgeDBException {		
		String predicate = "http://www.example.org/test#match";
		assertEquals(predicate, predicateMaker.combine(predicate , predicate));
	}
	
	@org.junit.jupiter.api.Test
	public void testCombine_diff() throws BridgeDBException {
        Assertions.assertThrows(BridgeDBException.class, ()->{
            String predicate1 = "http://www.example.org/test#match";
            String predicate2 = "http://www.example.com/test#equivalent";
            predicateMaker.combine(predicate1, predicate2);
        });
	}	

	//TODO: Need to write tests for the logic in the PredicateMaker
	@org.junit.jupiter.api.Test
	public void testCombine_sameAs_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OWLConstants.SAME_AS, OboConstants.HAS_PART));
	}

	@org.junit.jupiter.api.Test
	public void testCombine_oboHasParts_sameAs() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OboConstants.HAS_PART, OWLConstants.SAME_AS));
	}
	
	@org.junit.jupiter.api.Test
	public void testCombine_eqClass_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OWLConstants.EQUIVALENT_CLASS, OboConstants.HAS_PART));
	}

	@org.junit.jupiter.api.Test
	public void testCombine_oboHasParts_eqClass() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OboConstants.HAS_PART, OWLConstants.EQUIVALENT_CLASS));
	}
	
	@org.junit.jupiter.api.Test
	public void testCombine_exactMatch_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(SkosConstants.EXACT_MATCH, OboConstants.HAS_PART));
	}

	@org.junit.jupiter.api.Test
	public void testCombine_oboHasParts_exactMatch() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OboConstants.HAS_PART, SkosConstants.EXACT_MATCH));
	}
	
	@org.junit.jupiter.api.Test
	public void testCombine_related_exactMatch() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OboConstants.HAS_PART, SkosConstants.EXACT_MATCH));
	}

    @org.junit.jupiter.api.Test
	public void testCombine_closeMatch_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(SkosConstants.CLOSE_MATCH, OboConstants.HAS_PART));
	}

	@org.junit.jupiter.api.Test
	public void testCombine_oboHasParts_closeMatch() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OboConstants.HAS_PART, SkosConstants.CLOSE_MATCH));
	}

    @org.junit.jupiter.api.Test
	public void testCombine_broadMatch_oboHasParts() throws BridgeDBException {
        Assertions.assertThrows(BridgeDBException.class, ()->{
            assertEquals(OboConstants.HAS_PART, predicateMaker.combine(SkosConstants.BROAD_MATCH, OboConstants.HAS_PART));
        });
	}

    @org.junit.jupiter.api.Test
	public void testCombine_oboHasParts_broadMatch() throws BridgeDBException {
        Assertions.assertThrows(BridgeDBException.class, ()->{
            assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OboConstants.HAS_PART, SkosConstants.BROAD_MATCH));
        });
	}

    @org.junit.jupiter.api.Test
	public void testCombine_narrowMatch_oboHasParts() throws BridgeDBException {
        Assertions.assertThrows(BridgeDBException.class, ()->{
            assertEquals(OboConstants.HAS_PART, predicateMaker.combine(SkosConstants.NARROW_MATCH, OboConstants.HAS_PART));
        });
	}

    @org.junit.jupiter.api.Test
	public void testCombine_oboHasParts_narrowMatch() throws BridgeDBException {
        Assertions.assertThrows(BridgeDBException.class, ()->{
            assertEquals(OboConstants.HAS_PART, predicateMaker.combine(OboConstants.HAS_PART, SkosConstants.NARROW_MATCH));
        });
	}
	
	public void testCombine_related_related() throws BridgeDBException {
		assertEquals(SkosConstants.RELATED_MATCH, predicateMaker.combine(SkosConstants.RELATED_MATCH, SkosConstants.RELATED_MATCH));
	}

    /**
     * Test of combine method, of class PredicateMaker.
     */
    @org.junit.jupiter.api.Test
    public void testCombineOrder() throws BridgeDBException {
        Reporter.println("combine Order");
        for (int i = 0; i< rankedPredicates.size(); i++) {
            for (int j = 0; j< rankedPredicates.size(); j++){
                String result = predicateMaker.combine(rankedPredicates.get(i), rankedPredicates.get(j));
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
    @org.junit.jupiter.api.Test
    public void testOtherWithEquivellent() throws BridgeDBException {
        Reporter.println("combine Order with equivelent");
        for (int i = 0; i< equivelentPredicates.size(); i++) {
            for (int j = 0; j< otherPredicates.size(); j++){
                String result = predicateMaker.combine(equivelentPredicates.get(i), otherPredicates.get(j));
                assertEquals(otherPredicates.get(j), result);
                result = predicateMaker.combine(otherPredicates.get(j), equivelentPredicates.get(i));
                assertEquals(otherPredicates.get(j), result);
            }
        }
    }
    
   /**
     * Test of combine method, of class PredicateMaker.
     */
    @org.junit.jupiter.api.Test
    public void testOtherWithSkos() throws BridgeDBException {
        Reporter.println("combine Order with skos");
        for (int i = 0; i< skosPredicates.size(); i++) {
            for (int j = 0; j< otherPredicates.size(); j++){
                String result = predicateMaker.combine(skosPredicates.get(i), otherPredicates.get(j));
                assertEquals(SkosConstants.MAPPING_RELATION, result);
                result = predicateMaker.combine(otherPredicates.get(j), skosPredicates.get(i));
                assertEquals(SkosConstants.MAPPING_RELATION, result);
            }
        }
    }

    /**
     * Test of combine method, of class PredicateMaker.
     */
    @org.junit.jupiter.api.Test
    public void testOtherWithOther() throws BridgeDBException {
        Reporter.println("combine Other with Other");
        for (int i = 0; i< otherPredicates.size(); i++) {
            for (int j = 0; j< otherPredicates.size(); j++){
                String result = predicateMaker.combine(otherPredicates.get(i), otherPredicates.get(j));
                if (i == j){
                    assertEquals(otherPredicates.get(i), result);                
                } else {
                    assertEquals(SkosConstants.MAPPING_RELATION, result);
                }
            }
        }
    }
}
