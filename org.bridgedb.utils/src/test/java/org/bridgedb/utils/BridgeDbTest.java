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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.pairs.SyscodeBasedCodeMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Christian
 */
public class BridgeDbTest {

    static final Logger logger = Logger.getLogger(BridgeDbTest.class);
    public static final int DEFAULT_BUFFER_SIZE = 8192;
 
    @BeforeAll
    public static void setUplogger() throws Exception {
        ConfigReader.logToConsole();
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
    
	@org.junit.jupiter.api.Test
	public void testConfigReader() throws BridgeDBException, IOException {
        ConfigReader.getProperties();
        ConfigReader.getInputStream("BridgeDb.properties");
        ConfigReader.getProperties("BridgeDb.properties");
        ConfigReader.getProperty("Species");
        ConfigReader.useTest();
        assertTrue(ConfigReader.inTestMode());
        
        File localPropertiesFile = new File("local.properties");
        localPropertiesFile.createNewFile();  //creates a new file 
        File bridgeDbPropertiesFile = new File("BridgeDb.properties");
        bridgeDbPropertiesFile.createNewFile();  //creates a new file  
        File log4jFile = new File("log4j.properties");
        log4jFile.createNewFile();  //creates a new file 

        try (InputStream stream = Files.newInputStream(Paths.get("local.properties"))) {
            copyInputStreamToFile(stream, localPropertiesFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try (InputStream stream = Files.newInputStream(Paths.get("BridgeDb.properties"))) {
            copyInputStreamToFile(stream, bridgeDbPropertiesFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try (InputStream stream = Files.newInputStream(Paths.get("log4j.properties"))) {
            copyInputStreamToFile(stream, log4jFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
	}
	
	private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }

    
	@org.junit.jupiter.api.Test
	public void testIdSysCodePair() throws BridgeDBException {
		IdSysCodePair sysCodePair = new IdSysCodePair("id2000", "Dr");
		IdSysCodePair sysCodePair2 = new IdSysCodePair("id2000", "Dr");
		assertEquals("id2000",sysCodePair.getId());
		assertEquals("Dr", sysCodePair.getSysCode());
		sysCodePair.getClass();
		System.out.println(sysCodePair.hashCode());
		sysCodePair.toString();
		sysCodePair.equals(sysCodePair);
		DataSource sourceEG = DataSource.register("dsEntrez Gene", "Entrez Gene").asDataSource();
		DataSource sourceDrugBank = DataSource.register("Dr", "DrugBank").asDataSource();
		Xref source = new Xref("id1000", sourceEG);
		SyscodeBasedCodeMapper syscodeBasedCodeMapper = new SyscodeBasedCodeMapper();
		syscodeBasedCodeMapper.toIdSysCodePair(source);
		assertNotNull(syscodeBasedCodeMapper);
		SyscodeBasedCodeMapper syscodeBasedCodeMapper2 = new SyscodeBasedCodeMapper();
		assertNotNull(syscodeBasedCodeMapper2.toXref(sysCodePair));
	}
	
	@org.junit.jupiter.api.Test
	public void testBridgeDbException() throws BridgeDBException {
		BridgeDBException exc = new BridgeDBException("test message");
		Throwable throwNull = new Throwable("NullPointer",null);
		BridgeDBException excThrow = new BridgeDBException("test message", throwNull);
		assertNotNull(excThrow);
		BridgeDBException excThrow2 = new BridgeDBException("test message", throwNull, "test query");
		assertNotNull(excThrow2);
		IDMapperException idExc = new IDMapperException("IDMapperException");
		BridgeDBException idException = new BridgeDBException(idExc);
		assertNotNull(idException.convertToBridgeDB(idExc));

	}
	
	@org.junit.jupiter.api.Test
	public void testReporter() throws BridgeDBException {
		Reporter report = new Reporter();
		Reporter.println("reporter print");
		Reporter.warn("warning");
		Reporter.error("error");
		Exception ex = new Exception("Reporter Test Exception");
		Reporter.error("error", ex);		
	}

    @Test
    public void testLogErrors(){
        logger.trace("this is a trace");
        logger.info("this in info level");
        logger.warn("Warning");
        logger.error("This is a error");
    }
}
