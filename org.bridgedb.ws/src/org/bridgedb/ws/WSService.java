package org.bridgedb.ws;

import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XRefMapBean;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.CapabilitiesBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.IDMapperSQL;
import org.bridgedb.sql.MySQLAccess;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.MappingSupportedBean;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.XrefExistsBean;

@Path("/")
public class WSService implements WSInterface {

    private IDMapper idMapper;

    
    public WSService() throws BridgeDbSqlException {
        SQLAccess sqlAccess = new MySQLAccess("jdbc:mysql://localhost:3306/imstest", "imstest", "imstest");
        idMapper = new IDMapperSQL(sqlAccess);
    }
    
    public WSService(SQLAccess sqlAccess) throws BridgeDbSqlException {
        idMapper = new IDMapperSQL(sqlAccess);
    }

    //For testing allow another mapper to be inserted
    public WSService(IDMapper idMapper) throws BridgeDbSqlException {
        this.idMapper = idMapper;
    }
    
    @Context 
    private UriInfo uriInfo;

   // static {
   //     try {
   //             irs = new IRSImpl();
   //     } catch (IRSException ex) {
   //         String msg = "Cannot initialise IRS service";
   //        Logger.getLogger(WSService.class.getName()).log(Level.SEVERE, msg, ex);
   //     }
   // }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage() {
        StringBuilder sb = new StringBuilder();
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
        
        sb.append("<li>mapByXRef");
        sb.append("<ul>");
        sb.append("<li>Required arguements:<ul>");
        sb.append("<li>id as string</li>");
        sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
        sb.append("</ul></li>");
        sb.append("<li>Optional arguments<ul>");
        sb.append("<li>tgtCode as string ");
        sb.append("<ul>");        
        sb.append("<li>There can be more than one</li>");        
        sb.append("<li>Where code is the SystemCode of the DataSource)</li>");
        sb.append("</ul></ul></li>");

        sb.append("<li>mapByXRefs");
        sb.append("<ul>");
        sb.append("<li>Required arguements:<ul>");
        sb.append("<li>id as string</li>");
        sb.append("<li>code as string (Where code is the SystemCode of the DataSource)</li>");
        sb.append("<li>(There can be multiple \"id\" and \"code\" values");
        sb.append("<ul>");
        sb.append("<li>There must be at least one of each.</li>");                
        sb.append("<li>There must be the same number of each.</li>");                
        sb.append("<li>They will be paired by order.</li>");                
        sb.append("</ul>");
        sb.append("</ul></li>");
        sb.append("<li>Optional arguments<ul>");
        sb.append("<li>tgtCode as string ");
        sb.append("<ul>");        
 
        sb.append("<li>freeSearch");
        sb.append("<ul>");
        sb.append("<li>Required arguements:<ul>");
        sb.append("<li>text as string</li>");
        sb.append("</ul></li>");
        sb.append("<li>Optional arguments<ul>");
        sb.append("<li>limit as Integer ");
        sb.append("<ul>");        

        sb.append("<li>There can be more than one</li>");        
        sb.append("<li>Where code is the SystemCode of the DataSource)</li>");
        sb.append("</ul></ul></li>");
        sb.append("</ul></p>");
        sb.append("<li><a href=\"").append(uriInfo.getBaseUri()).append("getSupportedSrcDataSources\">Get sources</a></li>");
        sb.append("<li><a href=\"").append(uriInfo.getBaseUri()).append("getSupportedTgtDataSources\">Get targets</a></li>");
        sb.append("</ul>");
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getSupportedSrcDataSources")
    @Override
    public List<DataSourceBean> getSupportedSrcDataSources() throws IDMapperException {
        ArrayList<DataSourceBean> sources = new ArrayList<DataSourceBean>();
        Set<DataSource> dataSources = idMapper.getCapabilities().getSupportedSrcDataSources();
        for (DataSource dataSource:dataSources){
            DataSourceBean bean = new DataSourceBean(dataSource);
            sources.add(bean);
        }
        return sources;
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getDataSource/{code}")
    @Override
    public DataSourceBean getDataSoucre(@PathParam("code") String code) throws IDMapperException {
        DataSource dataSource = DataSource.getBySystemCode(code);
        DataSourceBean bean = new DataSourceBean(dataSource);
        return bean;
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/freeSearch")
    @Override
    public List<XrefBean> freeSearch(
            @QueryParam("text") String text,
            @QueryParam("limit") Integer limit) throws IDMapperException {
        if (text == null) throw new IDMapperException("text parameter missig");
        if (limit == null){
            Set<Xref> mappings = idMapper.freeSearch(text, Integer.MAX_VALUE);
            return setXrefToListXrefBeans(mappings);
        } else {
            Set<Xref> mappings = idMapper.freeSearch(text,limit);
            return setXrefToListXrefBeans(mappings);
        }
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapByXRef")
    @Override
    public List<XrefBean> mapByXref(
            @QueryParam("id") String id,
            @QueryParam("code") String scrCode,
            @QueryParam("tgtCode") List<String> targetCodes) throws IDMapperException {
        if (id == null) throw new IDMapperException("id parameter missig");
        if (scrCode == null) throw new IDMapperException("code parameter missig");
        DataSource dataSource = DataSource.getBySystemCode(scrCode);
        Xref source = new Xref(id, dataSource);
        if (targetCodes == null){
            Set<Xref> mappings = idMapper.mapID(source);
            return setXrefToListXrefBeans(mappings);
        } else {
            DataSource[] targetDataSources = new DataSource[targetCodes.size()];
            for (int i = 0; i< targetCodes.size(); i++){
                targetDataSources[i] = DataSource.getBySystemCode(targetCodes.get(i));
            }
            Set<Xref> mappings = idMapper.mapID(source, targetDataSources);
            return setXrefToListXrefBeans(mappings);
        }
    } 

    private List setXrefToListXrefBeans(Set<Xref> xrefs){
       ArrayList<XrefBean> results = new ArrayList<XrefBean>();
        for (Xref xref:xrefs){
           results.add(new XrefBean(xref));
        }
        return results;        
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapByXRefs")
    @Override
    public List<XRefMapBean> mapByXrefs(
            @QueryParam("id") List<String> id,
            @QueryParam("code") List<String> scrCode,
            @QueryParam("tgtCode") List<String> targetCodes) throws IDMapperException {
        if (id == null) throw new IDMapperException("id parameter missig");
        if (id.isEmpty()) throw new IDMapperException("id parameter missig");
        if (scrCode == null) throw new IDMapperException("code parameter missig");
        if (scrCode.isEmpty()) throw new IDMapperException("code parameter missig");
        if (id.size() != scrCode.size()) throw new IDMapperException("Must have same number of id and code parameters");
        HashSet<Xref> srcXrefs = new HashSet<Xref>();
        for (int i = 0; i < id.size() ;i++){
            DataSource dataSource = DataSource.getBySystemCode(scrCode.get(i));
            Xref source = new Xref(id.get(i), dataSource);
            srcXrefs.add(source);
        }
        Map<Xref, Set<Xref>> mappings;
        if (targetCodes == null){
            mappings = idMapper.mapID(srcXrefs);
        } else {
            DataSource[] targetDataSources = new DataSource[targetCodes.size()];
            for (int i = 0; i< targetCodes.size(); i++){
                targetDataSources[i] = DataSource.getBySystemCode(targetCodes.get(i));
            }
            mappings = idMapper.mapID(srcXrefs, targetDataSources);
        }
        ArrayList<XRefMapBean> results = new ArrayList<XRefMapBean>();
        for (Xref source:mappings.keySet()){
           results.add(new XRefMapBean(source, mappings.get(source)));
        }
        return results;
    } 

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/xrefExists")
    @Override
    public XrefExistsBean xrefExists( 
            @QueryParam("id") String id,
            @QueryParam("code") String scrCode) throws IDMapperException {
        if (id == null) throw new IDMapperException ("\"id\" parameter can not be null");
        if (scrCode == null) throw new IDMapperException ("\"code\" parameter can not be null");            
        DataSource dataSource = DataSource.getBySystemCode(scrCode);
        Xref source = new Xref(id, dataSource);
        return new XrefExistsBean(source, idMapper.xrefExists(source));
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getSupportedTgtDataSources")
    @Override
    public List<DataSourceBean> getSupportedTgtDataSources() throws IDMapperException {
        ArrayList<DataSourceBean> targets = new ArrayList<DataSourceBean>();
        Set<DataSource> dataSources = idMapper.getCapabilities().getSupportedSrcDataSources();
        for (DataSource dataSource:dataSources){
            DataSourceBean bean = new DataSourceBean(dataSource);
            targets.add(bean);
        }
        return targets;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/isFreeSearchSupported")
    @Override
    public FreeSearchSupportedBean isFreeSearchSupported() {
        return new FreeSearchSupportedBean(idMapper.getCapabilities().isFreeSearchSupported());
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/isMappingSupported")
    @Override
    public MappingSupportedBean isMappingSupported(
            @QueryParam("source") String srcCode, 
            @QueryParam("target") String tgtCode) throws IDMapperException {
        if (srcCode == null) throw new IDMapperException ("\"source\" parameter can not be null");
        if (tgtCode == null) throw new IDMapperException ("\"target\" parameter can not be null");
        DataSource src = DataSource.getBySystemCode(srcCode);
        DataSource tgt = DataSource.getBySystemCode(tgtCode);
        return new MappingSupportedBean(src, tgt, idMapper.getCapabilities().isMappingSupported(src, tgt));
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getProperty/{key}")
    @Override
    public PropertyBean getProperty(@PathParam("key")String key) {
        String property = idMapper.getCapabilities().getProperty(key);
        if (property == null) return null;
        return new PropertyBean(key, property);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getKeys")
    @Override
    public List<PropertyBean> getKeys() {
        Set<String> keys = idMapper.getCapabilities().getKeys();
        ArrayList<PropertyBean> results = new ArrayList<PropertyBean>();
        IDMapperCapabilities idMapperCapabilities = idMapper.getCapabilities();
        for (String key:keys){
            results.add(new PropertyBean(key, idMapperCapabilities.getProperty(key)));
        }
        return results;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getCapabilities")
    public CapabilitiesBean getCapabilities()  {
        return new CapabilitiesBean(idMapper.getCapabilities());
    }
}
