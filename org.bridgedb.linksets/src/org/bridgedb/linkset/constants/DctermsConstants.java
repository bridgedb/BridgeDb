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
package org.bridgedb.linkset.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 */
public class DctermsConstants {
    
    private static final String dctermns = "http://purl.org/dc/terms/";
    
    public static final URI CREATED = new URIImpl(dctermns + "created");
    public static final URI CREATOR = new URIImpl(dctermns + "creator");
    public static final URI DESCRIPTION = new URIImpl(dctermns + "description");
    public static final URI LICENSE = new URIImpl(dctermns + "license");
    public static final URI SUBJECT = new URIImpl(dctermns + "subject");
	public static final URI TITLE = new URIImpl(dctermns + "title");

    
}
