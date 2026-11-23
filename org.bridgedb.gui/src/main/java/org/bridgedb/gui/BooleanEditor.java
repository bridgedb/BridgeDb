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

import javax.swing.JCheckBox;


public class BooleanEditor implements Editor, ActionListener
{
	private JCheckBox ckVal;
	
	private final ParameterModel model;
	private final int index;
	private final ParameterPanel parent;
	
	public BooleanEditor(ParameterModel model, int index, ParameterPanel parent,
			DefaultFormBuilder builder)
	{
		this.index = index;
		this.parent = parent;
		this.model = model;

		ckVal = new JCheckBox();
		ckVal.setToolTipText(model.getHint(index));
		ckVal.addActionListener(this);
        builder.append(model.getLabel(index), ckVal, 2);				
        builder.nextLine();
	}

	public Object getValue()
	{
		return Boolean.valueOf(ckVal.isSelected());
	}

	public void setValue(Object val)
	{
		if (ignoreEvent) return;
		ckVal.setSelected ((Boolean)val);
	}

	private boolean ignoreEvent = false;
	
	public void actionPerformed(ActionEvent arg0)
	{		
		ignoreEvent = true;
		model.setValue(index, ckVal.isSelected());
		ignoreEvent = false;
	}

}
