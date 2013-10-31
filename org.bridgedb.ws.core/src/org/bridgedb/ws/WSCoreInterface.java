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
package org.bridgedb.ws;

import java.util.List;
import javax.ws.rs.core.Response;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public interface WSCoreInterface {

    /**
     * @param id
     * @param scrCode
     * @param targetCodes Optional
     * @return An XrefMapsBean wrapped in a Response
     * @throws BridgeDBException
     */
    Response mapID(List<String> id, List<String> scrCode, List<String> targetCodes) throws BridgeDBException;

    /**
     * @param id
     * @param scrCode
     * @return A XrefExistsBean wrapped in a response
     * @throws BridgeDBException
     */
    Response xrefExists(String id, String scrCode) throws BridgeDBException;

    /**
     * @param text
     * @param limit Optional
     * @return A XrefsBean wrapped in a Response
     * @throws BridgeDBException
     */
    Response freeSearch(String text, String limit) throws BridgeDBException;

    /**
     * @return A CapabilitiesBean wrapped in a Response
     */
    Response getCapabilities();

    /**
     * @return A FreeSearchSupportedBean wrapped in a Response
     */
    Response isFreeSearchSupported();

    /**
     * @return A DataSourcesBean wrapped in a Response
     * @throws BridgeDBException
     */
    Response getSupportedSrcDataSources() throws BridgeDBException;

    /**
     * @return A DataSourcesBean wrapped in a Response
     * @throws BridgeDBException
     */
    Response getSupportedTgtDataSources() throws BridgeDBException;

    /**
     * @param sourceCode
     * @param targetCode
     * @return A MappingSupportedBean wrapped in a Response
     * @throws BridgeDBException
     */
    Response isMappingSupported( String sourceCode, String targetCode) throws BridgeDBException;

    /**
     * @param key
     * @return A PropertyBean wrapped in a Response
     */
    Response getProperty(String key);

    /**
     * A Response wrapper for a 
     * @return A PropertiesBean wrapped in a Response
     */
    Response getKeys();
   
}
