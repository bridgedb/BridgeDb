/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import java.util.Date;
import org.bridgedb.IDMapperException;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * 
 * @author Christian
 */
public class ProvenanceMapperTest extends org.bridgedb.provenance.ProvenanceMapperTest {
    
    @BeforeClass
    public static void setupIDMapper() throws IDMapperException{
        connectionOk = false;
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        connectionOk = true;
        URLMapperSQL urlMapperSQL = new URLMapperSQL(sqlAccess);
        provenaceMapper = urlMapperSQL;
    }
            
}
