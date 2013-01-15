/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata.type;

import java.math.BigInteger;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class IntegerType implements MetaDataType{

    @Override
    public boolean correctType(Value value) {
        if (value instanceof Literal){
            Literal literal = (Literal)value;
            try {
                BigInteger test = literal.integerValue();
                return true;
            } catch (Exception ex) {  //NumberFormatException
                return false;
            }
        }
        return false;
    }

    @Override
    public String getCorrectType() {
        return " An Integer";
    }

  
}
