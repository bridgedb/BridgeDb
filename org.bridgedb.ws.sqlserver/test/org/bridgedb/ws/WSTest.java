/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.ws.bean.DataSourceBean;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class WSTest extends IDMapperTest{
    
    public static WSInterface webService;
    
    @Test
    public void testGetDataSource() throws IDMapperException{
        System.out.println("GetDataSource");
        DataSourceBean result = webService.getDataSoucre(DataSource1.getSystemCode());
        assertEquals(result.asDataSource(), DataSource1);
    }
    
}
