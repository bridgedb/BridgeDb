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

/**
 * Class to store an id + {@link DataSource} combination, which represents
 * an unique gene product
 * <p>
 * Immutable class, thread safe
 */
public class Xref implements Comparable<Xref> 
{	
	final private String id;
	final private DataSource ds;
	
	// String representation of this xref
	final private String rep;
	
	public Xref(String id, DataSource ds) {
		this.id = id;
		this.ds = ds;
		rep = (ds == null ? "" : ds.getSystemCode()) + ":" + id; 
	}
	
	public DataSource getDataSource() { return ds; }
	public String getId() { return id; }
	
	/**
	 * @deprecated
	 * use Xref.toString
	 */
	public String getName() { return toString(); }
	public String toString() { return rep;  }
	
	/**
	 * hashCode calculated from id and datasource combined
	 */
	public int hashCode() 
	{
		return rep.hashCode();
	}
	
	/**
	 * Returns true if both the id and the datasource are equal. 
	 */
	public boolean equals(Object o) 
	{
		if (o == null) return false;
		if(!(o instanceof Xref)) return false;
		Xref ref = (Xref)o;
		return 
			(id == null ? ref.id == null : id.equals(ref.id)) && 
			(ds == null ? ref.ds == null : ds.equals(ref.ds));
	}
	
	public int compareTo (Xref idc) 
	{
		return rep.compareTo(idc.rep);
	}
	
	/**
		@deprecated unused
	*/
	public boolean valid() 
	{
		return ds.getSystemCode().length() > 0 && id.length() > 0;
	}

	/**
	 * @deprecated use this.getDataSource().getFullName() instead.
	 */
	public String getDatabaseName() 
	{
		return ds.getFullName();
	}
	
	public String getUrl()
	{
		return ds.getUrl (id);
	}
}