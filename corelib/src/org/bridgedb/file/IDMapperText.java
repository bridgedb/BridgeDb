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
import java.util.HashSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			// parse arguments to determine idsep and dssep
            // sample: dssep:\t,idsep:;,idsep:,@file:/localfile.txt
            // \t represents tab, \@ represents @
            String  path = null;
            char[] dssep = null;
            char[] idsep = null;

            int idx = location.indexOf("@");
            if (idx<=0) {
                path = location;
                // defaults if no options are provided:
                idsep = new char[] { ',' };
                dssep = new char[] { '\t' };
            } else {
                // Is an url always contains :/ ?
                if (idx > location.indexOf(":/")) { //@ is part of the path
                    path = location;
                } else {
                    while (idx>0 && location.charAt(idx-1)=='\\') { //escape \@
                        idx = location.indexOf(idx+1);
                    }

                    if (idx==-1) {
                        throw new IDMapperException("Wrong link format!");
                    }

                    if (idx==location.length()-1) {
                        throw new IDMapperException("Empty address!");
                    }

                    path = location.substring(idx+1);

                    String config = location.substring(0, idx)+",";
                    dssep = parseConfig(config, "dssep");
                    idsep = parseConfig(config, "idsep");
                }
            }

			try
			{
				return new IDMapperText(new URL(path), dssep, idsep);
			}
			catch (MalformedURLException ex)
			{
				throw new IDMapperException(ex);
			}
		}

        private char[] parseConfig(String config, String head) {
            Set<Character> delimiters = new HashSet();
            Pattern p = Pattern.compile(head+":(.|\\t|\\@),", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(config);
            while (m.find()) {
                String sep = m.group(1);
                if (sep.equalsIgnoreCase("\\t")) {
                    sep = "\t";
                } else if (sep.equalsIgnoreCase("\\@")) {
                    sep = "@";
                }

                delimiters.add(sep.charAt(0));
            }

            int nsep = delimiters.size();
            if (nsep==0) {
                return null;
            }

            char[] ret = new char[nsep];
            int isep = 0;
            for (char c : delimiters) {
                ret[isep++] = c;
            }

            return ret;
        }
        
	}

    protected final URL url;
    protected char[] dataSourceDelimiters;
    protected char[] idDelimiters;


    public IDMapperText(final URL url) throws IDMapperException {
        this(url,  new char[] {'\t'}); // default is tab delimited
    }

    public IDMapperText(final URL url,
            final char[] dataSourceDelimiters) throws IDMapperException {
        this(url, dataSourceDelimiters, null);
    }

    public IDMapperText(final URL url,
            final char[] dataSourceDelimiters,
            final char[] idDelimiters) throws IDMapperException {
        this(url, dataSourceDelimiters, idDelimiters, false);
    }

    public IDMapperText(final URL url,
            final char[] dataSourceDelimiters,
            final char[] idDelimiters,
            final boolean transitivity) throws IDMapperException {
        super(new IDMappingReaderFromText(url,
                dataSourceDelimiters,
                idDelimiters),
              transitivity);
        this.url = url;
        this.dataSourceDelimiters = dataSourceDelimiters;
        this.idDelimiters = idDelimiters;
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

    @Override
    public void setTransitivity(final boolean transitivity) {
        super.setTransitivity(transitivity);
        ((IDMappingReaderFromText)reader).setTransitivity(transitivity);
    }

    public URL getURL() {
        return url;
    }

    public char[] getDataSourceDelimiters() {
        return dataSourceDelimiters;
    }

    public char[] getIDDelimiters() {
        return idDelimiters;
    }

    public void setDataSourceDelimiters(final char[] dataSourceDelimiters) throws IDMapperException {
        ((IDMappingReaderFromText)this.getIDMappingReader()).
                setDataSourceDelimiters(dataSourceDelimiters);
        this.dataSourceDelimiters = dataSourceDelimiters;
    }

    public void setIDDelimiters(final char[] idDelimiters) throws IDMapperException {
        ((IDMappingReaderFromText)this.getIDMappingReader())
                .setIDDelimiters(idDelimiters);
        this.idDelimiters = idDelimiters;
    }
}
