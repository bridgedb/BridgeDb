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
package org.bridgedb.linkset.transative;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.rdf.RdfFactory;
import org.bridgedb.linkset.rdf.RdfWrapper;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.tools.metadata.constants.DctermsConstants;
import org.bridgedb.tools.metadata.constants.DulConstants;
import org.bridgedb.tools.metadata.constants.FoafConstants;
import org.bridgedb.tools.metadata.constants.PavConstants;
import org.bridgedb.url.URLMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
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
 *
 * @author Christian
 */
public class TransativeCreator {
    
    private SQLAccess sqlAccess;
    private URLMapper mapper;
    private URI leftContext;
    private URI rightContext;
    private Resource leftLinkSet;
    private Resource rightLinkSet;
    private Value sourceDataSet;
    private Value targetDataSet;
    private Value sourceUriSpace;
    private Value targetUriSpace;
    private BufferedWriter buffer;
    private File outputFile;
    
    private Resource ANY_SUBJECT = null;
    private static final URI ANY_PREDICATE = null;
    private static final Value ANY_OBJECT = null;
    private static final String LEFT_ID = "leftId";
    private static final String RIGHT_ID = "rightId";
    private static String STORE = "store";
    private static String FILE = "file";
    private static String PREDICATE = "predicate";
    private static String LICENSE = "lisense";
    private static String DERIVED_BY = "derivedBy";
      
    static final Logger logger = Logger.getLogger(TransativeCreator.class);

    private TransativeCreator(int leftId, int rightId, String possibleFileName, StoreType storeType) 
            throws IDMapperException, IOException{
        sqlAccess = SqlFactory.createTheSQLAccess(storeType);
        mapper = new SQLUrlMapper(false, storeType);
        createBufferedWriter(possibleFileName, leftId, rightId);
        leftContext = RdfFactory.getLinksetURL(leftId);
        rightContext = RdfFactory.getLinksetURL(rightId);
    }
            
    public static String createTransative(int leftId, int rightId, String possibleFileName, StoreType storeType, 
            URI predicate, URI license, URI derivedBy) 
            throws RDFHandlerException, IOException, IDMapperException{
        if (license != null && derivedBy == null){
            throw new BridgeDBException("To change the " + LICENSE + " you must declare who you are using the " + 
                    DERIVED_BY + " parameter.");
        }
        TransativeCreator creator = new TransativeCreator(leftId, rightId, possibleFileName, storeType);
        predicate = creator.getVoid(leftId, rightId, storeType, predicate, license, derivedBy);
        creator.getSQL(leftId, rightId, storeType, predicate);
        String message = "Finishing creating transative mapping from " + leftId + " to " + rightId + "\n"
                + "Result saved at " + creator.getOutputPath();
        logger.info(message);
        return message;
    }
 
    private void createBufferedWriter(String possibleFileName, int leftId, int rightId) throws IOException {
        if (possibleFileName == null || possibleFileName.isEmpty()){
            outputFile = new File("linkset " + leftId + "Transitive" + rightId + ".ttl");
        } else {
            outputFile = new File(possibleFileName);
            if (outputFile.isDirectory()){
                outputFile = new File(outputFile, "linkset " + leftId + "Transitive" + rightId + ".ttl");
            }
        }        
        if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()){
            throw new IOException("Unable to create file " + outputFile.getName() + " because the requested directory " 
                    + outputFile.getParent() + " does not yet exist. Please create the directory and try again.");
        }
        FileWriter writer = new FileWriter(outputFile);
        buffer = new BufferedWriter(writer);
        buffer.flush();
    }

    private synchronized URI getVoid(int leftId, int rightId, StoreType storeType, URI predicate, URI license, URI derivedBy) 
            throws RDFHandlerException, IOException, IDMapperException{
        checkMappable(leftId, rightId);
        RdfWrapper rdfWrapper = RdfFactory.setupConnection(storeType);
        leftLinkSet = getLinkSet(rdfWrapper, leftContext);
        rightLinkSet = getLinkSet(rdfWrapper, rightContext);
        //showContext(rightContext);
        sourceDataSet = rdfWrapper.getTheSingeltonObject(leftLinkSet, VoidConstants.SUBJECTSTARGET, leftContext);
        targetDataSet = rdfWrapper.getTheSingeltonObject(rightLinkSet, VoidConstants.OBJECTSTARGET, rightContext);
        
        String linksetURI = ":linkset" + leftId + "Transitive" + rightId;
        
        registerVoIDDescription(linksetURI);
        predicate = registerNewLinkset(rdfWrapper, linksetURI, leftId, rightId, predicate, license, derivedBy);
        registerDataSet(rdfWrapper, sourceDataSet, leftContext);
        registerDataSet(rdfWrapper, targetDataSet, rightContext);
        sourceUriSpace = rdfWrapper.getTheSingeltonObject(sourceDataSet, VoidConstants.URI_SPACE_URI, leftContext);
        targetUriSpace = rdfWrapper.getTheSingeltonObject(targetDataSet, VoidConstants.URI_SPACE_URI, rightContext);

        rdfWrapper.shutdown();
        return predicate;
    }
    
    private Resource getLinkSet(RdfWrapper rdfWrapper, URI context) throws RDFHandlerException{
        return rdfWrapper.getTheSingeltonSubject (RdfConstants.TYPE_URI, VoidConstants.LINKSET, context);
    }
    
    private void checkMappable(int leftId, int rightId) throws IDMapperException{
        MappingSetInfo leftInfo = mapper.getMappingSetInfo(leftId);
        MappingSetInfo rightInfo = mapper.getMappingSetInfo(rightId);
        if (!leftInfo.getTargetSysCode().equals(rightInfo.getSourceSysCode())){
            throw new BridgeDBException ("Target of mappingSet " + leftId  + " is " + leftInfo.getTargetSysCode() 
                + " Which is not the same as the Source of " + rightId + " which is " + rightInfo.getSourceSysCode());
        }
        if (leftInfo.getSourceSysCode().equals(rightInfo.getTargetSysCode())){
            throw new BridgeDBException ("Source of mappingSet " + leftId  + "(" + leftInfo.getTargetSysCode() +")"
                + " is the same as the Target of " + rightId + ". No need for a transative mapping");
        }
    }
    
    private void registerVoIDDescription(String linksetURI) throws RDFHandlerException, IOException {
        buffer.write("@prefix : <#> .");
        buffer.newLine();
        buffer.write("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .");
        buffer.newLine();
        
    	buffer.write("<> ");
    	writeValue(RdfConstants.TYPE_URI);
    	writeValue(VoidConstants.DATASET_DESCRIPTION);
        semicolonNewlineTab();
    	writeValue(DctermsConstants.TITLE);
    	buffer.write("\"Transitive Linkset\"^^xsd:string ;");
        buffer.newLine();
    	buffer.write("\t");
    	writeValue(DctermsConstants.DESCRIPTION);
    	buffer.write("\"Transitive linkset autogenerated by IMS\"^^xsd:string ;");
        buffer.newLine();
    	buffer.write("\t");
    	writeValue(DctermsConstants.CREATOR);
    	buffer.write("<> ;\n\t");
        buffer.newLine();
    	buffer.write("\t");
    	addDate(DctermsConstants.CREATED);
    	writeValue(FoafConstants.PRIMARY_TOPIC);
    	buffer.write(" " + linksetURI + " .\n");
        buffer.newLine();
    }

    private final void semicolonNewlineTab() throws IOException{
    	buffer.write(";");
        buffer.newLine();
    	buffer.write("\t");        
    }
    
    private URI registerNewLinkset(RdfWrapper rdfWrapper, String linksetUri, int left, int right, 
            URI predicate, URI license, URI derivedBy) throws RDFHandlerException, IOException {
        buffer.newLine();
        buffer.write(linksetUri);
        buffer.write(" a ");
        writeValue(VoidConstants.LINKSET);
        semicolonNewlineTab();
        writeValue(VoidConstants.SUBJECTSTARGET);
        writeValue(sourceDataSet);   
        semicolonNewlineTab();
        writeValue(VoidConstants.OBJECTSTARGET);
        writeValue(targetDataSet);   
        semicolonNewlineTab();
        predicate = addPredicate(rdfWrapper, predicate);
        addJustification(rdfWrapper);
        addLicense(rdfWrapper, leftLinkSet, leftContext, license);
        addDrived(derivedBy);
        return predicate;
    }

    private void writeValue(Value value) throws IOException{
        if (value instanceof URI){
            buffer.write("<");
            buffer.write(value.toString());
            buffer.write("> ");
        } else {
            buffer.write(value.toString());
            buffer.write(" ");
        }
    }
    
    private void addLicense(RdfWrapper rdfWrapper, Value linkSet, URI context, Value license) throws RDFHandlerException, IOException{
        if (license == null){
            List<Statement> statements = rdfWrapper.getStatementList(linkSet, DctermsConstants.LICENSE, ANY_OBJECT, context);
            for (Statement statement:statements){
            	writeValue(DctermsConstants.LICENSE);
                writeValue(statement.getObject());   
                semicolonNewlineTab();  
           }        
        } else {
            writeValue(DctermsConstants.LICENSE);
            writeValue(license);   
            semicolonNewlineTab();  
        }
    }
    
    private URI addPredicate(RdfWrapper rdfWrapper, URI newPredicate) throws RDFHandlerException, IOException{
        if (newPredicate == null){
            Value leftPredicate = rdfWrapper.getTheSingeltonObject(leftLinkSet, VoidConstants.LINK_PREDICATE, leftContext);        
            Value rightPredicate = rdfWrapper.getTheSingeltonObject(rightLinkSet, VoidConstants.LINK_PREDICATE, rightContext);  
            newPredicate = PredicateMaker.combine(leftPredicate, rightPredicate);
        }
        writeValue(VoidConstants.LINK_PREDICATE);
        writeValue(newPredicate);   
        semicolonNewlineTab();
        return newPredicate;
    }
    
    private void addJustification(RdfWrapper rdfWrapper) throws IOException, RDFHandlerException {
    	Value leftPredicate = 
    			rdfWrapper.getTheSingeltonObject(leftLinkSet, DulConstants.EXPRESSES, leftContext);
    	Value rightPredicate = 
                rdfWrapper.getTheSingeltonObject(rightLinkSet, DulConstants.EXPRESSES, rightContext);  
        Value justification = 
        		JustificationMaker.combine(leftPredicate, rightPredicate);
        writeValue(DulConstants.EXPRESSES);
        writeValue(justification);   
        semicolonNewlineTab();
    }
    
    private void addDrived(URI derivedBy) throws RDFHandlerException, IOException {
        addDate(PavConstants.DERIVED_ON);
        writeValue(PavConstants.DERIVED_BY);   
        buffer.write("\"TransativeCreator.java\"");
        semicolonNewlineTab();
        writeValue(PavConstants.DERIVED_FROM);
        writeValue(leftLinkSet);   
        semicolonNewlineTab();  
        writeValue(PavConstants.DERIVED_FROM);
        writeValue(rightLinkSet);   
        buffer.write(". ");
        if (derivedBy != null){
            writeValue(PavConstants.DERIVED_BY);
            writeValue(derivedBy);   
            semicolonNewlineTab();  
        }
        buffer.newLine();
    }

	private void addDate(URI predicate) throws RDFHandlerException, IOException {
		try {
            GregorianCalendar c = new GregorianCalendar();
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            writeValue(predicate);  
            writeValue(new CalendarLiteralImpl(date2));
            semicolonNewlineTab();  
        } catch (DatatypeConfigurationException ex) {
//            RdfWrapper.shutdown(rdfWrapper);
            throw new RDFHandlerException ("Date conversion exception ", ex);
        }
	}

    private void registerDataSet(RdfWrapper rdfWrapper, Value dataSet, URI context) throws RDFHandlerException, IOException {
        buffer.newLine();
        List<Statement> statements = rdfWrapper.getStatementList(dataSet, ANY_PREDICATE, ANY_OBJECT, context);
        for (Statement statemement:statements){
            writeValue(statemement.getSubject());
            writeValue(statemement.getPredicate());
            writeValue(statemement.getObject());
            buffer.write(". ");
            buffer.newLine();
        }
    }
       
    private void getSQL(int leftId, int rightId, StoreType storeType, URI newPredicate) throws BridgeDBException, IOException {
        buffer.newLine();
        StringBuilder query = new StringBuilder(
                "SELECT mapping1.sourceId, mapping2.targetId ");
        query.append("FROM mapping as mapping1, mapping as mapping2 ");
        query.append("WHERE mapping1.targetId = mapping2.sourceId ");
        query.append("AND mapping1.mappingSetId = ");
            query.append(leftId);
            query.append(" ");
        query.append("AND mapping2.mappingSetId = ");
            query.append(rightId);
            query.append(" ");
        Connection connection = sqlAccess.getConnection();
        java.sql.Statement statement;
        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
           throw new BridgeDBException("Unable to get statement. ", ex);
        }
        try {
            logger.info("Running " + query.toString());
            ResultSet rs = statement.executeQuery(query.toString());
            logger.info("processing results");
            while (rs.next()){
                String sourceId = rs.getString("mapping1.sourceId");
                String targetId = rs.getString("mapping2.targetId");
                buffer.write("<");
                    buffer.write(sourceUriSpace.stringValue());
                    buffer.write(sourceId);
                buffer.write("> <");
                    buffer.write(newPredicate.stringValue());
                buffer.write( "> <");
                    buffer.write(targetUriSpace.stringValue());
                    buffer.write(targetId);
                buffer.write("> . "); 
                buffer.newLine();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
        buffer.flush();
        buffer.close();
    }
    
    public static void usage(String issue) {
        Reporter.println("Welcome to the OPS Transative Crestor.");
        Reporter.println("This method uses two normal paramter and several  (optional) named (-D) style parameters");
        Reporter.println("Required Parameter (following the jar) are:");
        Reporter.println("Source mapping set Id");
        Reporter.println("   Sources from this set will become the sources in the new set.");
        Reporter.println("Target mapping set Id");
        Reporter.println("   Targets from this set will become the targets in the new set.");
        Reporter.println("Note: The Target Dataset of the first mappingset must be the same as the Source Dataset of the second.");
        Reporter.println("   original linksets could have different UriSpaces, so long as they map to the same DataSet.");
        
        Reporter.println("Optional -D format (before the jar) Parameters are:");
        Reporter.println(STORE);
        Reporter.println("   Dettermines where the data will retreived from. ");
        Reporter.println("   " + StoreType.LIVE + ": Writes into the active database and rdf store");
        Reporter.println("   " + StoreType.LOAD + ": Writes into the secondary database and rdf store");
        Reporter.println("       Note: " + StoreType.LOAD + " defaults to " + StoreType.LIVE + " if not set in the config files");
        Reporter.println("   " + StoreType.TEST + ": Writes into the test database and rdf store");
        Reporter.println("       Note: " + StoreType.TEST + " database and rdf store are erased during junit tests.");
        Reporter.println("   Default is to " + StoreType.LIVE);
        
        Reporter.println(FILE);
        Reporter.println("   Name (ideally with path) of the file to be created.");
        Reporter.println("   WARNING Please make sure it has a \".ttl\" exstention or resulting file may not be readable."); 
        Reporter.println("   Alternatively provide only the directory to save to.");
        Reporter.println("   Default is the leftId + \"Transitive\" + the rightId + \".ttl\".  ");
        Reporter.println("      Which will be placed in the suggested directory if provided. ");
 
        Reporter.println(PREDICATE);
        Reporter.println("   Predicate to used in the new linkset.");
        Reporter.println("   Defaults any predicate shared by both original linksets");
        Reporter.println("       If orignals have different links the least specific one is used. If known");
        Reporter.println("       Otherwise an error is thrown and user will have to provide a " + PREDICATE);
        
        Reporter.println(LICENSE); 
        Reporter.println("   License to be used for the new Linkset.");
        Reporter.println("       Requires the " + DERIVED_BY + " to be used to say who choose this " + LICENSE);
        Reporter.println("   Users if Responsible for the suitablity of the new " + LICENSE);
        Reporter.println("   Default is to include all " + LICENSE + " found in the original linksets");
        
        Reporter.println(DERIVED_BY);
        Reporter.println("   Person who triggered the creation of the linkset.");
        Reporter.println("   Also person who authorized a different " + LICENSE);
        Reporter.println("   No Default value.");
        
        Reporter.println("WARNING:" + PREDICATE + ", " + LICENSE + " and " + DERIVED_BY + " Must be Uris");
        Reporter.println("   As implemented by openrdf URIImpl");
        Reporter.println("   Any leading < and trailing > characters are removed. So are not required but allowed.");
        
        Reporter.println("");
        Reporter.println(issue);
        System.exit(1);
    }

    private static URI getURI(String property) {
        if (property == null){
            return null;
        }
        if (property.isEmpty()){
            return null;
        }
        property = property.trim();
        while (property.startsWith("<")){
            property = property.substring(1);
        }
        while (property.endsWith(">")){
            property = property.substring(0, property.length() - 1);
        }
        try{
            return new URIImpl(property);
        } catch (Exception e){
            e.printStackTrace();
            usage(e.getMessage());
            return null; //never reached
        }
    }

    public static void main(String[] args) throws RDFHandlerException, IOException, IDMapperException {
        ConfigReader.logToConsole();
        if (args.length != 2){
            usage("Please provide the ids of the two mappingsets to combine and any farther -D format arguements.");
        }
        if (args[0] == null || args[0].isEmpty()){
            usage("First Parameter must be the ID of the Source mappingset");
        }
        if (args[1] == null || args[1].isEmpty()){
            usage("Second Parameter must be the ID of the Target mappingset");
        }
        int leftId = -1;
        int rightId = -1;
        try {
            leftId = Integer.parseInt(args[0].trim());
            rightId = Integer.parseInt(args[1].trim());
        } catch (Exception e){
            e.printStackTrace();
            usage(e.getMessage());
        }
        
        String storeString = System.getProperty(STORE);
        StoreType storeType = StoreType.LIVE;
        if (storeString != null && !storeString.isEmpty()){
            try {
                storeType = StoreType.parseString(storeString);
            } catch (IDMapperException ex) {
                ex.printStackTrace();
                usage(ex.getMessage());
            }
        }

        String fileName = System.getProperty(FILE);

        URI predicate = getURI(System.getProperty(PREDICATE));
        URI license = getURI(System.getProperty(LICENSE));
        URI derivedBy = getURI(System.getProperty(DERIVED_BY));
        Reporter.println(createTransative(leftId, rightId, fileName, storeType, predicate, license, derivedBy));
    }

    private String getOutputPath() {
        return outputFile.getAbsolutePath();
    }

}
