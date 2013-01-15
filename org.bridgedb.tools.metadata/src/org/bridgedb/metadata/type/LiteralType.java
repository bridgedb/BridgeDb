/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import java.math.BigInteger;
import org.bridgedb.metadata.constants.RdfsConstants;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class LiteralType implements MetaDataType{

    public LiteralType(){
    }
    
    @Override
    public boolean correctType(Value value) {
        if (value instanceof Literal){
            return true;
         }
        return false;
    }

    @Override
    public String getCorrectType() {
        return RdfsConstants.LITERAL;
    }

  
}
