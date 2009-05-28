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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
contains information about a certain DataSource, such as
<ul>
<li>It's full name ("Ensembl")
<li>It's system code ("En")
<li>It's main url ("http://www.ensembl.org")
<li>Id-specific url's ("http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=" + id)
</ul>
The DataSource class uses the extensible enum pattern.
You can't instantiate DataSources directly, instead you have to use one of
the constants such as DataSource.ENSEMBL, or 
the "getBySystemcode" or "getByFullname" methods.
These methods return a predefined DataSource object if it exists.
If a predefined DataSource for a requested SystemCode doesn't exists,
a new one springs to life automatically. This can be used 
when the user requests new, unknown data sources. If you call
getBySystemCode twice with the same argument, it is guaranteed
that you get the same return object. However, there is no way
to combine a new DataSource with a new FullName unless you use 
the "register" method.
<p>
This way any number of pre-defined DataSources can be used, 
but plugins can define new ones and you can
handle unknown systemcodes that occur in Gpml in the same 
way as predefined ones.
<p>
PathVisio should never have to refer to system codes as Strings, except
<ul>
<li>in low level SQL code dealing with Gdb's, Gex and MAPP's (MAPPFormat.java)
<li>in low level GPML code (GPMLFormat.java)
</ul>
The preferred way to refer to a specific database is using a 
constant defined here, e.g. "DataSource.ENSEMBL"
<p>
TODO: The definitions for the individual DataSources will move to the org.bridgedb.bio package in the future.
*/
public final class DataSource
{
	private static Map<String, DataSource> bySysCode = new HashMap<String, DataSource>();
	private static Map<String, DataSource> byFullName = new HashMap<String, DataSource>();
	private static Set<DataSource> registry = new HashSet<DataSource>();
	
	private String sysCode = null;
	private String fullName = null;
	private String mainUrl = null;
	private UrlMaker urlMaker = null;
	private Object organism = null;
	private boolean isPrimary= true;
	private boolean isMetabolite = false;
	private String idExample = null;
	
	/**
	 * Constructor is private, so that we don't
	 * get any standalone DataSources.
	 * That way we can make sure that two DataSources
	 * pointing to the same datbase are really the same.
	 * 
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML
	 * @param urlMaker turns an identifier into a valid link-out.
	 * @param mainUrl url of homepage 
	 * @param organism organism for which this system code is suitable, or null for any / not applicable
	 * @param isPrimary secondary id's such as EC numbers, Gene Ontology or vendor-specific systems occur in data or linkouts,
	 * 	but their use in pathways is discouraged
	 * @param isMetabolite true if this DataSource describes metabolites 
	 * @param idExample an example id from this system
	 */
	private DataSource (String sysCode, String fullName, 
			UrlMaker urlMaker, String mainUrl,
			String idExample, boolean isPrimary, boolean isMetabolite, Object organism)
	{
		this.sysCode = sysCode;
		this.fullName = fullName;
		this.mainUrl = mainUrl;
		this.urlMaker = urlMaker;
		this.idExample = idExample;
		this.isPrimary = isPrimary;
		this.isMetabolite = isMetabolite;
		this.organism = organism;
		
		registry.add (this);
		if (sysCode != null || "".equals(sysCode))
			bySysCode.put(sysCode, this);
		if (fullName != null || "".equals(fullName));
			byFullName.put(fullName, this);
	}
	
	/** 
	 * turn id into url pointing to info page on the web, e.g. "http://www.ensembl.org/get?id=ENSG..."
	 * @param id identifier to use in url
	 * @return Url
	 */
	public String getUrl(String id)
	{
		if (urlMaker != null)
			return urlMaker.getUrl(id); 
		else 
			return null;
	}
				
	/** 
	 * returns full name of DataSource e.g. "Ensembl". 
	 * May return null if only the system code is known. 
	 * Also used as identifier in GPML
	 * @return full name of DataSource 
	 */
	public String getFullName()
	{
		return fullName;
	}
	
	/** 
	 * returns GenMAPP SystemCode, e.g. "En". May return null,
	 * if only the full name is known.
	 * Also used as identifier in
	 * <ol> 
	 * <li>Gdb databases, 
	 * <li>Gex databases.
	 * <li>Imported data
	 * <li>the Mapp format.
	 * </ol> 
	 * We should try not to use the system code anywhere outside
	 * these 4 uses.
	 * @return systemcode, a short unique code.
	 */
	public String getSystemCode()
	{
		return sysCode;
	}
	
	/**
	 * Return the main Url for this datasource,
	 * that can be used to refer to the datasource in general.
	 * (e.g. http://www.ensembl.org/)
	 * 
	 * May return null in case the main url is unknown.
	 * @return main url
	 */
	public String getMainUrl()
	{	
		return mainUrl;
	}
	
	/** 
	 * so new system codes can be added easily by 
	 * plugins. url and urlMaker may be null 
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML
	 * @param urlMaker turns an identifier into a valid link-out.
	 * @param mainUrl url of homepage 
	 * @param organism organism for which this system code is suitable, or null for any / not applicable
	 * @param isPrimary secondary id's such as EC numbers, Gene Ontology or vendor-specific systems occur in data or linkouts,
	 * 	but their use in pathways is discouraged
	 * @param isMetabolite true if this DataSource describes metabolites 
	 * @param exampleId an example id from this system
	 */
	public static DataSource register(String sysCode, String fullName, UrlMaker urlMaker, String mainUrl, 
			String exampleId, boolean isPrimary, boolean isMetabolite, Object organism)
	{
		DataSource current = null;
		if (byFullName.containsKey(fullName))
		{
			current = byFullName.get(fullName);
		}
		else if (bySysCode.containsKey(sysCode))
		{
			current = bySysCode.get(bySysCode);
		}
		
		if (current == null)
		{
			return new DataSource (sysCode, fullName, urlMaker, mainUrl, exampleId, isPrimary, isMetabolite, organism);
		}
		else
		{
			current.sysCode = sysCode;
			current.fullName = fullName;
			current.urlMaker = urlMaker;
			current.mainUrl = mainUrl;
			current.idExample = exampleId;
			current.isPrimary = isPrimary;
			current.isMetabolite = isMetabolite;
			current.organism = organism;
			if (sysCode != null || "".equals(sysCode))
				bySysCode.put(sysCode, current);
			if (fullName != null || "".equals(fullName));
				byFullName.put(fullName, current);
			return current;
		}
	}
	
	/** 
	 * @param systemCode short unique code to query for
	 * @return pre-existing DataSource object by system code, 
	 * 	if it exists, or creates a new one. 
	 */
	public static DataSource getBySystemCode(String systemCode)
	{
		if (!bySysCode.containsKey(systemCode))
		{
			register (systemCode, null, null, null, null, false, false, null);
		}
		return bySysCode.get(systemCode);
	}
	
	/** 
	 * returns pre-existing DataSource object by 
	 * full name, if it exists, 
	 * or creates a new one. 
	 * @param fullName full name to query for
	 * @return DataSource
	 */
	public static DataSource getByFullName(String fullName)
	{
		if (!byFullName.containsKey(fullName))
		{
			register (null, fullName, null, null, null, false, false, null);
		}
		return byFullName.get(fullName);
	}
	
	/**
		get all registered datasoures as a set.
		@return set of all registered DataSources
	*/ 
	static public Set<DataSource> getDataSources()
	{
		return registry;
	}
	
	/**
	 * returns a filtered subset of available datasources.
	 * @param primary Filter for specified primary-ness. If null, don't filter on primary-ness.
	 * @param metabolite Filter for specified metabolite-ness. If null, don't filter on metabolite-ness.
	 * @param o Filter for specified organism. If null, don't filter on organism.
	 * @return filtered set.
	 */
	static public Set<DataSource> getFilteredSet (Boolean primary, Boolean metabolite, Object o)
	{
		final Set<DataSource> result = new HashSet<DataSource>();
		for (DataSource ds : registry)
		{
			if (
					(primary == null || ds.isPrimary == primary) &&
					(metabolite == null || ds.isMetabolite == metabolite) &&
					(o == null || ds.organism == null || o == ds.organism))
			{
				result.add (ds);
			}
		}
		return result;
	}
	
	/**
	 * Get a list of all non-null full names.
	 * <p>
	 * Warning: the ordering of this list is undefined.
	 * Two subsequent calls may give different results.
	 * @return List of full names
	 */
	static public List<String> getFullNames()
	{
		final List<String> result = new ArrayList<String>();
		result.addAll (byFullName.keySet());
		return result;
	}
	/**
	 * The string representation of a DataSource is equal to
	 * it's full name. (e.g. "Ensembl")
	 * @return String representation
	 */
	public String toString()
	{
		return fullName;
	}
	
	/** an UrlMaker knows how to turn an id into an Url string. */
	public static abstract class UrlMaker
	{
		/**
		 * Generate an Url from an identifier.
		 * @param id identifier to use in Url 
		 * @return url based on the identifier */
		public abstract String getUrl(String id);
	}
	
	/** Implements most common way an Url is made: add Id to a prefix. */ 
	public static class PrefixUrlMaker extends UrlMaker
	{
		private final String prefix;
		
		/** @param prefix prefix to use in url */
		public PrefixUrlMaker(String prefix)
		{
			this.prefix = prefix;
		}
		
		/**
		 * Generate url from identifier.
		 * @param id identifier to use in url 
		 * @return Simply returns prefix + id */
		@Override
		public String getUrl(String id) 
		{
			return prefix + id;
		}
	}	

	/**
	 * @return example Xref, mostly for testing purposes
	 */
	public Xref getExample ()
	{
		return new Xref (idExample, this);
	}
	
	/**
	 * @return if this is a primary DataSource or not. Primary DataSources 
	 * are preferred when annotating models.
	 */
	public boolean isPrimary()
	{
		return isPrimary;
	}
	
	/**
	 * @return if this DataSource describes metabolites or not.
	 */
	public boolean isMetabolite()
	{
		return isMetabolite;
	}

	/**
	 * @return Organism that this DataSource describes, or null if multiple / not applicable.
	 */
	public Object getOrganism()
	{
		return organism;
	}

}
