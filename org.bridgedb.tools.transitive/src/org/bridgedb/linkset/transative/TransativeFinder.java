/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLBase;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.UriMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;

/**
 *
 * @author Christian
 */
public class TransativeFinder extends SQLBase{
    private SQLUriMapper mapper;

    private final static String LAST_TRANSATIVEL_LOADED_KEY = "LastMappingLoadedTransatively";
    
    public TransativeFinder(StoreType storeType) throws BridgeDBException{
        super(storeType);
        mapper = new SQLUriMapper(false, storeType);  
    }
    
    public void UpdateTransative(){
        String lastIdString = mapper.getProperty(LAST_TRANSATIVEL_LOADED_KEY);
        int lastId;
        if (lastIdString == null){
            lastId = 0;
        } else {
            lastId = Integer.parseInt(lastIdString);
        }
        
    }
    
    private void computeTransatives(int lastTranstativeLoaded) throws BridgeDBException{
        int maxMappingSet = getMaxMappingSet();
        for (int i = lastTranstativeLoaded + 1; i <= maxMappingSet; i++){
            computeTransative(i);
        }
    }
   
    private void computeTransative(int mappingSetId) throws BridgeDBException {
        System.out.println ("Do transtaive mappingset " + mappingSetId);
//        lastTranstativeLoaded = mappingSetId;
        MappingSetInfo info = mapper.getMappingSetInfo(mappingSetId);
        Set<Integer> transativeCandidates = findTransativeCandidates(mappingSetId);
        Set<MappingSetInfo> possibleInfos = new HashSet<MappingSetInfo>();
        for (Integer transativeCandidate:transativeCandidates){
            possibleInfos.add(mapper.getMappingSetInfo(transativeCandidate));
        }
        for (MappingSetInfo possibleInfo:possibleInfos) {
            if (checkValidTransative(possibleInfo, info)){
                doTransative(possibleInfo, info);
                for (MappingSetInfo possibleInfo2:possibleInfos) {
                    if (checkValidTransative(info, possibleInfo2)){
                        if (checkValidTransative(possibleInfo, info, possibleInfo2)){
                            doTransative(possibleInfo, info, possibleInfo2);
                        }
                    }
                }
            }
            if (checkValidTransative(info, possibleInfo)){
                doTransative(info, possibleInfo);
            }
         }
    }
 
    private Set<Integer> findTransativeCandidates(int newMappingSetId) throws BridgeDBException {
        Statement statement = mapper.createStatement();
        String query = ("SELECT set1." + SQLUriMapper.ID_COLUMN_NAME 
                + " FROM " + SQLUriMapper.MAPPING_SET_TABLE_NAME + " as set1, "
                    + SQLUriMapper.MAPPING_SET_TABLE_NAME + " as set2"
                + " WHERE set2." + SQLUriMapper.ID_COLUMN_NAME + " = " + newMappingSetId
                + " AND set1."+ SQLUriMapper.ID_COLUMN_NAME + " < " + newMappingSetId
                + " AND set1." + SQLUriMapper.JUSTIFICATION_COLUMN_NAME + " = set2." + SQLUriMapper.JUSTIFICATION_COLUMN_NAME);  
        ResultSet rs;
        try {
            rs = statement.executeQuery(query);   
            Set<Integer> results = new HashSet<Integer>();
            while (rs.next()){
                results.add(rs.getInt(SQLUriMapper.ID_COLUMN_NAME));
            }
            return results;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
    }
    
    private boolean checkValidTransative(MappingSetInfo left, MappingSetInfo right) {
        if (!left.getTargetSysCode().equals(right.getSourceSysCode())){
            System.out.println ("No match " + left.getId() + " -> " + right.getId());
            System.out.println ("    " + left.getTargetSysCode() + " != " + right.getSourceSysCode());
            return false;
        }
        if (left.getSourceSysCode().equals(right.getTargetSysCode())){
            System.out.println ("Loop " + left.getId() + " -> " + right.getId());
            System.out.println ("    " + left.getSourceSysCode() + " == " + right.getTargetSysCode());
            return false;
        }
        return true; 
    }

    private boolean checkValidTransative(MappingSetInfo left, MappingSetInfo middle, MappingSetInfo right) {
         return true;
    }

    private void doTransative(MappingSetInfo left, MappingSetInfo right) {
        System.out.println ("Do transative from " + left + " to " + right);
    }

    private void doTransative(MappingSetInfo left,  MappingSetInfo middle, MappingSetInfo right) {
        System.out.println ("Do transative from " + left + " via " + middle + " to " + right);
    }

    private int getMaxMappingSet() {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
