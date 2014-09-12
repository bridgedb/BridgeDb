/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri.loader.transative;

import org.bridgedb.sql.transative.PredicateMaker;
import org.bridgedb.sql.transative.JustificationMaker;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.DataSetInfo;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.turtle.TurtleWriter;

/**
 *
 * @author Christian
 */
public class TransativeCreator {

    private static SQLAccess sqlAccess = null;
    private final UriMapper mapper;
    protected final MappingSetInfo leftInfo;
    protected final MappingSetInfo rightInfo;
    protected final URI predicate;
    protected final String justification;
    private final RegexUriPattern sourceRegexUriPattern;
    private final RegexUriPattern targetRegexUriPattern;
    private final boolean reflexive;
    
    private static URI GENERATE_PREDICATE = null;

    static final Logger logger = Logger.getLogger(TransativeCreator.class);
    
    public static File doTransativeIfPossible(MappingSetInfo left, MappingSetInfo right) throws BridgeDBException, IOException {
        TransativeCreator creator = new TransativeCreator(left, right);
        return creator.generateOutputFileIfPossible();
    }

    public static File doTransativeIfPossible(int leftId, int rightId) 
            throws BridgeDBException, IOException {
        SQLUriMapper mapper = SQLUriMapper.getExisting();
        MappingSetInfo left = mapper.getMappingSetInfo(leftId);
        MappingSetInfo right = mapper.getMappingSetInfo(rightId);
        return doTransativeIfPossible(left, right);
    }
    
    protected TransativeCreator(MappingSetInfo left, MappingSetInfo right) 
            throws BridgeDBException, IOException{
        if (sqlAccess == null){
            sqlAccess = SqlFactory.createTheSQLAccess();
        }
        mapper = SQLUriMapper.getExisting();
        leftInfo = left;
        rightInfo = right;
        predicate = new URIImpl(PredicateMaker.combine(left.getPredicate(), right.getPredicate()));
        justification = JustificationMaker.combine(left.getJustification(), right.getJustification());
        reflexive = left.getSource().getSysCode().equals(right.getTarget().getSysCode());
        UriPattern sourceUriPattern = getUriPattern(left.getSource());
        sourceRegexUriPattern = RegexUriPattern.factory(sourceUriPattern, left.getSource().getSysCode());
        //ystem.out.println(sourceRegexUriPattern);
        UriPattern targetUriPattern = getUriPattern(right.getTarget());
        targetRegexUriPattern = RegexUriPattern.factory(targetUriPattern, right.getTarget().getSysCode());
        //ystem.out.println(targetRegexUriPattern);
        //ystem.out.println(targetRegexUriPattern.getUri("1234"));
        checkTransativeLegal(left, right);
    }
    
    public File generateOutputFileIfPossible() throws BridgeDBException, IOException{
        try {
            File parent = TransativeConfig.getTransativeDirectory();
            File outputFile = new File(parent, getid());
            Reporter.println("Writing transative to " + outputFile.getAbsolutePath());
            FileWriter writer = new FileWriter(outputFile);
            RDFWriter rdfWriter = new TurtleWriter(writer);
            rdfWriter.startRDF();
            writeHeader(rdfWriter);
            boolean result = getSQL(rdfWriter);
            rdfWriter.endRDF();
            writer.flush();
            writer.close();
            if (result){
                return outputFile;
            } else {
                return null;
            }
        } catch (RDFHandlerException ex) {
            throw new BridgeDBException("Error writing to RDF ", ex);
        }
    }

    protected String getid(){
        return "Transitive" + leftInfo.getStringId() + "and" + rightInfo.getStringId() + ".ttl";
    }
    
    private boolean getSQL(RDFWriter rdfwriter) throws BridgeDBException, IOException, RDFHandlerException {
        boolean found = false;
         StringBuilder query = new StringBuilder(
                "SELECT mapping1.sourceId, mapping2.targetId ");
        query.append("FROM mapping as mapping1, mapping as mapping2 ");
        query.append("WHERE mapping1.targetId = mapping2.sourceId ");
        query.append("AND mapping1.mappingSetId = ");
            query.append(leftInfo.getIntId());
            query.append(" ");
        query.append("AND mapping2.mappingSetId = ");
            query.append(rightInfo.getIntId());
            query.append(" ");
        Connection connection = sqlAccess.getConnection();
        java.sql.Statement statement;
        try {
            statement = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
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
                if (reflexive && sourceId.equals(targetId)){
                    //do nothing as same uri;
                } else {
                    String sourceUri = sourceRegexUriPattern.getUri(sourceId);
                    URI sourceURI = new URIImpl(sourceUri);
                    String targetUri = targetRegexUriPattern.getUri(targetId);
                    URI targetURI = new URIImpl(targetUri);
                    Statement statment = new StatementImpl(sourceURI, predicate, targetURI);
                    rdfwriter.handleStatement(statment);
                    found = true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
        try {
            connection.close();
        } catch (SQLException ex) {
            throw new BridgeDBException("Error closing MYSQL connection", ex);
        }
        return found;
    }
    
    private UriPattern getUriPattern(DataSetInfo info) throws BridgeDBException {
        DataSource dataSource = info.getDataSource();
        String pattern = dataSource.getIdentifiersOrgUri("$id");
        if (pattern == null){
            pattern = dataSource.getKnownUrl("$id");
        }
        return UriPattern.existingByPattern(pattern);
    }

    private void checkTransativeLegal(MappingSetInfo left, MappingSetInfo right) throws BridgeDBException {
        if (!left.getTarget().equals(right.getSource())){
            throw new BridgeDBException("Left target " + left.getTarget() + " does not match right source " + right.getSource());
        }
    }

    /**
     * Empty method to allow subclasses to write headers
     * @param buffer 
     */
    protected void writeHeader(RDFWriter writer) throws BridgeDBException, RDFHandlerException {
        //Do nothing here
    }

    
}
