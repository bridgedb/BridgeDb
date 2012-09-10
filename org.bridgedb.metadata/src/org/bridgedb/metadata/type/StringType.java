/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class StringType implements MetaDataType{

    @Override
    public boolean correctType(Value value) {
        String stringValue = value.stringValue();
        if (stringValue == null){
            return false;
        }
        return !stringValue.isEmpty();
    }
    
}
