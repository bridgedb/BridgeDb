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
public class SearchSymbol extends IDMapperResource {
	List<IDMapperRdb> mappers;
	Xref xref;
	String searchStr;
	int limit;
  	String org;
	
	protected void doInit() throws ResourceException {
		try {
		    System.out.println( "SearchSymbol.init() start" );
		    org = (String) getRequest().getAttributes().get( IDMapperService.PAR_ORGANISM );
		    mappers = getIDMappers(org);
		    System.out.println( "1" );
		    searchStr = (String) getRequest().getAttributes().get( IDMapperService.PAR_SEARCH_STR );
		    System.out.println( "2: " + searchStr );
	       	    String limitStr = (String)getRequest().getAttributes().get( IDMapperService.PAR_TARGET_LIMIT );

	     	    limit = new Integer( limitStr ).intValue();

		    System.out.println( "SearchSymbol.doInit() done" );
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String getSearchSymbolResult() 
	{
	  System.out.println( "SearchSymbol.getSearchSymbolResult() start" );
	  try 
	  {
	    //The result set
	    Set<String> suggestions = new HashSet<String>();
	    
	    for(IDMapperRdb mapper : mappers ) {
		suggestions.addAll( mapper.getSymbolSuggestions( searchStr, limit ) );
	    }
	    
            StringBuilder result = new StringBuilder();
	    for( String x : suggestions ) {
	      result.append( x + "\n" );
	    }

	    return( result.toString() );
          } catch( Exception e ) {
	    e.printStackTrace();
	    setStatus( Status.SERVER_ERROR_INTERNAL );
	    return e.getMessage();
	  }
	}

}
