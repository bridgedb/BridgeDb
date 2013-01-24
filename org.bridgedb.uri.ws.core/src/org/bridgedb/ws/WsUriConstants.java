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
public class WsUriConstants extends WsConstants{
    
    public static final String TARGET_URI_SPACE = "targetURISpace";
    public static final String PROFILE_URL = "profileURL";
    public static final String URL = "URL";
    public static final String XML = "XML";
    
    public static final String DATA_SOURCE = "dataSource";
    public static final String GET_MAPPING_INFO = "getMappingInfo";
    public static final String GET_OVERALL_STATISTICS = "getOverallStatistics";
    public static final String GET_SAMPLE_SOURCE_URLS = "getSampleSourceURLs";
    public static final String GRAPHVIZ = "graphviz";
    public static final String MAPPING = "mapping";
    public static final String MAP_URL = "mapURL";
    public static final String MAP_TO_URLS = "mapToURLs";
    public static final String SQL_COMPAT_VERSION = "SqlCompatVersion";
    public static final String TO_XREF = "toXref";
    public static final String URL_EXISTS = "URLExists";
    public static final String URL_SEARCH = "URLSearch";
}
