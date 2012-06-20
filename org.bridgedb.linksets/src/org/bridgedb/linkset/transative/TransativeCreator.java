/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SqlFactory;

/**
 *
 * @author Christian
 */
public class TransativeCreator {
    
    SQLAccess sqlAccess;
    
    public TransativeCreator() throws BridgeDbSqlException{
        sqlAccess = SqlFactory.createSQLAccess();
    }
 
    public void createTransative(String SourceLinkset, String TargetLinkset, String outfile) throws BridgeDbSqlException{
        Connection connection = sqlAccess.getConnection();
        StringBuilder query = new StringBuilder("select concat(\"<\", link1.sourceURL,\"> ");
        query.append("<http://www/bridgebd.org/mapsTo> <\", link2.targetURL, \"> .\") ");
        query.append(" from link link1, link link2 ");
        query.append(" where link1.targetURL = link2.sourceURL");
        query.append(" and link1.provenance_id = \"");
        query.append("http://openphacts.cs.man.ac.uk:9090/OPS-IMS/linkset/27/#conceptwiki_swissprot");
        query.append("\" and link2.provenance_id = \"");
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

    }
    
    public static void main(String[] args)  {
        String SourceLinkset;
        String TargetLinkset;
    }

}
