package org.bridgedb.server;

import org.bridgedb.rdb.GdbProvider;
import org.bridgedb.bio.Organism;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class Contents extends ServerResource
{
	@Get
	public String getContents() 
	{
		try
		{
	        StringBuilder result = new StringBuilder();
	        for (Organism org : getGdbProvider().getOrganisms())
	        {
	        	result.append (org.shortName());
	        	result.append ("\t");
	        	result.append (org.latinName());
	        	result.append ("\n");
	        }	        
		    return result.toString();
		} 
		catch( Exception e ) 
		{
		    e.printStackTrace();
		    setStatus( Status.SERVER_ERROR_INTERNAL );
		    return e.getMessage();
		}
	}
	
	private GdbProvider getGdbProvider() {
		return ((IDMapperService)getApplication()).getGdbProvider();
	}	
}
