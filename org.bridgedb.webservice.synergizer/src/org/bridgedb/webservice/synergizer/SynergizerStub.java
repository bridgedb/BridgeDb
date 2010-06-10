// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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

package org.bridgedb.webservice.synergizer;

import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.IDMapperException;
import org.json.JSONException;

import synergizer.SynergizerClient;

/**
 * Cache for SynergizerClient
 * @author gjj
 */
public class SynergizerStub {
    public static final String defaultBaseURL
            = SynergizerClient.defaultServiceURLString;

    // cache data
    private Map<String,Map<String,Map<String,Set<String>>>>
            mapAuthSpeciesDomainRange = null;

    // one instance per url
    private static Map<String, SynergizerStub> instances = new HashMap();

    /**
     *
     * @return SynergizerStub with the default server url
     * @throws IOException if failed to connect
     */
    public static SynergizerStub getInstance() throws IOException {
        return getInstance(defaultBaseURL);
    }

    /**
     *
     * @param baseUrl server url
     * @return SynergizerStub from the server
     * @throws IOException if failed to connect
     */
    public static SynergizerStub getInstance(String baseUrl) throws IOException {
        if (baseUrl==null) {
            throw new IllegalArgumentException("base url cannot be null");
        }

        SynergizerStub instance = instances.get(baseUrl);
        if (instance==null) {
            instance = new SynergizerStub(baseUrl);
            instances.put(baseUrl, instance);
        }

        return instance;
    }

    private SynergizerClient client;

    /**
     *
     * @param baseUrl server url.
     * @throws IOException if failed to connect.
     */
    private SynergizerStub(String baseUrl) throws IOException {
        client = new SynergizerClient(new URL(baseUrl));
    }

    /**
     *
     * @return available authorities
     * @throws IDMapperException if failed
     */
    public Set<String> availableAuthorities() throws IDMapperException {
        if (mapAuthSpeciesDomainRange==null) {
            Set<String> auths;
            try {
                auths = client.availableAuthorities();
            } catch (IOException e) {
                throw new IDMapperException(e);
            } catch (JSONException e) {
                throw new IDMapperException(e);
            }

            mapAuthSpeciesDomainRange = new HashMap();
            for (String auth : auths) {
                mapAuthSpeciesDomainRange.put(auth, null);
            }
        }

        return mapAuthSpeciesDomainRange.keySet();
    }

    /**
     *
     * @param authority authority name
     * @return available species from this authority
     * @throws IDMapperException if failed
     */
    public Set<String> availableSpecies(String authority)
            throws IDMapperException {
        if (authority==null) {
            return new HashSet(0);
        }

        if (!availableAuthorities().contains(authority)) {
            return new HashSet(0);
        }

        Map<String,Map<String,Set<String>>> mapSpeciesDomainRange
                = mapAuthSpeciesDomainRange.get(authority);
        if (mapSpeciesDomainRange==null) {
            Set<String> species;
            try {
                species = client.availableSpecies(authority);
            } catch (IOException e) {
                throw new IDMapperException(e);
            } catch (JSONException e) {
                throw new IDMapperException(e);
            }

            mapSpeciesDomainRange = new HashMap();
            for (String sp : species) {
                mapSpeciesDomainRange.put(sp, null);
            }
            mapAuthSpeciesDomainRange.put(authority, mapSpeciesDomainRange);
        }

        return mapSpeciesDomainRange.keySet();
    }

    /**
     *
     * @param authority authority name
     * @param species species name
     * @return available domains / source id types of the species from this
     *         authority
     * @throws IDMapperException if failed
     */
    public Set<String> availableDomains(final String authority,
            final String species) throws IDMapperException {
        if (authority==null || species==null) {
            return new HashSet(0);
        }

        if (!availableSpecies(authority).contains(species)) {
            return new HashSet(0);
        }

        Map<String,Set<String>> mapDomainRange
                = mapAuthSpeciesDomainRange.get(authority).get(species);
        if (mapDomainRange==null) {
            Set<String> domains;
            try {
                domains = client.availableDomains(authority, species);
            } catch (IOException e) {
                throw new IDMapperException(e);
            } catch (JSONException e) {
                throw new IDMapperException(e);
            }

            mapDomainRange = new HashMap();
            for (String domain : domains) {
                mapDomainRange.put(domain, null);
            }
            mapAuthSpeciesDomainRange.get(authority)
                    .put(species, mapDomainRange);
        }

        return mapDomainRange.keySet();
    }

    /**
     *
     * @param authority authority name
     * @param species species name
     * @param domain domain name / source id type
     * @return range names / target id types for the source domain of species
     *         from the authority.
     * @throws IDMapperException if failed.
     */
    public Set<String> availableRanges(final String authority,
            final String species, final String domain)
            throws IDMapperException {

        if (authority==null || species==null || domain==null) {
            return new HashSet(0);
        }

        if (!availableDomains(authority, species).contains(domain)) {
            return new HashSet(0);
        }

        Set<String> ranges = mapAuthSpeciesDomainRange.get(authority)
                .get(species).get(domain);
        if (ranges==null) {
            try {
                ranges = client.availableRanges(authority, species, domain);
            } catch (IOException e) {
                throw new IDMapperException(e);
            } catch (JSONException e) {
                throw new IDMapperException(e);
            }

            mapAuthSpeciesDomainRange.get(authority).get(species)
                    .put(domain, ranges);
        }

        return ranges;
    }

    /**
     * Wrap the tranlate method in SynergizerClient
     * @param authority authority name
     * @param species species name
     * @param domain domain name / source id type
     * @param range range name / target id type
     * @param ids source ids to be translated
     * @return map from source id to target ids
     *         key: source id
     *         value: corresponding target ids
     *                null if the source id is not exist
     * @throws IDMapperException
     */
    public Map<String,Set<String>> translate(final String authority,
            final String species, final String domain, final String range,
            final Set<String> ids) throws IDMapperException {
        SynergizerClient.TranslateResult res;
        try {
             res = client.translate(authority, species, domain, range, ids);
        } catch (IOException e) {
            throw new IDMapperException(e);
        } catch (JSONException e) {
            throw new IDMapperException(e);
        }

        return res.translationMap();
    }

}
