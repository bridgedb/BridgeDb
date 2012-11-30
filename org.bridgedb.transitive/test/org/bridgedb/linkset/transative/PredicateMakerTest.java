/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import java.util.ArrayList;
import org.bridgedb.linkset.constants.OwlConstants;
import org.bridgedb.metadata.constants.SkosConstants;
import org.bridgedb.utils.TestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class PredicateMakerTest extends TestUtils{
    
    private static ArrayList<URI> predicates;
    private static ArrayList<URI> otherPredicates;
    
    public PredicateMakerTest() {
   }
    
    @BeforeClass
    public static void setUpClass() {
        predicates = new ArrayList<URI>();
        predicates.add(OwlConstants.SAME_AS);
        predicates.add(OwlConstants.EQUIVALENT_CLASS);
        predicates.add(SkosConstants.EXACT_MATCH);
        predicates.add(SkosConstants.CLOSE_MATCH);
        predicates.add(SkosConstants.MAPPING_RELATION);
        otherPredicates = new ArrayList<URI>();
        otherPredicates.add(SkosConstants.MAPPING_RELATION);
        otherPredicates.add(SkosConstants.RELATED_MATCH);
        otherPredicates.add(SkosConstants.BROAD_MATCH);
        otherPredicates.add(SkosConstants.NARROW_MATCH);
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
     * Test of combine method, of class PredicateMaker.
     */
    @Test
    public void testCombineOrder() throws Exception {
        report("combine Order");
        for (int i = 0; i< predicates.size(); i++) {
            for (int j = 0; j< predicates.size(); j++){
                URI result = PredicateMaker.combine(predicates.get(i), predicates.get(j));
                if (i < j){
                    assertEquals(predicates.get(j), result);
                } else {
                     assertEquals(predicates.get(i), result);
                }
            }
        }
    }

   /**
     * Test of combine method, of class PredicateMaker.
     */
    @Test
    @Ignore
    public void testOtherWithOrder() throws Exception {
        report("combine Order with other");
        for (int i = 0; i< predicates.size(); i++) {
            for (int j = 0; j< otherPredicates.size(); j++){
                URI result = PredicateMaker.combine(predicates.get(i), otherPredicates.get(j));
                assertEquals(SkosConstants.MAPPING_RELATION, result);
                result = PredicateMaker.combine(otherPredicates.get(j), predicates.get(i));
                assertEquals(SkosConstants.MAPPING_RELATION, result);
            }
        }
    }
    
   /**
     * Test of combine method, of class PredicateMaker.
     */
    @Test
    @Ignore
    public void testOtherWithOther() throws Exception {
        report("combine Other with Other");
        for (int i = 0; i< otherPredicates.size(); i++) {
            for (int j = 0; j< otherPredicates.size(); j++){
                URI result = PredicateMaker.combine(otherPredicates.get(i), otherPredicates.get(j));
                assertEquals(SkosConstants.MAPPING_RELATION, result);
            }
        }
    }
}
