// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
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

import java.io.File;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class Server 
{
	private Component component;

	public void run(int port, File configFile)
	{
		component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach(new IDMapperService(configFile));		
		try {
			System.out.println ("Starting server on port " + port);
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		try {
			component.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) 
	{
		Server server = new Server();
		
		int port = 8183; // default port
		if ( args.length > 0 )
		{
		  port = new Integer( args[0] ).intValue();
		}
	
		File configFile = null;
		
		if (args.length > 1)
		{
			configFile = new File(args[1]);
		}
		
		if (args.length > 2)
		{
			System.err.println ("Expected max 2 arguments");
			System.exit(1);
		}
		
		server.run (port, configFile);
	}
}
