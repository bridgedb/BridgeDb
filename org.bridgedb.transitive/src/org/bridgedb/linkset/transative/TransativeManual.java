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
package org.bridgedb.linkset.transative;

import org.bridgedb.linkset.LinksetLoader;

/**
 *
 * @author Christian
 */
public class TransativeManual {
    
    public static void main(String[] args) throws Exception {
        String[] args1 = new String[6];
        args1[0] = "33";
        args1[1] = "14";
        args1[2] = "load";
        args1[3] = "www";
        args1[4] = "purl";
        String fileName = "D:/OpenPhacts/ondex2linksets/JulyTransitive/cw-drugbankDrugs-transitive.ttl";
//        String fileName = "test-data/linkset2To3.ttl";
        args1[5] = fileName;
        TransativeCreator.main(args1);
        String[] args2 = new String[2];
        args2[0] = fileName;
        args2[1] = "validate";
        LinksetLoader.main (args2);
    }
}
