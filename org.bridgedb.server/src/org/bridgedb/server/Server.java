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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class Server 
{
	private Component component;

	/** @deprecated use run (port, configFile, transitive) instead */
	public void run(int port, File configFile)
	{
		run(port, configFile, false);
	}

	public void run(int port, File configFile, boolean transitive)
	{
		component = new Component();
		component.getServers().add(Protocol.HTTP, port);
		component.getDefaultHost().attach(new IDMapperService(configFile, transitive));		
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
		int port = 8183; // default port
		boolean transitive = false;
		File configFile = null;
		
		Options options = new Options();
		options.addOption(OptionBuilder.withArgName("port")
				.hasArg()
				.withDescription("Port to use (default: 8183)")
				.create("p"));
		options.addOption("t", false, "Enable transitive mode (default: false)");
		options.addOption(OptionBuilder.withArgName("file")
				.hasArg()
				.withDescription("Override configuration file (default: gdb.config)")
				.create("f"));
		options.addOption("h", false, "Print help and quit");
		CommandLineParser parser = new PosixParser();
		try
		{
			CommandLine line = parser.parse (options, args);
			if (line.getArgs().length > 0) throw new ParseException("Unknown options: " + line.getArgList());
			if (line.hasOption("h"))
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "startserver.sh", options );
				System.exit(0);
			}
			
			if (line.hasOption("p")) port = Integer.parseInt(line.getOptionValue("p"));
			if (line.hasOption("f")) configFile = new File (line.getOptionValue("f"));
			if (line.hasOption("t")) transitive = true; 
				
		}
		catch (Exception e)
		{
			System.err.println ("Did not understand command line options. Reason: " + e.getClass().getName() + " " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "startserver.sh", options );
			System.exit(-1);
		}
		
		Server server = new Server();
				
		server.run (port, configFile, transitive);
	}
}
