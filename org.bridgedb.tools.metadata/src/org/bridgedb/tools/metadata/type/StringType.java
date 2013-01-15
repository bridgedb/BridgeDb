/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata.type;

import org.bridgedb.tools.metadata.AppendBase;
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
