/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata;

import org.bridgedb.tools.metadata.MetaDataCollection;
import org.junit.Ignore;
import org.bridgedb.utils.Reporter;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class DataSet2MetaDataTest extends MetaDataTestBase{
    
    public DataSet2MetaDataTest() throws DatatypeConfigurationException, BridgeDBException{        
    }
    
    @Test
    public void testHasRequiredValues() throws BridgeDBException{
        report("HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection("loadDirectDataSet2()", loadDirectDataSet2(), voidRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testHasCorrectTypes() throws BridgeDBException{
        report("HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet2()", loadMayDataSet2(), voidRegistry);
        checkCorrectTypes(metaData);
    }

    @Test
    @Ignore
    public void testAllStatementsUsed() throws BridgeDBException{
        report("AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection("loadMayDataSet2()", loadMayDataSet2(), voidRegistry);
        checkAllStatementsUsed(metaData);
    }

}
