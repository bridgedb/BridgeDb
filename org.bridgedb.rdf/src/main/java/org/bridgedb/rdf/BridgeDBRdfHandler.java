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
package org.bridgedb.rdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.bio.DataSourceComparator;
import org.bridgedb.bio.Organism;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.DCTermsConstants;
import org.bridgedb.rdf.constants.DCatConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.rdf.pairs.RdfBasedCodeMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParserRegistry;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.turtle.TurtleWriter;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 *
 * @author Christian
 */
public class BridgeDBRdfHandler extends RdfBase{
   
    static boolean initialized = false;
    public static final String CONFIG_FILE_NAME = "DataSource.ttl";

    private static final Logger logger = Logger.getLogger(BridgeDBRdfHandler.class);

    private HashMap<Resource, DataSource> dataSourceRegister = new HashMap<Resource, DataSource>();
    private HashMap<Resource, UriPattern> uriPatternRegister = new HashMap<Resource, UriPattern>();

    private BridgeDBRdfHandler(){
        
    }
    
    private void doParseRdfInputStream(InputStream stream) throws BridgeDBException {
        Repository repository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.init();
            repositoryConnection = repository.getConnection();
            repositoryConnection.add(stream, DEFAULT_BASE_URI, DEFAULT_FILE_FORMAT);
            readAllDataSources(repositoryConnection);
            readAllUriPatterns(repositoryConnection);      
        } catch (Exception ex) {
            throw new BridgeDBException ("Error parsing RDF inputStream: " + ex.getMessage(), ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                logger.error("Error closing input Stream", ex);
            }
            shutDown(repository, repositoryConnection);
        }
    }

    private void readAllDataSources(RepositoryConnection repositoryConnection) throws RepositoryException, BridgeDBException {
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, RdfConstants.TYPE_URI, BridgeDBConstants.DATA_SOURCE_URI, true);
                //repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            Resource dataSourceResource = statement.getSubject();
            DataSource dataSource = getDataSource(repositoryConnection, dataSourceResource);
        }
    }
    
    private DataSource getDataSource(RepositoryConnection repositoryConnection, Resource dataSourceResource) 
            throws BridgeDBException, RepositoryException {
        DataSource result = dataSourceRegister.get(dataSourceResource);
        if (result == null){
            result = readDataSource(repositoryConnection, dataSourceResource);
            dataSourceRegister.put(dataSourceResource, result);
        }
        return result;
    }

    public DataSource readDataSource(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        String fullName = getSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.FULL_NAME_URI);
        String systemCode = getSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.SYSTEM_CODE_URI);
        DataSource.Builder builder = DataSource.register(systemCode, fullName);

        String idExample = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.ID_EXAMPLE_URI);
        if (idExample != null){
            builder.idExample(idExample);
        }
        
        String mainUrl = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.MAIN_URL_URI);
        if (mainUrl != null){
            builder.mainUrl(mainUrl);
        }
  
        Value organismId = getPossibleSingleton(repositoryConnection, dataSourceId, BridgeDBConstants.ABOUT_ORGANISM_URI);
        if (organismId != null){
            Object organism = OrganismRdf.byRdfResource(organismId);
            builder.organism(organism);
        }
            
        String primary = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.PRIMARY_URI);
        if (primary != null){
            builder.primary(Boolean.parseBoolean(primary));
        }

        String type = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.TYPE_URI);
        if (type != null){
            builder.type(type);
        }

        Value regexValue = getPossibleSingleton(repositoryConnection, dataSourceId, BridgeDBConstants.HAS_REGEX_PATTERN_URI);
        Pattern regex = null;
        if (regexValue != null){
            regex = Pattern.compile(regexValue.stringValue());
            DataSourcePatterns.registerPattern(builder.asDataSource(), regex);
        } else {
            regex = DataSourcePatterns.getPatterns().get(builder.asDataSource());
        }
        
        Value urlValue = getPossibleSingleton(repositoryConnection, dataSourceId, BridgeDBConstants.HAS_PRIMARY_URI_PATTERN_URI);
        if (urlValue != null){
            UriPattern urlPattern = getUriPattern(repositoryConnection, (Resource)urlValue, 
                    systemCode, UriPatternType.mainUrlPattern);
            builder.urlPattern(urlPattern.getUriPattern());
        }
        
        String urnBase = getPossibleSingletonString(repositoryConnection, dataSourceId, BridgeDBConstants.URN_BASE_URI);
        if (urnBase != null){
            builder.urnBase(urnBase);
        }
        
        Value identifiersOrgSimpleValue = getPossibleSingleton(repositoryConnection, dataSourceId, BridgeDBConstants.HAS_IDENTIFERS_ORG_PATTERN_URI);
        if (identifiersOrgSimpleValue != null){
            UriPattern identifiersOrgSimplePattern = getUriPattern(repositoryConnection, (Resource)identifiersOrgSimpleValue, 
                    systemCode, UriPatternType.identifiersOrgPatternSimple);
            String identifiersOrgInfo = identifiersOrgSimplePattern.getUriPattern().replace("identifiers.org","info.identifiers.org");
            UriPattern identifiersOrgInfoPattern  = UriPattern.register(identifiersOrgInfo, systemCode, UriPatternType.identifiersOrgPatternInfo);
            builder.identifiersOrgBase(identifiersOrgSimplePattern.getUriPattern());
        }
        
        Value identifiersOrgInfoValue = getPossibleSingleton(repositoryConnection, dataSourceId, BridgeDBConstants.HAS_IDENTIFERS_ORG_INFO_PATTERN_URI);
        if (identifiersOrgInfoValue != null){
            UriPattern identifiersOrgInfoPattern = getUriPattern(repositoryConnection, (Resource)identifiersOrgInfoValue, 
                    systemCode, UriPatternType.identifiersOrgPatternInfo);
            String identifiersOrgSimple = identifiersOrgInfoPattern.getUriPattern().replace("info.identifiers.org","identifiers.org");
            UriPattern identifiersOrgSimplePattern  = UriPattern.register(identifiersOrgSimple, systemCode, UriPatternType.identifiersOrgPatternSimple);            
            builder.identifiersOrgBase(identifiersOrgSimplePattern.getUriPattern());
        }

        String alternative = getPossibleSingletonString(repositoryConnection, dataSourceId, DCTermsConstants.ALTERNATIVE_URI);
        if (alternative != null){
            builder.alternative(alternative);
        }
        
        String description = getPossibleSingletonString(repositoryConnection, dataSourceId, DCatConstants.DESCRIPTION_URI);
        if (description != null){
            builder.description(description);
        }

        readUriPatterns(repositoryConnection, dataSourceId, systemCode, UriPatternType.dataSourceUriPattern);
 
        readCodeMapper (repositoryConnection, systemCode, regex);
        
        DataSourceMetaDataProvidor.setProvidor(systemCode, DataSourceMetaDataProvidor.RDF);

        return builder.asDataSource();
    }
    
    private void readCodeMapper(RepositoryConnection repositoryConnection, String systemCode, Pattern regex) throws RepositoryException, BridgeDBException {
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, BridgeDBConstants.SYSTEM_CODE_URI, SimpleValueFactory.getInstance().createLiteral(systemCode), true);
//        String xrefPrefix = null;
        Resource codeMapperReseource = null;
        while (statements.hasNext()) {
            Statement statement = statements.next();
            Resource subject = statement.getSubject();
            String xrefPrefix = getPossibleSingletonString(repositoryConnection, subject, BridgeDBConstants.XREF_PREFIX_URI);
            if (xrefPrefix != null){
                if (regex != null){
                    if (regex.pattern().startsWith(xrefPrefix)){
                        regex = Pattern.compile(regex.pattern().substring(xrefPrefix.length()));
                    } else if (regex.pattern().startsWith("^" + xrefPrefix)){
                        regex = Pattern.compile("^" + regex.pattern().substring(xrefPrefix.length()+1));
                    }

                }
                codeMapperReseource = subject;
                RdfBasedCodeMapper.addXrefPrefix(systemCode, xrefPrefix);
                this.readUriPatterns(repositoryConnection, codeMapperReseource, systemCode, UriPatternType.codeMapperPattern);
            }
        }
    }

    private void readUriPatterns(RepositoryConnection repositoryConnection, Resource subject, String sysCode, 
            UriPatternType patternType)  throws BridgeDBException, RepositoryException {
       RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(subject, BridgeDBConstants.HAS_URI_PATTERN_URI, null, true);
                //repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            Value uriValue = statement.getObject();
            UriPattern uriPattern = getUriPattern(repositoryConnection, (Resource)uriValue, sysCode, patternType);
         }
    }

    private UriPattern getUriPattern(RepositoryConnection repositoryConnection, Resource uriPatternResource, 
            String code, UriPatternType patternType) throws BridgeDBException, RepositoryException {
        UriPattern result = uriPatternRegister.get(uriPatternResource);
        if (result == null){
            result = UriPattern.readUriPattern(repositoryConnection, uriPatternResource, code, patternType);
            uriPatternRegister.put(uriPatternResource, result);
        } else {
        	result.getSysCodes().add(code);
        }
        return result;
    }

    private void readAllUriPatterns(RepositoryConnection repositoryConnection) throws RepositoryException, BridgeDBException {
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, RdfConstants.TYPE_URI, BridgeDBConstants.URI_PATTERN_URI, true);
                //repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            Resource uriPatternResource = statement.getSubject();
            UriPattern uriPattern = uriPatternRegister.get(uriPatternResource);
            if (uriPattern == null){
                throw new BridgeDBException ("Found an unused  "+ BridgeDBConstants.URI_PATTERN_URI + " " + uriPatternResource);
            }
        }
   }
    //Static methods
    
    public static void parseRdfFile(File file) throws BridgeDBException{
        try {
            InputStream inputStream = new FileInputStream(file);
            parseRdfInputStream(inputStream);
        } catch (IOException ex) {
            throw new BridgeDBException ("Error accessing file " + file.getAbsolutePath(), ex);
        }        
    }
    
    public static void parseRdfInputStream(InputStream stream) throws BridgeDBException {
        BridgeDBRdfHandler handler = new BridgeDBRdfHandler();
        handler.doParseRdfInputStream(stream);
        UriPattern.checkRegexPatterns();
    }
    
    public static void main(String[] args) throws RepositoryException, BridgeDBException, IOException, RDFParseException, RDFHandlerException {
        ConfigReader.logToConsole();
        File file1 = new File ("C:\\OpenPhacts\\BridgeDb\\org.bridgedb.rdf\\resources\\DataSource.ttl");
        parseRdfFile(file1);
    }

    public static void init() throws BridgeDBException{
        if (initialized){
            return;
        }
        InputStream stream = ConfigReader.getInputStream(CONFIG_FILE_NAME);
        parseRdfInputStream(stream);
        initialized = true;
        Reporter.println("BridgeDBRdfHandler initialized");
    }
    
    public static void writeRdfToFile(File file) throws BridgeDBException{
        TreeSet<DataSource> sortedDataSources = new TreeSet<DataSource>(new  DataSourceComparator());
        sortedDataSources.addAll(DataSource.getDataSources());
        writeRdfToFile(file, sortedDataSources);
    }
    
    public static void writeRdfToFile(File file, SortedSet<DataSource> dataSources) throws BridgeDBException{
        Reporter.println("Writing DataSource RDF to " + file.getAbsolutePath());
        Repository repository = null;
        RepositoryConnection repositoryConnection = null;
        try {
            repository = new SailRepository(new MemoryStore());
            repository.init();
            repositoryConnection = repository.getConnection();
            for (DataSource dataSource: dataSources){
                writeDataSource(repositoryConnection, dataSource);
            }
            OrganismRdf.addAll(repositoryConnection);
            UriPattern.addAll(repositoryConnection);
            writeRDF(repositoryConnection, file);        
        } catch (Exception ex) {
            throw new BridgeDBException ("Error writing RDF to file:" + ex.getMessage(), ex);
        } finally {
            shutDown(repository, repositoryConnection);
        }
    }
    
    private static void writeDataSource(RepositoryConnection repositoryConnection, DataSource dataSource) throws RepositoryException, BridgeDBException {
        Resource id = asResource(dataSource);
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.DATA_SOURCE_URI);         
        
        if (dataSource.getFullName() != null){
            repositoryConnection.add(id, BridgeDBConstants.FULL_NAME_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.getFullName()));
        }

        if (dataSource.getSystemCode() != null && (!dataSource.getSystemCode().trim().isEmpty())){
            repositoryConnection.add(id, BridgeDBConstants.SYSTEM_CODE_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.getSystemCode()));
        }
        
        if (dataSource.getMainUrl() != null){
            repositoryConnection.add(id, BridgeDBConstants.MAIN_URL_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.getMainUrl()));
        }

        if (dataSource.getExample() != null && dataSource.getExample().getId() != null){
            repositoryConnection.add(id, BridgeDBConstants.ID_EXAMPLE_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.getExample().getId()));
        }
 
        repositoryConnection.add(id, BridgeDBConstants.PRIMARY_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.isPrimary()));
 
        if (dataSource.getType() != null){
            repositoryConnection.add(id, BridgeDBConstants.TYPE_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.getType()));
        } 

        Pattern regex = DataSourcePatterns.getPatterns().get(dataSource);
        String url = dataSource.getKnownUrl("$id");
        UriPattern urlPattern = UriPattern.byPattern(url);
        if (urlPattern != null){
            repositoryConnection.add(id, BridgeDBConstants.HAS_PRIMARY_URI_PATTERN_URI, urlPattern.getResourceId());
        }

        String identifersOrgSimple = dataSource.getIdentifiersOrgUri("$id");
        UriPattern identifersOrgSimplePattern = UriPattern.byPattern(identifersOrgSimple);
        if (identifersOrgSimplePattern != null){
            repositoryConnection.add(id, BridgeDBConstants.HAS_IDENTIFERS_ORG_PATTERN_URI, identifersOrgSimplePattern.getResourceId());
            String identifersOrgInfo = identifersOrgSimple.replace("identifiers.org","info.identifiers.org");
            UriPattern identifersOrgInfoPattern = UriPattern.byPattern(identifersOrgInfo);
            if (identifersOrgInfoPattern != null){
                repositoryConnection.add(id, BridgeDBConstants.HAS_IDENTIFERS_ORG_INFO_PATTERN_URI, identifersOrgInfoPattern.getResourceId());
            }
        }

        if (dataSource.getOrganism() != null){
            Organism organism = (Organism)dataSource.getOrganism();
            repositoryConnection.add(id, BridgeDBConstants.ABOUT_ORGANISM_URI, OrganismRdf.getResourceId(organism));
        }
        
        Pattern pattern = DataSourcePatterns.getPatterns().get(dataSource);
        if (pattern != null && !pattern.toString().isEmpty()){
            Value patternValue = SimpleValueFactory.getInstance().createLiteral(pattern.toString());
            repositoryConnection.add(id, BridgeDBConstants.HAS_REGEX_PATTERN_URI, patternValue);            
        }
        
        if (dataSource.getAlternative() != null){
            repositoryConnection.add(id, DCTermsConstants.ALTERNATIVE_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.getAlternative()));
        } 
        
        if (dataSource.getDescription() != null){
            repositoryConnection.add(id, DCatConstants.DESCRIPTION_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.getDescription()));
        } 
        
       SortedSet<UriPattern> sortedPatterns = UriPattern.byCodeAndType(dataSource.getSystemCode(), UriPatternType.dataSourceUriPattern);
       if (sortedPatterns != null){
            for (UriPattern uriPattern:sortedPatterns){
                repositoryConnection.add(id, BridgeDBConstants.HAS_URI_PATTERN_URI, uriPattern.getResourceId());
            }
        }
       
        writeCodeMapper(repositoryConnection, dataSource);

    }
 
    private static void writeCodeMapper(RepositoryConnection repositoryConnection, DataSource dataSource) throws RepositoryException {
        String xrefPrefix = RdfBasedCodeMapper.getXrefPrefix(dataSource.getSystemCode());
        if (xrefPrefix == null){
            return;
        }
        Resource id = asCodeMapperResource(dataSource);
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.CODE_MAPPER_URI);
        repositoryConnection.add(id, BridgeDBConstants.SYSTEM_CODE_URI, SimpleValueFactory.getInstance().createLiteral(dataSource.getSystemCode()));
        Value prefixValue = SimpleValueFactory.getInstance().createLiteral(xrefPrefix);
        repositoryConnection.add(id, BridgeDBConstants.XREF_PREFIX_URI, prefixValue);            
   
        SortedSet<UriPattern> sortedPatterns = UriPattern.byCodeAndType(dataSource.getSystemCode(), UriPatternType.codeMapperPattern);
        if (sortedPatterns != null){
            for (UriPattern pattern:sortedPatterns){
                repositoryConnection.add(id, BridgeDBConstants.HAS_URI_PATTERN_URI, pattern.getResourceId());
            }
        }
    }
        
    private static void writeRDF(RepositoryConnection repositoryConnection, File file) 
            throws IOException, RDFHandlerException, RepositoryException{
        Writer writer = new FileWriter (file);
        TurtleWriter turtleWriter = new TurtleWriter(writer);
        writeRDF(repositoryConnection, turtleWriter);
        writer.close();
    }
    
    private static void writeRDF(RepositoryConnection repositoryConnection, RDFWriter rdfWriter) 
            throws IOException, RDFHandlerException, RepositoryException{ 
        rdfWriter.handleNamespace(BridgeDBConstants.PREFIX_NAME, BridgeDBConstants.PREFIX);
        rdfWriter.handleNamespace(DCatConstants.PREFIX_NAME, DCatConstants.voidns);
        rdfWriter.handleNamespace(DCTermsConstants.PREFIX_NAME, DCTermsConstants.voidns);
        rdfWriter.handleNamespace("", DEFAULT_BASE_URI);
        rdfWriter.startRDF();
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            rdfWriter.handleStatement(statement);
        }
        rdfWriter.endRDF();
    }
    
    private static RDFFormat getFormat(File file){
        String fileName = file.getName();
        if (fileName.endsWith(".n3")){
            fileName = "try.ttl";
        }
        RDFParserRegistry reg = RDFParserRegistry.getInstance();
        Optional<RDFFormat> fileFormat = reg.getFileFormatForFileName(fileName);
        if (fileFormat == null){
            //added bridgeDB/OPS specific extension here if required.           
            logger.warn("OpenRDF does not know the RDF Format for " + fileName);
            logger.warn("Using the default format " + DEFAULT_FILE_FORMAT);
            return DEFAULT_FILE_FORMAT;
        } else {
            return fileFormat.get();
        }
    }

    protected static Resource asResource(DataSource dataSource) {
        if (dataSource.getFullName() == null){
            return SimpleValueFactory.getInstance().createIRI(BridgeDBConstants.DATA_SOURCE1 + "_bysysCode_" + scrub(dataSource.getSystemCode()));
        } else {
            return SimpleValueFactory.getInstance().createIRI(BridgeDBConstants.DATA_SOURCE1 + "_" + scrub(dataSource.getFullName()));
        }
    }

    protected static Resource asCodeMapperResource(DataSource dataSource) {
        if (dataSource.getFullName() == null){
            return SimpleValueFactory.getInstance().createIRI(BridgeDBConstants.CODE_MAPPER1 + "_bysysCode_" + scrub(dataSource.getSystemCode()));
        } else {
            return SimpleValueFactory.getInstance().createIRI(BridgeDBConstants.CODE_MAPPER1 + "_" + scrub(dataSource.getFullName()));
        }
    }

}
