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
package org.bridgedb.uri.lens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.Reporter;

/**
 *
 * @author Alasdair and Christian
 */
public class Lens {

    private final String id;
    private String name;
    private String createdBy;
    private XMLGregorianCalendar createdOn;
    private String description;
    private final List<String> justifications;
    private final Set<DataSource> allowedMiddleSources;
    private final Set<String> allowedMiddleSysCodes;
        
    public static final String DEFAULT_LENS_NAME = "Default";
    public static final String TEST_LENS_NAME = "Test";
    public static final String ALL_LENS_NAME = "All";      
    public static final String METHOD_NAME = "Lens";
    public static final String URI_PREFIX = "/" + METHOD_NAME + "/";
    
    private static String defaultBaseUri = "";

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
    public Lens(String id, String name, String createdOn, String createdBy, String description,
            Collection<String> justifications, Collection<DataSource> allowedMiddleSources) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.setCreatedOn(createdOn);
        this.description = description;
        this.justifications = new ArrayList<String>(justifications);
        this.allowedMiddleSources = new HashSet<DataSource>();
        allowedMiddleSysCodes = new HashSet<String>();
        this.addAllowedMiddleSources(allowedMiddleSources);
    }

    protected Lens(String id, String name) throws BridgeDBException {
        this.name = name;
        this.id = id;
        this.justifications = new  ArrayList<String>();
        this.allowedMiddleSources = new HashSet<DataSource>();
        allowedMiddleSysCodes = new HashSet<String>();
        this.description = name + " lens";
        this.setCreatedOnNow();
        createdBy = "constructor";
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
    
     /**
     * @return the Id
     */
    public String getId() {
        return id;
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
    public XMLGregorianCalendar getCreatedOn() {
        return createdOn;
    }

    /**
      * @return the justification
      */
    public List<String> getJustifications() {
        return justifications;
    }

    /**
      * @return the Allowed Middle Sources
      */
    public Set<DataSource> getAllowedMiddleSources() {
        return this.allowedMiddleSources;
    }

    /**
      * @return the Allowed Middle Sources
      */
    public Set<String> getAllowedMiddleSysCodes() {
        return this.allowedMiddleSysCodes;
    }

    public static String getDefaultJustifictaionString() {
       return "http://semanticscience.org/resource/CHEMINF_000059"; 
    }

    public static String getTestJustifictaion() {
        return "http://www.bridgedb.org/test#testJustification";
    }

    final void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    private void setCreatedOn(XMLGregorianCalendar createdOn) {
        this.createdOn = createdOn;
    }

    protected final void setCreatedOn(String createdOnString) {
        try { 
            this.createdOn = DatatypeFactory.newInstance().newXMLGregorianCalendar(createdOnString);
        } catch (DatatypeConfigurationException ex) {
            Reporter.error("Unable to convert " + createdOnString,ex);
            setCreatedOnNow();
        }
    }

    protected final void setDescription(String description) {
        this.description = description;
    }

    protected final void addJustification(String justification) {
        if (!this.justifications.contains(justification)){
            this.justifications.add(justification);
        }
    }

    protected final void addJustifications(Collection<String> justifications) {
        for (String justification:justifications){
            addJustification(justification);
        }
    }

    public final void addAllowedMiddleSource(DataSource dataSource) {
        allowedMiddleSources.add(dataSource);
        allowedMiddleSysCodes.add(dataSource.getSystemCode());
    }

    public final void addAllowedMiddleSource(String allowedMiddleSource) {
        DataSource dataSource = DataSource.getExistingByFullName(allowedMiddleSource);
        addAllowedMiddleSource(dataSource);
    }

    protected final void addAllowedMiddleSources(Collection<DataSource> dataSources) {
        for (DataSource dataSource:dataSources){
            addAllowedMiddleSource(dataSource);
        }
    }

    protected final void setName(String newName) {
        name = newName;
    }

   /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    private void setCreatedOnNow() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
            setCreatedOn(now);
        } catch (DatatypeConfigurationException ex) {
            Reporter.error("Unable to set createdBy now! ", ex);
        }
   }
    
    public String toUri(String baseUri){
        if (baseUri == null){
            return defaultBaseUri + URI_PREFIX + getId();
        }
        return baseUri + URI_PREFIX + getId();
    }
    
    static void setDefaultBaseUri(String baseUri) {
        defaultBaseUri = baseUri;
    }


  
}
