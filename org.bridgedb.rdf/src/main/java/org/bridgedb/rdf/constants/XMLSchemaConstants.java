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
package org.bridgedb.rdf.constants;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class XMLSchemaConstants {
	private static final SimpleValueFactory factory = SimpleValueFactory.getInstance();

    public static final String PREFIX = "http://www.w3.org/2001/XMLSchema#";
    
    public static final IRI STRING = factory.createIRI(PREFIX + "string");
    public static final IRI BOOLEAN = factory.createIRI(PREFIX + "boolean");
    public static final IRI DECIMAL = factory.createIRI(PREFIX + "decimal");
    public static final IRI FLOAT = factory.createIRI(PREFIX + "float");
    public static final IRI DOUBLE = factory.createIRI(PREFIX + "double");
    public static final IRI DURATION = factory.createIRI(PREFIX + "duration");
    public static final IRI DATE_TIME = factory.createIRI(PREFIX + "dateTime");
    public static final IRI TIME = factory.createIRI(PREFIX + "time");
    public static final IRI DATE = factory.createIRI(PREFIX + "date");
    public static final IRI G_YEAR_MONTH = factory.createIRI(PREFIX + "gYearMonth");
    public static final IRI G_YEAR = factory.createIRI(PREFIX + "gYear");
    public static final IRI G_MONTH_DAY = factory.createIRI(PREFIX + "gMonthDay");
    public static final IRI G_DAY = factory.createIRI(PREFIX + "gDay");
    public static final IRI G_MONTH = factory.createIRI(PREFIX + "gMonth");
    public static final IRI HEX_BINARY = factory.createIRI(PREFIX + "hexBinary");
    public static final IRI BASE_64_BINARY = factory.createIRI(PREFIX + "base64Binary");
    public static final IRI ANY_URI = factory.createIRI(PREFIX + "anyURI");
    public static final IRI QNAME = factory.createIRI(PREFIX + "QName");
    public static final IRI NOTATION = factory.createIRI(PREFIX + "NOTATION");
    public static final IRI INTEGER = factory.createIRI(PREFIX + "integer");
    public static final IRI NON_NEGATIVE_INTEGER = factory.createIRI(PREFIX + "nonNegativeInteger");
    public static final IRI UNISGNED_BYTE = factory.createIRI(PREFIX + "unsignedByte");
}
