/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class MetaDataException extends IDMapperException {

    /**
     * Creates a new instance of <code>MetaDataException</code> without detail message.
     */
    public MetaDataException() {
    }

    /**
     * Constructs an instance of <code>MetaDataException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public MetaDataException(String msg) {
        super(msg);
    }

    public MetaDataException(String msg, Exception cause) {
        super(msg, cause);
    }
}
