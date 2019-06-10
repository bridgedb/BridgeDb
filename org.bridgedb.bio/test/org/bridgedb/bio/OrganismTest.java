/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2006-2009 BridgeDb Developers
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */

package org.bridgedb.bio;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class OrganismTest
{

    @BeforeEach
        public void setUp() {
    }

    @org.junit.jupiter.api.Test
    public void tasteWine() {
        Organism wine = Organism.VitisVinifera;
        // VitisVinifera ("Vitis vinifera", "Vv", "Wine Grape"),
        assertEquals("Vitis vinifera", wine.latinName());
        assertEquals("Vv", wine.code());
        assertEquals("Wine Grape", wine.shortName());
    }

    @org.junit.jupiter.api.Test
    public void testFromCode() {
        Organism org = Organism.fromCode("Zm");
        assertNotNull(org);
    }

    @org.junit.jupiter.api.Test
    public void testFromCodeNull() {
        Organism org = Organism.fromCode("Null");
        assertNull(org);
    }

    @org.junit.jupiter.api.Test
    public void testFromShortName() {
        Organism org = Organism.fromShortName("Frog");
        assertNotNull(org);
    }

    @org.junit.jupiter.api.Test
    public void testFromTaxonomyID() {
        Organism org = Organism.fromTaxonomyId(9606);
        assertNotNull(org);
        assertEquals("Homo sapiens", org.latinName());
    }

    @org.junit.jupiter.api.Test
    public void testFromNullShortName() {
        Organism org = Organism.fromShortName("Null");
        assertNull(org);
    }

    @org.junit.jupiter.api.Test
    public void testCodes() {
        String[] codes = Organism.codes();
        assertNotNull(codes);
        assertNotSame(0, codes.length);
    }

    @org.junit.jupiter.api.Test
    public void testUniqueLatinNames() {
	   Organism[] organisms = Organism.values();
	   Set<String> latinNames = new HashSet<String>();
	   for (Organism organism : organisms) {
		   latinNames.add(organism.latinName());
	   }
	   assertEquals(organisms.length, latinNames.size());
   }

    @org.junit.jupiter.api.Test
    public void testUniqueCodes() {
	   Organism[] organisms = Organism.values();
	   Set<String> codes = new HashSet<String>();
	   for (Organism organism : organisms) {
		   codes.add(organism.code());
	   }
	   assertEquals(organisms.length, codes.size());
   }

    @org.junit.jupiter.api.Test
    public void tasteHuman() {
       Organism wine = Organism.HomoSapiens;
       assertEquals("Homo sapiens", wine.latinName());
       assertEquals("9606", wine.taxonomyID().getId());
   }
}
