// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.tools.metadata;

import java.util.Set;
import org.bridgedb.utils.BridgeDBException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.openrdf.model.Resource;

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
            report("Found " + ids.size());
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
            if (report.contains("ERROR")){
                report(report);
           }
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
            report(report);
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
