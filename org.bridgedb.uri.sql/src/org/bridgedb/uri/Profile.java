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

import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.utils.BridgeDBException;

/**
 * This is just a Utils class to provide any default Profiles as well as a single Javadocs point.
 * <p>
 * 
 * @author Christian
 */
public class Profile {
    
    /**
     * The Default profile is the one that should be used whenever profileUri is null.
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
    public static String getDefaultProfile() throws BridgeDBException{
        return RdfConfig.getProfileURI(0);
    }
    
    /**
     * The profile used to indicate that all mappings should be returned.
     * <p>
     * @return A Profile that asks for all mappings to be returned.
     * @throws BridgeDBException 
     */
    public static String getAllProfile() throws BridgeDBException{
        return RdfConfig.getProfileURI(-1);
    }
}
