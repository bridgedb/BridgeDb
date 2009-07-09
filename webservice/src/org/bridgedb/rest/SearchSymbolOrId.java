package org.bridgedb.rest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.XrefWithSymbol;
import org.bridgedb.rdb.IDMapperRdb;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Resource that handles the xref queries
 */
public class SearchSymbolOrId extends IDMapperResource {
	List<IDMapperRdb> mappers;
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
		    System.out.println( "3: " + limitStr );
	     	    limit = new Integer( limitStr ).intValue();

		    System.out.println( "SearchSymbol.doInit() done" );
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}

	@Get
	public String getSearchFreeResult() 
	{
	  System.out.println( "SearchSymbol.getSearchSymbolResult() start" );
	  try 
	  {
	    //The result set
	    Set<XrefWithSymbol> suggestions = new HashSet<XrefWithSymbol>();
	    
	    for(IDMapperRdb mapper : mappers ) {
		suggestions.addAll( mapper.freeSearchWithSymbol( searchStr, limit ) );
	    }
	    
            StringBuilder result = new StringBuilder();
	    for( XrefWithSymbol x : suggestions ) {
		result.append( x.getId() );
		result.append( "\t" );
		result.append( x.getSymbol() );
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
