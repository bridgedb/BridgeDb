package org.bridgedb.server;

import org.bridgedb.bio.DataSourceTxt;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class DataSources extends ServerResource {

	protected void doInit() {
		DataSourceTxt.init();
	}
	
	@Get
	public String getDataSources() {
		return DataSourceTxt.datasourcesTxt;	
	}
}
