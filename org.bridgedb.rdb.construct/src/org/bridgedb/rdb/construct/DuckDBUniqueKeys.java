// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.rdb.construct;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Refuses duplicates and nulls before DuckDB sees them, as it aborts the transaction otherwise.
 * <p>
 * This does not use ON CONFLICT DO NOTHING or a query per row, as those make building a database
 * several times slower. To save memory, only a digest of each key is kept.
 */
final class DuckDBUniqueKeys extends DBConnector.UniqueKeys
{
	private record Digest(long high, long low) { }

	private final Set<Digest> keys = new HashSet<>();
	private final MessageDigest sha;

	/**
	 * Reads the keys already in the table, as rows can be added to an existing database.
	 */
	DuckDBUniqueKeys(Connection con, String table, String... columns) throws SQLException
	{
		try
		{
			sha = MessageDigest.getInstance("SHA-256");
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IllegalStateException("Every Java platform has SHA-256", e);
		}
		try (Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("SELECT " + String.join(", ", columns) + " FROM " + table))
		{
			String[] key = new String[columns.length];
			while (rs.next())
			{
				for (int i = 0; i < key.length; i++) key[i] = rs.getString(i + 1);
				keys.add(digest(key));
			}
		}
	}

	@Override
	void insert(PreparedStatement pst, String... key) throws SQLException
	{
		// the SQL states are the standard ones, as also used by Derby
		for (String part : key)
		{
			if (part == null)
				throw new SQLIntegrityConstraintViolationException(
					"Null in key: " + String.join(", ", key), "23502");
		}
		Digest digest = digest(key);
		if (keys.contains(digest))
			throw new SQLIntegrityConstraintViolationException(
				"Duplicate key: " + String.join(", ", key), "23505");
		pst.executeUpdate();
		keys.add(digest);
	}

	private Digest digest(String... key)
	{
		for (String part : key)
		{
			// UTF-8, as DuckDB compares that. The length keeps the parts apart.
			byte[] bytes = part.getBytes(StandardCharsets.UTF_8);
			sha.update(ByteBuffer.allocate(Integer.BYTES).putInt(0, bytes.length));
			sha.update(bytes);
		}
		ByteBuffer hash = ByteBuffer.wrap(sha.digest());
		return new Digest(hash.getLong(), hash.getLong());
	}
}
