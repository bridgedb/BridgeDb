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
package org.bridgedb;

import java.io.File;
import java.io.IOException;
import java.util.Random;

//import org.pathvisio.debug.Logger;

/**
 * Collection of static utility methods dealing with files.
 * 
 * Package private class
 */
class FileUtilsGdb {	
	
	/**
	 * Think "deltree" or "rm -r"
	 * TODO: duplicate with org.pathvisio.util.FileUtils
	 */
	public static void deleteRecursive(File file) {
		if(file.isDirectory()) {
			for(File f : file.listFiles()) deleteRecursive(f);
		}
		boolean deleted = file.delete();
//		Logger.log.trace((deleted ? "Deleted " : "Unable to delete ") + "file " + file);
	}
	
	/**
	 * Removes the file extension (everything from the last occurence of '.')
	 * TODO: duplicate with org.pathvisio.util.FileUtils
	 */
	public static String removeExtension(String fname) {
		int dot = fname.lastIndexOf('.');
		if(dot > 0) fname = fname.substring(0, dot);
		return fname;
	}
	
	/**
	 * Attempts to create a directory in the right temporary directory,
	 * with a random name that starts with prefix.
	 */
	public static File createTempDir(String prefix, String postfix) throws IOException
	{
		File result;
		Random rng = new Random();
		int i = rng.nextInt(100000);
		// check for a filename that is free
		do
		{
			result = new File (System.getProperty("java.io.tmpdir"), prefix + i + postfix);
			i++;
		}
		while (result.exists());
		result.mkdir();
		
		if (!result.exists()) throw new IOException();
		if (!result.isDirectory()) throw new IOException();
		
//		Logger.log.info ("Created temporary directory " + result.getAbsolutePath());
		return result;
	}
}
