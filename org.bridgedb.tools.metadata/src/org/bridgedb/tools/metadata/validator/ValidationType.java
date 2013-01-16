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
package org.bridgedb.tools.metadata.validator;

import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public enum ValidationType {
    ANY_RDF ("LinkSet.owl", "Rdf", "http://example.com" ,false, true),
    VOID ("LinkSet.owl", "Void", "http://rdfs.org/ns/void#Dataset", false, false),
    LINKS("LinkSet.owl", "LinkSet", "http://rdfs.org/ns/void#Linkset", true, false),
    //todo make minal set
    LINKSMINIMAL("LinkSet.owl", "Minimum", "http://rdfs.org/ns/void#Linkset", true, true);
   
    private final String owlFile;
    private final String name;
    private final URI directType;
    private final boolean linkset;
    private final boolean minimal;
    
    private ValidationType(String owlFile, String name, String type, boolean linkset, boolean isMinimal){
        this.owlFile = owlFile;
        this.name = name;
        this.directType = new URIImpl(type);
        this.linkset = linkset;
        this.minimal = isMinimal;
    }
    
    public static ValidationType parseString(String string) throws BridgeDBException{
       for(ValidationType type:ValidationType.values()){
           if (type.toString().equalsIgnoreCase(string)){
               return type;
           }
       }
       throw new BridgeDBException ("Unable to parse " + string + " to a ValidationType. "
               + "Legal values are " + valuesString());
    }
    
    public static String valuesString(){
        String result = ValidationType.values()[0].toString();
        for (int i = 1; i< ValidationType.values().length; i++){
            result = result + ", " + ValidationType.values()[i].toString();
        }
        return result;
    }
    
    public String getOwlFileName(){
        return owlFile;
    }
    
    public String getName(){
        return name;
    }
    
    public URI getDirectType(){
        return directType;
    }
    
    public boolean isLinkset(){
        return linkset;
    }
    
    public boolean isMinimal(){
        return this.minimal;
    }
}
