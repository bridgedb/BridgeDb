package org.bridgedb.server;

import java.util.Properties;

import org.bridgedb.BridgeDb;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class Config extends ServerResource
{
	@Get
	public String getConfig() 
	{
		try
		{
			Properties props = new Properties();
			props.load (BridgeDb.class.getResourceAsStream("BridgeDb.properties"));			
	        StringBuilder result = new StringBuilder();
	        result.append ("java.version\t" + System.getProperty("java.version") + "\n");	        
	        result.append ("bridgedb.version\t" + props.getProperty("bridgedb.version") + "\n");
	        result.append ("bridgedb.revision\t" + props.getProperty("REVISION") + "\n");	        
		    return result.toString();
		} 
		catch( Exception e ) 
		{
		    e.printStackTrace();
		    setStatus( Status.SERVER_ERROR_INTERNAL );
		    return e.getMessage();
		}
	}
	
}
