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
package org.bridgedb.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

/**
 * A utility class that simplifies creating a file filter for a JFileChooser dialog.
 *
 * Usage example:
 * <pre>
 *	JFileChooser jfc = new JFileChooser();
 *	jfc.setDialogType(JFileChooser.OPEN_DIALOG);
 *	jfc.addChoosableFileFilter(new SimpleFileFilter("Text files", "*.txt", true));
 *	if(jfc.showDialog (parent, "Open database") == JFileChooser.APPROVE_OPTION) {
 *		...
 * </pre>
 */
public class SimpleFileFilter extends FileFilter
{
	private String desc;
	private List<String> extensions;
	private boolean showDirectories;

	/**
	 * @param name example: "Data files"
	 * @param glob example: "*.txt|*.csv"
	 */
	public SimpleFileFilter (String name, String globs)
	{
		extensions = new ArrayList<String>();
		for (String glob : globs.split("\\|"))
		{
			if (!glob.startsWith("*."))
				throw new IllegalArgumentException("expected list of globs like \"*.txt|*.csv\"");
			// cut off "*"
			// store only lower case (make comparison case insensitive)
			extensions.add (glob.substring(1).toLowerCase());
		}
		desc = name + " (" + globs + ")";
	}

	public SimpleFileFilter(String name, String globs, boolean showDirectories) {
		this(name, globs);
		this.showDirectories = showDirectories;
	}

	@Override
	public boolean accept(File file)
	{
		if(showDirectories && file.isDirectory()) {
			return true;
		}
		String fileName = file.toString().toLowerCase();

		for (String extension : extensions)
		{
			if (fileName.endsWith (extension))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription()
	{
		return desc;
	}
}