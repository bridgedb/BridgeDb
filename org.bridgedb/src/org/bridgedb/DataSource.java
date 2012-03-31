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
package org.bridgedb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
the constants from the org.bridgedb.bio module such as BioDataSource.ENSEMBL, 
or the "getBySystemcode" or "getByFullname" methods.
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
handle unknown data sources in the same 
way as predefined ones.
<p>
Definitions for common DataSources can be found in {@link org.bridgedb.bio.BioDataSource}.
*/
public final class DataSource
{
	private static Map<String, DataSource> bySysCode = new HashMap<String, DataSource>();
	private static Map<String, DataSource> byFullName = new HashMap<String, DataSource>();
	private static Set<DataSource> registry = new HashSet<DataSource>();
	private static Map<String, DataSource> byAlias = new HashMap<String, DataSource>();
	private static Map<String, DataSource> byMiriamBase = new HashMap<String, DataSource>();
	private static HashMap<String, DataSource> byPrefix = new HashMap<String, DataSource>();
	private static Set<DataSource> withPrefixAndPostfix = new HashSet<DataSource>();

	private String sysCode = null;
	private String fullName = null;
	private String mainUrl = null;
	private String prefix = "";
	private String postfix = "";
	private Object organism = null;
	private String idExample = null;
	private boolean isPrimary = true;
	private String type = "unknown";
	private String urnBase = "";
	
	/**
	 * Constructor is private, so that we don't
	 * get any standalone DataSources. 
	 * DataSources should be obtained from 
	 * {@link getByFullName} or {@link getBySystemCode}. Information about
	 * DataSources can be added with {@link register}
	 */
	private DataSource () {}
	
	/** 
	 * Turn id into url pointing to info page on the web, e.g. "http://www.ensembl.org/get?id=ENSG..."
     * <p>
	 * @param id identifier to use in url
	 * @return Url
	 */
	public String getUrl(String id)
	{
		return prefix + id + postfix;
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
	 * @return type of entity that this DataSource describes, for example
	 *   "metabolite", "gene", "protein" or "probe" 
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 * Creates a global identifier. 
	 * It uses the MIRIAM data type list
	 * to create a MIRIAM URI like "urn:miriam:uniprot:P12345", 
	 * or if this DataSource is not included
	 * in the MIRIAM data types list, a bridgedb URI.
	 * @param id Id to generate URN from.
	 * @return the URN. 
	 */
	public String getURN(String id)
	{
		String idPart = "";
		try
		{
			idPart = URLEncoder.encode(id, "UTF-8");
		} catch (UnsupportedEncodingException ex) { idPart = id; }
		return urnBase + ":" + idPart;
	}

    /**
     * Sets the prefix and postfix for this DataSource.
     * <p>
     * 
     * @param prefix
     * @param postfix 
     */
    private void setFixes(String prefix, String postfix) {
        this.prefix = prefix;
        this.postfix = postfix;
        if (postfix.isEmpty()){ 
            byPrefix.put(prefix, this);
        } else {
            withPrefixAndPostfix.add(this);
        }
    }

    public String getNameSpace() {
        return prefix;
    }

	/**
	 * Uses builder pattern to set optional attributes for a DataSource. For example, this allows you to use the 
	 * following code:
	 * <pre>
	 * DataSource.register("X", "Affymetrix")
	 *     .mainUrl("http://www.affymetrix.com")
	 *     .type("probe")
	 *     .primary(false);
	 * </pre>
	 */
	public static final class Builder
	{
		private final DataSource current;
		
		/**
		 * Create a Builder for a DataSource. Note that an existing DataSource is
		 * modified rather than creating a new one.
		 * This constructor should only be called by the register method.
		 * @param current the DataSource to be modified
		 */
		private Builder(DataSource current)
		{
			this.current = current;
		}
		
		/**
		 * @return the DataSource under construction
		 */
		public DataSource asDataSource()
		{
			return current;
		}
		
		/**
		 * 
		 * The pattern should contain the substring "$id", which will be replaced by the actual identifier.
         * <p>
         * If more than one DataSource source is set with the same urlPattern than the last datasource set 
         *    using this method will be returned by the meothods getByURLPattern and getByNameSpace.
         * The behaviour of allowing two or more DataSources to share a URLPattern is historical.
         * <p>
         * Warning this method and nameSpace(String) has almost the same functionality.(see above)
         * Calling both will result in the second call overwriting the setting of the first call.
         * Similarly calling this function more than once will have the same effect. 
         * Only the last urlPattern will be valid.
         * 
		 * @param urlPattern is a template for generating valid URL's for identifiers. 
		 * @return the same Builder object so you can chain setters
		 */
		public Builder urlPattern (String urlPattern) 
		{
            //Clear any previously registered values
            byPrefix.values().remove(current);
            withPrefixAndPostfix.remove(current);
            
 			if (urlPattern == null || urlPattern.isEmpty())
			{
				current.prefix = "";
				current.postfix = "";
			}
			else
			{
				int pos = urlPattern.indexOf("$id");
				if (pos == -1) {
                    throw new IllegalArgumentException("Url maker pattern for " + current + 
                        "' should have $id in it");
                }
                if (urlPattern.equals("$id")){
                    throw new IllegalArgumentException("Url maker pattern for " + current + 
                        "' should be more than just \"$id\".");
                }
                //Can not check for previous as existing code allows two or more DataSoruces to share a URL pattern.
                //FOr example BIO.
                current.setFixes(urlPattern.substring(0, pos), urlPattern.substring(pos + 3));
			}
			return this;
		}
	
        /**
         * Uses this nameSpace to construct url where th id will be the localName 
         * <p>
         * If more than one DataSource source is set with the same nameSpace this methond will throw an exception.
         * However the namespace information can be overwritten using the urlPattern method.
         * Only the last datasource set using either method will be returned 
         *    by the meothods getByURLPattern and getByNameSpace.
         * For Historical reason the method urlPattern can not enforce that no two DataSources share the same namespace.
         * This method is new so can and does.
         * <p>
         * Warning this method and urlPattern(String) has almost (see above) the same functionality. 
         * It is equivellent to calling urlPattern(nameSpace + "$id").
         * Calling both will result in the second call overwriting the setting of the first call.
         * Similarly calling this function more than once will have the same effect. 
         * Only the last nameSpace will be valid.
       * 
         * @param prefix
         * @return 
         */
        public Builder nameSpace(String nameSpace) throws IDMapperException{
            //Clear any previously registered values
            byPrefix.values().remove(current);
            withPrefixAndPostfix.remove(current);

            if (nameSpace != null && !nameSpace.isEmpty()){
                if (byPrefix.get(nameSpace) != null){
                    throw new IDMapperException("Unable to set nameSpace for DataSoucrce: " + current + 
                            " because is has already been used by DataSource: " + byPrefix.get(nameSpace));
                }
                current.setFixes(nameSpace, "");
            }
            return this;
        }
        
 		/**
		 * @param mainUrl url of homepage
		 * @return the same Builder object so you can chain setters
		 */
		public Builder mainUrl (String mainUrl)
		{
			current.mainUrl = mainUrl;
			return this;
		}


		/**
		 * @param idExample an example id from this system
		 * @return the same Builder object so you can chain setters
		 */
		public Builder idExample (String idExample)
		{
			current.idExample = idExample;
			return this;
		}
		
		/**
		 * @param isPrimary secondary id's such as EC numbers, Gene Ontology or vendor-specific systems occur in data or linkouts,
		 * 	but their use in pathways is discouraged
		 * @return the same Builder object so you can chain setters
		 */
		public Builder primary (boolean isPrimary)
		{
			current.isPrimary = isPrimary;
			return this;
		}
		
		/**
		 * @param type the type of datasource, for example "protein", "gene", "metabolite" 
		 * @return the same Builder object so you can chain setters
		 */
		public Builder type (String type)
		{
			current.type = type;
			return this;
		}
		
		/**
		 * @param organism organism for which this system code is suitable, or null for any / not applicable
		 * @return the same Builder object so you can chain setters
		 */
		public Builder organism (Object organism)
		{
			current.organism = organism;
			return this;
		}
		
		/**
		 * @param base for urn generation, for example "urn:miriam:uniprot"
		 * @return the same Builder object so you can chain setters
		 */
		public Builder urnBase (String base)
		{
			current.urnBase = base;
			return this;
		}
	}
	
	/** 
	 * Register a new DataSource with (optional) detailed information.
	 * This can be used by other modules to define new DataSources.
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML. Must be 20 or less characters
	 * @return Builder that can be used for adding detailed information.
	 */
	public static Builder register(String sysCode, String fullName)
	{
		DataSource current = null;
		if (fullName == null && sysCode == null) throw new NullPointerException();
//		if (fullName != null && fullName.length() > 20) 
//		{ 
//			throw new IllegalArgumentException("full Name '" + fullName + "' must be 20 or less characters"); 
//		}
		
		if (byFullName.containsKey(fullName))
		{
			current = byFullName.get(fullName);
		}
		else if (bySysCode.containsKey(sysCode))
		{
			current = bySysCode.get(sysCode);
		}
		else
		{
			current = new DataSource ();
			registry.add (current);
		}
		
		if (current.urnBase != null)
		{
			byMiriamBase.put (current.urnBase, current);
		}
		
		current.sysCode = sysCode;
		current.fullName = fullName;

		if (isSuitableKey(sysCode))
			bySysCode.put(sysCode, current);
		if (isSuitableKey(fullName))
			byFullName.put(fullName, current);
		
		return new Builder(current);
	}
	
	public void registerAlias(String alias)
	{
		byAlias.put (alias, this);
	}
	
	/**
	 * Helper method to determine if a String is allowed as key for bySysCode and byFullname hashes.
	 * Null values and empty strings are not allowed.
	 * @param key key to check.
	 * @return true if the key is allowed
	 */
	private static boolean isSuitableKey(String key)
	{
		return !(key == null || "".equals(key));
	}
	
	
	/** 
	 * @param systemCode short unique code to query for
	 * @return pre-existing DataSource object by system code, 
	 * 	if it exists, or creates a new one. 
	 */
	public static DataSource getBySystemCode(String systemCode)
	{
		if (!bySysCode.containsKey(systemCode) && isSuitableKey(systemCode))
		{
			register (systemCode, null);
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
		if (!byFullName.containsKey(fullName) && isSuitableKey(fullName))
		{
			register (null, fullName);
		}
		return byFullName.get(fullName);
	}
	
	public static DataSource getByAlias(String alias)
	{
		return byAlias.get(alias);
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
					(primary == null || ds.isPrimary() == primary) &&
					(metabolite == null || ds.isMetabolite() == metabolite) &&
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
        if (fullName != null){
            return fullName + ":" + prefix;
        } else {
            return sysCode + ":" + prefix;
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
	 * 
	 * A DataSource is primary if it is not of type probe, 
	 * so that means e.g. Affymetrix or Agilent probes are not primary. All
	 * gene, protein and metabolite identifiers are primary.
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
		return type.equals ("metabolite");
	}

	/**
	 * @return Organism that this DataSource describes, or null if multiple / not applicable.
	 */
	public Object getOrganism()
	{
		return organism;
	}

	/**
	 * @param base the base urn, which must start with "urn:miriam:". It it isn't, null is returned.
	 * @returns the DataSource for a given urn base, or null if the base is invalid.
	 * If the given urn base is unknown, a new DataSource will be created with the full name equal to the urn base without "urn.miriam."  
	 */
	public static DataSource getByUrnBase(String base)
	{
		if (!base.startsWith ("urn:miriam:"))
		{
			return null;
		}
		DataSource current = null;
		
		if (byMiriamBase.containsKey(base))
		{
			current = byMiriamBase.get(base);
		}
		else
		{
			current = getByFullName(base.substring("urn:miriam:".length()));
			current.urnBase = base;
			byMiriamBase.put (base, current);
		}
		return current;
	}

    /**
     * Attempts to find a DataSource that fits this URL otherwise registers a new URL
     * <p>
     * The first attempt is to use URLPattern where the id is exactly the String "$id".
     * <p>
     * The second attempt is to assume the uri has a nameSpace followed by the ID.
     * This is where the urlPattern used in Builder.urlPattern ends with "#$ID", "/$ID" or ":#$ID", 
     * and "$ID" does not contain the characters '#', '/', or ':' 
     * <p>
     * The url is Split after the first occurrence of the '#' character,
     * If this fails, split after the last occurrence of the '/' character,
     * If this fails, split after the last occurrence of the ':' character. 
     * The first part of the split is assumed to be the prefix and a DataSource with that prefix is looked.
     * <p>
     * If that fails the method iterates through all know DataSources (with a urlPattern) 
     * and checks to see it the URL's start and ends match the pattern.
     * <p>
     * If no DataSource exists with this URL a new one is created.
     * <p>
     * Note the methods getByURL(String)  and getByNameSpace(String) are semantic sugar for getByURLPattern(String).
     * All work with the same internal data 
     *    so where they referer to the same URLPattern they will return the same DataSource
     * @param url A 
     * @return A DataSource whoe urlPattern matches the url. 
     */
    public static DataSource getByURL(String url) {
        int pos = url.indexOf("$id");
        if (pos == -1){
            return getByNonPattern(url);
        } else {
            return getByURLPatternOnly(url, true);
        }
    }
    
    public static Xref uriToXref(String url) throws IDMapperException{
        int pos = url.indexOf("$id");
        if (pos == -1){
            return uriToXrefByNonPattern(url);
        } else {
            throw new IDMapperException ("URLs with $id are considered to be patterns and not");
        }        
    }
    
    /**
     * Attempts to find a DataSource that fits this URL Patternotherwise registers a new URL
     * <p>
     * The URLPattern must contain the String "$id".
     * <p>
     * If no DataSource exists with this URL a new one is created.
     * <p>
     * Note the methods getByURL(String)  and getByNameSpace(String) are semantic sugar for getByURLPattern(String).
     * All work with the same internal data 
     *    so where they referer to the same URLPattern they will return the same DataSource
     * @param url A 
     * @return A DataSource whose urlPattern matches the urlPattern. 
     */
    public static DataSource getByURLPattern(String urlPattern) {    
        int pos = urlPattern.indexOf("$id");
        if (pos == -1){
            throw new IllegalArgumentException("Url pattern should have $id in it");
        } else {
            return getByURLPatternOnly(urlPattern, true);
        }
    }
    
    /**
     * Attempts to find a DataSource with this nameSpace.
     * <p>
     * Equivellent (but faster) to calling getByURLPattern (nameSpace + "$id").
     * <p>
     * If no DataSource exists with this URL a new one is created.
     * <p>
     * Note the methods getByURL(String) and getByNameSpace(String) are semantic sugar for getByURLPattern(String).
     * All work with the same internal data 
     *    so where they referer to the same URLPattern they will return the same DataSource
     * @param url A 
     * @return A DataSource whose urlPattern is nameSpace + "$id". 
     */
    public static DataSource getByNameSpace(String nameSpace){
        if (nameSpace == null){
            throw new IllegalArgumentException("nameSpace may not be null.");
        }
        if (nameSpace.isEmpty()){
            throw new IllegalArgumentException("nameSpace may not be empty.");            
        }
        DataSource result = byPrefix.get(nameSpace);
        if (result == null){
            return createDataSource(nameSpace);
        } else {
            return result;
        }
    }

    private static DataSource getByURLPatternOnly(String urlPattern, boolean createNew) {
        urlPattern = urlPattern.trim();
        int pos = urlPattern.indexOf("$id");
        String prefix = urlPattern.substring(0, pos);
        String postfix = urlPattern.substring(pos + 3);
        if (postfix.isEmpty()){
            DataSource result = byPrefix.get(prefix);
            if (result != null){
                return result;
            } else {
                if (createNew){
                return createDataSource(prefix);
                } else {
                    return null;
                }
            }
        }      
        for (DataSource source:withPrefixAndPostfix){
            if (prefix.equals(source.prefix) && postfix.equals(source.postfix)){
                return source;
            }
        }
        if (createNew){
            DataSource result = register(urlPattern, urlPattern).asDataSource();
            result.setFixes(prefix, postfix);
            return result;
        } else {
            return null;
        }
    }

    //Changes made here should also be maded to uriToXrefByNonPattern
    private static DataSource getByNonPattern(String url) {
        String prefix = null;
        url = url.trim();
        if (url.contains("#")){
            prefix = url.substring(0, url.lastIndexOf("#")+1);
        } else if (url.contains("/")){
            prefix = url.substring(0, url.lastIndexOf("/")+1);
        } else if (url.contains(":")){
            prefix = url.substring(0, url.lastIndexOf(":")+1);
        }
        //ystem.out.println(lookupPrefix);
        if (prefix == null){
            throw new IllegalArgumentException("Url should have a '#', '/, or a ':' in it.");
        }
        if (prefix.isEmpty()){
            throw new IllegalArgumentException("Url should not start with a '#', '/, or a ':'.");            
        }
        DataSource result = byPrefix.get(prefix);
        if (result != null){
            return result;
        }
        for (DataSource source:withPrefixAndPostfix){
            if (url.startsWith(source.prefix) && url.endsWith(source.postfix)){
                return source;
            }
        }
        return createDataSource(prefix);
    }
    
    //Changes made here should also be maded to getByNonPattern
    private static Xref uriToXrefByNonPattern(String url) {
        String prefix = null;
        String id = null;
        url = url.trim();
        if (url.contains("#")){
            prefix = url.substring(0, url.lastIndexOf("#")+1);
            id = url.substring(url.lastIndexOf("#")+1, url.length());
        } else if (url.contains("/")){
            prefix = url.substring(0, url.lastIndexOf("/")+1);
            id = url.substring(url.lastIndexOf("/")+1, url.length());
        } else if (url.contains(":")){
            prefix = url.substring(0, url.lastIndexOf(":")+1);
            id = url.substring(url.lastIndexOf(":")+1, url.length());
        }
        //ystem.out.println(lookupPrefix);
        if (prefix == null){
            throw new IllegalArgumentException("Url should have a '#', '/, or a ':' in it.");
        }
        if (prefix.isEmpty()){
            throw new IllegalArgumentException("Url should not start with a '#', '/, or a ':'.");            
        }
        DataSource dataSource = byPrefix.get(prefix);
        if (dataSource != null){
            return new Xref(id, dataSource);
        }
        for (DataSource source:withPrefixAndPostfix){
            if (url.startsWith(source.prefix) && url.endsWith(source.postfix)){
                id = url.substring(source.prefix.length(), url.length() -  source.postfix.length());
                return new Xref(id, source);
            }
        }
        dataSource = createDataSource(prefix);
        return new Xref(id, dataSource);
    }


    private static DataSource createDataSource(String prefix) {
        DataSource result = register(prefix, prefix).asDataSource();
        //ystem.out.println(prefix);
        //Calls setFixes directly as there is no need to check for conflicts with other 
        result.setFixes(prefix, "");
        return result;
    }
	
}
