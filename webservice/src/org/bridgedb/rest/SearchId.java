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
public class SearchId extends IDMapperResource {
	List<IDMapperRdb> mappers;
	Xref xref;
	String searchStr;
        int limit;
  	String org;
	
	protected void doInit() throws ResourceException {
		try {

		    org = (String) getRequest().getAttributes().get( IDMapperService.PAR_ORGANISM );
		    mappers = getIDMappers(org);
		    System.out.println( "1" );
		    searchStr = (String) getRequest().getAttributes().get( IDMapperService.PAR_SEARCH_STR );
		    System.out.println( "2: " + searchStr );
	       	    String limitStr = (String)getRequest().getAttributes().get( IDMapperService.PAR_TARGET_LIMIT );
		    System.out.println( "3: " + limitStr );
	     	    limit = new Integer( limitStr ).intValue();


		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String getSearchIdResult() 
	{
	  try 
	  {
	    //The result set
	    Set<Xref> suggestions = new HashSet<Xref>();
	    
	    for(IDMapperRdb mapper : mappers ) {
		suggestions.addAll( mapper.getIdSuggestions( searchStr, limit ) );
	    }
	    
            StringBuilder result = new StringBuilder();
	    for( Xref x : suggestions ) {
		result.append( x.getId() );
		result.append( "\t" );
		result.append( x.getDataSource().getFullName() );
		result.append( "\n" );
	    }

	    return( result.toString() );
          } catch( Exception e ) {
	    e.printStackTrace();
	    setStatus( Status.SERVER_ERROR_INTERNAL );
	    return e.getMessage();
	  }
	}

}
