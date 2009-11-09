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
package org.bridgedb.rest;

import java.util.HashSet;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.rdb.IDMapperRdb;
import org.restlet.data.Status;
import org.restlet.resource.Get;

public class AttributeSet extends IDMapperResource
{
	@Get
	public String getSupportedDataSourceResult() 
	{
		try
		{
			Set<String> attributes = new HashSet<String>();
			
		    for(IDMapperRdb mapper : getIDMappers() ) 
		    {
		    	if(mapper instanceof AttributeMapper) {
		    		attributes.addAll(((AttributeMapper)mapper).getAttributeSet());
		    	}
		    }
		    StringBuilder result = new StringBuilder();
		    for(String a : attributes) {
		    	result.append(a);
		    	result.append("\n");
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
