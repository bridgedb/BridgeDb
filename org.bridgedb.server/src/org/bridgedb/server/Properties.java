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
package org.bridgedb.server;

import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperStack;
import org.restlet.data.Status;
import org.restlet.resource.Get;

public class Properties extends IDMapperResource
{
	@Get
	public String getPropertiesResult() 
	{
		try
		{
	        StringBuilder result = new StringBuilder();
	        IDMapperStack stack = getIDMappers();
		    for(int i = 0; i < stack.getSize(); ++i) 
		    {
		    	IDMapper mapper = stack.getIDMapperAt(i);
		    	for (String key : mapper.getCapabilities().getKeys())
		    	{
		    		result.append( key );
		    		result.append( "\t" );
		    		result.append( mapper.getCapabilities().getProperty(key) );
		    		result.append( "\n" );
		    	}
		    }
		    return result.toString();
		} 
		catch( Exception e ) 
		{
		    e.printStackTrace();
		    setStatus( Status.SERVER_ERROR_INTERNAL );
		    return e.getMessage();
		}
	}
}
