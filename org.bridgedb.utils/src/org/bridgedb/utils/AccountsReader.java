/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class AccountsReader {
    
    private static HashSet<AccountInfo> infos = null;

    private static final String URI = "uri";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    
    private static void init() throws BridgeDBException{
        if (infos != null){
            return;
        }
        Properties properties = ConfigReader.getProperties();
        infos = new HashSet<AccountInfo>();
        Set<String> names = properties.stringPropertyNames();
        for (String name:names){
            if (!ConfigReader.CONFIG_PROPERTIES.contains(name)){
                String[] parts = name.split("\\.");
                if (parts.length == 2){
                    AccountInfo info = null;
                    for (AccountInfo i:infos){
                        if (i.getPropertyPart1().equals(parts[0])){
                            info = i;
                        }
                    }
                    if (info == null){
                        info = new AccountInfo(parts[0]);
                    }
                    if (parts[1].equals(URI)){
                        info.setUri(properties.getProperty(name));
                    } else if (parts[1].equals(LOGIN)){
                        info.setLogin(properties.getProperty(name));
                    } else if (parts[1].equals(PASSWORD)){
                        info.setPassword(properties.getProperty(name));
                    } else {
                        throw new BridgeDBException ("Unexpected property with a . in it." + name );                    
                    }
                    infos.add(info);
                } else {
                    //do nothing not an account property
                }
            }
        }
    }
    
    public static AccountInfo findByUrl(URL url) throws BridgeDBException {
        return findByUri(url.toString());
    }
    
    public static AccountInfo findByUri(String uri) throws BridgeDBException {
        init();
        AccountInfo result = null;
        for (AccountInfo info:infos){
            if (info.getUri() != null && uri.startsWith(info.getUri())){
                if (result == null){
                    result = info;
                } else {
                    if (result.getUri().length() < info.getUri().length()){
                        result = info;
                    }
                    //else keep the result as it is already more precise.
                }
            }
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception {
        init();
        for (AccountInfo info:infos){
            System.out.println(info);
        }
        System.out.println("   ");
        AccountInfo info = findByUri("https://raw.github.com/openphacts/ops-platform-setup/master/void/drugbank_void.ttl");
        System.out.println(info);        
    }
}
