package org.bridgedb.ws.client;

import org.bridgedb.IDMapperException;
import org.bridgedb.url.URLMapperTestBase;
import org.bridgedb.ws.WSClientFactory;
import org.bridgedb.ws.WSInterface;
import org.bridgedb.ws.WSMapper;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public class OpsMapperTest  extends org.bridgedb.ops.OpsMapperTest{
    
    @BeforeClass
    public static void setupURLs() throws IDMapperException{
        URLMapperTestBase.setupURLs();
        link1to2 = "http://localhost:8080/OPS-IMS/linkset/1/#Test1_2";
        link1to3 = "http://localhost:8080/OPS-IMS/linkset/2/#Test1_3";
        link2to1 = "http://localhost:8080/OPS-IMS/linkset/3/#Test2_1";
        link2to3 = "http://localhost:8080/OPS-IMS/linkset/4/#Test2_3";
        link3to1 = "http://localhost:8080/OPS-IMS/linkset/5/#Test3_1";
        link3to2 = "http://localhost:8080/OPS-IMS/linkset/6/#Test3_2";
    }

    @BeforeClass
    public static void setupIDMapper() throws IDMapperException {
        WSInterface webService = WSClientFactory.createTestWSClient();
        opsMapper = new WSMapper(webService);
    }

}
