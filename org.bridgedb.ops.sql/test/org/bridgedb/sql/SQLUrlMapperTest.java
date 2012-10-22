package org.bridgedb.sql;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.IDMapperException;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.url.URLListenerTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class SQLUrlMapperTest extends URLListenerTest {
		
	private static final String BASE_URI = "http://localhost:8080/OPS-IMS/";
	private static SQLAccess sqlAccess;
	private static SQLUrlMapper sqlUrlMapper;

	@BeforeClass
	public static void beforeClassInitialisation() throws IDMapperException {
		sqlAccess = TestSqlFactory.createTestSQLAccess();
		listener = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
		loadData();
		sqlUrlMapper = (SQLUrlMapper) listener;
	}
	
	private void validateResult(String query, int expectedResult)
			throws BridgeDbSqlException, SQLException {
		Connection connection = sqlAccess.getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			assertEquals(expectedResult, resultSet.getInt(1));
		}
	}
	
	@Test
	public void testRegisterProfile() throws IDMapperException, SQLException {
		List<String> justificationUris = new ArrayList<String>();
		justificationUris.add(TEST_JUSTIFICATION1);
		sqlUrlMapper.registerProfile("test1", "2012-09-28T16:02", "http://www.cs.man.ac.uk/~graya/me.ttl", justificationUris);
		validateResult("SELECT COUNT(*) FROM profile", 1);
		validateResult("SELECT COUNT(*) FROM profileJustifications", 1);
		assertEquals(1, sqlUrlMapper.getOverallStatistics().getNumberOfProfiles());
		justificationUris.add(TEST_JUSTIFICATION2);
		sqlUrlMapper.registerProfile("test2", "2012-10-01T16:15", "http://www.cs.man.ac.uk/~brennic", justificationUris);
		validateResult("SELECT COUNT(*) FROM profile", 2);
		validateResult("SELECT COUNT(*) FROM profileJustifications", 3);
		assertEquals(2, sqlUrlMapper.getOverallStatistics().getNumberOfProfiles());
	}

	@Test(expected=BridgeDbSqlException.class)
	public void testInvalidProfile_notaURI() throws BridgeDbSqlException {
		sqlUrlMapper.mapURL(map1URL1, "1");
	}
	
	@Test
	public void testGetMappingNoProfile() throws BridgeDbSqlException {
		Set<String> expectedURL = new HashSet<String>();
		expectedURL.add("http://www.foo.com/123");
		expectedURL.add("http://www.example.com/123");
		expectedURL.add("http://www.example.org#123");
		
		Set<String> mapURL = sqlUrlMapper.mapURL(map1URL1, BASE_URI + "0");
		assertEquals(3, mapURL.size());
		assertEquals(expectedURL, mapURL);
	}

	@Test(expected=BridgeDbSqlException.class)
	public void testGetMappingProfileNotValid() throws BridgeDbSqlException {
		Set<String> mapURL = sqlUrlMapper.mapURL(map1URL1, "Anything else");
		assertEquals(1, mapURL.size());
	}

	@Test
	public void testGetMappingsProfile1() throws BridgeDbSqlException {
		Set<String> expectedURL = new HashSet<String>();
		expectedURL.add("http://www.foo.com/123");
		expectedURL.add("http://www.example.com/123");
		
		Set<String> mapURL = sqlUrlMapper.mapURL(map1URL1, BASE_URI + "1");
		assertEquals(2, mapURL.size());
		assertEquals(expectedURL, mapURL);
	}

	@Test
	public void testGetMappingsProfile2() throws BridgeDbSqlException {
		Set<String> expectedURL = new HashSet<String>();
		expectedURL.add("http://www.foo.com/456");
		expectedURL.add("http://www.example.com/456");
		expectedURL.add("http://www.example.org#456");
		
		Set<String> mapURL = sqlUrlMapper.mapURL(map2URL1, BASE_URI + "2");
		assertEquals(3, mapURL.size());
		assertEquals(expectedURL, mapURL);
	}

}
