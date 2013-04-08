/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.linkset.transative;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.sql.SQLBase;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class TransativeFinder extends SQLBase{
    private SQLUriMapper mapper;
    private final StoreType storeType;
    private final LinksetLoader linksetLoader;
    private HashMap<Integer,Integer> newTransatives = new HashMap<Integer,Integer>();
    
    private final static String LAST_TRANSATIVEL_LOADED_KEY = "LastMappingLoadedTransatively";
    
    public TransativeFinder(StoreType storeType) throws BridgeDBException{
        super(storeType);
        this.storeType = storeType;
        mapper = SQLUriMapper.factory(false, storeType);  
        linksetLoader = new LinksetLoader();
    }
    
    public void UpdateTransative() throws BridgeDBException, RDFHandlerException, IOException{
        System.out.println("start update");
        String lastIdString = mapper.getProperty(LAST_TRANSATIVEL_LOADED_KEY);
        int lastId;
        if (lastIdString == null){
            lastId = 0;
        } else {
            lastId = Integer.parseInt(lastIdString);
        }
        computeTransatives(lastId);
    }
    
    private void computeTransatives(int lastTranstativeLoaded) throws BridgeDBException, RDFHandlerException, IOException{
        int maxMappingSet = getMaxMappingSet();
        if (maxMappingSet <= lastTranstativeLoaded){
            return;
        }
        for (int i = lastTranstativeLoaded + 1; i <= maxMappingSet; i++){
            MappingSetInfo info = mapper.getMappingSetInfo(i);
            if (!info.isSymmetric()){
                if (!info.isTransitive()){
                    computeTransative(info, info.getIntId());
                }
            }
        }
        HashMap<Integer,Integer> oldTransatives = (HashMap<Integer,Integer>)newTransatives.clone();
        newTransatives.clear();
        for (int transative:oldTransatives.keySet()){
            MappingSetInfo info = mapper.getMappingSetInfo(transative);
            computeTransative(info, oldTransatives.get(transative));            
        }
        int newMaxMappingSet = getMaxMappingSet();
        mapper.putProperty(LAST_TRANSATIVEL_LOADED_KEY, "" + maxMappingSet);
        if (maxMappingSet != newMaxMappingSet){
            computeTransatives(maxMappingSet);
        }
    }
   
    private void computeTransative(MappingSetInfo info, int checkTo) throws BridgeDBException, RDFHandlerException, IOException {
        System.out.println ("compute transtaive mappingset " + info.getIntId() + " up to " + checkTo);
        System.out.println (info);
//        lastTranstativeLoaded = mappingSetId;
        List<MappingSetInfo> possibleInfos = findTransativeCandidates(info, checkTo);
        for (MappingSetInfo possibleInfo:possibleInfos) {
            HashSet<Integer> chainIds = this.getChain(possibleInfo, info);
            if (checkValidTransative(possibleInfo, info, chainIds)){
                int result = doTransative(possibleInfo, info, chainIds);
                if (result >0) {
                   newTransatives.put(result, info.getIntId());
                }
            }
            if (checkValidTransative(info, possibleInfo, chainIds)){
                doTransative(info, possibleInfo, chainIds);
            }
         }
    }
 
    private List<MappingSetInfo> findTransativeCandidates(MappingSetInfo info, int checkTo) throws BridgeDBException {
        Statement statement = mapper.createStatement();
        String query = "SELECT *"
                + " FROM " + SQLUriMapper.MAPPING_SET_TABLE_NAME
                + " WHERE "+ SQLUriMapper.ID_COLUMN_NAME + " < " + checkTo
                + " AND " + SQLUriMapper.JUSTIFICATION_COLUMN_NAME + " = '" + info.getJustification() +"'";
        ResultSet rs;
        try {
            rs = statement.executeQuery(query);   
            return mapper.resultSetToMappingSetInfos(rs);
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
    }
    
    private boolean checkValidTransative(MappingSetInfo left, MappingSetInfo right, HashSet<Integer> chainIds) throws BridgeDBException {
        //Left dource must be less than right source
        if (left.getSourceSysCode().equals(right.getTargetSysCode())){
            System.out.println ("Loop " + left.getStringId() + " -> " + right.getStringId());
            System.out.println ("    " + left.getSourceSysCode() + " == " + right.getTargetSysCode());
            return false;
        }
        //Must match in the middle
        if (!left.getTargetSysCode().equals(right.getSourceSysCode())){
            System.out.println ("No match " + left.getStringId() + " -> " + right.getStringId());
            System.out.println ("    " + left.getTargetSysCode() + " != " + right.getSourceSysCode());
            return false;
        }
        //If Either is transantive only connect if left is a lower number
        //This makes sure the same chain is not connected in many differet ways
        /*if (!left.getViaSystemCode().isEmpty() || !right.getViaSystemCode().isEmpty()){
            int leftId = left.getIntId();
            int rightId = right.getIntId();
            if (leftId > rightId){
                System.out.println("Skipping Alternative chain with " + left.getStringId() + " -> " + right.getStringId());
                return false;
            }
        }*/
        for (String via:left.getViaSystemCode()){
            if (right.getTargetSysCode().equals(via)){
                System.out.println("Target in Via with " + left.getStringId() + " -> " + right.getStringId());
                System.out.println ("    " + via + " == " + right.getTargetSysCode());            
                return false;
            }
            if (left.getTargetSysCode().equals(via)){
                System.out.println("Middle in Via with " + left.getStringId() + " -> " + right.getStringId());
                System.out.println ("    " + via + " == " + left.getTargetSysCode());            
                return false;
            }
        }
        for (String via:right.getViaSystemCode()){
            if (left.getSourceSysCode().equals(via)){
                System.out.println("Source in Via with " + left.getStringId() + " -> " + right.getStringId());
                System.out.println ("    " + via + " == " + left.getSourceSysCode());            
                return false;
            }
            if (left.getTargetSysCode().equals(via)){
                System.out.println("Middle in Via with " + left.getStringId() + " -> " + right.getStringId());
                System.out.println ("    " + via + " == " + left.getTargetSysCode());            
                return false;
            }
            for (String via2:left.getViaSystemCode()){
                if (via.equals(via2)){
                    System.out.println("Similar via with " + left.getStringId() + " -> " + right.getStringId());
                    System.out.println("    " + via);
                    return false;
                }
            }
        }
        if (chainAlreadyExists(chainIds)){
            System.out.println("Chain already exists with " + left.getStringId() + " -> " + right.getStringId());
            System.out.println("    " + chainIds);
            return false;        
        }
        return true; 
    }

    private boolean chainAlreadyExists(Set<Integer> chainIds) throws BridgeDBException{
        if (chainIds.size() < 2){
            return false;
        }
        Set<Integer> possibles = null;
        for (Integer chainId:chainIds){
            Set<Integer> newPossibles = getTransativesThatUseId(chainId);
            if (possibles == null){
                possibles = newPossibles;
            } else {
                possibles = setIntersection(possibles, newPossibles);
            }
            if (possibles.isEmpty()){
                return false;
            }
        }
        return true;
    }
    
    private int doTransative(MappingSetInfo left, MappingSetInfo right, HashSet<Integer> chainIds) 
            throws RDFHandlerException, IOException, BridgeDBException {
        int leftId = left.getIntId();
        int rightId = right.getIntId();
        Reporter.println("Creating tranasative from " + leftId + " to " + rightId);
        System.out.println(left);
        System.out.println(right);
        System.out.println(chainIds);
        File fileName = TransativeCreator.doTransativeIfPossible(left, right, storeType);
        if (fileName == null){
            Reporter.println ("No transative links found");
            return -1;
        } else {
            Reporter.println("Created " + fileName);
            return linksetLoader.loadLinkset(fileName.getAbsolutePath(), storeType, ValidationType.LINKSMINIMAL, chainIds);
        }
    }

    private HashSet<Integer> getChain(MappingSetInfo left, MappingSetInfo right){
        HashSet<Integer> chainIds = new HashSet<Integer>();
        if (left.getChainIds().isEmpty()){
            if (left.isSymmetric()){
                chainIds.add(left.getSymmetric());
            } else {
                chainIds.add(left.getIntId());
            }
        } else {
            chainIds.addAll(left.getChainIds());
        }
        if (right.getChainIds().isEmpty()){
            if (right.isSymmetric()){
                chainIds.add(right.getSymmetric());
            } else {
                chainIds.add(right.getIntId());
            }
        } else {
            chainIds.addAll(right.getChainIds());
        }
        return chainIds;
    }
    
    private int getMaxMappingSet() throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT MAX(");
        query.append(SQLUriMapper.ID_COLUMN_NAME);
        query.append(") as mymax FROM ");
        query.append(SQLUriMapper.MAPPING_SET_TABLE_NAME);
        
        Statement statement = this.createStatement();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            if (rs.next()){
                return (rs.getInt("mymax"));
            }
            return 0;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
    }

    private Set<Integer> getTransativesThatUseId(Integer chainId) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(SQLUriMapper.MAPPING_SET_ID_COLUMN_NAME);
        query.append(" FROM ");
        query.append(SQLUriMapper.CHAIN_TABLE_NAME);
        query.append(" WHERE ");
        query.append(SQLUriMapper.CHAIN_ID_COLUMN_NAME);
        query.append(" = ");
        query.append(chainId);
        
        Statement statement = this.createStatement();
        HashSet results = new HashSet<Integer>();
        try {
            ResultSet rs = statement.executeQuery(query.toString());
            while (rs.next()){
                results.add(rs.getInt(SQLUriMapper.MAPPING_SET_ID_COLUMN_NAME));
            }
            return results;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }    
    }

    private Set<Integer> setIntersection(Set<Integer> possibles, Set<Integer> newPossibles) {
        if (possibles.size() > newPossibles.size()){
            return setIntersection(newPossibles, possibles);
        }
        //ystem.out.println(possibles);
        //ystem.out.println(newPossibles);
        Iterator<Integer> check = possibles.iterator();
        while (check.hasNext()){
            if (!newPossibles.contains(check.next())){
                check.remove();
            }
        }
        return possibles;
    }


}
