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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Resource that handles the attributes queries
 */
public class Attributes extends IDMapperResource {
	Xref xref;
	String attrType;
	
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			//Required parameters
			String id = urlDecode((String)getRequest().getAttributes().get(IDMapperService.PAR_ID));
			String dsName = urlDecode((String)getRequest().getAttributes().get(IDMapperService.PAR_SYSTEM));
			DataSource dataSource = parseDataSource(dsName);
			if(dataSource == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}
			xref = new Xref(id, dataSource);
			
			attrType = (String)getRequest().getAttributes().get(IDMapperService.PAR_TARGET_ATTR_NAME);
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String getAttributes() {
		try {
			if(attrType != null) {
				return getAttributesWithType();
			} else {
				return getAttributesWithoutType();
			}
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return e.getMessage();
		}
	}
	
	private String getAttributesWithType() throws IDMapperException {
		Set<String> values = new HashSet<String>();
		
		for(IDMapper mapper : getIDMappers()) {
			if(mapper instanceof AttributeMapper) {
				values.addAll(((AttributeMapper)mapper).getAttributes(xref, attrType));
			}
		}
		StringBuilder str = new StringBuilder();
		for(String v : values) {
			str.append(v);
			str.append("\n");
		}
		return str.toString();
	}
	
	private String getAttributesWithoutType() throws IDMapperException {
		Map<String, Set<String>> values = new HashMap<String, Set<String>>();
		
		for(IDMapper mapper : getIDMappers()) {
			if(mapper instanceof AttributeMapper) {
				values.putAll(((AttributeMapper)mapper).getAttributes(xref));
			}
		}
		StringBuilder str = new StringBuilder();
		for(String attr : values.keySet()) {
			for(String v : values.get(attr)) {
				str.append(attr);
				str.append("\t");
				str.append(v);
				str.append("\n");
			}
		}
		return str.toString();
	}	
}
