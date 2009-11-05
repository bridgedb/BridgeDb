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
import org.bridgedb.bio.GdbProvider;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdb.IDMapperRdb;
import org.restlet.resource.ServerResource;

/**
 * Base resource implementation that provides methods shared
 * between the idmapper resources (such as access to the IDMapper objects).
 */
public class IDMapperResource extends ServerResource {
	protected DataSource parseDataSource(String dsName) {
		if(dsName == null) return null;
		DataSource ds = DataSource.getBySystemCode(dsName);
		if(ds == null) {
			ds = DataSource.getByFullName(dsName);
		}
		return ds;
	}
	
	protected List<IDMapperRdb> getIDMappers(String orgName) {
		Organism org = Organism.fromLatinName(orgName);
		if(org == null) { //Fallback on code
			org = Organism.fromCode(orgName);
		}
		if(org == null) { //Fallback on shortname
			org = Organism.fromShortName(orgName);
		}
		if(org == null) {
			throw new IllegalArgumentException("Unknown organism: " + orgName + "<p><font size='+1'><i>Double check the spelling. We are expecting an entry like: Human</i></font></p>");
		}
		List<IDMapperRdb> mappers = getGdbProvider().getGdbs(org);
		if (mappers.isEmpty()){
			throw new IllegalArgumentException("No database found for: " + orgName +"<p><font size='+1'><i>Verify that the database is supported and properly referenced in gdb.config.</i></font></p>");
		}
		return mappers;
	}
	
	private GdbProvider getGdbProvider() {
		return ((IDMapperService)getApplication()).getGdbProvider();
	}
}
