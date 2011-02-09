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

import com.jgoodies.forms.builder.DefaultFormBuilder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class FileEditor implements Editor, DocumentListener, ActionListener
{
	JTextField txtFile;
	final ParameterModel model;
	final int index;
	final ParameterPanel parent;

	public FileEditor(ParameterModel model, int index, ParameterPanel parent, DefaultFormBuilder builder)
	{
		this.index = index;
		this.parent = parent;
		this.model = model;
		
		JButton btnBrowse = new JButton("Browse");
		txtFile = new JTextField();
		btnBrowse.addActionListener(this);
		txtFile.getDocument().addDocumentListener(this);
		txtFile.setToolTipText(model.getHint(index));
        builder.append(model.getLabel(index), txtFile, btnBrowse);
        builder.nextLine();
	}

	public Object getValue()
	{
		return new File (txtFile.getText());
	}

	boolean ignoreEvent = false;
	
	public void setValue(Object val)
	{
		if (ignoreEvent) return;
		txtFile.setText(((File)val).toString());
	}

	private void handleDocumentEvent(DocumentEvent arg0)
	{
		File file = new File(txtFile.getText());
		
		ignoreEvent = true;
		model.setValue(index, file);
		ignoreEvent = false;
	}

	public void changedUpdate(DocumentEvent arg0)
	{
		handleDocumentEvent(arg0);
	}

	public void insertUpdate(DocumentEvent arg0)
	{
		handleDocumentEvent(arg0);
	}

	public void removeUpdate(DocumentEvent arg0)
	{
		handleDocumentEvent(arg0);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object metaData = model.getMetaData(index); 
		JFileChooser jfc = new JFileChooser();
		if (metaData instanceof FileParameter)
		{
			FileParameter fileParameter = (FileParameter)metaData;
			jfc.setFileSelectionMode(fileParameter.getFileType());
			if (fileParameter.getFilter() != null &&
				fileParameter.getFileTypeName() != null)
				jfc.setFileFilter(new SimpleFileFilter(fileParameter.getFileTypeName(), fileParameter.getFilter(), true));
		}
		else
		{
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		jfc.setCurrentDirectory(new File(txtFile.getText()));
		if (jfc.showOpenDialog(parent.getTopLevelAncestor()) == JFileChooser.APPROVE_OPTION)
			txtFile.setText("" + jfc.getSelectedFile());
	}

}
