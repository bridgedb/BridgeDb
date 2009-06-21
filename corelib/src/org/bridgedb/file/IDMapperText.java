// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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

package org.bridgedb.file;

import java.util.Set;

import java.net.MalformedURLException;
import java.net.URL;

import org.bridgedb.BridgeDb;
import org.bridgedb.Driver;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * Class for mapping ID from delimited text file
 * @author gjj
 */
public class IDMapperText extends IDMapperFile 
{
	static
	{
		BridgeDb.register ("idmapper-text", new Driver());
	}
	
	private static class Driver implements org.bridgedb.Driver
	{
		private Driver() {} // prevent outside instantiation;
		
		public IDMapper connect(String location) throws IDMapperException
		{
			//TODO: parse arguments to determine idsep and dssep
			try
			{
				return new IDMapperText(new URL(location), 
						new String[] { "\t" }, 
						new String[] { "," });
			}
			catch (MalformedURLException ex)
			{
				throw new IDMapperException(ex);
			}
		}
	}
	
    public IDMapperText(final URL url,
            final String[] dataSourceDelimiters,
            final String[] regExIDDelimiter) {
        super(new IDMappingReaderFromText(url,
                dataSourceDelimiters,
                regExIDDelimiter));
    }

    /**
     * Free search is not supported for now.
     * @param text text to search
     * @param limit up limit of number of hits
     * @return a set of hit references
     * @throws IDMapperException if failed
     * @throws UnsupportedOperationException if free search is not supported.
     */
    public Set<Xref> freeSearch (String text, int limit) throws IDMapperException {
        throw new UnsupportedOperationException();
    }
}
