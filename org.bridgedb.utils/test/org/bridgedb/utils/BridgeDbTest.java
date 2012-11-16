/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.net.SMTPAppender;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class BridgeDbTest {

    static final Logger logger = Logger.getLogger(BridgeDbTest.class);
 
    @BeforeClass
    public static void setUplogger() throws Exception {
        //ConfigReader.logToConsole();
    /*    SMTPAppender mailer = new SMTPAppender();
        mailer.setSMTPHost("junk");
        mailer.setSMTPPort(25);
        mailer.setFrom("brenninc@cs.man.ac.uk");
        mailer.setTo("brenninc@cs.man.ac.uk");
        mailer.setBufferSize(1);
        mailer.setLayout(new SimpleLayout());
        logger.addAppender(mailer);
        logger.setLevel(Level.TRACE);*/
    }

    @Test
    public void testLogErrors(){
        logger.trace("this is a trace");
        logger.info("this in info level");
        logger.warn("Warning");
        logger.error("This is a error");
    }
}
