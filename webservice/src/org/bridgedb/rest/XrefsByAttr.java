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
public class XrefsByAttr extends IDMapperResource 
{
  List<IDMapperRdb> mappers;
  String targetAttrName;
  String targetAttrValue;
 
  protected void doInit() throws ResourceException 
  {
      System.out.print( "XrefsByAttr start" );
    try 
    {
      //Required parameters
      String org = (String)getRequest().getAttributes().get(IDMapperService.PAR_ORGANISM);
      mappers = getIDMappers(org);
      targetAttrName = (String)getRequest().getAttributes().get(IDMapperService.PAR_TARGET_ATTR_NAME );
      targetAttrValue = (String)getRequest().getAttributes().get(IDMapperService.PAR_TARGET_ATTR_VALUE );
                  

    }
    catch(Exception e) 
    {
      throw new ResourceException(e);
    }
  }

  @Get
  public String getXrefsByAttr() 
  {
      System.out.println( "targetAttrName = " + targetAttrName + " targetAttrValue = " + targetAttrValue );
    try 
    {
      //The result set
      Set<Xref> xrefs = new HashSet<Xref>();

      for(IDMapperRdb mapper : mappers) 
      {
	System.out.println( "mapper found" );
	xrefs.addAll(mapper.freeAttributeSearch(targetAttrValue, targetAttrName, 100));
      }
					
      StringBuilder result = new StringBuilder();
      for(Xref x : xrefs) 
      {
	  System.out.println( "Xref: " + x.getId() + " " + x.getDataSource().getFullName() );
        result.append(x.getId());
	result.append("\t");
	result.append(x.getDataSource().getFullName());
	result.append("\n");
      }
			
      return result.toString();
    }
    catch(Exception e) 
    {
      e.printStackTrace();
      setStatus(Status.SERVER_ERROR_INTERNAL);
      return e.getMessage();
    }
  }
}