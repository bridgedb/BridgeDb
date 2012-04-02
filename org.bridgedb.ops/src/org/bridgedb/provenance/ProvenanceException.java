/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.provenance;

import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class ProvenanceException extends IDMapperException {

    /**
     * Creates a new instance of <code>ProvenanceException</code> without detail message.
     */
    public ProvenanceException() {
    }

    /**
     * Constructs an instance of <code>ProvenanceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ProvenanceException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ProvenanceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ProvenanceException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
