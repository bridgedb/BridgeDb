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

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Resource that handles the xref queries
 */
public class Xrefs extends IDMapperResource {
	Xref xref;
	DataSource targetDs;
	
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
		    System.out.println( "Xrefs.doInit start" );
			//Required parameters
			String id = urlDecode((String)getRequest().getAttributes().get(IDMapperService.PAR_ID));
			String dsName = urlDecode((String)getRequest().getAttributes().get(IDMapperService.PAR_SYSTEM));
			DataSource dataSource = parseDataSource(dsName);
			if(dataSource == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}
			xref = new Xref(id, dataSource);
			
			//Optional parameters
			String targetDsName = (String)getRequest().getAttributes().get(IDMapperService.PAR_TARGET_SYSTEM);
			if(targetDsName != null) {
				targetDs = parseDataSource(urlDecode(targetDsName));
			}
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String getXrefs() {
	   System.out.println( "Xrefs.getXrefs() start" );
		try {
			//The result set

			IDMapper mapper = getIDMappers().get(0);
			Set<Xref> xrefs = (targetDs == null) ? mapper.mapID(xref) : mapper.mapID(xref, targetDs);

			for (int i = 1; i < getIDMappers().size(); ++i)
			{
				mapper = getIDMappers().get(i);
				xrefs.addAll ((targetDs == null) ? mapper.mapID(xref) : mapper.mapID(xref, targetDs));
			}
					
			StringBuilder result = new StringBuilder();
			for(Xref x : xrefs) {
				result.append(x.getId());
				result.append("\t");
				result.append(x.getDataSource().getFullName());
				result.append("\n");
			}
			
			return result.toString();
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return e.getMessage();
		}
	}
}
