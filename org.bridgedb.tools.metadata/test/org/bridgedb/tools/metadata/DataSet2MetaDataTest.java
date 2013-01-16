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

import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.utils.BridgeDBException;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public class DataSet2MetaDataTest extends MetaDataTestBase{
    
    public DataSet2MetaDataTest() throws DatatypeConfigurationException, BridgeDBException{        
    }
    
    @Test
    public void testHasRequiredValues() throws BridgeDBException{
        report("HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection("loadDirectDataSet2()", loadDirectDataSet2(), voidRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testHasCorrectTypes() throws BridgeDBException{
        report("HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet2()", loadMayDataSet2(), voidRegistry);
        checkCorrectTypes(metaData);
    }

    @Test
    @Ignore
    public void testAllStatementsUsed() throws BridgeDBException{
        report("AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet2()", loadMayDataSet2(), voidRegistry);
        checkAllStatementsUsed(metaData);
    }

}
