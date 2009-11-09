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

import java.util.HashMap;
import java.util.Map;

import org.bridgedb.AttributeMapper;
import org.bridgedb.Xref;
import org.bridgedb.rdb.IDMapperRdb;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Resource that handles the xref queries
 */
public class AttributeSearch extends IDMapperResource {
	String searchStr;
	String attribute;
	int limit = 0;

	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			searchStr = urlDecode((String) getRequest().getAttributes().get( IDMapperService.PAR_QUERY ));
			attribute = urlDecode((String)getRequest().getAttributes().get( IDMapperService.PAR_TARGET_ATTR_NAME ));
			String limitStr = (String)getRequest().getAttributes().get( IDMapperService.PAR_TARGET_LIMIT );

			if ( null != limitStr ) 
			{
				limit = new Integer( limitStr ).intValue();
			}
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String search() 
	{
		try 
		{
			Map<Xref, String> results = new HashMap<Xref, String>();

			for(IDMapperRdb mapper : getIDMappers() ) {
				if(mapper instanceof AttributeMapper) {
					results.putAll(((AttributeMapper)mapper).freeAttributeSearch(searchStr, attribute, limit));
				}
			}

			StringBuilder result = new StringBuilder();
			for(Xref x : results.keySet()) {
				result.append(x.getId());
				result.append("\t");
				result.append(x.getDataSource().getFullName());
				result.append("\t");
				result.append(results.get(x));
				result.append("\n");
			}

			return(result.toString());
		} catch( Exception e ) {
			e.printStackTrace();
			setStatus( Status.SERVER_ERROR_INTERNAL );
			return e.getMessage();
		}
	}

}
