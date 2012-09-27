package org.bridgedb.linkset.transative;

import static org.junit.Assert.*;

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
	
}
