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
import java.util.Map;
import java.util.HashMap;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;

import java.net.URLConnection;
import java.net.URL;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * Class for reading ID mapping data from delimited text file
 * @author gjj
 */
public class IDMappingReaderFromText implements IDMappingReader {
    private static int msConnectionTimeout = 2000;

    protected final URL url;
    protected boolean transitivity;
    protected String regExDataSourceDelimiter;
    protected String regExIDDelimiter;
    protected Set<DataSource> dataSources;
    protected Map<Xref,Set<Xref>> mapXrefs;

    protected boolean dsValid, idMappingValid;

    public IDMappingReaderFromText(final URL url) {
        this(url, new char[] {'\t'}); //tab-delimited as default
    }

    public IDMappingReaderFromText(final URL url,
            final char[] dataSourceDelimiters) {
        this(url, dataSourceDelimiters, null);
    }

    public IDMappingReaderFromText(final URL url,
            final char[] dataSourceDelimiters,
            final char[] regExIDDelimiter) {
        this(url, dataSourceDelimiters, regExIDDelimiter, false);
    }

    public IDMappingReaderFromText(final URL url,
            final char[] dataSourceDelimiters,
            final char[] regExIDDelimiter,
            final boolean transitivity) {
        this(url, strs2regex(dataSourceDelimiters), strs2regex(regExIDDelimiter), transitivity);
    }

    public IDMappingReaderFromText(final URL url,
            final String regExDataSourceDelimiter) {
        this(url, regExDataSourceDelimiter, null);
    }

    public IDMappingReaderFromText(final URL url,
            final String regExDataSourceDelimiter,
            final String regExIDDelimiter) {
        this(url, regExDataSourceDelimiter, regExIDDelimiter, false);
    }

    public IDMappingReaderFromText(final URL url,
            final String regExDataSourceDelimiter,
            final String regExIDDelimiter,
            final boolean transitivity) {
        if (url==null || regExDataSourceDelimiter==null) {
            throw new NullPointerException();
        }
        
        this.url = url;
        this.regExDataSourceDelimiter = regExDataSourceDelimiter;
        this.regExIDDelimiter = regExIDDelimiter;
        this.transitivity = transitivity;

        dsValid = false;
        idMappingValid = false;
    }

    public void setDataSourceDelimiters(final char[] dataSourceDelimiters) {
        regExDataSourceDelimiter = strs2regex(dataSourceDelimiters);
        dsValid = false;
        idMappingValid = false;
    }

    public void setIDDelimiters(final char[] idDelimiters) {
        regExIDDelimiter = strs2regex(idDelimiters);
        idMappingValid = false;
    }

    public void setTransitivity(final boolean transitivity) {
        this.transitivity = transitivity;
    }

    public boolean getTransitivity() {
        return transitivity;
    }

    /**
     *
     * @return data sources
     */
    public Set<DataSource> getDataSources() throws IDMapperException {
        if (!dsValid) {
            readDataSources();
        }

        return dataSources;
    }

    /**
     *
     * @return ID mappings
     */
    public Map<Xref,Set<Xref>> getIDMappings() throws IDMapperException {
        if (!idMappingValid) {
            readIDMappings();
        }

        return mapXrefs;
    }

    private static String strs2regex(final char[] chs) {
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

    /**
     *
     * @throws IDMapperException if failed
     */
    protected void readDataSources() throws IDMapperException {
        dataSources = new HashSet();
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

            dsValid = true;

            bufRd.close();
            fin.close();
        } catch(java.io.IOException ex) {
            throw new IDMapperException(ex);
        }
    }
    
    /**
     *
     * @throws IDMapperException if failed
     */
    protected void readIDMappings() throws IDMapperException {
        dataSources = new HashSet();
        mapXrefs = new HashMap();

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

            dsValid = true;

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
                        if (regExIDDelimiter==null) {
                            xrefs.add(new Xref(str, dss[i]));
                        } else {
                            String[] ids = str.split(regExIDDelimiter);
                            for (String id : ids) {
                                xrefs.add(new Xref(id, dss[i]));
                            }
                        }
                    }

                    addIDMapping(xrefs);
            }

            idMappingValid = true;

            bufRd.close();
            fin.close();
        } catch(java.io.IOException ex) {
            throw new IDMapperException(ex);
        }
    }

    protected static InputStream getInputStream(URL source) throws IOException {
		URLConnection uc = source.openConnection();
		uc.setUseCaches(false); // don't use a cached page
		uc.setConnectTimeout(msConnectionTimeout); // set timeout for connection
        return uc.getInputStream();
    }

    /**
     *
     * @param xrefs matched references
     */
    protected void addIDMapping(final Set<Xref> xrefs) {
        if (xrefs==null) {
            throw new NullPointerException();
        }

        if (transitivity) {
            Set<Xref> newXrefs = new HashSet(xrefs);

            for (Xref xref : xrefs) {
                Set<Xref> oldXrefs = mapXrefs.get(xref);
                if (oldXrefs!=null) {
                    newXrefs.addAll(oldXrefs); // merge
                }
            }

            for (Xref xref : newXrefs) {
                mapXrefs.put(xref, newXrefs);
            }
        } else {
            for (Xref xref : xrefs) {
                Set<Xref> oldXrefs = mapXrefs.get(xref);
                if (oldXrefs==null) {
                    oldXrefs = new HashSet();
                    mapXrefs.put(xref, oldXrefs);
                }

                oldXrefs.addAll(xrefs);
            }
        }


        

    }
}
