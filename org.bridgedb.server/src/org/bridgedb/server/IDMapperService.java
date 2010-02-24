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
package org.bridgedb.server;

import java.io.File;
import java.io.IOException;

import org.bridgedb.AttributeMapper;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.rdb.GdbProvider;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Redirector;
import org.restlet.routing.Route;
import org.restlet.routing.Router;

public class IDMapperService extends Application {
	public static final String CONF_GDBS = "gdb.config";

	public static final String PAR_ORGANISM = "organism";
	public static final String PAR_ID = "id";
	public static final String PAR_SYSTEM = "system";
	public static final String PAR_QUERY = "query";

	public static final String PAR_TARGET_SYSTEM = "dataSource";
	public static final String PAR_TARGET_ATTR_NAME = "attrName";
	public static final String PAR_TARGET_LIMIT = "limit";
	
	public static final String PAR_SOURCE_SYSTEM = "src";
	public static final String PAR_DEST_SYSTEM = "dest";

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
			System.err.println ("Please copy org.bridgedb.server/gdb.config.template and adjust it to your needs");
			System.exit(1);
		}
	}

	/**
	 * URL pattern to redirect to home page.<BR>
	 * 
	 * <code>
	 * {blank}
	 * </code>
	 */
	public static final String URL_HOME = "/";

	/**
 	 * URL pattern to catch a misformed query.<BR>
 	 *
 	 * <code>
 	 * {unmatched patter}
 	 * <code>
	 */
	public static final String URL_NO_MATCH = "/{" + PAR_ORGANISM + "}";

	/**
	 * URL pattern for mapping xrefs.<BR>
	 * <code>
	 * /{organism}/xrefs/{system}/{id}[?dataSource={dsName}]
	 * </code> 
	 * @see IDMapper#mapID(org.bridgedb.Xref, org.bridgedb.DataSource...)
	 */
	public static final String URL_XREFS = "/{" + PAR_ORGANISM + "}/xrefs/{" + PAR_SYSTEM + "}/{" + 
		PAR_ID + "}";

	/**
	 * URL pattern for searching xrefs.<BR>
	 * <code>
	 * /{organism}/search/{query}[?limit={limit}]
	 * </code>
	 * @see IDMapper#freeSearch(String, int)
	 */
	public static final String URL_SEARCH = "/{" + PAR_ORGANISM + "}/search/{" + PAR_QUERY + "}";
	
	/**
	 * URL pattern for finding out if an xref exists in the database.<BR>
	 * <code>
	 * /{organism}/xrefExists/{system}/{id}
	 * </code>
	 * @see IDMapper#xrefExists(org.bridgedb.Xref)
	 */
	public static final String URL_XREF_EXISTS = "/{" + PAR_ORGANISM + "}/xrefExists/{" + PAR_SYSTEM + "}/{" + PAR_ID + "}";

	/**
	 * URL pattern for getting IDMapper properties. Returns tab delimited text with a property on each line, 
	 * where the first column is the property key, the second is the property value.<BR>
	 * <code>
	 * /{organism}/properties
	 * </code>
	 * @see IDMapperCapabilities#getKeys()
	 * @see IDMapperCapabilities#getProperty(String)
	 */
	public static final String URL_PROPERTIES = "/{" + PAR_ORGANISM + "}/properties";

	/**
	 * URL pattern for getting the supported source datasources for this database.<BR>
	 * <code>
	 * /{organism}/sourceDataSources
	 * </code>
	 * @see IDMapperCapabilities#getSupportedSrcDataSources()
	 */
	public static final String URL_SUPPORTED_SOURCE_DATASOURCES = "/{" + PAR_ORGANISM + "}/sourceDataSources";
	
	/**
	 * URL pattern for getting the supported target datasources for this database.<BR>
	 * <code>
	 * /{organism}/targetDataSources
	 * </code>
	 * @see IDMapperCapabilities#getSupportedTgtDataSources()
	 */
	public static final String URL_SUPPORTED_TARGET_DATASOURCES = "/{" + PAR_ORGANISM + "}/targetDataSources";
	
	/**
	 * URL pattern for finding out if free search is supported.<BR>
	 * <code>
	 * /{organism}/isFreeSearchSupported
	 * </code>
	 * @see IDMapperCapabilities#isFreeSearchSupported()
	 */
	public static final String URL_IS_FREE_SEARCH_SUPPORTED = "/{" + PAR_ORGANISM + "}/isFreeSearchSupported";
	
	/**
	 * URL pattern to find out if a mapping is supported.<BR>
	 * <code>
	 * /{organism}/isMappingSupported/{source_system}/{target_system}
	 * </code>
	 * @see IDMapperCapabilities#isMappingSupported(org.bridgedb.DataSource, org.bridgedb.DataSource)
	 */
	public static final String URL_IS_MAPPING_SUPPORTED = "/{" + PAR_ORGANISM + "}/isMappingSupported/{" + PAR_SOURCE_SYSTEM + "}/{" + PAR_DEST_SYSTEM + "}";

	/**
	 * URL pattern for searching by attribute. Returns tab delimited text with an xref
	 * result on each line, where the first column is the identifier, the second column
	 * is the datasource and the third column is the attribute value.<BR>
	 * <code>
	 * /{organism}/attributeSearch/{query}?[limit={limit}][&attrName={attrName}]
	 * </code>
	 * @see AttributeMapper#freeAttributeSearch(String, String, int)
	 */
	public static final String URL_ATTRIBUTE_SEARCH = "/{" + PAR_ORGANISM + "}/attributeSearch/{" + PAR_QUERY + "}";
	
	/**
	 * URL pattern for getting attributes of an xref. If attrName is supplied, this method
	 * returns plain text with an attribute value on each line. If attrName is not supplied, 
	 * this method returns tab delimited text with an attribute key-value pair on each line.
	 * <BR>
	 * /{organism}/attributes/{system}/{id}[?attrName={attrName}]
	 * 
	 * @see AttributeMapper#getAttributes(org.bridgedb.Xref, String)
	 * @see AttributeMapper#getAttributes(org.bridgedb.Xref)
	 */
	public static final String URL_ATTRIBUTES = "/{" + PAR_ORGANISM + "}/attributes/{" + PAR_SYSTEM + "}/{" + PAR_ID + "}";

	/**
	 * URL pattern for getting the supported attribute set.<BR>
	 * <code>
	 * /{organism}/attributeSet
	 * </code>
	 * @see AttributeMapper#getAttributeSet()
	 */
	public static final String URL_ATTRIBUTE_SET = "/{" + PAR_ORGANISM + "}/attributeSet";

	/**
	 * URL pattern for getting the server configuration.<BR>
	 * <code>
	 * /config
	 * </code>
	 * @see Config#getConfig()
	 */
	public static final String URL_CONFIG = "/config";

	/**
	 * URL pattern for getting backpage HTML.<BR>
	 * URL:<BR>
	 * /{organism}/backPageText/{system}/{id}
	 */
	public static final String URL_BACK_PAGE_TEXT = "/{" + PAR_ORGANISM + "}/backPageText/{" + PAR_SYSTEM + "}/{" + 
		PAR_ID + "}";

	private GdbProvider gdbProvider;

	public synchronized void start() throws Exception {
		super.start();
		BioDataSource.init();
		connectGdbs();
	}

	public Restlet createRoot() {
		Router router = new Router(getContext());
		
		//router.setDefaultMatchingMode(Router.BEST);		
		//System.out.println("MatchingMode: "+ router.getDefaultMatchingMode() + " : "+ router.getRequiredScore());


		//Register the route for the home page url pattern
		String target = "http://bridgedb.org/wiki/BridgeWebservice";
		Redirector redirector = new Redirector(getContext(), target, Redirector.MODE_CLIENT_TEMPORARY);	
		router.attach(URL_HOME, redirector);

		router.attach(URL_CONFIG, Config.class);

		/* IDMapper methods */
		//Register the route for the xrefs url pattern
		Route xrefsRoute = router.attach(URL_XREFS, Xrefs.class);
		//Specify that the dataSource parameter needs to be included
		//in the attributes
		xrefsRoute.extractQuery(PAR_TARGET_SYSTEM, PAR_TARGET_SYSTEM, true);

		Route searchRoute = router.attach( URL_SEARCH, FreeSearch.class );
		searchRoute.extractQuery( PAR_TARGET_LIMIT, PAR_TARGET_LIMIT, true );
		
		router.attach(URL_XREF_EXISTS, XrefExists.class);
		
		/* IDMapperCapabilities methods */
		router.attach (URL_PROPERTIES, Properties.class );

		router.attach (
				URL_SUPPORTED_SOURCE_DATASOURCES, SupportedSourceDataSources.class );
		router.attach (
				URL_SUPPORTED_TARGET_DATASOURCES, SupportedTargetDataSources.class );
		
		router.attach(URL_ATTRIBUTE_SET, AttributeSet.class);
		
		router.attach(URL_IS_FREE_SEARCH_SUPPORTED, IsFreeSearchSupported.class);
		
		router.attach(URL_IS_MAPPING_SUPPORTED, IsMappingSupported.class);
		
		/* AttributeMapper methods */
		Route attrSearchRoute = router.attach( URL_ATTRIBUTE_SEARCH, AttributeSearch.class );
		attrSearchRoute.extractQuery( PAR_TARGET_LIMIT, PAR_TARGET_LIMIT, true );
		attrSearchRoute.extractQuery( PAR_TARGET_ATTR_NAME, PAR_TARGET_ATTR_NAME, true );
		
		Route attributesRoute = router.attach(URL_ATTRIBUTES, Attributes.class );
		attributesRoute.extractQuery( PAR_TARGET_ATTR_NAME, PAR_TARGET_ATTR_NAME, true );
		
		/* Extra methods */
		// Register the route for backPageText
		router.attach( URL_BACK_PAGE_TEXT, BackPageText.class );
		

                //Register the route for a url pattern that doesn't match other patterns
                router.attach(URL_NO_MATCH, NoMatch.class);
                
		return router;
	}

	public GdbProvider getGdbProvider() {
		return gdbProvider;
	}

	private void connectGdbs() throws IDMapperException, IOException, ClassNotFoundException 
	{
		String[] gdbconf = getContext().getParameters().getValuesArray(CONF_GDBS);
		File gdbFile = configFile;
		if(gdbconf.length > 0) {
			gdbFile = new File(gdbconf[0]);
		}
		gdbProvider = GdbProvider.fromConfigFile(gdbFile);
	}
}
