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
import java.util.List;

import javax.swing.JComboBox;


public class EnumEditor implements Editor, ActionListener
{
	JComboBox cbBox;

	private final ParameterModel model;
	private final int index;
	private final ParameterPanel parent;

	public EnumEditor(ParameterModel model, int index, ParameterPanel parent,
			DefaultFormBuilder builder)
	{
		this.index = index;
		this.parent = parent;
		this.model = model;
		
		List<?> values = (List<?>)model.getMetaData(index);
		cbBox = new JComboBox(values.toArray());
		cbBox.addActionListener(this);
		
        builder.append(model.getLabel(index), cbBox, 2);				
        builder.nextLine();
	}

	public Object getValue()
	{
		return cbBox.getSelectedItem();
	}

	public void setValue(Object val)
	{
		if (ignoreEvent) return;
		cbBox.setSelectedItem(val);
	}

	private boolean ignoreEvent = false;
	
	public void actionPerformed(ActionEvent arg0)
	{
		ignoreEvent = true;
		model.setValue(index, cbBox.getSelectedItem());
		ignoreEvent = false;		
	}
}
