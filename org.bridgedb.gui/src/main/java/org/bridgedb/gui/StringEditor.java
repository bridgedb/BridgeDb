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

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class StringEditor implements Editor, DocumentListener
{
	private JTextField txtField;
	final ParameterModel model;
	final int index;
	final ParameterPanel parent;	
	
	public StringEditor(ParameterModel model, int index, ParameterPanel parent,
			DefaultFormBuilder builder)
	{
		this.index = index;
		this.parent = parent;
		this.model = model;

		txtField = new JTextField();
		txtField.setText("" + model.getValue(index));
		txtField.getDocument().addDocumentListener(this);
		txtField.setToolTipText(model.getHint(index));
        builder.append(model.getLabel(index), txtField, 2);				
        builder.nextLine();
	}

	public Object getValue()
	{
		return txtField.getText();
	}

	public void setValue(Object val)
	{
		if (ignoreEvent) return;
		txtField.setText("" + val);		
	}
	
	// to prevent duplicate changes
	boolean ignoreEvent = false;
	
	private void handleDocumentEvent(DocumentEvent arg0)
	{
		ignoreEvent = true;
		model.setValue(index, txtField.getText());
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
	
}
