/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.MetaDataSpecification;

/**
 *
 * @author Christian
 */
public class InputStreamFinder {
    
    public static InputStream findByName(String name, Object caller) throws MetaDataException {
        //Look in call directory
        InputStream test = getInputStreamFromPath(name);
        if (test != null) { 
            return test;
        }
        //Look in config folders
//TODO        
        //Look in resource folder  
        test = getInputStreamFromPath("resources/" + name);
        if (test != null) { 
            return test;
        }
        
        test = getInputStreamFromResource(name, caller);
        if (test != null) { 
            return test;
        }
        test = getInputStreamFromJar(name, caller);
        if (test != null) { 
            return test;
        }
        throw new MetaDataException("Unable to find the metadata.xml file");
    }
    
    private static InputStream getInputStreamFromPath(String filePath){
        File file = new File(filePath);
        if (file.isFile()) {
            try {
                InputStream stream = new FileInputStream(file);
                return stream;
            } catch (FileNotFoundException ex) {
                Reporter.report(ex.toString());
            }
        }
        return null;
    }

    private static InputStream getInputStreamFromResource(String name, Object caller){
        java.net.URL url = caller.getClass().getResource(name);
        if (url != null){
            String fileName = url.getFile();
            return getInputStreamFromPath(fileName);
        }
        return null;
    }

    private static InputStream getInputStreamFromJar(String name, Object caller){
        ZipInputStream zip = null;
        try {
            CodeSource src = caller.getClass().getProtectionDomain().getCodeSource();
            URL jar = src.getLocation();
            zip = new ZipInputStream( jar.openStream());
            ZipEntry ze = null;
            while( ( ze = zip.getNextEntry() ) != null ) {
                if (name.equals(ze.getName())){
                    return zip;
                }
            }
            return null;
        } catch (IOException ex) {
            Logger.getLogger(MetaDataSpecification.class.getName()).log(Level.SEVERE, null, ex);
        }
        //NOTE: Stream must be left OPEN!
        return null;
    }
}
