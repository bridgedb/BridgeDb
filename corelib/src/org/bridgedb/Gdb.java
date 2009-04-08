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

import java.util.List;

/**
 * Interface for all classes that provide Gdb-like functionality,
 * such as looking up cross-references and backpage text.
 */
public interface Gdb 
{
	/**
	 * Check whether a connection to the database exists
	 * @return	true is a connection exists, false if not
	 * 
	 * A connection will not exist only 
	 * after the close() method is called.
	 * implementing classes should create a connection in
	 * the constructor, and throw an exception at that moment
	 * if a connection is not possible.
	 */
	public boolean isConnected();

	/**
	 * Gets the name of te currently used gene database
	 * @return the database name as specified in the connection string
	 */
	public String getDbName();

	/**
	 * <TABLE border='1'><TR><TH>Gene ID:<TH>g4507224_3p_at<TR><TH>Gene Name:<TH>SRY<TR><TH>Description:<TH>Sex-determining region Y protein (Testis-determining factor). [Source:Uniprot/SWISSPROT;Acc:Q05066]<TR><TH>Secondary id:<TH>g4507224_3p_at<TR><TH>Systemcode:<TH>X<TR><TH>System name:<TH>Affymetrix Probe Set ID<TR><TH>Database name (Ensembl):<TH>Affymx Microarray U133</TABLE>
	 * @param id The gene id to get the symbol info for
	 * @param code systemcode of the gene identifier
	 * @return The gene symbol, or null if the symbol could not be found
	 * @throws DataException 
	 */
	@Deprecated // this method is currently unused
	public String getGeneSymbol(Xref ref) throws DataException;

	/**
	 * Gets the backpage info for the given gene id for display on BackpagePanel
	 * @param ref The gene to get the backpage info for
	 * @return String with the backpage info, null if the gene was not found
	 */
	public String getBpInfo(Xref ref) throws DataException;

	/**
	 * Checks whether the given reference exists in the database
	 * @param xref The reference to check
	 * @return true if the reference exists, false if not
	 */
	public boolean xrefExists(Xref xref) throws DataException;
	
	/**
	 * Get all cross-references for the given id/code pair, restricting the
	 * result to contain only references from database with the given system
	 * code
	 * @param idc The id/code pair to get the cross references for
	 * @return An {@link ArrayList} containing the cross references, or an empty
	 * ArrayList when no cross references could be found
	 */
	public List<Xref> getCrossRefs(Xref idc) throws DataException;

	/**
	 * Get all cross-references for the given id/code pair, restricting the
	 * result to contain only references from database with the given system
	 * code
	 * @param idc The id/code pair to get the cross references for
	 * @param resultCode The system code to restrict the results to
	 * @return An {@link ArrayList} containing the cross references, or an empty
	 * ArrayList when no cross references could be found
	 */
	public List<Xref> getCrossRefs (Xref idc, DataSource resultDs) throws DataException;

	/**
	 * Get a list of cross-references for the given attribute name/value pair. This
	 * can be used to e.g. get the xref for a gene symbol.
	 * @param attrName	The attribute name (e.g. 'Symbol')
	 * @param attrValue	The attribute value (e.g. 'TP53')
	 * @return A list with the cross-references that have this attribute name/value, or an
	 * empty list if no cross-references could be found for this attribute name/value.
	 */
	public List<Xref> getCrossRefsByAttribute(String attrName, String attrValue) throws DataException;

	/**
	 * Closes the {@link Connection} to the Gene Database if possible
	 * @throws DataException 
	 */
	public void close() throws DataException;

	/**
	 * Get up to limit suggestions for a symbol autocompletion
	 */
	public List<String> getSymbolSuggestions(String text, int limit) throws DataException;

	
	/**
	 * Get up to limit suggestions for a identifier autocompletion
	 */
	public List<Xref> getIdSuggestions(String text, int limit) throws DataException;
	
	/**
	 * free text search for matching symbols or identifiers
	 * @throws DataException 
	 */
	public List<XrefWithSymbol> freeSearch (String text, int limit) throws DataException;
}
