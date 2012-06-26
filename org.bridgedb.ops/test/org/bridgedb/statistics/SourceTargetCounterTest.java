package org.bridgedb.statistics;

import org.bridgedb.ops.ProvenanceInfo;
import org.bridgedb.ops.OpsMapper;
import org.junit.BeforeClass;
import org.bridgedb.DataSource;
import java.util.List;
import java.util.ArrayList;
import org.bridgedb.IDMapperException;
import org.bridgedb.url.URLMapperTestBase;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

/**
 * @author Christian
 */
public abstract class SourceTargetCounterTest extends URLMapperTestBase {
    
    protected static OpsMapper opsMapper; 

    @BeforeClass
    public static void setupURLs() throws IDMapperException{
        URLMapperTestBase.setupURLs();
        link1to2 = "http://localhost:8080/OPS-IMS/linkset/1";
        link1to3 = "http://localhost:8080/OPS-IMS/linkset/3";
        link2to1 = "http://localhost:8080/OPS-IMS/linkset/2";
        link2to3 = "http://localhost:8080/OPS-IMS/linkset/5";
        link3to1 = "http://localhost:8080/OPS-IMS/linkset/4";
        link3to2 = "http://localhost:8080/OPS-IMS/linkset/6";
    }
    
    @Test
    public void testGetProvenanceInfos() throws IDMapperException{
        report("GetProvenanceInfos");
        List<ProvenanceInfo> results = opsMapper.getProvenanceInfos();
        SourceTargetCounter counter = new SourceTargetCounter(results);
        List<ProvenanceInfo> result = counter.getSummaryInfos();
        assertThat (results.size(), greaterThanOrEqualTo(6));
        boolean found = false;
        for (ProvenanceInfo info:results){
            if (info.getId().equals(link3to2)){
                found = true;
                assertEquals(nameSpace3, info.getSourceNameSpace());
                assertEquals(nameSpace2, info.getTargetNameSpace());
                assertEquals(new Integer(3), info.getNumberOfLinks());
            }
        }
        assertTrue(found);
    }
    
}
