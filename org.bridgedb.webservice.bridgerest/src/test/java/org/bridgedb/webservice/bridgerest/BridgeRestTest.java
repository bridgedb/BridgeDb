// Copyright 2024  Egon Willighagen <egonw@users.sf.net>
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
package org.bridgedb.webservice.bridgerest;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.DataSourceTxt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BridgeRestTest {

	private static BridgeRest service;

	@BeforeAll
    public static void init() throws IDMapperException {
		if (DataSource.getDataSources().size() == 0) DataSourceTxt.init();
		BridgeRestTest.service = new BridgeRest("https://webservice.bridgedb.org/Human");
		assertNotNull(service);
		assertTrue(service.isConnected());
	}

	@Test
	public void testMap() throws IDMapperException {
		assertNotNull(service);
		assertTrue(service.isConnected());
		Set<Xref> mappings = service.mapID(new Xref("CHEBI:123", DataSource.getExistingBySystemCode("Ce")));
		assertNotSame(0, mappings.size());
	}
}
