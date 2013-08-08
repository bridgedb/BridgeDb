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
package org.bridgedb.bio;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrganismTest
{

    @Before
        public void setUp() {
    }

    @Test
    public void tasteWine() {
        Organism wine = Organism.VitisVinifera;
        // VitisVinifera ("Vitis vinifera", "Vv", "Wine Grape"),
        Assert.assertEquals("Vitis vinifera", wine.latinName());
        Assert.assertEquals("Vv", wine.code());
        Assert.assertEquals("Wine Grape", wine.shortName());
    }

    @Test
    public void testFromCode() {
        Organism org = Organism.fromCode("Zm");
        Assert.assertNotNull(org);
    }

    @Test
    public void testFromCodeNull() {
        Organism org = Organism.fromCode("Null");
        Assert.assertNull(org);
    }

    @Test
    public void testFromShortName() {
        Organism org = Organism.fromShortName("Frog");
        Assert.assertNotNull(org);
    }

    @Test
    public void testFromNullShortName() {
        Organism org = Organism.fromShortName("Null");
        Assert.assertNull(org);
    }

    @Test
    public void testCodes() {
        String[] codes = Organism.codes();
        Assert.assertNotNull(codes);
        Assert.assertNotSame(0, codes.length);
    }

   @Test
   public void testUniqueLatinNames() {
	   Organism[] organisms = Organism.values();
	   Set<String> latinNames = new HashSet<String>();
	   for (Organism organism : organisms) {
		   latinNames.add(organism.latinName());
	   }
	   Assert.assertEquals(organisms.length, latinNames.size());
   }

   @Test
   public void testUniqueCodes() {
	   Organism[] organisms = Organism.values();
	   Set<String> codes = new HashSet<String>();
	   for (Organism organism : organisms) {
		   codes.add(organism.code());
	   }
	   Assert.assertEquals(organisms.length, codes.size());
   }
}
