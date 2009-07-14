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
package org.bridgedb.rdb;

import java.io.File;
import java.io.IOException;
import java.util.Random;

//import org.pathvisio.debug.Logger;

/**
 * Collection of static utility methods dealing with files.
 * <p>
 * Package private class
 */
final class FileUtilsGdb {	
	
	/** prevent instantiation of utility class. */
	private FileUtilsGdb() {}
	
	/**
	 * Think "deltree" or "rm -r".
	 * @param file directory to remove recursively
	 * TODO: duplicate with org.pathvisio.util.FileUtils
	 */
	public static void deleteRecursive(File file) {
		if(file.isDirectory()) {
			for(File f : file.listFiles()) deleteRecursive(f);
		}
		file.delete();
	}
	
	/**
	 * Removes the file extension (everything from the last occurence of '.').
	 * @param fname file name
	 * @return file name without extension
	 * TODO: duplicate with org.pathvisio.util.FileUtils
	 */
	public static String removeExtension(String fname) {
		int dot = fname.lastIndexOf('.');
		String result = fname;
		if(dot > 0) result = fname.substring(0, dot);
		return result;
	}
	
	/**
	 * Attempts to create a directory in the right temporary directory,
	 * with a random name that starts with prefix.
	 * @return a temporary directory
	 * @param prefix prefix of temp directory name
	 * @param postfix postfix of temp directory name
	 * @throws IOException when dir could not be made
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
