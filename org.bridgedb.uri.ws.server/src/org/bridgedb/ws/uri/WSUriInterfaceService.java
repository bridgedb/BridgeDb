// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
package org.bridgedb.ws.uri;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.linkset.LinksetInterfaceMinimal;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.reader.StatementReader;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.uri.Mapping;
import org.bridgedb.uri.Profile;
import org.bridgedb.uri.UriMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.WSCoreService;
import org.bridgedb.ws.WSUriInterface;
import org.bridgedb.ws.WsConstants;
import org.bridgedb.ws.WsUriConstants;
import org.bridgedb.ws.bean.DataSourceUriPatternBean;
import org.bridgedb.ws.bean.MappingBean;
import org.bridgedb.ws.bean.MappingSetInfoBean;
import org.bridgedb.ws.bean.OverallStatisticsBean;
import org.bridgedb.ws.bean.ProfileBean;
import org.bridgedb.ws.bean.UriExistsBean;
import org.bridgedb.ws.bean.UriMappings;
import org.bridgedb.ws.bean.UriSearchBean;
import org.bridgedb.ws.bean.XrefBean;
import org.openrdf.rio.RDFFormat;

@Path("/")
public class WSUriInterfaceService extends WSCoreService implements WSUriInterface {

    protected UriMapper uriMapper;
    protected LinksetInterfaceMinimal linksetInterface;
//    private String validationTypeString;
    public final String MIME_TYPE = "mimeType";
    public final String STORE_TYPE = "storeType";
    public final String VALIDATION_TYPE = "validationType";
    public final String INFO = "info"; 
    public final String FILE = "file";     
    public final String NO_RESULT = null;
    
    static final Logger logger = Logger.getLogger(WSUriInterfaceService.class);

    /**
     * Defuault constuctor for super classes.
     * 
     * Super classes will have the responsibilites of setting up the idMapper.
     */
    protected WSUriInterfaceService() throws BridgeDBException {
        super();
        this.linksetInterface = new LinksetLoader();
        uriMapper = SQLUriMapper.factory(false, StoreType.LIVE);
        idMapper = uriMapper;
    }

    public WSUriInterfaceService(UriMapper uriMapper) throws BridgeDBException {
        super(uriMapper);
        this.uriMapper = uriMapper;
        this.linksetInterface = new LinksetLoader();
        logger.info("WS Service running using supplied uriMapper");
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAP)
    @Override
    public List<MappingBean> map(
            @QueryParam(WsConstants.ID) String id,
            @QueryParam(WsConstants.DATASOURCE_SYSTEM_CODE) String scrCode,
    		@QueryParam(WsUriConstants.URI) String uri,
     		@QueryParam(WsUriConstants.PROFILE_URI) String profileUri,
            @QueryParam(WsConstants.TARGET_DATASOURCE_SYSTEM_CODE) List<String> targetCodes,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) List<String> targetUriPatterns) throws BridgeDBException {
       if (logger.isDebugEnabled()){
            logger.debug("map called! ");
            if (id != null){
                logger.debug("id = " + id);
            }
            if (scrCode != null){
                logger.debug("   scrCode = " + scrCode);             
            }
            if (uri != null){
                logger.debug("   uri = " + uri);             
            }
            logger.debug("   profileUri = " + profileUri);
            if (targetCodes!= null && !targetCodes.isEmpty()){
                logger.debug("   targetCodes = " + targetCodes);
            }
            if (targetUriPatterns!= null && !targetUriPatterns.isEmpty()){
                logger.debug("   targetUriPatterns = " + targetUriPatterns);
            }
        }
        DataSource[] targetDataSources = getDataSources(targetCodes);
        UriPattern[] targetPatterns = getUriPatterns(targetUriPatterns);
        if (id == null){
            if (scrCode != null) {
                throw new BridgeDBException (WsConstants.DATASOURCE_SYSTEM_CODE + " parameter " + scrCode 
                        + " should only be used together with " + WsConstants.ID + " parameter "); 
            }
            if (uri == null){
                throw new BridgeDBException ("Please provide either a " + WsConstants.ID + " or a "
                        + WsUriConstants.URI + " parameter.");                 
            }  
            return map(uri, profileUri, targetDataSources, targetPatterns);
        } else {
            if (uri != null){
                throw new BridgeDBException ("Please provide either a " + WsConstants.ID + " or a "
                        + WsConstants.DATASOURCE_SYSTEM_CODE + " parameter, but not both.");                 
            }
            if (scrCode == null) {
                throw new BridgeDBException (WsConstants.ID + " parameter must come with a " 
                        + WsConstants.DATASOURCE_SYSTEM_CODE + " parameter."); 
            }
        }
        return map(id, scrCode, profileUri, targetDataSources, targetPatterns);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAP_URI)
    public UriMappings mapUri(
    		@QueryParam(WsUriConstants.URI) List<String> uri,
     		@QueryParam(WsUriConstants.PROFILE_URI) String profileUri,
            @QueryParam(WsUriConstants.TARGET_URI_PATTERN) String targetUriPattern) throws BridgeDBException {
       if (logger.isDebugEnabled()){
            logger.debug("map called! ");
            if (uri != null){
                logger.debug("   uri = " + uri);             
            }
            logger.debug("   profileUri = " + profileUri);
            if (targetUriPattern!= null && !targetUriPattern.isEmpty()){
                logger.debug("   targetUriPatterns = " + targetUriPattern);
            }
        }
       Set<String> results = new HashSet<String>();
       UriPattern pattern = UriPattern.existingByPattern(targetUriPattern);
       for(String single:uri){
           results.addAll(uriMapper.mapUri(profileUri, profileUri, pattern));
       }
       return UriMappings.asBean(uri, results);
    }

    private List<MappingBean> map(String uri, String profileUri, DataSource[] targetDataSources, 
            UriPattern[] targetPatterns) throws BridgeDBException {
        Set<Mapping> mappings;
        if (targetDataSources == null){
            if (targetPatterns == null){
                mappings = uriMapper.mapFull(uri, profileUri);
            } else {
                mappings = mapByTargetUriPattern(uri, profileUri, targetPatterns);
            }
        } else {
            mappings = mapByTargetDataSource (uri, profileUri, targetDataSources);
            if (targetPatterns != null){
                mappings.addAll(mapByTargetUriPattern(uri, profileUri, targetPatterns));                
            } 
        }
        ArrayList<MappingBean> results = new ArrayList<MappingBean>();
        for (Mapping mapping:mappings){
            results.add(MappingBean.asBean(mapping));
        }
        return results; 
    }
    
    private List<MappingBean> map(String id, String scrCode, String profileUri, DataSource[] targetDataSources, 
            UriPattern[] targetPatterns) throws BridgeDBException {
        DataSource dataSource = DataSource.getBySystemCode(scrCode);
        Xref sourceXref = new Xref(id, dataSource);
        Set<Mapping> mappings;
        if (targetDataSources == null){
            if (targetPatterns == null){
                mappings = uriMapper.mapFull(sourceXref, profileUri);
            } else {
                mappings = mapByTargetUriPattern(sourceXref, profileUri, targetPatterns);
            }
        } else {
            mappings = mapByTargetDataSource (sourceXref, profileUri, targetDataSources);
            if (targetPatterns != null){
                mappings.addAll(mapByTargetUriPattern(sourceXref, profileUri, targetPatterns));                
            } 
        }
        ArrayList<MappingBean> results = new ArrayList<MappingBean>();
        for (Mapping mapping:mappings){
            results.add(MappingBean.asBean(mapping));
        }
        return results; 
    }

    private DataSource[] getDataSources(List<String> targetCodes){
        if (targetCodes == null || targetCodes.isEmpty()){
            return null;
        }
        HashSet<DataSource> targets = new HashSet<DataSource>();
        for (String targetCode:targetCodes){
            if (targetCode != null && !targetCode.isEmpty()){
                targets.add(DataSource.getBySystemCode(targetCode));
            }
        }
        if (targets.isEmpty()){
            return null; 
        }
        return targets.toArray(new DataSource[0]);        
    }
            
    private UriPattern[] getUriPatterns(List<String> targetUriPatterns){
        if (targetUriPatterns == null || targetUriPatterns.isEmpty()){
            return null;
        }
        HashSet<UriPattern> targets = new HashSet<UriPattern>();
        for (String targetUriPattern:targetUriPatterns){
            UriPattern pattern = UriPattern.existingByPattern(targetUriPattern);
            if (pattern != null){
                targets.add(pattern);
            }
        }
        if (targets.isEmpty()){
            return new UriPattern[0]; 
        }
        return targets.toArray(new UriPattern[0]);        
    }
    
    private Set<Mapping> mapByTargetDataSource(String sourceUri, String profileUri, DataSource[] targetDataSources) throws BridgeDBException{
        if (targetDataSources.length > 0){
            return uriMapper.mapFull(sourceUri, profileUri, targetDataSources);
        } else {
            return  new HashSet<Mapping>();
        }    
    }
    
    private Set<Mapping> mapByTargetDataSource(Xref sourceXref, String profileUri, DataSource[] targetDataSources) throws BridgeDBException{
        if (targetDataSources.length > 0){
            return uriMapper.mapFull(sourceXref, profileUri, targetDataSources);
        } else {
            return  new HashSet<Mapping>();
        }    
    }
    
    private Set<Mapping> mapByTargetUriPattern(String sourceUri, String profileUri, UriPattern[] targetUriPattern) throws BridgeDBException{
        if (targetUriPattern.length > 0){
            return uriMapper.mapFull(sourceUri, profileUri, targetUriPattern);
        } else {
            return  new HashSet<Mapping>();
        }    
    }
    
    private Set<Mapping> mapByTargetUriPattern(Xref sourceXref, String profileUri, UriPattern[] targetUriPattern) throws BridgeDBException{
        if (targetUriPattern.length > 0){
            return uriMapper.mapFull(sourceXref, profileUri, targetUriPattern);
        } else {
            return  new HashSet<Mapping>();
        }    
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.URI_EXISTS)
    @Override
    public UriExistsBean UriExists(@QueryParam(WsUriConstants.URI) String URI) throws BridgeDBException {
        if (URI == null) throw new BridgeDBException(WsUriConstants.URI + " parameter missing.");
        if (URI.isEmpty()) throw new BridgeDBException(WsUriConstants.URI + " parameter may not be null.");
        boolean exists = uriMapper.uriExists(URI);
        return new UriExistsBean(URI, exists);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.URI_SEARCH)
    @Override
    public UriSearchBean UriSearch(@QueryParam(WsUriConstants.TEXT) String text,
            @QueryParam(WsUriConstants.LIMIT) String limitString) throws BridgeDBException {
        if (text == null) throw new BridgeDBException(WsUriConstants.TEXT + " parameter missing.");
        if (text.isEmpty()) throw new BridgeDBException(WsUriConstants.TEXT + " parameter may not be null.");
        if (limitString == null || limitString.isEmpty()){
            Set<String> uris = uriMapper.uriSearch(text, Integer.MAX_VALUE);
            return new UriSearchBean(text, uris);
        } else {
            int limit = Integer.parseInt(limitString);
            Set<String> uris = uriMapper.uriSearch(text, limit);
            return new UriSearchBean(text, uris);
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.TO_XREF)
    @Override
    public XrefBean toXref(@QueryParam(WsUriConstants.URI) String URI) throws BridgeDBException {
        if (URI == null) throw new BridgeDBException(WsUriConstants.URI + " parameter missing.");
        if (URI.isEmpty()) throw new BridgeDBException(WsUriConstants.URI + " parameter may not be null.");
        logger.info(URI);
        Xref xref = uriMapper.toXref(URI);
        logger.info(xref);
        if (xref == null){
            return new XrefBean();  //Returns an empty bean
        }
        return XrefBean.asBean(xref);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAPPING)
    public Mapping getMapping() throws BridgeDBException {
       throw new BridgeDBException("Path parameter missing.");
    }

    @Override
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.MAPPING + "/{id}")
    public MappingBean getMapping(@PathParam(WsUriConstants.ID) String idString) throws BridgeDBException {
        if (idString == null) throw new BridgeDBException("Path parameter missing.");
        if (idString.isEmpty()) throw new BridgeDBException("Path parameter may not be null.");
        int id = Integer.parseInt(idString);
        Mapping mapping = uriMapper.getMapping(id);
        return MappingBean.asBean(mapping);
    }

    /*@Override
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.GET_SAMPLE_MAPPINGS) 
    public List<Mapping> getSampleMappings() throws BridgeDBException {
        return uriMapper.getSampleMapping();
    }*/

    @Override
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.GET_OVERALL_STATISTICS) 
    public OverallStatisticsBean getOverallStatistics() throws BridgeDBException {
        OverallStatistics overallStatistics = uriMapper.getOverallStatistics();
        OverallStatisticsBean bean = OverallStatisticsBean.asBean(overallStatistics);
        return bean;
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.GET_MAPPING_INFO + WsUriConstants.XML) 
    public List<MappingSetInfoBean> getMappingSetInfosXML(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode) throws BridgeDBException {
        return getMappingSetInfos(scrCode, targetCode);
    }
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.GET_MAPPING_INFO) 
    public List<MappingSetInfoBean> getMappingSetInfos(@QueryParam(WsUriConstants.SOURCE_DATASOURCE_SYSTEM_CODE) String scrCode,
            @QueryParam(WsUriConstants.TARGET_DATASOURCE_SYSTEM_CODE) String targetCode) throws BridgeDBException {
        List<MappingSetInfo> infos = uriMapper.getMappingSetInfos(scrCode, targetCode);
        ArrayList<MappingSetInfoBean> results = new ArrayList<MappingSetInfoBean>();
        for (MappingSetInfo info:infos){
            results.add(MappingSetInfoBean.asBean(info));
        }
        return results;
    }

	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@Path("/profile/{id}")
	public ProfileBean getProfile(@PathParam("id") String id) throws BridgeDBException {
		ProfileInfo profile = uriMapper.getProfile(Profile.getProfileURI(Integer.parseInt(id)));
		ProfileBean result = ProfileBean.asBean(profile);
		return result;
	}
    
	@Override
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/profile") 
	public List<ProfileBean> getProfiles() throws BridgeDBException {
		List<ProfileInfo> profiles = uriMapper.getProfiles();
		List<ProfileBean> results = new ArrayList<ProfileBean>();
		for (ProfileInfo profile:profiles) {
			results.add(ProfileBean.asBean(profile));
		}
		return results;
	}
    
    @Override
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.GET_MAPPING_INFO + "/{id}")
    public MappingSetInfoBean getMappingSetInfo(@PathParam("id") String idString) throws BridgeDBException {
        if (idString == null) throw new BridgeDBException("Path parameter missing.");
        if (idString.isEmpty()) throw new BridgeDBException("Path parameter may not be null.");
        int id = Integer.parseInt(idString);
        MappingSetInfo info = uriMapper.getMappingSetInfo(id);
        return MappingSetInfoBean.asBean(info);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/" + WsUriConstants.DATA_SOURCE)
    public DataSourceUriPatternBean getDataSource() throws BridgeDBException {
        throw new BridgeDBException("id path parameter missing.");
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Override
    @Path("/" + WsUriConstants.DATA_SOURCE + "/{id}")
    public DataSourceUriPatternBean getDataSource(@PathParam("id") String id) throws BridgeDBException {
        if (id == null) throw new BridgeDBException("Path parameter missing.");
        if (id.isEmpty()) throw new BridgeDBException("Path parameter may not be null.");
        DataSource ds = DataSource.getBySystemCode(id);
        DataSourceUriPatternBean bean = new DataSourceUriPatternBean(ds, uriMapper.getUriPatterns(id));
        return bean;
    }
    
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Override
    @Path("/" + WsUriConstants.SQL_COMPAT_VERSION)
    public String getSqlCompatVersion() throws BridgeDBException {
        return "" + uriMapper.getSqlCompatVersion();
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
    
    protected final RDFFormat getRDFFormatByMimeType(String mimeType) throws BridgeDBException{
        if (mimeType == null){
            throw new BridgeDBException (MIME_TYPE + " parameter may not be null");
        }
        mimeType = trim(mimeType);
        if (mimeType.isEmpty()){
            throw new BridgeDBException (MIME_TYPE + " parameter may not be empty");
        }
        return StatementReader.getRDFFormatByMimeType(mimeType);
    }
    
    protected final StoreType parseStoreType(String storeTypeString) throws BridgeDBException{
        if (storeTypeString == null){
            throw new BridgeDBException (STORE_TYPE + " parameter may not be null");
        }
        storeTypeString = trim(storeTypeString);
        if (storeTypeString.isEmpty()){
            throw new BridgeDBException (STORE_TYPE + " parameter may not be empty");
        }
        return StoreType.parseString(storeTypeString);
    }

    protected final ValidationType parseValidationType(String validationTypeString) throws BridgeDBException{
        if (validationTypeString == null){
            throw new BridgeDBException (VALIDATION_TYPE + " parameter may not be null");
        }
        if (validationTypeString.trim().isEmpty()){
            throw new BridgeDBException (VALIDATION_TYPE + " parameter may not be empty");
        }
        return ValidationType.parseString(validationTypeString);
    }
    
    protected final void validateInfo(String info) throws BridgeDBException{
        if (info == null){
            throw new BridgeDBException (INFO + " parameter may not be null");
        }
        if (info.trim().isEmpty()){
            throw new BridgeDBException (INFO + " parameter may not be empty");
        }        
    }
    
    void validateInputStream(InputStream inputStream) throws BridgeDBException {
        if (inputStream == null){
            throw new BridgeDBException (FILE + " parameter may not be null");
        }
    }

    /*@GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateString")
    public ValidationBean getValidateString(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType, 
            @QueryParam(STORE_TYPE)String storeTypeString, 
            @QueryParam(VALIDATION_TYPE)String validationTypeString, 
            @QueryParam("includeWarnings")String includeWarningsString) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateString called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (storeTypeString == null){
                        logger.debug("NO storeTypeString");
                    } else {
                        logger.debug("storeTypeString = " + storeTypeString);
                    }
                    if (includeWarningsString == null){
                        logger.debug("NO svalidationTypeString");
                    } else {
                        logger.debug("validationTypeString = " + includeWarningsString);
                    }
                    if (includeWarningsString == null){
                        logger.debug("NO includeWarningsStringg");
                    } else {
                        logger.debug("includeWarningsString = " + includeWarningsString);
                    }
                }
        ValidationBean result = validateString(info, mimeType, storeTypeString, validationTypeString, includeWarningsString);
        return result;
    }

    /*@Override
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateString")
    public ValidationBean validateString(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType, 
            @FormParam(STORE_TYPE)String storeTypeString, 
            @FormParam(VALIDATION_TYPE)String validationTypeString, 
            @FormParam("includeWarnings")String includeWarningsString) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateString called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (storeTypeString == null){
                        logger.debug("NO storeTypeString");
                    } else {
                        logger.debug("storeTypeString = " + storeTypeString);
                    }
                    if (includeWarningsString == null){
                        logger.debug("NO svalidationTypeString");
                    } else {
                        logger.debug("validationTypeString = " + includeWarningsString);
                    }
                    if (includeWarningsString == null){
                        logger.debug("NO includeWarningsStringg");
                    } else {
                        logger.debug("includeWarningsString = " + includeWarningsString);
                    }
                }
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/validateString")
    public ValidationBean validateInputStream(@FormDataParam("file") InputStream uploadedInputStream, 
            @FormParam(MIME_TYPE)String mimeType, 
            @FormParam(STORE_TYPE)String storeTypeString, 
            @FormParam(VALIDATION_TYPE)String validationTypeString, 
            @FormParam("includeWarnings")String includeWarningsString) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateInputStream called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                    if (storeTypeString == null){
                        logger.debug("NO storeTypeString");
                    } else {
                        logger.debug("storeTypeString = " + storeTypeString);
                    }
                    if (validationTypeString == null){
                        logger.debug("NO svalidationTypeString");
                    } else {
                        logger.debug("validationTypeString = " + validationTypeString);
                    }
                    if (includeWarningsString == null){
                        logger.debug("NO includeWarningsStringg");
                    } else {
                        logger.debug("includeWarningsString = " + includeWarningsString);
                    }
                }
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_XML)
    @Path("/validateStringXML")
    public ValidationBean validateString(JAXBElement<ValidationBean> input) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateString(JAXBElement<ValidationBean> input)!");
                    if (input == null){
                        logger.debug("NO input");
                    } else {
                        logger.debug("input = " + input);
                    }
                }
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
        if (includeWarnings){
            return validateString(info, mimeType, storeType, validationType, "true");
        } else {
            return validateString(info, mimeType, storeType, validationType, "false");
        }     
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsVoid")
    public ValidationBean validateStringAsVoid(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateStringAsVoid called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInfo(info);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateString("Webservice Call", info, format, StoreType.TEST, 
                    ValidationType.VOID, true);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, info, mimeType, StoreType.LIVE, ValidationType.VOID, true, exception);
    }

    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateInputStreamAsVoid")
    public ValidationBean validateInputStreamAsVoid(@FormDataParam("file") InputStream uploadedInputStream, 
            @FormDataParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateInputStreamAsVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInputStream(uploadedInputStream);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateInputStream("Webservice Call", uploadedInputStream,  format, 
                    StoreType.TEST, ValidationType.VOID, true);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, "data read directly from the Stream", mimeType, StoreType.LIVE, 
                ValidationType.LINKS, true,exception);
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsVoidXML")
    public ValidationBean validateStringAsVoidXML(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateStringAsVoidXML called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        return validateStringAsVoid(info, mimeType);
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsLinkSet")
    public ValidationBean validateInputStreamAsLinkSet(@FormDataParam("file") InputStream uploadedInputStream, 
            @FormDataParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateInputStreamAsLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInputStream(uploadedInputStream);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateInputStream("Webservice Call", uploadedInputStream, format, 
                    StoreType.TEST, ValidationType.LINKS, true);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, "data read directly from the Stream", mimeType, StoreType.LIVE, 
                ValidationType.LINKS, true,exception);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsVoid")
    public ValidationBean getValidateStringAsVoid(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateStringAsVoid called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        return validateStringAsVoid(info, mimeType);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsVoidXML")
    public ValidationBean getValidateStringAsVoidXML(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateStringAsVoidXML called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        return validateStringAsVoid(info, mimeType);
    }

    /*@Override
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsLinksetVoid")
    public ValidationBean validateStringAsLinksetVoid(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws BridgeDBException {
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
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsLinkSet")
    public ValidationBean validateStringAsLinkSet(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateStringAsLinkSet called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInfo(info);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateString("Webservice Call", info, format, StoreType.TEST, 
                    ValidationType.LINKS,true);
        } catch (Exception e){
            exception = e.toString();
        }
        return new ValidationBean(report, info, mimeType, StoreType.LIVE, ValidationType.LINKS, true,exception);
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsLinkSetXML")
    public ValidationBean validateStringAsLinkSetXML(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateStringAsLinkSetXML called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        String report = NO_RESULT;
        String exception = null;
        try{
            validateInfo(info);
            RDFFormat format = getRDFFormatByMimeType(mimeType);
            report =  linksetInterface.validateString("Webservice Call", info, format, StoreType.TEST, 
                    ValidationType.LINKS,true);
        } catch (Exception e){
            exception = e.toString();
        }
        return validateStringAsLinkSet(info, mimeType);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsLinkSet")
    public ValidationBean getValidateStringAsLinkSet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateStringAsLinkSet called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        return validateStringAsLinkSet(info, mimeType);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/validateStringAsLinkSetXML")
    public ValidationBean getValidateStringAsLinkSetXML(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType) throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateStringAsLinkSetXML called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }
                }
        return validateStringAsLinkSet(info, mimeType);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadString")
    public String loadString(@Context HttpServletRequest hsr,
            @QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType, 
            @QueryParam(STORE_TYPE)String storeTypeString, 
            @QueryParam(VALIDATION_TYPE)String validationTypeString) 
            throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateString called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (storeTypeString == null){
                        logger.debug("NO storeTypeString");
                    } else {
                        logger.debug("storeTypeString = " + storeTypeString);
                    }
                    if (validationTypeString == null){
                        logger.debug("NO svalidationTypeString");
                    } else {
                        logger.debug("validationTypeString = " + validationTypeString);
                    }
                }
        validateInfo(info);
        RDFFormat format = getRDFFormatByMimeType(mimeType);
        StoreType storeType = parseStoreType(storeTypeString);
        ValidationType validationType = parseValidationType(validationTypeString);
        String owner = IpConfig.checkIPAddress(hsr.getRemoteAddr());
        if (owner == null){
            return linksetInterface.saveString("Webservice Call", info, format, storeType, validationType);
        } else {
            return linksetInterface.loadString("Webservice Call", info, format, storeType, validationType);
        }
    }

    @Override
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/checkStringValid")
    public String checkStringValid(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType, 
            @QueryParam(STORE_TYPE)String storeTypeString, 
            @QueryParam(VALIDATION_TYPE)String validationTypeString) 
            throws BridgeDBException {
                if (logger.isDebugEnabled()){
                    logger.debug("checkStringValid called!");
                    if (info == null){
                        logger.debug("NO Info");
                    } else {
                        logger.debug("info length = " + info.length());
                    }
                    if (storeTypeString == null){
                        logger.debug("NO storeTypeString");
                    } else {
                        logger.debug("storeTypeString = " + storeTypeString);
                    }
                    if (validationTypeString == null){
                        logger.debug("NO svalidationTypeString");
                    } else {
                        logger.debug("validationTypeString = " + validationTypeString);
                    }
                }
        validateInfo(info);
        RDFFormat format = getRDFFormatByMimeType(mimeType);
        StoreType storeType = parseStoreType(storeTypeString);
        ValidationType validationType = parseValidationType(validationTypeString);
        linksetInterface.checkStringValid("Webservice Call", info, format, storeType, validationType);
        return "OK";
    }*/

}
