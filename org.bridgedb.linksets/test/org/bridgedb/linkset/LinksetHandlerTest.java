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
    
    public static void loadMappings() throws IDMapperException, IOException, OpenRDFException{
        report("sample2to1.ttl");
        String[] args1 = {"../org.bridgedb.linksets/test-data/sample1to2.ttl", "testnew"};
        LinksetLoader.main (args1);
        report("sample1to3.ttl");
        String[] args2 = {"../org.bridgedb.linksets/test-data/sample1to3.ttl", "test"};
        LinksetLoader.main (args2);
        report("sample2to3.ttl");
        String[] args3 = {"../org.bridgedb.linksets/test-data/sample2to3.ttl", "testforce"};
        LinksetLoader.main (args3);
	}

}
