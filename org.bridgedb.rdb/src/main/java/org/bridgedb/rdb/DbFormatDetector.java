// Copyright 2026 Egon Willighagen <egonw@users.sf.net>
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
package org.bridgedb.rdb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.bridgedb.IDMapperException;

/**
 * Detects whether a BridgeDb database file was produced by Derby (DataDerby) or
 * DuckDB (DataDuckDB), by sniffing its magic bytes. Both formats commonly use the
 * ".bridge" extension, so the file name alone doesn't tell them apart.
 * <p>
 * This class only reads bytes, and needs neither the Derby nor the DuckDB JDBC driver.
 */
public final class DbFormatDetector
{
	/** The database formats that can be detected. */
	public enum Format { DERBY, DUCKDB }

	/** prevent instantiation of utility class. */
	private DbFormatDetector() {}

	/**
	 * Derby databases, as produced by DataDerby, are a plain zip archive, recognizable
	 * by the standard zip local-file-header signature "PK\3\4" at offset 0.
	 * DuckDB databases, as produced by DataDuckDB, carry the ASCII magic "DUCK" at
	 * byte offset 8 of their main header block.
	 * @param f the database file to inspect
	 * @return the detected format
	 * @throws IDMapperException if the file could not be read, or its format not recognized
	 */
	public static Format detect(File f) throws IDMapperException
	{
		byte[] header = new byte[12];
		try (RandomAccessFile raf = new RandomAccessFile(f, "r"))
		{
			raf.readFully(header);
		}
		catch (IOException e)
		{
			throw new IDMapperException("Could not read database file '" + f + "' to detect its format", e);
		}

		if (header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04)
			return Format.DERBY;

		if (header[8] == 'D' && header[9] == 'U' && header[10] == 'C' && header[11] == 'K')
			return Format.DUCKDB;

		throw new IDMapperException("Could not recognize the database format of '" + f +
			"' (expected a Derby or DuckDB BridgeDb database)");
	}
}
