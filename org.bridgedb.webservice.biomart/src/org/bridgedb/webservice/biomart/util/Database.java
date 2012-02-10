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
 * Database, corresponding to database/mart in BioMart.
 * @author gjj
 */
public class Database {
    private String dbname;
    private Map<String, String> param;

    // cache for getAvailableDatasets()
    private Map<String,Dataset> datasets = null;

    /**
     * Get available datasets of a mart/database.
     * @return {@link Vector} of available datasets
     * @throws IOException if failed to read
     */
    public Map<String, Dataset> getAvailableDatasets()
            throws IOException 
    {
    	if (datasets != null) return datasets;

    	//TODO: not sure why this is needed
//        try {
//            getRegistry();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        }

        datasets = new HashMap<String, Dataset>();

        Map<String, String> detail = getParam();

        String urlStr = "http://" + detail.get("host") + ":" + detail.get("port")
                        + detail.get("path") + "?type=datasets&mart=" + detail.get("name");
        //System.out.println("DB name = " + martName + ", Target URL = " + urlStr + "\n");

        URL url = new URL(urlStr);
        InputStream is = InternalUtils.getInputStream(url);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;

        String[] parts;

        while ((s = reader.readLine()) != null) {
            parts = s.split("\\t");

            if ((parts.length > 4) && parts[3].equals("1")) {
                Dataset dataset = new Dataset(parts[1], parts[2], this);
                datasets.put(dataset.getName(),dataset);
                //datasourceMap.put(parts[1], martName);
                datasets.put(parts[1], dataset);
            }
        }

        is.close();
        reader.close();
        reader = null;
        is = null;

        return datasets;
    }

    /**
     * look up a dataset by name.
     * @param id the name of the dataset to look up
     * @return the specified Dataset
     * @throws IOException if the mart could not be accessed.
     */
    public Dataset getDataset (String id) throws IOException
    {
    	return getAvailableDatasets().get(id);
    }
    
    /**
     *
     * @param dbname database name
     * @param param database parameters
     */
    public Database(String dbname, Map<String, String> param) {
        this.dbname = dbname;
        this.param = param;
    }

    /**
     *
     * @return database name
     */
    public String getName() {
        return dbname;
    }

    /**
     *
     * @return database parameters
     */
    public Map<String, String> getParam() {
        return param;
    }

    /**
     *
     * @return true if visible; false otherwise
     */
    public boolean visible() {
        return param.get("visible").equals("1");
    }

    /**
     *
     * @return database display name
     */
    public String displayName() {
        return param.get("displayName");
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return displayName();
    }
}
