/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset;

import java.io.IOException;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Reporter;
import org.bridgedb.mysql.MysqlMapper;
import org.bridgedb.rdf.RdfStoreType;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SqlFactory;
import org.openrdf.OpenRDFException;

/**
 * WARNING overwrites the live database
 * @author Christian
 */
public class LinksetMainLoader {
    
    protected static URLLinkListener listener;
    private static final boolean IS_TEST = true;
    
    public static void main(String[] args) throws IDMapperException, IOException, OpenRDFException  {
        setup();
        SQLAccess sqlAccess = SqlFactory.createSQLAccess();
        MysqlMapper urlMapperSQL = new MysqlMapper(true, sqlAccess);
        //URLMapperLinkset mapper = new URLMapperLinkset(); 
        listener = urlMapperSQL;
        Reporter.report("sample2to1.ttl");
        LinksetHandler.clearAndParse (listener, "../org.bridgedb.linksets/test-data/sample1to2.ttl", RdfStoreType.MAIN);
        Reporter.report("sample1to3.ttl");
        LinksetHandler.parse (listener, "../org.bridgedb.linksets/test-data/sample1to3.ttl", RdfStoreType.MAIN);
        Reporter.report("sample2to3.ttl");
        LinksetHandler.parse (listener, "../org.bridgedb.linksets/test-data/sample2to3.ttl", RdfStoreType.MAIN);
	}

    //copied from IDMapperTestBase
    private static void setup() throws IDMapperException{
        String goodId1 = "123";
        DataSource DataSource1 = DataSource.register("TestDS1", "TestDS1"). urlPattern("http://www.foo.com/$id")
                .idExample(goodId1).asDataSource();
        DataSource DataSource2 = DataSource.register("TestDS2", "TestDS2").urlPattern("http://www.example.com/$id")
                .idExample(goodId1).asDataSource();
        DataSource DataSource3 = DataSource.register("TestDS3", "TestDS3").nameSpace("http://www.example.org#")
                .idExample(goodId1).asDataSource();
        //This DataSource MUST not be supported
        DataSource DataSourceBad = DataSource.register("TestDSBad", "TestDSBad")
                .nameSpace("www.NotInTheURlMapper.com#").asDataSource();
    }
}
