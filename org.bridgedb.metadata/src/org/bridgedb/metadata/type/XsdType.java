/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import java.math.BigInteger;
import org.bridgedb.metadata.constants.XMLSchemaConstants;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class XsdType implements MetaDataType{

    URI datatype;
    
    public XsdType(String type){
        datatype = new URIImpl(type);
    }
    
    @Override
    public boolean correctType(Value value) {
        if (value instanceof Literal){
            Literal literal = (Literal)value;
            URI literalType = literal.getDatatype();
            if (literalType == null){
                return false;
            }
            return sameOrSubTye(literalType);
        }
        return false;
    }

    private final boolean sameOrSubTye(Value literalType){
        if (datatype.equals(literalType)){
                return true;
        }
        if (datatype.stringValue().equalsIgnoreCase(literalType.stringValue())){
            return true;
        }
        if (datatype.equals(XMLSchemaConstants.INTEGER)){
            if (literalType.equals(XMLSchemaConstants.NON_NEGATIVE_INTEGER)){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getCorrectType() {
        return datatype.stringValue();
    }

  
}
