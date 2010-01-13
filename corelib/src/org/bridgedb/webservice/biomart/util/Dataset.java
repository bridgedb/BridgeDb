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

package org.bridgedb.webservice.biomart.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.bridgedb.impl.InternalUtils;

/**
 * Dataset, corresponding to dataset in BioMart.
 * @author gjj
 */
public class Dataset 
{
    private String name;
    private String displayName;
    private Database database;

    // cache for getFilters()
    private Map<String, Filter> filters = null;

    // cache for getAttributes()
    private Map<String, Attribute> attributes = null;

    /**
     * Parse a line of Biomart output and construct a Filter based on that.
     * @param line line to parse
     * @return a Filter, or null if the line is not correctly formatted, or it 
     *  	does not meet the criteria for a valid Filter (e.g. second column has to contain "ID(s)")
     */
    private static Filter parseFilter(String line)
    {
    	String[] parts;
        parts = line.split("\\t");

        if ((parts.length > 1)) {
            if ((parts[1].contains("ID(s)")
                        || parts[1].contains("Accession(s)")
                        || parts[1].contains("IDs"))
                     && (parts[0].startsWith("with_") == false)
                     && (parts[0].endsWith("-2") == false)
                     || parts.length>6
                     && parts[5].equals("id_list")) {
                return new Filter(parts[0], parts[1]);
            }
        }
        return null;
    }

    /**
     * Get filters for a dataset. Result will be cached.
     * @return filters
     * @throws IOException if failed to read
     */
    public Map<String, Filter> getFilters()
        throws IOException 
    {
    	filters = new HashMap<String, Filter>();
        Database database = getDatabase();

        Map<String, String> detail = database.getParam();
        String urlStr = "http://" 
                        + detail.get("host") + ":"
                        + detail.get("port")
                        + detail.get("path")
                        + "?virtualschema="
                        + detail.get("serverVirtualSchema")
                        + "&type=filters&dataset="
                        + name;

        //System.out.println("Dataset name = " + datasetName + ", Target URL = "
        //                      + urlStr + "\n");
        URL url = new URL(urlStr);
        InputStream is = InternalUtils.getInputStream(url);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = reader.readLine()) != null) 
        {
        	Filter f = parseFilter (s);
        	if (f != null) filters.put (f.getName(), f);
        }

        is.close();
        reader.close();
        reader = null;
        is = null;

        return filters;
    }
    
    /**
     * Get attributes. Result will be cached.
     * @return Map of attribute name to attributes
     * @throws IOException if failed to read
     */
    public Map<String, Attribute> getAttributes() throws IOException 
    {
    	if (attributes != null) return attributes;
        attributes = new HashMap<String, Attribute>();

        Database database = getDatabase();

        Map<String, String> detail = database.getParam();

        String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
                        + detail.get("path") + "?virtualschema="
                        + detail.get("serverVirtualSchema") + "&type=attributes&dataset="
                        + name;

        System.out.println (urlStr);
        
        URL url = new URL(urlStr);
        InputStream is = InternalUtils.getInputStream(url);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;

        String displayName;

        String[] parts;

        while ((s = reader.readLine()) != null) {

            String page = "unknown";
            String description;
            parts = s.split("\\t");

            if (parts.length == 0)
                continue;

            if (parts.length == 4) {
                displayName = parts[3] + ": " + parts[1];
                description = displayName;
            } else if (parts.length > 1) {
                displayName = parts[1];
                description = parts[2];
                page = parts[3];
            } else {
                displayName = "";
                description = "";
            }
            
            // only add attributes listed as "feature_page"
            if ("feature_page".equals(page))
            	attributes.put(parts[0], new Attribute(
            		parts[0], displayName, description, page));
        }

        is.close();
        reader.close();
        reader = null;
        is = null;

        return attributes;
    }    

    public Attribute getAttribute(String id) throws IOException
    {
    	return getAttributes().get(id);
    }
  
    /**
     *
     * @param name dataset name
     * @param displayName dataset display name
     * @param database database/mart of the dataset
     */
    public Dataset(String name, String displayName, Database database) 
    {
		if (name == null) throw new NullPointerException("name may not be null");
    	this.name = name;
        this.displayName = displayName;
        this.database = database;
    }

    /**
     *
     * @return dataset name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return dataset display name
     */
    public String displayName() {
        return displayName;
    }

    /**
     *
     * @return database/mart which this dataset belongs to
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return displayName();
    }

}
