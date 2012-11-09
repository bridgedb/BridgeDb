/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public abstract class TestUtils {
    
    static final Logger logger = Logger.getLogger(BridgeDbTestBase.class);

    @BeforeClass
    public static void configureLogger() throws BridgeDBException{
        ConfigReader.configureLogger();
    }
    
    public void report(String message){
        System.out.println(message); 
        logger.info(message);
    }
    
}
