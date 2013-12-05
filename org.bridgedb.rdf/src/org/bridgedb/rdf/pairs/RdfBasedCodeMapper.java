/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.pairs;

import java.util.HashMap;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.pairs.CodeMapper;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public class RdfBasedCodeMapper implements CodeMapper {

    private static HashMap<String,String> xrefPrefixes = new HashMap<String,String>();
    
    public static void addXrefPrefix(String sysCode, String extension) throws BridgeDBException {
        if (sysCode == null || sysCode.isEmpty()){
            throw new BridgeDBException ("Illegal sysCode:" + sysCode);
        }
        if (extension == null || extension.isEmpty()){
            throw new BridgeDBException ("Extension should not be null or empty. "
                    + "Just don't call this method if no extension is needed." );
        }
        if (xrefPrefixes.containsKey(sysCode)){
            if (!extension.equals(xrefPrefixes.get(sysCode))){
                throw new BridgeDBException ("Illegal attempt to change extension for sysCode:" + sysCode 
                        + " was " + xrefPrefixes.get(sysCode) + " so can not set it to " + sysCode);               
            } 
        } else {
            xrefPrefixes.put(sysCode, extension);
        }
    }

    public static String getXrefPrefix(String sysCode) {
        return xrefPrefixes.get(sysCode);
    }
    
    @Override
    public IdSysCodePair toIdSysCodePair(Xref xref) throws BridgeDBException {
        String sysCode = xref.getDataSource().getSystemCode();
        String id;
        if (xrefPrefixes.containsKey(sysCode)){
            String xrefPrefix = xrefPrefixes.get(sysCode);
            if (xrefPrefix.length() <= 0){
                throw new BridgeDBException ("Xref prefix for " + sysCode + " " + xrefPrefix +  " is to short.");
            }
            try {
                id = xref.getId().substring(xrefPrefix.length());
            } catch (Exception ex){
                throw new BridgeDBException ("Unexpected xrefID " + xref.getId() + " Excepted it to start with " 
                        + xrefPrefix + " with sycCode " + sysCode, ex);
            }
        } else {
            id = xref.getId();
        }
        return new IdSysCodePair(id, sysCode);
    }

    @Override
    public Xref toXref(IdSysCodePair pair) throws BridgeDBException{
        DataSource dataSource = DataSource.getExistingBySystemCode(pair.getSysCode());
        String id;
        if (xrefPrefixes.containsKey(pair.getSysCode())){
            id = xrefPrefixes.get(pair.getSysCode()) + pair.getId();
        } else {
            id = pair.getId();
        }
        return new Xref(id, dataSource);
    }
   
}
