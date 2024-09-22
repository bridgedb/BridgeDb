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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.bridgedb.IDMapperException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BridgeRestTest {

	@BeforeAll
    public static void init() {
		
	}

	@Test
	public void test() throws IDMapperException {
		BridgeRest service = new BridgeRest("https://webservice.bridgedb.org/Human");
		assertNotNull(service);
	}
}
