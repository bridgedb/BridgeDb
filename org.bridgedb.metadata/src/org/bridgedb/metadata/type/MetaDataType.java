/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import org.bridgedb.metadata.MetaDataException;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public interface MetaDataType {
    
    boolean correctType(Value value) throws MetaDataException;

    String getCorrectType();
}
