/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class BridgeDBException extends IDMapperException {

    static final Logger logger = Logger.getLogger(BridgeDBException.class);

	/** 
	 * See Exception (String). 
	 * @param msg a message
	 */
	public BridgeDBException (String msg)
	{
		super (msg);
        logger.error(msg, this);
	}

    /** 
	 * See Exception(String, Throwable). 
	 * @param msg a message
	 * @param t cause
	 */
	public BridgeDBException (String msg, Throwable t)
	{
		super (msg, t);
        logger.error(msg, t);
	}
}
