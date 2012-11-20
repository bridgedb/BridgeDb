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
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.statistics.ProfileInfo;
import org.bridgedb.url.URLListenerTest;
import org.bridgedb.utils.StoreType;
import org.junit.BeforeClass;
import org.junit.Test;

public class SQLUrlMapperTest extends URLListenerTest {
			
	private static SQLAccess sqlAccess;
	private static SQLUrlMapper sqlUrlMapper;

	@BeforeClass
	public static void beforeClassInitialisation() throws IDMapperException {
		 TestSqlFactory.checkSQLAccess();
		sqlAccess = SqlFactory.createTheSQLAccess(StoreType.TEST);
		listener = new SQLUrlMapper(true, StoreType.TEST);
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
	
	@Test
	public void testGetProfileInfo() throws IDMapperException {
		List<ProfileInfo> profiles = sqlUrlMapper.getProfiles();
		assertEquals(2, profiles.size());
	}

	@Test(expected=BridgeDbSqlException.class)
	public void testGetProfile_notAUri() throws IDMapperException {
		sqlUrlMapper.getProfile("Not a URI");
	}
	
	@Test(expected=BridgeDbSqlException.class)
	public void testGetProfile_invalidURI() throws IDMapperException {
		sqlUrlMapper.getProfile("http://www.foo.com/123");
	}
	
	@Test(expected=BridgeDbSqlException.class)
	public void testGetProfile_invalidProfileNumber() throws IDMapperException {
		sqlUrlMapper.getProfile(RdfConfig.getProfileURI(123));
	}
		
	@Test
	public void testGetProfile_validProfileURI() throws IDMapperException {
		ProfileInfo profile = sqlUrlMapper.getProfile(RdfConfig.getProfileURI(1));
		assertEquals("test1", profile.getName());
//		assertEquals("2012-09-28T16:02", profile.getCreatedOn());
		assertEquals("2012-09-28 16:02:00.0", profile.getCreatedOn());
		assertEquals("http://www.cs.man.ac.uk/~graya/me.ttl", profile.getCreatedBy());
		Set<String> justifications = profile.getJustification();
		assertEquals(1, justifications.size());
		assertEquals(TEST_JUSTIFICATION1, justifications.iterator().next());
	}

	@Test(expected=BridgeDbSqlException.class)
	public void testInvalidProfile_notaURI() throws IDMapperException {
		sqlUrlMapper.mapURL(map1URL1, "1");
	}
	
	@Test
	public void testGetMappingNoProfile() throws IDMapperException {
		Set<String> expectedURL = new HashSet<String>();
		expectedURL.add("http://www.foo.com/123");
		expectedURL.add("http://www.example.com/123");
		expectedURL.add("http://rdf.example.com/123");
		expectedURL.add("http://www.example.org#123");
		expectedURL.add("http://rdf.example.org#123");
		
		Set<String> mapURL = sqlUrlMapper.mapURL(map1URL1, RdfConfig.getProfileURI(0));
		assertEquals(5, mapURL.size());
		assertEquals(expectedURL, mapURL);
	}

	@Test(expected=BridgeDbSqlException.class)
	public void testGetMappingProfileNotValid() throws IDMapperException {
		Set<String> mapURL = sqlUrlMapper.mapURL(map1URL1, "Anything else");
		assertEquals(1, mapURL.size());
	}

	@Test
	public void testGetMappingsProfile1() throws IDMapperException {
		Set<String> expectedURL = new HashSet<String>();
		expectedURL.add("http://www.foo.com/123");
		expectedURL.add("http://www.example.com/123");
		expectedURL.add("http://rdf.example.com/123");
		
		Set<String> mapURL = sqlUrlMapper.mapURL(map1URL1, RdfConfig.getProfileURI(1));
		assertEquals(3, mapURL.size());
		assertEquals(expectedURL, mapURL);
	}

	@Test
	public void testGetMappingsProfile2() throws IDMapperException {
		Set<String> expectedURL = new HashSet<String>();
		expectedURL.add("http://www.foo.com/456");
		expectedURL.add("http://www.example.com/456");
		expectedURL.add("http://rdf.example.com/456");
		expectedURL.add("http://www.example.org#456");
		expectedURL.add("http://rdf.example.org#456");
		
		Set<String> mapURL = sqlUrlMapper.mapURL(map2URL1, RdfConfig.getProfileURI(2));
		assertEquals(5, mapURL.size());
		assertEquals(expectedURL, mapURL);
	}

}
