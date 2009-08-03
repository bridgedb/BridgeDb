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

package org.bridgedb.webservice.biomart;

/**
 * Dataset, corresponding to dataset in BioMart.
 * @author gjj
 */
public class Dataset {
    private String name;
    private String displayName;
    private Database database;

    /**
     *
     * @param name dataset name
     * @param displayName dataset display name
     * @param database database/mart of the dataset
     */
    public Dataset(String name, String displayName, Database database) {
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
    public String getDisplyName() {
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
        return getDisplyName();
    }

}
