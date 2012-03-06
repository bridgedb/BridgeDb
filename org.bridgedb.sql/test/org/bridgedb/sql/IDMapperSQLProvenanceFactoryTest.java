/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql;

import org.bridgedb.provenance.ProvenanceFactoryTest;
import org.bridgedb.provenance.SimpleProvenanceFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class IDMapperSQLProvenanceFactoryTest extends ProvenanceFactoryTest{
    
    public IDMapperSQLProvenanceFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        factory = new SimpleProvenanceFactory();
    }

}
