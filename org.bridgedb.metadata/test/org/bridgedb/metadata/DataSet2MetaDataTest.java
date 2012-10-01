/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.junit.Ignore;
import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.metadata.utils.Reporter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
@Ignore
public class DataSet2MetaDataTest extends MetaDataTestBase{
    
    public DataSet2MetaDataTest() throws DatatypeConfigurationException, MetaDataException{        
    }
    
    @Test
    public void testHasRequiredValues() throws MetaDataException{
        Reporter.report("HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet2(), dataSetRegistry);
        checkRequiredValues(metaData);
    } 

    @Test
    public void testHasCorrectTypes() throws MetaDataException{
        Reporter.report("HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet2(), dataSetRegistry);
        checkCorrectTypes(metaData);
    }

    @Test
    public void testAllStatementsUsed() throws MetaDataException{
        Reporter.report("AllStatementsUsed");
        MetaDataCollection metaData = new MetaDataCollection(loadDataSet2(), dataSetRegistry);
        checkAllStatementsUsed(metaData);
    }

}
