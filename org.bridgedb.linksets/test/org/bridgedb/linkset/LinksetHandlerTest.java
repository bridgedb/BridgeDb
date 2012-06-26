/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.mysql.MysqlMapper;
import org.bridgedb.rdf.HoldingRDFStore;
import org.bridgedb.rdf.RdfLoader;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.openrdf.OpenRDFException;

/**
 *
 * @author Christian
 */
public abstract class LinksetHandlerTest extends IDMapperTest {
    
    static URLLinkListener listener;
    private static RdfLoader rdfLoader;
    private static final boolean IS_TEST = true;
    
    private static void parse(String fileName) throws BridgeDbSqlException, IDMapperLinksetException{        
        rdfLoader = new HoldingRDFStore(RdfStoreType.TEST);
        LinksetHandler handler = new LinksetHandler (listener, rdfLoader);
        handler.parse (fileName);
    }
    
    public static void loadMappings() throws IDMapperException, IOException, OpenRDFException{
        rdfLoader = new HoldingRDFStore(RdfStoreType.TEST);
        rdfLoader.clear();
        //ystem.out.println("sample1to2.ttl");
        report("sample1to2.ttl");
        parse ("../org.bridgedb.linksets/test-data/sample1to2.ttl");
        report("sample1to3.ttl");
        parse ("../org.bridgedb.linksets/test-data/sample1to3.ttl");
        report("sample2to3.ttl");
        parse ("../org.bridgedb.linksets/test-data/sample2to3.ttl");
	}

}
