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
package org.bridgedb.tools.metadata.rdf;

import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class RdfController {
    
    private static final Resource CONTROLLER_RESOURCE = new URIImpl(RepositoryFactory.getBaseURI());
    private static final URI NEXT_CONTEXT = new URIImpl(RepositoryFactory.getBaseURI() + "#nextContext");
    private static final boolean CAN_BE_NEW = false;
    
    public static String getNextContext(StoreType storeType) throws BridgeDBException{
        WrappedRepository repository = RepositoryFactory.getRepository(storeType, CAN_BE_NEW);
        int nextContext = repository.getAndIncrementValue(CONTROLLER_RESOURCE, NEXT_CONTEXT, CONTROLLER_RESOURCE);
        return RepositoryFactory.getBaseURI() + "void/" + nextContext;
    }

    public static String getValidateBase() throws BridgeDBException{
        return RepositoryFactory.getBaseURI() + "void/validate/";
    }
}
