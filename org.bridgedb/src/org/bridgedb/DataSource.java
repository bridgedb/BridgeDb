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
<p>
* This contains modifications not yet finalized nor approved by the Whole BridgeDB community.
Until this message is removed use of new features is at the users own risk.

*/
public final class DataSource
{
    /**
     * String required to be in front of any URN(base) that will be valid in Miriam
     * @since Version 2  (Version 1 had "urn:miriam:" hard coded in)
     */
    public static final String MIRIAM_URN_ROOT = "urn:miriam:";
    /**
     * String that is at the front of any identifiers.org uri.
     * 
     * @since Version 2
     */
    public static final String IDENTIFIERS_URI_ROOT = "http://identifiers.org/";

    private static Map<String, DataSource> bySysCode = new HashMap<String, DataSource>();
	private static Map<String, DataSource> byFullName = new HashMap<String, DataSource>();
	private static Set<DataSource> registry = new HashSet<DataSource>();
	private static Map<String, DataSource> byAlias = new HashMap<String, DataSource>();
	private static Map<String, DataSource> byMiriamBase = new HashMap<String, DataSource>();
    private static DataSourceOverwriteLevel overwriteLevel = DataSourceOverwriteLevel.VERSION1;
    
	private String sysCode = null;
	private String fullName = null;
	private String mainUrl = null;
	private String prefix = "";
	private String postfix = "";
	private Object organism = null;
	private String idExample = null;
    /**
     * @since Version 1 However in Version 2 changed to the Object Boolean to capture a third state. Not yet Set!
     */
	private Boolean isPrimary = null;
    /**
     * @since Version 2. Only change is creation of a CONSTANT as in Version 1 type was defaulted to "unknown". 
     */
    public static String DEFAULT_TYPE = "unknown";
	private String type = DEFAULT_TYPE;
    /**
     * Used for both the getUrn and getIdentifiersOrgUri (where applicable)
     */
	private String urnBase = "";
    /**
     * Records any alternative FullName that this DataSource is mapped against.
     * The getByFullName method will return this DataSource for any of these Strings
     * However the getFullName method will not return any of these Strings
     * @since version2
     */
    private Set<String> alternativeFullNames = new HashSet<String>();    

	
	/**
	 * Constructor is private, so that we don't
	 * get any standalone DataSources. 
	 * DataSources should be obtained from 
	 * {@link getByFullName} or {@link getBySystemCode}. Information about
	 * DataSources can be added with {@link register}
	 */
	private DataSource () {}
    
    /**
     * Sets the DataSourceOverwriteLevel
     * @see DataSourceOverwriteLevel for the full explanation.
     * @param level the DataSourceOverwriteLevel
     * @since Version 2
     */
    public static void setOverwriteLevel(DataSourceOverwriteLevel level){
        overwriteLevel = level;
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
     * 
     * @return Set of fullNames where getByFullName will return this Datasource but getFullName will 
     *     NOT return these fullNames.
     * @since version2
     */
    public Set<String> getAlternativeFullNames() {
        return this.alternativeFullNames;
    }

    /**
     * Return the identifiers.org uri if avaiable.
     * 
     * This is generated by checing if the urnBase is a miriam urn base and if so 
     * by replacing the miriam:urn prefix with the identofiers.org prefix 
     * and using a slash divider instead of a colon.
     * @param id id part as a string or $id to get the pattern.
     * @return identifiers.org uri or null.
     */
    public String getIdentifiersOrgUri(String id) {
        if (urnBase != null && urnBase.startsWith(MIRIAM_URN_ROOT)){
            return IDENTIFIERS_URI_ROOT + urnBase.substring(MIRIAM_URN_ROOT.length()) + "/" + id;
        }
        return null;
    }

     /**
     * Registers an identifiers.org uri 
     * 
     * Since Version 2.0.0 If the urn base starts with "urn:miriam:" the part that comes after "urn:miriam:"
     * will also be used for identifiers.org uris.
     * 
     * Convert the base to a miriam urn Base and calls the same method builder.setUrnBase does 
     *          in DataSourceOverwriteLevel.STRICT mode (disregard the current level set)
     * @param identifiersOrgBase an idetifiers.org uri which must start with "http://identifiers.org/".
     *    Input supports both uris that ends with "/" and ones that do not,
     *    But output will always be the "offical" one with the slash.
     * @throws IDMapperException If a previous (different) urn base was registered by either this method or 
     *     indirectly by the urnBase method. 
     * @since Version 2.0.0
	 */
   public void setIdentifiersOrgUriBase(String identifiersOrgBase) throws IDMapperException {
        if (identifiersOrgBase == null || identifiersOrgBase.isEmpty()){
            throw new IDMapperException("IdentifiersOrgUriBase may not set to null or empty");
        }
        if (identifiersOrgBase.startsWith(IDENTIFIERS_URI_ROOT)){
            try {
                if (identifiersOrgBase.endsWith("/")) {
                    setUrnBaseStrict(MIRIAM_URN_ROOT + identifiersOrgBase.substring(IDENTIFIERS_URI_ROOT.length(),
                            identifiersOrgBase.length()-1));
                } else {
                    setUrnBaseStrict(MIRIAM_URN_ROOT + identifiersOrgBase.substring(IDENTIFIERS_URI_ROOT.length()));                    
                }
           } catch (Exception e){
               throw new IDMapperException("Unable to set dentifiersOrgUriBase to " + identifiersOrgBase, e);
           }
       } else {
           throw new IDMapperException("illegal IdentifiersOrgUriBase " + identifiersOrgBase 
                   + " it must start with " + IDENTIFIERS_URI_ROOT);
       }
    }
	
   /**
    * Sets the urnBase keeping the same data as in version 1
    * 
    * In contrast to Builder.setUrnBase in Version 1 this method does register the DataSource with this urn Base.
    * 
    * Called by Builder.setUrn if the DataSourceOverwriteLevel is VERSION1
    * @param base for urn generation, for example "urn:miriam:uniprot"
    * @throw IllegalArgumentException NEVER
    * @see DataSourceOverwriteLevel
    */
    private void setUrnBaseVersion1(String base) {
        this.urnBase = base;
        if (base.startsWith(MIRIAM_URN_ROOT)){
            byMiriamBase.put(base, this);
        }
    }

   /**
    * Sets the urnBase keeping a base which starts with urn:miriam over one that does not.
    * 
    * In contrast to Builder.setUrnBase in Version 1 this method does register the DataSource with this urn Base.

    * Called by Builder.setUrn if the DataSourceOverwriteLevel is CONTROLLED
    
    * @param base for urn generation, for example "urn:miriam:uniprot"
    * @throw IllegalArgumentException if the base is empty or null.
    *     Also thrown if a second different base starting with urn:miriam is set.
    * @since Version 2
    * @see DataSourceOverwriteLevel
    */
    private void setUrnBaseControlled(String base) {
        if (base == null || base.isEmpty()){
            throw new IllegalArgumentException("urnBase may not be null or empty. Received " + base);
        }
        if (urnBase == null || urnBase.isEmpty()){
            setUrnBaseVersion1(base);
        }
        if (base.equals(urnBase)){
            return; //already the same
        }
        if (urnBase.startsWith(MIRIAM_URN_ROOT)){
            if (base.startsWith(MIRIAM_URN_ROOT)){
                throw new IllegalArgumentException("Illegal attemt to overwrite a Miriam urnBase for " + this + 
                        " was " + urnBase + " so can not set " + base);
            } else {
                System.err.println("Ignoring attempt to overwrite urnBase for " + this + " from " + urnBase + " to " + base);
            }
        } else {
            System.err.println("Overwrite urnBase for " + this + " from " + urnBase + " to " + base);
            setUrnBaseVersion1(base);
        }
    }

   /**
    * Sets the urnBase if and only if no previous base has been set.
    * 
    * In contrast to Builder.setUrnBase in Version 1 this method does register the DataSource with this urn Base.

    * Called by Builder.setUrn if the DataSourceOverwriteLevel is STRICT
    
    * @param base for urn generation, for example "urn:miriam:uniprot"
    * @throw IllegalArgumentException if the base is empty or null.
    *     Also thrown if a second different base (regardless of what they start with) is set.
    * @since Version 2
    * @see DataSourceOverwriteLevel
    */
    private void setUrnBaseStrict(String base) {
       if (base == null || base.isEmpty()){
            throw new IllegalArgumentException("urnBase may not be null or empty. Received " + base);
        }
        if (urnBase == null || urnBase.isEmpty()){
            setUrnBaseVersion1(base);
        }
        if (base.equals(urnBase)){
            return; //already the same
        }
        throw new IllegalArgumentException("Illegal attemt to overwrite a Miriam urnBase for " + this + 
                " was " + urnBase + " so can not set " + base);
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
			if (urlPattern == null || "".equals (urlPattern))
			{
				current.prefix = "";
				current.postfix = "";
			}
			else
			{
				int pos = urlPattern.indexOf("$id");
				if (pos == -1) throw new IllegalArgumentException("Url maker pattern for " + current + "' should have $id in it");
				current.prefix = urlPattern.substring(0, pos);
				current.postfix = urlPattern.substring(pos + 3);
			}
			return this;
		}
		
		/**
		 * @param mainUrl url of homepage
		 * @return the same Builder object so you can chain setters
         * @since Version 1 but since Version2 could throw an Exception
         * @throws IllegalArgumentException If and only if overwriteLevel == STRICT 
         *    AND a previous different non null mainUrl had been set.
		 */
		public Builder mainUrl (String mainUrl)
		{
            if (current.mainUrl != null) {
                if (DataSource.overwriteLevel == DataSourceOverwriteLevel.STRICT && !current.mainUrl.equals(mainUrl)){
                    throw new IllegalArgumentException("Illegal attemt to overwrite a mainUrl for " + current + 
                " was " + current.mainUrl + " so can not set " + mainUrl);
                }
            }
			current.mainUrl = mainUrl;
			return this;
		}


		/**
		 * @param idExample an example id from this system
		 * @return the same Builder object so you can chain setters
         * @since Version 1 but since Version2 could throw an Exception
         * @throws IllegalArgumentException If and only if overwriteLevel == STRICT 
         *    AND a previous different non null idExample had been set.
		 */
		public Builder idExample (String idExample)
		{
            if (current.idExample != null && !current.idExample.isEmpty()){
                if (DataSource.overwriteLevel == DataSourceOverwriteLevel.STRICT && !current.idExample.equals(idExample)){
                    throw new IllegalArgumentException("Illegal attemt to overwrite a idExample for " + current + 
                " was " + current.idExample + " so can not set " + idExample);
                }
            }
			current.idExample = idExample;
			return this;
		}
		
		/**
		 * @param isPrimary secondary id's such as EC numbers, Gene Ontology or vendor-specific systems occur in data or linkouts,
		 * 	but their use in pathways is discouraged
		 * @return the same Builder object so you can chain setters
         * @since Version 1 but since Version2 could throw an Exception
         * @throws IllegalArgumentException If and only if overwriteLevel == STRICT 
         *    AND a previous different isPrimary was set.
		 */
		public Builder primary (boolean isPrimary)
		{
            if (current.isPrimary != null && DataSource.overwriteLevel == DataSourceOverwriteLevel.STRICT && 
                    current.isPrimary != isPrimary){
                throw new IllegalArgumentException("Illegal attemt to change primary for " + current + 
                        " was " + current.isPrimary + " so can not set " + isPrimary);
            }
			current.isPrimary = isPrimary;
			return this;
		}
		
		/**
		 * @param type the type of datasource, for example "protein", "gene", "metabolite" 
		 * @return the same Builder object so you can chain setters
         * @since Version 1 but since Version2 could throw an Exception
         * @throws IllegalArgumentException If and only if overwriteLevel == STRICT 
         *    AND a previous different type was set to something other than "unknown".
		 */
		public Builder type (String type)
		{
            System.out.println(current + " " + type);
            if (!current.type.equals(DEFAULT_TYPE) && DataSource.overwriteLevel == DataSourceOverwriteLevel.STRICT && 
                    !current.type.equals(type)){
                throw new IllegalArgumentException("Illegal attemt to change type for " + current + 
                        " was " + current.type + " so can not set " + type);
            }
 			current.type = type;
			return this;
		}
		
		/**
		 * @param organism organism for which this system code is suitable, or null for any / not applicable
		 * @return the same Builder object so you can chain setters
         * @since Version 1 but since Version2 could throw an Exception
         * @throws IllegalArgumentException If and only if overwriteLevel == STRICT 
         *    AND a previous different organism was set.
		 */
		public Builder organism (Object organism)
		{
            if (current.organism != null && DataSource.overwriteLevel == DataSourceOverwriteLevel.STRICT && 
                    !current.organism.equals(organism)){
                throw new IllegalArgumentException("Illegal attemt to change Orgamism for " + current + 
                        " was " + current.organism + " so can not set " + organism);
            }
			current.organism = organism;
			return this;
		}
		
		/**
		 * @param base for urn generation, for example "urn:miriam:uniprot"
		 * @return the same Builder object so you can chain setters
		 */
		public Builder urnBase (String base)
		{
            switch (overwriteLevel){
                case VERSION1 : {
                    current.setUrnBaseVersion1(base);
        			return this;
               }
                case CONTROLLED: {
                    current.setUrnBaseControlled(base);
        			return this;
                }   
                case STRICT: {
                    current.setUrnBaseStrict(base);
        			return this;
                }
                default: throw new IllegalStateException ("Unexpected overwriteLevel " + overwriteLevel);
            }       
 		}

        /**
         * Adds an alternative name to this DataSource, and registers it by fullName
         * 
         * The result is that the getFullName method will NOT return this alternativeName.
         * 
         * However getByFullName(alternativeName) will return the same DataSource as 
         * returned by this Builder's getDataSource method.
         * 
         * As this method did not exist in Version 1 
         *    the error checking will always be at DataSourceOverwriteLevel.STRICT.
         * 
         * Note: The behaviour of throwing an Exception if the alternativeName is equal to the fullName is open to change.
         *    If required the behaviour could be changed to just ignoring the request.
         * 
         * @param alternativeName
         * throw new IllegalArgumentException If the name is null or empty.
         *     Also thrown if the alternativeName is already the full name. 
         *     Also thrown if alternativeName is already mapped to a different DataSource
		 * @return the same Builder object so you can chain setters
         * @since Version 2
         * @see DataSourceOverwriteLevel.
         */
        public Builder alternativeFullName(String alternativeName) {
            if (alternativeName == null || alternativeName.isEmpty()){
                throw new IllegalArgumentException("alternativeName may not be null or empty. Received "+ alternativeName);
            }
            DataSource other = byFullName.get(alternativeName);
            if (other!= null && !other.equals(current)){
                throw new IllegalArgumentException("alternativeName " + alternativeName + " already assigned to " 
                        + other + " so can not be asisgned to " + current);                
            }
            if (current.fullName.equals(alternativeName)){
                throw new IllegalArgumentException("Illegal attempt to set alternativeName same as fullName for " + current);
            }
            current.alternativeFullNames.add(alternativeName);
            DataSource.byFullName.put(alternativeName, current);
            return this;
        }
	}
	
	/** 
	 * Register a new DataSource with (optional) detailed information.
	 * This can be used by other modules to define new DataSources.
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML. Must be 20 or less characters
	 * @return Builder that can be used for adding detailed information.
     * @since Version 1 However With Version 2 the behaviour can be different depending on the DataSourceOverwriteLevel
     * @see DataSourceOverwriteLevel
     * @see lookupDataSourceVersion1(String sysCode, String fullName)
    * @see lookupDataSourceControlled(String sysCode, String fullName)
    * @see lookupDataSourceStrict(String sysCode, String fullName)
	 */
	public static Builder register(String sysCode, String fullName)
	{
		if (fullName == null && sysCode == null) throw new NullPointerException();
		
		DataSource current = lookupDataSources(sysCode, fullName);
		
		if (current.urnBase != null)
		{
			byMiriamBase.put (current.urnBase, current);
		}
		
		if (isSuitableKey(sysCode))
			bySysCode.put(sysCode, current);
		if (isSuitableKey(fullName))
			byFullName.put(fullName, current);
		
		return new Builder(current);
	}
	
    /**
     * Helper method to call the appropriate lookup method depending on the DataSourceOverwriteLevel
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML. Must be 20 or less characters
     * @return And existing or new DataSource
     * @since Version 2
     * @see DataSourceOverwriteLevel.
     * @throws IllegalArgumentException (see individual methods)
     */
    private static DataSource lookupDataSources(String sysCode, String fullName) {
        switch (overwriteLevel){
            case VERSION1 : {
                return lookupDataSourceVersion1(sysCode, fullName);
            }
            case CONTROLLED: {
                DataSource result = lookupDataSourceControlled(sysCode, fullName);
                return result;
            }
            case STRICT: {
                return lookupDataSourceStrict(sysCode, fullName);
            }
            default: throw new IllegalStateException ("Unexpected overwriteLevel " + overwriteLevel);
        }
    }
    
    /**
     * Finds or creates a DataSource as done in Version 1
     * 
     * Alternative name is not saved.
     * 
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML. Must be 20 or less characters
     * @return An existing or new DataSource
     * @since Version 2 (although code here is from Version 1)
     * @see DataSourceOverwriteLevel.
     * @throws IllegalArgumentException NEVER
     */
    private static DataSource lookupDataSourceVersion1(String sysCode, String fullName) {
        DataSource current;
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
		current.sysCode = sysCode;
		current.fullName = fullName;
        return current;
    }
    
   /**
     * Finds or creates a DataSource similar to Version 1 
     *    but capturing when different fullNames are used for the same SysCode.
     * 
     * Similar to Version 1 the last fullName registered will be the one returned by getFullName.
     * However now earlier fullNames will be saved as alternatives.
     * 
     * Also now an Exception is thrown if sysCode and fullname where each already mapped to a different DataSources.
     *    Version 1 code would simply have used the DataSource mapped to FullName.
     * 
     * As there is no known case of the same FullName being assigned to two different sysCodes, 
     *    an Exception is thrown if this occurs. 
     *    This can and should be changed to saving alternative sysCodes if required.
     * 
 	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML. Must be 20 or less characters
     * @return An existing or new DataSource
     * @since Version 2 
     * @see DataSourceOverwriteLevel.
     * @throws IllegalArgumentException If this could result in tow Possible DataSources or a DataSource with two sysCodes.
     */
    private static DataSource lookupDataSourceControlled(String sysCode, String fullName) {
        DataSource byName = byFullName.get(fullName);
        DataSource byCode = bySysCode.get(sysCode);
        if (byName == byCode){
            if (byName == null){
                return createNew(sysCode, fullName);
            } 
            //A DataSource can be registered against several fullNames. Make sure getFullName returns the last
            if (byCode.fullName != null && !byCode.fullName.equals(fullName) && !byCode.fullName.isEmpty()){
                byCode.alternativeFullNames.add(byCode.fullName);
                byCode.alternativeFullNames.remove(fullName);
                System.err.println("sysCode \"" + sysCode + " already used wtih fullName \"" + 
                    byCode.fullName + "\" which does not match new fullName \"" + fullName + "\"");
            }
            byCode.fullName = fullName;    
            return byCode;
        }
        if (sysCode == null || sysCode.isEmpty()){
            //No byCode possible and no interest in overwritting possible existing sysCode in byName
            return returnOrCreateNew(byName, sysCode, fullName);
        }
        if (fullName == null || fullName.isEmpty()){
            //No byName possible and no interest in overwritting possible existing fullName in byCode
            DataSource result = returnOrCreateNew(byCode, sysCode, fullName);
            return result;
        }
        if (byName == null){
            //byCode != null otherwise byName == byCode
            //keep track of the old fullName in byCode before overWriting it.
            if (byCode.fullName != null && !byCode.fullName.isEmpty()){
                byCode.alternativeFullNames.add(byCode.fullName);
                byCode.alternativeFullNames.remove(fullName);
                System.err.println("sysCode \"" + sysCode + " already used wtih fullName \"" + 
                    byCode.fullName + "\" which does not match new fullName \"" + fullName + "\"");
            }
            byCode.fullName = fullName;
            byFullName.put(fullName, byCode);
            return byCode;
        } else if (byCode == null) {
            if (byName.sysCode == null || byName.sysCode.isEmpty()){
                byName.sysCode = sysCode;
                return byName;
            }
            //No known case of fullName mapped to two sysCode
            throw new IllegalArgumentException("FullName " + fullName + " already mapped to " + byName 
                    + " which has a sysCode " + byName.sysCode + " so can not register with sysCode " + sysCode);
        } else {
            throw new IllegalArgumentException("Multiple possible DataSources found. " 
                + "SysCode " + sysCode + " already maps to " + byCode 
                + "while fullName " + fullName + " maps to " + byName);        
        }
    }

    /**
     * Helper method which check if the DataSources is null and either returns it or creates a new one.
     * 
     * Note: As this is a private method it is the callers responsibility to make sure that if not null 
     *    other.sysCode == susCode and other.fullName == fullName.
     *    Also to make sure no other DataSource exists with this sysCode or fullName
     * @param other A DataSource or null
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML. Must be 20 or less characters
     * @return An existing or new DataSource
     */
    private static DataSource returnOrCreateNew (DataSource other, String sysCode, String fullName){
        if (other != null) {
            return other;
        }
        return createNew(sysCode, fullName);
    }
    
    /**
     * Helper method that creates a new DataSource, sets the sysCode and fullName and registers it.
     * 
     * Currently this method does not add the DataSource to byFullName and bySysCode as it is only called indirectly by 
     *    Builder.register and that is already done there.
     * Note: As this is a private method it is the callers responsibility to make sure 
     *     no other DataSource exists with this sysCode or fullName
	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML. Must be 20 or less characters
     * @return 
     */
    private static DataSource createNew(String sysCode, String fullName){
        DataSource current = new DataSource ();
		registry.add (current);
		current.sysCode = sysCode;
		current.fullName = fullName;
        return current;
    }

   /**
     * Finds or creates a DataSource making sure that no other DataSource exists with the same (non null) fullName or 
     *    (non null) sysCode.
     *    but capturing when different fullNames are used for the same SysCode.
     * 
     * throws a IllegalArgumentException rather than registering alternative names.

     * Also now an Exception is thrown if sysCode and fullname where each already mapped to a different DataSources.
     *    Version 1 code would simply have used the DataSource mapped to FullName.
     * 
  	 * @param sysCode short unique code between 1-4 letters, originally used by GenMAPP
	 * @param fullName full name used in GPML. Must be 20 or less characters
     * @return An existing or new DataSource
     * @since Version 2 
     * @see DataSourceOverwriteLevel.
     * @throws IllegalArgumentException If this could result in two Possible DataSources or a DataSource with two sysCodes.
     */
    private static DataSource lookupDataSourceStrict(String sysCode, String fullName) {
        DataSource byName = byFullName.get(fullName);
        DataSource byCode = bySysCode.get(sysCode);
        if (byName == byCode){
            return returnOrCreateNew(byName, sysCode, fullName);
        }
        if ((sysCode == null || sysCode.isEmpty()) && (byName.sysCode == null || byName.sysCode.isEmpty())){
            return returnOrCreateNew(byName, sysCode, fullName);
        }
        if ((fullName == null || fullName.isEmpty()) && (byCode.fullName == null || byCode.fullName.isEmpty())){
            return returnOrCreateNew(byCode, sysCode, fullName);
        }
        throw new IllegalArgumentException("Multiple possible DataSources found. " 
                + "SysCode " + sysCode + " already maps to " + byCode 
                + " while fullName " + fullName + " maps to " + byName);
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
     * @since Version 1 However since Version 2 if no primary was set during building a default value of true is still returned.
     *     (in version 1 primary is automatically set to true.)
	 */
	public boolean isPrimary()
	{
        if (isPrimary == null){
            return true;
        }
		return isPrimary.booleanValue();
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
		if (!base.startsWith (MIRIAM_URN_ROOT))
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
			current = getByFullName(base.substring(MIRIAM_URN_ROOT.length()));
			current.urnBase = base;
			byMiriamBase.put (base, current);
		}
		return current;
	}

}
