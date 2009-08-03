// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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
package org.bridgedb.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;
import java.net.URLConnection;

import org.bridgedb.IDMapperException;


/**
 * Class for reading ID mapping data from delimited text file.
 * @author gjj
 */
public class IDMappingReaderFromText extends IDMappingReaderFromDelimitedReader {
    
    protected final URL url;

    /**
     * Transitivity is unsupported. No delimiter between IDs.
     * @param url url {@link URL} of the file
     * @param dataSourceDelimiters delimiters between data sources
     * @throws IDMapperException if failed to read file
     */
    public IDMappingReaderFromText(final URL url,
            final char[] dataSourceDelimiters) throws IDMapperException {
        this(url, dataSourceDelimiters, null);
    }

    /**
     * Transitivity is unsupported.
     * @param url url {@link URL} of the file
     * @param dataSourceDelimiters delimiters between data sources
     * @param idDelimiters delimiters between IDs
     * @throws IDMapperException if failed to read file
     */
    public IDMappingReaderFromText(final URL url,
            final char[] dataSourceDelimiters,
            final char[] regExIDDelimiter) throws IDMapperException {
        this(url, dataSourceDelimiters, regExIDDelimiter, false);
    }

    /**
     * Constructor from the {@link URL} of a tab-delimited text file,
     * delimiters to separate between different data sources and IDs and
     * transitivity support.
     * @param url url {@link URL} of the file
     * @param dataSourceDelimiters delimiters between data sources
     * @param idDelimiters delimiters between IDs
     * @param transitivity support transitivity if true
     * @throws IDMapperException if failed to read file
     */
    public IDMappingReaderFromText(final URL url,
            final char[] dataSourceDelimiters,
            final char[] regExIDDelimiter,
            final boolean transitivity) throws IDMapperException {
        this(url, strs2regex(dataSourceDelimiters), strs2regex(regExIDDelimiter), transitivity);
    }

    /**
     * Transitivity is unsupported. No delimiters between IDs.
     * @param url the {@link URL} of the delimited text file
     * @param regExDataSourceDelimiter regular expression of delimiter between
     *        data sources
     * @throws IDMapperException if failed to read
     */
    public IDMappingReaderFromText(final URL url,
            final String regExDataSourceDelimiter) throws IDMapperException {
        this(url, regExDataSourceDelimiter, null);
    }

    /**
     * Transitivity is unsupported.
     * @param url the {@link URL} of the delimited text file
     * @param regExDataSourceDelimiter regular expression of delimiter between
     *        data sources
     * @param regExIDDelimiter regular expression of delimiter between IDs
     * @throws IDMapperException if failed to read
     */
    public IDMappingReaderFromText(final URL url,
            final String regExDataSourceDelimiter,
            final String regExIDDelimiter) throws IDMapperException {
        this(url, regExDataSourceDelimiter, regExIDDelimiter, false);
    }

    /**
     *
     * @param url the {@link URL} of the delimited text file
     * @param regExDataSourceDelimiter regular expression of delimiter between
     *        data sources
     * @param regExIDDelimiter regular expression of delimiter between IDs
     * @param transitivity transitivity support
     * @throws IDMapperException if failed to read
     */
    public IDMappingReaderFromText(final URL url,
            final String regExDataSourceDelimiter,
            final String regExIDDelimiter,
            final boolean transitivity) throws IDMapperException {
        super(getReader(url), regExDataSourceDelimiter,
                regExIDDelimiter, transitivity); 
        
        this.url = url;
    }

    /**
     * Set delimiters between data sources.
     * @param dataSourceDelimiters delimiters between data sources
     */
    public void setDataSourceDelimiters(final char[] dataSourceDelimiters) {
        dsValid = false;
        idMappingValid = false;
        regExDataSourceDelimiter = strs2regex(dataSourceDelimiters);
    }

    /**
     * Set delimiters between data IDs.
     * @param idDelimiters delimiters between data IDs
     */
    public void setIDDelimiters(final char[] idDelimiters) {
        idMappingValid = false;
        regExIDDelimiter = strs2regex(idDelimiters);
    }

    protected static String strs2regex(final char[] chs) {
        if (chs==null || chs.length==0) {
            return null;
        }

        StringBuilder regex = new StringBuilder();
        int n = chs.length;
        if (n>0) {
            regex.append("[");
            for (int i=0; i<n; i++) {
                regex.append("\\0"+Integer.toOctalString(chs[i]));
            }
            regex.append("]");
        }

        return regex.toString();
    }

    private static Reader getReader(URL url) throws IDMapperException {
        try {
            InputStream inputStream = getInputStream(url);
            return new InputStreamReader(inputStream);
        } catch(IOException e) {
            throw new IDMapperException(e);
        }
    }

    private static final int msConnectionTimeout = 2000;
    //TODO: test when IOException is throwed
    protected static InputStream getInputStream(URL source) throws IOException {
        InputStream stream = null;
        int expCount = 0;
        int timeOut = msConnectionTimeout;
        while (true) { // multiple chances
            try {
                URLConnection uc = source.openConnection();
                uc.setUseCaches(false); // don't use a cached page
                uc.setConnectTimeout(timeOut); // set timeout for connection
                stream = uc.getInputStream();
                break;
            } catch (IOException e) {
                if (expCount++==4) {
                    throw(e);
                } else {
                    timeOut *= 2;
                }
            }
        }

        return stream;
    }

}
