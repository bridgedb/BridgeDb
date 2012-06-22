/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class IDMapperLinksetException extends IDMapperException {

    /**
     * Creates a new instance of <code>IDMapperLinksetException</code> without detail message.
     */
    public IDMapperLinksetException() {
    }

    /**
     * Constructs an instance of <code>IDMapperLinksetException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public IDMapperLinksetException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>IDMapperLinksetException</code> with the specified detail message.
     * @param msg the detail message.
     * @param ex Exception thrown and wrapped
     */
    public IDMapperLinksetException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
