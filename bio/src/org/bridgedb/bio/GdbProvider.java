// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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
package org.bridgedb.bio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bridgedb.DBConnector;
import org.bridgedb.DataDerby;
import org.bridgedb.DataException;
import org.bridgedb.Gdb;
import org.bridgedb.SimpleGdb;
import org.bridgedb.SimpleGdbFactory;


//import org.pathvisio.debug.Logger;

/**
 * Utility class that maintains a list of synonym databases and the species they
 * apply to. This list can be read from a configuration file with on each line:
 * <pre>
 * species_latin_name[Tab]database_file_location
 * </pre>
 * If a database applies to all species (e.g. metabolites), use "*" as species.
 */
public class GdbProvider {
	Map<Organism, List<Gdb>> organism2gdb = new HashMap<Organism, List<Gdb>>();
	List<Gdb> globalGdbs = new ArrayList<Gdb>();
	
	public void addOrganismGdb(Organism organism, Gdb gdb) {
		List<Gdb> l = organism2gdb.get(organism);
		if(l == null) {
			organism2gdb.put(organism, l = new ArrayList<Gdb>());
		}
		if(!l.contains(gdb)) {
			l.add(gdb);
		}
	}
	
	public void removeOrganismGdb(Organism organism, Gdb gdb) {
		List<Gdb> l = organism2gdb.get(organism);
		if(l != null) {
			l.remove(gdb);
		}
	}
	
	public void addGlobalGdb(Gdb gdb) {
		if(!globalGdbs.contains(gdb)) globalGdbs.add(gdb);
	}
	
	public void removeGlobalGdb(Gdb gdb) {
		globalGdbs.remove(gdb);
	}
	
	public List<Gdb> getGdbs(Organism organism) {
		List<Gdb> gdbs = organism2gdb.get(organism);
		if(gdbs == null) {
			gdbs = new ArrayList<Gdb>();
		}
		gdbs.addAll(globalGdbs);
		return gdbs;
	}
	
	static final String DB_GLOBAL = "*";
	
	public static GdbProvider fromConfigFile(File f) throws DataException, IOException {
//		Logger.log.info("Parsing gene database configuration: " + f);
		GdbProvider gdbs = new GdbProvider();
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line = in.readLine();
		while(line != null) {
			String[] kv = line.split("\t");
			if(kv.length == 2) {
				String key = kv[0];
				String value = kv[1];
				Organism org = Organism.fromLatinName(key);
				if(org != null) {
					DataDerby dbConn = new DataDerby();
					SimpleGdb gdb = SimpleGdbFactory.createInstance(value, dbConn, DBConnector.PROP_NONE);
					gdbs.addOrganismGdb(org, gdb);
				} else if(DB_GLOBAL.equalsIgnoreCase(key)) {
					DataDerby dbConn = new DataDerby();
					SimpleGdb gdb = SimpleGdbFactory.createInstance(value, dbConn, DBConnector.PROP_NONE);
					gdbs.addGlobalGdb(gdb);
				} else {
//					Logger.log.warn("Unable to parse organism: " + key);
				}
			} else {
//				Logger.log.error("Invalid key/value pair in gene database configuration: " + line);
			}
			line = in.readLine();
		}
		in.close();
		return gdbs;
	}
}
