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
import java.util.logging.Level;
import java.util.logging.Logger;

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
<p>
This contains modifications not yet finalized nor approved by the Whole BridgeDB community.
Until this message is removed use of new features is at the users own risk.
* 
*/
public final class DataSource
{
    public static final String MIRIAM_URN_ROOT = "urn:miriam:";
    public static final String IDENTIFIERS_URI_ROOT = "http://identifiers.org/";
    
	private static Map<String, DataSource> bySysCode = new HashMap<String, DataSource>();
	private static Map<String, DataSource> byFullName = new HashMap<String, DataSource>();
	private static Set<DataSource> registry = new HashSet<DataSource>();
	private static Map<String, DataSource> byAlias = new HashMap<String, DataSource>();
	private static Map<String, DataSource> byMiriamBase = new HashMap<String, DataSource>();
	
	private String sysCode = null;
	private String fullName = null;
    private Set<String> alternativeFullNames = new HashSet<String>();    
	private String mainUrl = null;
	private String prefix = "";
	private String postfix = "";
	private Object organism = null;
	private String idExample = null;
	private boolean isPrimary = true;
	private String type = "unknown";
	private String urnBase = "";
    private String miriamBase = "";
	
	/**
	 * Constructor is private, so that we don't
	 * get any standalone DataSources. 
	 * DataSources should be obtained from 
	 * {@link getByFullName} or {@link getBySystemCode}. Information about
	 * DataSources can be added with {@link register}
	 */
	private DataSource (String sysCode, String fullName) {
        this.sysCode = sysCode;
        this.fullName = fullName;
    }
	
	/** 
	 * Turn id into url pointing to info page on the web, e.g. "http://www.ensembl.org/get?id=ENSG..."
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
	 * returns alternative full names of DataSource e.g. "EC Number" for "Enzyme Nomenclature" 
	 * 
 	 * @return Set of alternative full names (not including the one returned by getFullName() or 
     * (in the majority of case) an Empty set.
     * @since 2.0.0
	 */
	public Set<String> getAlternativeFullNames()
	{
		return this.alternativeFullNames;
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
     * @deprecated behaviour when no MIRIAM URI is known is inconsistent. 
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
	 * Creates a Mirian identifier if possible. 
	 * It uses the MIRIAM data type list
	 * to create a MIRIAM URI like "urn:miriam:uniprot:P12345", 
	 * or if this DataSource is not included
	 * in the MIRIAM data types list, returns a null.
	 * @param id Id to generate URN from.
	 * @return the Mirian URN or null.
     * @throws IDMapperException is the id can not be safely URL encoded
     * @since 2.0.0
	 */
    public String getMiriamUrn(String id) throws IDMapperException{
        if (miriamBase == null){
            return null;
        }
		try
		{
    		return MIRIAM_URN_ROOT + miriamBase + ":" + URLEncoder.encode(id, "UTF-8");
		} catch (UnsupportedEncodingException ex) { 
            throw new IDMapperException("Unable to Encode id " + id, ex);
        }
	}

	/**
	 * Creates an identifiers.org URI if possible. 
	 * It uses the MIRIAM data type list
	 * to create a identifiers.org URI like "http://identifiers.org/uniprot/P12345", 
	 * or if this DataSource is not included returns null
	 * @param id Id to generate URN from.
	 * @return the identifiers.org uri or null.
     * @throws IDMapperException is the id can not be safely URL encoded
     * @since 2.0.0
	 */
    public String getIdentifiersOrgUri(String id) throws IDMapperException{
        if (miriamBase.isEmpty()){
            return null;
        }
		try
		{
    		return "http://identifiers.org/" + miriamBase + "/" + URLEncoder.encode(id, "UTF-8");
		} catch (UnsupportedEncodingException ex) { 
            throw new IDMapperException("Unable to Encode id " + id, ex);
        }
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
		 * @param urlPattern is a template for generating valid URL's for identifiers. 
		 * 	The pattern should contain the substring "$ID", which will be replaced by the actual identifier.
		 * @return the same Builder object so you can chain setters
		 */
		public Builder urlPattern (String urlPattern)
		{
            if (current.prefix.isEmpty() && current.postfix.isEmpty()){
    			if (urlPattern == null || "".equals (urlPattern))
        		{
            		current.prefix = "";
                	current.postfix = "";
                } 
                else
                {
                    int pos = urlPattern.indexOf("$id");
                    if (pos == -1) throw new IllegalArgumentException("Url maker pattern " + urlPattern + " for " + 
                            current + "' should have $id in it");
                    current.prefix = urlPattern.substring(0, pos);
                    current.postfix = urlPattern.substring(pos + 3);
                }
            } else {
    			if (urlPattern == null || "".equals (urlPattern))
        		{
                    System.err.println("Ignoring attempt to replace urlPattern for " + current.fullName + " with null ");
                } 
                else
                {
                    int pos = urlPattern.indexOf("$id");
                    if (pos == -1) throw new IllegalArgumentException("Url maker pattern " + urlPattern + " for " + 
                            current + "' should have $id in it");
                    if (current.prefix.equals(urlPattern.substring(0, pos)) && 
                            current.postfix.equals(urlPattern.substring(pos + 3))){
                        //Ok matches so fine
                    } else {
                        throw new IllegalArgumentException ("Illegal attempt to replace urlPattern " 
                                + current.getUrl("$id") + " for " + current.fullName + " with " + urlPattern);
                    }
                }
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
		 * Registers a base for urn generation, for example "urn:miriam:uniprot"
         * 
         * Since Version 2.0.0 If the base starts with "urn:miriam:" the part that comes after "urn:miriam:"
         * will also be used for identifiers.org uris.
		 * @param base for urn generation, for example "urn:miriam:uniprot"
		 * @return the same Builder object so you can chain setters
         * @throws IllegalStateException If a previous (different) urnBase was registered by either this method or 
         *     indirectly by the identifiersOrg method.
		 */
		public Builder urnBase (String base)
		{
            if (base!= null && !base.isEmpty()){
                try {
                    if (base.startsWith(MIRIAM_URN_ROOT)){
                        current.setMarianBase(base.substring(MIRIAM_URN_ROOT.length()));
                    } else {
                        current.setNonMarianBase(base);
                    }
                } catch (IDMapperException ex) {
                    throw new IllegalStateException("Unable to set base ", ex);
                }
            }
            return this;
		}
        
		/**
		 * Registers an indetifiers.org uri 
         * 
         * Since Version 2.0.0 If the base starts with "urn:miriam:" the part that comes after "urn:miriam:"
         * will also be used for identifiers.org uris.
		 * @param uri an indetifiers.org uri which must start with "http://identifiers.org/".
         *    Input supports both uris that ends with "/" and ones that do not,
         *    But output will always be the "offical" one with the slash.
		 * @return the same Builder object so you can chain setters
         * @throws IDMapperException If a previous (different) uri was registered by either this method or 
         *     indirectly by the urnBase method.
		 */
		public Builder identifiersOrgUri (String uri) throws IDMapperException
		{
            if (uri.startsWith(IDENTIFIERS_URI_ROOT)){
                if (uri.endsWith("/")){
                    current.setMarianBase(uri.substring(IDENTIFIERS_URI_ROOT.length(), uri.length()-1));
                } else {
                    current.setMarianBase(uri.substring(IDENTIFIERS_URI_ROOT.length()));                    
                }
                return this;
            } else {
                throw new IDMapperException("identifiers.Org uri must start with " + IDENTIFIERS_URI_ROOT 
                        + " which \"" + uri + "\" does not");
            }
		}
                
        /**
         * Allows you to add an Extra FullName to a DataSource
         * 
         * Registered the DataSource with this name but keeps the original fullName
         * 
         * @param alternativeFullName A DIFFERENT name!
		 * @return the same Builder object so you can chain setters
         * @throws IllegalStateException If the alternativeFullName is equals to the current fullName.
         * @since 2.0.0
         */
        public Builder alternativeFullName(String alternativeFullName){
            //This is a safety test only
            if (alternativeFullName.equals(current.fullName)){
               throw new IllegalStateException ("Illegal attempt to assign alterntiveFullName \"" + 
                       alternativeFullName + "\" which is already the fullName \"");                
            }
            current.alternativeFullNames.add(alternativeFullName);
            byFullName.put(alternativeFullName, current);
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
//		
        //This blokc is the new version 2.0 way of registeringf
        DataSource byName = byFullName.get(fullName);
        DataSource byCode = bySysCode.get(sysCode);
        
        if (byName == null){
            if (byCode == null){
    			current = new DataSource (sysCode, fullName);
    			registry.add (current);
            } else if (byCode.fullName == null){
                System.err.println("Found DataSource with sysCode \"" + sysCode + " and null fullName. "+ 
                        " Which is now being set to " + fullName);
    			current = byCode;
                current.fullName = fullName;                
            } else if (byCode.fullName.equals(fullName)){
                //Strange should never happen.
                System.err.println("sysCode \"" + sysCode + " already used wtih fullName \"" + 
                        byCode.fullName + "\" which does not match new fullName \"" + fullName + "\"");
    			current = byCode;
                current.fullName = fullName;                
            } else {
                byCode.alternativeFullNames.add(byCode.fullName);
                byCode.alternativeFullNames.remove(fullName);
                System.err.println("sysCode \"" + sysCode + " already used wtih fullName \"" + 
                        byCode.fullName + "\" which does not match new fullName \"" + fullName + "\"");
    			current = byCode;
                current.fullName = fullName;
            }
        } else {
            if (byCode == null){
                //This will catch both sysCodes being null;
                if (byName.sysCode == sysCode){
                    current = byName;
                //this one because "abc" != "abc" but "abc".equals("abc")    
                } else if (byName.sysCode.equals(sysCode)){
                    current = byName;                
                } else if (byName.sysCode == null){
                    current = byName;     
                    System.err.println("Overwriting null syscode for " + fullName);
                    current.sysCode = sysCode;
                } else if (byName.sysCode.isEmpty()){
                    current = byName;     
                    System.err.println("Overwriting empty syscode for " + fullName);
                    current.sysCode = sysCode;
                } else if (sysCode == null){
                    current = byName;     
                    System.err.println("Not overwriting syscode for " + fullName + " with null");
                } else if (sysCode.isEmpty()){
                    current = byName;     
                    System.err.println("Not overwriting syscode for " + fullName + " with empty");
                } else {
                    throw new IllegalStateException ("fullName " + fullName + " already used wtih systemCode \"" + 
                            byName.sysCode + "\" which does not match new systemCode \"" + sysCode + "\"");
                }
            } else {
                if (byName == byCode){
                    current = byCode;
                } else {
                    throw new IllegalStateException ("Found two possible DataSources! FullName + \"" + fullName + 
                            "\" already has code \"" + byName.sysCode + "\". While SysCode \"" + sysCode +
                            "\" already has a full name "+ byCode.fullName);                
                }
            }
        }
/*		//This block shows the version 1.0 way of registering 
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
*/
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
		return sysCode + ":" + fullName;
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
	 * If the given urn base is unknown, a new DataSource will be created 
     *    with the full name equal to the urn base without "urn.miriam."  
	 */
	public static DataSource getByUrnBase(String base)
	{
		if (!base.startsWith (MIRIAM_URN_ROOT))
		{
			return null;
		}
		String marianBase = base.substring(MIRIAM_URN_ROOT.length());
        return getByMiranBase(marianBase);
	}

	/**
	 * @param nameSpace the namespace, which must start with "http://identifiers.org/". It it isn't, null is returned.
	 * @returns the DataSource for a given urn base, or null if the base is invalid.
	 * If the given urn base is unknown, a new DataSource will be created 
     *    with the full name equal to the urn base without "http://identifiers.org/"  
	 */
	public static DataSource getByIdentifiersOrgUri(String base)
	{
		if (!base.startsWith (IDENTIFIERS_URI_ROOT))
		{
			return null;
		}
		String marianBase = base.substring(IDENTIFIERS_URI_ROOT.length());
        return getByMiranBase(marianBase);
	}

	/**
	 * @param marianBase the bit that comes (after "urn:miriam:" and before the next ":")
     * or (after "http://identifiers.org/" and beofre the next "/". 
  	 * @returns the DataSource for a given marianBase, or null if the base is invalid.
	 * If the given urn base is unknown, a new DataSource will be created with the full name equal to the urn base without "urn.miriam."  
	 */
	private static DataSource getByMiranBase(String marianBase)
	{
		DataSource current = null;
        
		if (byMiriamBase.containsKey(marianBase))
		{
			current = byMiriamBase.get(marianBase);
		}
		else
		{
			current = getByFullName(marianBase);
            try {
                current.setMarianBase(marianBase);
            } catch (IDMapperException ex) {
                throw new IllegalStateException("Unable to set base", ex);
            }
		}
		return current;
	}

    /**
	 * @param base the bit that comes (after "urn:miriam:" and before the next ":")
     * or (after "http://identifiers.org/" and beofre the next "/". 
     * @throws IDMapperException If a different base is set
     */
    private void setMarianBase(String base) throws IDMapperException{
        if (!miriamBase.isEmpty() && !miriamBase.equals(base)){
            throw new IDMapperException("Illegal attempt to change miriamBase for " + this 
                + ". Current value \"" +miriamBase + "\" is NOT equal to new Value \"" + base + "\"");            
        } 
        String newUrnBase = MIRIAM_URN_ROOT + base;
        if (!urnBase.isEmpty() && !newUrnBase.equals(urnBase)){
            System.err.println("Overwriting none Miriam UrnBase \"" + urnBase + "\""
                + " with Miriam base \"" + newUrnBase + "\" for " + this);
        }
        miriamBase = base;
        urnBase = newUrnBase;
        byMiriamBase.put (base, this);   
    }

    /**
	 * @param base A urn base predetermined not to start with "urn:miriam:"
     * @throws IDMapperException If a different base is set
     */
    private void setNonMarianBase(String base) throws IDMapperException{
        if (urnBase != "" && !urnBase.equals(base)){
            if (miriamBase.isEmpty()){
                throw new IDMapperException("Illegal attempt to change (none Miram) urnBase for " + this 
                        + ". Current value \"" + urnBase + "\" is NOT equal to new Value \"" + base + "\"");            
            }
            System.err.println("Ignoring attempt to overwrite Miriam UrnBase \"" + urnBase + "\""
                    + " with non Miriam base \"" + base + "\" for " + this);
            return;
        }
        urnBase = base;
        System.err.println("None miriam urnBase \"" + base + "\" used for " + this);
    }

}
