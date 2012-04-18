package org.bridgedb.url;

import org.bridgedb.IDMapperException;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.url.OpsMapper;
import org.bridgedb.url.URLMapperTestBase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.*;


/**
 * @author Christian
 */
public abstract class OpsMapperTest extends URLMapperTestBase {
    
    protected static OpsMapper opsMapper;
    
    @Test
    public void testGetOverallStatistics()  throws IDMapperException{
        report("GetOverallStatistics");
        OverallStatistics results = opsMapper.getOverallStatistics();
        assertThat (results.getNumberOfMappings(), greaterThanOrEqualTo(6));
        assertThat (results.getNumberOfProvenances(), greaterThan(1));
        assertThat (results.getNumberOfTargetDataSources(), greaterThan(3));
        assertThat (results.getNumberOfSourceDataSources(), greaterThan(3));
    }

}
