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
package org.bridgedb;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of IDMapperCapabilities,
 * which assumes that the supported datasources are the
 * same for target and source, and that the supported
 * data sources and properties are static and known at creation
 * time.
 */
public abstract class AbstractIDMapperCapabilities implements
		IDMapperCapabilities {

	/** {@inheritDoc} */
	public Set<String> getKeys() 
	{
		return properties.keySet();
	}

	/** {@inheritDoc} */
	public String getProperty(String key) 
	{
		return properties.get(key);
	}

	private final Set<DataSource> supportedDataSources;
	private final Map<String, String> properties;
	private final boolean freeSearchSupported;
	
	/**
	 * @param supportedDataSources Supported DataSources. IDMappers
	 * are usually symmetrical, so in the default implementation
	 * supportedDataSources is both target and source DataSources.
	 * @param props properties, may be null.
	 * @param freeSearchSupported if free search is supported or not
	 */
	public AbstractIDMapperCapabilities(Set<DataSource> supportedDataSources, boolean freeSearchSupported, Map<String, String> props) 
	{
		this.supportedDataSources = supportedDataSources;
		this.freeSearchSupported = freeSearchSupported;
		if (props == null)
		{
			properties = Collections.emptyMap();
		}
		else
		{
			properties = props;
		}
	}
	
	/** {@inheritDoc} */
	public Set<DataSource> getSupportedSrcDataSources()
			throws IDMapperException {
		return supportedDataSources;
	}

	/** {@inheritDoc} */
	public Set<DataSource> getSupportedTgtDataSources()
			throws IDMapperException {
		return supportedDataSources;
	}

        /** {@inheritDoc} */
        public boolean isMappingSupported(DataSource src, DataSource tgt)
			throws IDMapperException  {
            return getSupportedSrcDataSources().contains(src)
                    && getSupportedTgtDataSources().contains(tgt);
        }

	/** {@inheritDoc} */
	public boolean isFreeSearchSupported() 
	{
		return freeSearchSupported;
	}
}
