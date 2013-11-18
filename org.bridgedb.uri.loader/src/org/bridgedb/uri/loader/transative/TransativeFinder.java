/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.uri.loader.transative;

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
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.sql.SQLBase;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.statistics.DataSetInfo;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.UriListener;
import org.bridgedb.uri.loader.LinksetListener;
import org.bridgedb.uri.loader.RdfParser;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class TransativeFinder extends SQLBase{

   private SQLUriMapper mapper;
  
    private final static String LAST_TRANSATIVE_LOADED_KEY = "LastMappingLoadedTransatively";
        
    private final static boolean LIMITED_VIA = true;
    
    private static Set<String> limitedSysCodes;
    
    static final Logger logger = Logger.getLogger(TransativeFinder.class);

    public TransativeFinder() throws BridgeDBException{
        super();
        mapper = SQLUriMapper.getExisting();  
        getLimited();
     }
    
    public void UpdateTransative() throws BridgeDBException, RDFHandlerException, IOException{
        String lastIdString = mapper.getProperty(LAST_TRANSATIVE_LOADED_KEY);
        int lastId;
        if (lastIdString == null){
            lastId = 0;
        } else {
            lastId = Integer.parseInt(lastIdString);
        }
        computeTransatives(lastId);
    }
    
    private void computeTransatives(int lastTranstativeLoaded) throws BridgeDBException, RDFHandlerException, IOException{
        logger.info("Computing from " + lastTranstativeLoaded);
        int maxMappingSet = getMaxMappingSet();
        if (maxMappingSet <= lastTranstativeLoaded){
            return;
        }
        for (int i = lastTranstativeLoaded + 1; i <= maxMappingSet; i++){
            MappingSetInfo info = mapper.getMappingSetInfo(i);
            if (info != null){
                computeTransative(info);
            }
            mapper.putProperty(LAST_TRANSATIVE_LOADED_KEY, "" + i); 
            //See if this recovers some memory.
            //Connection will automatically be reopened later.
            mapper.closeConnection();
        }
        int newMaxMappingSet = getMaxMappingSet();
        mapper.putProperty(LAST_TRANSATIVE_LOADED_KEY, "" + maxMappingSet);
        if (maxMappingSet != newMaxMappingSet){
            computeTransatives(maxMappingSet);
        }
    }
   
    private void computeTransative(MappingSetInfo info) throws BridgeDBException, RDFHandlerException, IOException {
        if (logger.isDebugEnabled()){
            logger.debug("compute transtaive mappingset " + info.getIntId());
        }
        //ystem.out.println (info);
//        lastTranstativeLoaded = mappingSetId;
        List<MappingSetInfo> possibleInfos = findTransativeCandidates(info);
        for (MappingSetInfo possibleInfo:possibleInfos) {
            HashSet<Integer> chainIds = this.mergeChain(possibleInfo, info);
            //if chainIds == null the same id is used three times
            if (chainIds != null){
                if (checkValidTransative(possibleInfo, info, chainIds)){
                    int result = doTransative(possibleInfo, info, chainIds);
                }
                if (checkValidTransative(info, possibleInfo, chainIds)){
                    doTransative(info, possibleInfo, chainIds);
                }
            }
         }
    }
 
    private List<MappingSetInfo> findTransativeCandidates(MappingSetInfo info) throws BridgeDBException {
        Statement statement = mapper.createStatement();
        StringBuilder query = new StringBuilder("SELECT *");
        query.append(" FROM ");
        query.append(SQLUriMapper.MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
        query.append(SQLUriMapper.ID_COLUMN_NAME);
        query.append(" < ");
        query.append(info.getIntId());
        ResultSet rs;
        try {
            rs = statement.executeQuery(query.toString());   
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
        if(LIMITED_VIA){
            if (!getLimited().contains(left.getTarget().getSysCode())){
                if (logger.isDebugEnabled()){
                    logger.debug("Not allowed middle " + left.getStringId() + " -> " + right.getStringId());
                    logger.debug ("    " + left.getTarget());
                }
                return false;
            }
        }
        //Must match in the middle
        if (!left.getTarget().getSysCode().equals(right.getSource().getSysCode())){
            if (logger.isDebugEnabled()){
                logger.debug("No match " + left.getStringId() + " -> " + right.getStringId());
                logger.debug ("    " + left.getTarget().getSysCode() + " != " + right.getSource().getSysCode());
            }
            return false;
        }
        //must not to double map to self
        if (left.getTarget().getSysCode().equals(left.getSource().getSysCode())){
            if (right.getTarget().getSysCode().equals(right.getSource().getSysCode())){
                if (logger.isDebugEnabled()){
                    logger.debug("Double macth to self " + left.getStringId() + " -> " + right.getStringId());
                    logger.debug ("    " + right.getSource().getSysCode() + " == " + right.getTarget().getSysCode() 
                            + " == " + left.getSource().getSysCode() + "== " + left.getTarget().getSysCode());
                }
                System.out.println("Double macth to self " + left.getStringId() + " -> " + right.getStringId());
                System.out.println("    " + right.getSource().getSysCode() + " == " + right.getTarget().getSysCode() 
                            + " == " + left.getSource().getSysCode() + "== " + left.getTarget().getSysCode());
                return false;
            }
        }
        if (left.isSymmetric()){
            if (left.getSymmetric() == right.getIntId()){
                if (logger.isDebugEnabled()){
                    logger.debug("Symmetric " + left.getStringId() + " -> " + right.getStringId());
                }
                return false;
                
            }
        }
        if (right.isSymmetric()){
            if (right.getSymmetric() == left.getIntId()){
                if (logger.isDebugEnabled()){
                    logger.debug("Symmetric " + left.getStringId() + " -> " + right.getStringId());
                }
                return false;              
            }
        }
     
       /*boolean repeatFound = false;
        Integer loopFound = null;
        for (Integer id:chainIds){
            if (id < 0){
                if (repeatFound){
                    if (logger.isDebugEnabled()){
                        logger.debug("Multiple linksets used repeatedly with " + left.getStringId() + " -> " + right.getStringId());
                        logger.debug("    " + chainIds);
                    }
                    return false;
                }
                repeatFound = true;
            }
            if (id > 0){
                MappingSetInfo info = mapper.getMappingSetInfo(id);
                if (info != null && info.getSource().equals(info.getTarget())){
                    if (loopFound != null){
                        if (logger.isDebugEnabled()){
                            logger.debug("Two Loops found: " + left.getStringId() + " -> " + right.getStringId());
                            logger.debug("    " + id + " and " + loopFound);
                        }
                        return false;            
                    }  else {
                        loopFound = id;
                    }
                }
            }
        }
       */ if (!checkValidNoLoopTransative(left, right, chainIds)){
            if (left.getSource().getSysCode().equals(right.getTarget().getSysCode())){
                if (!checkValidLoopTransative(left, right, chainIds)){
                    return false;
                }
            } else {
                return false;
            }
        }
        if (chainAlreadyExists(chainIds)){
            if (logger.isDebugEnabled()){
                logger.debug("Chain already exists with " + left.getStringId() + " -> " + right.getStringId());
                logger.debug("    " + chainIds);
            }
            return false;        
        }
        return true;
    }
    
    private boolean checkValidNoLoopTransative(MappingSetInfo left, MappingSetInfo right, HashSet<Integer> chainIds) throws BridgeDBException {
        for (DataSetInfo via:left.getViaDataSets()){
            if (right.getTarget().getSysCode().equals(via.getSysCode())){
                if (logger.isDebugEnabled()){
                    logger.debug("Target in Via with " + left.getStringId() + " -> " + right.getStringId());
                    logger.debug("    " + via.getSysCode() + " == " + right.getTarget().getSysCode());            
                }
                return false;
            }
            if (left.getTarget().getSysCode().equals(via.getSysCode())){
                if (logger.isDebugEnabled()){
                    logger.debug("Middle in Via with " + left.getStringId() + " -> " + right.getStringId());
                    logger.debug("    " + via.getSysCode() + " == " + left.getTarget().getSysCode());            
                }
                return false;
            }
        }
        for (DataSetInfo via:right.getViaDataSets()){
            if (left.getSource().getSysCode().equals(via.getSysCode())){
                if (logger.isDebugEnabled()){
                    logger.debug("Source in Via with " + left.getStringId() + " -> " + right.getStringId());
                    logger.debug("    " + via.getSysCode() + " == " + left.getSource().getSysCode());  
                }
                return false;
            }
            if (left.getTarget().getSysCode().equals(via.getSysCode())){
                if (logger.isDebugEnabled()){
                    logger.debug("Middle in Via with " + left.getStringId() + " -> " + right.getStringId());
                    logger.debug("    " + via.getSysCode() + " == " + left.getTarget().getSysCode());            
                }
                return false;
            }
            for (DataSetInfo via2:left.getViaDataSets()){           
                if (via.equals(via2)){
                    if (logger.isDebugEnabled()){
                        logger.debug("Similar via with " + left.getStringId() + " -> " + right.getStringId());
                        logger.debug("    " + via);
                    }
                    return false;
                }
            }
        }
        return true; 
    }

    private boolean checkValidLoopTransative(MappingSetInfo left, MappingSetInfo right, HashSet<Integer> chainIds) throws BridgeDBException {
        if (logger.isDebugEnabled()){
            logger.debug("Checkin loop " + left.getStringId() + " -> " + right.getStringId());
        }
        if (left.getSource().getSysCode().equals(right.getSource().getSysCode())){
            if (logger.isDebugEnabled()){
                logger.debug("Loop with self in middle " + left.getStringId() + " -> " + right.getStringId());
                logger.debug("    " + left.getSource().getSysCode()+ " == " + right.getSource().getSysCode() + " == " + right.getTarget().getSysCode());
            }
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
        if (logger.isDebugEnabled()){
            logger.debug("chain size mismatch " + left.getStringId() + " -> " + right.getStringId());
            logger.debug("    " + leftChain + " / " + rightChain );
        }
        return false;
        
    }

    private boolean chainAlreadyExists(Set<Integer> chainIds) throws BridgeDBException{
        //ystem.out.println("chainAlreadyExists" +  chainIds);
        if (chainIds.size() < 2){
            return false;
        }
        Set<Integer> possibles = null;
        for (Integer chainId:chainIds){
            Set<Integer> newPossibles = getTransativesThatUseId(chainId);
            //ystem.out.println(chainId + "" + newPossibles);
            if (possibles == null){
                possibles = newPossibles;
            } else {
                possibles = setIntersection(possibles, newPossibles);
            }
            if (possibles.isEmpty()){
                return false;
            }
        }
        //ystem.out.println(possibles);
        for (Integer possible:possibles){
            MappingSetInfo possibleInfo = mapper.getMappingSetInfo(possible);
            Set<Integer> check = getChain(possibleInfo);
            if (check.size() == chainIds.size()){
                return true;
            }
        }
        return false;
    }
    
    private int doTransative(MappingSetInfo left, MappingSetInfo right, HashSet<Integer> chainIds) 
            throws RDFHandlerException, IOException, BridgeDBException {
        int leftId = left.getIntId();
        int rightId = right.getIntId();
        Reporter.println("Creating tranasative from " + leftId + " to " + rightId + " chain: " + chainIds);
        if (logger.isDebugEnabled()){
            logger.debug(left);
            logger.debug(right);
            logger.debug(chainIds);
        }
        Set<String> viaLabels = new HashSet<String>();
        for (DataSetInfo info:left.getViaDataSets()){
            viaLabels.add(info.getSysCode());
        }
        for (DataSetInfo info:right.getViaDataSets()){
            viaLabels.add(info.getSysCode());
        }
        viaLabels.add(left.getTarget().getSysCode());
        if (logger.isDebugEnabled()){
            logger.debug(viaLabels);
        }
        String predicate = PredicateMaker.combine(left.getPredicate(), right.getPredicate());
        String justification = JustificationMaker.combine(left.getJustification(), right.getJustification());

        File fileName = doTransativeIfPossible(left, right);
        if (fileName == null){
            Reporter.println ("No transative links found");
            return -1;
        } else {
            Reporter.println("Created " + fileName);
            int dataSet =  loadLinkset(fileName.getAbsolutePath(), predicate, justification, viaLabels, chainIds);
            if (logger.isDebugEnabled()){
                logger.debug("Loaded " + dataSet);
            }
            return dataSet;
        }
    }

    //allows sub classes to call a subclass of TransativeCreator
    protected File doTransativeIfPossible(MappingSetInfo left, MappingSetInfo right) throws BridgeDBException, IOException{
        return TransativeCreator.doTransativeIfPossible(left, right);
    }
    
    public static HashSet<Integer> mergeChain(MappingSetInfo left, MappingSetInfo right){
        HashSet<Integer> leftChain = getChain(left);
        HashSet<Integer> rightChain = getChain(right);
        for (Integer id:rightChain){
            if (leftChain.contains(id)){
                if (leftChain.contains(0-id)){
                    if (logger.isDebugEnabled()){
                        logger.debug("Same set used three times " + leftChain + rightChain);
                    }
                    return null;
                }
                leftChain.add(0-id);
            } else{
                leftChain.add(id);            
            }
        }
        return leftChain;
    }
    
    public static HashSet<Integer> getChainOld(MappingSetInfo left, MappingSetInfo right){
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
        if (logger.isDebugEnabled()){
            logger.debug(bigChain + " -> " + smallChain);
        }
        bigChain.removeAll(smallChain);
        if (bigChain.size() != 1){
            if (logger.isDebugEnabled()){
                logger.debug("Chain too different " + left.getStringId() + " -> " + right.getStringId());
                logger.debug("    " + bigChain);
            }
           return false;
        }
        Integer id = bigChain.iterator().next();
        MappingSetInfo info = mapper.getMappingSetInfo(id);
        if (info == null){
            return false;
        }
        if (!info.getSource().equals(info.getTarget())){
            if (logger.isDebugEnabled()){
                logger.debug("Diffrent is not loop " + left.getStringId() + " -> " + right.getStringId());
                logger.debug("    " + info);
            }
           return false;            
        }
        if (info.getSource().equals(left.getSource())){
            if (logger.isDebugEnabled()){
                logger.debug("Loop same source " + left.getStringId() + " -> " + right.getStringId());
                logger.debug("    " + left.getSource() + " == " + info.getSource());
            }
           return false;            
        }
        return true;
    }

    protected int loadLinkset(String absolutePath, String predicate, String justification, Set<String> viaLabels, 
            HashSet<Integer> chainIds) throws BridgeDBException {
        UriListener uriListener = SQLUriMapper.getExisting();
        LinksetListener loader = new LinksetListener(uriListener);
        //(File file, String mappingSource, URI linkPredicate, String justification)
        File file = new File(absolutePath);
        URI mappingUri = RdfParser.fileToURL(file);
        URI linkPredicate = new URIImpl(predicate);
        return loader.parse(file, mappingUri, mappingUri, linkPredicate, justification, viaLabels, chainIds);
    }

    /**
     * This allows tests tp 
     * @param dataSource 
     */
    public static void addAcceptableVai(DataSource dataSource) {
        getLimited().add(dataSource.getSystemCode());
    }

    private static Set<String> getLimited() {
        if (limitedSysCodes == null){
            limitedSysCodes = new HashSet<String>();
            limitedSysCodes.add(DataSource.getExistingByFullName("Chemspider").getSystemCode());
            limitedSysCodes.add(DataSource.getExistingByFullName("OPS Chemical Registry Service").getSystemCode());
            limitedSysCodes.add(DataSource.getExistingByFullName("ChEMBL target component").getSystemCode());
            limitedSysCodes.add(DataSource.getExistingByFullName("Uniprot-TrEMBL").getSystemCode());
            limitedSysCodes.add(DataSource.getExistingByFullName("Ensembl").getSystemCode());
            limitedSysCodes.add(DataSource.getExistingByFullName("DrugBank").getSystemCode());
            limitedSysCodes.add(DataSource.getExistingByFullName("HMDB").getSystemCode());
        }
        return limitedSysCodes;
    }
 }
