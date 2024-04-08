package org.bridgedb.bio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class XrefTest {

	@BeforeAll
	public static void setUpSources() {
		if (DataSource.getDataSources().size() == 0) DataSourceTxt.init();
	}

	@Test
	public void testCheckCorrectBioregistryIdentifier() {
		Xref xref = Xref.fromBioregistryIdentifier("ensembl:ENSGALG00000007562");
		assertNotNull(xref);
		assertEquals("En:ENSGALG00000007562:T", xref.toString());
	}

}
