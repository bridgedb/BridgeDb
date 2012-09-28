package org.bridgedb.sql;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.bridgedb.mysql.MySQLSpecific;
import org.junit.Test;

public class SQLUrlMapperTest {
	
	@Test
	public void testRegisterProfile() throws IDMapperException, SQLException {
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
		SQLUrlMapper sqlUrlMapper = new SQLUrlMapper(true, sqlAccess, new MySQLSpecific());
		List<String> justificationUris = new ArrayList<String>();
		justificationUris.add("http://www.example.com/test#predicate");
		sqlUrlMapper.registerProfile("2012-09-28 16:02", "http://www.cs.man.ac.uk/~graya/me.ttl", justificationUris);
		validateResult(sqlAccess, "SELECT COUNT(*) FROM profile", 1);
		validateResult(sqlAccess, "SELECT COUNT(*) FROM profileJustifications", 1);
	}

	private void validateResult(SQLAccess sqlAccess, String query, int expectedResult)
			throws BridgeDbSqlException, SQLException {
		Connection connection = sqlAccess.getConnection();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			assertEquals(expectedResult, resultSet.getInt(1));
		}
	}

}
