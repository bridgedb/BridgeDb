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

import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.rdb.IDMapperRdb;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class SupportedDataSources extends IDMapperResource 
{
	List<IDMapperRdb> mappers;
  	String org;

  	protected void doInit() throws ResourceException 
	{
		try {
		    System.out.println( "SupportedDataSources.init() start" );
		    org = (String) getRequest().getAttributes().get( IDMapperService.PAR_ORGANISM );
		    mappers = getIDMappers(org);
		    System.out.println( "SupportedDataSources.doInit() done" );
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String getSupportedDataSourceResult() 
	{
		try
		{
	        StringBuilder result = new StringBuilder();
		    for(IDMapperRdb mapper : mappers ) 
		    {
		    	for (DataSource ds : mapper.getCapabilities().getSupportedSrcDataSources())
		    	{
		    		result.append(ds.getSystemCode());
		    		result.append ("\n");
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
