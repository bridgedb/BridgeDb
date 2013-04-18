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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bridgedb.linkset.LinksetLoader;
import org.bridgedb.sql.SQLBase;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.statistics.DataSetInfo;
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
                computeTransative(info);
            }
        }
        int newMaxMappingSet = getMaxMappingSet();
        mapper.putProperty(LAST_TRANSATIVEL_LOADED_KEY, "" + maxMappingSet);
        if (maxMappingSet != newMaxMappingSet){
            computeTransatives(maxMappingSet);
        }
    }
   
    private void computeTransative(MappingSetInfo info) throws BridgeDBException, RDFHandlerException, IOException {
        System.out.println ("compute transtaive mappingset " + info.getIntId());
        //ystem.out.println (info);
//        lastTranstativeLoaded = mappingSetId;
        List<MappingSetInfo> possibleInfos = findTransativeCandidates(info);
        for (MappingSetInfo possibleInfo:possibleInfos) {
            HashSet<Integer> chainIds = this.getChain(possibleInfo, info);
            if (checkValidTransative(possibleInfo, info, chainIds)){
                int result = doTransative(possibleInfo, info, chainIds);
            }
            if (checkValidTransative(info, possibleInfo, chainIds)){
                doTransative(info, possibleInfo, chainIds);
            }
         }
    }
 
    private List<MappingSetInfo> findTransativeCandidates(MappingSetInfo info) throws BridgeDBException {
        Statement statement = mapper.createStatement();
        String query = "SELECT *"
                + " FROM " + SQLUriMapper.MAPPING_SET_TABLE_NAME
                + " WHERE "+ SQLUriMapper.ID_COLUMN_NAME + " < " + info.getIntId();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query);   
            List<MappingSetInfo> possibles = mapper.resultSetToMappingSetInfos(rs);
            List<MappingSetInfo> results = new ArrayList<MappingSetInfo>();
            for (MappingSetInfo possible:possibles){
                String combine = JustificationMaker.possibleCombine(possible.getJustification(), info.getJustification());
                if (combine != null){
                    results.add(possible);
                }
            }
            return results;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
    }
    
    private List<MappingSetInfo> findTransativeCandidatesOld(MappingSetInfo info) throws BridgeDBException {
        Statement statement = mapper.createStatement();
        String query = "SELECT *"
                + " FROM " + SQLUriMapper.MAPPING_SET_TABLE_NAME
                + " WHERE "+ SQLUriMapper.ID_COLUMN_NAME + " < " + info.getIntId()
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
        //Must match in the middle
        if (!left.getTarget().getSysCode().equals(right.getSource().getSysCode())){
            System.out.println ("No match " + left.getStringId() + " -> " + right.getStringId());
            System.out.println ("    " + left.getTarget().getSysCode() + " != " + right.getSource().getSysCode());
            return false;
        }
        boolean repeatFound = false;
        for (Integer id:chainIds){
            if (id < 0){
                if (repeatFound){
                    System.out.println("Multiple linksets used repeatedly with " + left.getStringId() + " -> " + right.getStringId());
                    System.out.println("    " + chainIds);
                    return false;
                }
                repeatFound = true;
            }
        }
        if (!checkValidNoLoopTransative(left, right, chainIds)){
            if (left.getSource().getSysCode().equals(right.getTarget().getSysCode())){
                if (!checkValidLoopTransative(left, right, chainIds)){
                    return false;
                }
            } else {
                return false;
            }
        }
        if (chainAlreadyExists(chainIds)){
            System.out.println("Chain already exists with " + left.getStringId() + " -> " + right.getStringId());
            System.out.println("    " + chainIds);
            return false;        
        }
        return true;
    }
    
    private boolean checkValidNoLoopTransative(MappingSetInfo left, MappingSetInfo right, HashSet<Integer> chainIds) throws BridgeDBException {
        for (DataSetInfo via:left.getViaDataSets()){
            if (right.getTarget().getSysCode().equals(via.getSysCode())){
                System.out.println("Target in Via with " + left.getStringId() + " -> " + right.getStringId());
                System.out.println ("    " + via.getSysCode() + " == " + right.getTarget().getSysCode());            
                return false;
            }
            if (left.getTarget().getSysCode().equals(via.getSysCode())){
                System.out.println("Middle in Via with " + left.getStringId() + " -> " + right.getStringId());
                System.out.println ("    " + via.getSysCode() + " == " + left.getTarget().getSysCode());            
                return false;
            }
        }
        for (DataSetInfo via:right.getViaDataSets()){
            if (left.getSource().getSysCode().equals(via.getSysCode())){
                System.out.println("Source in Via with " + left.getStringId() + " -> " + right.getStringId());
                System.out.println ("    " + via.getSysCode() + " == " + left.getSource().getSysCode());            
                return false;
            }
            if (left.getTarget().getSysCode().equals(via.getSysCode())){
                System.out.println("Middle in Via with " + left.getStringId() + " -> " + right.getStringId());
                System.out.println ("    " + via.getSysCode() + " == " + left.getTarget().getSysCode());            
                return false;
            }
            for (DataSetInfo via2:left.getViaDataSets()){           
                if (via.equals(via2)){
                    System.out.println("Similar via with " + left.getStringId() + " -> " + right.getStringId());
                    System.out.println("    " + via);
                    return false;
                }
            }
        }
        return true; 
    }

    private boolean checkValidLoopTransative(MappingSetInfo left, MappingSetInfo right, HashSet<Integer> chainIds) throws BridgeDBException {
        System.out.println ("Checkin loop " + left.getStringId() + " -> " + right.getStringId());
        if (left.getSource().getSysCode().equals(right.getSource().getSysCode())){
            System.out.println ("Loop with self in middle " + left.getStringId() + " -> " + right.getStringId());
            System.out.println ("    " + left.getSource().getSysCode()+ " == " + right.getSource().getSysCode() + " == " + right.getTarget().getSysCode());
            return false;
        }
        Set<Integer> leftChain = getChain(left);
        Set<Integer> rightChain = getChain(right);
        if (leftChain.size() == rightChain.size() -1){
            return compareChains(left, right, rightChain, leftChain);
        }
        if (leftChain.size()-1 == rightChain.size()){
            return compareChains(left, right, leftChain, rightChain);
        }
        System.out.println ("chain size mismatch " + left.getStringId() + " -> " + right.getStringId());
        System.out.println ("    " + leftChain + " / " + rightChain );
        return false;
        
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
            int dataSet =  linksetLoader.loadLinkset(fileName.getAbsolutePath(), storeType, ValidationType.LINKSMINIMAL, chainIds);
            System.out.println("Loaded " + dataSet);
            return dataSet;
        }
    }

    public static HashSet<Integer> getChain(MappingSetInfo left, MappingSetInfo right){
        HashSet<Integer> chainIds = getChain(left);
        for (Integer id:getChain(right)){
            if (chainIds.contains(id)){
                chainIds.add(0-id);
            } else{
                chainIds.add(id);            
            }
        }
        return chainIds;
    }
    
    private static HashSet<Integer> getChain(MappingSetInfo info){
        HashSet<Integer> chainIds = new HashSet<Integer>();
        if (info.getChainIds().isEmpty()){
            if (info.isSymmetric()){
                chainIds.add(info.getSymmetric());
            } else {
                chainIds.add(info.getIntId());
            }
        }
        chainIds.addAll(info.getChainIds());
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

    private boolean compareChains(MappingSetInfo left, MappingSetInfo right, Set<Integer> bigChain, Set<Integer> smallChain) throws BridgeDBException {
        System.out.println(bigChain + " -> " + smallChain);
        bigChain.removeAll(smallChain);
        if (bigChain.size() != 1){
           System.out.println ("Chain too different " + left.getStringId() + " -> " + right.getStringId());
           System.out.println ("    " + bigChain);
           return false;
        }
        Integer id = bigChain.iterator().next();
        MappingSetInfo info = mapper.getMappingSetInfo(id);
        if (!info.getSource().equals(info.getTarget())){
           System.out.println ("Diffrent is not loop " + left.getStringId() + " -> " + right.getStringId());
           System.out.println ("    " + info);
           return false;            
        }
        return true;
    }

}
