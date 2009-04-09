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
 * Stores a combination of {@link DataSource}, id and symbol. See also {@link Xref},
 * which only stores DataSource and id. A symbol is a name or biological shorthand
 * that is not guaranteed to be unique or unambiguous 
 * <p>
 * Immutable class, thread-safe
 */
public final class XrefWithSymbol implements Comparable<XrefWithSymbol> 
{
	final private String symbol;
	final private Xref xref;

	// String representation
	final private String rep;
	
	public XrefWithSymbol(Xref xref, String symbol)
	{
		this.symbol = symbol;
		this.xref = xref;
		rep = xref + "[" + symbol + "]";
	}
	
	public Xref asXref()
	{
		return xref;
	}
	
	/**
	 * null values for all three params are allowed,
	 */
	public XrefWithSymbol(String id, DataSource ds, String symbol) 
	{
		this (new Xref (id, ds), symbol);
	}

	/**
	 * @return symbol part, a name or biological shorthand
	 * that is not guaranteed to be unique or unambiguous
	 */
	public String getSymbol() 
	{
		return symbol;
	}
	
	/**
	 * @return DataSource part, such as 
	 */
	public DataSource getDataSource()
	{
		return xref.getDataSource();
	}
	
	/**
	 * @return identifier part, such as "3643" or "ENSG00000001"
	 */
	public String getId()
	{
		return xref.getId();
	}
	
	/**
	 * compares on the string representation
	 */
	public int compareTo (XrefWithSymbol o2) 
	{
		return rep.compareTo(o2.rep);
	}	

	/**
	 * hashCode depends on symbol + id + datasource
	 */
	@Override
	public int hashCode() 
	{
		return rep.hashCode();
	}
	
	/**
	 * returns true if id, DataSource and symbol are all three the same.
	 */
	@Override
	public boolean equals(Object o) 
	{
		if (o == null) return false;
		if (!(o instanceof XrefWithSymbol)) return false;
		XrefWithSymbol ref = (XrefWithSymbol)o;
		return 
			(xref == null ? ref.xref == null : xref.equals(ref.xref)) && 
			(symbol == null ? ref.symbol == null : symbol.equals(ref.symbol));
	}
	
	@Override
	public String toString()
	{
		return rep;
	}
}