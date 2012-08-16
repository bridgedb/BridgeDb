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

import org.openrdf.rio.ParseErrorListener;

/**
 *
 * @author Christian
 */
public class LinksetParserErrorListener implements ParseErrorListener{

    @Override
    public void warning(String message, int lineNo, int colNo) {
        System.out.println("WARNING *** " + message);
        System.out.println ("Line number: " + lineNo + " columns number: " + colNo);
    }

    @Override
    public void error(String message, int lineNo, int colNo) {
        System.out.println("***ERROR*** " + message);
        System.out.println ("Line number: " + lineNo + " columns number: " + colNo);
    }

    @Override
    public void fatalError(String message, int lineNo, int colNo) {
        System.out.println("******* FETAL ERROR  *** ");
        System.out.println(message);
        System.out.println ("Line number: " + lineNo + " columns number: " + colNo);
        System.out.println("*************************************************************************");
        System.out.println();
    }
    
}
