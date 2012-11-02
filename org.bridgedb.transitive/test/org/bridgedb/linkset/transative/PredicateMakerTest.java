package org.bridgedb.linkset.transative;

import org.bridgedb.linkset.constants.OwlConstants;
import org.bridgedb.metadata.constants.OboConstants;
import static org.junit.Assert.*;

import org.bridgedb.metadata.constants.SkosConstants;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;

public class PredicateMakerTest {

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
	
}
