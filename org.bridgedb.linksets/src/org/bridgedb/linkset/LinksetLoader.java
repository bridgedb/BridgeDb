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
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.LinksetVoidInformation;
import org.bridgedb.metadata.MetaDataCollection;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.MetaDataSpecification;
import org.bridgedb.metadata.constants.PavConstants;
import org.bridgedb.metadata.constants.VoidConstants;
import org.bridgedb.metadata.validator.MetaDataSpecificationRegistry;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.metadata.validator.Validator;
import org.bridgedb.mysql.MySQLSpecific;
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
import org.openrdf.rio.RDFHandlerException;

/**
 * Main class for loading linksets.
 *
 * The Main method can parse and either input or validate a linkset.
 *
 * @see usage() for a description of the paramters.
 * @author Christian
 */
public class LinksetLoader {
    
    private static URI THIS_AS_URI = new URIImpl("https://github.com/openphacts/BridgeDb/blob/master/org.bridgedb.linksets/src/org/bridgedb/linkset/LinksetLoader.java");
    private static String LAST_USED_VOID_ID = "LastUsedVoidId";
    private static String STORE = "store";
    private static String CLEAR_EXISING_DATA = "clearExistingData";
    private static String LOAD = "load";
    private int mappingId;
    private boolean symmetric;
    private URI linksetContext;
    private URI inverseContext;
    private Resource linksetResource;
    private Resource inverseResource;
    private LinksetVoidInformation information;
    private final LinksetStatements statements;
    private final URI accessedFrom;
    private final ValidationType validationType;
    private final StoreType storeType;
    
    public LinksetLoader(File file, ValidationType validationType, StoreType storeType) throws IDMapperException {
        Reporter.report("Loading " + file);
        accessedFrom = new URIImpl(file.toURI().toString());
        this.validationType = validationType;
        this.storeType = storeType;
        statements = new LinksetStatementReaderAndImporter(file, storeType);      
        if (validationType.isLinkset()){
            for (Statement st:statements.getVoidStatements()){
                System.out.println(st);
            }
            information = new LinksetVoidInformation(statements, validationType);        
        } else {
            MetaDataSpecification specification = 
                 MetaDataSpecificationRegistry.getMetaDataSpecificationByValidatrionType(validationType);
            MetaDataCollection metaDataCollection= new MetaDataCollection(statements.getVoidStatements(), specification);
            metaDataCollection.validate();
        }
        Reporter.report("Validation successful");          
    }
    
    //private void inputVoid(){
    //    statements = new LinksetStatementReaderAndImporter(file, storeType);      
    //}
    
    //private void inputLinkset(){
    //    statements = new LinksetStatementReaderAndImporter(file, storeType);    
    //}
    
    public synchronized void Load() throws IDMapperException{
        if (validationType.isLinkset()){
            linksetLoad();
        } else {
            voidLoad();
        }
    }
    
    public void linksetLoad() throws IDMapperException{
        if (storeType == null){
            return;
        }
        SQLAccess sqlAccess = SqlFactory.createSQLAccess(storeType);
        URLListener urlListener = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
        getLinksetContexts(information, urlListener);
        statements.resetBaseURI(linksetContext+"/");
        loadVoidHeader();
        loadSQL(statements, urlListener);
        urlListener.closeInput();    
        Reporter.report("Load finished for " + accessedFrom);
    }
    
    private void getLinksetContexts(LinksetVoidInformation information, URLListener urlListener) throws IDMapperException {
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
            System.out.println(oldIDString);
            oldId = Integer.parseInt(oldIDString);
            System.out.println(oldId);
        }
        int id = oldId + 1;
        mapper.putProperty(LAST_USED_VOID_ID, ""+id);
        return RdfFactory.getVoidURL(id);
    }

   /**
     * Loads the linkset into existing data
     * @param file Could be either a File or a directory
     * @param arg "load" to add to the load data, "test" to add to the test data
     *     anything else just validates
     * @throws BridgeDbSqlException
     * @throws IDMapperException
     * @throws FileNotFoundException
     */
    private static void parse(File file, StoreType storeType, ValidationType validationType, boolean load) 
    		throws IDMapperException {
    	if (!file.exists()) {
    		Reporter.report("File not found: " + file.getAbsolutePath());
    		throw new IDMapperLinksetException("File not found: " + file.getAbsolutePath());
    	} else if (file.isDirectory()){
            File[] children = file.listFiles();
            for (File child:children){
                parse(child, storeType, validationType, load);
            }
        } else { 
            LinksetLoader loader = new LinksetLoader(file, validationType, storeType);
            if (load){
                loader.Load();
            }
        }
    }

    public static void clearExistingData (StoreType storeType) 
    		throws IDMapperException  {
        RdfFactory.clear(storeType);
        Reporter.report(storeType + " RDF cleared");
        SQLAccess sqlAccess = SqlFactory.createSQLAccess(storeType);
        URLListener listener = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
        Reporter.report(storeType + " SQL cleared");                
    }

    /**
     * Converts the fileName into a file and then calls parse
     * Should only be called by main() or tests
     *
     * @param fileName name of a file or directory
     * @param arg "load" to add to the load data, "test" to add to the test data
     *     anything else just validates
     * @throws IDMapperException
     * @throws FileNotFoundException
     */
    public static void parse (String fileName, StoreType storeType, ValidationType type, boolean load) throws IDMapperException {
        if (load && storeType == null){
            throw new IDMapperException ("Can not load if no storeType set");
        }
        File file = new File(fileName);
        parse(file, storeType, type, load);
    }

    /**
     * Main entry poit for loading linksets.
     *
     * @seem usage() fr explanation of the paramters.
     *
     * Unpublisted second arguements of "new" and "testnew" will cause the clearing of all existing data.
     * These will also recreate the database and rdf store.
     * USE WITH CAUTION previous data can not be recovered!
     *
     * @param args
     * @throws BridgeDbSqlException
     */
    public static void main(String[] args) throws IDMapperException {
         if (args.length != 1){
            usage("Please specify a file/directory and use -D format for all other arguements.");
        }

        String fileName = args[0];
        if (fileName == null || fileName.isEmpty()){
            usage("Please specify the file or directory top be loaded.");
        }

        String storeString = System.getProperty(STORE);
        StoreType storeType = StoreType.TEST;
        if (storeString != null && !storeString.isEmpty()){
            try {
                storeType = StoreType.parseString(storeString);
            } catch (IDMapperException ex) {
                usage(ex.getMessage());
            }
        }

        String validationString = System.getProperty(Validator.VALIDATION);
        ValidationType validationType = null;
        if (validationString == null || validationString.isEmpty()){
            validationType = ValidationType.LINKS;
        } else {
            try {
                validationType = ValidationType.parseString(validationString);
                if (storeString != null && !validationType.isLinkset()){
                    usage(Validator.VALIDATION + " setting " + validationType + " is not supported for loading.");
                }
            } catch (MetaDataException ex) {
                usage(ex.getMessage());
            }
        }

        String clearExistingDataString = System.getProperty(CLEAR_EXISING_DATA, "false");
        boolean clearExistingData = Boolean.valueOf(clearExistingDataString);
        if (clearExistingData){
            clearExistingData(storeType);
        }

        String loadString = System.getProperty(LOAD, "true");
        boolean load = Boolean.valueOf(loadString);
        
        parse (fileName, storeType, validationType, load);
    }

    public static void usage(String issue) {
        Reporter.report("Welcome to the OPS Linkset Loader.");
        Reporter.report("This method uses a normal paramter and several (optional) named (-D) style parameters");
        Reporter.report("Required Parameter (following the jar) is:");
        Reporter.report("File or Directory to load");
        Reporter.report("   Name (ideally with path) of the file to be loaded.");
        Reporter.report("   Type of file will be dettermined based on the exstension.");
        Reporter.report("   This may also be a directory if all files in it can be loaded. ");
        Reporter.report("      Includes subdirectories with the same requirement of all loadable. ");
        Reporter.report("Optional -D format (before the jar) Parameters are:");
        Reporter.report(STORE);
        Reporter.report("   Dettermines where (if at all) the data will be stored ");
        Reporter.report("   " + StoreType.LIVE + ": Writes into the active database and rdf store");
        Reporter.report("   " + StoreType.LOAD + ": Writes into the secondary database and rdf store");
        Reporter.report("       Note: " + StoreType.LOAD + " defaults to " + StoreType.LIVE + " if not set in the config files");
        Reporter.report("   " + StoreType.TEST + ": Writes into the test database and rdf store");
        Reporter.report("       Note: " + StoreType.TEST + " database and rdf store are erased during junit tests.");
        Reporter.report("   Default is " + StoreType.TEST);
        Reporter.report(Validator.VALIDATION);
        Reporter.report("   " + ValidationType.LINKS + ": Checks that all MUST and SHOULD values are present");
        Reporter.report("       See: http://www.openphacts.org/specs/datadesc/");
        Reporter.report("   " + ValidationType.LINKSMINIMAL + ": requires only the absolute mininal void to load the data");
        Reporter.report("   Default is " + ValidationType.LINKS);
        Reporter.report(CLEAR_EXISING_DATA);
        Reporter.report("   true: clears the exisiting database and rdfstore.");
        Reporter.report("        Only the database and rdf specified by " + STORE + " are cleared.");
        Reporter.report("   default is to append to the existing database and rdf store");
        Reporter.report(issue);
        System.exit(1);
    }

}
