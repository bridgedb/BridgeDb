/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.bridgedb.metadata.constants.DctermsConstants;
import org.openrdf.model.Statement;
import java.util.Set;
import org.bridgedb.utils.Reporter;
import org.junit.Ignore;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.utils.BridgeDBException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class MinLinkSetMetaDataTest extends MetaDataTestBase{
    
    public MinLinkSetMetaDataTest() throws DatatypeConfigurationException, BridgeDBException{        
    }
    
    @Test
    public void testHasRequiredValues() throws BridgeDBException{
        report("Linkset HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection("loadMinLinkSet()", loadMinLinkSet(), minLinksetSetRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testHasCorrectTypes() throws BridgeDBException{
        report("Linkset HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection("loadMinLinkSet()", loadMinLinkSet(), minLinksetSetRegistry);
        checkCorrectTypes(metaData);
    }
 
    @Test
    public void testIgnoreBadTypes() throws BridgeDBException{
        report("Linkset IgnoreBadTypes");
        Set<Statement> statements = loadMinLinkSet();
        d1ModifiedStatement = new StatementImpl(D1_ID, DctermsConstants.MODIFIED, TITLE);  
        statements.add(d1ModifiedStatement);
        MetaDataCollection metaData = new MetaDataCollection("testIgnoreBadTypes()", statements, minLinksetSetRegistry);
        assertFalse(metaData.hasCorrectTypes());
        metaData.validate();
    }
 
    @Test
    @Ignore
    public void testAllStatementsUsed() throws BridgeDBException{
        report("LinkSet AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection("loadMinLinkSet()", loadMinLinkSet(), minLinksetSetRegistry);
        checkAllStatementsUsed(metaData);
    }

    @Test
    public void testValidateOk() throws BridgeDBException{
        report("LinkSet Validate OK");
        MetaDataCollection metaData = new MetaDataCollection("loadMinLinkSet()", loadMinLinkSet(), minLinksetSetRegistry);
        metaData.validate();
    }

    @Test
    public void testValidateOkEvenWithWrongExtraType() throws BridgeDBException{
        report("LinkSet Validate OKEven With Wrong Extra Type");
        Set<Statement> statements = loadMinLinkSet();
        Statement badLinkLicenseStatement = 
                new StatementImpl(LINK_ID, DctermsConstants.LICENSE, new LiteralImpl("LICENSE"));
        statements.add(badLinkLicenseStatement);
        MetaDataCollection metaData = new MetaDataCollection("testValidateOkEvenWithWrongExtraType()", 
                statements, minLinksetSetRegistry);
        metaData.validate();
    }

   @Test
    public void testSummary() throws BridgeDBException{
        report("LinkSet Summary");
        MetaDataCollection metaData = new MetaDataCollection("loadMinLinkSet()", loadMinLinkSet(), minLinksetSetRegistry);
        String expected = "(Linkset) http://www.example.com/test/linkset1 OK!\n"
                + "(Dataset) http://www.example.com/test/dataset2 OK!\n"
                + "(Dataset) http://www.example.com/test/dataset1 OK!\n";
        String summary = metaData.summary();
        assertEquals(expected, summary);
    }
}
