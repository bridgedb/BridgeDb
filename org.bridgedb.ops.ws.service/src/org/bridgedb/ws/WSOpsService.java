// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.ws;

import com.sun.jersey.multipart.FormDataParam;
import java.io.InputStream;
import java.util.ArrayList;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.URLExistsBean;
import org.bridgedb.ws.bean.URLMappingBean;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.linkset.LinksetInterfaceMinimal;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.rdf.StatementReader;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.url.URLMapper;
import org.bridgedb.url.URLMapping;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.bean.DataSourceUriSpacesBean;
import org.bridgedb.ws.bean.DataSourceUriSpacesBeanFactory;
import org.bridgedb.ws.bean.MappingSetInfoBeanFactory;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.OverallStatisticsBeanFactory;
import org.bridgedb.ws.bean.ProfileBean;
import org.bridgedb.ws.bean.ProfileBeanFactory;
import org.bridgedb.ws.bean.URLBean;
import org.bridgedb.ws.bean.URLMappingBeanFactory;
import org.bridgedb.ws.bean.URLSearchBean;
import org.bridgedb.ws.bean.ValidationBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefBeanFactory;
import org.openrdf.rio.RDFFormat;

@Path("/")
public class WSOpsService extends WSCoreService implements WSOpsInterface {

    protected URLMapper urlMapper;
    protected LinksetInterfaceMinimal linksetInterface;
//    private String validationTypeString;
    public final String MIME_TYPE = "mimeType";
    public final String STORE_TYPE = "storeType";
    public final String VALIDATION_TYPE = "validationType";
    public final String INFO = "info"; 
    public final String FILE = "file";     
    public final String NO_RESULT = null;
    
    protected WSOpsService() {
        this.linksetInterface = new LinksetLoader();
    }

    public WSOpsService(URLMapper urlMapper) {
        super(urlMapper);
        this.urlMapper = urlMapper;
        this.linksetInterface = new LinksetLoader();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapURL")
    @Override
    public List<URLMappingBean> mapURL(@QueryParam("URL") String URL,
    		@QueryParam("profileURL") String profileURL,
            @QueryParam("targetURISpace") List<String> targetURISpace) throws IDMapperException {
        if (URL == null) throw new IDMapperException("URL parameter missing.");        
        if (URL.isEmpty()) throw new IDMapperException("URL parameter may not be null.");        
        if (profileURL == null || profileURL.isEmpty()){
            profileURL = RdfFactory.getProfileURI(0).stringValue();
        }
        if (targetURISpace == null) {
        	targetURISpace = new ArrayList<String>();
        }
        String[] targetURISpaces = new String[targetURISpace.size()];
        for (int i = 0; i < targetURISpace.size(); i++){
            targetURISpaces[i] = targetURISpace.get(i);
        }
        Set<URLMapping> urlMappings = urlMapper.mapURLFull(URL, profileURL, targetURISpaces);
        List<URLMappingBean> results = new ArrayList<URLMappingBean>(); 
        for (URLMapping urlMapping:urlMappings){
            results.add(URLMappingBeanFactory.asBean(urlMapping));
        }
        return results;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/URLExists")
    @Override
    public URLExistsBean URLExists(@QueryParam("URL") String URL) throws IDMapperException {
        if (URL == null) throw new IDMapperException("URL parameter missing.");
        if (URL.isEmpty()) throw new IDMapperException("URL parameter may not be null.");
        boolean exists = urlMapper.uriExists(URL);
        return new URLExistsBean(URL, exists);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/URLSearch")
    @Override
    public URLSearchBean URLSearch(@QueryParam("text") String text,
            @QueryParam("limit") String limitString) throws IDMapperException {
        if (text == null) throw new IDMapperException("text parameter missing.");
        if (text.isEmpty()) throw new IDMapperException("text parameter may not be null.");
        if (limitString == null || limitString.isEmpty()){
            Set<String> urls = urlMapper.urlSearch(text, Integer.MAX_VALUE);
            return new URLSearchBean(text, urls);
        } else {
            int limit = Integer.parseInt(limitString);
            Set<String> urls = urlMapper.urlSearch(text, limit);
            return new URLSearchBean(text, urls);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/toXref")
    @Override
    public XrefBean toXref(@QueryParam("URL") String URL) throws IDMapperException {
        if (URL == null) throw new IDMapperException("URL parameter missing.");
        if (URL.isEmpty()) throw new IDMapperException("URL parameter may not be null.");
        Xref xref = urlMapper.toXref(URL);
        return XrefBeanFactory.asBean(xref);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapping")
    public URLMappingBean getMapping() throws IDMapperException {
        throw new IDMapperException("id path parameter missing.");
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/mapping/{id}")
    public URLMappingBean getMapping(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null) throw new IDMapperException("id path parameter missing.");
        if (idString.isEmpty()) throw new IDMapperException("id path parameter may not be null.");
        int id = Integer.parseInt(idString);
        URLMapping mapping = urlMapper.getMapping(id);
        return URLMappingBeanFactory.asBean(mapping);
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getSampleSourceURLs") 
    public List<URLBean> getSampleSourceURLs() throws IDMapperException {
        Set<String> URLs = urlMapper.getSampleSourceURLs();
        List<URLBean> beans = new ArrayList<URLBean>();
        for (String URL:URLs){
            URLBean bean = new URLBean();
            bean.setURL(URL);
            beans.add(bean);
        }
        return beans;
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getOverallStatistics") 
    public OverallStatisticsBean getOverallStatistics() throws IDMapperException {
        OverallStatistics overallStatistics = urlMapper.getOverallStatistics();
        OverallStatisticsBean bean = OverallStatisticsBeanFactory.asBean(overallStatistics);
        return bean;
    }


    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getMappingSetInfos") 
    public List<MappingSetInfoBean> getMappingSetInfos() throws IDMapperException {
        List<MappingSetInfo> infos = urlMapper.getMappingSetInfos();
        ArrayList<MappingSetInfoBean> results = new ArrayList<MappingSetInfoBean>();
        for (MappingSetInfo info:infos){
            results.add(MappingSetInfoBeanFactory.asBean(info));
        }
        return results;
    }

	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@Path("/profile/{id}")
	public ProfileBean getProfile(@PathParam("id") String id) throws BridgeDbSqlException {
		ProfileInfo profile = urlMapper.getProfile(RdfFactory.getProfileURI(Integer.parseInt(id)).stringValue());
		ProfileBean result = ProfileBeanFactory.asBean(profile);
		return result;
	}
    
	@Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/profile") 
	public List<ProfileBean> getProfiles() throws BridgeDbSqlException {
		List<ProfileInfo> profiles = urlMapper.getProfiles();
		List<ProfileBean> results = new ArrayList<ProfileBean>();
		for (ProfileInfo profile:profiles) {
			results.add(ProfileBeanFactory.asBean(profile));
		}
		return results;
	}
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/getMappingSetInfo/{id}")
    public MappingSetInfoBean getMappingSetInfo(@PathParam("id") String idString) throws IDMapperException {
        if (idString == null) throw new IDMapperException("id path parameter missing.");
        if (idString.isEmpty()) throw new IDMapperException("id path parameter may not be null.");
        int id = Integer.parseInt(idString);
        MappingSetInfo info = urlMapper.getMappingSetInfo(id);
        return MappingSetInfoBeanFactory.asBean(info);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/dataSource")
    public DataSourceUriSpacesBean getDataSource() throws IDMapperException {
        throw new IDMapperException("id path parameter missing.");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Override
    @Path("/dataSource/{id}")
    public DataSourceUriSpacesBean getDataSource(@PathParam("id") String id) throws IDMapperException {
        if (id == null) throw new IDMapperException("id path parameter missing.");
        if (id.isEmpty()) throw new IDMapperException("id path parameter may not be null.");
        Set<String> urls = urlMapper.getUriSpaces(id);
        DataSource ds = DataSource.getBySystemCode(id);
        DataSourceUriSpacesBean bean = DataSourceUriSpacesBeanFactory.asBean(ds, urls);
        return bean;
    }
    
    //**** LinksetInterfaceMinimal methods

    private String trim(String original){
        String result = original.trim();
        while (result.startsWith("\"")){
            result = result.substring(1);
        }
        while (result.endsWith("\"")){
            result = result.substring(0,result.length()-1);
        }
        return result.trim();
    }
    
    protected final RDFFormat getRDFFormatByMimeType(String mimeType) throws MetaDataException{
        if (mimeType == null){
            throw new MetaDataException (MIME_TYPE + " parameter may not be null");
        }
        mimeType = trim(mimeType);
        if (mimeType.isEmpty()){
            throw new MetaDataException (MIME_TYPE + " parameter may not be empty");
        }
        return  StatementReader.getRDFFormatByMimeType(mimeType);
    }
    
    protected final StoreType parseStoreType(String storeTypeString) throws IDMapperException{
        if (storeTypeString == null){
            throw new MetaDataException (STORE_TYPE + " parameter may not be null");
        }
        storeTypeString = trim(storeTypeString);
        if (storeTypeString.isEmpty()){
            throw new MetaDataException (STORE_TYPE + " parameter may not be empty");
        }
        return StoreType.parseString(storeTypeString);
    }

    protected final ValidationType parseValidationType(String validationTypeString) throws IDMapperException{
        if (validationTypeString == null){
            throw new MetaDataException (VALIDATION_TYPE + " parameter may not be null");
        }
        if (validationTypeString.trim().isEmpty()){
            throw new MetaDataException (VALIDATION_TYPE + " parameter may not be empty");
        }
        return ValidationType.parseString(validationTypeString);
    }
    
    protected final void validateInfo(String info) throws MetaDataException{
        if (info == null){
            throw new MetaDataException (INFO + " parameter may not be null");
        }
        if (info.trim().isEmpty()){
            throw new MetaDataException (INFO + " parameter may not be empty");
        }        
    }
    
    private void validateInputStream(InputStream inputStream) throws MetaDataException {
        if (inputStream == null){
            throw new MetaDataException (FILE + " parameter may not be null");
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateString")
    public ValidationBean getValidateString(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType, 
            @QueryParam(STORE_TYPE)String storeTypeString, 
            @QueryParam(VALIDATION_TYPE)String validationTypeString, 
            @QueryParam("includeWarnings")String includeWarningsString) throws IDMapperException {
        return validateString(info, mimeType, storeTypeString, validationTypeString, includeWarningsString);
    }

    @Override
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateString")
    public ValidationBean validateString(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType, 
            @FormParam(STORE_TYPE)String storeTypeString, 
            @FormParam(VALIDATION_TYPE)String validationTypeString, 
            @FormParam("includeWarnings")String includeWarningsString) throws IDMapperException {
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInfo(info);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            StoreType storeType = parseStoreType(storeTypeString);
            ValidationType validationType = parseValidationType(validationTypeString);
            boolean includeWarnings = Boolean.parseBoolean(includeWarningsString);
            report = linksetInterface.validateString("Webservice Call", info, format, storeType, validationType, includeWarnings);
            return new ValidationBean(report, info, mimeType, storeTypeString, validationTypeString, 
                    includeWarnings, exception);
        } catch (Exception e){
            exception = e.toString();
            return new ValidationBean(report, info, mimeType, storeTypeString, validationTypeString, 
                    includeWarningsString, exception);
        }
    }

    @Override
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/validateString")
    public ValidationBean validateInputStream(@FormDataParam("file") InputStream uploadedInputStream, 
            @FormParam(MIME_TYPE)String mimeType, 
            @FormParam(STORE_TYPE)String storeTypeString, 
            @FormParam(VALIDATION_TYPE)String validationTypeString, 
            @FormParam("includeWarnings")String includeWarningsString) throws IDMapperException {
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInputStream(uploadedInputStream);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            StoreType storeType = parseStoreType(storeTypeString);
            ValidationType validationType = parseValidationType(validationTypeString);
            boolean includeWarnings = Boolean.parseBoolean(includeWarningsString);
            report = linksetInterface.validateInputStream("Webservice Call", uploadedInputStream, format, storeType, validationType, includeWarnings);
            return new ValidationBean(report, "data read directly from the Stream", mimeType, storeTypeString, validationTypeString, 
                    includeWarnings, exception);
        } catch (Exception e){
            exception = e.toString();
            return new ValidationBean(report, "data read directly from the Stream", mimeType, storeTypeString, validationTypeString, 
                    includeWarningsString, exception);
        }
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/validateStringXML")
    public ValidationBean validateString(JAXBElement<ValidationBean> input) throws IDMapperException {
        System.out.println("received");
        String report = NO_RESULT;
        String info = null;
        String mimeType = null;
        String storeType = null;
        String validationType = null;
        Boolean includeWarnings = null;
        String exception = null;       
        try{
            ValidationBean bean = input.getValue();
            info = bean.getInfo();
            mimeType = bean.getMimeType();
            storeType = bean.getStoreType();
            validationType = bean.getValidationType();
            includeWarnings = bean.getIncludeWarnings();
        } catch (Exception e){
            exception = e.toString();
            return new ValidationBean(report, info, mimeType, storeType, validationType, includeWarnings, exception);
        }
        System.out.println("calling");
        if (includeWarnings){
            return validateString(info, mimeType, storeType, validationType, "true");
        } else {
            return validateString(info, mimeType, storeType, validationType, "false");
        }     
    }

    @Override
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsVoid")
    public ValidationBean validateStringAsVoid(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException {
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInfo(info);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateStringAsVoid("Webservice Call", info, mimeType);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, info, mimeType, StoreType.LIVE, ValidationType.VOID, true, exception);
    }

    @Override
    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateInputStreamAsVoid")
    public ValidationBean validateInputStreamAsVoid(@FormDataParam("file") InputStream uploadedInputStream, 
            @FormDataParam(MIME_TYPE)String mimeType) throws IDMapperException {
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInputStream(uploadedInputStream);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateInputStreamAsVoid("Webservice Call", uploadedInputStream, mimeType);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, "data read directly from the Stream", mimeType, StoreType.LIVE, 
                ValidationType.LINKS, true,exception);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsVoidXML")
    public ValidationBean validateStringAsVoidXML(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException {
        return validateStringAsVoid(info, mimeType);
    }

    @Override
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsLinkSet")
    public ValidationBean validateInputStreamAsLinkSet(@FormDataParam("file") InputStream uploadedInputStream, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException {
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInputStream(uploadedInputStream);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateInputStreamAsLinks("Webservice Call", uploadedInputStream, mimeType);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, "data read directly from the Stream", mimeType, StoreType.LIVE, 
                ValidationType.LINKS, true,exception);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsVoid")
    public ValidationBean getValidateStringAsVoid(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException {
        return validateStringAsVoid(info, mimeType);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsVoidXML")
    public ValidationBean getValidateStringAsVoidXML(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException {
        return validateStringAsVoid(info, mimeType);
    }

    /*@Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsLinksetVoid")
    public ValidationBean validateStringAsLinksetVoid(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException {
        String report = NO_RESULT;
        String exception = NO_EXCEPTION;
        try{
            validateInfo(info);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateStringAsLinksetVoid(info, mimeType);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, info, mimeType, StoreType.LIVE, ValidationType.LINKSETVOID, true, exception);
    }*/

    @Override
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsLinkSet")
    public ValidationBean validateStringAsLinkSet(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException {
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInfo(info);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateStringAsLinks("Webservice Call", info, mimeType);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, info, mimeType, StoreType.LIVE, ValidationType.LINKS, true,exception);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsLinkSetXML")
    public ValidationBean validateStringAsLinkSetXML(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws IDMapperException {
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInfo(info);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateStringAsLinks("Webservice Call", info, mimeType);
        } catch (Exception e){
            exception = e.toString();
        }
        return validateStringAsLinkSet(info, mimeType);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsLinkSet")
    public ValidationBean getValidateStringAsLinkSet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException {
        return validateStringAsLinkSet(info, mimeType);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateStringAsLinkSetXML")
    public ValidationBean getValidateStringAsLinkSetXML(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws IDMapperException {
        return validateStringAsLinkSet(info, mimeType);
    }

    @Override
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadString")
    public String loadString(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType, 
            @QueryParam(STORE_TYPE)String storeTypeString, 
            @QueryParam(VALIDATION_TYPE)String validationTypeString) 
            throws IDMapperException {
        validateInfo(info);
        RDFFormat format = getRDFFormatByMimeType(mimeType);
        StoreType storeType = parseStoreType(storeTypeString);
        ValidationType validationType = parseValidationType(validationTypeString);
        linksetInterface.loadString("Webservice Call", info, format, storeType, validationType);
        return "Load successful";
    }

    @Override
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/checkStringValid")
    public String checkStringValid(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType, 
            @QueryParam(STORE_TYPE)String storeTypeString, 
            @QueryParam(VALIDATION_TYPE)String validationTypeString) 
            throws IDMapperException {
        validateInfo(info);
        RDFFormat format = getRDFFormatByMimeType(mimeType);
        StoreType storeType = parseStoreType(storeTypeString);
        ValidationType validationType = parseValidationType(validationTypeString);
        linksetInterface.checkStringValid("Webservice Call", info, format, storeType, validationType);
        return "OK";
    }
 
}
