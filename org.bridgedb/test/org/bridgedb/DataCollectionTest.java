package org.bridgedb;

import java.util.HashSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Christian
 */
public class DataCollectionTest extends IDMapperTest {

    @BeforeClass
    public static void setupIDMapper(){
        HashSet<DataSource> dataSources = new HashSet<DataSource>();
        dataSources.add(DataSource1);
        dataSources.add(DataSource2);
        dataSources.add(DataSource3);
        idMapper = new DataCollection(dataSources, "\\d*");
    }
    
}
