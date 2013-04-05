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
import java.util.List;
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
            //if (info.isSymmetric()){
            //    i++;
            //}
            if (!info.isTransitive()){
                computeTransative(info, true);
            }
        }
        mapper.putProperty(LAST_TRANSATIVEL_LOADED_KEY, "" + maxMappingSet);
        int newMaxMappingSet = getMaxMappingSet();
        if (maxMappingSet != newMaxMappingSet){
            computeTransatives(maxMappingSet);
        }
    }
   
    private void computeTransative(MappingSetInfo info, boolean beforeAndAfter) throws BridgeDBException, RDFHandlerException, IOException {
        System.out.println ("Do transtaive mappingset " + info.getId());
//        lastTranstativeLoaded = mappingSetId;
        List<MappingSetInfo> possibleInfos = findTransativeCandidates(info);
        for (MappingSetInfo possibleInfo:possibleInfos) {
            //ystem.out.println("checking " + possibleInfo.getId());
            if (checkValidTransative(possibleInfo, info)){
                doTransative(possibleInfo, info);
            }
            //ystem.out.println("checking inverse " + possibleInfo.getId());
            if (checkValidTransative(info, possibleInfo)){
                doTransative(info, possibleInfo);
            }
         }
    }
 
    private List<MappingSetInfo> findTransativeCandidates(MappingSetInfo info) throws BridgeDBException {
        Statement statement = mapper.createStatement();
        String query = "SELECT *"
                + " FROM " + SQLUriMapper.MAPPING_SET_TABLE_NAME
                + " WHERE "+ SQLUriMapper.ID_COLUMN_NAME + " < " + info.getId()
                + " AND " + SQLUriMapper.JUSTIFICATION_COLUMN_NAME + " = '" + info.getJustification() +"'";
        ResultSet rs;
        try {
            rs = statement.executeQuery(query);   
            return mapper.resultSetToMappingSetInfos(rs);
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
    }
    
    private boolean checkValidTransative(MappingSetInfo left, MappingSetInfo right) {
        //Left dource must be less than right source
        if (left.getSourceSysCode().compareTo(right.getTargetSysCode()) >= 0){
            //ystem.out.println ("Loop " + left.getId() + " -> " + right.getId());
            //ystem.out.println ("    " + left.getSourceSysCode() + " == " + right.getTargetSysCode());
            return false;
        }
        //Must match in the middle
        if (!left.getTargetSysCode().equals(right.getSourceSysCode())){
            //ystem.out.println ("No match " + left.getId() + " -> " + right.getId());
            //stem.out.println ("    " + left.getTargetSysCode() + " != " + right.getSourceSysCode());
            return false;
        }
        //If Either is transantive only connect if left is a lower number
        //This makes sure the same chain is not connected in many differet ways
        if (!left.getViaSystemCode().isEmpty() || !right.getViaSystemCode().isEmpty()){
            int leftId = Integer.parseInt(left.getId());
            int rightId = Integer.parseInt(right.getId());
            if (leftId > rightId){
                //ystem.out.println("Skipping Alternative chain with " + left.getId() + " -> " + right.getId());
                return false;
            }
        }
        for (String via:left.getViaSystemCode()){
            if (right.getTargetSysCode().equals(via)){
                //ystem.out.println("Target in Via with " + left.getId() + " -> " + right.getId());
                //ystem.out.println ("    " + via + " == " + right.getTargetSysCode());            
                return false;
            }
            if (left.getTargetSysCode().equals(via)){
                //ystem.out.println("Middle in Via with " + left.getId() + " -> " + right.getId());
                //ystem.out.println ("    " + via + " == " + left.getTargetSysCode());            
                return false;
            }
        }
        for (String via:right.getViaSystemCode()){
            if (left.getSourceSysCode().equals(via)){
                //ystem.out.println("Source in Via with " + left.getId() + " -> " + right.getId());
                //ystem.out.println ("    " + via + " == " + left.getSourceSysCode());            
                return false;
            }
            if (left.getTargetSysCode().equals(via)){
                //ystem.out.println("Middle in Via with " + left.getId() + " -> " + right.getId());
                //ystem.out.println ("    " + via + " == " + left.getTargetSysCode());            
                return false;
            }
            for (String via2:left.getViaSystemCode()){
                if (via.equals(via2)){
                    //ystem.out.println("Similar via with " + left.getId() + " -> " + right.getId());
                    //ystem.out.println("    " + via);
                    return false;
                }
            }
        }
        return true; 
    }

    private void doTransative(MappingSetInfo left, MappingSetInfo right) 
            throws RDFHandlerException, IOException, BridgeDBException {
        int leftId = Integer.parseInt(left.getId());
        int rightId = Integer.parseInt(right.getId());
        Reporter.println("Creating tranasative from " + leftId + " to " + rightId);
        System.out.println(left);
        System.out.println(right);
        File fileName = TransativeCreator.doTransativeIfPossible(left, right, storeType);
        if (fileName == null){
            Reporter.println ("No transative links found");
        } else {
            Reporter.println("Created " + fileName);
            linksetLoader.load(fileName.getAbsolutePath(), storeType, ValidationType.LINKSMINIMAL);
        }
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


}
