/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata.type;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class UriType implements MetaDataType{

    @Override
    public boolean correctType(Value value) {
        return value instanceof URI;
    }

    @Override
    public String getCorrectType() {
        return "A URI.";
    }
    
}
