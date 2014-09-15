package org.bridgedb.mysql.justification;

import org.bridgedb.rdf.constants.ChemInf;
import org.bridgedb.rdf.constants.OboConstants;
import org.bridgedb.sql.justification.JustificationMaker;
import org.bridgedb.sql.justification.OpsJustificationMaker;
import org.bridgedb.utils.BridgeDBException;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class OpsJustificationMakerTest {

        @BeforeClass
        public static void init() throws BridgeDBException{
            OpsJustificationMaker.init();
        }

	@Test
	public void testCombine_same() throws BridgeDBException {		
		String justification = "http://www.example.org/test#justification";
                JustificationMaker justificationMaker = OpsJustificationMaker.getInstance();
		assertEquals(justification, justificationMaker.combine(justification , justification));
	}
	
	@Test(expected=BridgeDBException.class)
	public void testCombine_diff() throws BridgeDBException {		
		String justification1 = "http://www.example.org/test#justification";
		String justification2 = "http://www.example.com/test#different";
                JustificationMaker justificationMaker = OpsJustificationMaker.getInstance();
		justificationMaker.combine(justification1, justification2);
	}	

	@Test
	public void testCombine_hack1() throws BridgeDBException {		
		String justification1 = "http://example.com/EnsemblBasedProteinGene";
		String justification2 = "http://semanticscience.org/resource/SIO_010035";
                JustificationMaker justificationMaker = OpsJustificationMaker.getInstance();
		justificationMaker.combine(justification1, justification2);
	}
        
	@Test
	public void testCombine_hack2() throws BridgeDBException {		
		String justification1 = "http://semanticscience.org/resource/SIO_010035";
		String justification2 = "http://example.com/EnsemblBasedProteinGene";
                JustificationMaker justificationMaker = OpsJustificationMaker.getInstance();
		justificationMaker.combine(justification1, justification2);
	}	

        @Test
	public void testCombine_inchi() throws BridgeDBException {
                JustificationMaker justificationMaker = OpsJustificationMaker.getInstance();
		assertEquals(ChemInf.INCHI_KEY, 
				justificationMaker.combine(ChemInf.INCHI_KEY, ChemInf.INCHI_KEY));
	}

	@Test
	public void testCombine_inchi_oboHasParts() throws BridgeDBException {
                JustificationMaker justificationMaker = OpsJustificationMaker.getInstance();
		assertEquals(OboConstants.HAS_PART, 
				justificationMaker.combine(ChemInf.INCHI_KEY, OboConstants.HAS_PART));
	}

	@Test
	public void testCombine_oboHasParts_inchi() throws BridgeDBException {
                JustificationMaker justificationMaker = OpsJustificationMaker.getInstance();
		assertEquals(OboConstants.HAS_PART, 
				justificationMaker.combine(OboConstants.HAS_PART, ChemInf.INCHI_KEY));
	}
	
}
