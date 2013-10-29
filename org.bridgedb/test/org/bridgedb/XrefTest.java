// Copyright      2013  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.Assert;
import org.junit.Test;

public class XrefTest {

    //Oct 29, 2013 Name changed to match that used elsewhere
	private final DataSource EN = DataSource.register("En", "Ensembl").asDataSource();
	private final DataSource UNIPROT = DataSource.register("S", "Uniprot-TrEMBL").
		urnBase("urn:miriam:uniprot:").asDataSource();

	@Test
	public void testConstructor() {
		Xref xref = new Xref("ENSG000001", EN);
		Assert.assertNotNull(xref);
		Assert.assertEquals("ENSG000001", xref.getId());
		Assert.assertNotNull(xref.getDataSource());
		Assert.assertEquals("En", xref.getDataSource().getSystemCode());
	}

	@Test
	public void testEquals_Null() {
		Xref xref = new Xref("ENSG000001", EN);
		Assert.assertFalse(xref.equals(null));
	}

	@Test
	public void testEquals_NonXref() {
		Xref xref = new Xref("ENSG000001", EN);
		Assert.assertFalse(xref.equals("ENSG000001"));
	}

	@Test
	public void testEquals_DiffId() {
		Xref xref = new Xref("ENSG000001", EN);
		Xref xref2 = new Xref("ENSG000002", EN);
		Assert.assertFalse(xref.equals(xref2));
		Assert.assertFalse(xref2.equals(xref)); // and symmetric
	}

	@Test
	public void testEquals() {
		Xref xref = new Xref("ENSG000001", EN);
		Xref xref2 = new Xref("ENSG000001", EN);
		Assert.assertTrue(xref.equals(xref2));
		Assert.assertTrue(xref2.equals(xref)); // and symmetric
	}

	@Test
	public void testCompareTo() {
		Xref xref = new Xref("ENSG000001", EN);
		Xref xref2 = new Xref("ENSG000001", EN);
		Assert.assertEquals(0, xref.compareTo(xref2));
		Assert.assertEquals(0, xref2.compareTo(xref)); // and symmetric
	}

	@Test
	public void testCompareTo_Diff() {
		Xref xref = new Xref("ENSG000001", EN);
		Xref xref2 = new Xref("ENSG000002", EN);
		Assert.assertNotSame(0, xref.compareTo(xref2));
		Assert.assertNotSame(0, xref2.compareTo(xref)); // and symmetric
	}

	@Test
	public void testURNRoundtripping() {
		Xref xref = new Xref("P12345", UNIPROT);
		String urn = xref.getURN();
		Assert.assertNotNull(urn);
		Assert.assertNotSame(0, urn.length());
		Xref xref2 = Xref.fromUrn(urn);
		Assert.assertTrue(xref.equals(xref2));
		Assert.assertTrue(xref2.equals(xref)); // and symmetric
	}
}
