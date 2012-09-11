/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import javax.xml.datatype.DatatypeConfigurationException;
import org.bridgedb.metadata.utils.Reporter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class LinkSetMetaDataTest extends MetaDataTestBase{
    
    public LinkSetMetaDataTest() throws DatatypeConfigurationException{        
    }
    
    @Test
    public void testHasRequiredValues() throws MetaDataException{
        Reporter.report("Linkset HasRequiredValues");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet());
        System.out.println(metaData.toString());
        checkRequiredValues(metaData, RequirementLevel.MUST);
        assertFalse(metaData.hasRequiredValues(RequirementLevel.MAY));
    } 

    @Test
    public void testHasCorrectTypes() throws MetaDataException{
        Reporter.report("Linkset HasCorrectTypes");
        MetaDataCollection metaData = new MetaDataCollection(loadLinkSet());
        checkCorrectTypes(metaData);
    }
    
}
