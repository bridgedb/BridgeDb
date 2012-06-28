package org.bridgedb.linkset.transative;

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
    
    private Resource ANY_SUBJECT = null;
    
    public TransativeCreator(int leftId, int rightId) throws BridgeDbSqlException, RDFHandlerException{
        leftContext = RdfWrapper.getLinksetURL(leftId);
        rightContext = RdfWrapper.getLinksetURL(rightId);
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
        output.append(":linkset");
            output.append(left);
            output.append("Transitive");
            output.append(right);
            output.append(" a ");
            writeValue(VoidConstants.DATASET);
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
        addCreated();
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
    
    private void addCreated() throws RDFHandlerException {
        try {
            GregorianCalendar c = new GregorianCalendar();
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            output.append("   ");
                writeValue(PavConstants.CREATED_ON);  
                writeValue(new CalendarLiteralImpl(date2));
                output.append(" ; \n");
        } catch (DatatypeConfigurationException ex) {
            RdfWrapper.shutdown(connection);
            throw new RDFHandlerException ("Date conversion exception ", ex);
        }
        output.append("   ");
            writeValue(PavConstants.CREATED_BY);   
            output.append("TransativeCreator ;\n");
    }

    private void registerDataSet(Value dataSet, URI context) throws RDFHandlerException {
        List<Statement> statements = 
                RdfWrapper.getStatementList(connection, dataSet, null, null, context);
        for (Statement statemement:statements){
            writeValue(statemement.getSubject());
            writeValue(statemement.getPredicate());
            writeValue(statemement.getObject());
             output.append(" . \n");
        }
    }

   /* public void createTransative(String SourceLinkset, String TargetLinkset, String outfile) throws BridgeDbSqlException{
        Connection connection = sqlAccess.getConnection();
        StringBuilder query = new StringBuilder("select concat(\"<\", link1.sourceURL,\"> ");
        query.append("<http://www/bridgebd.org/mapsTo> <\", link2.targetURL, \"> .\") ");
        query.append(" from link link1, link link2 ");
        query.append(" where link1.targetURL = link2.sourceURL");
        query.append(" and link1.linkSetId = \"");
        query.append("http://openphacts.cs.man.ac.uk:9090/OPS-IMS/linkset/27/#conceptwiki_swissprot");
        query.append("\" and link2.linkSetId = \"");
        query.append("http://openphacts.cs.man.ac.uk:9090/OPS-IMS/linkset/21/#chembl_uniprot/inverted");
        query.append("\" into outfile \"");
        query.append("/var/local/ims/linksets/cw_chembl-target.ttl");
        query.append("\";");
        Statement statement;
        try {
            statement = connection.createStatement();
        } catch (SQLException ex) {
           throw new BridgeDbSqlException("Unable to get statement. ", ex);
        }
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            return;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDbSqlException("Unable to run query. " + query, ex);
        }

    }*/
    
    public static void main(String[] args) throws BridgeDbSqlException, RDFHandlerException  {
        new TransativeCreator(2,3);
    }

}
