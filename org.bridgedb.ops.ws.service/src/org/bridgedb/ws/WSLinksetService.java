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


import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.IpConfig;
import org.bridgedb.utils.StoreType;
import org.openrdf.rio.RDFFormat;

/**
 * WebService forms of the LinksetInterfaceMinimal methods.
 * 
 * Currently contains many repeated methods due to the methods not picking up an InputStream and any other parameter at the same time.
 * 
 * @author Christian
 */
public class WSLinksetService extends WSUrlService{
    
    static final Logger logger = Logger.getLogger(WSOpsInterfaceService.class);

    public WSLinksetService()  throws IDMapperException   {
        super();
        logger.info("WsOpsServer setup");        
    }
    
    // validateString methods
            
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateRdf")
    public Response validateRdf(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateRdf called!");
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
        return validateString(info, mimeType, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateRdf")
    public Response validateRdfGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateRdf called!");
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
        return validateString(info, mimeType, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateVoid")
    public Response validateVoid(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateVoid called!");
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
        return validateString(info, mimeType, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateVoid")
    public Response validateVoidGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateVoid called!");
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
        return validateString(info, mimeType, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateLinkSet")
    public Response validateLinkSet(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateLinkSet called!");
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
        return validateString(info, mimeType, ValidationType.LINKS, httpServletRequest);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateLinkSet")
    public Response validateLinkSetGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateLinkSet called!");
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
        return validateString(info, mimeType, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateMinimum")
    public Response validateMinimum(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateMinimum called!");
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
        return validateString(info, mimeType, ValidationType.LINKSMINIMAL, httpServletRequest);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/validateMinimum")
    public Response validateMinimumGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getMinimumSet called!");
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
        return validateString(info, mimeType, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    private Response validateString(String info, String mimeType, ValidationType validationType, 
            HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
       String report = null;
       try{
            if (info != null && !info.isEmpty()){
                RDFFormat format = getRDFFormatByMimeType(mimeType);
                report = linksetInterface.validateString("Webservice Call", info, format, StoreType.TEST, validationType, true);
            }
        } catch (Exception e){
            report = e.toString();
        }
        StringBuilder sb = topAndSide(validationType.getName() + " Validator", httpServletRequest);
        addValidationForm(sb, validationType, info, report);
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    // validateInputStream methods
    
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateTurtleVoid")
    public Response validateTurtleVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateTurtleVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateTurtleVoid")
    public Response validateTurtleVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateTurtleVoid called!");
                }
        return validateInputStream(null, RDFFormat.TURTLE, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateRdfXmlVoid")
    public Response validateRdfXmlVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateRdfXmlVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateRdfXmlVoid")
    public Response validateRdfXmlVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateRdfXmlVoid called!");
                }
        return validateInputStream(null, RDFFormat.RDFXML, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateNTriplesVoid")
    public Response validateNTriplesVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateNTriplesVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateNTriplesVoid")
    public Response validateNTriplesVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateNTriplesVoid called!");
                }
        return validateInputStream(null, RDFFormat.NTRIPLES, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateTurtleLinkSet")
    public Response validateTurtleLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateTurtleLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateTurtleLinkSet")
    public Response validateTurtleLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateTurtleLinkSet called!");
                }
        return validateInputStream(null, RDFFormat.TURTLE, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateRdfXmlLinkSet")
    public Response validateRdfXmlLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateRdfXmlLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateRdfXmlLinkSet")
    public Response validateRdfXmlLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateRdfXmlLinkSet called!");
                }
        return validateInputStream(null, RDFFormat.RDFXML, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateNTriplesLinkSet")
    public Response validateNTriplesLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateNTriplesLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateNTriplesLinkSet")
    public Response validateNTriplesLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateNTriplesLinkSet called!");
                }
        return validateInputStream(null, RDFFormat.NTRIPLES, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateTurtleMinimum")
    public Response validateTurtleMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateTurtleMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateTurtleMinimum")
    public Response validateTurtleMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateTurtleMinimum called!");
                }
        return validateInputStream(null, RDFFormat.TURTLE, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateRdfXmlMinimum")
    public Response validateRdfXmlMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateRdfXmlMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateRdfXmlMinimum")
    public Response validateRdfXmlMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateRdfXmlMinimum called!");
                }
        return validateInputStream(null, RDFFormat.RDFXML, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateNTriplesMinimum")
    public Response validateNTriplesMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateNTriplesMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateNTriplesMinimum")
    public Response validateNTriplesMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateNTriplesMinimum called!");
                }
        return validateInputStream(null, RDFFormat.NTRIPLES, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateTurtleRdf")
    public Response validateTurtleRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateTurtleRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateTurtleRdf")
    public Response validateTurtleRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateTurtleRdf called!");
                }
        return validateInputStream(null, RDFFormat.TURTLE, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateRdfXmlRdf")
    public Response validateRdfXmlRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateRdfXmlRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateRdfXmlRdf")
    public Response validateRdfXmlRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateRdfXmlRdf called!");
                }
        return validateInputStream(null, RDFFormat.RDFXML, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateNTriplesRdf")
    public Response validateNTriplesRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateNTriplesRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return validateInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validateNTriplesRdf")
    public Response validateNTriplesRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("getValidateNTriplesRdf called!");
                }
        return validateInputStream(null, RDFFormat.NTRIPLES, ValidationType.ANY_RDF, httpServletRequest);
    }

    private Response validateInputStream(InputStream input, RDFFormat format, ValidationType validationType, 
            HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
       String report = null;
       try{
            if (input != null && input.available() > 10){
                report = linksetInterface.validateInputStream("Webservice Call", input, format, StoreType.TEST, validationType, true);
            }
        } catch (Exception e){
            report = e.toString();
        }
        StringBuilder sb = topAndSide(validationType.getName() + " Validator", httpServletRequest);
        addValidateForm(sb, validationType, report);
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
   
    //Load String methods
    
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadRdf")
    public Response loadRdf(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdf called!");
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
        return loadString(info, mimeType, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadRdf")
    public Response loadRdfGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfGet called!");
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
        return loadString(info, mimeType, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadVoid")
    public Response loadVoid(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadVoid called!");
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
        return loadString(info, mimeType, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadVoid")
    public Response loadVoidGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadVoidGet called!");
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
        return loadString(info, mimeType, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadLinkSet")
    public Response loadLinkSet(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadLinkSet called!");
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
        return loadString(info, mimeType, ValidationType.LINKS, httpServletRequest);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadLinkSet")
    public Response loadLinkSetGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadLinkSetGet called!");
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
        return loadString(info, mimeType, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadMinimum")
    public Response loadMinimum(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadMinimum called!");
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
        return loadString(info, mimeType, ValidationType.LINKSMINIMAL, httpServletRequest);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/loadMinimum")
    public Response loadMinimumGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadMinimumGet called!");
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
        return loadString(info, mimeType, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    private Response loadString(String info, String mimeType, ValidationType validationType,
            HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
        if (IpConfig.isAdminIPAddress(httpServletRequest.getRemoteAddr())){
            String report = null;
            try{
                if (info != null && !info.isEmpty()){
                    RDFFormat format = getRDFFormatByMimeType(mimeType);
                    report = linksetInterface.loadString("Webservice Call", info, format, StoreType.TEST, validationType);
                }
            } catch (Exception e){
                report = e.toString();
            }
            StringBuilder sb = topAndSide("Welcome Admin! Load a " + validationType.getName(), httpServletRequest);
            addLoadForm(sb, validationType, info, report);
            sb.append(END);
            return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
        } else {
            return redirectToSave(httpServletRequest);
        }
    }

    //load InputStream methods
    
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadTurtleVoid")
    public Response loadTurtleVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadTurtleVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadTurtleVoid")
    public Response loadTurtleVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadTurtleVoidGet called!");
                }
        return loadInputStream(null, RDFFormat.TURTLE, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadRdfXmlVoid")
    public Response loadRdfXmlVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfXmlVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadRdfXmlVoid")
    public Response loadRdfXmlVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfXmlvoidGet called!");
                }
        return loadInputStream(null, RDFFormat.RDFXML, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadNTriplesVoid")
    public Response loadNTriplesVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadNTriplesVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadNTriplesVoid")
    public Response loadNTriplesVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadNTriplesVoidGet called!");
                }
        return loadInputStream(null, RDFFormat.NTRIPLES, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadTurtleLinkSet")
    public Response loadTurtleLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadTurtleLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadTurtleLinkSet")
    public Response loadTurtleLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadTurtleLinkSetGet called!");
                }
        return loadInputStream(null, RDFFormat.TURTLE, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadRdfXmlLinkSet")
    public Response loadRdfXmlLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfXmlLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadRdfXmlLinkSet")
    public Response loadRdfXmlLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfXmlLinkSetGet called!");
                }
        return loadInputStream(null, RDFFormat.RDFXML, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadNTriplesLinkSet")
    public Response loadNTriplesLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadNTriplesLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadNTriplesLinkSet")
    public Response loadNTriplesLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadeNTriplesLinkSetGet called!");
                }
        return loadInputStream(null, RDFFormat.NTRIPLES, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadTurtleMinimum")
    public Response loadTurtleMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadTurtleMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadTurtleMinimum")
    public Response loadTurtleMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadTurtleMinimumGet called!");
                }
        return loadInputStream(null, RDFFormat.TURTLE, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadRdfXmlMinimum")
    public Response loadRdfXmlMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfXmlMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadRdfXmlMinimum")
    public Response loadRdfXmlMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfXmlMinimumGet called!");
                }
        return loadInputStream(null, RDFFormat.RDFXML, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadNTriplesMinimum")
    public Response loadNTriplesMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadNTriplesMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadNTriplesMinimum")
    public Response loadNTriplesMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadNTriplesMinimumGet called!");
                }
        return loadInputStream(null, RDFFormat.NTRIPLES, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadTurtleRdf")
    public Response loadTurtleRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadTurtleRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadTurtleRdf")
    public Response loadTurtleRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadTurtleRdfGet called!");
                }
        return loadInputStream(null, RDFFormat.TURTLE, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadRdfXmlRdf")
    public Response loadRdfXmlRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfXmlRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadRdfXmlRdf")
    public Response loadRdfXmlRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadRdfXmlRdfGet called!");
                }
        return loadInputStream(null, RDFFormat.RDFXML, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadNTriplesRdf")
    public Response loadNTriplesRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadNTriplesRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return loadInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/loadNTriplesRdf")
    public Response loadNTriplesRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadNTriplesRdfGet called!");
                }
        return loadInputStream(null, RDFFormat.NTRIPLES, ValidationType.ANY_RDF, httpServletRequest);
    }

    private Response loadInputStream(InputStream input, RDFFormat format, ValidationType validationType, 
            HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
        if (IpConfig.isAdminIPAddress(httpServletRequest.getRemoteAddr())){
            String report = null;
            try{
                if (input != null && input.available() > 10){
                    report = linksetInterface.loadInputStream("Webservice Call", input, format, StoreType.TEST, validationType);
                }
            } catch (Exception e){
                report = e.toString();
            }
            StringBuilder sb = topAndSide(validationType.getName() + " Loader", httpServletRequest);
            addLoadForm(sb, validationType, report);
            sb.append(END);
            return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
        } else {
            return redirectToSave(httpServletRequest);
        }
    }

    //Save String methods
    
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/saveRdf")
    public Response saveRdf(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdf called!");
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
        return saveString(info, mimeType, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/saveRdf")
    public Response saveRdfGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfGet called!");
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
        return saveString(info, mimeType, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/saveVoid")
    public Response saveVoid(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveVoid called!");
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
        return saveString(info, mimeType, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/saveVoid")
    public Response saveVoidGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveVoidGet called!");
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
        return saveString(info, mimeType, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/saveLinkSet")
    public Response saveLinkSet(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveLinkSet called!");
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
        return saveString(info, mimeType, ValidationType.LINKS, httpServletRequest);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/saveLinkSet")
    public Response saveLinkSetGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveLinkSetGet called!");
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
        return saveString(info, mimeType, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Path("/saveMinimum")
    public Response saveMinimum(@FormParam(INFO)String info, 
            @FormParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveMinimum called!");
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
        return saveString(info, mimeType, ValidationType.LINKSMINIMAL, httpServletRequest);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/saveMinimum")
    public Response saveMinimumGet(@QueryParam(INFO)String info, 
            @QueryParam(MIME_TYPE)String mimeType,
            @Context HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveMinimumGet called!");
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
        return saveString(info, mimeType, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    private Response saveString(String info, String mimeType, ValidationType validationType,
            HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
       String report = null;
       try{
            if (info != null && !info.isEmpty()){
                RDFFormat format = getRDFFormatByMimeType(mimeType);
                report = linksetInterface.saveString("Webservice Call", info, format, StoreType.TEST, validationType);
            }
        } catch (Exception e){
            report = e.toString();
        }
        StringBuilder sb = topAndSide("Save but not Load: " + validationType.getName(), httpServletRequest);
        addSaveForm(sb, validationType, info, report);
        sb.append("<h1>WARNING Data not Loaded</h1>");
        sb.append("<h2>Please contact an Admin to Load this data</h2>");
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    //Save InputStream methods
    
    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveTurtleVoid")
    public Response saveTurtleVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveTurtleVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveTurtleVoid")
    public Response saveTurtleVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveTurtleVoidGet called!");
                }
        return saveInputStream(null, RDFFormat.TURTLE, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveRdfXmlVoid")
    public Response saveRdfXmlVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfXmlVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveRdfXmlVoid")
    public Response saveRdfXmlVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfXmlvoidGet called!");
                }
        return saveInputStream(null, RDFFormat.RDFXML, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveNTriplesVoid")
    public Response saveNTriplesVoid(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveNTriplesVoid called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.VOID, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveNTriplesVoid")
    public Response saveNTriplesVoidGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveNTriplesVoidGet called!");
                }
        return saveInputStream(null, RDFFormat.NTRIPLES, ValidationType.VOID, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveTurtleLinkSet")
    public Response saveTurtleLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveTurtleLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveTurtleLinkSet")
    public Response saveTurtleLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveTurtleLinkSetGet called!");
                }
        return saveInputStream(null, RDFFormat.TURTLE, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveRdfXmlLinkSet")
    public Response saveRdfXmlLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfXmlLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveRdfXmlLinkSet")
    public Response saveRdfXmlLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfXmlLinkSetGet called!");
                }
        return saveInputStream(null, RDFFormat.RDFXML, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveNTriplesLinkSet")
    public Response saveNTriplesLinkSet(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveNTriplesLinkSet called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.LINKS, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveNTriplesLinkSet")
    public Response saveNTriplesLinkSetGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveeNTriplesLinkSetGet called!");
                }
        return saveInputStream(null, RDFFormat.NTRIPLES, ValidationType.LINKS, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveTurtleMinimum")
    public Response saveTurtleMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateTurtleMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveTurtleMinimum")
    public Response saveTurtleMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveTurtleMinimumGet called!");
                }
        return saveInputStream(null, RDFFormat.TURTLE, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveRdfXmlMinimum")
    public Response saveRdfXmlMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfXmlMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveRdfXmlMinimum")
    public Response saveRdfXmlMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfXmlMinimumGet called!");
                }
        return saveInputStream(null, RDFFormat.RDFXML, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveNTriplesMinimum")
    public Response saveNTriplesMinimum(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveNTriplesMinimum called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveNTriplesMinimum")
    public Response saveNTriplesMinimumGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveNTriplesMinimumGet called!");
                }
        return saveInputStream(null, RDFFormat.NTRIPLES, ValidationType.LINKSMINIMAL, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveTurtleRdf")
    public Response saveTurtleRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveTurtleRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.TURTLE, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveTurtleRdf")
    public Response saveTurtleRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveTurtleRdfGet called!");
                }
        return saveInputStream(null, RDFFormat.TURTLE, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveRdfXmlRdf")
    public Response saveRdfXmlRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfXmlRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.RDFXML, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveRdfXmlRdf")
    public Response saveRdfXmlRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveRdfXmlRdfGet called!");
                }
        return saveInputStream(null, RDFFormat.RDFXML, ValidationType.ANY_RDF, httpServletRequest);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveNTriplesRdf")
    public Response saveNTriplesRdf(@FormDataParam("file") InputStream uploadedInputStream,
            @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveNTriplesRdf called!");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                }
        return saveInputStream(uploadedInputStream, RDFFormat.NTRIPLES, ValidationType.ANY_RDF, httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/saveNTriplesRdf")
    public Response saveNTriplesRdfGet(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveNTriplesRdfGet called!");
                }
        return saveInputStream(null, RDFFormat.NTRIPLES, ValidationType.ANY_RDF, httpServletRequest);
    }

    private Response saveInputStream(InputStream input, RDFFormat format, ValidationType validationType, 
            HttpServletRequest httpServletRequest) throws IDMapperException, UnsupportedEncodingException {
       String report = null;
       try{
            if (input != null && input.available() > 10){
                report = linksetInterface.saveInputStream("Webservice Call", input, format, StoreType.TEST, validationType);
            }
        } catch (Exception e){
            report = e.toString();
        }
        StringBuilder sb = topAndSide(validationType.getName() + " Saver (Load to be done later by admin)", httpServletRequest);
        addSaveForm(sb, validationType, report);
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

     //Index methods for Validate InputStream methods
    
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/validate")
    public Response validateIndex(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("validateIndex called!");
                }
        StringBuilder sb = topAndSide("Validators Index", httpServletRequest);
        sb.append("\n<h1>Validate as a Void Description.</h1>");
        addValidateString(sb, ValidationType.VOID);
        addValidateFileLine(sb,  ValidationType.VOID, RDFFormat.TURTLE);
        addValidateFileLine(sb,  ValidationType.VOID, RDFFormat.RDFXML);
        addValidateFileLine(sb,  ValidationType.VOID, RDFFormat.NTRIPLES);
        sb.append("\n<h1>Validate as a Linkset.</h1>");
        addValidateString(sb, ValidationType.LINKS);
        addValidateFileLine(sb,  ValidationType.LINKS, RDFFormat.TURTLE);
        addValidateFileLine(sb,  ValidationType.LINKS, RDFFormat.RDFXML);
        addValidateFileLine(sb,  ValidationType.LINKS, RDFFormat.NTRIPLES);
        //if (IpConfig.isAdminIPAddress(hsr.getRemoteAddr())){
            sb.append("\n<h1>Validate a File as the minimum to load a linkset.</h1>");
            addValidateString(sb, ValidationType.LINKSMINIMAL);
            addValidateFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.TURTLE);
            addValidateFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.RDFXML);
            addValidateFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.NTRIPLES);
            sb.append("\n<h1>Validate a File as RDF.</h1>");
            addValidateString(sb, ValidationType.LINKSMINIMAL);
            addValidateFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.TURTLE);
            addValidateFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.RDFXML);
            addValidateFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.NTRIPLES);
        //}
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/save")
    public Response saveIndex(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveIndex called!");
                }
        return saveIndex("Savers Index", httpServletRequest);
    }

    public Response saveIndex(String title, @Context HttpServletRequest httpServletRequest) 
            throws IDMapperException {
                if (logger.isDebugEnabled()){
                    logger.debug("saveIndex called!");
                }
        StringBuilder sb = topAndSide(title, httpServletRequest);
        sb.append("\n<h1>Save as a Void Description.</h1>");
        addSaveString(sb, ValidationType.VOID);
        addSaveFileLine(sb,  ValidationType.VOID, RDFFormat.TURTLE);
        addSaveFileLine(sb,  ValidationType.VOID, RDFFormat.RDFXML);
        addSaveFileLine(sb,  ValidationType.VOID, RDFFormat.NTRIPLES);
        sb.append("\n<h1>Save as a Linkset.</h1>");
        addSaveString(sb, ValidationType.LINKS);
        addSaveFileLine(sb,  ValidationType.LINKS, RDFFormat.TURTLE);
        addSaveFileLine(sb,  ValidationType.LINKS, RDFFormat.RDFXML);
        addSaveFileLine(sb,  ValidationType.LINKS, RDFFormat.NTRIPLES);
        //if (IpConfig.isAdminIPAddress(hsr.getRemoteAddr())){
            sb.append("\n<h1>Save a File as the minimum to load a linkset.</h1>");
            addSaveString(sb, ValidationType.LINKSMINIMAL);
            addSaveFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.TURTLE);
            addSaveFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.RDFXML);
            addSaveFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.NTRIPLES);
            sb.append("\n<h1>Save a File as RDF.</h1>");
            addSaveString(sb, ValidationType.LINKSMINIMAL);
            addSaveFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.TURTLE);
            addSaveFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.RDFXML);
            addSaveFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.NTRIPLES);
        //}
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

    private Response redirectToSave(HttpServletRequest httpServletRequest) throws IDMapperException {
        logger.warn("Load attempt blocked from IP address " + httpServletRequest.getRemoteAddr());
        return saveIndex("Please Save and then ask an admin to Load", httpServletRequest);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Path("/load")
    public Response loadIndex(@Context HttpServletRequest httpServletRequest) 
            throws IDMapperException, UnsupportedEncodingException {
                if (logger.isDebugEnabled()){
                    logger.debug("loadIndex called!");
                }
        StringBuilder sb = topAndSide("Loaders Index", httpServletRequest);
        sb.append("\n<h1>Load as a Void Description.</h1>");
        addLoadString(sb, ValidationType.VOID);
        addLoadFileLine(sb,  ValidationType.VOID, RDFFormat.TURTLE);
        addLoadFileLine(sb,  ValidationType.VOID, RDFFormat.RDFXML);
        addLoadFileLine(sb,  ValidationType.VOID, RDFFormat.NTRIPLES);
        sb.append("\n<h1>Load as a Linkset.</h1>");
        addLoadString(sb, ValidationType.LINKS);
        addLoadFileLine(sb,  ValidationType.LINKS, RDFFormat.TURTLE);
        addLoadFileLine(sb,  ValidationType.LINKS, RDFFormat.RDFXML);
        addLoadFileLine(sb,  ValidationType.LINKS, RDFFormat.NTRIPLES);
        //if (IpConfig.isAdminIPAddress(hsr.getRemoteAddr())){
            sb.append("\n<h1>Load a File as the minimum to load a linkset.</h1>");
            addLoadString(sb, ValidationType.LINKSMINIMAL);
            addLoadFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.TURTLE);
            addLoadFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.RDFXML);
            addLoadFileLine(sb,  ValidationType.LINKSMINIMAL, RDFFormat.NTRIPLES);
            sb.append("\n<h1>Load a File as RDF.</h1>");
            addLoadString(sb, ValidationType.LINKSMINIMAL);
            addLoadFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.TURTLE);
            addLoadFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.RDFXML);
            addLoadFileLine(sb,  ValidationType.ANY_RDF, RDFFormat.NTRIPLES);
        //}
        sb.append(END);
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }
    
    /**
     * Allows Super classes to add to the side bar
     */
    protected void addSideBarMiddle(StringBuilder sb, HttpServletRequest httpServletRequest) throws IDMapperException{
        addSideBarIMS(sb);
        addSideBarItem(sb, "validate", "Validate");
        addSideBarItem(sb, "save", "Save");
        if (httpServletRequest != null && IpConfig.isAdminIPAddress(httpServletRequest.getRemoteAddr())){
            addSideBarItem(sb, "load", "Load");
        }
        addSideBarStatisitics(sb);
    }
    
    //Support methods 
    private void addValidationForm(StringBuilder sb, ValidationType validationType, String info, String report) throws BridgeDBException{
        sb.append("\n<p>Use this page to validate a ");
        addValidationTypeExplanation(sb, validationType);
        addValidationFormStart(sb,  validationType);
        if (report != null){
            addReport(sb, validationType, report);
        }
        //sb.append(FORM_OUTPUT_FORMAT);
        sb.append(FORM_MINE_TYPE);
        sb.append(FORM_INFO_START);
        if (info != null && !info.isEmpty()){
            sb.append(info);
        }
            
        sb.append(FORM_INFO_END);
        sb.append("\n<p>");
        sb.append(FORM_SUBMIT_TO_VALIDATE);        
        sb.append(FORM_NOTE);      
        sb.append("</p>");
    }
    
    private void addSaveForm(StringBuilder sb, ValidationType validationType, String info, String report) throws BridgeDBException{
        sb.append("\n<p>Use this page to save a ");
        addValidationTypeExplanation(sb, validationType);
        addSaveFormStart(sb,  validationType);
        if (report != null){
            addReport(sb, validationType, report);
        }
        //sb.append(FORM_OUTPUT_FORMAT);
        sb.append(FORM_MINE_TYPE);
        sb.append(FORM_INFO_START);
        if (info != null && !info.isEmpty()){
            sb.append(info);
        }
            
        sb.append(FORM_INFO_END);
        sb.append("\n<p>");
        sb.append(FORM_SUBMIT_TO_SAVE);        
        sb.append("</p>");
    }

    private void addLoadForm(StringBuilder sb, ValidationType validationType, String info, String report) throws BridgeDBException{
        addValidationTypeExplanation(sb, validationType);
        addLoadFormStart(sb,  validationType);
        if (report != null){
            addReport(sb, validationType, report);
        }
        //sb.append(FORM_OUTPUT_FORMAT);
        sb.append(FORM_MINE_TYPE);
        sb.append(FORM_INFO_START);
        if (info != null && !info.isEmpty()){
            sb.append(info);
        }
            
        sb.append(FORM_INFO_END);
        sb.append("\n<p>");
        sb.append(FORM_SUBMIT_TO_LOAD);        
        sb.append("</p>");
    }

    private void addValidateForm(StringBuilder sb, ValidationType validationType, String report) throws BridgeDBException{
        sb.append("\n<p>Use this page to validate a ");
        addValidationTypeExplanation(sb, validationType);
        if (report != null){
            addReport(sb, validationType, report);
        }
        addValidateFileLine(sb,  validationType, RDFFormat.TURTLE);
        addValidateFileLine(sb,  validationType, RDFFormat.RDFXML);
        addValidateFileLine(sb,  validationType, RDFFormat.NTRIPLES);
    }

    private void addSaveForm(StringBuilder sb, ValidationType validationType, String report) throws BridgeDBException{
        sb.append("\n<p>Use this page to save (but not load) a ");
        addValidationTypeExplanation(sb, validationType);
        if (report != null){
            addReport(sb, validationType, report);
        }
        addSaveFileLine(sb,  validationType, RDFFormat.TURTLE);
        addSaveFileLine(sb,  validationType, RDFFormat.RDFXML);
        addSaveFileLine(sb,  validationType, RDFFormat.NTRIPLES);
    }
    
    private void addLoadForm(StringBuilder sb, ValidationType validationType, String report) throws BridgeDBException{
        sb.append("\n<p>Use this page to Load a ");
        addValidationTypeExplanation(sb, validationType);
        if (report != null){
            addReport(sb, validationType, report);
        }
        addLoadFileLine(sb,  validationType, RDFFormat.TURTLE);
        addLoadFileLine(sb,  validationType, RDFFormat.RDFXML);
        addLoadFileLine(sb,  validationType, RDFFormat.NTRIPLES);
    }

    private void addValidationTypeExplanation(StringBuilder sb, ValidationType validationType) throws BridgeDBException{
        switch (validationType){
            case VOID: {
                sb.append("VOID descripition.");
                break;
            }
            case LINKS: {
                sb.append("Linkset.");
                break;
            }
            case LINKSMINIMAL: {
                sb.append("linkset you are too lazy to add a full header to.");
                sb.append("<br>WARNING: Using a Minimal void does not excuss you from providing a full header later.");
                break;
            }
            case ANY_RDF: {
                sb.append("Any RDF which will act as a parent for void or linkset.");
                sb.append("<br>WARNING: Using just RDF does not excuss you from providing a full void later.");
                break;
            } default:{
                throw new BridgeDBException("Unexpected validationType" + validationType);
            }
        }
        sb.append(".</p>");       
    }

    private void addValidationFormStart(StringBuilder sb, ValidationType validationType) throws BridgeDBException{
        sb.append("<form method=\"post\" action=\"/");
        sb.append(getServiceName());
        sb.append("/validate");
        sb.append(validationType.getName());
        sb.append("\">");        
    }
    
    private void addSaveFormStart(StringBuilder sb, ValidationType validationType) throws BridgeDBException{
        sb.append("<form method=\"post\" action=\"/");
        sb.append(getServiceName());
        sb.append("/save");
        sb.append(validationType.getName());
        sb.append("\">");        
    }
    
    private void addLoadFormStart(StringBuilder sb, ValidationType validationType) throws BridgeDBException{
        sb.append("<form method=\"post\" action=\"/");
        sb.append(getServiceName());
        sb.append("/load");
        sb.append(validationType.getName());
        sb.append("\">");        
    }

    private void addValidateFileLine(StringBuilder sb, ValidationType validationType, RDFFormat format) throws BridgeDBException{
        addFormStart(sb, validationType, format, "validate");
        sb.append("Select ");
        sb.append(format.getName());
        sb.append(" File to validate as a ");
        sb.append(validationType.getName());
        sb.append("<input type=\"file\" name=\"file\" size=\"45\" />");
        sb.append(FORM_SUBMIT_TO_VALIDATE);   
        sb.append("<br>");
    }
    
    private void addSaveFileLine(StringBuilder sb, ValidationType validationType, RDFFormat format) throws BridgeDBException{
        addFormStart(sb, validationType, format, "save");
        sb.append("Select ");
        sb.append(format.getName());
        sb.append(" File to Save as a ");
        sb.append(validationType.getName());
        sb.append("<input type=\"file\" name=\"file\" size=\"45\" />");
        sb.append(FORM_SUBMIT_TO_SAVE);   
        sb.append("<br>");
    }

    private void addLoadFileLine(StringBuilder sb, ValidationType validationType, RDFFormat format) throws BridgeDBException{
        addFormStart(sb, validationType, format, "load");
        sb.append("Select ");
        sb.append(format.getName());
        sb.append(" File to Load as a ");
        sb.append(validationType.getName());
        sb.append("<input type=\"file\" name=\"file\" size=\"45\" />");
        sb.append(FORM_SUBMIT_TO_LOAD);   
        sb.append("<br>");
    }

    private void addFormStart(StringBuilder sb, ValidationType validationType, RDFFormat format, String action) throws BridgeDBException{
        String formatSt;
        if (format == RDFFormat.TURTLE){
            formatSt = "Turtle";
        } else if (format == RDFFormat.RDFXML){
            formatSt = "RdfXml";
        } else if (format == RDFFormat.NTRIPLES){
            formatSt = "NTriples";
        } else {
            throw new BridgeDBException("Unexpected format" + format);
        }
        sb.append("\n<form method=\"post\" action=\"/");
        sb.append(getServiceName());
        sb.append("/");
        sb.append(action);
        sb.append(formatSt);
        sb.append(validationType.getName());
        sb.append("\" enctype=\"multipart/form-data\">");        
    }
    
    private void addReport(StringBuilder sb, ValidationType validationType, String report){
        int lines = 1;
        for (int i=0; i < report.length(); i++) {
            if (report.charAt(i) == '\n') lines++;
        }
        sb.append("<h2>Report as a ");
        sb.append(validationType.getName());
        sb.append("</h2>");
        sb.append("\n<p><textarea readonly style=\"width:100%;\" rows=");
        sb.append(lines);
        sb.append(">");
        sb.append(report);
        sb.append("</textarea></p>\n");       
    }
    
    private void addValidateString(StringBuilder sb, ValidationType validationType) {
        sb.append("<a href=\"validate");
        sb.append(validationType.getName());
        sb.append("\">Validate String as a ");
        sb.append(validationType.getName());
        sb.append("</a>");        
    }
    
    private void addSaveString(StringBuilder sb, ValidationType validationType) {
        sb.append("<a href=\"save");
        sb.append(validationType.getName());
        sb.append("\">Save String as a ");
        sb.append(validationType.getName());
        sb.append("</a>");        
    }

    private void addLoadString(StringBuilder sb, ValidationType validationType) {
        sb.append("<a href=\"load");
        sb.append(validationType.getName());
        sb.append("\">Load String as a ");
        sb.append(validationType.getName());
        sb.append("</a>");        
    }

    private final String FORM_MINE_TYPE = " \n<p>Mime Type:"
            + "     <select size=\"1\" name=\"mimeType\">"
            + "         <option value=\"application/x-turtle\">Turtle (mimeType=application/x-turtle; ext=ttl)</option>"
            + "         <option value=\"text/plain\">N-Triples (mimeType=text/plain; ext=nt)</option>"
            + "         <option value=\"application/rdf+xml\">RDF/XML (mimeType=application/rdf+xml; ext=rdf, rdfs, owl, xml</option>"
            + " 	</select>"
            + " </p>";
    private final String FORM_INFO_START = "\n<p><textarea rows=\"15\" name=\"info\" style=\"width:100%; background-color: #EEEEFF;\">";
    private final String FORM_INFO_END = "</textarea></p>";
    private final String FORM_SUBMIT_TO_VALIDATE = " <input type=\"submit\" value=\"Validate!\"></input></form>";
    private final String FORM_SUBMIT_TO_LOAD = " <input type=\"submit\" value=\"Load!\"></input></form>";
    private final String FORM_SUBMIT_TO_SAVE = " <input type=\"submit\" value=\"Save (for Admin to Load later!)\"></input></form>";
    private final String FORM_NOTE ="    Note: If the new page does not open click on the address and press enter</p>"
            + "</form>";
    private final String URI_MAPPING_FORM = "<form method=\"get\" action=\"/QueryExpander/mapURI\">"
            + " \n<p>Input URI (URI to be looked up in Identity Mapping Service.)"
            + "     (see <a href=\"/QueryExpander/api#inputURI\">API</a>)</p>"
            + " \n<p><input type=\"text\" name=\"inputURI\" style=\"width:100%\"/></p>"
            + " \n<p>Graph/Context (Graph value to limit the returned URIs)"
            + "     (see <a href=\"/QueryExpander/api#graph\">API</a>)</p>"
            + " \n<p><input type=\"text\" name=\"graph\" style=\"width:100%\"/></p>"
            + " \n<p><input type=\"submit\" value=\"Expand!\"></input> "
            + "    Note: If the new page does not open click on the address and press enter</p>"
            + "</form>";

    //Support function to test the IP Address
    @GET
	@Path("/checkIpAddress")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response checkIpAddress(@Context HttpServletRequest hsr) throws IOException, IDMapperException {
                if (logger.isDebugEnabled()){
                    logger.debug("checkIpAddress called");
                }
                logger.debug("Client IP = " + hsr.getRemoteAddr()); 
       
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInnerPure;
        StringBuilder sbInnerEncoded;

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>test</h1>");
        sb.append("\n<p>IP Address:");
        sb.append(hsr.getRemoteAddr());
        sb.append("</P>");
        String owner = IpConfig.checkIPAddress(hsr.getRemoteAddr());
        if (owner == null){
            sb.append("<h1>Unknown</h1>");
            sb.append("Sorry you are not known to this system.");
            sb.append("<br>You access attempt has been logged.");
            sb.append("<br>Please register your IP address by contacting an Administrator.");
        } else {
            sb.append("<h1>Welcome ");
            sb.append(owner);
            sb.append("</h1>");            
        }
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
	}

    // Test methods
    
    //Code from  http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-jersey/
    @POST
	@Path("/uploadTest")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
        //TODO work out why the FormDataContentDisposition is null
		 @FormDataParam("file") InputStream uploadedInputStream,
         @FormDataParam("file") FormDataContentDisposition fileDetail,
         @Context HttpServletRequest hsr
       ) throws IOException {
                if (logger.isDebugEnabled()){
                    logger.debug("uploadFile called");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                    if (fileDetail == null){
                        logger.debug("fileDetail == null");
                    } else {
                        logger.debug("fileDetail = " + fileDetail);
                    }
                }
      
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInnerPure;
        StringBuilder sbInnerEncoded;

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>test</h1>");
        sb.append("\n<p>File name:");
        sb.append(fileDetail);
        sb.append("\n<p>The IP Address:");
        sb.append(hsr.getRemoteAddr());
        sb.append("</P>");
        
        InputStreamReader reader = new InputStreamReader(uploadedInputStream);
        BufferedReader buffer = new BufferedReader(reader);
        int count = 0;
        while (buffer.ready() && count < 5){
            sb.append("<br>");
            sb.append(buffer.readLine());
            count++;
        }
        sb.append("<br>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
	}

    @POST
	@Path("/uploadTest2")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile2(
        //TODO work out why the FormDataContentDisposition is null
		 @FormDataParam("file") InputStream uploadedInputStream,
         @FormDataParam("file") FormDataContentDisposition fileDetail,
         @FormParam(MIME_TYPE)String mimeType
       ) throws IOException {
 
                if (logger.isDebugEnabled()){
                    logger.debug("uploadFile2 called");
                    if (uploadedInputStream == null){
                        logger.debug("NO uploadedInputStream");
                    } else {
                        try {
                            logger.debug("uploadedInputStream.available = " + uploadedInputStream.available());
                        } catch (IOException ex) {
                            logger.error("unable to get inputStream.available:", ex);
                        }
                    }
                    if (fileDetail == null){
                        logger.debug("fileDetail == null");
                    } else {
                        logger.debug("fileDetail = " + fileDetail);
                    }
                    if (mimeType == null){
                        logger.debug("NO mimeType");
                    } else {
                        logger.debug("mimeType = " + mimeType);
                    }

                }
       
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInnerPure;
        StringBuilder sbInnerEncoded;

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>test</h1>");
        sb.append("\n<p>File name:");
        sb.append(fileDetail);
        sb.append("</P>");
        InputStreamReader reader = new InputStreamReader(uploadedInputStream);
        BufferedReader buffer = new BufferedReader(reader);
        int count = 0;
        while (buffer.ready() && count < 5){
            sb.append("<br>");
            sb.append(buffer.readLine());
            count++;
        }
        sb.append(uploadedInputStream.toString());
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
	}

}


