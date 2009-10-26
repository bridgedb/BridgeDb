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
import org.bridgedb.Xref;
import org.bridgedb.rdb.IDMapperRdb;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class XrefExists extends IDMapperResource 
{
	List<IDMapperRdb> mappers;
	Xref xref;
  	String org;

  	protected void doInit() throws ResourceException 
	{
		try {
		    org = (String) getRequest().getAttributes().get( IDMapperService.PAR_ORGANISM );
		    mappers = getIDMappers(org);
		    
		    String id = (String)getRequest().getAttributes().get(IDMapperService.PAR_ID);
			String dsName = (String)getRequest().getAttributes().get(IDMapperService.PAR_SYSTEM);
			DataSource dataSource = parseDataSource(dsName);
			if(dataSource == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}
			xref = new Xref(id, dataSource);
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String isFreeSearchSupported() 
	{
		try
		{
			boolean exists = false;
		    for(IDMapperRdb mapper : mappers ) 
		    {
		    	if(mapper.xrefExists(xref)) {
		    		exists = true;
		    		break;
		    	}
		    }
		    return "" + exists;
		} 
		catch( Exception e ) 
		{
		    e.printStackTrace();
		    setStatus( Status.SERVER_ERROR_INTERNAL );
		    return e.getMessage();
		}
	}
}
