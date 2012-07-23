package org.bridgedb.linkset;
import org.bridgedb.IDMapperException;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.utils.Reporter;

/**
 * @author Christian
 */
public class SetupLoaderWithTestData {
        
   public static void main(String[] args) throws BridgeDbSqlException, IDMapperException {
        Reporter.report("sample2to1.ttl");
        String[] args1 = {"../org.bridgedb.linksets/test-data/sample1to2.ttl", "new"};
        LinksetLoader.main (args1);
        Reporter.report("sample1to3.ttl");
        String[] args2 = {"../org.bridgedb.linksets/test-data/sample1to3.ttl", "load"};
        LinksetLoader.main (args2);
        Reporter.report("sample2to3.ttl");
        String[] args3 = {"../org.bridgedb.linksets/test-data/sample2to3.ttl", "load"};
        LinksetLoader.main (args3);
	}

}
