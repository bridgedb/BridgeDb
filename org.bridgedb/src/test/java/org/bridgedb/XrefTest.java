/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2013 Egon Willighagen <egonw@users.sf.net>
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */
package org.bridgedb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class XrefTest {

	private static DataSource EN;
	private static DataSource UNIPROT;
	private static DataSource CHEBI;
	
    @BeforeAll
    public static void registerDataSources() {
        XrefTest.EN = DataSource.mock("En", "Ensembl").
                urnBase("urn:miriam:ensembl").
                asDataSource();
    	XrefTest.CHEBI = DataSource.mock("Ce", "ChEBI").
    			urnBase("urn:miriam:chebi").
    		    bioregistryPrefix("chebi").
    		    asDataSource();
    	XrefTest.UNIPROT = DataSource.mock("S", "Uniprot-TrEMBL").
    		    urnBase("urn:miriam:uniprot").
    		    bioregistryPrefix("uniprot").
    		    asDataSource();
    }

	@Test
	public void testConstructor() {
		Xref xref = new Xref("ENSG000001", EN);
		assertNotNull(xref);
		assertEquals("ENSG000001", xref.getId());
		assertNotNull(xref.getDataSource());
		assertEquals("En", xref.getDataSource().getSystemCode());
	}

	@Test
	public void testGetMiriamURN() {
		Xref xref = new Xref("ENSG000001", EN);
		assertEquals("urn:miriam:ensembl:ENSG000001", xref.getMiriamURN());
		Xref xref2 = Xref.fromMiriamUrn("urn:miriam:ensembl:ENSG000001");
		assertEquals(xref.getDataSource().getSystemCode(), xref2.getDataSource().getSystemCode());
		assertEquals(xref.getId(), xref2.getId());
	}

	@Test
	public void testEquals_Null() {
		Xref xref = new Xref("ENSG000001", EN);
		assertNotEquals(null, xref);
		assertNotEquals(xref, null);
	}

	@Test
	public void testEquals_NonXref() {
		Xref xref = new Xref("ENSG000001", EN);
		assertNotEquals("ENSG000001", xref);
	}

	@Test
	public void testEquals_DiffId() {
		Xref xref = new Xref("ENSG000001", EN);
		Xref xref2 = new Xref("ENSG000002", EN);
		assertNotEquals(0, xref.compareTo(xref2));
		assertNotEquals(0, xref.compareTo(xref2));
	}

	@Test
	public void testEquals() {
		Xref xref = new Xref("ENSG000001", EN);
		Xref xref2 = new Xref("ENSG000001", EN);
		assertEquals(0, xref2.compareTo(xref));
		assertEquals(0, xref.compareTo(xref2)); // and symmetric
		Xref xref3 = new Xref("ENSG000001", EN, true);
		Xref xref4 = new Xref("ENSG000001", EN, false);
		assertFalse( xref3.equals(xref4));
	}

	@Test
	public void testCompareTo() {
		Xref xref1 = new Xref("ENSG000001", EN);
		Xref xref2 = new Xref("ENSG000001", EN);
		assertEquals(0, xref1.compareTo(xref2));
		assertEquals(0, xref2.compareTo(xref1)); // and symmetric
		Xref xref3 = new Xref("ENSG000001", EN, true);
		Xref xref4 = new Xref("ENSG000001", EN, false);
		assertFalse(xref3.equals(xref4));
		assertFalse(xref4.equals(xref3)); // and symmetric
	}

	@Test
	public void testCompareTo_Diff() {
		Xref xref = new Xref("ENSG000001", EN);
		Xref xref2 = new Xref("ENSG000002", EN);
		assertNotSame(0, xref.compareTo(xref2));
		assertNotSame(0, xref2.compareTo(xref)); // and symmetric
	}

	@Test
	public void testGetDataSource() {
		Xref xref = new Xref("ENSG000001", EN);
		assertEquals(EN, xref.getDataSource());
	}

	@Test
	public void testGetId() {
		Xref xref = new Xref("P12345", UNIPROT);
		assertEquals("P12345", xref.getId());
	}

	@Test
	public void testGetCompactidentifier() {
		Xref xref = new Xref("P12345", UNIPROT);
		assertEquals("uniprot:P12345", xref.getCompactidentifier());
	}

	@Test
	public void testFromCompactidentifier() {
		Xref xref = Xref.fromCompactIdentifier("uniprot:P12345");
		assertEquals(UNIPROT, xref.getDataSource());
		assertEquals("P12345", xref.getId());
	}

	@Test
	public void testFromCompactidentifier_MissingColon() {
		Xref xref = Xref.fromCompactIdentifier("uniprot/P12345");
		assertNull(xref);
	}

	@Test
	public void testFromCompactidentifier_UnknownDataSource() {
		Xref xref = Xref.fromCompactIdentifier("unifrot:P12345");
		assertNull(xref);
	}

	@Test
	public void testGetBioregistryIdentifier() {
		Xref xref = new Xref("P12345", UNIPROT);
		assertEquals("uniprot:P12345", xref.getBioregistryIdentifier());
	}

	@Test
	public void testGetBioregistryIdentifier_17855() {
		Xref xref = new Xref("CHEBI:17855", CHEBI);
		assertEquals("chebi:17855", xref.getBioregistryIdentifier());
		xref = new Xref("17855", CHEBI);
		assertEquals("chebi:17855", xref.getBioregistryIdentifier());
	}

	@Test
	public void testFromBioregistryIdentifier() {
		Xref xref = Xref.fromBioregistryIdentifier("uniprot:P12345");
		assertEquals(UNIPROT, xref.getDataSource());
		assertEquals("P12345", xref.getId());
	}

	@Test
	public void testFromBioregistryIdentifier_MissingColon() {
		Xref xref = Xref.fromBioregistryIdentifier("uniprot/P12345");
		assertNull(xref);
	}

	@Test
	public void testFromBioregistryIdentifier_UnknownDataSource() {
		Xref xref = Xref.fromBioregistryIdentifier("unifrot:P12345");
		assertNull(xref);
	}

	@Test
	public void testToString() {
		assertEquals("Ce:CHEBI:17855:T", new Xref("CHEBI:17855", CHEBI, true).toString());
		assertEquals("Ce:CHEBI:17855:F", new Xref("CHEBI:17855", CHEBI, false).toString());
	}

	@Test
	public void testGetKnownUrl() {
		System.out.println(new Xref("CHEBI:17855", CHEBI, true).getKnownUrl());
	}

}
