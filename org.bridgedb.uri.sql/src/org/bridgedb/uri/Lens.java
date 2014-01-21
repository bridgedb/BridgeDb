// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
package org.bridgedb.uri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.rdf.RdfBase;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.DCTermsConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Alasdair and Christian
 */
public class Lens {

    private final String id;
    private String name;
    private String createdBy;
    private String createdOn;
    private String description;
    private final List<String> justifications;
    
    private static final String PROPERTIES_FILE = "lens.properties";
    public static final String DEFAULT_BAE_URI_KEY = "defaultLensBaseUri";
    
    private static HashMap<String,Lens> register = null;
    private static int nextNumber = -0; 
    
    private static final String ID_PREFIX = "L";
    public static final String METHOD_NAME = "Lens";
    public static final String URI_PREFIX = "/" + METHOD_NAME + "/";
    private static final String PROPERTY_PREFIX = "lens";
    
    private static final String CREATED_BY = "createdBy";
    private static final String CREATED_ON = "createdOn";
    private static final String DESCRIPTION = "description";
    private static final String JUSTIFICATION = "justification";
    private static final String NAME = "name";
    
    private static final String DEFAULT_LENS_NAME = "Default";
    private static final String TEST_LENS_NAME = "Test";
    private static final String ALL_LENS_NAME = "All";
      
    public static String defaultBaseUri;
    
    static final Logger logger = Logger.getLogger(Lens.class);

    /**
     * This methods should only be called by WS Clients as it Does not register the Lens 
     * 
     * Use factory method instead.
     * 
     * @param id
     * @param name
     * @param createdOn
     * @param createdBy
     * @param justifications 
     */
    public Lens(String id, String name, String createdOn, String createdBy, String description, Collection<String> justifications) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.description = description;
        this.justifications = new  ArrayList<String>(justifications);
    }

    private Lens(String id, String name) {
        this.name = name;
        this.id = id;
        this.justifications = new  ArrayList<String>();
        this.description = name + " lens";
        register(this);
        logger.info("Register " + this);
    }
 
     public static Lens byId(String id) throws BridgeDBException{
        if (id.contains(URI_PREFIX)){
            id = id.substring(id.indexOf(URI_PREFIX)+URI_PREFIX.length());
        }
        Lens result = lookupById(id);
        if (result == null){
            throw new BridgeDBException("No Lens known with Id " + id);
        }
        return result;
    }
    
    private static Lens findOrCreatedById(String id) throws BridgeDBException{
        Lens result = lookupById(id);
        if (result == null){
            result = new Lens(id, id);
        }
        return result;       
    }
    
    //Meathod currently not used but required if lens per justifcation is turned back on
    private static Lens findOrCreatedByName(String name) throws BridgeDBException{
        for (Lens lens:register.values()){
            if (lens.getName().equals(name)){
                return lens;
            }
        }
        String id;
        Lens check;
        do {
            id = ID_PREFIX + nextNumber;
            nextNumber++;
            check = lookupById(id.toLowerCase());
        } while (check != null);
        return new Lens(id, name);
    }

    public static List<String> getJustificationsbyId(String id) throws BridgeDBException{
        Lens lens = byId(id);
        return lens.getJustifications();
    }
    
    @Override
    public String toString(){
           return  "Lens Id: " + this.getId() + 
        		   " Name: " + this.getName() +
        		   " Created By: " + this.getCreatedBy() +
        		   " Created On: " + this.getCreatedOn() +
                   " Description: " + this.getDescription() + 
        		   " Justifications: " + this.getJustifications();
    }
    
    public static int getNumberOfLenses() throws BridgeDBException{
        init();
        return register.size();
    }

    /**
     * The Default lens is the one that should be used whenever lensUri is null.
     * <p>
     * The suggestion behaviour is that the default will the mappings that 
     *   are generally considered to apply in most situations, much as the Mappings in Version 1
     * This is not to say that these will only be Owl:sameAs mappings (as almost none are.)
     * <p>
     * However the default should not return mappings in catagories such as broader than, narrower than, 
     *    or where only the first half of the inch Strings match.
     * @return the DefaultUri as a String
     * @throws BridgeDBException 
     */
    public static String getDefaultLens() {
        return DEFAULT_LENS_NAME;
    }
    
    
    public static String getTestLens() throws BridgeDBException{
        return TEST_LENS_NAME;
    }
    
    /**
     * The lens used to indicate that all mappings should be returned.
     * <p>
     * @return A lens that asks for all mappings to be returned.
     * @throws BridgeDBException 
     */
    public static String getAllLens() throws BridgeDBException{
        return ALL_LENS_NAME;
    }
    

    /**
     * @return the Id
     */
    public String getId() {
        return id;
    }
    
    public String toUri(String baseUri){
        if (baseUri == null){
            return defaultBaseUri + URI_PREFIX + getId();
        }
        return baseUri + URI_PREFIX + getId();
    }
    
    /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @return the createdOn
	 */
	public String getCreatedOn() {
		return createdOn;
	}

	/**
	 * @return the justification
	 */
	public List<String> getJustifications() {
		return justifications;
	}

    public static String getDefaultJustifictaionString() {
       return "http://semanticscience.org/resource/CHEMINF_000059"; 
    }

    public static String getTestJustifictaion() {
        return "http://www.bridgedb.org/test#testJustification";
    }

    private static Lens lookupById(String id) throws BridgeDBException {
        if (register.isEmpty()){
            init();
        }
        return register.get(id.toLowerCase());
    }
    
    private void register(Lens lens){
        register.put(id.toLowerCase(), lens);
    }
    
    public static void init() throws BridgeDBException {
        if (register == null){
            logger.info("init");
            register = new HashMap<String,Lens>();
            //Create the all, default and test lens
            Lens all = findOrCreatedById(ALL_LENS_NAME);
            Lens defaultLens = findOrCreatedById(DEFAULT_LENS_NAME);
            Lens testLens = findOrCreatedById(TEST_LENS_NAME);
            Properties properties = ConfigReader.getProperties(PROPERTIES_FILE);
            Set<String> keys = properties.stringPropertyNames();
            for (String key:keys){
                if (key.startsWith(PROPERTY_PREFIX)){
                    String[] parts = key.split("\\.");
                    Lens lens = findOrCreatedById(parts[1]);
                    if (parts[2].equals(CREATED_BY)){
                        lens.setCreatedBy(properties.getProperty(key));
                    } else if (parts[2].equals(CREATED_ON)){
                        lens.setCreatedOn(properties.getProperty(key));
                    } else if (parts[2].equals(NAME)){
                        lens.name = properties.getProperty(key);
                    } else if (parts[2].equals(DESCRIPTION)){
                        lens.setDescription(properties.getProperty(key));
                    } else if (parts[2].equals(JUSTIFICATION)){
                        lens.addJustification(properties.getProperty(key));
                    } else {
                        logger.error("Found unexpected property " + key);
                    }
                }
            }
            all.setCreatedOn(new Date().toString());
            for (Lens lens:getLens()){
                all.addJustifications(lens.getJustifications());
            }
            all.addJustification(getDefaultJustifictaionString());
            all.addJustification(getTestJustifictaion());
            if (all.getDescription() == null || all.getDescription().isEmpty()){
                all.setDescription("Lens which includes all justfications.");
            }
            if (defaultLens.getJustifications().isEmpty()){
                defaultLens.addJustifications(all.getJustifications());
            }
            testLens.addJustification(getTestJustifictaion());
            defaultBaseUri = properties.getProperty(DEFAULT_BAE_URI_KEY);
        }
     }

  public static void init(UriMapper mapper) throws BridgeDBException {
        init();      
        Lens all = byId(Lens.getAllLens());
        //Code currently not used but allows lens per justifcation if turned back on
        Collection<String> justifications = mapper.getJustifications();
        for (String justification:justifications){
            all.addJustification(justification);
            //    Lens byName = findOrCreatedByName(justification);
            //    byName.setDescription("Lens with just the single jusification: " + justification);
            //    byName.addJustification(justification);
        }
        Lens defaultLens =  byId(Lens.getDefaultLens());
        if (defaultLens.getJustifications().isEmpty()){
            defaultLens.addJustifications(all.getJustifications());
        }
        if (defaultLens.getDescription() == null || defaultLens.getDescription().isEmpty()){
            defaultLens.setDescription("Lens which includes the default justfications.");
        }
   }

    public static List<Lens> getLens() throws BridgeDBException {
        init();
        List results = new ArrayList<Lens> (register.values());
        Lens defaultLens = byId(Lens.getDefaultLens());
        results.remove(defaultLens);
        results.add(0, defaultLens);
        return results;
    }

    private void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    private void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void addJustification(String justification) {
        if (!this.justifications.contains(justification)){
            this.justifications.add(justification);
        }
    }

    private void addJustifications(Collection<String> justifications) {
        for (String justification:justifications){
            addJustification(justification);
        }
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

  
}
