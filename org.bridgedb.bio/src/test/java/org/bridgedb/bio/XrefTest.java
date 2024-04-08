package org.bridgedb.bio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.bridgedb.Xref;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class XrefTest {

	@BeforeAll
	public static void setUpSources() {
		DataSourceTxt.init();
	}

	@Test
	public void testCheckCorrectBioregistryIdentifier() {
		Xref xref = Xref.fromBioregistryIdentifier("ensembl:ENSGALG00000007562");
		assertNotNull(xref);
		assertEquals("En:ENSGALG00000007562:T", xref.toString());
	}

}
