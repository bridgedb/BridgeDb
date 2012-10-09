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
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.FoafConstants;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SqlFactory;
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
    
    SQLAccess sqlAccess;
    URI leftContext;
    URI rightContext;
    Resource leftLinkSet;
    Resource rightLinkSet;
    RepositoryConnection connection;
    Value sourceDataSet;
    Value targetDataSet;
    Value sourceUriSpace;
    Value targetUriSpace;
    StringBuilder output;
    Value newPredicate;
    BufferedWriter buffer;
    
    private Resource ANY_SUBJECT = null;
    
    public TransativeCreator(int leftId, int rightId, String diffLeft, String diffRight, String possibleFileName, 
            StoreType storeType) throws BridgeDbSqlException, RDFHandlerException, IOException{
        if (possibleFileName != null && !possibleFileName.isEmpty()){
            createBufferedWriter(possibleFileName);
        } else {
             createBufferedWriter(leftId, rightId);
        }
        leftContext = RdfWrapper.getLinksetURL(leftId);
        rightContext = RdfWrapper.getLinksetURL(rightId);
        getVoid(leftId, rightId, diffLeft, diffRight, storeType);
        getSQL(leftId, rightId, storeType);
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

    private void createBufferedWriter(int leftId, int rightId) throws IOException {
        createBufferedWriter ("linkset " + leftId + "Transitive" + rightId + ".ttl");
    }

    private synchronized void getVoid(int leftId, int rightId, String diffLeft, String diffRight, StoreType storeType) throws RDFHandlerException, IOException{
        connection = RdfWrapper.setupConnection(storeType);
        leftLinkSet = getLinkSet(leftContext);
        rightLinkSet = getLinkSet(rightContext);
        //showContext(rightContext);
        checkMiddle(diffLeft, diffRight);
        sourceDataSet = 
                RdfWrapper.getTheSingeltonObject(connection, leftLinkSet, VoidConstants.SUBJECTSTARGET, leftContext);
        targetDataSet = 
                RdfWrapper.getTheSingeltonObject(connection, rightLinkSet, VoidConstants.OBJECTSTARGET, rightContext);
        
        output = new StringBuilder ("@prefix : <#> .\n");
        output.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n");
        
        String linksetURI = ":linkset" + leftId + "Transitive" + rightId;
        
        registerVoIDDescription(linksetURI);
        registerNewLinkset(linksetURI, leftId, rightId);
        registerDataSet(sourceDataSet, leftContext);
        registerDataSet(targetDataSet, rightContext);
        sourceUriSpace = RdfWrapper.getTheSingeltonObject(connection, sourceDataSet, VoidConstants.URI_SPACE, leftContext);
        targetUriSpace = RdfWrapper.getTheSingeltonObject(connection, targetDataSet, VoidConstants.URI_SPACE, rightContext);

        RdfWrapper.shutdown(connection);
        //ystem.out.println(output.toString());
        //ystem.out.println(sourceUriSpace);
        //ystem.out.println(targetUriSpace);
        buffer.write(output.toString());
    }
    
    private void showContext(URI context) throws RDFHandlerException{
        List<Statement> statements = RdfWrapper.getStatementList(connection, null, null, null, context);
        for (Statement statement:statements){
            Reporter.report(statement.toString());
        }
    }
    private Resource getLinkSet(URI context) throws RDFHandlerException{
        return RdfWrapper.getTheSingeltonSubject (connection, RdfConstants.TYPE_URI, VoidConstants.LINKSET, context);
    }
    
    private void checkMiddle(String leftDiff, String rightDiff) throws RDFHandlerException{
        Value leftTarget = 
                RdfWrapper.getTheSingeltonObject(connection, leftLinkSet, VoidConstants.OBJECTSTARGET, leftContext);
        //ystem.out.println (leftTarget);
        Value leftURISpace =
                RdfWrapper.getTheSingeltonObject(connection, leftTarget, VoidConstants.URI_SPACE, leftContext);
        Value rightSubject = 
                RdfWrapper.getTheSingeltonObject(connection, rightLinkSet, VoidConstants.SUBJECTSTARGET, rightContext);
        //ystem.out.println (rightSubject);
        Value rightURISpace =
                RdfWrapper.getTheSingeltonObject(connection, rightSubject, VoidConstants.URI_SPACE, rightContext);
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
        RdfWrapper.shutdown(connection);
        throw new RDFHandlerException("Target URISpace " + leftURISpace + " of the left linkset " + leftContext + 
                " does not match the subject URISpace " + rightURISpace + " of the right linkset " + rightContext);
    }

    private void registerVoIDDescription(String linksetURI) throws RDFHandlerException {
    	output.append("<> ");
    	writeValue(RdfConstants.TYPE_URI);
    	writeValue(VoidConstants.DATASET);
    	output.append(";\n");
    	output.append("\t");
    	writeValue(DctermsConstants.TITLE);
    	output.append("\"Transitive Linkset\"^^xsd:string ;\n\t");
    	writeValue(DctermsConstants.DESCRIPTION);
    	output.append("\"Transitive linkset autogenerated by IMS\"^^xsd:string ;\n\t");
    	writeValue(DctermsConstants.CREATOR);
    	output.append("<> ;\n\t");
    	addDate(DctermsConstants.CREATED);
    	writeValue(FoafConstants.PRIMARY_TOPIC);
    	output.append(" " + linksetURI + " .\n");
    }
    
    private void registerNewLinkset(String linksetUri, int left, int right) throws RDFHandlerException {
        output.append("\n");
        output.append(linksetUri);
        output.append(" a ");
        writeValue(VoidConstants.LINKSET);
        output.append(";\n\t");
        writeValue(VoidConstants.SUBJECTSTARGET);
        writeValue(sourceDataSet);   
        output.append("; \n\t");   
        writeValue(VoidConstants.OBJECTSTARGET);
        writeValue(targetDataSet);   
        output.append("; \n\t");  
        addPredicate();
        addLicense(leftLinkSet, leftContext);
        addDrived();
    }

    private void writeValue(Value value){
        if (value instanceof URI){
            output.append("<");
            output.append(value);
            output.append("> ");
        } else {
            output.append(value);
            output.append(" ");
        }
    }
    
    private void addLicense(Value linkSet, URI context) throws RDFHandlerException{
        List<Statement> statements = 
                RdfWrapper.getStatementList(connection, linkSet, DctermsConstants.LICENSE, null, context);
        for (Statement statement:statements){
        	writeValue(DctermsConstants.LICENSE);
        	writeValue(statement.getObject());   
        	output.append("; \n\t");   
        }        
    }
    
    private void addPredicate() throws RDFHandlerException{
        Value leftPredicate = 
                RdfWrapper.getTheSingeltonObject(connection, leftLinkSet, VoidConstants.LINK_PREDICATE, leftContext);        
        Value rightPredicate = 
                RdfWrapper.getTheSingeltonObject(connection, rightLinkSet, VoidConstants.LINK_PREDICATE, rightContext);  
        newPredicate = 
        		PredicateMaker.combine(leftPredicate, rightPredicate);
        writeValue(VoidConstants.LINK_PREDICATE);
        writeValue(newPredicate);   
        output.append("; \n\t");      
    }
    
    private void addDrived() throws RDFHandlerException {
        addDate(PavConstants.DERIVED_ON);
        writeValue(PavConstants.DERIVED_BY);   
        output.append("\"TransativeCreator\" ;\n\t");
        writeValue(PavConstants.DERIVED_FROM);
        writeValue(leftLinkSet);   
        output.append("; \n\t");   
        writeValue(PavConstants.DERIVED_FROM);
        writeValue(rightLinkSet);   
        output.append(". \n");   
    }

	private void addDate(URI predicate) throws RDFHandlerException {
		try {
            GregorianCalendar c = new GregorianCalendar();
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            writeValue(predicate);  
            writeValue(new CalendarLiteralImpl(date2));
            output.append(" ; \n\t");
        } catch (DatatypeConfigurationException ex) {
            RdfWrapper.shutdown(connection);
            throw new RDFHandlerException ("Date conversion exception ", ex);
        }
	}

    private void registerDataSet(Value dataSet, URI context) throws RDFHandlerException {
        output.append("\n");
        List<Statement> statements = 
                RdfWrapper.getStatementList(connection, dataSet, null, null, context);
        for (Statement statemement:statements){
            writeValue(statemement.getSubject());
            writeValue(statemement.getPredicate());
            writeValue(statemement.getObject());
            output.append(" . \n");
        }
    }
       
    private void getSQL(int leftId, int rightId, StoreType storeType) throws BridgeDbSqlException, IOException {
        SQLAccess sqlAccess = SqlFactory.createSQLAccess(storeType);
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
    
    private static void usage() {
        System.out.println("Welcome to the OPS Transative Linkset Creator.");
        System.out.println("This methods requires the number of the two linksest being combined.");
        System.out.println("The next parameter should be one of \"load\", \"main\" or \"test\" to idntify which dataset to use.");
        System.out.println("Optional fourth AND fifth parameter describe the different in the middle UriSpaces");
        System.out.println("    Must provide both even if one is blank.");
        System.out.println ("Optional final parameter if the file name to write to.");
        System.out.println ("If no filename provided output file will be linkset(leftid)Transitive(rightid).ttl");
        System.out.println("Please run this again with three to six paramters");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception  {
        if (args.length < 3 || args.length > 6){
            usage();    
        }
        int leftId = Integer.parseInt(args[0]);
        int rightId = Integer.parseInt(args[1]);
        StoreType type = StoreType.parseString(args[2]);
        switch (args.length){
            case 3:
                new TransativeCreator(leftId, rightId, null, null, null, type);
                break;
            case 4:    
                new TransativeCreator(leftId, rightId, null, null, args[3], type);
                break;
            case 5:
                new TransativeCreator(leftId, rightId, args[3], args[4], null, type);            
                break;
            case 6:
                new TransativeCreator(leftId, rightId, args[3], args[4], args[5], type);            
                break;
            default:
                usage();
        }
    }

}
