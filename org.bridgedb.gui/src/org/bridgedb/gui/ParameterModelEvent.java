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

/**
 * Event to signify a change in the parameter model, either 
 * parameter value(s) or parameter type(s). 
 */
public class ParameterModelEvent
{
	/**
	 * Event type. What is affected: parameter values, types, or both?
	 */
	public enum Type
	{
		/** type, label or number of parameters changed. */
		MODEL_CHANGED, 
		/** multiple parameters changed value but not type or label. */
		VALUES_CHANGED, 
		/** single parameter changed value but not type or label. */
		VALUE_CHANGED,   
	};
	
	private final ParameterModelEvent.Type type;
	
	/** @return type of event */
	public ParameterModelEvent.Type getType() { return type; }
	
	/**
	 * Constructor.
	 * @param type Type of the parameter.
	 */
	public ParameterModelEvent(ParameterModelEvent.Type type)
	{
		this(type, -1);
	}

	/**
	 * Constructor.
	 * @param type Type of the parameter.
	 */
	public ParameterModelEvent(ParameterModelEvent.Type type, int index)
	{
		this.type = type;
		this.index = index;
	}

	private int index;
	public int getIndex() { return index; }

}