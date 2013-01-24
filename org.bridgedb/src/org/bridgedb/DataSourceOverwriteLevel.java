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
package org.bridgedb;

/**
 * Sets the behaviour of the DataSource model at register time.
 * 
 * Only the Register method and builder methods are effecting. 
 * Changing the level should never change the information in an existing DataSource.
 * 
 * This modification is not yet finalized nor approved by the Whole BridgeDB community.
 * Until this message is removed use of new features is at the users own risk.
 * 
 * @author Christian
 * @since Version 2
 */
public enum DataSourceOverwriteLevel {
    /**
     * The behaviour is a close to the behaviour displayed in Version 1 as possible.
     * 
     * For example Values can and will be overwritten with no warning to the users.
     * 
     * This level is used for people and projects who are used to version 1 and don't want any behaviour or information to change.
     */ 
    VERSION1,
    /**
     * The behaviour is a close to the behaviour displayed in Version 1 with a few key exceptions.
     * 
     * 1. If a fullName is overridden the previous fullName is kept as an alternative name
     * 2. If a non null none empty sysCode is overridden an exception is thrown.
     *    a. This behaviour was implemented as there is no know case of this occurring.
     * 3. If the UrnBase is overridden with a different value the two values are compared.
     *    a. If neither starts with urn:miriam: the newer one is kept as in Version 1
     *    b. If one starts with urn:miriam: that one is used.
     *    c. If both start with urn:miriam: an exception is thrown. 
     * 
     * Farther changes to Version 1 can and should be implemented as required.
     * 
     * This level is intended to capture as much of the original information as possible especially from the BIO package,
     *    Without having to manually change the original data, without exceptions being thrown 
     *    and with limited and controlled changes to the behaviour
     */ 
    CONTROLLED,
    //Additional levels can be added to suit individual needs. 
    //REMEMBER TO CHECK FOR ALL switch (overwriteLevel) calls.
    /**
     * This is the strictest level where no values can be overridden.
     * 
     * Use of this level requires clean up information to be used.
     * 
     * It helps control that no information will be lost and helps catch inconsistencies in the data.
     * However it does so by throwing exception which will break existig code!
     * 
     * At the time of writing (Jan 2013) the Data in the Bio package (both svn and git) could not be loaded with 
     * this level of strictness. While it may be possible to tidy up the data in the BIO package to meet this strict level,
     * care must be taken to not break any behaviour depended on by other users.
     */
    STRICT;
}
