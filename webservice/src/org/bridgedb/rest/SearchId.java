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
		    searchStr = (String) getRequest().getAttributes().get( IDMapperService.PAR_QUERY );
		    System.out.println( "2: " + searchStr );
	       	    String limitStr = (String)getRequest().getAttributes().get( IDMapperService.PAR_TARGET_LIMIT );
		    if ( limitStr != null )
                    {
		      System.out.println( "3: " + limitStr );
	     	      limit = new Integer( limitStr ).intValue();
	 	    }
		    else
	            {
		      limit = 0;
		    }


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
		suggestions.addAll( mapper.freeSearch( searchStr, limit ) );
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
