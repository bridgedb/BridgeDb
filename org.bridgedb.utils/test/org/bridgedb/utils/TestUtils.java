/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public abstract class TestUtils {
    
    static final Logger logger = Logger.getLogger(TestUtils.class);

    @BeforeClass
    public static void setup() throws IDMapperException{
        ConfigReader.configureLogger();
        DirectoriesConfig.useTestDirectory();
    }
    
    public void report(String message){
        System.out.println(message); 
        logger.info(message);
    }
    
}
