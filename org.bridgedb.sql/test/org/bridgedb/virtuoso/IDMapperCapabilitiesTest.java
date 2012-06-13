/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.virtuoso;

import org.bridgedb.IDMapperException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.TestSqlFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
@Ignore
public class IDMapperCapabilitiesTest extends org.bridgedb.IDMapperCapabilitiesTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        SQLAccess sqlAccess = TestSqlFactory.createTestVirtuosoAccess();
        URLMapperVirtuoso urlMapperVirtuoso = new URLMapperVirtuoso(sqlAccess);
        capabilities = urlMapperVirtuoso;
    }
            
}
