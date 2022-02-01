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

import org.bridgedb.Xref;

public class Names2ThingsResolver implements IResolver {

	private static IResolver me;
	
	private Names2ThingsResolver() {}
	
	public static IResolver getInstance() {
		if (me == null) me = new Names2ThingsResolver();
		return me;
	}
	
	@Override
	public String getURL(Xref xref) {
		return "http://n2t.net/" + xref.getCompactidentifier();
	}

}
