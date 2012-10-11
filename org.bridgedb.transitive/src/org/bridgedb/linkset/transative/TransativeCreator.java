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
import java.util.Set;
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
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.url.URLMapper;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.repository.RepositoryConnection;
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
//    private StringBuilder output;
    private Value newPredicate;
    private BufferedWriter buffer;
    
    private Resource ANY_SUBJECT = null;
    private static final URI ANY_PREDICATE = null;
    private static final Value ANY_OBJECT = null;
    private static final String LEFT_ID = "leftId";
    private static final String RIGHT_ID = "rightId";
    private static String STORE = "store";
    private static String FILE = "file";
      
    private TransativeCreator(int leftId, int rightId, String possibleFileName, StoreType storeType) 
            throws IDMapperException, IOException{
        sqlAccess = SqlFactory.createSQLAccess(storeType);
        mapper = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
        createBufferedWriter(possibleFileName, leftId, rightId);
        leftContext = RdfFactory.getLinksetURL(leftId);
        rightContext = RdfFactory.getLinksetURL(rightId);
    }
            
    public static void createTransative(int leftId, int rightId, String possibleFileName, StoreType storeType) 
            throws RDFHandlerException, IOException, IDMapperException{
        TransativeCreator creator = new TransativeCreator(leftId, rightId, possibleFileName, storeType);
        creator.getVoid(leftId, rightId, storeType);
        creator.getSQL(leftId, rightId, storeType);
    }
 
    private void createBufferedWriter(String fileName) throws IOException {
        File file = new File(fileName);
        //if (!file.canWrite()){
        //    throw new IOException("Unable to write to " + file.getAbsolutePath());
        //}
        if (!file.getParentFile().exists()){
            throw new IOException("Unable to create file " + fileName + " because the requested directory " 
                    + file.getParent() + " does not yet exist. Please create the directory and try again.");
        }
        FileWriter writer = new FileWriter(file);
        buffer = new BufferedWriter(writer);
        buffer.flush();
    }

    private void createBufferedWriter(File file) throws IOException {
        if (file.getParentFile() != null && !file.getParentFile().exists()){
            throw new IOException("Unable to create file " + file.getName() + " because the requested directory " 
                    + file.getParent() + " does not yet exist. Please create the directory and try again.");
        }
        FileWriter writer = new FileWriter(file);
        buffer = new BufferedWriter(writer);
        buffer.flush();
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
        createBufferedWriter (file);
    }

    private void createBufferedWriter(File dir, int leftId, int rightId) throws IOException {
        createBufferedWriter ("linkset " + leftId + "Transitive" + rightId + ".ttl");
    }
    
    private synchronized void getVoid(int leftId, int rightId, StoreType storeType) 
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
        registerNewLinkset(rdfWrapper, linksetURI, leftId, rightId);
        registerDataSet(rdfWrapper, sourceDataSet, leftContext);
        registerDataSet(rdfWrapper, targetDataSet, rightContext);
        sourceUriSpace = rdfWrapper.getTheSingeltonObject(sourceDataSet, VoidConstants.URI_SPACE, leftContext);
        targetUriSpace = rdfWrapper.getTheSingeltonObject(targetDataSet, VoidConstants.URI_SPACE, rightContext);

        rdfWrapper.shutdown();
        //ystem.out.println(output.toString());
        //ystem.out.println(sourceUriSpace);
        //ystem.out.println(targetUriSpace);
    }
    
    //private void showContext(URI context) throws RDFHandlerException{
    //    List<Statement> statements = RdfWrapper.getStatementList(rdfWrapper, null, null, null, context);
    //    for (Statement statement:statements){
    //        Reporter.report(statement.toString());
    //    }
    //}
    
    private Resource getLinkSet(RdfWrapper rdfWrapper, URI context) throws RDFHandlerException{
        return rdfWrapper.getTheSingeltonSubject (RdfConstants.TYPE_URI, VoidConstants.LINKSET, context);
    }
    
    private void checkMappable(int leftId, int rightId) throws IDMapperException{
        MappingSetInfo leftInfo = mapper.getMappingSetInfo(leftId);
        MappingSetInfo rightInfo = mapper.getMappingSetInfo(rightId);
        if (!leftInfo.getTargetSysCode().equals(rightInfo.getSourceSysCode())){
            throw new IDMapperException ("Target of mappingSet " + leftId  + " is " + leftInfo.getTargetSysCode() 
                + " Which is not the same as the Source of " + rightId + " which is " + rightInfo.getSourceSysCode());
        }
        if (leftInfo.getSourceSysCode().equals(rightInfo.getTargetSysCode())){
            throw new IDMapperException ("Source of mappingSet " + leftId  + "(" + leftInfo.getTargetSysCode() +")"
                + " is the same as the Target of " + rightId + ". No need for a transative mapping");
        }
    }
    
    /*private void checkMiddle(RdfWrapper rdfWrapper, String leftDiff, String rightDiff) throws RDFHandlerException{
        Value leftTarget = rdfWrapper.getTheSingeltonObject(leftLinkSet, VoidConstants.OBJECTSTARGET, leftContext);
        //ystem.out.println (leftTarget);
        Value leftURISpace = rdfWrapper.getTheSingeltonObject(leftTarget, VoidConstants.URI_SPACE, leftContext);
        Value rightSubject =  rdfWrapper.getTheSingeltonObject(rightLinkSet, VoidConstants.SUBJECTSTARGET, rightContext);
        //ystem.out.println (rightSubject);
        Value rightURISpace = rdfWrapper.getTheSingeltonObject(rightSubject, VoidConstants.URI_SPACE, rightContext);
        if (rightURISpace.equals(leftURISpace)){
            return; //ok
        }
        if (leftDiff != null || rightDiff != null){
            String newRight = rightURISpace.stringValue().replace(rightDiff, leftDiff);
            if (newRight.equals(leftURISpace.stringValue())){
                return; //ok;
            } else {
                System.err.println(leftDiff);
                System.err.println(rightDiff);
                System.err.println(newRight);
            }
        } 
        //Error
        rdfWrapper.shutdown();
        throw new RDFHandlerException("Target URISpace " + leftURISpace + " of the left linkset " + leftContext + 
                " does not match the subject URISpace " + rightURISpace + " of the right linkset " + rightContext);
    }*/

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
    
    private void registerNewLinkset(RdfWrapper rdfWrapper, String linksetUri, int left, int right) throws RDFHandlerException, IOException {
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
        addPredicate(rdfWrapper);
        addLicense(rdfWrapper, leftLinkSet, leftContext);
        addDrived();
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
    
    private void addLicense(RdfWrapper rdfWrapper, Value linkSet, URI context) throws RDFHandlerException, IOException{
        List<Statement> statements = rdfWrapper.getStatementList(linkSet, DctermsConstants.LICENSE, ANY_OBJECT, context);
        for (Statement statement:statements){
        	writeValue(DctermsConstants.LICENSE);
        	writeValue(statement.getObject());   
   	        semicolonNewlineTab();  
        }        
    }
    
    private void addPredicate(RdfWrapper rdfWrapper) throws RDFHandlerException, IOException{
        Value leftPredicate = rdfWrapper.getTheSingeltonObject(leftLinkSet, VoidConstants.LINK_PREDICATE, leftContext);        
        Value rightPredicate = rdfWrapper.getTheSingeltonObject(rightLinkSet, VoidConstants.LINK_PREDICATE, rightContext);  
        newPredicate = PredicateMaker.combine(leftPredicate, rightPredicate);
        writeValue(VoidConstants.LINK_PREDICATE);
        writeValue(newPredicate);   
        semicolonNewlineTab();
    }
    
    private void addDrived() throws RDFHandlerException, IOException {
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
       
    private void getSQL(int leftId, int rightId, StoreType storeType) throws BridgeDbSqlException, IOException {
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
                //ystem.out.println("<" + sourceUrl + "> <" + newPredicate + "> <" + targetUrl + "> . "); 
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
        Reporter.report(issue);
        int error = 1/0;
        System.exit(1);
    }

    public static void main(String[] args) throws RDFHandlerException, IOException, IDMapperException {
         if (args.length > 0){
            usage("Please use -D format arguements only.");
        }
        String left = System.getProperty(LEFT_ID);
        if (left == null || left.isEmpty()){
            usage("No Parameter " + LEFT_ID + " found");
        }
       String right = System.getProperty(RIGHT_ID);
        if (left == null || left.isEmpty()){
            usage("No Parameter " + RIGHT_ID + " found");
        }
        int leftId = -1;
        int rightId = -1;
        try {
            leftId = Integer.parseInt(left);
            rightId = Integer.parseInt(right);
        } catch (Exception e){
            usage(e.getMessage());
        }
        
        String storeString = System.getProperty(STORE);
        StoreType storeType = null;
        if (storeString != null || !storeString.isEmpty()){
            try {
                storeType = StoreType.parseString(storeString);
            } catch (IDMapperException ex) {
                usage(ex.getMessage());
            }
        }

        String fileName = System.getProperty(FILE);

        createTransative(leftId, rightId, fileName, storeType);
    }

}
