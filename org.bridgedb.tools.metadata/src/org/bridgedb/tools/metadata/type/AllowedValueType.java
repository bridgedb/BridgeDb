/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata.type;

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.tools.metadata.AppendBase;
import org.bridgedb.tools.metadata.constants.SchemaConstants;
import org.openrdf.model.URI;
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
