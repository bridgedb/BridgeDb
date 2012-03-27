/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws.server;


import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.ws.WSService;

/**
 *
 * @author Christian
 */
public class WsSqlServer extends WSService{
    
    public WsSqlServer() throws BridgeDbSqlException  {
        SQLAccess sqlAccess = SqlFactory.createURLSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        idMapper = urlMapperSQL;
        urlMapper = urlMapperSQL;
        byXrefPossition = urlMapperSQL;
        byURLPossition = urlMapperSQL;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage() throws IDMapperException {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInner;
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPR IRS</title></head><body>");
        sb.append("<h1>Open PHACTS Identity Mapping Service</h1>");
        sb.append("<p>Welcome to the prototype Identity Mapping Service. ");
        sb.append("Support services include:");
        
        sb.append("<ul>");
        sb.append("<li>mapByXRef</li>");
            sb.append("<ul>");
            sb.append("<li>Required arguements:</li>");
                sb.append("<ul>");
                sb.append("<li>id as string</li>");
                sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
                sb.append("</ul>");
            sb.append("<li>Optional arguments</li>");
                sb.append("<ul>");
                sb.append("<li>tgtCode as string</li> ");
                    sb.append("<ul>");        
                    sb.append("<li>There can be more than one</li>");        
                    sb.append("<li>Where code is the SystemCode of the DataSource)</li>");
                sb.append("</ul>");
            sb.append("<li>Example: <a href=\"");
                sb.append(uriInfo.getBaseUri());
                sbInner = new StringBuilder("mapByXRef?id=");
                Xref first = byXrefPossition.getXrefByPossition(0);
                sbInner.append(first.getId());
                sbInner.append("&code=");
                sbInner.append(first.getDataSource().getSystemCode());
                sb.append(sbInner.toString());
                sb.append("\">");
                sb.append(sbInner.toString());
                sb.append("</a></li>");    
            sb.append("</ul>");

        sb.append("<li>mapByXRefs</li>");
        //    sb.append("<ul>");
        //    sb.append("<li>Required arguements:</li>");
        //        sb.append("<ul>");
        //        sb.append("<li>id as string</li>");
        //        sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
        //        sb.append("<li>(There can be multiple \"id\" and \"code\" values</li>");
        //            sb.append("<ul>");
        //            sb.append("<li>There must be at least one of each.</li>");                
        //            sb.append("<li>There must be the same number of each.</li>");                
        //            sb.append("<li>They will be paired by order.</li>");                
        //            sb.append("</ul>");
        //        sb.append("</ul>");
        //    sb.append("<li>Optional arguments</li>");
        //        sb.append("<ul>");
        //            sb.append("<li>tgtCode as string<li> ");
        //        sb.append("</ul>");        
        //    sb.append("</ul>");

        sb.append("<li>freeSearch</li>");
        //    sb.append("<ul>");
        //    sb.append("<li>Required arguements:");
        //        sb.append("<ul>");
        //        sb.append("<li>text as string</li>");
        //        sb.append("</ul>");
        //    sb.append("<li>Optional arguments</li>");
        //        sb.append("<ul>");
        //        sb.append("<li>limit as Integer ");
        //        sb.append("</ul>");        
        //    sb.append("</ul>");
        sb.append("</ul>");

        //sb.append("<li><a href=\"").append(uriInfo.getBaseUri()).append("getSupportedSrcDataSources\">Get sources</a></li>");
        //sb.append("<li><a href=\"").append(uriInfo.getBaseUri()).append("getSupportedTgtDataSources\">Get targets</a></li>");
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    

}


