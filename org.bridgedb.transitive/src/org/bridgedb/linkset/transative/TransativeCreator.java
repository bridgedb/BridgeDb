package org.bridgedb.linkset.transative;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.linkset.constants.DctermsConstants;
import org.bridgedb.linkset.constants.PavConstants;
import org.bridgedb.linkset.constants.RdfConstants;
import org.bridgedb.linkset.constants.VoidConstants;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.rdf.RdfWrapper;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SqlFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
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
    StringBuilder output;
    Value newPredicate;
    BufferedWriter buffer;
    
    private Resource ANY_SUBJECT = null;
    
    public TransativeCreator(int leftId, int rightId, String possibleFileName) 
            throws BridgeDbSqlException, RDFHandlerException, IOException{
        if (possibleFileName != null && !possibleFileName.isEmpty()){
            createBufferedWriter(possibleFileName);
        } else {
             createBufferedWriter(leftId, rightId);
        }
        leftContext = RdfWrapper.getLinksetURL(leftId);
        rightContext = RdfWrapper.getLinksetURL(rightId);
        getVoid(leftId, rightId);
        getSQL();
    }
 
    private void createBufferedWriter(String fileName) throws IOException {
        File file = new File(fileName);
        //if (!file.canWrite()){
        //    throw new IOException("Unable to write to " + file.getAbsolutePath());
        //}
        FileWriter writer = new FileWriter(file);
        buffer = new BufferedWriter(writer);
        buffer.flush();
    }

    private void createBufferedWriter(int leftId, int rightId) throws IOException {
        createBufferedWriter ("linkset " + leftId + "Transitive" + rightId + ".ttl");
    }

    private synchronized void getVoid(int leftId, int rightId) throws RDFHandlerException, IOException{
        connection = RdfWrapper.setupConnection(RdfStoreType.MAIN);
        leftLinkSet = getLinkSet(leftContext);
        rightLinkSet = getLinkSet(rightContext);
        //showContext(rightContext);
        checkMiddle();
        sourceDataSet = 
                RdfWrapper.getTheSingeltonObject(connection, leftLinkSet, VoidConstants.SUBJECTSTARGET, leftContext);
        targetDataSet = 
                RdfWrapper.getTheSingeltonObject(connection, rightLinkSet, VoidConstants.OBJECTSTARGET, rightContext);
        
        output = new StringBuilder ("@prefix : <#> .\n");
        registerNewLinkset(leftId, rightId);
        registerDataSet(sourceDataSet, leftContext);
        registerDataSet(targetDataSet, rightContext);
        
        RdfWrapper.shutdown(connection);
        System.out.println(output.toString());
        buffer.write(output.toString());
    }
    
    private void showContext(URI context) throws RDFHandlerException{
        List<Statement> statements = RdfWrapper.getStatementList(connection, null, null, null, context);
        for (Statement statement:statements){
            System.out.println(statement);
        }
    }
    private Resource getLinkSet(URI context) throws RDFHandlerException{
        return RdfWrapper.getTheSingeltonSubject (connection, RdfConstants.TYPE_URI, VoidConstants.LINKSET, context);
    }
    
    private void checkMiddle() throws RDFHandlerException{
        Value leftTarget = 
                RdfWrapper.getTheSingeltonObject(connection, leftLinkSet, VoidConstants.OBJECTSTARGET, leftContext);
        System.out.println (leftTarget);
        Value leftURISpace =
                RdfWrapper.getTheSingeltonObject(connection, leftTarget, VoidConstants.URI_SPACE, leftContext);
        Value rightSubject = 
                RdfWrapper.getTheSingeltonObject(connection, rightLinkSet, VoidConstants.SUBJECTSTARGET, rightContext);
        System.out.println (rightSubject);
        Value rightURISpace =
                RdfWrapper.getTheSingeltonObject(connection, rightSubject, VoidConstants.URI_SPACE, rightContext);
        if (!rightURISpace.equals(leftURISpace)){
            //TODO handle differences here
            RdfWrapper.shutdown(connection);
            throw new RDFHandlerException("Target URISpace " + leftURISpace + " of the left linkset " + leftContext + 
                    " does not match the subject URISpace " + rightURISpace + " of the right linkset " + rightContext);
        }
    }
    
    private void registerNewLinkset(int left, int right) throws RDFHandlerException {
        output.append("\n");
        output.append(":linkset");
            output.append(left);
            output.append("Transitive");
            output.append(right);
            output.append(" a ");
            writeValue(VoidConstants.LINKSET);
            output.append(";\n");
        output.append("   ");   
            writeValue(VoidConstants.SUBJECTSTARGET);
            writeValue(sourceDataSet);   
            output.append("; \n");   
        output.append("   ");   
            writeValue(VoidConstants.OBJECTSTARGET);
            writeValue(targetDataSet);   
            output.append("; \n");  
        addPredicate();
        addLiscence(leftLinkSet, leftContext);
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
    
    private void addLiscence(Value linkSet, URI context) throws RDFHandlerException{
        List<Statement> statements = 
                RdfWrapper.getStatementList(connection, linkSet, DctermsConstants.LICENSE, null, context);
        for (Statement statement:statements){
            output.append("   ");   
                writeValue(DctermsConstants.LICENSE);
                writeValue(statement.getObject());   
                output.append("; \n");   
        }        
    }
    
    private void addPredicate() throws RDFHandlerException{
        Value leftPredicate = 
                RdfWrapper.getTheSingeltonObject(connection, leftLinkSet, VoidConstants.LINK_PREDICATE, leftContext);        
        Value rightPredicate = 
                RdfWrapper.getTheSingeltonObject(connection, rightLinkSet, VoidConstants.LINK_PREDICATE, rightContext);  
        newPredicate = 
                PredicateMaker.combine(leftPredicate, rightPredicate);
        output.append("   ");   
            writeValue(VoidConstants.LINK_PREDICATE);
            writeValue(newPredicate);   
            output.append("; \n");      
    }
    
    private void addDrived() throws RDFHandlerException {
        try {
            GregorianCalendar c = new GregorianCalendar();
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            output.append("   ");
                writeValue(PavConstants.DERIVED_ON);  
                writeValue(new CalendarLiteralImpl(date2));
                output.append(" ; \n");
        } catch (DatatypeConfigurationException ex) {
            RdfWrapper.shutdown(connection);
            throw new RDFHandlerException ("Date conversion exception ", ex);
        }
        output.append("   ");
            writeValue(PavConstants.DERIVED_BY);   
            output.append("\"TransativeCreator\" ;\n");
        output.append("   ");   
            writeValue(PavConstants.DERIVED_FROM);
            writeValue(leftLinkSet);   
            output.append("; \n");   
        output.append("   ");   
            writeValue(PavConstants.DERIVED_FROM);
            writeValue(rightLinkSet);   
            output.append(". \n");   
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

    private void getSQL() throws BridgeDbSqlException, IOException {
        buffer.newLine();
        StringBuilder query = new StringBuilder("SELECT link1.sourceURL, link2.targetURL ");
        query.append("FROM link link1, link link2 ");
        query.append("WHERE link1.targetURL = link2.sourceURL ");
        query.append("AND link1.linkSetId = \"");
            query.append(leftContext);
            query.append("\"");
        query.append("AND link2.linkSetId = \"");
            query.append(rightContext);
            query.append("\"");
        SQLAccess sqlAccess = SqlFactory.createSQLAccess();
        Connection connection = sqlAccess.getConnection();
        java.sql.Statement statement;
        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
           throw new BridgeDbSqlException("Unable to get statement. ", ex);
        }
        try {
            System.out.println("Running " + query.toString());
            ResultSet rs = statement.executeQuery(query.toString());
            System.out.println("processing results");
            while (rs.next()){
                String sourceUrl = rs.getString("sourceUrl");
                String targetUrl = rs.getString("targetUrl");
                buffer.write("<" + sourceUrl + "> <" + newPredicate + "> <" + targetUrl + "> . "); 
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
        System.out.println ("Optional third parameter if the file name to write to.");
        System.out.println ("If no filename provided output file will be linkset(leftid)Transitive(rightid).ttl");
        System.out.println("Please run this again with two paramters");
        System.exit(1);
    }

    public static void main(String[] args) throws BridgeDbSqlException, RDFHandlerException, IOException  {
        if (args.length < 2 || args.length > 3){
            usage();    
        }
        int leftId = Integer.parseInt(args[0]);
        int rightId = Integer.parseInt(args[1]);
        new TransativeCreator(leftId, rightId, args[2]);
    }

}
