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

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.tools.metadata.constants.SchemaConstants;
import org.openrdf.model.Value;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Christian
 */
public class AllowedValueType implements MetaDataType{

    List<String> allowedValues = new ArrayList<String>();
    
    public AllowedValueType(Element element){
        NodeList list = element.getElementsByTagName(SchemaConstants.ALLOWED_VALUE);
        for (int i = 0; i < list.getLength(); i++){
            Node node = list.item(i);
            allowedValues.add(node.getFirstChild().getNodeValue());
        }
    }
    
    @Override
    public boolean correctType(Value value) {
        String stringValue = value.stringValue();
        if (stringValue == null){
            return false;
        }
        return allowedValues.contains(stringValue);
    }

    @Override
    public String getCorrectType() {
        return " One of " + allowedValues;
    }

  
}
