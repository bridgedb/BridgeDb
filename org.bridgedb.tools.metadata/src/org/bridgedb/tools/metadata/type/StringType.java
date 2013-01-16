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
package org.bridgedb.tools.metadata.type;

import org.bridgedb.rdf.constants.XMLSchemaConstants;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class StringType implements MetaDataType{

    @Override
    public boolean correctType(Value value) {
        if (value instanceof Literal){
            Literal literal = (Literal)value;
            URI literalType = literal.getDatatype();
            if (literalType != null){
                return (XMLSchemaConstants.STRING.equals(literalType));
            }
        }
        String stringValue = value.stringValue();
        if (stringValue == null){
            return false;
        }
        return !stringValue.isEmpty();
    }

    @Override
    public String getCorrectType() {
        return " A String";
    }

  
}
