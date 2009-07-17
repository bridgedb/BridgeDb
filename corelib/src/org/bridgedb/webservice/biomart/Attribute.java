package org.bridgedb.webservice.biomart;

public class Attribute {
	private String name;
    private String displayName;
	
	public Attribute(String name, String displayName) {
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
