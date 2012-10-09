// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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

/**
 * for all exceptions thrown by IDMapper interface. 
<p>	
	Simple wrapper so using classes don't need to deal with implementation details
	of specific IDMapper implementations. 
<p>
	Usually wraps SQLException, ClassNotFoundException, for IDMapperRdb,
	XmlRpcException for IDMapperWebservice and IOException for IDMapperText
<p>
	Simply use the inherited constructor IDMapperException(Throwable cause)
	to wrap an exception.
*/
public class UtilsException extends Exception
{

	/** 
	 * See Exception(Throwable). 
	 * @param t cause
	 */
	public UtilsException (Throwable t)
	{
		super (t);
	}
	
	/** 
	 * See Exception(String, Throwable). 
	 * @param msg a message
	 * @param t cause
	 */
	public UtilsException (String msg, Throwable t)
	{
		super (msg, t);
	}
	
	/** 
	 * See Exception (String). 
	 * @param msg a message
	 */
	public UtilsException (String msg)
	{
		super (msg);
	}
	
	/** See Exception (). */
	public UtilsException ()
	{
		super();
	}
}
