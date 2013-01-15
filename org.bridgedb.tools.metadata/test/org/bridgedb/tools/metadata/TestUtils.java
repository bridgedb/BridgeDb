/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata;

import org.bridgedb.tools.metadata.MetaData;
import org.bridgedb.tools.metadata.MetaDataCollection;
import org.bridgedb.utils.Reporter;
import java.util.Set;
import org.openrdf.model.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.utils.BridgeDBException;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Christian
 */
public abstract class TestUtils extends org.bridgedb.utils.TestUtils{
    
    //Flags for easy reading of tests
    static final boolean INCLUDE_WARNINGS = true;
    static final boolean NO_WARNINGS = false;;
     
    void checkCorrectNumberOfIds(MetaDataCollection metaData, int numberOfIds){
        Set<Resource> ids = metaData.getIds();
        boolean ok = (ids.size() == numberOfIds);
        if (!ok){
            //This test will fail but with extra info
            assertEquals(numberOfIds + " ids Expected ", ids);
            report(metaData.toString());
            assertTrue(ok);
        }        
    }

    void checkRequiredValues(MetaDataCollection metaData) throws BridgeDBException{
        boolean ok = metaData.hasRequiredValuesOrIsSuperset();
        if (!ok){
            //This test will fail but with extra info
            String report = metaData.validityReport(NO_WARNINGS);
            assertThat(report, not(containsString("ERROR")));
            report("hasRequiredValuesOrIsSuperset failed but validity report clear");
            report(metaData.toString());
            assertTrue(ok);
        }        
    }

    void checkCorrectTypes(MetaData metaData) throws BridgeDBException{
        boolean ok = metaData.hasCorrectTypes();
        if (!ok){
            //This test will fail but with extra info
            String report = metaData.validityReport(NO_WARNINGS);
            assertThat(report, not(containsString("ERROR")));
            assertTrue(ok);
        }        
    }
    
    void checkAllStatementsUsed(MetaDataCollection metaData) {
        boolean ok = metaData.allStatementsUsed();
        if (!ok){
            //This test will fail but with extra info
            assertEquals("", metaData.unusedStatements());
            report(metaData.toString());
            assertTrue(ok);
        }        
    }
    
}
