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
package org.bridgedb.linkset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.LinksetVoidInformation;
import org.bridgedb.metadata.MetaData;
import org.bridgedb.metadata.MetaDataCollection;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.MetaDataSpecification;
import org.bridgedb.metadata.constants.PavConstants;
import org.bridgedb.metadata.constants.VoidConstants;
import org.bridgedb.metadata.validator.MetaDataSpecificationRegistry;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.metadata.validator.Validator;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.rdf.IDMapperLinksetException;
import org.bridgedb.rdf.LinksetStatementReader;
import org.bridgedb.rdf.LinksetStatementReaderAndImporter;
import org.bridgedb.rdf.LinksetStatements;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.SQLIdMapper;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.url.URLListener;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

/**
 * Main class for loading linksets.
 *
 * The Main method can parse and either input or validate a linkset.
 *
 * @see usage() for a description of the paramters.
 * @author Christian
 */
public class LinksetLoaderImplentation{
    
    private static URI THIS_AS_URI = new URIImpl("https://github.com/openphacts/BridgeDb/blob/master/org.bridgedb.linksets/src/org/bridgedb/linkset/LinksetLoader.java");
    private static String LAST_USED_VOID_ID = "LastUsedVoidId";
    
    private final MetaData metaData;
    private final URI accessedFrom;
    private final LinksetStatements statements;
    private final ValidationType validationType;
    private final StoreType storeType;
    
    private int mappingId;
    private boolean symmetric;
    private URI linksetContext;
    private URI inverseContext;
    private Resource linksetResource;
    private Resource inverseResource;
    
    protected LinksetLoaderImplentation(File file, ValidationType validationType, StoreType storeType) throws IDMapperException {
        Reporter.report("Loading " + file);
        accessedFrom = new URIImpl(file.toURI().toString());
        this.validationType = validationType;
        this.storeType = storeType;
        statements = new LinksetStatementReaderAndImporter(file, storeType);     
        if (validationType.isLinkset()){
            metaData = new LinksetVoidInformation(statements, validationType);        
        } else {
            MetaDataSpecification specification = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(validationType);
            metaData = new MetaDataCollection(statements.getVoidStatements(), specification);
        }
    }
    
    protected LinksetLoaderImplentation(String info, RDFFormat format, URI accessedFrom, ValidationType validationType, StoreType storeType) throws IDMapperException {
        Reporter.report("Loading " + info);
        this.accessedFrom = accessedFrom;
        this.validationType = validationType;
        this.storeType = storeType;
        statements = new LinksetStatementReaderAndImporter(info, format, storeType);     
        if (validationType.isLinkset()){
            metaData = new LinksetVoidInformation(statements, validationType);        
        } else {
            MetaDataSpecification specification = 
                MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(validationType);
            metaData = new MetaDataCollection(statements.getVoidStatements(), specification);
        }
    }

    protected String validityReport(boolean includeWarnings){
        return metaData.validityReport(includeWarnings);
    }
    
    protected void validate() throws MetaDataException{
        metaData.validate();
    }
    
    protected synchronized void load() throws IDMapperException{
        if (validationType.isLinkset()){
            linksetLoad();
        } else {
            voidLoad();
        }
    }
    
    private void linksetLoad() throws IDMapperException{
        if (storeType == null){
            return;
        }
        SQLAccess sqlAccess = SqlFactory.createSQLAccess(storeType);
        URLListener urlListener = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
        getLinksetContexts(urlListener);
        resetBaseURI();
        loadVoidHeader();
        loadSQL(statements, urlListener);
        urlListener.closeInput();    
        Reporter.report("Load finished for " + accessedFrom);
    }
    
    private void getLinksetContexts(URLListener urlListener) throws IDMapperException {
        LinksetVoidInformation information = (LinksetVoidInformation)metaData;
        String subjectUriSpace = information.getSubjectUriSpace();
        String targetUriSpace = information.getTargetUriSpace();
        String predicate = information.getPredicate();
        //TODO work out way to do this
        symmetric = true;
        boolean transative = information.isTransative();
        mappingId = urlListener.registerMappingSet(subjectUriSpace, predicate, targetUriSpace, symmetric, transative);   
        linksetContext = RdfFactory.getLinksetURL(mappingId);
        linksetResource = information.getLinksetResource();
        if (symmetric) {
            inverseContext = RdfFactory.getLinksetURL(mappingId + 1);             
            inverseResource = invertResource(linksetResource);
        } else {
            inverseContext = null;
            inverseResource = null;
        }
    }
   
    private Resource invertResource(Resource resource){
        if (resource instanceof URI){
            return new URIImpl(resource.toString()+"_Symmetric");
        }
        return resource;
    }
    
    private void resetBaseURI() {
        statements.resetBaseURI(linksetContext+"/");
        linksetResource = LinksetStatementReader.resetBaseURI(linksetContext+"/", linksetResource);
        if (symmetric) {
            inverseResource = invertResource(linksetResource);
        }
    }
    
    private void loadVoidHeader() 
            throws IDMapperException{
        RdfWrapper rdfWrapper = null;
        try {
            rdfWrapper = RdfFactory.setupConnection(storeType);
            for (Statement st:statements.getVoidStatements()){
                rdfWrapper.add(st.getSubject(), st.getPredicate(), st.getObject(), linksetContext);
                addInverse(rdfWrapper, st);
            }
            rdfWrapper.add(linksetResource, PavConstants.SOURCE_ACCESSED_FROM, accessedFrom, linksetContext);
            if (inverseContext != null){
                addInverse(rdfWrapper, linksetResource, PavConstants.SOURCE_ACCESSED_FROM, accessedFrom);
            }
            GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
            try {
                XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
                CalendarLiteralImpl now = new CalendarLiteralImpl(xgcal);
                rdfWrapper.add(linksetResource, PavConstants.SOURCE_ACCESSED_ON, now, linksetContext);
                addInverse(rdfWrapper, linksetResource, PavConstants.SOURCE_ACCESSED_ON, now);
            } catch (DatatypeConfigurationException ex) {
                //Should never happen so basically ignore
                ex.printStackTrace();
            }
            rdfWrapper.add(linksetResource, PavConstants.SOURCE_ACCESSED_BY, THIS_AS_URI, linksetContext);
            addInverse(rdfWrapper, linksetResource, PavConstants.SOURCE_ACCESSED_BY, THIS_AS_URI);
            addInverse(rdfWrapper, inverseResource, PavConstants.DERIVED_FROM, linksetResource);
        } catch (RDFHandlerException ex) {
            throw new IDMapperException("Error loading RDF " + ex);
        } finally {
            try {
                if (rdfWrapper != null){
                    rdfWrapper.shutdown();
                }
            } catch (RDFHandlerException ex) {
                throw new IDMapperException("Error loading RDF " + ex);
            }
        }
    }

    private void addInverse(RdfWrapper rdfWrapper, Statement statement) throws RDFHandlerException{
        addInverse(rdfWrapper, statement.getSubject(), statement.getPredicate(), statement.getObject());
    }
    
    private void addInverse(RdfWrapper rdfWrapper, Resource subject, URI predicate, Value object) 
            throws RDFHandlerException{
        if (inverseContext != null){
            if (subject.equals(linksetResource)){
                if (predicate.equals(VoidConstants.SUBJECTSTARGET)){
                    rdfWrapper.add(inverseResource, VoidConstants.OBJECTSTARGET, object, inverseContext); 
                } else if (predicate.equals(VoidConstants.OBJECTSTARGET)){
                    rdfWrapper.add(inverseResource, VoidConstants.SUBJECTSTARGET, object, inverseContext); 
                } else {
                    rdfWrapper.add(inverseResource, predicate, object, inverseContext);                
                }
            } else {
                rdfWrapper.add(subject, predicate, object, inverseContext);
            }
        }
    }

    private void loadSQL(LinksetStatements statements, URLListener urlListener) throws IDMapperException {
        for (Statement st:statements.getLinkStatements()){
            String sourceURL = st.getSubject().stringValue();
            String targetURL = st.getObject().stringValue();
            urlListener.insertURLMapping(sourceURL, targetURL, mappingId, symmetric);
        }
    }
        
    private void voidLoad() throws IDMapperException {
        RdfWrapper rdfWrapper = null;
        try {
            URI voidContext = getVoidContext(rdfWrapper);
            rdfWrapper = RdfFactory.setupConnection(storeType);
            for (Statement st:statements.getVoidStatements()){
                rdfWrapper.add(st.getSubject(), st.getPredicate(), st.getObject(), voidContext);
            }
            rdfWrapper.add(voidContext, PavConstants.SOURCE_ACCESSED_FROM, accessedFrom, voidContext);
            GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance();
            try {
                XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
                CalendarLiteralImpl now = new CalendarLiteralImpl(xgcal);
                rdfWrapper.add(voidContext, PavConstants.SOURCE_ACCESSED_ON, now, voidContext);
            } catch (DatatypeConfigurationException ex) {
                //Should never happen so basically ignore
                ex.printStackTrace();
            }
            rdfWrapper.add(voidContext, PavConstants.SOURCE_ACCESSED_BY, THIS_AS_URI, voidContext);
        } catch (RDFHandlerException ex) {
            throw new IDMapperException("Error loading RDF " + ex);
        } finally {
            try {
                if (rdfWrapper != null){
                    rdfWrapper.shutdown();
                }
            } catch (RDFHandlerException ex) {
                throw new IDMapperException("Error loading RDF " + ex);
            }
        }
    }

    private synchronized URI getVoidContext(RdfWrapper rdfWrapper) throws BridgeDbSqlException {
        SQLAccess sqlAccess = SqlFactory.createSQLAccess(storeType);
        SQLIdMapper mapper = new SQLIdMapper(false, sqlAccess, new MySQLSpecific());
        String oldIDString = mapper.getProperty(LAST_USED_VOID_ID);
        Integer oldId;
        if (oldIDString == null){
            oldId = 0;
        } else {
            oldId = Integer.parseInt(oldIDString);
        }
        int id = oldId + 1;
        mapper.putProperty(LAST_USED_VOID_ID, ""+id);
        return RdfFactory.getVoidURL(id);
    }

}
