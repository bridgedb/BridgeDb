package org.bridgedb.sql;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bridgedb.IDMapperException;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.url.URLMapperTestBase;
import org.junit.BeforeClass;
import org.junit.Test;

public class SQLUrlMapperTest extends URLMapperTestBase {
	
	private static SQLAccess sqlAccess;
	private static SQLUrlMapper sqlUrlMapper;
	
	@BeforeClass
	public static void beforeClassInitialisation() throws IDMapperException {
		sqlAccess = TestSqlFactory.createTestSQLAccess();
		sqlUrlMapper = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
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
		justificationUris.add("http://www.example.com/test#predicate");
		sqlUrlMapper.registerProfile("2012-09-28 16:02", "http://www.cs.man.ac.uk/~graya/me.ttl", justificationUris);
		validateResult("SELECT COUNT(*) FROM profile", 1);
		validateResult("SELECT COUNT(*) FROM profileJustifications", 1);
	}

	@Test
	public void testGetMappingNoProfile() throws BridgeDbSqlException {
		Set<String> mapURL = sqlUrlMapper.mapURL(map1URL1, "0");
		assertEquals(1, mapURL.size());
	}

	@Test(expected=BridgeDbSqlException.class)
	public void testGetMappingProfileNotValid() throws BridgeDbSqlException {
		Set<String> mapURL = sqlUrlMapper.mapURL(map1URL1, "Anything else");
		assertEquals(1, mapURL.size());
	}

}
