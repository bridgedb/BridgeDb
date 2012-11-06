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
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.constants.DctermsConstants;
import org.bridgedb.metadata.constants.FoafConstants;
import org.bridgedb.metadata.constants.PavConstants;
import org.bridgedb.metadata.constants.RdfConstants;
import org.bridgedb.metadata.constants.VoidConstants;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.rdf.RdfFactory;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.url.URLMapper;
import org.bridgedb.utils.BridgeDBException;
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
      
    private TransativeCreator(int leftId, int rightId, String possibleFileName, StoreType storeType) 
            throws IDMapperException, IOException{
        sqlAccess = SqlFactory.createTheSQLAccess(storeType);
        mapper = new SQLUrlMapper(false, storeType);
        createBufferedWriter(possibleFileName, leftId, rightId);
        leftContext = RdfFactory.getLinksetURL(leftId);
        rightContext = RdfFactory.getLinksetURL(rightId);
    }
            
    public static void createTransative(int leftId, int rightId, String possibleFileName, StoreType storeType, 
            URI predicate, URI license, URI derivedBy) 
            throws RDFHandlerException, IOException, IDMapperException{
        if (license != null && derivedBy == null){
            throw new BridgeDBException("To change the " + LICENSE + " you must declare who you are using the " + 
                    DERIVED_BY + " parameter.");
        }
        TransativeCreator creator = new TransativeCreator(leftId, rightId, possibleFileName, storeType);
        predicate = creator.getVoid(leftId, rightId, storeType, predicate, license, derivedBy);
        creator.getSQL(leftId, rightId, storeType, predicate);
    }
 
    private void createBufferedWriter(String possibleFileName, int leftId, int rightId) throws IOException {
        File file;
        if (possibleFileName == null || possibleFileName.isEmpty()){
            file = new File("linkset " + leftId + "Transitive" + rightId + ".ttl");
        } else {
            file = new File(possibleFileName);
            if (file.isDirectory()){
                file = new File(file, "linkset " + leftId + "Transitive" + rightId + ".ttl");
            }
        }        
        if (file.getParentFile() != null && !file.getParentFile().exists()){
            throw new IOException("Unable to create file " + file.getName() + " because the requested directory " 
                    + file.getParent() + " does not yet exist. Please create the directory and try again.");
        }
        FileWriter writer = new FileWriter(file);
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
        sourceUriSpace = rdfWrapper.getTheSingeltonObject(sourceDataSet, VoidConstants.URI_SPACE, leftContext);
        targetUriSpace = rdfWrapper.getTheSingeltonObject(targetDataSet, VoidConstants.URI_SPACE, rightContext);

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
       
    private void getSQL(int leftId, int rightId, StoreType storeType, URI newPredicate) throws BridgeDbSqlException, IOException {
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
           throw new BridgeDbSqlException("Unable to get statement. ", ex);
        }
        try {
            Reporter.report("Running " + query.toString());
            ResultSet rs = statement.executeQuery(query.toString());
            Reporter.report("processing results");
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
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }
        buffer.flush();
        buffer.close();
    }
    
    public static void usage(String issue) {
        Reporter.report("Welcome to the OPS Transative Crestor.");
        Reporter.report("This method uses two normal paramter and several  (optional) named (-D) style parameters");
        Reporter.report("Required Parameter (following the jar) are:");
        Reporter.report("Source mapping set Id");
        Reporter.report("   Sources from this set will become the sources in the new set.");
        Reporter.report("Target mapping set Id");
        Reporter.report("   Targets from this set will become the targets in the new set.");
        Reporter.report("Note: The Target Dataset of the first mappingset must be the same as the Source Dataset of the second.");
        Reporter.report("   original linksets could have different UriSpaces, so long as they map to the same DataSet.");
        
        Reporter.report("Optional -D format (before the jar) Parameters are:");
        Reporter.report(STORE);
        Reporter.report("   Dettermines where the data will retreived from. ");
        Reporter.report("   " + StoreType.LIVE + ": Writes into the active database and rdf store");
        Reporter.report("   " + StoreType.LOAD + ": Writes into the secondary database and rdf store");
        Reporter.report("       Note: " + StoreType.LOAD + " defaults to " + StoreType.LIVE + " if not set in the config files");
        Reporter.report("   " + StoreType.TEST + ": Writes into the test database and rdf store");
        Reporter.report("       Note: " + StoreType.TEST + " database and rdf store are erased during junit tests.");
        Reporter.report("   Default is to " + StoreType.LIVE);
        
        Reporter.report(FILE);
        Reporter.report("   Name (ideally with path) of the file to be created.");
        Reporter.report("   WARNING Please make sure it has a \".ttl\" exstention or resulting file may not be readable."); 
        Reporter.report("   Alternatively provide only the directory to save to.");
        Reporter.report("   Default is the leftId + \"Transitive\" + the rightId + \".ttl\".  ");
        Reporter.report("      Which will be placed in the suggested directory if provided. ");
 
        Reporter.report(PREDICATE);
        Reporter.report("   Predicate to used in the new linkset.");
        Reporter.report("   Defaults any predicate shared by both original linksets");
        Reporter.report("       If orignals have different links the least specific one is used. If known");
        Reporter.report("       Otherwise an error is thrown and user will have to provide a " + PREDICATE);
        
        Reporter.report(LICENSE); 
        Reporter.report("   License to be used for the new Linkset.");
        Reporter.report("       Requires the " + DERIVED_BY + " to be used to say who choose this " + LICENSE);
        Reporter.report("   Users if Responsible for the suitablity of the new " + LICENSE);
        Reporter.report("   Default is to include all " + LICENSE + " found in the original linksets");
        
        Reporter.report(DERIVED_BY);
        Reporter.report("   Person who triggered the creation of the linkset.");
        Reporter.report("   Also person who authorized a different " + LICENSE);
        Reporter.report("   No Default value.");
        
        Reporter.report("WARNING:" + PREDICATE + ", " + LICENSE + " and " + DERIVED_BY + " Must be Uris");
        Reporter.report("   As implemented by openrdf URIImpl");
        Reporter.report("   Any leading < and trailing > characters are removed. So are not required but allowed.");
        
        Reporter.report("");
        Reporter.report(issue);
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
        createTransative(leftId, rightId, fileName, storeType, predicate, license, derivedBy);
    }

}
