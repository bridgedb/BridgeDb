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

    public static final String ID = "id";
    public static final String DATASOURCE_SYSTEM_CODE = "code";
    public static final String SOURCE_DATASOURCE_SYSTEM_CODE = "sourceCode";
    public static final String TARGET_DATASOURCE_SYSTEM_CODE = "targetCode";
    public static final String LIMIT = "limit";
    public static final String TEXT = "text";
    
    public static final String FREE_SEARCH = "freeSearch";
    public static final String GET_CAPABILITIES = "getCapabilities";
    public static final String GET_KEYS = "getKeys";
    public static final String GET_SUPPORTED_SOURCE_DATA_SOURCES = "getSupportedSrcDataSources";
    public static final String GET_SUPPORTED_TARGET_DATA_SOURCES = "getSupportedTgtDataSources";
    public static final String IS_FREE_SEARCH_SUPPORTED = "isFreeSearchSupported";
    public static final String IS_MAPPING_SUPPORTED = "isMappingSupported";
    public static final String MAP_ID = "mapID";
    public static final String PROPERTY = "property";
    public static final String XREF_EXISTS = "xrefExists";
}
