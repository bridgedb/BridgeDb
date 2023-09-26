// Copyright (c) 2022 Egon Willighagen 
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
package org.bridgedb.resolvers;

import java.net.MalformedURLException;
import java.net.URL;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.junit.Assert;
import org.junit.Test;

abstract class AbstractResolverTest {

	static IResolver resolver = null;

	@Test
	public void getURL() throws MalformedURLException {
		Xref xref = new Xref("138488", DataSource.register("Ce", "ChEBI").compactIdentifierPrefix("chebi").asDataSource());
		String urlStr = resolver.getURL(xref);
		Assert.assertNotNull(urlStr);
		Assert.assertTrue(urlStr.contains("chebi:138488"));
		URL url = new URL(urlStr);
		Assert.assertNotNull(url);
	}
	
}
