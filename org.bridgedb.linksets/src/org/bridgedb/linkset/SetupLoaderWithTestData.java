// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.linkset;
import java.io.FileNotFoundException;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.utils.Reporter;

/**
 * @author Christian
 */
public class SetupLoaderWithTestData {
        
   public static void main(String[] args) throws BridgeDbSqlException, IDMapperException, IDMapperLinksetException, FileNotFoundException {
        Reporter.report("sample2to1.ttl");
        String[] args1 = {"../org.bridgedb.linksets/test-data/sample1to2.ttl", "new"};
        LinksetLoader.main (args1);
        Reporter.report("sample1to3.ttl");
        String[] args2 = {"../org.bridgedb.linksets/test-data/sample1to3.ttl", "load"};
        LinksetLoader.main (args2);
        Reporter.report("sample2to3.ttl");
        String[] args3 = {"../org.bridgedb.linksets/test-data/sample2to3.ttl", "load"};
        LinksetLoader.main (args3);
	}

}