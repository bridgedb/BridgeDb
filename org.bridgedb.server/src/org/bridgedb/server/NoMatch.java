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
package org.bridgedb.server;

import org.restlet.resource.Get;

public class NoMatch extends IDMapperResource
{
	@Get
	public String getNoMatchResult() 
	{
		throw new IllegalArgumentException("Unrecognized query<p><font size='+1'><i>Double check the spelling and syntax. We are expecting someting like: <a href='http://webservice.bridgedb.org/Human/xrefs/L/1234'>webservice.bridgedb.org/Human/xrefs/L/1234</a></i></font></p>");
	}
}
