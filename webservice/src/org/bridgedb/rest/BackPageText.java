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
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

/**
 * Resource that handles the xref queries
 */
public class BackPageText extends IDMapperResource {
	Xref xref;
	DataSource targetDs;
	
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
		    System.out.println( "Xrefs.doInit start" );
			//Required parameters
			String id = urlDecode((String)getRequest().getAttributes().get(IDMapperService.PAR_ID));
			String dsName = urlDecode((String)getRequest().getAttributes().get(IDMapperService.PAR_SYSTEM));
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
	    Set<String> bpInfoSym = new HashSet<String>();
            Set<String> bpInfoDes = new HashSet<String>();
            Set<String> bpInfoTyp = new HashSet<String>();
            Set<String> bpInfoChr = new HashSet<String>();
            Set<String> bpInfoSyn = new HashSet<String>();
	    
	    for(IDMapper mapper : getIDMappers()) 
	    {
	    	if (mapper instanceof AttributeMapper)
	    	{
	    		AttributeMapper attr = (AttributeMapper)mapper;
				bpInfoSym.addAll(attr.getAttributes( xref, "Symbol"));
		                bpInfoDes.addAll(attr.getAttributes( xref, "Description"));
		                bpInfoTyp.addAll(attr.getAttributes( xref, "Type"));
		                bpInfoChr.addAll(attr.getAttributes( xref, "Chromosome"));
		                bpInfoSyn.addAll(attr.getAttributes( xref, "Synonyms"));
	    	}
	    }
	    
            StringBuilder result = new StringBuilder();
	    result.append("<html><body><table>");
	    for( String x : bpInfoSym ) {
	      result.append("<tr><td>Symbol</td><td>" + x + "</td></tr>" );
	    }
            for( String x : bpInfoDes ) {
              result.append("<tr><td>Description</td><td>" + x + "</td></tr>" );
            }
            for( String x : bpInfoTyp ) {
              result.append("<tr><td>Type</td><td>" + x + "</td></tr>" );
            }
            for( String x : bpInfoChr ) {
              result.append("<tr><td>Chromosome</td><td>" + x + "</td></tr>" );
            }
            for( String x : bpInfoSyn ) {
              result.append("<tr><td>Synonyms</td><td>" + x + "</td></tr>" );
            }
	    result.append("</table></body></html>");
	    return( result.toString() );
          } catch( Exception e ) {
	    e.printStackTrace();
	    setStatus( Status.SERVER_ERROR_INTERNAL );
	    return e.getMessage();
	  }
	}

}
