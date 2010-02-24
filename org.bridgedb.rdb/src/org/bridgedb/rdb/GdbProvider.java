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
package org.bridgedb.rdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.Organism;

/**
 * Utility class that maintains a list of synonym databases and the species they
 * apply to. This list can be read from a configuration file with on each line:
 * <pre>
 * species_latin_name[Tab]database_file_location
 * </pre>
 * If a database applies to all species (e.g. metabolites), use "*" as species.
 */
public class GdbProvider {
	Map<Organism, List<IDMapper>> organism2gdb = new HashMap<Organism, List<IDMapper>>();
	List<IDMapper> globalGdbs = new ArrayList<IDMapper>();
	
	public void addOrganismGdb(Organism organism, IDMapper gdb) {
		List<IDMapper> l = organism2gdb.get(organism);
		if(l == null) {
			organism2gdb.put(organism, l = new ArrayList<IDMapper>());
		}
		if(!l.contains(gdb)) {
			l.add(gdb);
		}
	}
	
	public void removeOrganismGdb(Organism organism, IDMapperRdb gdb) {
		List<IDMapper> l = organism2gdb.get(organism);
		if(l != null) {
			l.remove(gdb);
		}
	}
	
	public void addGlobalGdb(IDMapper gdb) {
		if(!globalGdbs.contains(gdb)) globalGdbs.add(gdb);
	}
	
	public void removeGlobalGdb(IDMapper gdb) {
		globalGdbs.remove(gdb);
	}
	
	public List<IDMapper> getGdbs(Organism organism) {
		List<IDMapper> gdbs = organism2gdb.get(organism);
		if(gdbs == null) {
			gdbs = new ArrayList<IDMapper>();
		}
		gdbs.addAll(globalGdbs);
		return gdbs;
	}
	
	static final String DB_GLOBAL = "*";
	
	public static GdbProvider fromConfigFile(File f) throws IDMapperException, IOException, ClassNotFoundException {
		System.out.println("Parsing gene database configuration: " + f.getAbsolutePath());
		GdbProvider gdbs = new GdbProvider();
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line = in.readLine();
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		Class.forName("org.bridgedb.file.IDMapperText");
		try
		{
			Class.forName ("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException ex)
		{
			System.out.println ("MySQL driver not in classpath, mysql backend unavailable");
		}
		while(line != null) {
			String[] kv = line.split("\t");
			if(kv.length == 2) {
				String key = kv[0];
				String value = kv[1];
				
				if (!value.startsWith("idmapper")) value = "idmapper-pgdb:" + value;
				IDMapper mapper = BridgeDb.connect (value);
				Organism org = Organism.fromLatinName(key);
				if(org != null) {
					gdbs.addOrganismGdb(org, mapper);
				} else if(DB_GLOBAL.equalsIgnoreCase(key)) {
					gdbs.addGlobalGdb(mapper);
				} else {
					System.out.println("Unable to parse organism: " + key);
				}
			} else {
				System.out.println("Invalid key/value pair in gene database configuration: " + line);
			}
			line = in.readLine();
		}
		in.close();
		return gdbs;
	}
}
