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

/**
 *
 * @author Christian
 */
public class WsConstants {

    static final String ID = "id";
    static final String DATASOURCE_SYSTEM_CODE = "code";
    static final String SOURCE_DATASOURCE_SYSTEM_CODE = "sourceCode";
    static final String TARGET_DATASOURCE_SYSTEM_CODE = "targetCode";
    static final String LIMIT = "limit";
    static final String TEXT = "text";
    
    static final String FREE_SEARCH = "freeSearch";
    static final String GET_CAPABILITIES = "getCapabilities";
    static final String GET_KEYS = "getKeys";
    static final String GET_SUPPORTED_SOURCE_DATA_SOURCES = "getSupportedSrcDataSources";
    static final String GET_SUPPORTED_TARGET_DATA_SOURCES = "getSupportedTgtDataSources";
    static final String IS_FREE_SEARCH_SUPPORTED = "isFreeSearchSupported";
    static final String IS_MAPPING_SUPPORTED = "isMappingSupported";
    static final String MAP_ID = "mapID";
    static final String PROPERTY = "property";
    static final String XREF_EXISTS = "xrefExists";
}
