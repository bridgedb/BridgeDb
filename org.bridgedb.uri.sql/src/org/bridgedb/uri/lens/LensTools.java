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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.log4j.Logger;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.DCTermsConstants;
import org.bridgedb.rdf.constants.PavConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.ConfigReader;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Alasdair and Christian
 */
public class LensTools {

    private static final String PROPERTIES_FILE = "lens.properties";
    public static final String DEFAULT_BAE_URI_KEY = "defaultLensBaseUri";
    
    private static HashMap<String,Lens> register = null;
    private static int nextNumber = -0; 
    
    private static final String ID_PREFIX = "L";
    private static final String PROPERTY_PREFIX = "lens";
   
    private static final String CREATED_BY = "createdBy";
    private static final String CREATED_ON = "createdOn";
    private static final String DESCRIPTION = "description";
    private static final String JUSTIFICATION = "justification";
    private static final String NAME = "name";
    
    
    static final Logger logger = Logger.getLogger(LensTools.class);

    private static Lens lensFactory(String id, String name) throws BridgeDBException {
        Lens lens = new Lens(id, name);
        register(lens);
        logger.info("Register " + lens);
        return lens;
    }
 
    private static String cleanId(String id){
        if (id.contains(Lens.URI_PREFIX)){
            return id.substring(id.indexOf(Lens.URI_PREFIX)+Lens.URI_PREFIX.length());
        }        
        return id;
    }
    
    public static Lens byId(String id) throws BridgeDBException{
        id = cleanId(id);
        Lens result = lookupById(id);
        if (result == null){
            throw new BridgeDBException("No Lens known with Id " + id);
        }
        return result;
    }
    
    public static boolean isAllLens(String id){
        id = cleanId(id);
        return (Lens.ALL_LENS_NAME.equals(id));
    }
    
    private static Lens findOrCreatedById(String id) throws BridgeDBException{
        Lens result = lookupById(id);
        if (result == null){
            result = lensFactory(id, id);
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
        return lensFactory(id, name);
    }

    public static List<String> getJustificationsbyId(String id) throws BridgeDBException{
        Lens lens = byId(id);
        return lens.getJustifications();
    }
    
    public static int getNumberOfLenses() throws BridgeDBException{
        init();
        return register.size();
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
    
    private static void register(Lens lens){
        register.put(lens.getId().toLowerCase(), lens);
    }
    
    public static void init() throws BridgeDBException {
        if (register == null){
            logger.info("init");
            register = new HashMap<String,Lens>();
            //Create the all, default and test lens
            Lens all = findOrCreatedById(Lens.ALL_LENS_NAME);
            Lens defaultLens = findOrCreatedById(Lens.DEFAULT_LENS_NAME);
            Lens testLens = findOrCreatedById(Lens.TEST_LENS_NAME);
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
                        lens.setName(properties.getProperty(key));
                    } else if (parts[2].equals(DESCRIPTION)){
                        lens.setDescription(properties.getProperty(key));
                    } else if (parts[2].equals(JUSTIFICATION)){
                        lens.addJustification(properties.getProperty(key));
                    } else {
                        logger.error("Found unexpected property " + key);
                    }
                }
            }
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
            Lens.setDefaultBaseUri(properties.getProperty(DEFAULT_BAE_URI_KEY));
        }
     }

  public static void init(UriMapper mapper) throws BridgeDBException {
        init();      
        Lens all = byId(Lens.ALL_LENS_NAME);
        //Code currently not used but allows lens per justifcation if turned back on
        Collection<String> justifications = mapper.getJustifications();
        for (String justification:justifications){
            all.addJustification(justification);
            //    Lens byName = findOrCreatedByName(justification);
            //    byName.setDescription("Lens with just the single jusification: " + justification);
            //    byName.addJustification(justification);
        }
        Lens defaultLens =  byId(Lens.DEFAULT_LENS_NAME);
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
        Lens defaultLens = byId(Lens.DEFAULT_LENS_NAME);
        results.remove(defaultLens);
        results.add(0, defaultLens);
        return results;
    }

    public static Set<Statement> getLensAsRdf(String baseUri) {
        HashSet<Statement> results = new HashSet<Statement>();
        for (Lens lens:register.values()){
            results.addAll(asRdf(lens, baseUri));
        }
        return results;
    }
    
    public static Set<Statement> asRdf(Lens lens, String baseUri) {
        HashSet<Statement> results = new HashSet<Statement>();
        URI subject = new URIImpl(lens.toUri(baseUri));
        results.add(new StatementImpl(subject, RdfConstants.TYPE_URI, BridgeDBConstants.LENS_URI));
        results.add(new StatementImpl(subject, DCTermsConstants.TITLE_URI, new LiteralImpl(lens.getName())));
        results.add(new StatementImpl(subject, DCTermsConstants.DESCRIPTION_URI, new LiteralImpl(lens.getDescription())));
        results.add(new StatementImpl(subject, PavConstants.CREATED_BY, new LiteralImpl(lens.getCreatedBy())));
        CalendarLiteralImpl createdOnLiteral = new CalendarLiteralImpl(lens.getCreatedOn());
        results.add(new StatementImpl(subject, PavConstants.CREATED_ON, createdOnLiteral));
        for (String justification:lens.getJustifications()){
            results.add(new StatementImpl(subject, BridgeDBConstants.LINKSET_JUSTIFICATION, new URIImpl(justification)));
        }
        return results;
    }
}
