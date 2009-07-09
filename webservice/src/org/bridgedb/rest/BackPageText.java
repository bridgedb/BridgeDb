package org.bridgedb.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.rdb.IDMapperRdb;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Resource that handles the xref queries
 */
public class BackPageText extends IDMapperResource {
	List<IDMapperRdb> mappers;
	Xref xref;
	DataSource targetDs;
	
	protected void doInit() throws ResourceException {
		try {
		    System.out.println( "Xrefs.doInit start" );
			//Required parameters
			String org = (String)getRequest().getAttributes().get(IDMapperService.PAR_ORGANISM);
			mappers = getIDMappers(org);

			String id = (String)getRequest().getAttributes().get(IDMapperService.PAR_ID);
			String dsName = (String)getRequest().getAttributes().get(IDMapperService.PAR_SYSTEM);
			DataSource dataSource = parseDataSource(dsName);
			if(dataSource == null) {
				throw new IllegalArgumentException("Unknown datasource: " + dsName);
			}
			xref = new Xref(id, dataSource);
			
			//Optional parameters
			String targetDsName = (String)getRequest().getAttributes().get(IDMapperService.PAR_TARGET_SYSTEM);
			targetDs = parseDataSource(targetDsName);
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String getBackPageText() 
	{
	  System.out.println( "Xrefs.getBackPageText() start" );
	  try 
	  {
	    //The result set
	    Set<String> bpInfos = new HashSet<String>();
	    
	    for(IDMapperRdb mapper : mappers ) {
		bpInfos.add( mapper.getBpInfo( xref ) );
	    }
	    
            StringBuilder result = new StringBuilder();
	    for( String x : bpInfos ) {
	      result.append( x );
	    }

	    return( result.toString() );
          } catch( Exception e ) {
	    e.printStackTrace();
	    setStatus( Status.SERVER_ERROR_INTERNAL );
	    return e.getMessage();
	  }
	}

}
