// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009 BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
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

    /** 
	 * See Exception(String, Throwable). 
	 * @param msg a message
	 * @param t cause
	 */
	public BridgeDBException (String msg, Throwable t, String query)
	{
		super (msg + "with Query " + query, t);
        logger.error(msg + "with Query " + query, t);
	}

    /** 
	 * See Exception(String, Throwable). 
	 * @param msg a message
	 * @param t cause
	 */
	public BridgeDBException (IDMapperException t)
	{
		super (t);
        logger.error(t);
	}
}
