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
import com.jgoodies.forms.layout.FormLayout;

import java.awt.Component;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.bridgedb.gui.ParameterModel.ParameterModelListener;

public class ParameterPanel extends JPanel implements ParameterModelListener
{
	private final ParameterModel model;

	public ParameterPanel (ParameterModel model)
	{
		this.model = model;
		refreshModel();		
	}

	private Editor[] editors;
	
	private void refreshValue(int i)
	{
		editors[i].setValue(model.getValue(i));
	}
	
	private void refreshValues()
	{
		for (int i = 0; i < model.getNum(); ++i)
		{
			refreshValue(i);
		}
	}
	
	private void refreshModel()
	{
		for (Component c : getComponents())
			remove(c);

		editors = new Editor[model.getNum()];

		DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(""), this);
        builder.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        builder.appendColumn("right:pref");
        builder.appendColumn("3dlu");
        builder.appendColumn("fill:max(pref; 100px):grow");
        builder.appendColumn("3dlu");
        builder.appendColumn("right:pref");

		for (int i = 0; i < model.getNum(); ++i)
		{
			if (model.getMetaData(i) instanceof FileParameter ||
					model.getMetaData(i) instanceof File)
			{
				editors[i] = new FileEditor (model, i, this, builder);
			}
			else if (model.getMetaData(i) instanceof Boolean)
			{
				editors[i] = new BooleanEditor (model, i, this, builder);
			}
			else if (model.getMetaData(i) instanceof List<?>)
			{
				editors[i] = new EnumEditor (model, i, this, builder);
			}
			else
			{
				editors[i] = new StringEditor (model, i, this, builder);
			}
		}
		
		refreshValues();
	}

	public void parametersChanged(ParameterModelEvent e)
	{
		switch (e.getType())
		{
		case VALUE_CHANGED:
			refreshValue(e.getIndex());
			break;
		case VALUES_CHANGED:
			refreshValues();
			break;
		case MODEL_CHANGED:
			refreshModel();
			break;
		}
	}
}
