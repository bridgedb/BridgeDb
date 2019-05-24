// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
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

package org.bridgedb.server;

import java.util.Iterator;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

/**
 * @author nuno
 *
 */
public class Batch extends IDMapperResource {

	private DataSource sourceDs;
	private DataSource targetDs;

	/* (non-Javadoc)
	 * @see src.org.bridgedb.server.IDMapperResource#doInit()
	 */
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			System.out.println( "Batch Xrefs.doInit start" );
			//Required parameters
			
			String dsName = urlDecode((String)getRequest().getAttributes().get(IDMapperService.PAR_SYSTEM));
			sourceDs = parseDataSource(dsName);
			String targetDsName = (String)getRequest().getAttributes().get(IDMapperService.PAR_TARGET_SYSTEM);
			targetDs = parseDataSource(targetDsName);
			if(targetDsName != null) {
				targetDs = parseDataSource(urlDecode(targetDsName));
			}
		} catch(Exception e) {
			throw new ResourceException(e);
		}
	}
	
	/**
	 * @param entity
	 * @return 
	 */
	@Post
    public String accept(Representation entity) { 
		String result=null;
		if (sourceDs!=null){
			result = oneDataSource(entity);
		}
		else{
			result = multiDataSource(entity);
		}
		return result;
    }
    
	/**
	 * @param entity
	 * @return
	 */
	public String multiDataSource(Representation entity){
		System.out.println( "Batch Multi Xrefs.getXrefs() start" );
		try {
			//The result set
			String[] splitXrefs = entity.getText().split("\n");
			IDMapper mapper = getIDMappers();
			StringBuilder result = new StringBuilder();
			for(String line : splitXrefs){
				String[] lineSplit = line.split("\t");
				String id = lineSplit[0].trim();
				
				DataSource ds = parseDataSource(lineSplit[1]);				
				Xref source = new Xref(lineSplit[0].trim(),ds);		
						
				Set<Xref> xrefs;
				
				if (targetDs == null)
					xrefs = mapper.mapID(source);
				else
					xrefs = mapper.mapID(source, targetDs);
				if (xrefs.isEmpty()){
					result.append(id.trim());
					result.append("\t");
					result.append(ds.getFullName());
					result.append("\t");
					result.append("N/A");
					result.append("\n");
				}
				else{
					result.append(id.trim());
					result.append("\t");
					result.append(ds.getFullName());
					result.append("\t");
					Iterator<Xref> iter = xrefs.iterator();
					result.append(iter.next());
					while (iter.hasNext()) {
					    result.append(","+iter.next());
					}
					result.append("\n");
				}				
			}
			return result.toString();
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return e.getMessage();
		}
	}
	
	/**
	 * @param entity
	 * @return
	 */
	public String oneDataSource(Representation entity){
		System.out.println( "Batch Xrefs.getXrefs() start" );
		try {
			//The result set
			String[] splitXrefs = entity.getText().split("\n");
			IDMapper mapper = getIDMappers();
			StringBuilder result = new StringBuilder();
			for(String id : splitXrefs){
				Xref xref =  new Xref(id.trim(), sourceDs);
				Set<Xref> xrefs;
				if (targetDs == null)
					xrefs = mapper.mapID(xref);
				else
					xrefs = mapper.mapID(xref, targetDs);
				if (xrefs.isEmpty()){
					result.append(id.trim());
					result.append("\t");
					result.append(sourceDs.getFullName());
					result.append("\t");
					result.append("N/A");
					result.append("\n");
				}
				else{
					result.append(id.trim());
					result.append("\t");
					result.append(sourceDs.getFullName());
					result.append("\t");
					Iterator<Xref> iter = xrefs.iterator();
					result.append(iter.next());
					while (iter.hasNext()) {
					    result.append(","+iter.next());
					}
					result.append("\n");
				}				
			}
			return result.toString();
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return e.getMessage();
		}
	}
	
	/* (non-Javadoc)
	 * @see src.org.bridgedb.server.IDMapperResource#parseDataSource(String)
	 */
	protected DataSource parseDataSource(String dsName) {
		if(dsName == null) return null;
		DataSource ds = null;
		//Try parsing by full name
		if(DataSource.getFullNames().contains(dsName)) {
			ds = DataSource.getExistingByFullName(dsName);
		} else { //If not possible, use system code
			ds = DataSource.getExistingBySystemCode(dsName);
		}
		return ds;
	}
}