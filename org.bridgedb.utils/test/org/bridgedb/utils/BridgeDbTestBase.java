/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.junit.BeforeClass;

/**
 *
 * @author Christian
 */
public abstract class BridgeDbTestBase {

    static final Logger logger = Logger.getLogger(BridgeDbTestBase.class);
 
    @BeforeClass
    public static void setUplogger() throws Exception {
        ConfigReader.configureLogger();
        Logger.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout(), ConsoleAppender.SYSTEM_OUT));
        logger.setLevel(Level.TRACE);
    }

}
