package org.bridgedb.server;

import org.bridgedb.bio.DataSourceTsv;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class DataSources extends ServerResource {

	protected void doInit() {
		DataSourceTsv.init();
	}
	
	@Get
	public String getDataSources() {
		return DataSourceTsv.datasourcesTsv;	
	}
}
