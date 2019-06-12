/* BridgeDb,
 * An abstraction layer for identifier mapping services, both local and online.
 *
 * Copyright 2006-2009  BridgeDb developers
 * Copyright 2012-2013  Christian Y. A. Brenninkmeijer
 * Copyright 2012-2013  OpenPhacts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bridgedb.utils;

import org.apache.log4j.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Christian
 */
public class BridgeDbTest {

    static final Logger logger = Logger.getLogger(BridgeDbTest.class);
 
    @BeforeAll
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
