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

/**
 * A Driver knows how to create an IDMapper instance.
 * The connect method should not be called directly, instead
 * use BridgeDb.connect() to automatically pick
 * the correct driver for a protocol.
 */
public interface Driver 
{
	/**
	 * Never call this method directly, use {@link BridgeDb.connect()} instead.
	 * This method interprets the location part of the connection string and uses
	 * that to configure an {@link IDMapper}.
	 * @return a new instance of the correct
	 * 	IDMapper implementation every time, configured according to the locationString.
	 * @param locationString string with all necessary information to configure the resource. 
	 * 	e.g. this could contain a URL or file location with optional parameters at the end.
	 * @throws IDMapperException when a connection to the resource could not be created, or
	 *  the IDMapper implementation could not be instantiated for any reason. 
	 */
	IDMapper connect (String locationString) throws IDMapperException;
}
