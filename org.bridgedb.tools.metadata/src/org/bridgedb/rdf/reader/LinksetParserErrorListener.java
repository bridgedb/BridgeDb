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
package org.bridgedb.rdf.reader;

import org.apache.log4j.Logger;
import org.bridgedb.utils.Reporter;
import org.openrdf.rio.ParseErrorListener;

/**
 *
 * @author Christian
 */
public class LinksetParserErrorListener implements ParseErrorListener{

    static final Logger logger = Logger.getLogger(LinksetParserErrorListener.class);
    
    @Override
    public void warning(String message, int lineNo, int colNo) {
        logger.warn("WARNING *** " + message);
        logger.warn("Line number: " + lineNo + " columns number: " + colNo);
    }

    @Override
    public void error(String message, int lineNo, int colNo) {
        logger.error("***ERROR*** " + message);
        logger.error("Line number: " + lineNo + " columns number: " + colNo);
    }

    @Override
    public void fatalError(String message, int lineNo, int colNo) {
        logger.fatal("******* FETAL ERROR  *** ");
        logger.fatal(message);
        logger.fatal ("Line number: " + lineNo + " columns number: " + colNo);
        logger.fatal("*************************************************************************");
        logger.fatal("");
    }
    
}
