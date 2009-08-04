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
        public static final String PAR_SEARCH_STR = "searchStr";
       
	public static final String PAR_TARGET_SYSTEM = "dataSource";
        public static final String PAR_TARGET_ATTR_NAME = "attrName";
        public static final String PAR_TARGET_ATTR_VALUE = "attrValue";
        public static final String PAR_TARGET_LIMIT = "limit";
	
    public final File configFile;
    
    public IDMapperService(File aConfigFile)
    {
    	if (aConfigFile == null)
    	{
    		this.configFile = new File (CONF_GDBS);
    	}
    	else
    	{
    		this.configFile = aConfigFile;
    	}
    		
    	if (!configFile.exists())
    	{
    		System.err.println ("Could not find config file " + configFile);
    		System.err.println ("Please copy webservice/gdb.config.template and adjust it to your needs");
    		System.exit(1);
    	}
    }
    
    /*
	 * URL pattern for:
	 * /model/<organism>/<system>/<id>/xrefs[?dataSource=<dsCode>]
	 */
	public static final String URL_XREFS = "/model/{" + PAR_ORGANISM + "}/{" + PAR_SYSTEM + "}/{" 
											+ PAR_ID + "}/xrefs";
        /*
         * URL pattern for:
         * /model/<organism>/xrefsByAttr?attrName=<attrName>&attrValue=<attrValue>
         */
        public static final String URL_XREFS_BY_ATTR = "/model/{" + PAR_ORGANISM + "}/xrefsByAttr";

        /* 
         * URL pattern for:
         * /model/<organism>/<system>/<id>/backPageText
         */
        public static final String URL_BACK_PAGE_TEXT = "/model/{" + PAR_ORGANISM + "}/{" + PAR_SYSTEM + "}/{" + 
            PAR_ID + "}/backPageText";

        /*
	 * URL pattern for:
	 * /search/<organism>/symbol/<searchStr>&limit=<limit>
         */
        public static final String URL_SEARCH_SYMBOL = "/search/{" + PAR_ORGANISM + "}/symbol/{" + PAR_SEARCH_STR + "}";

        /*
	 * URL pattern for:
	 * /search/<organism>/id/<searchStr>&limit=<limit>
	 */
    public static final String URL_SEARCH_ID = "/search/{" + PAR_ORGANISM + "}/id/{" + PAR_SEARCH_STR + "}";
        /*
	 * URL pattern for:
	 * /search/symbolOrId/<searchStr>&limit=<limit>
	 */
    public static final String URL_SEARCH_SYMBOL_OR_ID = "/search/{" + PAR_ORGANISM + "}/symbolOrId/{" + PAR_SEARCH_STR + "}";

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

		// Register the route for xrefsByAttr
                Route xrefsByAttrRoute = router.attach( URL_XREFS_BY_ATTR, XrefsByAttr.class );
                xrefsByAttrRoute.extractQuery(PAR_TARGET_ATTR_NAME,PAR_TARGET_ATTR_NAME, true );
                xrefsByAttrRoute.extractQuery(PAR_TARGET_ATTR_VALUE,PAR_TARGET_ATTR_VALUE, true );
		
		// Register the route for backPageText
		Route backPageTextRoute = router.attach( URL_BACK_PAGE_TEXT, BackPageText.class );
	       
		// Register the route for search-symbol
		Route searchSymbolRoute = router.attach( URL_SEARCH_SYMBOL, SearchSymbol.class );
		searchSymbolRoute.extractQuery( PAR_TARGET_LIMIT, PAR_TARGET_LIMIT, true );

		// Register the route for search-id
		Route searchIdRoute = router.attach( URL_SEARCH_ID, SearchId.class );
	        searchIdRoute.extractQuery( PAR_TARGET_LIMIT, PAR_TARGET_LIMIT, true );
		// Register the route for search-symbol-or-id
		Route searchSymbolOrIdRoute = router.attach( URL_SEARCH_SYMBOL_OR_ID, SearchSymbolOrId.class );
		searchSymbolOrIdRoute.extractQuery( PAR_TARGET_LIMIT, PAR_TARGET_LIMIT, true );

		return router;
	}
	
	public GdbProvider getGdbProvider() {
		return gdbProvider;
	}
	
	private void connectGdbs() throws IDMapperException, IOException {
		String[] gdbconf = getContext().getParameters().getValuesArray(CONF_GDBS);
		File gdbFile = configFile;
		if(gdbconf.length > 0) {
			gdbFile = new File(gdbconf[0]);
		}
		gdbProvider = GdbProvider.fromConfigFile(gdbFile);
	}
}
