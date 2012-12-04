package org.bridgedb.linkset.transative;

import java.util.ArrayList;
import org.bridgedb.linkset.constants.OwlConstants;
import org.bridgedb.metadata.constants.OboConstants;
import static org.junit.Assert.*;

import org.bridgedb.metadata.constants.SkosConstants;
import org.bridgedb.utils.TestUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;

public class PredicateMakerTest extends TestUtils{
 
    private static ArrayList<URI> predicates;
    private static ArrayList<URI> otherPredicates;
    
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
    
 	@Test
	public void testCombine_same() throws RDFHandlerException {		
		URI predicate = new URIImpl("http://www.example.org/test#match");
		assertEquals(predicate, PredicateMaker.combine(predicate , predicate));
	}
	
	@Test(expected=RDFHandlerException.class)
	public void testCombine_diff() throws RDFHandlerException {		
		URI predicate1 = new URIImpl("http://www.example.org/test#match");
		URI predicate2 = new URIImpl("http://www.example.com/test#equivalent");
		PredicateMaker.combine(predicate1, predicate2);
	}	

	//TODO: Need to write tests for the logic in the PredicateMaker
	@Test
	public void testCombine_sameAs_oboHasParts() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OwlConstants.SAME_AS, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_sameAs() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, OwlConstants.SAME_AS));
	}
	
	@Test
	public void testCombine_eqClass_oboHasParts() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OwlConstants.EQUIVALENT_CLASS, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_eqClass() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, OwlConstants.EQUIVALENT_CLASS));
	}
	
	@Test
	public void testCombine_exactMatch_oboHasParts() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(SkosConstants.EXACT_MATCH, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_exactMatch() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.EXACT_MATCH));
	}
	
	@Test
	public void testCombine_closeMatch_oboHasParts() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(SkosConstants.CLOSE_MATCH, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_closeMatch() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.CLOSE_MATCH));
	}
	
	@Test(expected=RDFHandlerException.class)
	public void testCombine_broadMatch_oboHasParts() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(SkosConstants.BROAD_MATCH, OboConstants.HAS_PART));
	}

	@Test(expected=RDFHandlerException.class)
	public void testCombine_oboHasParts_broadMatch() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.BROAD_MATCH));
	}
	
	@Test(expected=RDFHandlerException.class)
	public void testCombine_narrowMatch_oboHasParts() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(SkosConstants.NARROW_MATCH, OboConstants.HAS_PART));
	}

	@Test(expected=RDFHandlerException.class)
	public void testCombine_oboHasParts_narrowMatch() throws RDFHandlerException {
		assertEquals(OboConstants.HAS_PART, PredicateMaker.combine(OboConstants.HAS_PART, SkosConstants.NARROW_MATCH));
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
