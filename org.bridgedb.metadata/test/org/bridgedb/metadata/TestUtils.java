/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.utils.Reporter;
import java.util.Set;
import org.openrdf.model.Resource;
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
     
    void checkCorrectNumberOfIds(MetaDataCollection metaData, int numberOfIds){
        Set<Resource> ids = metaData.getIds();
        boolean ok = (ids.size() == numberOfIds);
        if (!ok){
            //This test will fail but with extra info
            assertEquals(numberOfIds + " ids Expected ", ids);
            Reporter.report(metaData.toString());
            assertTrue(ok);
        }        
    }

    void checkRequiredValues(MetaDataCollection metaData, RequirementLevel forceLevel){
        boolean ok = metaData.hasRequiredValuesOrIsSuperset(forceLevel);
        if (!ok){
            //This test will fail but with extra info
            assertEquals(AppendBase.CLEAR_REPORT, metaData.validityReport(
                    forceLevel, NO_WARNINGS));
            Reporter.report(metaData.toString());
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
            Reporter.report(metaData.toString());
            assertTrue(ok);
        }        
    }
    
}
