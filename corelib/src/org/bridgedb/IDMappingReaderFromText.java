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

package org.bridgedb;

import java.util.Set;
import java.util.HashSet;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;

import java.net.URLConnection;
import java.net.URL;

/**
 * Class for reading ID mapping data from delimited text file
 * @author gjj
 */
public class IDMappingReaderFromText extends IDMappingReaderFromFile {
    private static int msConnectionTimeout = 2000;

    protected final String regExDataSourceDelimiter;
    protected final String regExIDDelimiter;

    public IDMappingReaderFromText(final URL url,
            final String regExDataSourceDelimiter,
            final String regExIDDelimiter) {
        super(url);

        if (regExDataSourceDelimiter==null || regExIDDelimiter==null) {
            throw new NullPointerException();
        }
        this.regExDataSourceDelimiter = regExDataSourceDelimiter;
        this.regExIDDelimiter = regExIDDelimiter;
    }

    public IDMappingReaderFromText(final URL url,
            final String[] dataSourceDelimiters,
            final String[] regExIDDelimiter) {
        this(url, strs2regex(dataSourceDelimiters), strs2regex(regExIDDelimiter));
    }

    private static String strs2regex(final String[] strs) {
        if (strs==null) {
            throw new NullPointerException();
        }
        
        StringBuilder regex = new StringBuilder();
        int n = strs.length;
        if (n>0) {
            regex.append(strs[0]);
            for (int i=1; i<n; i++) {
                regex.append("|"+strs[i]);
            }
        }

        return regex.toString();
    }
    
    /**
     *
     * @throws IDMapperException if failed
     */
    public void read() throws IDMapperException {
        try {
            InputStream inputStream = getInputStream(url);
            Reader fin = new InputStreamReader(inputStream);
            BufferedReader bufRd = new BufferedReader(fin);

            // add data sources
            String line = bufRd.readLine();
            if (line==null) {
                    System.err.println("Empty file");
                    return;
            }

            String[] types = line.split(regExDataSourceDelimiter);
            int nds = types.length;
            DataSource[] dss = new DataSource[nds];
            for (int ids=0; ids<nds; ids++) {
                String type = types[ids];
                if (type.length()==0) {//TODO: how to deal with consecutive Delimiters
                    return;
                }

                dss[ids] = DataSource.getByFullName(type);
                dataSources.add(dss[ids]);
            }

            // read each ID mapping (line)
            int lineCount = 1;
            while ((line=bufRd.readLine())!=null) {
                    lineCount++;
                    String[] strs = line.split(regExDataSourceDelimiter);
                    if (strs.length>types.length) {
                            System.err.println("The number of ID is larger than the number of types at row "+lineCount);
                            //continue;
                    }

                    int n = Math.min(strs.length, types.length);

                    Set<Xref> xrefs = new HashSet();

                    for (int i=0; i<n; i++) {
                        String str = strs[i];
                        String[] ids = str.split(regExIDDelimiter);
                        for (String id : ids) {
                            xrefs.add(new Xref(id, dss[i]));
                        }
                    }

                    addIDMapping(xrefs);
            }

            bufRd.close();
            fin.close();
        } catch(java.io.IOException ex) {
            throw new IDMapperException(ex);
        }
    }

    private static InputStream getInputStream(URL source) throws IOException {
		URLConnection uc = source.openConnection();
		uc.setUseCaches(false); // don't use a cached page
		uc.setConnectTimeout(msConnectionTimeout); // set timeout for connection
        return uc.getInputStream();
    }
}
