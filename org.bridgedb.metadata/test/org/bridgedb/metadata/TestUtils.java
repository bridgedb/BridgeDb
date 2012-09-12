/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import javax.xml.datatype.DatatypeConfigurationException;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class TestUtils {
    
    //Flags for easy reading of tests
    static final boolean INCLUDE_WARNINGS = true;
    static final boolean NO_WARNINGS = false;;
     
    void checkRequiredValues(MetaData metaData, RequirementLevel forceLevel){
        boolean ok = metaData.hasRequiredValues(forceLevel);
        if (!ok){
            //This test will fail but with extra info
            assertEquals(AppendBase.CLEAR_REPORT, metaData.validityReport(
                    forceLevel, NO_WARNINGS));
            assertTrue(ok);
        }        
    }

    void checkCorrectTypes(MetaData metaData){
        boolean ok = metaData.hasCorrectTypes();
        if (!ok){
            //This test will fail but with extra info
            assertEquals(MetaDataBase.CLEAR_REPORT, metaData.validityReport(RequirementLevel.TECHNICAL_MUST, NO_WARNINGS));
            assertTrue(ok);
        }        
    }
    
    void checkAllStatementsUsed(MetaDataCollection metaData) {
        boolean ok = metaData.allStatementsUsed();
        if (!ok){
            //This test will fail but with extra info
            assertEquals("", metaData.unusedStatements());
            assertTrue(ok);
        }        
    }
    
}
