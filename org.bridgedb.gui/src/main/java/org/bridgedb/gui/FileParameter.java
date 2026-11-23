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

import javax.swing.JFileChooser;

public class FileParameter
{
	private boolean isSave;
	private final String simpleFilter;
	private int fileType;
	private final String fileTypeName;

	public FileParameter ()
	{
		this ("All files", "*.*", false, JFileChooser.FILES_AND_DIRECTORIES);
	}
	
	/**
	 * @param fileType - one of JFileChooser.FILE_AND_DIRECTORIES, ... 
	 * @param fileTypeName - file type name
	 * @param simpleFilter - filter
	 * @param isSave - boolean to choose whether or not file is saved
	 */
	public FileParameter (String fileTypeName, String simpleFilter, boolean isSave, int fileType)
	{
		this.isSave = isSave;
		this.simpleFilter = simpleFilter;
		this.fileType = fileType;
		this.fileTypeName = fileTypeName;
	}
	
	public String getFileTypeName()
	{
		return fileTypeName;
	}
	
	public String getFilter()
	{
		return simpleFilter;
	}
	
	public int getFileType()
	{
		return fileType;
	}
	
	public boolean isSave()
	{
		return isSave;
	}	
}
