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

package org.bridgedb.rdb;

import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * Interface for constructing a gene database.
 */
public interface GdbConstruct 
{

	/**
	 * Add a biological entity to the database.
	 * @param ref entity to add
	 * @param bpText html-formatted description
	 * @return 1 if addition was successful, 0 otherwise. 
	 */
	public int addGene(Xref ref, String bpText); 

	/**
	 * Add an attribute for a biological entity to the database.
	 * @param ref entity to add
	 * @param attr key
	 * @param val value
	 * @return 1 if addition was successful, 0 otherwise. 
	 */
    public int addAttribute(Xref ref, String attr, String val);

	/**
	 * Add a link between two refs to the database.
	 * @param left left part of relationship
	 * @param right right part of relationship
	 * @return 1 if addition was successful, 0 otherwise. 
	 */
    public int addLink(Xref left, Xref right);
    
	/**
	   Create indices on the database
	   You can call this at any time after creating the tables,
	   but it is good to do it only after inserting all data.
	   @throws IDMapperException on failure
	 */
	public void createGdbIndices() throws IDMapperException;   
}
