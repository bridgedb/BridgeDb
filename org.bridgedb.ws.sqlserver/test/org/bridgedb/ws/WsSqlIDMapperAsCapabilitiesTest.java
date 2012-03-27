package org.bridgedb.ws;

import java.net.MalformedURLException;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperCapabilitiesTest;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.IDMapperSQL;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.URLMapperSQL;
import org.bridgedb.sql.URLSqlFactory;
import org.junit.BeforeClass;

/**
 * This test uses WSMapper directly for the Capabilities rather than downloading the whole capabilities xml.
 * @author Christian
 */
public class WsSqlIDMapperAsCapabilitiesTest extends IDMapperCapabilitiesTest{
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException, MalformedURLException{

        SQLAccess sqlAccess = URLSqlFactory.createSQLAccess();;
        IDMapper inner = new URLMapperSQL(sqlAccess);
        WSService wsService = new WSService(inner);
        idMapper = new WSMapper(wsService){
            public IDMapperCapabilities getCapabilities() {
                return this;
            }
        };
    }

}
