package org.bridgedb.webservice.biomart;

public class Filter {
	private String name;
	private String displayName;
	
	
	public Filter(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}
