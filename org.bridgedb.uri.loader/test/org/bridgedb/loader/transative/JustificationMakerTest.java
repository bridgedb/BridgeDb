package org.bridgedb.loader.transative;

import org.bridgedb.uri.loader.transative.JustificationMaker;
import org.bridgedb.uri.loader.transative.constant.ChemInf;
import org.bridgedb.uri.loader.transative.constant.OboConstants;
import org.bridgedb.utils.BridgeDBException;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class JustificationMakerTest {

	@Test
	public void testCombine_same() throws BridgeDBException {		
		String justification = "http://www.example.org/test#justification";
		assertEquals(justification, JustificationMaker.combine(justification , justification));
	}
	
	@Test(expected=BridgeDBException.class)
	public void testCombine_diff() throws BridgeDBException {		
		String justification1 = "http://www.example.org/test#justification";
		String justification2 = "http://www.example.com/test#different";
		JustificationMaker.combine(justification1, justification2);
	}	

	@Test
	public void testCombine_inchi() throws BridgeDBException {
		assertEquals(ChemInf.INCHI_KEY, 
				JustificationMaker.combine(ChemInf.INCHI_KEY, ChemInf.INCHI_KEY));
	}

	@Test
	public void testCombine_inchi_oboHasParts() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, 
				JustificationMaker.combine(ChemInf.INCHI_KEY, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_inchi() throws BridgeDBException {
		assertEquals(OboConstants.HAS_PART, 
				JustificationMaker.combine(OboConstants.HAS_PART, ChemInf.INCHI_KEY));
	}
	
}
