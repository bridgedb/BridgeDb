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
package org.bridgedb.linkset.rdf;

import org.bridgedb.IDMapperException;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public interface RdfLoader {
    
    public void processFirstNoneHeader(Statement firstMap) throws RDFHandlerException;

    public void addHeaderStatement(Statement st) throws RDFHandlerException;

    public void insertURLMapping(Statement st) throws RDFHandlerException;

    public void closeInput()throws IDMapperException;

    public void setSourceFile(String absolutePath);

    public String getBaseUri();

}
