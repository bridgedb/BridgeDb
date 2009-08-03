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

import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;


/**
 * Interface for reading ID mapping data.
 * 
 */
public interface IDMappingReader {

    /**
     * Get {@link DataSource}s from the file.
     * @return {@link DataSource}s from the file
     * @throws IDMapperException if failed to read the file
     */
    public Set<DataSource> getDataSources() throws IDMapperException;

    /**
     * Get {@link Xref}s from the file.
     * @return Map from a {@link Xref} to the {@link Set} of all its matched
     * Xref.
     * @throws IDMapperException if failed to read the file
     */
    public Map<Xref,Set<Xref>> getIDMappings() throws IDMapperException;

}
