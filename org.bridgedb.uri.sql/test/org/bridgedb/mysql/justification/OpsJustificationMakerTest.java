package org.bridgedb.mysql.justification;

import org.bridgedb.rdf.constants.ChemInf;
import org.bridgedb.rdf.constants.OboConstants;
import org.bridgedb.sql.justification.JustificationMaker;
import org.bridgedb.sql.justification.OpsJustificationMaker;
import org.bridgedb.utils.BridgeDBException;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

import org.junit.Test;

public class OpsJustificationMakerTest {

    static JustificationMaker justificationMaker;
   
    @BeforeClass
    public static void setUpClass() throws BridgeDBException {
        OpsJustificationMaker.init();
        justificationMaker = OpsJustificationMaker.getInstance();
    }

    @Test
    public void testCombine_same() throws BridgeDBException {		
        String justification = "http://www.example.org/test#justification";
        assertEquals(justification, justificationMaker.combine(justification , justification));
    }
	
    @Test(expected=BridgeDBException.class)
    public void testCombine_diff() throws BridgeDBException {		
        String justification1 = "http://www.example.org/test#justification";
        String justification2 = "http://www.example.com/test#different";
        justificationMaker.combine(justification1, justification2);
    }	

    @Test
    public void testCombine_hack1() throws BridgeDBException {		
        String justification1 = "http://example.com/EnsemblBasedProteinGene";
        String justification2 = "http://semanticscience.org/resource/SIO_010035";
        justificationMaker.combine(justification1, justification2);
    }
        
    @Test
    public void testCombine_hack2() throws BridgeDBException {		
        String justification1 = "http://semanticscience.org/resource/SIO_010035";
        String justification2 = "http://example.com/EnsemblBasedProteinGene";
       	justificationMaker.combine(justification1, justification2);
    }	

    @Test
    public void testCombine_inchi() throws BridgeDBException {
        assertEquals(ChemInf.INCHI_KEY, 
                justificationMaker.combine(ChemInf.INCHI_KEY, ChemInf.INCHI_KEY));
    }

    @Test
    public void testCombine_inchi_oboHasParts() throws BridgeDBException {
        assertEquals(OboConstants.HAS_PART, 
                justificationMaker.combine(ChemInf.INCHI_KEY, OboConstants.HAS_PART));
    }

    @Test
    public void testCombine_oboHasParts_inchi() throws BridgeDBException {
        assertEquals(OboConstants.HAS_PART, 
                justificationMaker.combine(OboConstants.HAS_PART, ChemInf.INCHI_KEY));
    }
    
    @Test
    public void testCombine_narrowMatch_oboHasParts() throws BridgeDBException {
        assertEquals(ChemInf.CHEMICAL_ENTITY, justificationMaker.combine(ChemInf.CHEMICAL_ENTITY, ChemInf.INCHI_KEY));
    }

    
        
	
}
