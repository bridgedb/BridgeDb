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
package org.bridgedb.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;

/**
 * Class for reading ID mapping data from delimited reader.
 * @author gjj
 */
public class IDMappingReaderFromDelimitedReader implements IDMappingReader {
    private boolean transitivity;
    private List<DataSource> dataSources;
    private Map<Xref,Set<Xref>> mapXrefs;
    private List<String> data;

    protected String regExDataSourceDelimiter;
    protected String regExIDDelimiter;
    protected boolean dsValid, idMappingValid;

    /**
     *
     * @param reader a {@link Reader}
     * @param regExDataSourceDelimiter regular expression of delimiter between
     *        data sources
     * @param regExIDDelimiter regular expression of delimiter between IDs
     * @param transitivity transitivity support
     * @throws IDMapperException if failed to read
     */
    public IDMappingReaderFromDelimitedReader(final Reader reader,
            final String regExDataSourceDelimiter,
            final String regExIDDelimiter,
            final boolean transitivity) throws IDMapperException {
        if (reader==null || regExDataSourceDelimiter==null) {
            throw new java.lang.IllegalArgumentException("reader and regExDataSourceDelimiter cannot be null");
        }

        readData(reader);
        this.regExDataSourceDelimiter = regExDataSourceDelimiter;
        this.regExIDDelimiter = regExIDDelimiter;
        this.transitivity = transitivity;

        dsValid = false;
        idMappingValid = false;
    }

    /**
     * Read data.
     * @param reader to read data from
     * @throws IDMapperException when file can't be read
     */
    protected void readData(final Reader reader) throws IDMapperException {
        data = new ArrayList<String>();
        BufferedReader bfdrd = new BufferedReader(reader);
        try {
            String line = bfdrd.readLine();

            while (line!=null) {
                data.add(line);
                line = bfdrd.readLine();
            }
            
            bfdrd.close();
            reader.close();
        } catch(IOException e) {
            throw new IDMapperException(e);
        }
    }

    /**
     * Set transitivity support.
     * @param transitivity support transitivity if true.
     */
    public void setTransitivity(final boolean transitivity) {
        this.transitivity = transitivity;
    }

    /**
     * Get transitivity support.
     * @return true if support transitivity; false otherwise.
     */
    public boolean getTransitivity() {
        return transitivity;
    }

    /**
     * Set {@link DataSource}s. This will override the data sources read from
     * the delimited reader.
     * @param dataSources {@link DataSource}s
     */
    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
        dsValid = dataSources==null;
    }

    /**
     * {@inheritDoc}
     */
    public Set<DataSource> getDataSources() throws IDMapperException {
        if (!dsValid) {
            try {
                readDataSources();
            } catch(IOException ex) {
                throw new IDMapperException(ex);
            }
        }

        return new HashSet<DataSource>(dataSources);
    }

    /**
     * {@inheritDoc}
     */
    public Map<Xref,Set<Xref>> getIDMappings() throws IDMapperException {
        if (!idMappingValid) {
            try {
                readIDMappings();
            } catch(IOException ex) {
                throw new IDMapperException(ex);
            }
        }

        return mapXrefs;
    }

    /**
     * Read {@link DataSource}s from the reader.
     * @throws IOException on failing to read file
     */
    protected void readDataSources() throws IOException {
        dataSources = new ArrayList<DataSource>();

        // add data sources
        if (data.isEmpty()) {
                System.err.println("Empty file");
                return;
        }

        String[] types = data.get(0).split(regExDataSourceDelimiter);
        int nds = types.length;
        DataSource[] dss = new DataSource[nds];
        for (int ids=0; ids<nds; ids++) {
            String type = types[ids];
            if (type.length()==0) {//TODO: how to deal with consecutive Delimiters
                return;
            }

            if (DataSource.fullNameExists(type)) {
                dss[ids] = DataSource.getExistingByFullName(type);
            } else {
            	dss[ids] = DataSource.register("ds" + type, type).asDataSource();
            }
            dataSources.add(dss[ids]);
        }

        dsValid = true;
    }
    
    /**
     * Read ID mappings from the reader.
     * @throws IOException on file read error
     */
    protected void readIDMappings() throws IOException {
        mapXrefs = new HashMap<Xref, Set<Xref>>();

        int nline = data.size();
        if (nline<2) {
                System.err.println("No ID mapping data");
                return;
        }

        // read each ID mapping (line)
        for (int iline=1; iline<nline; iline++) {
            String line = data.get(iline);
            String[] strs = line.split(regExDataSourceDelimiter);
            if (strs.length>dataSources.size()) {
                    System.err.println("The number of ID is larger than the number of types at row "+iline);
                    //continue;
            }

            int n = Math.min(strs.length, dataSources.size());

            Set<Xref> xrefs = new HashSet<Xref>();

            for (int i=0; i<n; i++) {
                String str = strs[i];
                if (regExIDDelimiter==null) {
                    xrefs.add(new Xref(str, dataSources.get(i)));
                } else {
                    String[] ids = str.split(regExIDDelimiter);
                    for (String id : ids) {
                        xrefs.add(new Xref(id, dataSources.get(i)));
                    }
                }
            }

            addIDMapping(xrefs);
        }

        idMappingValid = true;
    }
    
    /**
     * Add matched references.
     * @param xrefs matched references
     */
    protected void addIDMapping(final Set<Xref> xrefs) {
        if (xrefs==null) {
            throw new NullPointerException();
        }

        if (transitivity) {
            Set<Xref> newXrefs = new HashSet<Xref>(xrefs);

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
            for (Xref ref : xrefs) 
            {
        		InternalUtils.multiMapPutAll(mapXrefs, ref, xrefs);
            }
        }
    }
}
