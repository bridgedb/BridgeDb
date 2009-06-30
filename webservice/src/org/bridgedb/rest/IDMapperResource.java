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
			throw new IllegalArgumentException("Unknown organism: " + orgName);
		}
		return getGdbProvider().getGdbs(org);
	}
	
	private GdbProvider getGdbProvider() {
		return ((IDMapperService)getApplication()).getGdbProvider();
	}
}
