/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.linkset.LinksetParser;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author Christian
 */
public class TableCreator {
    
   	public static void main(String[] args) throws BridgeDbSqlException {
        SQLAccess sqlAccess = URLSqlFactory.createSQLAccess();
        URLMapperSQL urlMapperSQL = new URLMapperSQL(true,sqlAccess);
    }

}
