/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import java.util.ArrayList;
import java.util.List;
import org.bridgedb.metadata.AppendBase;
import org.bridgedb.metadata.constants.SchemaConstants;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Christian
 */
public class AllowedUriType implements MetaDataType{

    List<URI> allowedValues = new ArrayList<URI>();
    
    public AllowedUriType(Element element){
        NodeList list = element.getElementsByTagName(SchemaConstants.ALLOWED_VALUE);
        for (int i = 0; i < list.getLength(); i++){
            Node node = list.item(i);
            String stringValue = node.getFirstChild().getNodeValue();
            allowedValues.add(new URIImpl(stringValue));
        }
    }
    
    @Override
    public boolean correctType(Value value) {
        return allowedValues.contains(value);
    }

    @Override
    public String getCorrectType() {
        return " URI in " + allowedValues;
    }

  
}
