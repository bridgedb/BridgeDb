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
public class Xrefs extends IDMapperResource {
	List<IDMapperRdb> mappers;
	Xref xref;
	DataSource targetDs;
	
	protected void doInit() throws ResourceException {
		try {
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
	public String getXrefs() {
		try {
			//The result set
			Set<Xref> xrefs = new HashSet<Xref>();

			for(IDMapperRdb mapper : mappers) {
				if(targetDs == null) {
					xrefs.addAll(mapper.getCrossRefs(xref));
				} else {
					xrefs.addAll(mapper.getCrossRefs(xref, targetDs));
				}
			}
					
			StringBuilder result = new StringBuilder();
			for(Xref x : xrefs) {
				result.append(x.getId());
				result.append("\t");
				result.append(x.getDataSource().getFullName());
				result.append("\n");
			}
			
			return result.toString();
		} catch(Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return e.getMessage();
		}
	}
}
