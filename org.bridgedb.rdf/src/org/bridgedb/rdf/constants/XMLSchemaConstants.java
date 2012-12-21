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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.constants;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 */
public class XMLSchemaConstants {


    public static final String PREFIX = "http://www.w3.org/2001/XMLSchema#";
    
    public static final URI STRING = new URIImpl(PREFIX + "string");
    public static final URI BOOLEAN = new URIImpl(PREFIX + "boolean");
    public static final URI DECIMAL = new URIImpl(PREFIX + "decimal");
    public static final URI FLOAT = new URIImpl(PREFIX + "float");
    public static final URI DOUBLE = new URIImpl(PREFIX + "double");
    public static final URI DURATION = new URIImpl(PREFIX + "duration");
    public static final URI DATE_TIME = new URIImpl(PREFIX + "dateTime");
    public static final URI TIME = new URIImpl(PREFIX + "time");
    public static final URI DATE = new URIImpl(PREFIX + "date");
    public static final URI G_YEAR_MONTH = new URIImpl(PREFIX + "gYearMonth");
    public static final URI G_YEAR = new URIImpl(PREFIX + "gYear");
    public static final URI G_MONTH_DAY = new URIImpl(PREFIX + "gMonthDay");
    public static final URI G_DAY = new URIImpl(PREFIX + "gDay");
    public static final URI G_MONTH = new URIImpl(PREFIX + "gMonth");
    public static final URI HEX_BINARY = new URIImpl(PREFIX + "hexBinary");
    public static final URI BASE_64_BINARY = new URIImpl(PREFIX + "base64Binary");
    public static final URI ANY_URI = new URIImpl(PREFIX + "anyURI");
    public static final URI QNAME = new URIImpl(PREFIX + "QName");
    public static final URI NOTATION = new URIImpl(PREFIX + "NOTATION");
    public static final URI INTEGER = new URIImpl(PREFIX + "integer");
    public static final URI NON_NEGATIVE_INTEGER = new URIImpl(PREFIX + "nonNegativeInteger");
    public static final URI UNISGNED_BYTE = new URIImpl(PREFIX + "unsignedByte");
}
