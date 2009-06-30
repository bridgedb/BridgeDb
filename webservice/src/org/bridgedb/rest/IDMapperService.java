package org.bridgedb.rest;

import java.io.File;
import java.io.IOException;

import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.bio.GdbProvider;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Route;
import org.restlet.routing.Router;

public class IDMapperService extends Application {
	public static final String CONF_GDBS = "gdb.config";
	
	public static final String PAR_ORGANISM = "organism";
	public static final String PAR_ID = "id";
	public static final String PAR_SYSTEM = "system";
	public static final String PAR_TARGET_SYSTEM = "dataSource";
	
	/*
	 * URL pattern for:
	 * /model/<organism>/<system>/<id>/xrefs[&dataSource=<dsCode>]
	 */
	public static final String URL_XREFS = "/model/{" + PAR_SYSTEM + "}/{" + PAR_ORGANISM + "}/{" 
											+ PAR_ID + "}/xrefs";
	
	private GdbProvider gdbProvider;
	
	public synchronized void start() throws Exception {
		super.start();
		BioDataSource.init();
		connectGdbs();
	}
	
	public Restlet createRoot() {
		Router router = new Router(getContext());
		
		//Register the route for the xrefs url pattern
		Route xrefsRoute = router.attach(URL_XREFS, Xrefs.class);
		//Specify that the dataSource parameter needs to be included
		//in the attributes
		xrefsRoute.extractQuery(PAR_TARGET_SYSTEM, PAR_TARGET_SYSTEM, true);
		
		return router;
	}
	
	public GdbProvider getGdbProvider() {
		return gdbProvider;
	}
	
	private void connectGdbs() throws IDMapperException, IOException {
		String[] gdbconf = getContext().getParameters().getValuesArray(CONF_GDBS);
		File gdbFile = new File("gdb.config");
		if(gdbconf.length > 0) {
			gdbFile = new File(gdbconf[0]);
		}
		gdbProvider = GdbProvider.fromConfigFile(gdbFile);
	}
}
