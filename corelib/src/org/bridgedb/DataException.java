// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
package org.bridgedb;

/**
 * for all exceptions thrown by IGdb or IGex interfaces. 
	
	Simple wrapper so using classes don't need to deal with implemenation details
	of the Gdb/Gex. Usually wraps SQLException, ClassNotFoundException, etc.

	Simply use the inherited constructor DataException(Throwable cause)
	to wrap an exception.
*/
public class DataException extends Exception
{

	/** See Exception(Throwable) */
	public DataException (Throwable t)
	{
		super (t);
	}
	
	/** See Exception(String, Throwable) */
	public DataException (String msg, Throwable t)
	{
		super (msg, t);
	}
	
	/** See Exception (String) */
	DataException (String msg)
	{
		super (msg);
	}
	
	/** See Exception () */
	DataException ()
	{
		super();
	}
}
