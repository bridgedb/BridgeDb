/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.util.HashMap;
import org.bridgedb.DataSource;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public enum DataSourceMetaDataProvidor {
    
    BIO, RDF, MIRIAM_ONLY;
    
    private static HashMap<String, DataSourceMetaDataProvidor> register = 
            new HashMap<String, DataSourceMetaDataProvidor>();
    
    public static void assumeUnknownsAreBio(){
        for (DataSource dataSource:DataSource.getDataSources()){
            DataSourceMetaDataProvidor old = register.get(dataSource.getSystemCode());
            if (old == null){
                register.put(dataSource.getSystemCode(), BIO);
            }  
        }
    }

    public static void setProvidor (String sysCode, DataSourceMetaDataProvidor providor){
        DataSourceMetaDataProvidor old = register.get(sysCode);
        if (old!= null && old.compareTo(providor) <= 0){
            return;
        }
        register.put(sysCode, providor);
    }
    
    public static DataSourceMetaDataProvidor getProvider(String sysCode) throws BridgeDBException{
        if (register.containsKey( sysCode)){
            return register.get(sysCode);
        }
        throw new BridgeDBException ("No provider known for " + sysCode);
    }
 
    public static int compare(String sysCode1, String sysCode2) throws BridgeDBException {
        DataSourceMetaDataProvidor provider1 = getProvider(sysCode1);
        DataSourceMetaDataProvidor provider2 = getProvider(sysCode2);
        return provider1.compareTo(provider2);
    }
}
